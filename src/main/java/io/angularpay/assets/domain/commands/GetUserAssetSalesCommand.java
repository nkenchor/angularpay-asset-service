package io.angularpay.assets.domain.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.angularpay.assets.adapters.outbound.MongoAdapter;
import io.angularpay.assets.domain.AssetRequest;
import io.angularpay.assets.domain.Role;
import io.angularpay.assets.domain.Seller;
import io.angularpay.assets.exceptions.ErrorObject;
import io.angularpay.assets.models.GetUserInvestmentsCommandRequest;
import io.angularpay.assets.models.UserInvestmentModel;
import io.angularpay.assets.validation.DefaultConstraintValidator;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class GetUserAssetSalesCommand extends AbstractCommand<GetUserInvestmentsCommandRequest, List<UserInvestmentModel>> {

    private final MongoAdapter mongoAdapter;
    private final DefaultConstraintValidator validator;

    public GetUserAssetSalesCommand(ObjectMapper mapper, MongoAdapter mongoAdapter, DefaultConstraintValidator validator) {
        super("GetUserAssetSalesCommand", mapper);
        this.mongoAdapter = mongoAdapter;
        this.validator = validator;
    }

    @Override
    protected String getResourceOwner(GetUserInvestmentsCommandRequest request) {
        return request.getAuthenticatedUser().getUserReference();
    }

    @Override
    protected List<UserInvestmentModel> handle(GetUserInvestmentsCommandRequest request) {
        Pageable pageable = PageRequest.of(request.getPaging().getIndex(), request.getPaging().getSize());
        List<UserInvestmentModel> investmentRequests = new ArrayList<>();
        List<AssetRequest> response = this.mongoAdapter.listRequests(pageable).getContent();
        for (AssetRequest assetRequest : response) {
            List<Seller> providers = assetRequest.getSellers();
            for (Seller seller : providers) {
                if (request.getAuthenticatedUser().getUserReference().equalsIgnoreCase(seller.getUserReference())) {
                    investmentRequests.add(UserInvestmentModel.builder()
                            .requestReference(assetRequest.getReference())
                            .investmentReference(seller.getReference())
                            .userReference(seller.getUserReference())
                            .requestCreatedOn(seller.getCreatedOn())
                            .build());
                }
            }
        }
        return investmentRequests;
    }

    @Override
    protected List<ErrorObject> validate(GetUserInvestmentsCommandRequest request) {
        return this.validator.validate(request);
    }

    @Override
    protected List<Role> permittedRoles() {
        return Collections.emptyList();
    }
}
