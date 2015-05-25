package com.conversant.chump.route.v1.businesspartners;

import com.conversant.chump.common.ChumpRoute;
import com.conversant.chump.model.BusinessPartnerRequest;
import com.conversant.webservice.ReadBusinessPartnerBySearchKeyRequest;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.util.StringUtils;

import static com.conversant.chump.util.AdempiereHelper.createLoginRequest;
import static com.conversant.chump.util.Constants.ADEMPIERE_USER_DRUPAL;
import static com.conversant.chump.util.Constants.TYPE_READ_BUSINESS_PARTNER;

/**
 * Created by jhill on 20/06/15.
 */
public abstract class AbstractBusinessPartnersRoute implements ChumpRoute {

    /**
     * Base resource
     */
    protected static final String RESOURCE = "/v1/businesspartners";

    /**
     * Business partner request processor
     *
     * Populates BusinessPartnerRequest's search key from header else it will get lost when calling create trx
     */
    protected static final class BusinessPartnerRequestProcessor implements Processor {

        public static final Processor INSTANCE = new BusinessPartnerRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            BusinessPartnerRequest businessPartnerRequest = exchange.getIn().getBody(BusinessPartnerRequest.class);
            if (businessPartnerRequest.getSearchKey() == null)
                businessPartnerRequest.setSearchKey((String) exchange.getIn().getHeader("businessPartnerSearchKey"));
        }
    }

    /**
     * Read business partner request processor
     */
    protected static final class ReadBusinessPartnerBySearchKeyRequestProcessor implements Processor {

        public static final Processor INSTANCE = new ReadBusinessPartnerBySearchKeyRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            ReadBusinessPartnerBySearchKeyRequest request = new ReadBusinessPartnerBySearchKeyRequest();
            request.setLoginRequest(createLoginRequest(exchange, TYPE_READ_BUSINESS_PARTNER, ADEMPIERE_USER_DRUPAL));

            // If BusinessPartnerRequest supplied use it's search key else attempt to get from header
            BusinessPartnerRequest businessPartnerRequest = exchange.getProperty(BusinessPartnerRequest.class.getName(), BusinessPartnerRequest.class);
            if (businessPartnerRequest != null && !StringUtils.isEmpty(businessPartnerRequest.getSearchKey())) {
                request.setSearchKey(businessPartnerRequest.getSearchKey());
            }
            else {
                request.setSearchKey((String) exchange.getIn().getHeader("businessPartnerSearchKey"));
            }

            exchange.getIn().setBody(request);
        }
    }
}
