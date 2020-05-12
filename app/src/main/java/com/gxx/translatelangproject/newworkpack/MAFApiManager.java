package com.gxx.translatelangproject.newworkpack;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.gxx.translatelangproject.MainActivity.TRANS_API_HOST;

public class MAFApiManager {
    private static MAFApiManager sMAFApiManager;
    private OkHttpClient mClient;
    private Retrofit retrofit;

    /**
    * @date 创建时间 2019/8/1
    * @author gaoxiaoxiong
    * @desc 初始化使用
    **/
    public static void initApiManager(){
        getInstence();
    }

    /**
     * 作者：GaoXiaoXiong
     * 创建时间:2018/5/19
     * 注释描述:构造类，只获取单一的类
     */
    public static MAFApiManager getInstence() {
        if (sMAFApiManager == null) {
            synchronized (MAFApiManager.class) {
                if (sMAFApiManager == null) {
                    sMAFApiManager = new MAFApiManager();
                }
            }
        }
        return sMAFApiManager;
    }

    private MAFApiManager() {
        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor();//日志太多会崩溃
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        mClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(new HttpInterceptor())
                .addNetworkInterceptor(logInterceptor)
                .retryOnConnectionFailure(true)     //是否失败重新请求连接
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(TRANS_API_HOST)
                .client(mClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    /**
     * @date: 2019/3/8 0008
     * @author: gaoxiaoxiong
     * @description:获取接口
     **/
    public MAFApiService createBaseApi() {
        return getApi(MAFApiService.class);
    }

    /**
     * 获取接口
     * 这里返回的接口的基类，需要自行把强制转换为需要返回的接口
     * @param clazz 类类型
     * @date 2018-06-11 16:24
     */
    public <T> T getApi(Class<?> clazz) {
        return (T) retrofit.create(clazz);
    }
}
