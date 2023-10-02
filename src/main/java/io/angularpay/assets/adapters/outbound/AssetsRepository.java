package io.angularpay.assets.adapters.outbound;

import io.angularpay.assets.domain.RequestStatus;
import io.angularpay.assets.domain.AssetRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface AssetsRepository extends MongoRepository<AssetRequest, String> {

    Optional<AssetRequest> findByReference(String reference);
    Page<AssetRequest> findAll(Pageable pageable);
    Page<AssetRequest> findByStatusIn(Pageable pageable, List<RequestStatus> statuses);
    Page<AssetRequest> findByVerified(Pageable pageable, boolean verified);
    Page<AssetRequest> findAByBuyerUserReference(Pageable pageable, String userReference);
    long countByVerified(boolean verified);
    long countByStatus(RequestStatus status);
}
