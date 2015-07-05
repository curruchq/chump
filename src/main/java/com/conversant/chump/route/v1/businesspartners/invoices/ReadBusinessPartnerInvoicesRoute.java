package com.conversant.chump.route.v1.businesspartners.invoices;

import com.conversant.chump.common.ChumpOperation;
import com.conversant.chump.common.RestOperation;
import com.conversant.chump.processor.ApiResponseProcessor;
import com.conversant.chump.processor.StandardResponseRemover;
import com.conversant.chump.route.AdempiereRoute;
import com.conversant.chump.route.v1.businesspartners.AbstractBusinessPartnersRoute;
import com.conversant.webservice.BusinessPartner;
import com.conversant.webservice.ReadBusinessPartnerResponse;
import com.conversant.webservice.ReadInvoicesByBusinessPartnerRequest;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static com.conversant.chump.common.RestOperation.HttpMethod.GET;
import static com.conversant.chump.util.AdempiereHelper.createLoginRequest;
import static com.conversant.chump.util.Constants.ADEMPIERE_USER_INTALIO;
import static com.conversant.chump.util.Constants.TYPE_READ_BUSINESS_PARTNER_INVOICES;

/**
 * Created by jhill on 7/09/15.
 */
@Component
public class ReadBusinessPartnerInvoicesRoute extends AbstractBusinessPartnersRoute {

    /**
     * Read business partner invoices
     */
    public static final ChumpOperation READ = ChumpOperation.builder()
            .rest(RestOperation.builder()
                    .resource(RESOURCE)
                    .path("/{businessPartnerSearchKey}/invoices")
                    .method(GET)
                    .build())
            .trx(false)
            .to(Arrays.asList(
                    ChumpOperation.pair(ReadBusinessPartnerBySearchKeyRequestProcessor.INSTANCE, AdempiereRoute.READ_BUSINESS_PARTNER_BY_SEARCH_KEY.getUri()),
                    ChumpOperation.pair(ReadBusinessPartnerInvoicesRequestProcessor.INSTANCE, AdempiereRoute.READ_BUSINESS_PARTNER_INVOICES.getUri())))
            .postProcessors(Arrays.asList(new StandardResponseRemover("invoice"), ApiResponseProcessor.INSTANCE))
            .build();

    /**
     * Read business partner invoices request processor
     */
    private static final class ReadBusinessPartnerInvoicesRequestProcessor implements Processor {

        public static final Processor INSTANCE = new ReadBusinessPartnerInvoicesRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            BusinessPartner businessPartner = exchange.getIn().getBody(ReadBusinessPartnerResponse.class).getBusinessPartner();

            ReadInvoicesByBusinessPartnerRequest request = new ReadInvoicesByBusinessPartnerRequest();
            request.setLoginRequest(createLoginRequest(exchange, TYPE_READ_BUSINESS_PARTNER_INVOICES, ADEMPIERE_USER_INTALIO));
            request.setBusinessPartnerId(businessPartner.getBusinessPartnerId());

            exchange.getIn().setBody(request);
        }
    }
}
