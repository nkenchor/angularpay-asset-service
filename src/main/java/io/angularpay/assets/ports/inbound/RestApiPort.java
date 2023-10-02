package io.angularpay.assets.ports.inbound;

import io.angularpay.assets.domain.*;
import io.angularpay.assets.models.*;

import java.util.List;
import java.util.Map;

public interface RestApiPort {
    GenericReferenceResponse createScheduledRequest(String schedule, CreateRequest request, Map<String, String> headers);
    GenericReferenceResponse create(CreateRequest request, Map<String, String> headers);
    void updateSummary(String requestReference, SummaryModel summaryModel, Map<String, String> headers);
    void updateAssetType(String requestReference, AssetTypeModel assetTypeModel, Map<String, String> headers);
    void updateBudgetAmount(String requestReference, Amount amount, Map<String, String> headers);
    void updateVerificationStatus(String requestReference, boolean verified, Map<String, String> headers);
    GenericReferenceResponse addSeller(String requestReference, Map<String, String> headers);
    void removeSeller(String requestReference, String investmentReference, Map<String, String> headers);
    void updateRequestStatus(String requestReference, RequestStatusModel status, Map<String, String> headers);
    AssetRequest getRequestByReference(String requestReference, Map<String, String> headers);
    List<AssetRequest> getNewsfeedModel(int page, Map<String, String> headers);
    List<UserRequestModel> getUserAssetPurchases(int page, Map<String, String> headers);
    List<UserInvestmentModel> getUserAssetSales(int page, Map<String, String> headers);
    List<AssetRequest> getNewsfeedByStatus(int page, List<RequestStatus> statuses, Map<String, String> headers);
    List<AssetRequest> getRequestListByStatus(int page, List<RequestStatus> statuses, Map<String, String> headers);
    List<AssetRequest> getRequestListByVerification(int page, boolean verified, Map<String, String> headers);
    List<AssetRequest> getRequestList(int page, Map<String, String> headers);
    List<Statistics> getStatistics(Map<String, String> headers);
}
