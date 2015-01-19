package com.conversant.chump.model;

import lombok.Data;

import java.util.List;

/**
 * Created by jhill on 19/01/15.
 */
@Data
public class SelectCallRecordRequest {

    private List<String> ids;
}
