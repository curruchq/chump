package com.conversant.chump.route.v1.businesspartners.payments;

import com.conversant.chump.common.ChumpOperation;
import com.conversant.chump.common.RestOperation;
import com.conversant.chump.model.BusinessPartnerPaymentRequest;
import com.conversant.chump.processor.ApiResponseProcessor;
import com.conversant.chump.processor.StandardResponseRemover;
import com.conversant.chump.route.AdempiereRoute;
import com.conversant.chump.route.v1.businesspartners.AbstractBusinessPartnersRoute;
import com.conversant.webservice.*;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

import static com.conversant.chump.common.RestOperation.HttpMethod.GET;
import static com.conversant.chump.common.RestOperation.HttpMethod.POST;
import static com.conversant.chump.util.AdempiereHelper.createLoginRequest;
import static com.conversant.chump.util.Constants.ADEMPIERE_USER_INTALIO;
import static com.conversant.chump.util.Constants.TYPE_CREATE_BUSINESS_PARTNER_PAYMENT;
import static com.conversant.chump.util.Constants.TYPE_READ_BUSINESS_PARTNER_PAYMENTS;

/**
 * Created by Tate on 27/01/2016.
 */
@Component
public class BusinessPartnerPaymentsRoute extends AbstractBusinessPartnersRoute {

    /**
     * Read business partner payments
     */
    public static final ChumpOperation READ = ChumpOperation.builder()
            .rest(RestOperation.builder()
                    .resource(RESOURCE)
                    .path("/{businessPartnerSearchKey}/payments")
                    .method(GET)
                    .build())
            .trx(false)
            .to(Arrays.asList(
                    ChumpOperation.pair(ReadBusinessPartnerBySearchKeyRequestProcessor.INSTANCE, AdempiereRoute.READ_BUSINESS_PARTNER_BY_SEARCH_KEY.getUri()),
                    ChumpOperation.pair(ReadBusinessPartnerPaymentsRequestProcessor.INSTANCE, AdempiereRoute.READ_BUSINESS_PARTNER_PAYMENTS.getUri())))
            .postProcessors(Arrays.asList(new StandardResponseRemover("payment"), ApiResponseProcessor.INSTANCE))
            .build();

    /**
     * Create business partner payment
     */
    public static final ChumpOperation CREATE = ChumpOperation.builder()
            .rest(RestOperation.builder()
                    .resource(RESOURCE)
                    .path("/{businessPartnerSearchKey}/payments")
                    .requestType(BusinessPartnerPaymentRequest.class)
                    .method(POST)
                    .build())
            .trx(false)
            .to(Arrays.asList(
                    ChumpOperation.pair(ReadBusinessPartnerBySearchKeyRequestProcessor.INSTANCE, AdempiereRoute.READ_BUSINESS_PARTNER_BY_SEARCH_KEY.getUri()),
                    ChumpOperation.pair(CreateOneOffPaymentProcessor.INSTANCE, AdempiereRoute.CREATE_BUSINESS_PARTNER_PAYMENT.getUri())))
            .postProcessors(Collections.singletonList(ApiResponseProcessor.INSTANCE))
            .build();

    /**
     * Read business partner payments request processor
     */
    private static final class ReadBusinessPartnerPaymentsRequestProcessor implements Processor {

        public static final Processor INSTANCE = new ReadBusinessPartnerPaymentsRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            BusinessPartner businessPartner = exchange.getIn().getBody(ReadBusinessPartnerResponse.class).getBusinessPartner();

            ReadPaymentsByBusinessPartnerRequest request = new ReadPaymentsByBusinessPartnerRequest();

            request.setLoginRequest(createLoginRequest(exchange, TYPE_READ_BUSINESS_PARTNER_PAYMENTS, ADEMPIERE_USER_INTALIO));
            request.setBusinessPartnerId(businessPartner.getBusinessPartnerId());

            exchange.getIn().setBody(request);
        }
    }

    /**
     * Create business partner payment request processor
     */
    private static final class CreateOneOffPaymentProcessor implements Processor {

        public static final Processor INSTANCE = new CreateOneOffPaymentProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            // Load and store business partner response
            BusinessPartner businessPartner = exchange.getIn().getBody(ReadBusinessPartnerResponse.class).getBusinessPartner();

            BusinessPartnerPaymentRequest businessPartnerPaymentRequest = exchange.getProperty(BusinessPartnerPaymentRequest.class.getName(), BusinessPartnerPaymentRequest.class);
            CreateOneOffPaymentRequest createOneOffPaymentRequest = new CreateOneOffPaymentRequest();

            createOneOffPaymentRequest.setLoginRequest(createLoginRequest(exchange, TYPE_CREATE_BUSINESS_PARTNER_PAYMENT, ADEMPIERE_USER_INTALIO));
            createOneOffPaymentRequest.setBusinessPartnerId(businessPartner.getBusinessPartnerId());

            //Required fields
            createOneOffPaymentRequest.setBusinessPartnerLocationId(businessPartnerPaymentRequest.getBusinessPartnerLocationId());
            createOneOffPaymentRequest.setInvoiceId(businessPartnerPaymentRequest.getInvoiceId());
            createOneOffPaymentRequest.setCreditCardNumber(businessPartnerPaymentRequest.getCreditCardNumber());
            createOneOffPaymentRequest.setCreditCardVerificationCode(businessPartnerPaymentRequest.getCreditCardVerificationCode());
            createOneOffPaymentRequest.setCreditCardExpiryMonth(businessPartnerPaymentRequest.getCreditCardExpiryMonth());
            createOneOffPaymentRequest.setCreditCardExpiryYear(businessPartnerPaymentRequest.getCreditCardExpiryYear());
            createOneOffPaymentRequest.setAmount(businessPartnerPaymentRequest.getAmount());
            createOneOffPaymentRequest.setOrgId(businessPartnerPaymentRequest.getOrgId());

            exchange.getIn().setBody(createOneOffPaymentRequest);
        }
    }
}
