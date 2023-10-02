package io.angularpay.assets.domain.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.angularpay.assets.adapters.outbound.MongoAdapter;
import io.angularpay.assets.domain.Role;
import io.angularpay.assets.exceptions.ErrorObject;
import io.angularpay.assets.models.GetUserRequestsCommandRequest;
import io.angularpay.assets.models.UserRequestModel;
import io.angularpay.assets.validation.DefaultConstraintValidator;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GetUserAssetPurchasesCommand extends AbstractCommand<GetUserRequestsCommandRequest, List<UserRequestModel>> {

    private final MongoAdapter mongoAdapter;
    private final DefaultConstraintValidator validator;

    public GetUserAssetPurchasesCommand(ObjectMapper mapper, MongoAdapter mongoAdapter, DefaultConstraintValidator validator) {
        super("GetUserAssetPurchasesCommand", mapper);
        this.mongoAdapter = mongoAdapter;
        this.validator = validator;
    }

    @Override
    protected String getResourceOwner(GetUserRequestsCommandRequest request) {
        return request.getAuthenticatedUser().getUserReference();
    }

    @Override
    protected List<UserRequestModel> handle(GetUserRequestsCommandRequest request) {
        Pageable pageable = PageRequest.of(request.getPaging().getIndex(), request.getPaging().getSize());
        return this.mongoAdapter.findByBuyerUserReference(pageable, request.getAuthenticatedUser().getUserReference())
                .getContent().stream()
                .map(x -> UserRequestModel.builder()
                        .requestReference(x.getReference())
                        .userReference(x.getBuyer().getUserReference())
                        .requestCreatedOn(x.getCreatedOn())
                        .build()).collect(Collectors.toList());
    }

    @Override
    protected List<ErrorObject> validate(GetUserRequestsCommandRequest request) {
        return this.validator.validate(request);
    }

    @Override
    protected List<Role> permittedRoles() {
        return Collections.emptyList();
    }
}
