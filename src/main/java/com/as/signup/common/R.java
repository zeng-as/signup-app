package com.as.signup.common;

import lombok.Data;
import static com.as.signup.common.CommonEnums.*;

@Data
public class R<T> {
    private String code;
    private T data;
    private String desc;

    private R() {
    }

    private R(String code, T data, String desc) {
        this.code = code;
        this.data = data;
        this.desc = desc;
    }
    public static <T> R<T> ok() {
        return new R<T>(ResCode.OK.getCode(), (T)null, ResCode.OK.getDesc());
    }

    public static <T> R<T> ok(T data) {
        return new R<T>(ResCode.OK.getCode(), data, ResCode.OK.getDesc());
    }

    public static <T> R<T> fail() {
        return new R<T>(ResCode.EX_SYSTEM.getCode(), (T)null, ResCode.EX_SYSTEM.getDesc());
    }

    public static <T> R<T> fail(T data) {
        return new R<T>(ResCode.EX_SYSTEM.getCode(), data, ResCode.EX_SYSTEM.getDesc());
    }

    public static <T> R<T> getInstance(ResCode resCode) {
        return new R<T>(resCode.getCode(), (T)null, resCode.getDesc());
    }

    public static <T> R<T> getInstance(ResCode resCode, T data) {
        return new R<T>(resCode.getCode(), data, resCode.getDesc());
    }

    public static <T> R<T> getInstance(ResCode resCode, T data, String msg) {
        return new R<T>(resCode.getCode(), data, msg);
    }
}
