package com.gxx.translatelangproject.newworkpack;


import io.reactivex.Observer;

public abstract class BaseObserver<T> implements Observer<T> {
    @Override
    public void onError(Throwable e) {
        String errorMessage = "";
       if (e instanceof ExceptionHandle.ResponeThrowable) {
            ExceptionHandle.ResponeThrowable error = (ExceptionHandle.ResponeThrowable) e;
            onError(error.code, errorMessage);
        }
    }
    public abstract void onError(int status, String msg);
}