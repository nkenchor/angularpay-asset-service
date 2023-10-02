package io.angularpay.assets.adapters.outbound;

import io.angularpay.assets.domain.RequestStatus;
import io.angularpay.assets.domain.AssetRequest;
import io.angularpay.assets.ports.outbound.PersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MongoAdapter implements PersistencePort {

    private final AssetsRepository assetsRepository;

    @Override
    public AssetRequest createRequest(AssetRequest request) {
        request.setCreatedOn(Instant.now().truncatedTo(ChronoUnit.SECONDS).toString());
        request.setLastModified(Instant.now().truncatedTo(ChronoUnit.SECONDS).toString());
        return assetsRepository.save(request);
    }

    @Override
    public AssetRequest updateRequest(AssetRequest request) {
        request.setLastModified(Instant.now().truncatedTo(ChronoUnit.SECONDS).toString());
        return assetsRepository.save(request);
    }

    @Override
    public Optional<AssetRequest> findRequestByReference(String reference) {
        return assetsRepository.findByReference(reference);
    }

    @Override
    public Page<AssetRequest> listRequests(Pageable pageable) {
        return assetsRepository.findAll(pageable);
    }

    @Override
    public Page<AssetRequest> findRequestsByStatus(Pageable pageable, List<RequestStatus> statuses) {
        return assetsRepository.findByStatusIn(pageable, statuses);
    }

    @Override
    public Page<AssetRequest> findRequestsByVerification(Pageable pageable, boolean verified) {
        return assetsRepository.findByVerified(pageable, verified);
    }

    @Override
    public Page<AssetRequest> findByBuyerUserReference(Pageable pageable, String userReference) {
        return assetsRepository.findAByBuyerUserReference(pageable, userReference);
    }

    @Override
    public long getCountByVerificationStatus(boolean verified) {
        return assetsRepository.countByVerified(verified);
    }

    @Override
    public long getCountByRequestStatus(RequestStatus status) {
        return assetsRepository.countByStatus(status);
    }

    @Override
    public long getTotalCount() {
        return assetsRepository.count();
    }
}
