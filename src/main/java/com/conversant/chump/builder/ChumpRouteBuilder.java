package com.conversant.chump.builder;

import com.conversant.chump.common.ChumpOperation;
import com.conversant.chump.common.ChumpRoute;
import com.conversant.chump.common.RestOperation;
import com.conversant.chump.exception.FailedStandardResponseException;
import com.conversant.chump.model.ApiResponse;
import com.conversant.chump.route.AdempiereRoute;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.TryDefinition;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestDefinition;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.conversant.chump.util.Constants.PROPERTY_API_RESPONSE;

/**
 * Created by jhill on 31/12/14.
 */
@Component
@Slf4j
public class ChumpRouteBuilder extends RouteBuilder {

    public static final String REST_COMPONENT = "jetty";
    public static final String SCHEME = "{{server.scheme:http}}";
    public static final String HOST = "{{server.host:localhost}}";
    public static final String PORT = "{{server.port:9090}}";
    public static final String PATH = "{{server.path:/chump}}";

    @Override
    public void configure() throws Exception {

//        onException(Exception.class)
//                .process(e -> log.warn("ExchangeId: {}", e.getExchangeId()))
//                .process(ExceptionProcessor.INSTANCE)
//                .handled(true);
//                .end();

        // Configure REST
        restConfiguration()
                .component(REST_COMPONENT)
                .bindingMode(RestBindingMode.json)
                .skipBindingOnErrorCode(false)
                .dataFormatProperty("prettyPrint", "true")
                .scheme(SCHEME)
                .host(HOST)
                .port(PORT);

        // Add all ChumpRoutes
        getContext().getRegistry().findByType(ChumpRoute.class).stream().forEach(this::add);
    }

    private void add(ChumpRoute route) {

        List<ChumpOperation> operations = Arrays

                // Stream all fields on instance of ChumpRoute class
                .stream(route.getClass().getDeclaredFields())

                        // Filter out fields which aren't ChumpOperations
                .filter(f -> f.getType() == ChumpOperation.class)

                        // Get field's value (usually static)
                .map(f -> {
                    try {
                        return (ChumpOperation) f.get(null);
                    } catch (IllegalAccessException e) {
                        return null;
                    }
                })

                        // Collect into a list
                .collect(Collectors.toList());

        addRest(operations);
        addRoute(operations);
    }

    private void addRest(List<ChumpOperation> operations) {

        // Keep track of RestDefinitions to reuse when resource matches i.e. POST /v1/number and
        // POST /v1/number/{number}/provision will share RestDefinition
        Map<String, RestDefinition> definitions = new HashMap<>();

        operations.stream()

                // Filter out operations w/o a RestOperation set
                .filter(o -> o.getRest() != null)

                        // Add response processor
                .map(o -> {
                    o.getPostProcessors().add(RestResponseProcessor.INSTANCE);
                    return o;
                })

                        // Get RestOperation
                .map(ChumpOperation::getRest)

                        // Foreach RestOperation add
                .forEach(operation -> addRestOperation(operation, definitions));
    }

    private void addRestOperation(RestOperation operation, Map<String, RestDefinition> definitions) {

        RestDefinition def = definitions.get(operation.getResource());
        if (def == null) {
            def = rest(PATH + operation.getResource());
            definitions.put(operation.getResource(), def);
        }

        switch (operation.getMethod()) {
            case GET:
                def = def.get(operation.getPath());
                break;
            case POST:
                def = def.post(operation.getPath());
                break;
            default:
                throw new UnsupportedOperationException("Unsupported HTTP method[" + operation.getMethod() + "]");
        }

        def.type(operation.getRequestType());
        def.to(operation.getUri());
    }

    private void addRoute(List<ChumpOperation> operations) {

        operations.forEach(operation -> {

            RouteDefinition route = from(operation.getUri());
//            route.onException(Exception.class).handled(true).process(ExceptionProcessor.INSTANCE).process(e -> log.error("Caught on route: {}", e.getExchangeId()));

            route.process(RequestProcessor.INSTANCE);
            operation.getPreProcessors().forEach(route::process);

            if (operation.getTrx()) {
                // TODO: Should this go inside try block? Prevent rollback if not created? Exception on this call handled how?
                route.to(AdempiereRoute.CREATE_TRX.getUri());

                // Try
                TryDefinition doTry = route.doTry();
                operation.getTo().forEach(pair -> {
                    if (pair.getProcessor() != null)
                        doTry.process(pair.getProcessor());
                    doTry.to(pair.getTo());
                });
                doTry.to(AdempiereRoute.COMMIT_TRX.getUri());

                // Catch
                doTry.doCatch(Exception.class);
                doTry.process(ExceptionProcessor.INSTANCE);
                doTry.to(AdempiereRoute.ROLLBACK_TRX.getUri());

                // Finally
                doTry.doFinally();
                operation.getPostProcessors().forEach(doTry::process);

//                doTry.end();
            } else {
//                route.errorHandler(noErrorHandler());
//                route.onException(Exception.class).handled(true).process(ExceptionProcessor.INSTANCE).process(e -> log.error("Caught on route: {}", e.getExchangeId()));
                operation.getTo().forEach(pair -> {
                    if (pair.getProcessor() != null)
                        route.process(pair.getProcessor());
                    route.to(pair.getTo());
                });
                operation.getPostProcessors().forEach(route::process);
            }
        });
    }

    private static final class RequestProcessor implements Processor {

        public static final Processor INSTANCE = new RequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {
            Object body = exchange.getIn().getBody();
            exchange.setProperty(body.getClass().getName(), body);
        }
    }

    private static final class RestResponseProcessor implements Processor {

        public static final Processor INSTANCE = new RestResponseProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {
            ApiResponse response = exchange.getProperty(PROPERTY_API_RESPONSE, ApiResponse.class);
            if (response == null) {
                response = ApiResponse.success();
            }
            exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, response.getCode());
            exchange.getOut().setBody(response);
        }
    }

    private static final class ExceptionProcessor implements Processor {

        public static final Processor INSTANCE = new ExceptionProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            Exception cause = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);

            // TODO: Improve
            int code = cause instanceof FailedStandardResponseException ? ApiResponse.BAD_REQUEST : ApiResponse.ERROR;
            StringBuilder message = new StringBuilder(cause.getMessage());
            if (cause.getCause() != null)
                message.append(" - ").append(cause.getCause().getMessage());

            ApiResponse response = ApiResponse.builder().code(code).message(message.toString()).build();
            exchange.setProperty(PROPERTY_API_RESPONSE, response);
        }
    }
}
