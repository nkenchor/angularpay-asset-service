package io.angularpay.assets.ports.inbound;

import io.angularpay.assets.models.platform.PlatformConfigurationIdentifier;

public interface InboundMessagingPort {
    void onMessage(String message, PlatformConfigurationIdentifier identifier);
}
