package com.hxh.socket.core.utils;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * description:
 * 该段代码来自RocketMQ
 *
 * @author huangxianhong
 * @version v1.0.0
 * @date Created in 2019/12/26 11:26
 */
public class MapUtils {

    private static final Charset CHARSET_UTF8 = Charset.forName("UTF-8");

    public static Map<String, String> deserialize(byte[] bytes) {
        HashMap<String, String> map = new HashMap(16);
        if (bytes == null || bytes.length <= 0) {
            return map;
        }


        ByteBuffer byteBuffer = BufferUtil.wrap(bytes);

        short keySize;
        byte[] keyContent;
        int valSize;
        byte[] valContent;
        while (byteBuffer.hasRemaining()) {
            keySize = byteBuffer.getShort();
            keyContent = new byte[keySize];
            byteBuffer.get(keyContent);

            valSize = byteBuffer.getInt();
            valContent = new byte[valSize];
            byteBuffer.get(valContent);

            map.put(new String(keyContent, CHARSET_UTF8), new String(valContent, CHARSET_UTF8));
        }
        return map;
    }


    public static byte[] serialize(Map<String, String> map) {
        // keySize+key+valSize+val
        if (null == map || map.isEmpty()) {
            return null;
        }


        int totalLength = 0;
        int kvLength;
        Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            if (entry.getKey() != null && entry.getValue() != null) {
                kvLength =
                        // keySize + Key
                        2 + entry.getKey().getBytes(CHARSET_UTF8).length
                                // valSize + val
                                + 4 + entry.getValue().getBytes(CHARSET_UTF8).length;
                totalLength += kvLength;
            }
        }

        ByteBuffer content = BufferUtil.allocate(totalLength);
        byte[] key;
        byte[] val;
        it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            if (entry.getKey() != null && entry.getValue() != null) {
                key = entry.getKey().getBytes(CHARSET_UTF8);
                val = entry.getValue().getBytes(CHARSET_UTF8);

                content.putShort((short) key.length);
                content.put(key);

                content.putInt(val.length);
                content.put(val);
            }
        }

        return content.array();
    }

    public static void main(String[] args) {
        Map<String, String> map = new HashMap<>(1);
        map.put("userName", "hxh");
        byte[] bytes = MapUtils.serialize(map);
        System.out.println(bytes);
    }


}
