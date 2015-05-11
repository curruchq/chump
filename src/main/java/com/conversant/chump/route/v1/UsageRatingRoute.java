package com.conversant.chump.route.v1;

import com.conversant.chump.common.ChumpOperation;
import com.conversant.chump.common.ChumpRoute;
import com.conversant.chump.common.RestOperation;
import com.conversant.chump.model.BillingCustomerRequest;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.conversant.chump.common.RestOperation.HttpMethod.POST;
import static com.conversant.chump.common.RestOperation.HttpMethod.PUT;

/**
 * Created by Saren Currie on 2015-04-09.
 */
@Component
public class UsageRatingRoute implements ChumpRoute {

    private static final String RESOURCE = "/v1/usageratings";

    public static final ChumpOperation INSERT_BILLING_CUSTOMER = ChumpOperation.builder()
            .rest(RestOperation.builder()
                    .method(POST)
                    .resource(RESOURCE)
                    .path("/billingcustomers")
                    .requestType(BillingCustomerRequest.class)
                    .build())
            .trx(false)
            .to(Arrays.asList(ChumpOperation.pair(BillingCustomerRequestProcessor.INSTANCE, "sql:{{sql.cdrtool.insertBillingCustomer}}")))
            .build();

    public static final ChumpOperation UPDATE_BILLING_CUSTOMER = ChumpOperation.builder()
            .rest(RestOperation.builder()
                    .method(PUT)
                    .resource(RESOURCE)
                    .path("/billingcustomers")
                    .requestType(BillingCustomerRequest.class)
                    .build())
            .trx(false)
            .to(Arrays.asList(ChumpOperation.pair(BillingCustomerRequestProcessor.INSTANCE, "sql:{{sql.cdrtool.updateBillingCustomer}}")))
            .build();

    private static final class BillingCustomerRequestProcessor implements Processor {

        public static final Processor INSTANCE = new BillingCustomerRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            BillingCustomerRequest request = exchange.getIn().getBody(BillingCustomerRequest.class);

            Map<String, Object> body = new HashMap<>();
            body.put("domain", request.getDomain());
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
}