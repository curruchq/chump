package com.conversant.chump.model;

import lombok.Data;

import java.util.Date;

/**
 * Created by jhill on 6/07/15.
 */
@Data
public class EndDateUserPreferenceRequest {

    private String uuid;
    private String attribute;
    private Date dateEnd;
}
