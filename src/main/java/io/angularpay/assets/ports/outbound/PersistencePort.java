package io.angularpay.assets.ports.outbound;

import io.angularpay.assets.domain.AssetRequest;
import io.angularpay.assets.domain.RequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PersistencePort {
    AssetRequest createRequest(AssetRequest request);
    AssetRequest updateRequest(AssetRequest request);
    Optional<AssetRequest> findRequestByReference(String reference);
    Page<AssetRequest> listRequests(Pageable pageable);
    Page<AssetRequest> findRequestsByStatus(Pageable pageable, List<RequestStatus> statuses);
    Page<AssetRequest> findRequestsByVerification(Pageable pageable, boolean verified);
    Page<AssetRequest> findByBuyerUserReference(Pageable pageable, String userReference);
    long getCountByVerificationStatus(boolean verified);
    long getCountByRequestStatus(RequestStatus status);
    long getTotalCount();
}
