
package io.angularpay.assets.domain;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Seller {

    @JsonProperty("created_on")
    private String createdOn;
    @JsonProperty("deleted_by")
    private DeletedBy deletedBy;
    @JsonProperty("deleted_on")
    private String deletedOn;
    @JsonProperty("is_deleted")
    private boolean deleted;
    private String reference;
    @JsonProperty("user_reference")
    private String userReference;
}
