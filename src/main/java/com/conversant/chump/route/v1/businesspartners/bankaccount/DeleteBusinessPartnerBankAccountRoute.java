package com.conversant.chump.route.v1.businesspartners.bankaccount;

import com.conversant.chump.common.ChumpOperation;
import com.conversant.chump.common.RestOperation;
import com.conversant.chump.model.BusinessPartnerBankAccountRequest;
import com.conversant.chump.processor.ApiResponseProcessor;
import com.conversant.chump.route.AdempiereRoute;
import com.conversant.chump.route.v1.businesspartners.AbstractBusinessPartnersRoute;
import com.conversant.webservice.DeleteBPBankAccountRequest;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.util.Arrays;

import static com.conversant.chump.common.RestOperation.HttpMethod.DELETE;
import static com.conversant.chump.util.AdempiereHelper.createLoginRequest;
import static com.conversant.chump.util.Constants.ADEMPIERE_USER_INTALIO;;
import static com.conversant.chump.util.Constants.TYPE_DELETE_BUSINESS_PARTNER_BANK_ACCOUNT;

/**
 * Created by Tate on 25/02/2016.
 */
public class DeleteBusinessPartnerBankAccountRoute extends AbstractBusinessPartnersRoute {

    public static final ChumpOperation DELETE_BUSINESS_PARTNER_BANK_ACCOUNT = ChumpOperation.builder()
            .trx(false)
            .rest(RestOperation.builder()
                    .method(DELETE)
                    .resource(RESOURCE)
                    .path("/{businessPartnerSearchKey}/bankaccount/{bpBankAccountId}")
                    .build())
            .to(Arrays.asList(ChumpOperation.pair(DeleteBusinessPartnerBankAccountRequestProcessor.INSTANCE, AdempiereRoute.DELETE_BUSINESS_PARTNER_BANK_ACCOUNT.getUri())))
            .postProcessors(Arrays.asList(ApiResponseProcessor.INSTANCE))
            .build();

    private static final class DeleteBusinessPartnerBankAccountRequestProcessor implements Processor {

        public static final Processor INSTANCE = new DeleteBusinessPartnerBankAccountRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            DeleteBPBankAccountRequest deleteBusinessPartnerBankAccountRequest = new DeleteBPBankAccountRequest();

            deleteBusinessPartnerBankAccountRequest.setLoginRequest(createLoginRequest(exchange, TYPE_DELETE_BUSINESS_PARTNER_BANK_ACCOUNT, ADEMPIERE_USER_INTALIO));
            deleteBusinessPartnerBankAccountRequest.setBpBankAccountId(Integer.parseInt((String) exchange.getIn().getHeader("bpBankAccountId")));

            exchange.getIn().setBody(deleteBusinessPartnerBankAccountRequest);
        }
    }

}
