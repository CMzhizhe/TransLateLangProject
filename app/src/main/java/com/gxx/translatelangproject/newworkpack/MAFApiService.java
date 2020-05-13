package com.gxx.translatelangproject.newworkpack;


import com.gxx.translatelangproject.model.TransResultModel;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Url;
public interface MAFApiService {

    @FormUrlEncoded
    @POST()
    Observable<TransResultModel> postTransRequest(@Url String url, @FieldMap Map<String, String> mapString);


    /**
     * @date: 2019/7/20 0020
     * @author: gaoxiaoxiong
     * @description:普通的request请求
     * @map是放在body里面
     **/
    @FormUrlEncoded
    @POST()
    Observable<ResponseBody> postJsonRequest(@Url String url, @FieldMap Map<String, String> mapString);
}
