package com.hxh.socket.core.protocol;

import com.hxh.socket.core.FType;
import com.hxh.socket.core.OneWayType;
import com.hxh.socket.core.utils.BufferUtil;
import com.hxh.socket.frame.AbstractFrame;
import com.hxh.socket.frame.protocol.FrameProtocol;
import com.hxh.socket.core.transport.TcpAioSession;
import com.hxh.socket.core.utils.MapUtils;
import com.hxh.socket.core.utils.Time;
import com.hxh.socket.core.utils.UUIDUtils;
import org.apache.commons.codec.binary.Hex;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * description:
 *
 * @author huangxianhong
 * @version v1.0.0
 * @date Created in 2019/12/26 11:41
 */
public class FrameProtocolTest {

    @Test
    public void decodeTest() throws Exception{


        int headerDataLength = 0;

        int opaque = 1000;
        String frameId = UUIDUtils.getId();
        long createTime = Time.SYSTEM.milliseconds();
        Map<String, String> map = new HashMap<>();
        map.put("userName", "hxh");
        byte[] bytes = MapUtils.serialize(map);


        ByteBuffer header = BufferUtil.allocate(AbstractFrame.BASE_LENGTH + bytes.length);
        header.put(FType.REQUEST.getCode());
        header.putInt(opaque);
        header.put((byte) frameId.getBytes().length);
        header.put(frameId.getBytes());
        header.putLong(createTime);
        header.put(bytes);

        headerDataLength = header.array().length;


        int bodyDataLength = 5;

        ByteBuffer byteBuffer = BufferUtil.allocate(1 + 4 + 4 + headerDataLength + bodyDataLength);
        byteBuffer.put((byte) 0xF4);
        byteBuffer.putInt(4 + 4 + headerDataLength + bodyDataLength);
        byteBuffer.put(AbstractFrame.markOneWayType(headerDataLength, OneWayType.NO));

        byteBuffer.put(header.array());


        byteBuffer.put("hello".getBytes());
        byteBuffer.flip();

        String hex = Hex.encodeHexString(byteBuffer, false);

        Protocol<AbstractFrame, AbstractFrame> protocol = new FrameProtocol();
        AbstractFrame frame = protocol.decode(byteBuffer, new TcpAioSession<>(null, null, null, null,null));
        System.out.println(frame);
    }


    @Test
    public void encodeTest() {

    }
}
