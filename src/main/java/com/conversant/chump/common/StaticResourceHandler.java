package com.conversant.chump.common;

import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.springframework.stereotype.Component;

/**
 * Created by jhill on 20/09/15.
 */
@Component(value = "staticResourceHandler")
public class StaticResourceHandler extends ResourceHandler {

    public StaticResourceHandler() {
        super();
        setBaseResource(Resource.newClassPathResource("/static"));
    }
}
