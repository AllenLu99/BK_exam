package com.interview.exam.entity.res;

import lombok.Data;

@Data
public class ApiResponse<T> {

    private Error error;
    private T data;

    public ApiResponse(String code, String message, T data) {
        this.error = new Error(code, message);
        this.data = data;
    }

    @Data
    public static class Error {
        private String code;
        private String message;

        public Error(String code, String message) {
            this.code = code;
            this.message = message;
        }
    }
}
