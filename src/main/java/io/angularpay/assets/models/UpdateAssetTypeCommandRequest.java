package io.angularpay.assets.models;

import io.angularpay.assets.domain.AssetTypes;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UpdateAssetTypeCommandRequest extends AccessControl {

    @NotEmpty
    private String requestReference;

    @NotNull
    private AssetTypes type;

    UpdateAssetTypeCommandRequest(AuthenticatedUser authenticatedUser) {
        super(authenticatedUser);
    }
}
