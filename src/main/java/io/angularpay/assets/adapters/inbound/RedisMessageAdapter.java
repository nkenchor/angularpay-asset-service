package io.angularpay.assets.adapters.inbound;

import io.angularpay.assets.domain.commands.PlatformConfigurationsConverterCommand;
import io.angularpay.assets.models.platform.PlatformConfigurationIdentifier;
import io.angularpay.assets.ports.inbound.InboundMessagingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static io.angularpay.assets.models.platform.PlatformConfigurationSource.TOPIC;

@Service
@RequiredArgsConstructor
public class RedisMessageAdapter implements InboundMessagingPort {

    private final PlatformConfigurationsConverterCommand converterCommand;

    @Override
    public void onMessage(String message, PlatformConfigurationIdentifier identifier) {
        this.converterCommand.execute(message, identifier, TOPIC);
    }
}
