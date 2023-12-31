
package io.angularpay.assets.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Document("asset_requests")
public class AssetRequest {

    @Id
    private String id;
    @Version
    private int version;
    @JsonProperty("service_code")
    private String serviceCode;
    private boolean verified;
    @JsonProperty("verified_on")
    private String verifiedOn;
    private String summary;
    private Amount budget;
    @JsonProperty("created_on")
    private String createdOn;
    private Buyer buyer;
    private List<Seller> sellers;
    @JsonProperty("last_modified")
    private String lastModified;
    private String reference;
    @JsonProperty("request_tag")
    private String requestTag;
    private AssetTypes type;
    private RequestStatus status;
}
