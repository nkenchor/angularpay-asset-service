
package io.angularpay.assets.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.angularpay.assets.domain.Amount;
import io.angularpay.assets.domain.AssetTypes;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class CreateRequest {

    @NotNull
    private AssetTypes type;

    @NotEmpty
    private String summary;

    @NotNull
    @Valid
    private Amount budget;
}
