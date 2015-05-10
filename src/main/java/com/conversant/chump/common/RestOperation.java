package com.conversant.chump.common;

import lombok.Data;
import lombok.experimental.Builder;

/**
 * Created by jhill on 31/12/14.
 */
@Data
@Builder
public class RestOperation {

    private final HttpMethod method;
    private final String resource;
    private final String path;
    private final Class requestType;
    private final String uri;

    public RestOperation(HttpMethod method, String resource, String path, Class requestType, String uri) {
        this.method = method;
        this.resource = resource;
        this.path = path != null ? path : "";
        this.requestType = requestType;
        this.uri = uri != null ? uri : "direct:" + this.method + this.resource + this.path;
    }

    public enum HttpMethod {
        GET, POST, DELETE, PUT
    }
}
