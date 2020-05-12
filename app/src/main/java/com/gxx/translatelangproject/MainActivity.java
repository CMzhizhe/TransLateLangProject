package com.gxx.translatelangproject;

import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.gxx.translatelangproject.model.TransResultModel;
import com.gxx.translatelangproject.newworkpack.MAFMobileRequest;
import com.gxx.translatelangproject.newworkpack.OnRequestFailListener;
import com.gxx.translatelangproject.newworkpack.OnRequestSuccessListener;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private String TAG = MainActivity.class.getSimpleName();
    public static final String APPID = "";
    public static final String SECURITYKEY = "";
    public static final String TRANS_API_HOST = "http://api.fanyi.baidu.com/api/trans/vip/translate/";
    private List<TransResultModel> transResultModelList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        readXml();
    }

    /**
     * @date 创建时间: 2020/5/12
     * @auther gaoxiaoxiong
     * @description 获取xml的值
     **/
    private void readXml() {
        transResultModelList.clear();
        Observable.just("").observeOn(Schedulers.newThread())
                .map(new Function<String, List<TransResultModel>>() {
                    @Override
                    public List<TransResultModel> apply(String s) throws Exception {
                        List<TransResultModel> list = new ArrayList<>();
                        // 创建解析器
                        SAXReader xSaxReader = new SAXReader();
                        // 获取根节点
                        InputStream input = getResources().openRawResource(R.raw.strings);
                        Document document = xSaxReader.read(input);
                        Element rootElement = document.getRootElement();
                        Iterator<Element> iterator = rootElement.elementIterator();
                        int i = 0;
                        while (iterator.hasNext()) {
                            Element element = iterator.next();
                            Attribute attribute=element.attribute("name"); //ID属性对象
                            TransResultModel transResultModel = new TransResultModel();
                            transResultModel.setPosition(i);
                            transResultModel.setResourceKey(attribute.getValue());
                            transResultModel.setResourceValue(element.getText());
                            i = i + 1;
                            list.add(transResultModel);
                       /*     Log.e(TAG, attribute.getValue());//获取name的值
                            Log.e(TAG, element.getText());//获取参数值*/
                        }
                        input.close();
                        return list;
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<TransResultModel>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<TransResultModel> list) {
                        transResultModelList.addAll(list);
                        readBaiduFanYi();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * @date 创建时间: 2020/5/12
     * @auther gaoxiaoxiong
     * @description 百度翻译
     **/
    private void readBaiduFanYi(){
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = transResultModelList.size() - 1; i >= 0; i--) {
            stringBuilder.append(transResultModelList.get(i).getResourceValue());
            stringBuilder.append("\n");
        }
        String query = stringBuilder.toString();
        Map<String, String> map = new HashMap<>();
        map.put("q", query);
        map.put("from", "zh");
        map.put("to", "en");
        MAFMobileRequest.getJsonRequest(TRANS_API_HOST, map, new OnRequestSuccessListener() {
            @Override
            public void onRequestSuccess(Object response) {
                JsonObject jsonObject = new JsonParser().parse(response.toString()).getAsJsonObject();
                JsonArray jsonArray = jsonObject.getAsJsonArray("trans_result");
                List<TransResultModel> list = new Gson().fromJson(jsonArray.toString(), new TypeToken<List<TransResultModel>>() {
                }.getType());
                Log.e(TAG,response.toString());
            }
        }, new OnRequestFailListener() {
            @Override
            public void onReqeustFail(int status, String failMsg) {

            }
        });
    }
}
