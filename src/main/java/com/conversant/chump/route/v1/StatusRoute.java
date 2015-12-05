package com.conversant.chump.route.v1;

import com.conversant.chump.common.ChumpOperation;
import com.conversant.chump.common.ChumpRoute;
import com.conversant.chump.common.RestOperation;
import com.conversant.chump.model.ApiResponse;
import com.conversant.chump.processor.ApiResponseProcessor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static com.conversant.chump.common.RestOperation.HttpMethod.GET;

/**
 * Created by jhill on 11/05/15.
 */
@Component
public class StatusRoute implements ChumpRoute {

    private static final String RESOURCE = "/v1/status";

    public static final ChumpOperation STATUS = ChumpOperation.builder()
            .rest(RestOperation.builder()
                    .method(GET)
                    .resource(RESOURCE)
                    .build())
            .trx(false)
            .preProcessors(Collections.singletonList(StatusRequestProcessor.INSTANCE))
            .postProcessors(Collections.singletonList(ApiResponseProcessor.INSTANCE))
            .to(Collections.emptyList())
            .build();

    private static final class StatusRequestProcessor implements Processor {

        public static final Processor INSTANCE = new StatusRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            Properties properties = new Properties();
            properties.load(getClass().getClassLoader().getResourceAsStream("git.properties"));
            properties.load(getClass().getClassLoader().getResourceAsStream("build.properties"));

            if (exchange.getIn().getHeader("all") != null) {
                exchange.getIn().setBody(properties);
            }
            else {
                Map<String, Object> filtered = new HashMap<>();
                filtered.put("git.branch", properties.getProperty("git.branch"));
                filtered.put("git.commit.id", properties.getProperty("git.commit.id"));
                filtered.put("git.commit.time", properties.getProperty("git.commit.time"));
                filtered.put("git.build.time", properties.getProperty("git.build.time"));
                filtered.put("build.version", properties.getProperty("build.version"));

                exchange.getIn().setBody(filtered);
            }
        }
    }
}
