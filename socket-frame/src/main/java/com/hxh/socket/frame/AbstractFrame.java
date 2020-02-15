package com.hxh.socket.frame;

import com.hxh.socket.core.FType;
import com.hxh.socket.core.IFrame;
import com.hxh.socket.core.OneWayType;
import com.hxh.socket.core.utils.BufferUtil;
import com.hxh.socket.core.exception.FrameException;
import com.hxh.socket.core.utils.Time;
import com.hxh.socket.core.utils.UUIDUtils;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * description:
 * totalLength+headerLength+headerBytes+bodyBytes
 *
 * @author huangxianhong
 * @version v1.0.0
 * @date Created in 2019/12/24 10:26
 */
public abstract class AbstractFrame implements IFrame {
    /**
     * 最大Header
     */
    public static final int MAX_HEADER_LENGTH = 1024 * 1024;

    /**
     * 最大Body
     */
    public static final int MAX_BODY_LENGTH = 1024 * 1024;

    /**
     * type+oneWayType+opaque+frameId+createTime
     */
    public static final int BASE_LENGTH = 1 + 4 + 1 + 32 + 8;

    public static final int MIN_READ_LENGTH = 1 + 4 + 4 + BASE_LENGTH;
    public static final int MAX_READ_LENGTH = 1024;
    /**
     * 标志位长度
     */
    public static final int SIGN_LENGTH = 1;
    /**
     * 请求ID
     */
    private static final AtomicInteger REQUEST_ID = new AtomicInteger(0);

    /**
     * 是request还是response
     */
    private FType type = FType.REQUEST;

    /**
     * 请求是否需要返回值
     */
    private OneWayType oneWayType = OneWayType.YES;

    private int opaque = REQUEST_ID.getAndIncrement();

    /**
     * 帧ID
     */
    private String frameId = UUIDUtils.getId();

    /**
     * 帧创建时间
     */
    private long createTime = Time.SYSTEM.milliseconds();

    private String from;
    private String to;

    private int writeIndex;
    /**
     * 总长度字节数{@link AbstractFrame#getTotalLengthSize()} + 头部长度字节数{@link AbstractFrame#getHeaderLengthSize()} + header数据长度+body数据长度
     */
    private int writeTotal;

    /**
     * header数据长度
     */
    private int headerDataLength;

    private int headerExtDataLength;
    /**
     * body数据长度
     */
    private int bodyDataLength;
    /**
     * 数据缓存
     */
    private byte[] cache;

    private int readTotal;
    private int readIndex;

    public int getOpaque() {
        return opaque;
    }

    @Override
    public String getFrameId() {
        return frameId;
    }

    public long getCreateTime() {
        return createTime;
    }


    public int getWriteIndex() {
        return writeIndex;
    }

    public void setWriteIndex(int writeIndex) {
        this.writeIndex = writeIndex;
    }

    public int getWriteTotal() {
        return writeTotal;
    }

    public void setWriteTotal(int writeTotal) {
        this.writeTotal = writeTotal;
    }


    public int getReadTotal() {
        return readTotal;
    }

    public void setReadTotal(int readTotal) {
        this.readTotal = readTotal;
    }

    public int getReadIndex() {
        return readIndex;
    }

    public void setReadIndex(int readIndex) {
        this.readIndex = readIndex;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    @Override
    public FType getType() {
        return type;
    }

    public void setType(FType type) {
        this.type = type;
    }

    public int getHeaderDataLength() {
        return headerDataLength;
    }

    public void setHeaderDataLength(int headerDataLength) {
        this.headerDataLength = headerDataLength;
    }

    public int getHeaderExtDataLength() {
        return headerExtDataLength;
    }

    public void setHeaderExtDataLength(int headerExtDataLength) {
        this.headerExtDataLength = headerExtDataLength;
    }

    public byte[] getCache() {
        return cache;
    }

    public void setCache(byte[] cache) {
        this.cache = cache;
    }

    public void setOpaque(int opaque) {
        this.opaque = opaque;
    }

    public void setFrameId(String frameId) {
        this.frameId = frameId;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getBodyDataLength() {
        return bodyDataLength;
    }

    public void setBodyDataLength(int bodyDataLength) {
        this.bodyDataLength = bodyDataLength;
    }

    @Override
    public OneWayType getOneWayType() {
        return oneWayType;
    }

    @Override
    public void setOneWayType(OneWayType oneWayType) {
        this.oneWayType = oneWayType;
    }

    /**
     * header长度
     *
     * @return 头部长度暂用字节数
     */
    public abstract int getHeaderLengthSize();


    /**
     * 该Frame所有字节长度所占用字节数
     *
     * @return 该Frame所有字节长度所占用字节数
     */
    public abstract int getTotalLengthSize();

    /**
     * 解析header
     *
     * @param bytes header
     */
    public abstract void writeHeader(byte[] bytes);

    /**
     * 解析body
     *
     * @param bytes body
     */
    public abstract void writeBody(byte[] bytes);

    /**
     * 标识
     *
     * @return 标识
     */
    public abstract byte getSign();


    private void calculateBodyLength() {
        bodyDataLength = writeTotal - getTotalLengthSize() - getHeaderLengthSize() - headerDataLength;
    }

    public void startWriteToFrame() {
        setWriteTotal(getTotalLengthSize());
    }

    public int needWriting() {
        return writeTotal - writeIndex - (cache == null ? 0 : cache.length);
    }

    public void writeToFrame(byte[] bytes) {
        if (cache == null || cache.length == 0) {
            cache = bytes;
        } else {
            int total = bytes.length + cache.length;
            byte[] combine = new byte[total];
            System.arraycopy(cache, 0, combine, 0, cache.length);
            System.arraycopy(bytes, 0, combine, cache.length, bytes.length);
            cache = combine;
        }

        //解析总长度
        if (writeIndex < getTotalLengthSize()) {
            if (!parseTotalLength()) {
                return;
            }
        }

        //解析头数据长度和body数据长度
        if (writeIndex < getTotalLengthSize() + getHeaderLengthSize()) {
            if (parseHeaderLength()) {
                calculateBodyLength();
            } else {
                return;
            }
        }


        //解析头数据
        if (writeIndex < getTotalLengthSize() + getHeaderLengthSize() + headerDataLength) {
            if (cache.length < headerDataLength) {
                return;
            } else {
                byte[] header = new byte[getHeaderDataLength()];
                System.arraycopy(cache, 0, header, 0, headerDataLength);
                writeHeader(header);
                writeIndex += headerDataLength;

                byte[] remain = new byte[cache.length - headerDataLength];
                System.arraycopy(cache, headerDataLength, remain, 0, remain.length);
                cache = remain;
            }
        }

        if (cache.length < bodyDataLength) {
            return;
        }
        writeBody(cache);
        writeIndex += cache.length;
        cache = null;
    }

    private boolean parseHeaderLength() {
        if (getHeaderLengthSize() > cache.length) {
            return false;
        } else {
            byte[] dst = new byte[getHeaderLengthSize()];
            System.arraycopy(cache, 0, dst, 0, getHeaderLengthSize());
            ByteBuffer lengthBuffer = BufferUtil.wrap(dst);
            int oriHeaderLen = lengthBuffer.getInt();
            headerDataLength = getHeaderLength(oriHeaderLen);
            oneWayType = getOneWayType(oriHeaderLen);
            headerExtDataLength = headerDataLength - BASE_LENGTH;
            writeIndex += getHeaderLengthSize();

            byte[] remain = new byte[cache.length - getHeaderLengthSize()];
            System.arraycopy(cache, getHeaderLengthSize(), remain, 0, remain.length);
            cache = remain;
            return true;
        }
    }


    private boolean parseTotalLength() {
        if (getTotalLengthSize() > cache.length) {
            return false;
        } else {
            byte[] dst = new byte[getTotalLengthSize()];
            System.arraycopy(cache, 0, dst, 0, getTotalLengthSize());
            ByteBuffer lengthBuffer = BufferUtil.wrap(dst);
            writeTotal = lengthBuffer.getInt();
            writeIndex += getTotalLengthSize();

            byte[] remain = new byte[cache.length - getTotalLengthSize()];
            System.arraycopy(cache, getTotalLengthSize(), remain, 0, remain.length);
            cache = remain;
            return true;
        }
    }


    /**
     * 头部buffer
     * @return buffer
     */
    public abstract byte[] getHeaderBytes();


    /**
     * 数据buffer
     * @return buffer
     */
    public abstract byte[] getBodyBytes();


    public void startToRead() {
        //opaque+frameId.length+frameId+createTime+头部数据
        int headerDataLength = BASE_LENGTH + (getHeaderBytes() == null ? 0 : getHeaderBytes().length);
        setBodyDataLength(getBodyBytes() == null ? 0 : getBodyBytes().length);
        setHeaderDataLength(headerDataLength);
        setHeaderExtDataLength(headerDataLength - BASE_LENGTH);
        setReadTotal(SIGN_LENGTH + getTotalLengthSize() + getHeaderLengthSize() + getHeaderDataLength() + getBodyDataLength());
        setReadIndex(0);
    }

    @Override
    public int remainReading() {
        return readTotal - readIndex;
    }


    /**
     * <ul>
     *     <li>总字节 = sign(1字节)+总长度（4字节）+头部总长度（4字节）+FType(1字节)+opaque(4字节)+frameIdLength(1字节)+frameId(32字节) + createTime(8字节)</li>
     *     <li>头部总长度(4字节) = OneWayType（1个字节）+头部数据长度（3字节）</li>
     * </ul>
     *
     * @return 55字节
     */
    private byte[] readBase() {
        ByteBuffer byteBuffer = BufferUtil.allocate(SIGN_LENGTH + getTotalLengthSize() + getHeaderLengthSize() + BASE_LENGTH);
        byteBuffer.put(this.getSign());
        byteBuffer.putInt(getTotalLengthSize() + getHeaderLengthSize() + headerDataLength + bodyDataLength);

        byteBuffer.put(markOneWayType(headerDataLength, oneWayType));

        byteBuffer.put(type.getCode());
        byteBuffer.putInt(opaque);
        byte[] frameIdBytes = frameId.getBytes();
        byteBuffer.put((byte) frameIdBytes.length);
        byteBuffer.put(frameIdBytes);
        byteBuffer.putLong(createTime);
        return byteBuffer.array();
    }


    /**
     * @param maxReadSize 需要读取的字节数
     * @return {@link AbstractFrame#getHeaderBytes 读取头部字节}
     */
    private byte[] readHeader(int maxReadSize) {
        return getBytes(maxReadSize, getHeaderBytes(), BASE_LENGTH);
    }


    /**
     * @param maxReadSize 需要读取的字节数
     * @return {@link AbstractFrame#getBodyBytes()}  读取数据字节}
     */
    private byte[] readBody(int maxReadSize) {
        return getBytes(maxReadSize, getBodyBytes(), headerDataLength);
    }


    /**
     * @param maxReadSize 需要读取的字节数
     * @param bytes       原始字节
     * @param length      长度
     * @return 字节
     */
    private byte[] getBytes(int maxReadSize, byte[] bytes, int length) {
        if (maxReadSize >= bytes.length) {
            return bytes;
        }
        int index = readIndex - getTotalLengthSize() - getHeaderLengthSize() - SIGN_LENGTH - length;

        int len = Math.min(bytes.length - index, maxReadSize);
        byte[] b = new byte[len];
        System.arraycopy(bytes, index, b, 0, b.length);
        return b;
    }


    /**
     * 改造了一下，让每次读取Frame的字节数尽量大，这样每个Frame发送时调用write的次数就会减少，改造后效率有所提升
     *
     * @param maxReadSize 需要读取的字节数
     * @return 字节
     */
    public byte[] readFromFrameNew(int maxReadSize) {
        checkHeader();
        checkBody();

        byte[] cacheBytes = readFromFrame(maxReadSize);

        for (; cacheBytes != null
                && cacheBytes.length < MAX_READ_LENGTH
                && remainReading() > 0; ) {

            byte[] bytes = readFromFrame(maxReadSize - cacheBytes.length);
            if (bytes == null || bytes.length == 0) {
                continue;
            } else {
                int len = cacheBytes.length + bytes.length;
                byte[] combine = new byte[len];
                System.arraycopy(cacheBytes, 0, combine, 0, cacheBytes.length);
                System.arraycopy(bytes, 0, combine, cacheBytes.length, bytes.length);
                cacheBytes = combine;
            }
        }
        return cacheBytes;
    }


    /**
     * 读取frame中的数据
     * <pre>
     *  一个frame分多次读取
     *  </pre>
     *
     * @param maxReadSize 需要读取的字节数
     * @return 字节
     */
    public byte[] readFromFrame(int maxReadSize) {
        if (maxReadSize == 0) {
            throw new FrameException("maxReadSize is zero");
        }

        if (maxReadSize < MIN_READ_LENGTH) {
            throw new FrameException(String.format("maxReadSize should greater than MIN_READ_LENGTH(%s)", MIN_READ_LENGTH));
        }

        if (maxReadSize > remainReading()) {
            maxReadSize = remainReading();
        }

        if (readIndex < SIGN_LENGTH + getTotalLengthSize() + getHeaderLengthSize() + BASE_LENGTH) {
            byte[] bytes = readBase();
            readIndex += bytes.length;
            return bytes;
        }

        if (readIndex < SIGN_LENGTH + getTotalLengthSize() + getHeaderLengthSize() + headerDataLength) {
            byte[] bytes = readHeader(maxReadSize);
            readIndex += bytes.length;
            return bytes;
        }

        byte[] bytes = readBody(maxReadSize);
        readIndex += bytes.length;
        return bytes;
    }

    /**
     * 来自rocketMQ
     *
     * @param source
     * @param type
     * @return
     */
    public static byte[] markOneWayType(int source, OneWayType type) {
        byte[] result = new byte[4];

        result[0] = type.getCode();
        result[1] = (byte) ((source >> 16) & 0xFF);
        result[2] = (byte) ((source >> 8) & 0xFF);
        result[3] = (byte) (source & 0xFF);
        return result;
    }

    /**
     * 来自rocketMQ
     *
     * @param source
     * @return
     */
    public static OneWayType getOneWayType(int source) {
        return OneWayType.valueOf((byte) ((source >> 24) & 0xFF));
    }


    /**
     * 来自rocketMQ
     *
     * @param length
     * @return
     */
    public static int getHeaderLength(int length) {
        return length & 0xFFFFFF;
    }

    /**
     * 检查header是否超过最大限制
     */
    public void checkHeader() {
        if (this.getHeaderBytes() != null && MAX_HEADER_LENGTH < this.getHeaderBytes().length) {
            throw new FrameException(String.format("header bytes length is larger than %d", MAX_HEADER_LENGTH));
        }
    }

    /**
     * 检查body是否超过最大限制
     */
    public void checkBody() {
        if (this.getBodyBytes() != null && MAX_BODY_LENGTH < this.getBodyBytes().length) {
            throw new FrameException(String.format("body bytes length is larger than %d", MAX_BODY_LENGTH));
        }
    }
}
