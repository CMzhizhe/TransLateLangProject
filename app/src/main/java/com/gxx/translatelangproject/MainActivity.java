package com.gxx.translatelangproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.gxx.translatelangproject.newworkpack.MAFMobileRequest;
import com.gxx.translatelangproject.newworkpack.OnRequestFailListener;
import com.gxx.translatelangproject.newworkpack.OnRequestSuccessListener;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private String TAG = MainActivity.class.getSimpleName();
    public static final String TRANS_API_HOST = "http://api.fanyi.baidu.com/api/trans/vip/translate/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String query = "高度600米\n商学院";
        Map<String,String> map = new HashMap<>();
        map.put("q",query);
        map.put("from","zh");
        map.put("to","en");
        MAFMobileRequest.getJsonRequest(TRANS_API_HOST, map, new OnRequestSuccessListener() {
            @Override
            public void onRequestSuccess(Object response) {
                Log.e(TAG,response.toString());
            }
        }, new OnRequestFailListener() {
            @Override
            public void onReqeustFail(int status, String failMsg) {

            }
        });
    }
}
