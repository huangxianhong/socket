package com.hxh.socket.core;

/**
 * description:
 * 是request还是response
 *
 * @author huangxianhong
 * @version v1.0.0
 * @date Created in 2019/12/31 16:44
 */
public enum FType {
    /**
     * request
     */
    REQUEST((byte) 0, "请求"),
    /**
     * response
     */
    RESPONSE((byte) 1, "返回");

    private byte code;
    private String desc;

    FType(byte code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public byte getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static FType valueOf(byte code) {
        for (FType fType : FType.values()) {
            if (fType.getCode() == code) {
                return fType;
            }
        }
        return null;
    }
}
