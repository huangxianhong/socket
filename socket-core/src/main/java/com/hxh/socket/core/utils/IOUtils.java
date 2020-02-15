package com.hxh.socket.core.utils;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;

/**
 * description:
 *
 * @author huangxianhong
 * @version v1.0.0
 * @date Created in 2019/12/24 19:52
 */
public class IOUtils {
    public static void close(AsynchronousSocketChannel channel) {
        if (channel == null) {
            throw new NullPointerException();
        }
        try {
            channel.shutdownInput();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            channel.shutdownOutput();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
