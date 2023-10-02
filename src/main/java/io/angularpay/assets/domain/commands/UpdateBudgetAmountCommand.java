package io.angularpay.assets.domain.commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.angularpay.assets.adapters.outbound.MongoAdapter;
import io.angularpay.assets.adapters.outbound.RedisAdapter;
import io.angularpay.assets.domain.AssetRequest;
import io.angularpay.assets.domain.Role;
import io.angularpay.assets.exceptions.ErrorObject;
import io.angularpay.assets.helpers.CommandHelper;
import io.angularpay.assets.models.GenericCommandResponse;
import io.angularpay.assets.models.GenericReferenceResponse;
import io.angularpay.assets.models.UpdateBuyerBudgetCommandRequest;
import io.angularpay.assets.validation.DefaultConstraintValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import static io.angularpay.assets.helpers.CommandHelper.getRequestByReferenceOrThrow;
import static io.angularpay.assets.helpers.CommandHelper.validRequestStatusOrThrow;

@Slf4j
@Service
public class UpdateBudgetAmountCommand extends AbstractCommand<UpdateBuyerBudgetCommandRequest, GenericReferenceResponse>
        implements UpdatesPublisherCommand<GenericCommandResponse> {

    private final MongoAdapter mongoAdapter;
    private final DefaultConstraintValidator validator;
    private final CommandHelper commandHelper;
    private final RedisAdapter redisAdapter;

    public UpdateBudgetAmountCommand(
            ObjectMapper mapper,
            MongoAdapter mongoAdapter,
            DefaultConstraintValidator validator,
            CommandHelper commandHelper, RedisAdapter redisAdapter) {
        super("UpdateBudgetAmountCommand", mapper);
        this.mongoAdapter = mongoAdapter;
        this.validator = validator;
        this.commandHelper = commandHelper;
        this.redisAdapter = redisAdapter;
    }

    @Override
    protected String getResourceOwner(UpdateBuyerBudgetCommandRequest request) {
        return this.commandHelper.getRequestOwner(request.getRequestReference());
    }

    @Override
    protected GenericCommandResponse handle(UpdateBuyerBudgetCommandRequest request) {
        AssetRequest found = getRequestByReferenceOrThrow(this.mongoAdapter, request.getRequestReference());
        validRequestStatusOrThrow(found);
        Supplier<GenericCommandResponse> supplier = () -> updateBudgetPrice(request);
        return this.commandHelper.executeAcid(supplier);
    }

    private GenericCommandResponse updateBudgetPrice(UpdateBuyerBudgetCommandRequest request) throws OptimisticLockingFailureException {
        AssetRequest found = getRequestByReferenceOrThrow(this.mongoAdapter, request.getRequestReference());
        AssetRequest response = this.commandHelper.updateProperty(found, request::getBudget, found::setBudget);
        return GenericCommandResponse.builder()
                .requestReference(response.getReference())
                .assetRequest(response)
                .build();
    }

    @Override
    protected List<ErrorObject> validate(UpdateBuyerBudgetCommandRequest request) {
        return this.validator.validate(request);
    }

    @Override
    protected List<Role> permittedRoles() {
        return Collections.emptyList();
    }

    @Override
    public String convertToUpdatesMessage(AssetRequest assetRequest) throws JsonProcessingException {
        return this.commandHelper.toJsonString(assetRequest);
    }

    @Override
    public RedisAdapter getRedisAdapter() {
        return this.redisAdapter;
    }
}
