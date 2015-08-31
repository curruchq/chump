package com.conversant.chump.route;

import com.conversant.chump.common.ChumpOperation;
import com.conversant.chump.common.ChumpRoute;
import com.conversant.chump.exception.FailedStandardResponseException;
import com.conversant.chump.util.AdempiereHelper;
import com.conversant.webservice.CommitTrxRequest;
import com.conversant.webservice.CreateTrxRequest;
import com.conversant.webservice.RollbackTrxRequest;
import com.conversant.webservice.StandardResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.cxf.common.message.CxfConstants;
import org.apache.cxf.message.MessageContentsList;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.conversant.chump.util.AdempiereHelper.createLoginRequest;
import static com.conversant.chump.util.Constants.*;

/**
 * Created by jhill on 31/12/14.
 */
@Component
@Slf4j
public class AdempiereRoute implements ChumpRoute {

    public static final String NAMESPACE_CONVERSANT_WEBSERVICES = "http://webservice.conversant.com/";
    public static final String TRX_NAME_PREFIX = "camel";

    public static final String ENDPOINT_ACCOUNTING = "cxf:bean:accountingEndpoint";
    public static final String ENDPOINT_ADMIN = "cxf:bean:adminEndpoint";
    public static final String ENDPOINT_PROVISION = "cxf:bean:provisionEndpoint";

    public static final ChumpOperation CREATE_TRX = build(ENDPOINT_ADMIN, "direct://createTrx", CreateTrxRequestProcesser.INSTANCE, CreateTrxResponseProcesser.INSTANCE);
    public static final ChumpOperation COMMIT_TRX = build(ENDPOINT_ADMIN, "direct://commitTrx", CommitTrxRequestProcesser.INSTANCE);
    public static final ChumpOperation ROLLBACK_TRX = build(ENDPOINT_ADMIN, "direct://rollbackTrx", RollbackTrxRequestProcesser.INSTANCE);

    public static final ChumpOperation CREATE_CALL_PRODUCT = build(ENDPOINT_PROVISION, "direct://createCallProduct");
    public static final ChumpOperation CREATE_CALL_PRODUCT_2 = build(ENDPOINT_PROVISION, "direct://createCallProduct2");
    public static final ChumpOperation CREATE_DID_PRODUCT = build(ENDPOINT_PROVISION, "direct://createDIDProduct");
    public static final ChumpOperation UPDATE_DID_PRODUCT = build(ENDPOINT_PROVISION, "direct://updateDIDProduct");
    public static final ChumpOperation CREATE_CALL_SUBSCRIPTION = build(ENDPOINT_PROVISION, "direct://createCallSubscription");
    public static final ChumpOperation CREATE_CALL_SUBSCRIPTION_2 = build(ENDPOINT_PROVISION, "direct://createCallSubscription2");
    public static final ChumpOperation CREATE_DID_SUBSCRIPTION = build(ENDPOINT_PROVISION, "direct://createDIDSubscription");
    public static final ChumpOperation CREATE_NUMBER_PORT_SUBSCRIPTION = build(ENDPOINT_PROVISION, "direct://createNumberPortSubscription");

    public static final ChumpOperation CREATE_BUSINESS_PARTNER = build(ENDPOINT_ADMIN, "direct://createBusinessPartner");
    public static final ChumpOperation CREATE_LOCATION = build(ENDPOINT_ADMIN, "direct://createLocation");
    public static final ChumpOperation CREATE_BUSINESS_PARTNER_LOCATION = build(ENDPOINT_ADMIN, "direct://createBusinessPartnerLocation");
    public static final ChumpOperation READ_BUSINESS_PARTNER_BY_SEARCH_KEY = build(ENDPOINT_ADMIN, "direct://readBusinessPartnerBySearchKey");
    public static final ChumpOperation READ_BUSINESS_PARTNER_BY_ID = build(ENDPOINT_ADMIN, "direct://readBusinessPartner");
    public static final ChumpOperation READ_BUSINESS_PARTNERS_BY_GROUP = build(ENDPOINT_ADMIN, "direct://readBusinessPartnersByGroup");
    public static final ChumpOperation READ_BUSINESS_PARTNER_LOCATION = build(ENDPOINT_ADMIN, "direct://readBPLocations");
    public static final ChumpOperation READ_BUSINESS_PARTNER_USERS = build(ENDPOINT_ADMIN, "direct://readUsersByBusinessPartner");
    public static final ChumpOperation UPDATE_BUSINESS_PARTNER = build(ENDPOINT_ADMIN, "direct://updateBusinessPartner");
    public static final ChumpOperation UPDATE_BUSINESS_PARTNER_LOCATION = build(ENDPOINT_ADMIN, "direct://updateBusinessPartnerLocation");

    public static final ChumpOperation READ_SUBSCRIBED_NUMBERS = build(ENDPOINT_ADMIN, "direct://readSubscribedNumbers");
    public static final ChumpOperation READ_INVOICE = build(ENDPOINT_ACCOUNTING, "direct://readInvoicesByBusinessPartner");
    public static final ChumpOperation READ_INVOICE_LINES = build(ENDPOINT_ACCOUNTING, "direct://readInvoiceLines");
    public static final ChumpOperation READ_SUBSCRIPTIONS = build(ENDPOINT_ADMIN, "direct://readSubscriptions");
    public static final ChumpOperation READ_SUBSCRIPTION = build(ENDPOINT_ADMIN, "direct://readSubscription");
    public static final ChumpOperation UPDATE_SUBSCRIPTION = build(ENDPOINT_ADMIN, "direct://updateSubscription");
    public static final ChumpOperation CREATE_SUBSCRIPTION = build(ENDPOINT_ADMIN, "direct://createSubscription");
    public static final ChumpOperation READ_ORDER = build(ENDPOINT_ADMIN, "direct://readOrder");
    public static final ChumpOperation READ_ORDER_DIDS = build(ENDPOINT_ADMIN, "direct://readOrderDIDs");
    public static final ChumpOperation READ_ORDER_LINES = build(ENDPOINT_ADMIN, "direct://readOrderLines");
    public static final ChumpOperation READ_PRODUCT = build(ENDPOINT_ADMIN, "direct://readProduct");
    public static final ChumpOperation READ_PRODUCT_BP_PRICE = build(ENDPOINT_ADMIN, "direct://readProductBPPrice");
    public static final ChumpOperation READ_ORDER_NUMBER_PORTS = build(ENDPOINT_ADMIN, "direct://readOrderNumberPorts");
    public static final ChumpOperation READ_ORGANISATION = build(ENDPOINT_ADMIN, "direct://readOrganization");
    public static final ChumpOperation READ_RADIUS_ACCOUNTS = build(ENDPOINT_PROVISION, "direct://readRadiusAccountsByInvoice");
    
    public static final ChumpOperation CREATE_USER = build(ENDPOINT_ADMIN, "direct://createUser");
    public static final ChumpOperation READ_USER = build(ENDPOINT_ADMIN, "direct://readUser");
    public static final ChumpOperation UPDATE_USER = build(ENDPOINT_ADMIN, "direct://updateUser");
    public static final ChumpOperation DELETE_USER = build(ENDPOINT_ADMIN, "direct://deleteUser");

    public static final ChumpOperation CREATE_USER_ROLE = build(ENDPOINT_ADMIN, "direct://createUserRole");
    public static final ChumpOperation READ_USER_ROLE = build(ENDPOINT_ADMIN, "direct://readUserRole");
    public static final ChumpOperation DELETE_USER_ROLE = build(ENDPOINT_ADMIN, "direct://deleteUserRole");

    private static ChumpOperation build(String endPoint, String uri) {
        return build(endPoint, uri, null);
    }

    private static ChumpOperation build(String endPoint, String uri, Processor preProcessor) {
        return build(endPoint, uri, preProcessor, null);
    }

    private static ChumpOperation build(String endPoint, String uri, Processor preProcessor, Processor postProcessor) {
        return ChumpOperation.builder()
                .uri(uri)
                .trx(false)
                .preProcessors(preProcessor != null ? getPreProcessors(preProcessor) : getPreProcessors())
                .to(Arrays.asList(ChumpOperation.single(endPoint)))
                .postProcessors(postProcessor != null ? getPostProcessors(postProcessor) : getPostProcessors())
                .build();
    }

    private static List<Processor> getPreProcessors(Processor... processors) {
        List<Processor> result = new ArrayList<>();
        result.add(HeaderProcesser.INSTANCE);
        for (Processor p : processors)
            result.add(p);
        result.add(BodyProcesser.INSTANCE);
        return result;
    }

    private static List<Processor> getPostProcessors(Processor... processors) {
        List<Processor> result = new ArrayList<>();
        result.add(ResponseProcessor.INSTANCE);
        for (Processor p : processors)
            result.add(p);
        return result;
    }

    private static class HeaderProcesser implements Processor {

        public static final Processor INSTANCE = new HeaderProcesser();

        @Override
        public void process(Exchange exchange) throws Exception {

            // TODO: Fixme
            String operationName = (String) exchange.getProperty("CamelToEndpoint");
            operationName = operationName.substring(operationName.indexOf("//") + 2);

            exchange.getIn().setHeader(CxfConstants.OPERATION_NAMESPACE, NAMESPACE_CONVERSANT_WEBSERVICES);
            exchange.getIn().setHeader(CxfConstants.OPERATION_NAME, operationName);
        }
    }

    private static class BodyProcesser implements Processor {

        public static final Processor INSTANCE = new BodyProcesser();

        @Override
        public void process(Exchange exchange) throws Exception {

            // TODO: Remove this processor once ProcessToPair used
            Object body = AdempiereHelper.removeRequestBodyProperty(exchange);
            if (body != null)
                exchange.getIn().setBody(body);
        }
    }

    private static class ResponseProcessor implements Processor {

        public static final Processor INSTANCE = new ResponseProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {
            MessageContentsList contents = exchange.getIn().getBody(MessageContentsList.class);

            // TODO: Another way to check for failure and throw exception?
            StandardResponse response;
            if (contents.get(0) instanceof StandardResponse) {
                response = (StandardResponse) contents.get(0);
            } else {
                Object obj = contents.get(0);
                Method method = obj.getClass().getMethod("getStandardResponse");
                if (method != null && method.getReturnType().equals(StandardResponse.class)) {
                    response = (StandardResponse) method.invoke(obj);
                } else {
                    throw new RuntimeException("Failed to load StandardResponse");
                }
            }

            if (response.isSuccess()) {
                // TODO: Only used by com.conversant.chump.route.external.AdempiereRoute.CreateTrxPostProcesser.process()
                exchange.getOut().setBody(contents.get(0));
            } else {
                throw new FailedStandardResponseException(response);
            }
        }
    }

    private static class CreateTrxRequestProcesser implements Processor {

        public static final Processor INSTANCE = new CreateTrxRequestProcesser();

        @Override
        public void process(Exchange exchange) throws Exception {

            CreateTrxRequest request = new CreateTrxRequest();
            request.setLoginRequest(createLoginRequest(exchange, TYPE_CREATE_TRX));
            request.setTrxNamePrefix(TRX_NAME_PREFIX);

            AdempiereHelper.setRequestBodyProperty(exchange, CREATE_TRX, request);
        }
    }

    private static class CreateTrxResponseProcesser implements Processor {

        public static final Processor INSTANCE = new CreateTrxResponseProcesser();

        @Override
        public void process(Exchange exchange) throws Exception {

            // TODO: Only do if trx(true)
            String trxName = ((StandardResponse) exchange.getIn().getBody()).getTrxName();
            exchange.setProperty(PROPERTY_TRX_NAME, trxName);
        }
    }

    private static class CommitTrxRequestProcesser implements Processor {

        public static final Processor INSTANCE = new CommitTrxRequestProcesser();

        @Override
        public void process(Exchange exchange) throws Exception {

            CommitTrxRequest request = new CommitTrxRequest();
            request.setLoginRequest(createLoginRequest(exchange, TYPE_COMMIT_TRX));

            AdempiereHelper.setRequestBodyProperty(exchange, COMMIT_TRX, request);
            exchange.setProperty(PROPERTY_TRX_NAME, null);
        }
    }

    private static class RollbackTrxRequestProcesser implements Processor {

        public static final Processor INSTANCE = new RollbackTrxRequestProcesser();

        @Override
        public void process(Exchange exchange) throws Exception {

            RollbackTrxRequest request = new RollbackTrxRequest();
            request.setLoginRequest(createLoginRequest(exchange, TYPE_ROLLBACK_TRX));

            AdempiereHelper.setRequestBodyProperty(exchange, ROLLBACK_TRX, request);
            exchange.setProperty(PROPERTY_TRX_NAME, null);
        }
    }
}
