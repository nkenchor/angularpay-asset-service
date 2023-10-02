package io.angularpay.assets.domain.commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.angularpay.assets.adapters.outbound.RedisAdapter;
import io.angularpay.assets.domain.AssetRequest;

import java.util.Objects;

public interface UpdatesPublisherCommand<T extends AssetRequestSupplier> {

    RedisAdapter getRedisAdapter();

    String convertToUpdatesMessage(AssetRequest assetRequest) throws JsonProcessingException;

    default void publishUpdates(T t) {
        AssetRequest assetRequest = t.getAssetRequest();
        RedisAdapter redisAdapter = this.getRedisAdapter();
        if (Objects.nonNull(assetRequest) && Objects.nonNull(redisAdapter)) {
            try {
                String message = this.convertToUpdatesMessage(assetRequest);
                redisAdapter.publishUpdates(message);
            } catch (JsonProcessingException exception) {
                throw new RuntimeException(exception);
            }
        }
    }
}
