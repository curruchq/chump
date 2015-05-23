package com.conversant.chump.route.v1.numbers;

import com.conversant.chump.common.ChumpRoute;
import com.conversant.chump.model.NumberRequest;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

/**
 * Created by jhill on 23/05/15.
 */
public abstract class AbstractNumbersRoute implements ChumpRoute {

    /**
     * Base resource
     */
    protected static final String RESOURCE = "/v1/numbers";

    /**
     * Number request processor
     */
    protected static final class NumberRequestProcessor implements Processor {

        public static final Processor INSTANCE = new NumberRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            // TODO: How get into request POJO automatically? Generic processor?
            NumberRequest request = exchange.getProperty(NumberRequest.class.getName(), NumberRequest.class);
            if (request != null && request.getNumber() == null)
                request.setNumber((String) exchange.getIn().getHeader("number"));
        }
    }
}
