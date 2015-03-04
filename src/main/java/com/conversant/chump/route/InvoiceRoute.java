package com.conversant.chump.route;

import com.conversant.chump.common.ChumpOperation;
import com.conversant.chump.common.ChumpRoute;
import com.conversant.chump.common.RestOperation;
import com.conversant.chump.model.ReadInvoiceLinesByIdRequest;
import com.conversant.chump.model.ReadInvoiceRequest;
import com.conversant.chump.processor.ApiResponseProcessor;
import com.conversant.chump.processor.ReadBusinessPartnerRequestProcessor;
import com.conversant.chump.processor.StandardResponseRemover;
import com.conversant.webservice.BusinessPartner;
import com.conversant.webservice.ReadBusinessPartnerResponse;
import com.conversant.webservice.ReadInvoiceLinesRequest;
import com.conversant.webservice.ReadInvoicesByBusinessPartnerRequest;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static com.conversant.chump.common.RestOperation.HttpMethod.GET;
import static com.conversant.chump.util.AdempiereHelper.createLoginRequest;
import static com.conversant.chump.util.Constants.*;

/**
 * Created by Saren Currie on 15-01-16.
 */
@Component
public class InvoiceRoute implements ChumpRoute {

    private static final String RESOURCE = "/v1/invoices";

    public static final ChumpOperation READ = ChumpOperation.builder()
            .trx(false)
            .rest(RestOperation.builder()
                    .resource(RESOURCE)
                    .method(GET)
                    .requestType(ReadInvoiceRequest.class)
                    .build())
            .to(Arrays.asList(
                    ChumpOperation.pair(ReadBusinessPartnerRequestProcessor.INSTANCE, AdempiereRoute.READ_BUSINESS_PARTNER.getUri()),
                    ChumpOperation.pair(ReadInvoicesByBusinessPartnerRequestProcessor.INSTANCE, AdempiereRoute.READ_INVOICE.getUri())))
            .postProcessors(
                    Arrays.asList(new StandardResponseRemover("invoice"), ApiResponseProcessor.INSTANCE))
            .build();

    public static final ChumpOperation GET_LINES = ChumpOperation.builder()
            .trx(false)
            .rest(RestOperation.builder()
                            .path("/{invoiceId}/lines")
                            .resource(RESOURCE)
                            .method(GET)
                            .requestType(ReadInvoiceLinesByIdRequest.class)
                            .build()
            ).to(Arrays.asList(
                    ChumpOperation.pair(ReadInvoiceLinesRequestProcessor.INSTANCE, AdempiereRoute.READ_INVOICE_LINES.getUri())))
            .postProcessors(
                    Arrays.asList(new StandardResponseRemover("invoiceLine"), ApiResponseProcessor.INSTANCE))
            .build();

    private static final class ReadInvoicesByBusinessPartnerRequestProcessor implements Processor {

        public static final Processor INSTANCE = new ReadInvoicesByBusinessPartnerRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {
            BusinessPartner bp = exchange.getIn().getBody(ReadBusinessPartnerResponse.class).getBusinessPartner();

            ReadInvoicesByBusinessPartnerRequest request = new ReadInvoicesByBusinessPartnerRequest();
            request.setLoginRequest(createLoginRequest(exchange, TYPE_READ_INVOICES, ADEMPIERE_USER_DRUPAL));
            request.setBusinessPartnerId(bp.getBusinessPartnerId());

            exchange.getIn().setBody(request);

        }
    }

    private static final class ReadInvoiceLinesRequestProcessor implements Processor {

        public static final Processor INSTANCE = new ReadInvoiceLinesRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            ReadInvoiceLinesRequest request = new ReadInvoiceLinesRequest();
            request.setLoginRequest(createLoginRequest(exchange, TYPE_READ_INVOICE_LINES, ADEMPIERE_USER_INTALIO));
            request.setInvoiceId(Integer.parseInt((String) exchange.getIn().getHeader("invoiceId")));

            exchange.getIn().setBody(request);
        }
    }
}
