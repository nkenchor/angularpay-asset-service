package io.angularpay.assets.domain.commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.angularpay.assets.adapters.outbound.MongoAdapter;
import io.angularpay.assets.adapters.outbound.RedisAdapter;
import io.angularpay.assets.domain.AssetRequest;
import io.angularpay.assets.domain.DeletedBy;
import io.angularpay.assets.domain.Role;
import io.angularpay.assets.domain.Seller;
import io.angularpay.assets.exceptions.ErrorObject;
import io.angularpay.assets.helpers.CommandHelper;
import io.angularpay.assets.models.*;
import io.angularpay.assets.validation.DefaultConstraintValidator;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

import static io.angularpay.assets.helpers.CommandHelper.getRequestByReferenceOrThrow;
import static io.angularpay.assets.helpers.CommandHelper.validRequestStatusAndInvestmentExists;
import static io.angularpay.assets.helpers.Helper.getAllPartiesExceptActor;
import static io.angularpay.assets.models.UserNotificationType.INVESTOR_DELETED_BY_SELF;

@Service
public class RemoveSellerCommand extends AbstractCommand<RemoveSellerCommandRequest, GenericCommandResponse>
        implements UpdatesPublisherCommand<GenericCommandResponse>,
        UserNotificationsPublisherCommand<GenericCommandResponse> {

    private final MongoAdapter mongoAdapter;
    private final DefaultConstraintValidator validator;
    private final CommandHelper commandHelper;
    private final RedisAdapter redisAdapter;

    public RemoveSellerCommand(
            ObjectMapper mapper,
            MongoAdapter mongoAdapter,
            DefaultConstraintValidator validator,
            CommandHelper commandHelper,
            RedisAdapter redisAdapter) {
        super("RemoveSellerCommand", mapper);
        this.mongoAdapter = mongoAdapter;
        this.validator = validator;
        this.commandHelper = commandHelper;
        this.redisAdapter = redisAdapter;
    }

    @Override
    protected String getResourceOwner(RemoveSellerCommandRequest request) {
        return this.commandHelper.getInvestmentOwner(request.getRequestReference(), request.getInvestmentReference());
    }

    @Override
    protected GenericCommandResponse handle(RemoveSellerCommandRequest request) {
        AssetRequest found = getRequestByReferenceOrThrow(this.mongoAdapter, request.getRequestReference());
        String investmentReference = request.getInvestmentReference();
        validRequestStatusAndInvestmentExists(found, investmentReference);
        Supplier<GenericCommandResponse> supplier = () -> removeSeller(request);
        return this.commandHelper.executeAcid(supplier);
    }

    private GenericCommandResponse removeSeller(RemoveSellerCommandRequest request) {
        AssetRequest found = getRequestByReferenceOrThrow(this.mongoAdapter, request.getRequestReference());
        found.getSellers().forEach(x-> {
            if (request.getInvestmentReference().equalsIgnoreCase(x.getReference())) {
                x.setDeleted(true);
                x.setDeletedOn(Instant.now().truncatedTo(ChronoUnit.SECONDS).toString());
                x.setDeletedBy(DeletedBy.SELLER);
            }
        });
        AssetRequest response = this.mongoAdapter.updateRequest(found);
        return GenericCommandResponse.builder()
                .requestReference(response.getReference())
                .assetRequest(response)
                .build();
    }

    @Override
    protected List<ErrorObject> validate(RemoveSellerCommandRequest request) {
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

    @Override
    public UserNotificationType getUserNotificationType(GenericCommandResponse commandResponse) {
        return INVESTOR_DELETED_BY_SELF;
    }

    @Override
    public List<String> getAudience(GenericCommandResponse commandResponse) {
        return getAllPartiesExceptActor(commandResponse.getAssetRequest(), commandResponse.getItemReference());
    }

    @Override
    public String convertToUserNotificationsMessage(UserNotificationBuilderParameters<GenericCommandResponse, AssetRequest> parameters) throws JsonProcessingException {
        String summary;
        Optional<String> optional = parameters.getCommandResponse().getAssetRequest().getSellers().stream()
                .filter(x -> x.getReference().equalsIgnoreCase(parameters.getCommandResponse().getItemReference()))
                .map(Seller::getUserReference)
                .findFirst();
        if (optional.isPresent() && optional.get().equalsIgnoreCase(parameters.getUserReference())) {
            summary = "the comment you made on an Assets post, was deleted because payment wasn't received";
        } else {
            summary = "someone's investment on an Assets post that you commented on, was deleted";
        }

        UserNotificationInvestmentPayload userNotificationInvestmentPayload = UserNotificationInvestmentPayload.builder()
                .requestReference(parameters.getCommandResponse().getRequestReference())
                .investmentReference(parameters.getCommandResponse().getItemReference())
                .build();
        String payload = mapper.writeValueAsString(userNotificationInvestmentPayload);

        String attributes = mapper.writeValueAsString(parameters.getRequest());

        UserNotification userNotification = UserNotification.builder()
                .reference(UUID.randomUUID().toString())
                .createdOn(Instant.now().truncatedTo(ChronoUnit.SECONDS).toString())
                .serviceCode(parameters.getRequest().getServiceCode())
                .userReference(parameters.getUserReference())
                .type(parameters.getType())
                .summary(summary)
                .payload(payload)
                .attributes(attributes)
                .build();

        return mapper.writeValueAsString(userNotification);
    }
}
