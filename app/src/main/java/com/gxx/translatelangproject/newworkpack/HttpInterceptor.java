package com.gxx.translatelangproject.newworkpack;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 创建时间: 2018/9/19
 * 创建人: GaoXiaoXiong
 * 功能描述:
 */
public class HttpInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        Request requst = builder.addHeader("Content-type", "application/json;charset=UTF-8").build();
        return chain.proceed(requst);
    }
}
