package com.conversant.chump.route.v1.businesspartners;

import com.conversant.chump.common.ChumpOperation;
import com.conversant.chump.common.RestOperation;
import com.conversant.chump.processor.ApiResponseProcessor;
import com.conversant.chump.processor.StandardResponseRemover;
import com.conversant.chump.route.AdempiereRoute;
import com.conversant.webservice.BusinessPartner;
import com.conversant.webservice.ReadBusinessPartnerResponse;
import com.conversant.webservice.ReadSubscribedNumbersRequest;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static com.conversant.chump.common.RestOperation.HttpMethod.GET;
import static com.conversant.chump.util.AdempiereHelper.createLoginRequest;
import static com.conversant.chump.util.Constants.ADEMPIERE_USER_DRUPAL;
import static com.conversant.chump.util.Constants.TYPE_READ_SUBSCRIBED_NUMBERS;

/**
 * Created by jhill on 20/06/15.
 */
@Component
public class BusinessPartnerNumbersRoute extends AbstractBusinessPartnersRoute {

    /**
     * Read business partner numbers
     */
    public static final ChumpOperation READ = ChumpOperation.builder()
            .rest(RestOperation.builder()
                    .resource(RESOURCE)
                    .path("/{businessPartnerSearchKey}/numbers")
                    .method(GET)
                    .build())
            .trx(false)
            .to(Arrays.asList(
                    ChumpOperation.pair(ReadBusinessPartnerBySearchKeyRequestProcessor.INSTANCE, AdempiereRoute.READ_BUSINESS_PARTNER_BY_SEARCH_KEY.getUri()),
                    ChumpOperation.pair(ReadSubscribedNumbersRequestProcessor.INSTANCE, AdempiereRoute.READ_SUBSCRIBED_NUMBERS.getUri())))
            .postProcessors(Arrays.asList(
                    new StandardResponseRemover("numbers"), ApiResponseProcessor.INSTANCE))
            .build();

    /**
     * Read subscribed numbers request processor
     */
    private static final class ReadSubscribedNumbersRequestProcessor implements Processor {

        public static final Processor INSTANCE = new ReadSubscribedNumbersRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            BusinessPartner businessPartner = exchange.getIn().getBody(ReadBusinessPartnerResponse.class).getBusinessPartner();

            ReadSubscribedNumbersRequest request = new ReadSubscribedNumbersRequest();
            request.setLoginRequest(createLoginRequest(exchange, TYPE_READ_SUBSCRIBED_NUMBERS, ADEMPIERE_USER_DRUPAL));
            request.setBusinessPartnerId(businessPartner.getBusinessPartnerId());

            exchange.getIn().setBody(request);
        }
    }
}
