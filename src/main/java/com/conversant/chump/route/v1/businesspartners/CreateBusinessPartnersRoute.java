package com.conversant.chump.route.v1.businesspartners;

import com.conversant.chump.common.ChumpOperation;
import com.conversant.chump.common.RestOperation;
import com.conversant.chump.model.BusinessPartnerRequest;
import com.conversant.chump.processor.ApiResponseProcessor;
import com.conversant.chump.route.AdempiereRoute;
import com.conversant.webservice.CreateBusinessPartnerRequest;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.Collections;

import static com.conversant.chump.common.RestOperation.HttpMethod.POST;
import static com.conversant.chump.util.AdempiereHelper.createLoginRequest;
import static com.conversant.chump.util.Constants.ADEMPIERE_USER_DRUPAL;
import static com.conversant.chump.util.Constants.TYPE_CREATE_BUSINESS_PARTNER;

/**
 * Created by jhill on 20/06/15.
 */
@Component
public class CreateBusinessPartnersRoute extends AbstractBusinessPartnersRoute {

    /**
     * Create business partner
     */
    public static final ChumpOperation CREATE = ChumpOperation.builder()
            .rest(RestOperation.builder()
                    .resource(RESOURCE)
                    .method(POST)
                    .requestType(BusinessPartnerRequest.class)
                    .build())
            .trx(false)
            .to(Collections.singletonList(ChumpOperation.pair(CreateBusinessPartnerRequestProcessor.INSTANCE, AdempiereRoute.CREATE_BUSINESS_PARTNER.getUri())))
            .postProcessors(Collections.singletonList(ApiResponseProcessor.INSTANCE))
            .build();

    /**
     * Create business partner request processor
     */
    private static final class CreateBusinessPartnerRequestProcessor implements Processor {

        public static final Processor INSTANCE = new CreateBusinessPartnerRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            BusinessPartnerRequest businessPartnerRequest = exchange.getProperty(BusinessPartnerRequest.class.getName(), BusinessPartnerRequest.class);

            CreateBusinessPartnerRequest createBusinessPartnerRequest = new CreateBusinessPartnerRequest();
            createBusinessPartnerRequest.setLoginRequest(createLoginRequest(exchange, TYPE_CREATE_BUSINESS_PARTNER, ADEMPIERE_USER_DRUPAL));
            createBusinessPartnerRequest.setOrgId(businessPartnerRequest.getOrgId());
            createBusinessPartnerRequest.setSearchKey(businessPartnerRequest.getSearchKey());
            createBusinessPartnerRequest.setName(businessPartnerRequest.getName());
            createBusinessPartnerRequest.setTaxExempt(businessPartnerRequest.isTaxExempt());
            createBusinessPartnerRequest.setBusinessPartnerGroupId(businessPartnerRequest.getBusinessPartnerGroupId());
            createBusinessPartnerRequest.setSalesRepId(businessPartnerRequest.getSalesRepId());

            exchange.getIn().setBody(createBusinessPartnerRequest);
        }
    }
}
