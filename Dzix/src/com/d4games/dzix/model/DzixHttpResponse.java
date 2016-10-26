package com.d4games.dzix.model;

import java.util.HashMap;
import java.util.Map;

import org.jboss.netty.handler.codec.http.HttpResponseStatus;

import com.strategicgains.restexpress.Response;

public class DzixHttpResponse {
    private int code = 200;
    private String desc = "OK";
    private Map<String, Object> content = new HashMap<String, Object>();

    public DzixHttpResponse(Response response, HttpResponseStatus status) {
        super();
        this.code = status.getCode();
        this.desc = status.getReasonPhrase();
        response.setResponseCode(this.code);
    }

    public DzixHttpResponse(Response response, int code, String desc) {
        super();
        this.code = code;
        this.desc = desc;
        response.setResponseCode(this.code);
    }

    public DzixHttpResponse(int code, String desc) {
        super();
        this.code = code;
        this.desc = desc;
    }

    public DzixHttpResponse() {
        super();
    }

    public int getCode() {
        return code;
    }

    public DzixHttpResponse setCode(int code) {
        this.code = code;
        return this;
    }

    public String getDesc() {
        return desc;
    }

    public DzixHttpResponse setDesc(String desc) {
        this.desc = desc;
        return this;
    }

    public Map<String, Object> getContent() {
        return content;
    }

    public Map<?, ?> getContentChildMap(String child) {
        Object childObj = content.get(child);
        if (childObj instanceof Map<?, ?>) return (Map<?, ?>) childObj;
        return null;
    }

    public DzixHttpResponse setContent(Map<String, Object> content) {
        this.content = content;
        return this;
    }

    public DzixHttpResponse addContent(Map<String, Object> content) {
        this.content.putAll(content);
        return this;
    }

    public DzixHttpResponse setContent(String key, Object value) {
        this.content.put(key, value);
        return this;
    }

    public DzixHttpResponse setContentObject(String key, Object value) {
        this.content.put(key, value);
        return this;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("DzixResponse [code=");
        builder.append(code);
        builder.append(", desc=");
        builder.append(desc);
        builder.append(", content=");
        builder.append(content);
        builder.append("]");
        return builder.toString();
    }

    public boolean isOK() {
        return getCode() == 200;
    }
}
