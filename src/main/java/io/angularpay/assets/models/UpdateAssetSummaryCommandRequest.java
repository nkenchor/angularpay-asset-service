package io.angularpay.assets.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotEmpty;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UpdateAssetSummaryCommandRequest extends AccessControl {

    @NotEmpty
    private String requestReference;

    @NotEmpty
    private String summary;

    UpdateAssetSummaryCommandRequest(AuthenticatedUser authenticatedUser) {
        super(authenticatedUser);
    }
}
