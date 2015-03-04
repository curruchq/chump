package com.conversant.chump.processor;

import com.conversant.chump.model.ApiResponse;
import com.conversant.webservice.StandardResponse;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.beanutils.PropertyUtils;

import java.util.Map;

/**
 * Removes the StandardResponse object from a message body by replacing it with the value with the given key.
 * <p>
 * Created by Saren Currie on 15-01-30.
 */
public class StandardResponseRemover implements Processor {

    private static final String STANDARD_RESPONSE = "standardResponse";

    private final String key;

    public StandardResponseRemover(String key) {
        this.key = key;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        Map<String, Object> m = PropertyUtils.describe(exchange.getIn().getBody());
        if (m.containsKey(STANDARD_RESPONSE) && m.get(STANDARD_RESPONSE) instanceof StandardResponse) {
            StandardResponse standardResponse = (StandardResponse) m.get(STANDARD_RESPONSE);
            if (standardResponse.isSuccess()) {
                if (m.containsKey(key)) {
                    exchange.getIn().setBody(m.get(key));
                }
                else {
                    throw new RuntimeException("Message does not contain value with key: " + key);
                }
            }
            else {
                exchange.getIn().setBody(ApiResponse.error());
            }
        }
        else {
            throw new RuntimeException("Message has no standard response");
        }
    }
}
