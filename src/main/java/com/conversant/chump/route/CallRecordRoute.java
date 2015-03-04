package com.conversant.chump.route;

import com.conversant.chump.common.ChumpOperation;
import com.conversant.chump.common.ChumpRoute;
import com.conversant.chump.common.RestOperation;
import com.conversant.chump.model.SelectCallRecordRequest;
import com.conversant.chump.processor.ApiResponseProcessor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.sql.SqlConstants;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Created by jhill on 19/01/15.
 */
@Component
public class CallRecordRoute implements ChumpRoute {

    private static final String RESOURCE = "/v1/callrecords";

    public static final ChumpOperation SELECT = ChumpOperation.builder()
            .rest(RestOperation.builder()
                    .path("/search")
                    .resource(RESOURCE)
                    .method(RestOperation.HttpMethod.POST)
                    .requestType(SelectCallRecordRequest.class)
                    .build())
            .trx(false)
            .to(Arrays.asList(ChumpOperation.pair(SelectRequestProcessor.INSTANCE, "sql:{{sql.radius.selectCallRecord}}")))
            .postProcessors(Arrays.asList(ApiResponseProcessor.INSTANCE))
            .build();

    private static final class SelectRequestProcessor implements Processor {

        public static final Processor INSTANCE = new SelectRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            SelectCallRecordRequest request = exchange.getIn().getBody(SelectCallRecordRequest.class);

            String ids = request.getIds().stream().collect(Collectors.joining("','"));
            exchange.getIn().setHeader(SqlConstants.SQL_QUERY, "SELECT * FROM radius.radacct WHERE AcctSessionId IN ('" + ids + "')");
        }
    }
}
