package com.hxh.socket.core;

/**
 * description:
 *
 * @author huangxianhong
 * @version v1.0.0
 * @date Created in 2020/1/2 13:41
 */
public class Pair<T1, T2> {
    private T1 t1;

    private T2 t2;

    public Pair(T1 t1, T2 t2) {
        this.t1 = t1;
        this.t2 = t2;
    }

    public T1 getT1() {
        return t1;
    }

    public void setT1(T1 t1) {
        this.t1 = t1;
    }

    public T2 getT2() {
        return t2;
    }

    public void setT2(T2 t2) {
        this.t2 = t2;
    }
}
