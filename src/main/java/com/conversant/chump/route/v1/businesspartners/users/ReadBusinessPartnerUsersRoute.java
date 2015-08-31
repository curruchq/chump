package com.conversant.chump.route.v1.businesspartners.users;

import com.conversant.chump.common.ChumpOperation;
import com.conversant.chump.common.RestOperation;
import com.conversant.chump.processor.ApiResponseProcessor;
import com.conversant.chump.processor.StandardResponseRemover;
import com.conversant.chump.route.AdempiereRoute;
import com.conversant.chump.route.v1.businesspartners.AbstractBusinessPartnersRoute;
import com.conversant.webservice.BusinessPartner;
import com.conversant.webservice.ReadBusinessPartnerResponse;
import com.conversant.webservice.ReadUsersByBusinessPartnerRequest;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static com.conversant.chump.common.RestOperation.HttpMethod.GET;
import static com.conversant.chump.util.AdempiereHelper.createLoginRequest;
import static com.conversant.chump.util.Constants.ADEMPIERE_USER_DRUPAL;
import static com.conversant.chump.util.Constants.TYPE_READ_BUSINESS_PARTNER_USERS;

/**
 * Created by jhill on 20/06/15.
 */
@Component
public class ReadBusinessPartnerUsersRoute extends AbstractBusinessPartnersRoute {

    /**
     * Read business partner locations
     */
    public static final ChumpOperation READ = ChumpOperation.builder()
            .rest(RestOperation.builder()
                    .resource(RESOURCE)
                    .path("/{businessPartnerSearchKey}/users")
                    .method(GET)
                    .build())
            .trx(false)
            .to(Arrays.asList(
                    ChumpOperation.pair(ReadBusinessPartnerBySearchKeyRequestProcessor.INSTANCE, AdempiereRoute.READ_BUSINESS_PARTNER_BY_SEARCH_KEY.getUri()),
                    ChumpOperation.pair(ReadBusinessPartnerUsersRequestProcessor.INSTANCE, AdempiereRoute.READ_BUSINESS_PARTNER_USERS.getUri())))
            .postProcessors(Arrays.asList(new StandardResponseRemover("user"), ApiResponseProcessor.INSTANCE))
            .build();

    /**
     * Read business partner users request processor
     */
    private static final class ReadBusinessPartnerUsersRequestProcessor implements Processor {

        public static final Processor INSTANCE = new ReadBusinessPartnerUsersRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            BusinessPartner businessPartner = exchange.getIn().getBody(ReadBusinessPartnerResponse.class).getBusinessPartner();

            ReadUsersByBusinessPartnerRequest request = new ReadUsersByBusinessPartnerRequest();
            request.setLoginRequest(createLoginRequest(exchange, TYPE_READ_BUSINESS_PARTNER_USERS, ADEMPIERE_USER_DRUPAL));
            request.setBusinessPartnerId(businessPartner.getBusinessPartnerId());

            exchange.getIn().setBody(request);
        }
    }
}
