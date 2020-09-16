package com.kirago.netty.im.common.util.api;

public class ResponseResult<T> {

    public int code;

    private String msg;

    private T data;

    public ResponseResult<T> setCode(ResultCode retCode) {
        this.code = retCode.code;
        return this;
    }

    public int getCode() {
        return code;
    }

    public ResponseResult<T> setCode(int code) {
        this.code = code;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public ResponseResult<T> setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public T getData() {
        return data;
    }

    public ResponseResult<T> setData(T data) {
        this.data = data;
        return this;
    }

    public enum ResultCode{

        SUCCESS(200),

        FAIL(400),

        UNAUTHORIZED(401),

        INTERNAL_SERVER_ERROR(500);

        public int code;
        ResultCode(int code){
            this.code = code;
        }
    }
}

