package io.angularpay.assets.adapters.inbound;

import io.angularpay.assets.configurations.AngularPayConfiguration;
import io.angularpay.assets.domain.*;
import io.angularpay.assets.domain.commands.*;
import io.angularpay.assets.models.*;
import io.angularpay.assets.ports.inbound.RestApiPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static io.angularpay.assets.helpers.Helper.fromHeaders;

@RestController
@RequestMapping("/assets/requests")
@RequiredArgsConstructor
public class RestApiAdapter implements RestApiPort {

    private final AngularPayConfiguration configuration;

    private final CreateRequestCommand createRequestCommand;
    private final UpdateAssetRequestSummaryCommand updateAssetRequestSummaryCommand;
    private final UpdateBudgetAmountCommand updateBudgetAmountCommand;
    private final UpdateVerificationStatusCommand updateVerificationStatusCommand;
    private final AddSellerCommand addSellerCommand;
    private final RemoveSellerCommand removeSellerCommand;
    private final UpdateRequestStatusCommand updateRequestStatusCommand;
    private final GetRequestByReferenceCommand getRequestByReferenceCommand;
    private final GetNewsfeedCommand getNewsfeedCommand;
    private final GetUserAssetPurchasesCommand getUserAssetPurchasesCommand;
    private final GetUserAssetSalesCommand getUserAssetSalesCommand;
    private final GetNewsfeedByStatusCommand getNewsfeedByStatusCommand;
    private final GetRequestListByStatusCommand getRequestListByStatusCommand;
    private final GetRequestListByVerificationCommand getRequestListByVerificationCommand;
    private final GetRequestListCommand getRequestListCommand;
    private final UpdateAssetTypeCommand updateAssetTypeCommand;
    private final ScheduledRequestCommand scheduledRequestCommand;
    private final GetStatisticsCommand getStatisticsCommand;

    @PostMapping("/schedule/{schedule}")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public GenericReferenceResponse createScheduledRequest(
            @PathVariable String schedule,
            @RequestBody CreateRequest request,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        ScheduledRequestCommandRequest scheduledRequestCommandRequest = ScheduledRequestCommandRequest.builder()
                .runAt(schedule)
                .createRequest(request)
                .authenticatedUser(authenticatedUser)
                .build();
        return scheduledRequestCommand.execute(scheduledRequestCommandRequest);
    }

    @PostMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public GenericReferenceResponse create(
            @RequestBody CreateRequest request,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        CreateRequestCommandRequest createRequestCommandRequest = CreateRequestCommandRequest.builder()
                .createRequest(request)
                .authenticatedUser(authenticatedUser)
                .build();
        return createRequestCommand.execute(createRequestCommandRequest);
    }

    @PutMapping("/{requestReference}/summary")
    @Override
    public void updateSummary(
            @PathVariable String requestReference,
            @RequestBody SummaryModel summaryModel,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        UpdateAssetSummaryCommandRequest updateAssetSummaryCommandRequest = UpdateAssetSummaryCommandRequest.builder()
                .requestReference(requestReference)
                .summary(summaryModel.getSummary())
                .authenticatedUser(authenticatedUser)
                .build();
        updateAssetRequestSummaryCommand.execute(updateAssetSummaryCommandRequest);
    }

    @PutMapping("/{requestReference}/type")
    @Override
    public void updateAssetType(
            @PathVariable String requestReference,
            @RequestBody AssetTypeModel assetTypeModel,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        UpdateAssetTypeCommandRequest updateAssetTypeCommandRequest = UpdateAssetTypeCommandRequest.builder()
                .requestReference(requestReference)
                .type(assetTypeModel.getType())
                .authenticatedUser(authenticatedUser)
                .build();
        updateAssetTypeCommand.execute(updateAssetTypeCommandRequest);
    }

    @PutMapping("/{requestReference}/budget")
    @Override
    public void updateBudgetAmount(
            @PathVariable String requestReference,
            @RequestBody Amount amount,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        UpdateBuyerBudgetCommandRequest updateBuyerBudgetCommandRequest = UpdateBuyerBudgetCommandRequest.builder()
                .requestReference(requestReference)
                .budget(amount)
                .authenticatedUser(authenticatedUser)
                .build();
        updateBudgetAmountCommand.execute(updateBuyerBudgetCommandRequest);
    }

    @PutMapping("/{requestReference}/verify/{verified}")
    @Override
    public void updateVerificationStatus(
            @PathVariable String requestReference,
            @PathVariable boolean verified,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        UpdateVerificationStatusCommandRequest updateVerificationStatusCommandRequest = UpdateVerificationStatusCommandRequest.builder()
                .requestReference(requestReference)
                .verified(verified)
                .authenticatedUser(authenticatedUser)
                .build();
        updateVerificationStatusCommand.execute(updateVerificationStatusCommandRequest);
    }

    @PostMapping("/{requestReference}/sellers")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public GenericReferenceResponse addSeller(
            @PathVariable String requestReference,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        AddSellerCommandRequest addSellerCommandRequest = AddSellerCommandRequest.builder()
                .requestReference(requestReference)
                .authenticatedUser(authenticatedUser)
                .build();
        return addSellerCommand.execute(addSellerCommandRequest);
    }

    @DeleteMapping("/{requestReference}/sellers/{investmentReference}")
    @Override
    public void removeSeller(
            @PathVariable String requestReference,
            @PathVariable String investmentReference,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        RemoveSellerCommandRequest removeSellerCommandRequest = RemoveSellerCommandRequest.builder()
                .requestReference(requestReference)
                .investmentReference(investmentReference)
                .authenticatedUser(authenticatedUser)
                .build();
        removeSellerCommand.execute(removeSellerCommandRequest);
    }

    @PutMapping("/{requestReference}/status")
    @Override
    public void updateRequestStatus(
            @PathVariable String requestReference,
            @RequestBody RequestStatusModel status,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        UpdateRequestStatusCommandRequest updateRequestStatusCommandRequest = UpdateRequestStatusCommandRequest.builder()
                .requestReference(requestReference)
                .status(status.getStatus())
                .authenticatedUser(authenticatedUser)
                .build();
        updateRequestStatusCommand.execute(updateRequestStatusCommandRequest);
    }

    @GetMapping("/{requestReference}")
    @Override
    public AssetRequest getRequestByReference(
            @PathVariable String requestReference,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        GetRequestByReferenceCommandRequest getRequestByReferenceCommandRequest = GetRequestByReferenceCommandRequest.builder()
                .requestReference(requestReference)
                .authenticatedUser(authenticatedUser)
                .build();
        return getRequestByReferenceCommand.execute(getRequestByReferenceCommandRequest);
    }

    @GetMapping("/list/newsfeed/page/{page}")
    @Override
    public List<AssetRequest> getNewsfeedModel(
            @PathVariable int page,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        GenericGetRequestListCommandRequest genericGetRequestListCommandRequest = GenericGetRequestListCommandRequest.builder()
                .authenticatedUser(authenticatedUser)
                .paging(Paging.builder().size(this.configuration.getPageSize()).index(page).build())
                .build();
        return getNewsfeedCommand.execute(genericGetRequestListCommandRequest);
    }

    @GetMapping("/list/user-asset-purchases/page/{page}")
    @Override
    public List<UserRequestModel> getUserAssetPurchases(
            @PathVariable int page,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        GetUserRequestsCommandRequest getUserRequestsCommandRequest = GetUserRequestsCommandRequest.builder()
                .authenticatedUser(authenticatedUser)
                .paging(Paging.builder().size(this.configuration.getPageSize()).index(page).build())
                .build();
        return getUserAssetPurchasesCommand.execute(getUserRequestsCommandRequest);
    }

    @GetMapping("/list/user-asset-sales/page/{page}")
    @Override
    public List<UserInvestmentModel> getUserAssetSales(
            @PathVariable int page,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        GetUserInvestmentsCommandRequest getUserInvestmentsCommandRequest = GetUserInvestmentsCommandRequest.builder()
                .authenticatedUser(authenticatedUser)
                .paging(Paging.builder().size(this.configuration.getPageSize()).index(page).build())
                .build();
        return getUserAssetSalesCommand.execute(getUserInvestmentsCommandRequest);
    }

    @GetMapping("/list/newsfeed/page/{page}/filter/statuses/{statuses}")
    @ResponseBody
    @Override
    public List<AssetRequest> getNewsfeedByStatus(
            @PathVariable int page,
            @PathVariable List<RequestStatus> statuses,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        GenericGetByStatusCommandRequest genericGetByStatusCommandRequest = GenericGetByStatusCommandRequest.builder()
                .authenticatedUser(authenticatedUser)
                .paging(Paging.builder().size(this.configuration.getPageSize()).index(page).build())
                .statuses(statuses)
                .build();
        return getNewsfeedByStatusCommand.execute(genericGetByStatusCommandRequest);
    }
    @GetMapping("/list/page/{page}/filter/statuses/{statuses}")
    @ResponseBody
    @Override
    public List<AssetRequest> getRequestListByStatus(
            @PathVariable int page,
            @PathVariable List<RequestStatus> statuses,
            Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        GenericGetByStatusCommandRequest genericGetByStatusCommandRequest = GenericGetByStatusCommandRequest.builder()
                .authenticatedUser(authenticatedUser)
                .paging(Paging.builder().size(this.configuration.getPageSize()).index(page).build())
                .statuses(statuses)
                .build();
        return getRequestListByStatusCommand.execute(genericGetByStatusCommandRequest);
    }

    @GetMapping("/list/page/{page}/filter/verified/{verified}")
    @ResponseBody
    @Override
    public List<AssetRequest> getRequestListByVerification(
            @PathVariable int page,
            @PathVariable boolean verified,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        GetRequestListByVerificationCommandRequest getRequestListByVerificationCommandRequest = GetRequestListByVerificationCommandRequest.builder()
                .authenticatedUser(authenticatedUser)
                .paging(Paging.builder().size(this.configuration.getPageSize()).index(page).build())
                .verified(verified)
                .build();
        return getRequestListByVerificationCommand.execute(getRequestListByVerificationCommandRequest);
    }

    @GetMapping("/list/page/{page}")
    @ResponseBody
    @Override
    public List<AssetRequest> getRequestList(
            @PathVariable int page,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        GenericGetRequestListCommandRequest genericGetRequestListCommandRequest = GenericGetRequestListCommandRequest.builder()
                .authenticatedUser(authenticatedUser)
                .paging(Paging.builder().size(this.configuration.getPageSize()).index(page).build())
                .build();
        return getRequestListCommand.execute(genericGetRequestListCommandRequest);
    }

    @GetMapping("/statistics")
    @ResponseBody
    @Override
    public List<Statistics> getStatistics(@RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        GetStatisticsCommandRequest getStatisticsCommandRequest = GetStatisticsCommandRequest.builder()
                .authenticatedUser(authenticatedUser)
                .build();
        return getStatisticsCommand.execute(getStatisticsCommandRequest);
    }
}
