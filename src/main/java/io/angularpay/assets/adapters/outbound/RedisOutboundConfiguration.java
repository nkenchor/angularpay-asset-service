package io.angularpay.assets.adapters.outbound;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.ChannelTopic;

import static io.angularpay.assets.common.Constants.UPDATES_TOPIC;
import static io.angularpay.assets.common.Constants.USER_NOTIFICATIONS_TOPIC;

@Configuration
public class RedisOutboundConfiguration {

    @Bean
    ChannelTopic updatesTopic() {
        return new ChannelTopic(UPDATES_TOPIC);
    }

    @Bean
    ChannelTopic userNotificationsTopic() {
        return new ChannelTopic(USER_NOTIFICATIONS_TOPIC);
    }
}
