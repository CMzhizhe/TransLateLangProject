package com.gxx.translatelangproject.newworkpack;


import java.util.HashMap;
import java.util.Map;

import io.reactivex.disposables.Disposable;

import static com.gxx.translatelangproject.MainActivity.APPID;
import static com.gxx.translatelangproject.MainActivity.SECURITYKEY;

/**
 * @date: 2019/7/19 0019
 * @author: gaoxiaoxiong
 * @description:封装一些常用的请求方式
 **/
public class MAFMobileRequest {
    public static void postJsonRequest(String url, Map<String, String> mapString, OnRequestSuccessListener onRequestSuccessListener, OnRequestFailListener onRequestFailListener) {
        if (mapString == null) {
            mapString = new HashMap<>();
        }

        mapString.put("appid", APPID);
        // 随机数
        String salt = String.valueOf(System.currentTimeMillis());
        mapString.put("salt", salt);
        // 签名
        String src = APPID + mapString.get("q") + salt + SECURITYKEY; // 加密前的原文
        mapString.put("sign", MD5.md5(src));
        MAFApiManager.getInstence().createBaseApi().postJsonRequest(url, mapString)
                .compose(ResponseTransformer.JsonHandleResult())
                .compose(SchedulerProvider.getInstance().applySchedulers())
                .subscribe(new BaseObserver<String>() {
                    @Override
                    public void onError(int status, String msg) {
                        onRequestFailListener.onReqeustFail(status, msg);
                    }

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String jsonString) {
                        onRequestSuccessListener.onRequestSuccess(jsonString);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

}
