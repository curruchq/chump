package com.conversant.chump.route.v1.businesspartners.bankaccount;

import com.conversant.chump.common.ChumpOperation;
import com.conversant.chump.common.RestOperation;
import com.conversant.chump.processor.ApiResponseProcessor;
import com.conversant.chump.route.AdempiereRoute;
import com.conversant.chump.route.v1.businesspartners.AbstractBusinessPartnersRoute;
import com.conversant.webservice.*;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;

import static com.conversant.chump.common.RestOperation.HttpMethod.DELETE;
import static com.conversant.chump.util.AdempiereHelper.createLoginRequest;
import static com.conversant.chump.util.Constants.ADEMPIERE_USER_INTALIO;;
import static com.conversant.chump.util.Constants.TYPE_DELETE_BUSINESS_PARTNER_BANK_ACCOUNT;

/**
 * Created by Tate on 25/02/2016.
 */
@Component
public class DeleteBusinessPartnerBankAccountRoute extends AbstractBusinessPartnersRoute {

    public static final ChumpOperation REMOVE = ChumpOperation.builder()
            .trx(false)
            .rest(RestOperation.builder()
                    .method(DELETE)
                    .resource(RESOURCE)
                    .path("/{businessPartnerSearchKey}/bankaccount/{bpBankAccount}")
                    .build())
            .to(Collections.singletonList(ChumpOperation.pair(DeleteBusinessPartnerBankAccountRequestProcessor.INSTANCE, AdempiereRoute.DELETE_BUSINESS_PARTNER_BANK_ACCOUNT.getUri())))
            .build();

    private static final class DeleteBusinessPartnerBankAccountRequestProcessor implements Processor {

        public static final Processor INSTANCE = new DeleteBusinessPartnerBankAccountRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            DeleteBPBankAccountRequest deleteBusinessPartnerBankAccountRequest = new DeleteBPBankAccountRequest();

            deleteBusinessPartnerBankAccountRequest.setLoginRequest(createLoginRequest(exchange, TYPE_DELETE_BUSINESS_PARTNER_BANK_ACCOUNT, ADEMPIERE_USER_INTALIO));
            String bpBankAccount = (String) exchange.getIn().getHeader("bpBankAccount");
            try {
                if(bpBankAccount.length() == 7) {
                    int bankId = Integer.parseInt(bpBankAccount);
                    deleteBusinessPartnerBankAccountRequest.setAccountName("");
                    deleteBusinessPartnerBankAccountRequest.setBpBankAccountId(bankId);
                } else {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException n) {
                deleteBusinessPartnerBankAccountRequest.setAccountName(bpBankAccount);
            }


            exchange.getIn().setBody(deleteBusinessPartnerBankAccountRequest);
        }
    }

}
