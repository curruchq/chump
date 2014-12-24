package com.conversant.chump.util;

import com.conversant.chump.common.ChumpOperation;
import com.conversant.webservice.LoginRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jhill on 24/12/14.
 */
@Slf4j
public class AdempiereHelper {

    public static LoginRequest createLoginRequest(Exchange exchange, String type) {
        LoginRequest request = new LoginRequest();
//        request.setUsername(exchange.getProperty(PROPERTY_USERNAME, String.class));
//        request.setPassword(exchange.getProperty(PROPERTY_PASSWORD, String.class));
        request.setUsername("IntalioUser");
        request.setPassword("dJw%U#s5");
        request.setTrxName(exchange.getProperty(Constants.PROPERTY_TRX_NAME, String.class));
        request.setType(type);

        return request;
    }

    public static Object getRequestBodyProperty(Exchange exchange) {
        return getRequestBodyProperties(exchange).get(exchange.getProperty("CamelToEndpoint"));
    }

    public static Object removeRequestBodyProperty(Exchange exchange) {
        return getRequestBodyProperties(exchange).remove(exchange.getProperty("CamelToEndpoint"));
    }

    public static void setRequestBodyProperty(Exchange exchange, ChumpOperation operation, Object request) {
        getRequestBodyProperties(exchange).put(operation.getUri(), request);
    }

    public static Map<String, Object> getRequestBodyProperties(Exchange exchange) {
        Map properties = exchange.getProperty("RequestBodyProperties", Map.class);
        if (properties == null) {
            properties = new HashMap<>();
            exchange.setProperty("RequestBodyProperties", properties);
        }
        return properties;
    }
}
