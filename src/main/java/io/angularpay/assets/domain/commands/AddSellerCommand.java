package io.angularpay.assets.domain.commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.angularpay.assets.adapters.outbound.MongoAdapter;
import io.angularpay.assets.adapters.outbound.RedisAdapter;
import io.angularpay.assets.domain.AssetRequest;
import io.angularpay.assets.domain.Role;
import io.angularpay.assets.domain.Seller;
import io.angularpay.assets.exceptions.ErrorObject;
import io.angularpay.assets.helpers.CommandHelper;
import io.angularpay.assets.models.*;
import io.angularpay.assets.validation.DefaultConstraintValidator;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

import static io.angularpay.assets.helpers.CommandHelper.getRequestByReferenceOrThrow;
import static io.angularpay.assets.helpers.CommandHelper.validRequestStatusOrThrow;
import static io.angularpay.assets.helpers.Helper.getAllPartiesExceptActor;
import static io.angularpay.assets.models.UserNotificationType.PEER_INVESTOR_ADDED;

@Service
public class AddSellerCommand extends AbstractCommand<AddSellerCommandRequest, GenericReferenceResponse>
        implements UpdatesPublisherCommand<GenericCommandResponse>,
        ResourceReferenceCommand<GenericCommandResponse, ResourceReferenceResponse>,
        UserNotificationsPublisherCommand<GenericCommandResponse> {

    private final MongoAdapter mongoAdapter;
    private final DefaultConstraintValidator validator;
    private final CommandHelper commandHelper;
    private final RedisAdapter redisAdapter;

    public AddSellerCommand(ObjectMapper mapper, MongoAdapter mongoAdapter, DefaultConstraintValidator validator, CommandHelper commandHelper, RedisAdapter redisAdapter) {
        super("AddSellerCommand", mapper);
        this.mongoAdapter = mongoAdapter;
        this.validator = validator;
        this.commandHelper = commandHelper;
        this.redisAdapter = redisAdapter;
    }

    @Override
    protected String getResourceOwner(AddSellerCommandRequest request) {
        return request.getAuthenticatedUser().getUserReference();
    }

    @Override
    protected GenericCommandResponse handle(AddSellerCommandRequest request) {
        AssetRequest found = getRequestByReferenceOrThrow(this.mongoAdapter, request.getRequestReference());
        validRequestStatusOrThrow(found);
        Supplier<GenericCommandResponse> supplier = () -> addSeller(request);
        return this.commandHelper.executeAcid(supplier);
    }

    private GenericCommandResponse addSeller(AddSellerCommandRequest request) throws OptimisticLockingFailureException {
        AssetRequest found = getRequestByReferenceOrThrow(this.mongoAdapter, request.getRequestReference());
        Seller seller = Seller.builder()
                .reference(UUID.randomUUID().toString())
                .userReference(request.getAuthenticatedUser().getUserReference())
                .createdOn(Instant.now().truncatedTo(ChronoUnit.SECONDS).toString())
                .build();
        AssetRequest response = this.commandHelper.addItemToCollection(found, seller, found::getSellers, found::setSellers);
        return GenericCommandResponse.builder()
                .requestReference(found.getReference())
                .itemReference(seller.getReference())
                .assetRequest(response)
                .build();
    }

    @Override
    protected List<ErrorObject> validate(AddSellerCommandRequest request) {
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
        return PEER_INVESTOR_ADDED;
    }

    @Override
    public List<String> getAudience(GenericCommandResponse commandResponse) {
        return getAllPartiesExceptActor(commandResponse.getAssetRequest(), commandResponse.getItemReference());
    }

    @Override
    public String convertToUserNotificationsMessage(UserNotificationBuilderParameters<GenericCommandResponse, AssetRequest> parameters) throws JsonProcessingException {
        Optional<Seller> optional = parameters.getCommandResponse().getAssetRequest().getSellers().stream()
                .filter(x -> x.getReference().equalsIgnoreCase(parameters.getCommandResponse().getItemReference()))
                .findFirst();

        String summary;
        if (parameters.getCommandResponse().getAssetRequest().getBuyer().getUserReference()
                .equalsIgnoreCase(parameters.getUserReference())) {
            summary = "someone wants to sell the Asset you're looking for";
        } else {
            summary = "someone else wants to sell on the Assets post that you commented on";
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

    @Override
    public ResourceReferenceResponse map(GenericCommandResponse genericCommandResponse) {
        return new ResourceReferenceResponse(genericCommandResponse.getItemReference());
    }
}
