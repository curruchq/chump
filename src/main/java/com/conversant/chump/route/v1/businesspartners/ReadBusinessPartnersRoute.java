package com.conversant.chump.route.v1.businesspartners;

import com.conversant.chump.common.ChumpOperation;
import com.conversant.chump.common.RestOperation;
import com.conversant.chump.processor.ApiResponseProcessor;
import com.conversant.chump.processor.StandardResponseRemover;
import com.conversant.chump.route.AdempiereRoute;
import com.conversant.webservice.BusinessPartner;
import com.conversant.webservice.ReadBusinessPartnerRequest;
import com.conversant.webservice.ReadBusinessPartnerResponse;
import com.conversant.webservice.ReadBusinessPartnersByGroupRequest;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;

import static com.conversant.chump.util.AdempiereHelper.createLoginRequest;
import static com.conversant.chump.util.Constants.*;
import static com.conversant.chump.common.RestOperation.HttpMethod.GET;

/**
 * Created by jhill on 20/06/15.
 */
@Component
public class ReadBusinessPartnersRoute extends AbstractBusinessPartnersRoute {

    /**
     * Read business partner
     */
    public static final ChumpOperation READ = ChumpOperation.builder()
            .rest(RestOperation.builder()
                    .resource(RESOURCE)
                    .path("/{businessPartnerSearchKey}")
                    .method(GET)
                    .build())
            .trx(false)
            .to(Arrays.asList(
                    ChumpOperation.pair(ReadBusinessPartnerBySearchKeyRequestProcessor.INSTANCE, AdempiereRoute.READ_BUSINESS_PARTNER_BY_SEARCH_KEY.getUri()),
                    ChumpOperation.pair(ReadBusinessPartnerByIdRequestProcessor.INSTANCE, AdempiereRoute.READ_BUSINESS_PARTNER_BY_ID.getUri())))
            .postProcessors(Arrays.asList(
                    new StandardResponseRemover("businessPartner"), ApiResponseProcessor.INSTANCE))
            .build();

    /**
     * Read business partner by id request processor
     */
    private static final class ReadBusinessPartnerByIdRequestProcessor implements Processor {

        public static final Processor INSTANCE = new ReadBusinessPartnerByIdRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            BusinessPartner businessPartner = exchange.getIn().getBody(ReadBusinessPartnerResponse.class).getBusinessPartner();

            ReadBusinessPartnerRequest readBusinessPartnerRequest = new ReadBusinessPartnerRequest();
            readBusinessPartnerRequest.setLoginRequest(createLoginRequest(exchange, TYPE_READ_BUSINESS_PARTNERS, ADEMPIERE_USER_DRUPAL));
            readBusinessPartnerRequest.setBusinessPartnerId(businessPartner.getBusinessPartnerId());

            exchange.getIn().setBody(readBusinessPartnerRequest);
        }
    }

    /**
     * Read business partner by group
     */
    public static final ChumpOperation READ_BY_GROUP = ChumpOperation.builder()
            .rest(RestOperation.builder()
                    .resource(RESOURCE)
                    .method(GET)
                    .build())
            .trx(false)
            .to(Collections.singletonList(
                    ChumpOperation.pair(ReadBusinessPartnerByGroupRequestProcessor.INSTANCE, AdempiereRoute.READ_BUSINESS_PARTNERS_BY_GROUP.getUri())))
            .postProcessors(Arrays.asList(
                    new StandardResponseRemover("businessPartner"), ApiResponseProcessor.INSTANCE))
            .build();

    /**
     * Read business partner by group request processor
     */
    private static final class ReadBusinessPartnerByGroupRequestProcessor implements Processor {

        public static final Processor INSTANCE = new ReadBusinessPartnerByGroupRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            ReadBusinessPartnersByGroupRequest request = new ReadBusinessPartnersByGroupRequest();
            request.setLoginRequest(createLoginRequest(exchange, TYPE_READ_BUSINESS_PARTNERS_BY_GROUP, ADEMPIERE_USER_INTALIO));
            request.setBusinessPartnerGroupId(Integer.parseInt((String) exchange.getIn().getHeader("businessPartnerGroupId")));

            exchange.getIn().setBody(request);
        }
    }
}
