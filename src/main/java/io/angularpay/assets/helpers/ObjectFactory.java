package io.angularpay.assets.helpers;

import io.angularpay.assets.domain.AssetRequest;
import io.angularpay.assets.domain.RequestStatus;

import java.util.ArrayList;
import java.util.UUID;

import static io.angularpay.assets.common.Constants.SERVICE_CODE;
import static io.angularpay.assets.util.SequenceGenerator.generateRequestTag;

public class ObjectFactory {

    public static AssetRequest assetsRequestWithDefaults() {
        return AssetRequest.builder()
                .reference(UUID.randomUUID().toString())
                .serviceCode(SERVICE_CODE)
                .verified(false)
                .status(RequestStatus.ACTIVE)
                .requestTag(generateRequestTag())
                .sellers(new ArrayList<>())
                .build();
    }
}