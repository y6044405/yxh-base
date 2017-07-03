package com.tzg.ex.core.common;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 
 * Filename:    Result.java  
 * Description: 通用交互返回对象   
 * Copyright:   Copyright (c) 2015-2018 All Rights Reserved.
 * Company:     tzg.cn Inc.
 * @author:     heyiwu 
 * @version:    1.0  
 * Create at:   2016年9月26日 下午3:17:17  
 *
 */
public class Result<T> implements Serializable {
    //成功/失败
    private boolean             success;
    //消息
    private Map<String, String> messages;
    //数据
    private T                   data;

    public Result(boolean success) {
        this.success = success;
    }

    public Result(boolean success, Map<String, String> messages) {
        this(success, messages, null);
    }

    public Result(boolean success, T data) {
        this(success, null, data);
    }

    public Result(boolean success, Map<String, String> messages, T data) {
        this.success = success;
        this.messages = messages;
        this.data = data;
    }

    @SuppressWarnings("rawtypes")
    public static Result create(boolean success) {
        return new Result(success);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static Result create(boolean success, Map<String, String> messages) {
        return new Result(success, messages);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static Result create(boolean success, Object data) {
        return new Result(success, data);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static Result create(boolean success, Map<String, String> messages, Object data) {
        return new Result(success, null, data);
    }

    public Map<String, String> getMessages() {
        if (messages == null) {
            messages = new LinkedHashMap<>();
        }
        return messages;
    }
    /**
     * 获取消息
     * @author:  heyiwu 
     * @return
     */
    public String getMsg() {
        if (getMessages().isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (String value : getMessages().values()) {
            builder.append(value + ",");
        }
        return builder.deleteCharAt(builder.length() - 1).toString();
    }

    public void addMessage(String key, String value) {
        getMessages().put(key, value);
    }

    public void addMessage(String key, String value, boolean success) {
        this.addMessage(key, value);
        this.setSuccess(success);
    }

    public void setMessages(Map<String, String> messages) {
        this.messages = messages;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

}
