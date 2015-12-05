package com.conversant.chump.route.v1;

import com.conversant.chump.common.ChumpOperation;
import com.conversant.chump.common.ChumpRoute;
import com.conversant.chump.common.RestOperation;
import com.conversant.chump.model.CreateUserRequest;
import com.conversant.chump.model.UpdateUserRequest;
import com.conversant.chump.processor.ApiResponseProcessor;
import com.conversant.chump.processor.StandardResponseRemover;
import com.conversant.chump.route.AdempiereRoute;
import com.conversant.webservice.DeleteUserRequest;
import com.conversant.webservice.ReadUserRequest;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static com.conversant.chump.common.RestOperation.HttpMethod.*;
import static com.conversant.chump.util.AdempiereHelper.createLoginRequest;
import static com.conversant.chump.util.Constants.*;

@Component
public class UserRoute implements ChumpRoute {

    /**
     * Base resource
     */
    private static final String RESOURCE = "/v1/users";

    public static final ChumpOperation CREATE_USER = ChumpOperation.builder()
            .trx(false)
            .rest(RestOperation.builder()
                    .method(POST)
                    .resource(RESOURCE)
                    .requestType(CreateUserRequest.class)
                    .build())
            .to(Arrays.asList(ChumpOperation.pair(CreateUserRequestProcessor.INSTANCE, AdempiereRoute.CREATE_USER.getUri())))
            .postProcessors(Arrays.asList(ApiResponseProcessor.INSTANCE))
            .build();

    public static final ChumpOperation READ_USER = ChumpOperation.builder()
            .trx(false)
            .rest(RestOperation.builder()
                    .method(GET)
                    .resource(RESOURCE)
                    .path("/{searchKey}")
                    .build())
            .to(Arrays.asList(ChumpOperation.pair(ReadUserRequestProcessor.INSTANCE, AdempiereRoute.READ_USER.getUri())))
            .postProcessors(Arrays.asList(new StandardResponseRemover("user"), ApiResponseProcessor.INSTANCE))
            .build();

    public static final ChumpOperation UPDATE_USER = ChumpOperation.builder()
            .trx(false)
            .rest(RestOperation.builder()
                    .method(PUT)
                    .resource(RESOURCE)
                    .path("/{userId}")
                    .requestType(UpdateUserRequest.class)
                    .build())
            .to(Arrays.asList(ChumpOperation.pair(UpdateUserRequestProcessor.INSTANCE, AdempiereRoute.UPDATE_USER.getUri())))
            .postProcessors(Arrays.asList(ApiResponseProcessor.INSTANCE))
            .build();

    public static final ChumpOperation DELETE_USER = ChumpOperation.builder()
            .trx(false)
            .rest(RestOperation.builder()
                    .method(DELETE)
                    .resource(RESOURCE)
                    .path("/{userId}")
                    .build())
            .to(Arrays.asList(ChumpOperation.pair(DeleteUserRequestProcessor.INSTANCE, AdempiereRoute.DELETE_USER.getUri())))
            .postProcessors(Arrays.asList(ApiResponseProcessor.INSTANCE))
            .build();

    private static final class CreateUserRequestProcessor implements Processor {

        public static final Processor INSTANCE = new CreateUserRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            CreateUserRequest request = exchange.getProperty(CreateUserRequest.class.getName(), CreateUserRequest.class);

            com.conversant.webservice.CreateUserRequest createUserRequest = new com.conversant.webservice.CreateUserRequest();
            createUserRequest.setLoginRequest(createLoginRequest(exchange, TYPE_CREATE_USER, ADEMPIERE_USER_DRUPAL));
            createUserRequest.setSearchKey(request.getSearchKey());
            createUserRequest.setName(request.getName());
            createUserRequest.setPassword(request.getPassword());
            createUserRequest.setEmail(request.getEmail());
            createUserRequest.setPhone(request.getPhone());
            createUserRequest.setMobile(request.getMobile());
            createUserRequest.setBusinessPartnerId(request.getBusinessPartnerId());
            createUserRequest.setBusinessPartnerLocationId(request.getBusinessPartnerLocationId());

            exchange.getIn().setBody(createUserRequest);
        }
    }

    private static final class ReadUserRequestProcessor implements Processor {

        public static final Processor INSTANCE = new ReadUserRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            ReadUserRequest request = new ReadUserRequest();
            request.setLoginRequest(createLoginRequest(exchange, TYPE_READ_USER, ADEMPIERE_USER_DRUPAL));
            request.setSearchKey((String) exchange.getIn().getHeader("searchKey"));
            request.setIsSalesPerson(Boolean.parseBoolean((String) exchange.getIn().getHeader("salesPerson", "false")));

            exchange.getIn().setBody(request);
        }
    }

    private static final class UpdateUserRequestProcessor implements Processor {

        public static final Processor INSTANCE = new UpdateUserRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            UpdateUserRequest request = exchange.getProperty(UpdateUserRequest.class.getName(), UpdateUserRequest.class);

            com.conversant.webservice.UpdateUserRequest updateUserRequest = new com.conversant.webservice.UpdateUserRequest();
            updateUserRequest.setLoginRequest(createLoginRequest(exchange, TYPE_UPDATE_USER, ADEMPIERE_USER_DRUPAL));
            updateUserRequest.setUserId(Integer.parseInt((String) exchange.getIn().getHeader("userId")));
            updateUserRequest.setSearchKey(request.getSearchKey());
            updateUserRequest.setName(request.getName());
            updateUserRequest.setPassword(request.getPassword());
            updateUserRequest.setEmail(request.getEmail());
            updateUserRequest.setPhone(request.getPhone());
            updateUserRequest.setMobile(request.getMobile());

            exchange.getIn().setBody(updateUserRequest);
        }
    }

    private static final class DeleteUserRequestProcessor implements Processor {

        public static final Processor INSTANCE = new DeleteUserRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            DeleteUserRequest request = new DeleteUserRequest();
            request.setLoginRequest(createLoginRequest(exchange, TYPE_DELETE_USER, ADEMPIERE_USER_DRUPAL));

            // TODO: Not implemented in ADempiere. De-activate a user?

            exchange.getIn().setBody(request);
        }
    }
}
