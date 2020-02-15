
package com.hxh.socket.core;

/**
 * description:
 * 请求是否需要返回值
 *
 * @author huangxianhong
 * @version v1.0.0
 * @date Created in 2019/12/31 16:44
 */
public enum OneWayType {
    /**
     * 需要返回值的请求
     */
    NO((byte) 0, "需要返回值"),
    /**
     * 不需要返回值的请求
     */
    YES((byte) 1, "不需要返回值");

    private byte code;
    private String desc;

    OneWayType(byte code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static OneWayType valueOf(byte code) {
        for (OneWayType oneWayType : OneWayType.values()) {
            if (oneWayType.getCode() == code) {
                return oneWayType;
            }
        }
        return null;
    }

    public byte getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
