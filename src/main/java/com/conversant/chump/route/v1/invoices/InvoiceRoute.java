package com.conversant.chump.route.v1.invoices;

import com.conversant.chump.common.ChumpOperation;
import com.conversant.chump.common.ChumpRoute;
import com.conversant.chump.common.RestOperation;
import com.conversant.chump.model.ReadInvoiceLinesByIdRequest;
import com.conversant.chump.processor.ApiResponseProcessor;
import com.conversant.chump.processor.ReadBusinessPartnerRequestProcessor;
import com.conversant.chump.processor.StandardResponseRemover;
import com.conversant.chump.route.AdempiereRoute;
import com.conversant.webservice.*;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;

import static com.conversant.chump.common.RestOperation.HttpMethod.GET;
import static com.conversant.chump.util.AdempiereHelper.createLoginRequest;
import static com.conversant.chump.util.Constants.*;

/**
 * Created by Saren Currie on 15-01-16.
 */
@Component
public class InvoiceRoute implements ChumpRoute {

    private static final String RESOURCE = "/v1/invoices";

    // TODO: Remove, replaced with /v1/businesspartners/1000009/invoices in ReadBusinessPartnerInvoicesRoute
    public static final ChumpOperation READ = ChumpOperation.builder()
            .trx(false)
            .rest(RestOperation.builder()
                    .resource(RESOURCE)
                    .method(GET)
                    .build())
            .to(Arrays.asList(
                    ChumpOperation.pair(ReadBusinessPartnerRequestProcessor.INSTANCE, AdempiereRoute.READ_BUSINESS_PARTNER_BY_SEARCH_KEY.getUri()),
                    ChumpOperation.pair(ReadInvoicesByBusinessPartnerRequestProcessor.INSTANCE, AdempiereRoute.READ_BUSINESS_PARTNER_INVOICES.getUri())))
            .postProcessors(
                    Arrays.asList(new StandardResponseRemover("invoice"), ApiResponseProcessor.INSTANCE))
            .build();

    public static final ChumpOperation GET_SINGLE = ChumpOperation.builder()
            .trx(false)
            .rest(RestOperation.builder()
                    .path("/{invoiceId}")
                    .resource(RESOURCE)
                    .method(GET)
                    .build())
            .to(Collections.singletonList(
                    ChumpOperation.pair(ReadInvoiceRequestProcessor.INSTANCE, AdempiereRoute.READ_INVOICE.getUri())))
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
                    .build())
            .to(Collections.singletonList(
                    ChumpOperation.pair(ReadInvoiceLinesRequestProcessor.INSTANCE, AdempiereRoute.READ_INVOICE_LINES.getUri())))
            .postProcessors(
                    Arrays.asList(new StandardResponseRemover("invoiceLine"), ApiResponseProcessor.INSTANCE))
            .build();

    public static final ChumpOperation GET_RADIUS_ACCOUNTS = ChumpOperation.builder()
            .trx(false)
            .rest(RestOperation.builder()
                    .path("/{invoiceId}/radiusAccounts")
                    .resource(RESOURCE)
                    .method(GET)
                    .build())
            .to(Collections.singletonList(
                    ChumpOperation.pair(ReadRadiusAccountsByInvoiceRequestProcessor.INSTANCE, AdempiereRoute.READ_RADIUS_ACCOUNTS.getUri())))
            .postProcessors(
                    Arrays.asList(new StandardResponseRemover("radiusAccount"), ApiResponseProcessor.INSTANCE))
            .build();

    private static final class ReadInvoicesByBusinessPartnerRequestProcessor implements Processor {

        public static final Processor INSTANCE = new ReadInvoicesByBusinessPartnerRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {
            BusinessPartner bp = exchange.getIn().getBody(ReadBusinessPartnerResponse.class).getBusinessPartner();

            ReadInvoicesByBusinessPartnerRequest request = new ReadInvoicesByBusinessPartnerRequest();
            request.setLoginRequest(createLoginRequest(exchange, TYPE_READ_INVOICES_BY_BUSINESS_PARTNER, ADEMPIERE_USER_DRUPAL));
            request.setBusinessPartnerId(bp.getBusinessPartnerId());

            exchange.getIn().setBody(request);

        }
    }

    private static final class ReadInvoiceRequestProcessor implements Processor {

        public static final Processor INSTANCE = new ReadInvoiceRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            ReadInvoiceRequest request = new ReadInvoiceRequest();
            request.setLoginRequest(createLoginRequest(exchange, TYPE_READ_INVOICE, ADEMPIERE_USER_INTALIO));

            String id = (String) exchange.getIn().getHeader("invoiceId");
            try {
                request.setInvoiceId(Integer.parseInt(id));
                request.setGuid("");
            }
            catch (NumberFormatException e) {
                request.setGuid(id);
            }

            exchange.getIn().setBody(request);
        }
    }

    private static final class ReadInvoiceLinesRequestProcessor implements Processor {

        public static final Processor INSTANCE = new ReadInvoiceLinesRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            ReadInvoiceLinesRequest request = new ReadInvoiceLinesRequest();
            request.setLoginRequest(createLoginRequest(exchange, TYPE_READ_INVOICE_LINES, ADEMPIERE_USER_INTALIO));

            String id = (String) exchange.getIn().getHeader("invoiceId");
            try {
                request.setInvoiceId(Integer.parseInt(id));
                request.setGuid("");
            }
            catch (NumberFormatException e) {
                request.setGuid(id);
            }

            exchange.getIn().setBody(request);
        }
    }

    private static final class ReadRadiusAccountsByInvoiceRequestProcessor implements Processor {

        public static final Processor INSTANCE = new ReadRadiusAccountsByInvoiceRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            ReadRadiusAccountsByInvoiceRequest request = new ReadRadiusAccountsByInvoiceRequest();
            request.setLoginRequest(createLoginRequest(exchange, TYPE_READ_RADIUS_ACCOUNTS, ADEMPIERE_USER_INTALIO));

            String id = (String) exchange.getIn().getHeader("invoiceId");
            try {
                request.setInvoiceId(Integer.parseInt(id));
                request.setGuid("");
            }
            catch (NumberFormatException e) {
                request.setGuid(id);
            }

            exchange.getIn().setBody(request);
        }
    }
}
