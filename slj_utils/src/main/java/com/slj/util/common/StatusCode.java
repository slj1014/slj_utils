package com.slj.util.common;

public enum StatusCode {
    SUCCESS("2000","SUCCESS"),
    FORBIDDEN("40001","FORBIDDEN"),
    FAIL("5000","FAIL");

    private String code;
    private String value;

    StatusCode(String code, String value) {
        this.code = code;
        this.value = value;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
