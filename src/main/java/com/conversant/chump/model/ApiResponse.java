package com.conversant.chump.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Builder;

import java.util.List;

/**
 * Created by jhill on 28/12/14.
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse {

    public static final int SUCCESS = 200;
    public static final int BAD_REQUEST = 400;
    public static final int ERROR = 500;

    private final int code;
    private String message;
    private Object data;
    private List<ApiResponse> responses;

    public static ApiResponse success() {
        return ApiResponse.builder().code(ApiResponse.SUCCESS).message("Success").build();
    }

    public static ApiResponse badRequest() {
        return ApiResponse.builder().code(ApiResponse.BAD_REQUEST).message("Failed").build();
    }

    public static ApiResponse error() {
        return ApiResponse.builder().code(ApiResponse.ERROR).message("Error").build();
    }
}
