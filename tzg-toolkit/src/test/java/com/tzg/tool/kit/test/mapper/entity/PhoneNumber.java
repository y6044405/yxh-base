package com.tzg.tool.kit.test.mapper.entity;

public class PhoneNumber {
    private int    code;

    private String number;

    public PhoneNumber(int i, String string) {
        this.code=i;
        this.number=string;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
