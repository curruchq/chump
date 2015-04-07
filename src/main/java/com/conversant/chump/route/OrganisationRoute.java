package com.conversant.chump.route;

import com.conversant.chump.common.ChumpOperation;
import com.conversant.chump.common.ChumpRoute;
import com.conversant.chump.common.RestOperation;
import com.conversant.chump.model.ReadOrganisationBySearchKeyRequest;
import com.conversant.chump.processor.ApiResponseProcessor;
import com.conversant.chump.processor.StandardResponseRemover;
import com.conversant.chump.util.AdempiereHelper;
import com.conversant.chump.util.Constants;
import com.conversant.webservice.ReadOrganizationRequest;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static com.conversant.chump.common.RestOperation.HttpMethod.GET;

/**
 * Created by Saren Currie on 2015-04-02.
 */
@Component
public class OrganisationRoute implements ChumpRoute {
    private static final String RESOURCE = "/v1/organisations";

    public static final ChumpOperation READ = ChumpOperation.builder()
            .rest(RestOperation.builder()
                    .resource(RESOURCE)
                    .path("/{organisationId}")
                    .method(GET)
                    .requestType(ReadOrganisationBySearchKeyRequest.class)
                    .build()
            )
            .trx(false)
            .to(Arrays.asList(
                    ChumpOperation.pair(ReadOrganisationProcessor.INSTANCE, AdempiereRoute.READ_ORGANISATION.getUri())
            ))
            .postProcessors(Arrays.asList(
                    new StandardResponseRemover("organization"), ApiResponseProcessor.INSTANCE
            ))
            .build();

    private static final class ReadOrganisationProcessor implements Processor {
        public static final Processor INSTANCE = new ReadOrganisationProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {
            ReadOrganizationRequest request = new ReadOrganizationRequest();

            request.setLoginRequest(AdempiereHelper.createLoginRequest(exchange, Constants.TYPE_READ_ORGANISATION, Constants.ADEMPIERE_USER_INTALIO));
            request.setOrgId(Integer.parseInt((String) exchange.getIn().getHeader("organisationId")));

            exchange.getIn().setBody(request);
        }
    }
}
