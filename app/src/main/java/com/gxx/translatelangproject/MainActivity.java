package com.gxx.translatelangproject;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.gxx.translatelangproject.model.TransResultModel;
import com.gxx.translatelangproject.newworkpack.MAFMobileRequest;
import com.gxx.translatelangproject.newworkpack.OnRequestFailListener;
import com.gxx.translatelangproject.newworkpack.OnRequestSuccessListener;
import com.gxx.translatelangproject.utils.FileSDCardUtil;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private String TAG = MainActivity.class.getSimpleName();
    public static final String TargetLang = "cht";
    public static final String APPID = "";
    public static final String SECURITYKEY = "";
    public static final String TRANS_API_HOST = "http://api.fanyi.baidu.com/api/trans/vip/translate/";
    private boolean toParseAndroid = false;
    private String iosText = "\"tabbar.home.title\" = \"中国\";\n" +
            "\"tabbar.category.title\" = \"分类\";\n" +
            "\"tabbar.discover.title\" = \"发现\";\n" +
            "\"tabbar.shopsCart.title\" = \"购物车\";";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (toParseAndroid) {
            parserAndroidXml();
        } else {
            parseIosText();
        }
    }

    /**
     * @date 创建时间:2020/5/13 0013
     * @auther gaoxiaoxiong
     * @Descriptiion 解析IOS
     **/
    private void parseIosText() {
        String[] arrayList = iosText.trim().toString().split(";");
        List<TransResultModel> transResultModelList = new ArrayList<>();
        for (int i = 0; i < arrayList.length; i++) {
            TransResultModel transResultModel = new TransResultModel();
            transResultModel.setResourceKey(arrayList[i].split("=")[0].trim().replace("\"", "").replace("\n",""));
            transResultModel.setResourceValue(arrayList[i].split("=")[1].trim().replace("\"", "").replace("\n",""));
            transResultModelList.add(transResultModel);
        }
        readAndroidBaiduFanYi(transResultModelList);
    }

    /**
     * @date 创建时间: 2020/5/12
     * @auther gaoxiaoxiong
     * @description 获取xml的值
     **/
    private void parserAndroidXml() {
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
                        while (iterator.hasNext()) {
                            Element element = iterator.next();
                            Attribute attribute = element.attribute("name"); //ID属性对象
                            TransResultModel transResultModel = new TransResultModel();
                            transResultModel.setResourceKey(attribute.getValue());
                            transResultModel.setResourceValue(element.getText());
                            list.add(transResultModel);
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
                        readAndroidBaiduFanYi(list);
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
    private void readAndroidBaiduFanYi(List<TransResultModel> transResultModelList) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < transResultModelList.size(); i++) {
            stringBuilder.append(transResultModelList.get(i).getResourceValue());
            if (i + 1 != transResultModelList.size()) {
                stringBuilder.append("\n");
            }
        }
        String query = stringBuilder.toString();
        if (query.length() >= 2000) {
            Log.e(TAG, "警告：单次翻译字符不能超过2000个，否则会导致翻译不准确");
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("q", query);
        map.put("from", "zh");
        map.put("to", TargetLang);
        MAFMobileRequest.postJsonRequest(TRANS_API_HOST, map, new OnRequestSuccessListener() {
            @Override
            public void onRequestSuccess(Object response) {
                JsonObject jsonObject = new JsonParser().parse(response.toString()).getAsJsonObject();
                JsonArray jsonArray = jsonObject.getAsJsonArray("trans_result");
                List<TransResultModel> newTransResultList = new Gson().fromJson(jsonArray.toString(), new TypeToken<List<TransResultModel>>() {
                }.getType());
                for (int i = 0; i < newTransResultList.size(); i++) {
                    newTransResultList.get(i).setResourceKey(transResultModelList.get(i).getResourceKey());
                }
                if (toParseAndroid) {
                    createXml(newTransResultList);
                } else {
                    createIosText(newTransResultList);
                }
            }
        }, new OnRequestFailListener() {
            @Override
            public void onReqeustFail(int status, String failMsg) {

            }
        });
    }

    /**
     * @date 创建时间:2020/5/13 0013
     * @auther gaoxiaoxiong
     * @Descriptiion android 生成一个文件
     **/
    public void createXml(List<TransResultModel> list) {
        String path = FileSDCardUtil.getInstance().getSandboxPublickDiskFileDir(this, "translate");
        File file = new File(path, "translateAndroid.xml");
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Document document = DocumentHelper.createDocument();
        Element resourcesElement = document.addElement("resources");
        for (int i = 0; i < list.size(); i++) {
            Element stringElement = resourcesElement.addElement("string");
            stringElement.addAttribute("name", list.get(i).getResourceKey()).setText(list.get(i).getDst());
        }
        try {
            Writer fileWriter = new FileWriter(file);
            XMLWriter xmlWriter = new XMLWriter(fileWriter);
            xmlWriter.write(document);
            xmlWriter.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * @date 创建时间:2020/5/13 0013
     * @auther gaoxiaoxiong
     * @Descriptiion 创建IOS文本
     **/
    public void createIosText(List<TransResultModel> list) {
        String path = FileSDCardUtil.getInstance().getSandboxPublickDiskFileDir(this, "translate");
        File file = new File(path, "translateAndroid.txt");
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Observable.just("").observeOn(Schedulers.newThread())
                .map(new Function<String, String>() {
                    @Override
                    public String apply(String s) throws Exception {
                        for (int i = 0; i < list.size(); i++) {
                            StringBuilder stringBuilder = new StringBuilder();
                            TransResultModel transResultModel = list.get(i);
                            stringBuilder.append("\"");
                            stringBuilder.append(transResultModel.getResourceKey());
                            stringBuilder.append("\"");

                            stringBuilder.append("=");

                            stringBuilder.append("\"");
                            stringBuilder.append(transResultModel.getDst());
                            stringBuilder.append("\"");
                            stringBuilder.append(";");
                            stringBuilder.append("\r\n");
                            String content = stringBuilder.toString();
                            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
                            raf.seek(file.length());
                            raf.write(content.getBytes());
                            raf.close();
                        }
                        return "";
                    }
                }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String s) {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

}
