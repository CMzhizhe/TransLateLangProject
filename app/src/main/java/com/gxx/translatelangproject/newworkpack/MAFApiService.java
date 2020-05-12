package com.gxx.translatelangproject.newworkpack;


import java.util.Map;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

//https://www.jianshu.com/p/308f3c54abdd/
//https://blog.csdn.net/u013449800/article/details/77772656 传递对象数组
//@Body标签不能和@FormUrlEncoded标签同时使用
public interface MAFApiService {

    /**
     * @date: 2019/7/20 0020
     * @author: gaoxiaoxiong
     * @description:普通的request请求
     * @map 是放在url后面
     **/
    @GET()
    Observable<ResponseBody> getJsonRequest(@Url String url, @QueryMap Map<String, String> mapString);


}
