package io.angularpay.assets.helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.angularpay.assets.adapters.outbound.MongoAdapter;
import io.angularpay.assets.configurations.AngularPayConfiguration;
import io.angularpay.assets.domain.AssetRequest;
import io.angularpay.assets.domain.RequestStatus;
import io.angularpay.assets.domain.Seller;
import io.angularpay.assets.exceptions.CommandException;
import io.angularpay.assets.exceptions.ErrorCode;
import io.angularpay.assets.models.GenericCommandResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static io.angularpay.assets.exceptions.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class CommandHelper {

    private final MongoAdapter mongoAdapter;
    private final ObjectMapper mapper;
    private final AngularPayConfiguration configuration;

    public GenericCommandResponse executeAcid(Supplier<GenericCommandResponse> supplier) {
        int maxRetry = this.configuration.getMaxUpdateRetry();
        OptimisticLockingFailureException optimisticLockingFailureException;
        int counter = 0;
        //noinspection ConstantConditions
        do {
            try {
                return supplier.get();
            } catch (OptimisticLockingFailureException exception) {
                if (counter++ >= maxRetry) throw exception;
                optimisticLockingFailureException = exception;
            }
        }
        while (Objects.nonNull(optimisticLockingFailureException));
        throw optimisticLockingFailureException;
    }

    public String getRequestOwner(String requestReference) {
        AssetRequest found = this.mongoAdapter.findRequestByReference(requestReference).orElseThrow(
                () -> commandException(HttpStatus.NOT_FOUND, REQUEST_NOT_FOUND)
        );
        return found.getBuyer().getUserReference();
    }

    private static CommandException commandException(HttpStatus status, ErrorCode errorCode) {
        return CommandException.builder()
                .status(status)
                .errorCode(errorCode)
                .message(errorCode.getDefaultMessage())
                .build();
    }

    public String getInvestmentOwner(String requestReference, String investmentReference) {
        AssetRequest found = this.mongoAdapter.findRequestByReference(requestReference).orElseThrow(
                () -> commandException(HttpStatus.NOT_FOUND, REQUEST_NOT_FOUND)
        );
        if (CollectionUtils.isEmpty(found.getSellers())) return "";
        return found.getSellers().stream()
                .filter(x -> investmentReference.equalsIgnoreCase(x.getReference()))
                .map(Seller::getUserReference)
                .findFirst()
                .orElse("");
    }

    public <T> AssetRequest updateProperty(AssetRequest assetRequest, Supplier<T> getter, Consumer<T> setter) {
        setter.accept(getter.get());
        return this.mongoAdapter.updateRequest(assetRequest);
    }

    public <T> AssetRequest addItemToCollection(AssetRequest assetRequest, T newProperty, Supplier<List<T>> collectionGetter, Consumer<List<T>> collectionSetter) {
        if (CollectionUtils.isEmpty(collectionGetter.get())) {
            collectionSetter.accept(new ArrayList<>());
        }
        collectionGetter.get().add(newProperty);
        return this.mongoAdapter.updateRequest(assetRequest);
    }

    public <T> String toJsonString(T t) throws JsonProcessingException {
        return this.mapper.writeValueAsString(t);
    }

    public static AssetRequest getRequestByReferenceOrThrow(MongoAdapter mongoAdapter, String requestReference) {
        return mongoAdapter.findRequestByReference(requestReference).orElseThrow(
                () -> commandException(HttpStatus.NOT_FOUND, REQUEST_NOT_FOUND)
        );
    }

    public static void validRequestStatusAndInvestmentExists(AssetRequest found, String investmentReference) {
        validRequestStatusOrThrow(found);
        if (CollectionUtils.isEmpty(found.getSellers())) {
            throw commandException(HttpStatus.NOT_FOUND, REQUEST_NOT_FOUND);
        }
        if (found.getSellers().stream().noneMatch(x -> investmentReference.equalsIgnoreCase(x.getReference()))) {
            throw commandException(HttpStatus.NOT_FOUND, REQUEST_NOT_FOUND);
        }
    }

    public static void validRequestStatusOrThrow(AssetRequest found) {
        if (found.getStatus() == RequestStatus.COMPLETED) {
            throw commandException(HttpStatus.UNPROCESSABLE_ENTITY, REQUEST_COMPLETED_ERROR);
        }
        if (found.getStatus() == RequestStatus.CANCELLED) {
            throw commandException(HttpStatus.UNPROCESSABLE_ENTITY, REQUEST_CANCELLED_ERROR);
        }
    }
}
