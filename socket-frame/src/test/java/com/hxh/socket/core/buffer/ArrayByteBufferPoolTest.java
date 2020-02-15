package com.hxh.socket.core.buffer;

import com.hxh.socket.core.utils.Time;
import org.apache.commons.lang3.RandomUtils;

/**
 * description:
 *
 * @author huangxianhong
 * @version v1.0.0
 * @date Created in 2020/1/2 17:07
 */
public class ArrayByteBufferPoolTest {
    public static void main(String []args) throws Exception{
        ByteBufferPool byteBufferPool = new DefaultByteBufferPool(5 * 1024, 1024, Time.SYSTEM);
        while(true){
            PooledByteBuffer byteBuffer = byteBufferPool.allocate(RandomUtils.nextInt(1,2000), 1000);
            System.out.println(byteBuffer);
        }
    }
}
