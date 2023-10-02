
package io.angularpay.assets.models;

import io.angularpay.assets.domain.AssetTypes;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AssetTypeModel {

    @NotNull
    private AssetTypes type;
}
