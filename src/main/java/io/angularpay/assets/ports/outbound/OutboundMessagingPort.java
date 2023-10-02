package io.angularpay.assets.ports.outbound;

import java.util.Map;

public interface OutboundMessagingPort {
    void publishUpdates(String message);
    void publishUserNotification(String message);
    Map<String, String> getPlatformConfigurations(String hashName);
}
