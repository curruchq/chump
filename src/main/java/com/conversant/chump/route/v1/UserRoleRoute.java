package com.conversant.chump.route.v1;

import static com.conversant.chump.common.RestOperation.HttpMethod.POST;
import static com.conversant.chump.common.RestOperation.HttpMethod.GET;
import static com.conversant.chump.common.RestOperation.HttpMethod.DELETE;
import static com.conversant.chump.util.AdempiereHelper.createLoginRequest;
import static com.conversant.chump.util.Constants.ADEMPIERE_USER_DRUPAL;
import static com.conversant.chump.util.Constants.TYPE_READ_USER_ROLE;
import static com.conversant.chump.util.Constants.TYPE_CREATE_USER_ROLE;
import static com.conversant.chump.util.Constants.TYPE_DELETE_USER_ROLE;

import java.util.Arrays;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import com.conversant.chump.common.ChumpOperation;
import com.conversant.chump.common.ChumpRoute;
import com.conversant.chump.common.RestOperation;
import com.conversant.chump.model.CreateUserRoleRequest;
import com.conversant.chump.processor.ApiResponseProcessor;
import com.conversant.chump.route.AdempiereRoute;
import com.conversant.webservice.DeleteUserRoleRequest;
import com.conversant.webservice.ReadUserRoleRequest;

@Component
public class UserRoleRoute implements ChumpRoute{
	
	private static final String RESOURCE = "/v1/users";

	public static final ChumpOperation READ = ChumpOperation.builder()
			.trx(false)
			.rest(RestOperation.builder()
					.resource(RESOURCE)
					.method(GET)
					.path("/{userId}/roles/{roleId}")
					.build())
			.to(Arrays.asList(ChumpOperation.pair(ReadUserRoleRequestProcessor.INSTANCE,AdempiereRoute.READ_USER_ROLE.getUri())))
			.postProcessors(Arrays.asList(ApiResponseProcessor.INSTANCE))
			.build();

	public static final ChumpOperation CREATE = ChumpOperation
			.builder()
			.trx(false)
			.rest(RestOperation.builder()
					.method(POST)
					.path("/{userId}/roles")
					.resource(RESOURCE)
					.requestType(CreateUserRoleRequest.class)
					.build())
			.to(Arrays.asList(ChumpOperation.pair(CreateUserRoleRequestProcessor.INSTANCE,AdempiereRoute.CREATE_USER_ROLE.getUri())))
			.postProcessors(Arrays.asList(ApiResponseProcessor.INSTANCE))
			.build();
	public static final ChumpOperation DELETE_ROLE = ChumpOperation
			.builder()
			.trx(false)
			.rest(RestOperation.builder()
					.method(DELETE)
					.path("/{userId}/roles/{roleId}")
					.resource(RESOURCE)
					.build())
			.to(Arrays.asList(ChumpOperation.pair(DeleteUserRoleRequestProcessor.INSTANCE,AdempiereRoute.DELETE_USER_ROLE.getUri())))
			.postProcessors(Arrays.asList(ApiResponseProcessor.INSTANCE))
			.build();
    
	private static final class ReadUserRoleRequestProcessor implements Processor {

		public static final Processor INSTANCE = new ReadUserRoleRequestProcessor();

		@Override
		public void process(Exchange exchange) throws Exception {

			ReadUserRoleRequest userRoleRequest = new ReadUserRoleRequest();
			userRoleRequest.setLoginRequest(createLoginRequest(exchange,TYPE_READ_USER_ROLE, ADEMPIERE_USER_DRUPAL));
			userRoleRequest.setUserId(Integer.parseInt((String) exchange.getIn().getHeader("userId")));
			userRoleRequest.setRoleId(Integer.parseInt((String) exchange.getIn().getHeader("roleId")));

			exchange.getIn().setBody(userRoleRequest);
		}
	}

	private static final class CreateUserRoleRequestProcessor implements Processor {

		public static final Processor INSTANCE = new CreateUserRoleRequestProcessor();

		@Override
		public void process(Exchange exchange) throws Exception {

			CreateUserRoleRequest request = exchange.getProperty(CreateUserRoleRequest.class.getName(), CreateUserRoleRequest.class);

			com.conversant.webservice.CreateUserRoleRequest createUserRoleRequest = new com.conversant.webservice.CreateUserRoleRequest();
			createUserRoleRequest.setLoginRequest(createLoginRequest(exchange,TYPE_CREATE_USER_ROLE, ADEMPIERE_USER_DRUPAL));
			createUserRoleRequest.setUserId(Integer.parseInt((String) exchange.getIn().getHeader("userId")));
			createUserRoleRequest.setRoleId(request.getRoleId());
			
			exchange.getIn().setBody(createUserRoleRequest);
		}
	}

	private static final class DeleteUserRoleRequestProcessor implements Processor {

		public static final Processor INSTANCE = new DeleteUserRoleRequestProcessor();

		@Override
		public void process(Exchange exchange) throws Exception {

			DeleteUserRoleRequest createUserRoleRequest = new DeleteUserRoleRequest();
			createUserRoleRequest.setLoginRequest(createLoginRequest(exchange,TYPE_DELETE_USER_ROLE, ADEMPIERE_USER_DRUPAL));
			createUserRoleRequest.setUserId(Integer.parseInt((String) exchange.getIn().getHeader("userId")));
			createUserRoleRequest.setRoleId(Integer.parseInt((String) exchange.getIn().getHeader("roleId")));
			
			exchange.getIn().setBody(createUserRoleRequest);
		}
	}
}
