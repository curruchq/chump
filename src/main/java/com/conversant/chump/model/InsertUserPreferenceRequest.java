package com.conversant.chump.model;

import lombok.Data;

import java.util.Date;

/**
 * Created by jhill on 4/01/15.
 */
@Data
public class InsertUserPreferenceRequest {

    private String uuid;
    private String username;
    private String domain;
    private String attribute;
    private String value;
    private String type;
    private Date modified;
    private Date dateStart;
    private Date dateEnd;
    private String subscriberId;
}
