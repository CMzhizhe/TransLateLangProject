package com.gxx.translatelangproject.newworkpack;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.Function;
import okhttp3.ResponseBody;

/**
 * Created by Zaifeng on 2018/2/28.
 * 对返回的数据进行处理，区分异常的情况。
 */

public class ResponseTransformer {

    /**
     * @date: 2019/4/27 0027
     * @author: gaoxiaoxiong
     * @description:JSON解析
     **/
    public static <T> ObservableTransformer<ResponseBody, String> JsonHandleResult() {
        return upstream -> upstream
                .onErrorResumeNext(new ErrorJsonResumeFunction<>())//处理服务器给的崩溃异常，或者是404异常  这里的404异常是在  retrofit BodyObservable 一个类里处理的 onNext 方法，他里面处理了 HttpException 异常
                .flatMap(new ResponseJsonFunction<>());
    }


    /**
     * 服务器产生的异常，非正常状态的异常，比如404
     *
     * @param <T>
     * @description:JSON解析
     */
    private static class ErrorJsonResumeFunction<T> implements Function<Throwable, ObservableSource<? extends ResponseBody>> {
        @Override
        public ObservableSource<? extends ResponseBody> apply(Throwable throwable) throws Exception {
            return Observable.error(ExceptionHandle.handleException(throwable));
        }
    }


    /**
     * 服务其返回的数据解析
     * 正常服务器返回数据和服务器可能返回的exception
     * activityStack
     *
     * @param <T>
     * @description:JSON解析
     */
    private static class ResponseJsonFunction<T> implements Function<ResponseBody, Observable<String>> {
        @Override
        public Observable<String> apply(ResponseBody tResponse) throws Exception {
            String jsString = tResponse.string();
            JsonObject jsonObject = new JsonParser().parse(jsString).getAsJsonObject();
            tResponse.close();
            return Observable.just(jsonObject.toString());
        }
    }

}
