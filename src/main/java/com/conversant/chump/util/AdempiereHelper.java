package com.conversant.chump.util;

import com.conversant.chump.common.ChumpOperation;
import com.conversant.webservice.LoginRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;

import java.util.HashMap;
import java.util.Map;

import static com.conversant.chump.util.Constants.*;

/**
 * Created by jhill on 24/12/14.
 */
@Slf4j
public class AdempiereHelper {

    public static LoginRequest createLoginRequest(Exchange exchange, String type, String userName) {
        LoginRequest request = new LoginRequest();
//        request.setUsername(exchange.getProperty(PROPERTY_USERNAME, String.class));
//        request.setPassword(exchange.getProperty(PROPERTY_PASSWORD, String.class));
        switch (userName) {
            case ADEMPIERE_USER_INTALIO:
                request.setUsername(userName);
                request.setPassword(ADEMPIERE_PASS_INTALIO);
                break;
            case ADEMPIERE_USER_DRUPAL:
                request.setUsername(userName);
                request.setPassword(ADEMPIERE_PASS_DRUPAL);
                break;
            default:
                request.setUsername(ADEMPIERE_USER_DEFAULT);
                request.setUsername(ADEMPIERE_PASS_DEFAULT);
                break;
        }
        request.setTrxName(exchange.getProperty(Constants.PROPERTY_TRX_NAME, String.class));
        request.setType(type);

        return request;
    }

    public static LoginRequest createLoginRequest(Exchange exchange, String type) {
        return createLoginRequest(exchange, type, ADEMPIERE_USER_DEFAULT);
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
