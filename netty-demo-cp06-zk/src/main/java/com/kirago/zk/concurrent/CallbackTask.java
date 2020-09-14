package com.kirago.zk.concurrent;

public interface CallbackTask<R> {
    R execute() throws Exception;
    
    void onSuccess(R r);
    
    void onFailure(Throwable t);
    
}
