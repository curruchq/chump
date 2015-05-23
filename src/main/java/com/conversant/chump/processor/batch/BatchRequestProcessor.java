package com.conversant.chump.processor.batch;

import com.conversant.chump.model.BatchRequest;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

/**
 * Created by jhill on 23/05/15.
 */
public final class BatchRequestProcessor implements Processor {

    public static final Processor INSTANCE = new BatchRequestProcessor();

    @Override
    public void process(Exchange exchange) throws Exception {
        BatchRequest batch = exchange.getProperty(BatchRequest.class.getName(), BatchRequest.class);
        if (batch != null) {
            exchange.getIn().setBody(batch.getRequests());
        }
    }
}
