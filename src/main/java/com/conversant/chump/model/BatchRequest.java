package com.conversant.chump.model;

import lombok.Data;

import java.util.List;

/**
 * Created by jhill on 23/05/15.
 */
@Data
public abstract class BatchRequest<T> {
    private List<T> requests;
}
