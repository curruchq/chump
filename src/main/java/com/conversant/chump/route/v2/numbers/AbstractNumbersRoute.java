package com.conversant.chump.route.v2.numbers;

import com.conversant.chump.common.ChumpRoute;
import com.conversant.chump.model.BatchRequest;
import com.conversant.chump.model.CreateNumberRequest;
import com.conversant.chump.model.NumberRequest;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.beanutils.BeanUtils;

import java.util.ArrayList;

/**
 * Created by jhill on 23/05/15.
 */
public abstract class AbstractNumbersRoute implements ChumpRoute {

    /**
     * Base resource
     */
    protected static final String RESOURCE = "/v2/numbers";

    /**
     * Number request processor
     */
    protected static final class NumberRequestProcessor implements Processor {

        public static final Processor INSTANCE = new NumberRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            NumberRequest batch = exchange.getProperty(BatchRequest.class.getName(), NumberRequest.class);
            if (batch != null && batch.getRequests() != null) {

                // Batch request - look for first request which doesn't have number set
                for (NumberRequest numberRequest : batch.getRequests()) {
                    if (numberRequest.getNumber() == null) {
                        numberRequest.setNumber((String) exchange.getIn().getHeader("number"));
                        break;
                    }
                }
            }
            else {
                NumberRequest request = exchange.getProperty(NumberRequest.class.getName(), NumberRequest.class);

                // Single request with multiple numbers - create batch request
                if (request != null && request.getNumbers() != null && request.getNumbers().size() > 0) {

                    batch = new NumberRequest();
                    batch.setRequests(new ArrayList<>());

                    // Create children
                    for (String number : request.getNumbers()) {
                        NumberRequest child = (NumberRequest) BeanUtils.cloneBean(request);
                        child.setNumber(number);
                        child.setNumbers(null);
                        batch.getRequests().add(child);
                    }

                    // Clear single request and set batch in exchange for BatchRequestProcessor
                    exchange.setProperty(NumberRequest.class.getName(), null);
                    exchange.setProperty(BatchRequest.class.getName(), batch);
                }
                // Single request - set if isn't set (i.e. came on path)
                else if (request != null && request.getNumber() == null) {
                    request.setNumber((String) exchange.getIn().getHeader("number"));
                }
            }
        }
    }

    /**
     * Create number request processor
     */
    protected static final class CreateNumberRequestProcessor implements Processor {

        public static final Processor INSTANCE = new CreateNumberRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            CreateNumberRequest batch = exchange.getProperty(BatchRequest.class.getName(), CreateNumberRequest.class);
            if (batch != null && batch.getRequests() != null) {

                // Batch request - look for first request which doesn't have number set
                for (CreateNumberRequest createNumberRequest : batch.getRequests()) {
                    if (createNumberRequest.getNumber() == null) {
                        createNumberRequest.setNumber((String) exchange.getIn().getHeader("number"));
                        break;
                    }
                }
            } else {
                CreateNumberRequest request = exchange.getProperty(CreateNumberRequest.class.getName(), CreateNumberRequest.class);

                // Single request with multiple numbers - create batch request
                if (request != null && request.getNumbers() != null && request.getNumbers().size() > 0) {

                    batch = new CreateNumberRequest();
                    batch.setRequests(new ArrayList<>());

                    // Create children
                    for (String number : request.getNumbers()) {
                        CreateNumberRequest child = (CreateNumberRequest) BeanUtils.cloneBean(request);
                        child.setNumber(number);
                        child.setNumbers(null);
                        batch.getRequests().add(child);
                    }

                    // Clear single request and set batch in exchange for BatchRequestProcessor
                    exchange.setProperty(NumberRequest.class.getName(), null);
                    exchange.setProperty(BatchRequest.class.getName(), batch);
                }
                // Single request - set if isn't set (i.e. came on path)
                else if (request != null && request.getNumber() == null) {
                    request.setNumber((String) exchange.getIn().getHeader("number"));
                }
            }
        }
    }
}
