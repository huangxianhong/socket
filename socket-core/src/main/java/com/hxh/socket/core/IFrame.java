package com.hxh.socket.core;

/**
 * description:
 *
 * @author huangxianhong
 * @version v1.0.0
 * @date Created in 2019/12/31 9:37
 */
public interface IFrame {
    /**
     * FrameId
     * @return FrameId
     */
    String getFrameId();


    /**
     * 当发送数据时，看数据是否写完
     * @return 剩余字节数
     */
    int remainReading();


    /**
     * 请求是否需要返回值
     * @return oneWayType
     */
    OneWayType getOneWayType();


    /**
     * 请求是否需要返回值
     * @param oneWayType oneWayType
     */
    void setOneWayType(OneWayType oneWayType);

    /**
     * Request OR Response
     * @return 请求还是响应
     */
    FType getType();
}
