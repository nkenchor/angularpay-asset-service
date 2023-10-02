
package io.angularpay.assets.models;

import io.angularpay.assets.domain.AssetRequest;
import io.angularpay.assets.domain.commands.AssetRequestSupplier;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class GenericCommandResponse extends GenericReferenceResponse implements AssetRequestSupplier {

    private final String requestReference;
    private final String itemReference;
    private final AssetRequest assetRequest;
}
