package com.hxh.socket.core.ssl;

import com.hxh.socket.core.IFrame;
import com.hxh.socket.core.exception.SslException;
import com.hxh.socket.core.transport.*;
import com.hxh.socket.core.utils.BufferUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

/**
 * description:
 *
 * @author huangxianhong
 * @version v1.0.0
 * @date Created in 2020/2/8 11:25
 */
public class SslTcpAioSession<T extends IFrame, R extends IFrame> extends TcpAioSession {

    private static final Logger logger = LoggerFactory.getLogger(SslTcpAioSession.class);

    /**
     * 写数据到网络时加密
     */
    private ByteBuffer encryptWriteBuffer;

    /**
     * 读数据后解密
     */
    private ByteBuffer decryptReadBuffer;


    private ByteBuffer cache;


    /**
     * ssl支持
     */
    private SslSupport sslSupport;


    /**
     * ssl引擎
     */
    private SSLEngine sslEngine;

    /**
     * @param socket                 通道
     * @param readCompletionHandler  读handler
     * @param writeCompletionHandler 写handler
     * @param config                 配置
     * @param sslSupport             ssl支持
     */
    public SslTcpAioSession(AsynchronousSocketChannel socket, ReadCompletionHandler readCompletionHandler, WriteCompletionHandler writeCompletionHandler, AioConfig config, SslSupport sslSupport, SSLEngine sslEngine, AbstractRemote remote) throws Exception {
        super(socket, readCompletionHandler, writeCompletionHandler, config, remote);
        this.sslSupport = sslSupport;
        this.sslEngine = sslEngine;
        this.decryptReadBuffer = BufferUtil.allocate(readBuffer.getBuffer().capacity());
        this.encryptWriteBuffer = BufferUtil.allocate(sslEngine.getSession().getPacketBufferSize());
    }


    /**
     * 写数据流到channel
     *
     * @param byteBuffer
     * @throws Exception
     */
    @Override
    protected void flushToNet(ByteBuffer byteBuffer) throws Exception {
        ByteBuffer willSendBuffer = handWrap(byteBuffer);
        if (willSendBuffer != null && willSendBuffer.hasRemaining()) {
            super.flushToNet(willSendBuffer);
        }
    }


    /**
     * 把这次需要发送的数据，加密打包放在一起，返回给channel,然后发送
     *
     * @param byteBuffer
     * @return
     * @throws Exception
     */
    private ByteBuffer handWrap(ByteBuffer byteBuffer) throws Exception {
        ByteBuffer willSendBuffer = null;
        while (byteBuffer.hasRemaining()) {
            encryptWriteBuffer.clear();
            SSLEngineResult result = sslEngine.wrap(byteBuffer, encryptWriteBuffer);
            switch (result.getStatus()) {
                case OK:
                    encryptWriteBuffer.flip();
                    if (willSendBuffer == null) {
                        willSendBuffer = encryptWriteBuffer;
                    } else {
                        ByteBuffer combine = BufferUtil.allocate(willSendBuffer.capacity() + encryptWriteBuffer.capacity());
                        combine.put(willSendBuffer);
                        combine.put(encryptWriteBuffer);
                        willSendBuffer = combine;
                    }
                    break;
                case BUFFER_OVERFLOW:
                    encryptWriteBuffer = sslSupport.enlargePacketBuffer(sslEngine, encryptWriteBuffer);
                    break;
                case BUFFER_UNDERFLOW:
                    throw new SslException("Buffer underflow occured after a wrap. I don't think we should ever get here.");
                case CLOSED:
                    throw new SslException("Client wants to close this channel when wrap data");
                default:
                    throw new IllegalStateException("Invalid SSL status: " + result.getStatus());
            }
        }
        return willSendBuffer;
    }


    /**
     * 解析frame
     *
     * @throws Exception
     */
    @Override
    protected void readFrame() throws Exception {
        ByteBuffer peerNetData = super.readBuffer.getBuffer();
        ByteBuffer peerNetDataCombine = BufferUtil.allocate((cache != null ? cache.capacity() : 0) + peerNetData.capacity());
        if (cache != null && cache.hasRemaining()) {
            peerNetDataCombine.put(cache);
        }
        peerNetDataCombine.put(peerNetData);
        peerNetDataCombine.flip();
        while (peerNetDataCombine.hasRemaining()) {
            decryptReadBuffer.clear();
            SSLEngineResult result = sslEngine.unwrap(peerNetDataCombine, decryptReadBuffer);
            switch (result.getStatus()) {
                case OK:
                    decryptReadBuffer.flip();
                    T current = (T) config.getProtocol().decode(decryptReadBuffer, this);
                    if (current == null) {
                        break;
                    }
                    try {
                        super.processWhenReceived(current);
                    } catch (Exception e) {
                        logger.error("process when received error:", e);
                    } finally {
                        cache = null;
                    }
                    break;
                case BUFFER_OVERFLOW:
                    decryptReadBuffer = sslSupport.enlargeApplicationBuffer(sslEngine, decryptReadBuffer);
                    break;
                case BUFFER_UNDERFLOW:
                    cache = peerNetDataCombine;
                    return;
                case CLOSED:
                    throw new SslException("Client wants to close this channel when unWrap data");
                default:
                    throw new IllegalStateException("Invalid SSL status: " + result.getStatus());
            }
        }
    }
}
