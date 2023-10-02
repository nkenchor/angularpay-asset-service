package io.angularpay.assets.ports.outbound;

import io.angularpay.assets.models.SchedulerServiceRequest;
import io.angularpay.assets.models.SchedulerServiceResponse;

import java.util.Map;
import java.util.Optional;

public interface SchedulerServicePort {
    Optional<SchedulerServiceResponse> createScheduledRequest(SchedulerServiceRequest request, Map<String, String> headers);
}
