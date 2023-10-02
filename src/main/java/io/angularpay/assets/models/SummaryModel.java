
package io.angularpay.assets.models;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class SummaryModel {

    @NotEmpty
    private String summary;
}
