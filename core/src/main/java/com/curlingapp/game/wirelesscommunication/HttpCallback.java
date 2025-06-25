package com.curlingapp.game.wirelesscommunication;

public interface HttpCallback<T> {
    void onSuccess(T result);
    void onError(Throwable t);
}
