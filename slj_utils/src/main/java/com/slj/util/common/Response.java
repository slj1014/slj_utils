package com.slj.util.common;


import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author Damon
 */
public class Response<T> {
    private String code;
    private String message;
    private T data;

    public Response() {
    }


    public static <T> Response<T> getInstance(String code,String message){
        Response<T> response = new Response();
        response.setCode(code);
        response.setMessage(message);
        return response;
    }


    public static <T> Response<T> getInstance(String code,String message,T data){
        Response<T> response = new Response();
        response.setCode(code);
        response.setMessage(message);
        response.setData(data);
        return response;
    }

    public static <T> Response<T> succeed(T data) {
        Response<T> response = new Response();
        response.setCode(StatusCode.SUCCESS.getCode());
        response.setMessage(StatusCode.SUCCESS.getValue());
        response.setData(data);
        return response;
    }

    public static <T> Response<T> succeed() {
        Response<T> response = new Response();
        response.setCode(StatusCode.SUCCESS.getCode());
        response.setMessage(StatusCode.SUCCESS.getValue());
        return response;
    }

    public static <T> Response<T> succeed(T data, String message) {
        Response<T> response = new Response();
        response.setCode(StatusCode.SUCCESS.getCode());
        response.setMessage(message);
        response.setData(data);
        return response;
    }

    public static Response<String> fail(String message) {
        Response response = new Response();
        response.setCode(StatusCode.FAIL.getCode());
        response.setMessage(message);
        return response;
    }

    public static Response<String> fail() {
        Response response = new Response();
        response.setCode(StatusCode.FAIL.getCode());
        response.setMessage(StatusCode.FAIL.getValue());
        return response;
    }

    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
