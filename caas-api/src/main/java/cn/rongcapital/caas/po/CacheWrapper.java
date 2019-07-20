package cn.rongcapital.caas.po;

import java.io.Serializable;

public class CacheWrapper<T> implements Serializable {

    private static final long serialVersionUID = -3659761346314030460L;

    private T data;

    public CacheWrapper() {

    }

    public CacheWrapper(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
