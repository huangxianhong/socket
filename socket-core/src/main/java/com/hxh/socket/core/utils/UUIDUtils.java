package com.hxh.socket.core.utils;

import java.util.UUID;

/**
 * description:
 *
 * @author huangxianhong
 * @version v1.0.0
 * @date Created in 2019/12/24 14:39
 */
public class UUIDUtils {
    public UUIDUtils() {
    }

    public static String getId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String getRangeString(int length) {
        return getId().substring(0, length);
    }
}
