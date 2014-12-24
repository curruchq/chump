package com.conversant.chump.exception;

import com.conversant.webservice.StandardResponse;

/**
 * Created by jhill on 20/12/14.
 */
public class FailedStandardResponseException extends Exception {

    private final StandardResponse response;

    public FailedStandardResponseException(StandardResponse response) {
        super(response.getMessage());
        this.response = response;
    }

    public StandardResponse getResponse() {
        return response;
    }

    @Override
    public String toString() {
        return "FailedResponseException{" +
                "message=" + response.getMessage() +
                ", trxName=" + response.getTrxName() +
                '}';
    }
}
