package com.d4games.dzix.model;

public class DzixHttpRequest<T> {
    private String qid = "1";
    private String src = "playerKey_or_serverIp";
    private T bodyParams;

    public String getQid() {
        return qid;
    }

    public void setQid(String qid) {
        this.qid = qid;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public T getBodyParams() {
        return bodyParams;
    }

    public void setBodyParams(T bodyParams) {
        this.bodyParams = bodyParams;
    }

    public DzixHttpRequest(String qid, String src, T bodyParams) {
        super();
        this.qid = qid;
        this.src = src;
        this.bodyParams = bodyParams;
    }

    public DzixHttpRequest(String qid, String src) {
        super();
        this.qid = qid;
        this.src = src;
    }

    public DzixHttpRequest() {
        super();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("DzixHttpRequest [qid=");
        builder.append(qid);
        builder.append(", src=");
        builder.append(src);
        builder.append(", bodyParams=");
        builder.append(bodyParams);
        builder.append("]");
        return builder.toString();
    }

}
