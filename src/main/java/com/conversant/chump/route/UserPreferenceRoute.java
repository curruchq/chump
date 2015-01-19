package com.conversant.chump.route;

import com.conversant.chump.common.ChumpOperation;
import com.conversant.chump.common.ChumpRoute;
import com.conversant.chump.model.InsertUserPreferenceRequest;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jhill on 4/01/15.
 */
@Component
public class UserPreferenceRoute implements ChumpRoute {

    public static final ChumpOperation INSERT = ChumpOperation.builder()
            .uri("direct://insertUserPreference")
            .trx(false)
            .to(Arrays.asList(ChumpOperation.pair(InsertRequestProcessor.INSTANCE, "sql:{{sql.ser.insertUserPreference}}")))
            .build();

    private static final class InsertRequestProcessor implements Processor {

        public static final Processor INSTANCE = new InsertRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            InsertUserPreferenceRequest request = exchange.getIn().getBody(InsertUserPreferenceRequest.class);

            Map<String, Object> body = new HashMap<>();
            body.put("uuid", request.getUuid());
            body.put("username", request.getUsername());
            body.put("domain", request.getDomain());
            body.put("attribute", request.getAttribute());
            body.put("value", request.getValue());
            body.put("type", request.getType());
            body.put("modified", request.getModified());
            body.put("date_start", request.getDateStart());
            body.put("date_end", request.getDateEnd());
            body.put("subscriber_id", request.getSubscriberId());

            exchange.getIn().setBody(body);
        }
    }
}

