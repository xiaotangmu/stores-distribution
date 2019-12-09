package com.schooltraining.storesdistribution.entities;

import java.util.Map;

public class Msg {

    private String code;//20000: 成功，60204：失败

    private Object extend;//返回的数据

    private Msg() {
    }

    //成功
    public static Msg success(Object obj) {
        Msg msg = new Msg();
        msg.setCode("20000");
        msg.setExtend(obj);
        return msg;
    }

    //失败
    public static Msg fail(Object obj) {
        Msg msg = new Msg();
        msg.setCode("60204");
        msg.setExtend(obj);
        return msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Object getExtend() {
        return extend;
    }

    public void setExtend(Object extend) {
        this.extend = extend;
    }
}
