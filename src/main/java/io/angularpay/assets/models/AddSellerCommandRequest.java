package io.angularpay.assets.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AddSellerCommandRequest extends AccessControl {

    private String requestReference;

    AddSellerCommandRequest(AuthenticatedUser authenticatedUser) {
        super(authenticatedUser);
    }
}
