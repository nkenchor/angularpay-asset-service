package io.angularpay.assets.models;

import io.angularpay.assets.domain.Amount;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UpdateBuyerBudgetCommandRequest extends AccessControl {

    @NotEmpty
    private String requestReference;

    @NotNull
    @Valid
    private Amount budget;

    UpdateBuyerBudgetCommandRequest(AuthenticatedUser authenticatedUser) {
        super(authenticatedUser);
    }
}
