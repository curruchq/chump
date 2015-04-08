package com.conversant.chump.route;

import com.conversant.chump.common.ChumpOperation;
import com.conversant.chump.common.ChumpRoute;
import com.conversant.chump.common.RestOperation;
import com.conversant.chump.model.InsertBillingCustomerInboundRequest;
import com.conversant.chump.model.InsertBillingCustomerOutboundRequest;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.conversant.chump.common.RestOperation.HttpMethod.POST;
import static com.conversant.chump.common.RestOperation.HttpMethod.PUT;

/**
 * Route for inserting entries into the cdrtool.billing_customers table.
 *
 * Created by Saren Currie on 2015-04-09.
 */
@Component
public class BillingCustomerRoute implements ChumpRoute {

    private static final String RESOURCE = "/v1/billingCustomers";

    public static final ChumpOperation INSERT_INBOUND = ChumpOperation.builder()
            .rest(RestOperation.builder()
                    .resource(RESOURCE)
                    .path("/inbound")
                    .requestType(InsertBillingCustomerInboundRequest.class)
                    .method(POST)
                    .build()
            )
            .trx(false)
            .to(Arrays.asList(ChumpOperation.pair(InsertInboundRequestProcessor.INSTANCE, "sql:{{sql.cdrtool.insertBillingCustomer}}")))
            .build();

    public static final ChumpOperation UPDATE_INBOUND = ChumpOperation.builder()
            .rest(RestOperation.builder()
                    .resource(RESOURCE)
                    .path("/inbound/{subscriber}")
                    .requestType(InsertBillingCustomerInboundRequest.class)
                    .method(PUT)
                    .build()
            )
            .trx(false)
            .to(Arrays.asList(ChumpOperation.pair(UpdateInboundRequestProcessor.INSTANCE, "sql:{{sql.cdrtool.updateBillingCustomerInbound}}")))
            .build();

    public static final ChumpOperation INSERT_OUTBOUND = ChumpOperation.builder()
            .rest(RestOperation.builder()
                    .resource(RESOURCE)
                    .path("/outbound")
                    .requestType(InsertBillingCustomerOutboundRequest.class)
                    .method(POST)
                    .build()
            )
            .trx(false)
            .to(Arrays.asList(ChumpOperation.pair(InsertOutboundRequestProcessor.INSTANCE, "sql:{{sql.cdrtool.insertBillingCustomer}}")))
            .build();

    public static final ChumpOperation UPDATE_OUTBOUND = ChumpOperation.builder()
            .rest(RestOperation.builder()
                    .resource(RESOURCE)
                    .path("/outbound/{domain}")
                    .requestType(InsertBillingCustomerOutboundRequest.class)
                    .method(PUT)
                    .build()
            )
            .trx(false)
            .to(Arrays.asList(ChumpOperation.pair(UpdateOutboundRequestProcessor.INSTANCE, "sql:{{sql.cdrtool.updateBillingCustomerOutbound}}")))
            .build();


    private static final class InsertInboundRequestProcessor implements Processor {

        public static final Processor INSTANCE = new InsertInboundRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            InsertBillingCustomerInboundRequest request = exchange.getIn().getBody(InsertBillingCustomerInboundRequest.class);

            Map<String, Object> body = new HashMap<>();
            body.put("domain", null);
            body.put("subscriber", request.getSubscriber());
            body.put("profile_name1", request.getProfileName());
            body.put("profile_name1_alt", request.getProfileNameAlt());
            body.put("profile_name2", request.getProfileName());
            body.put("profile_name2_alt", request.getProfileNameAlt());
            body.put("timezone", request.getTimezone());
            body.put("increment", 0);
            body.put("min_duration", 0);
            body.put("country_code", "");

            exchange.getIn().setBody(body);
        }
    }

    private static final class UpdateInboundRequestProcessor implements Processor {

        public static final Processor INSTANCE = new UpdateInboundRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            InsertBillingCustomerInboundRequest request = exchange.getIn().getBody(InsertBillingCustomerInboundRequest.class);

            Map<String, Object> body = new HashMap<>();
            body.put("update_subscriber", exchange.getIn().getHeader("subscriber"));
            body.put("domain", null);
            body.put("subscriber", request.getSubscriber());
            body.put("profile_name1", request.getProfileName());
            body.put("profile_name1_alt", request.getProfileNameAlt());
            body.put("profile_name2", request.getProfileName());
            body.put("profile_name2_alt", request.getProfileNameAlt());
            body.put("timezone", request.getTimezone());
            body.put("increment", 0);
            body.put("min_duration", 0);
            body.put("country_code", "");

            exchange.getIn().setBody(body);
        }
    }

    private static final class InsertOutboundRequestProcessor implements Processor {

        public static final Processor INSTANCE = new InsertOutboundRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            InsertBillingCustomerOutboundRequest request = exchange.getIn().getBody(InsertBillingCustomerOutboundRequest.class);

            Map<String, Object> body = new HashMap<>();
            body.put("domain", request.getDomain());
            body.put("subscriber", null);
            body.put("profile_name1", request.getProfileName());
            body.put("profile_name1_alt", request.getProfileNameAlt());
            body.put("profile_name2", request.getProfileName());
            body.put("profile_name2_alt", request.getProfileNameAlt());
            body.put("timezone", request.getTimezone());
            body.put("increment", 0);
            body.put("min_duration", 0);
            body.put("country_code", "");

            exchange.getIn().setBody(body);
        }
    }

    private static final class UpdateOutboundRequestProcessor implements Processor {

        public static final Processor INSTANCE = new UpdateOutboundRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            InsertBillingCustomerOutboundRequest request = exchange.getIn().getBody(InsertBillingCustomerOutboundRequest.class);

            Map<String, Object> body = new HashMap<>();
            body.put("update_domain", exchange.getIn().getHeader("domain"));
            body.put("domain", request.getDomain());
            body.put("subscriber", null);
            body.put("profile_name1", request.getProfileName());
            body.put("profile_name1_alt", request.getProfileNameAlt());
            body.put("profile_name2", request.getProfileName());
            body.put("profile_name2_alt", request.getProfileNameAlt());
            body.put("timezone", request.getTimezone());
            body.put("increment", 0);
            body.put("min_duration", 0);
            body.put("country_code", "");

            exchange.getIn().setBody(body);
        }
    }
}

