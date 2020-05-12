package com.gxx.translatelangproject.newworkpack;


import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.reactivex.disposables.Disposable;

import static com.gxx.translatelangproject.MainActivity.APPID;
import static com.gxx.translatelangproject.MainActivity.SECURITYKEY;

/**
 * @date: 2019/7/19 0019
 * @author: gaoxiaoxiong
 * @description:封装一些常用的请求方式
 **/
public class MAFMobileRequest {

    /**
     * @date: 创建时间:2019/9/4
     * @author: gaoxiaoxiong
     * @descripion:get请求，map类型的转成?的拼接模式
     **/
    public static void getJsonRequest(String url, Map<String, String> mapString, OnRequestSuccessListener onRequestSuccessListener, OnRequestFailListener onRequestFailListener) {
        if (mapString == null) {
            mapString = new HashMap<>();
        }

        mapString.put("appid",APPID);
        // 随机数
        String salt = String.valueOf(System.currentTimeMillis());
        mapString.put("salt", salt);
        // 签名
        String src = APPID + mapString.get("q") + salt + SECURITYKEY; // 加密前的原文
        mapString.put("sign", MD5.md5(src));
        MAFApiManager.getInstence().createBaseApi().getJsonRequest(url,mapString)
                .compose(ResponseTransformer.JsonHandleResult())
                .compose(SchedulerProvider.getInstance().applySchedulers())
                .subscribe(new BaseObserver<String>() {
                    @Override
                    public void onError(int status, String msg) {
                        onRequestFailListener.onReqeustFail(status,msg);
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


    public static String getUrlWithQueryString(String url, Map<String, String> params) {
        if (params == null) {
            return url;
        }

        StringBuilder builder = new StringBuilder(url);
        if (url.contains("?")) {
            builder.append("&");
        } else {
            builder.append("?");
        }

        int i = 0;
        for (String key : params.keySet()) {
            String value = params.get(key);
            if (value == null) { // 过滤空的key
                continue;
            }

            if (i != 0) {
                builder.append('&');
            }

            builder.append(key);
            builder.append('=');
            builder.append(value);

            i++;
        }

        return builder.toString();
    }

    private static TrustManager myX509TrustManager = new X509TrustManager() {


        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    };


}
