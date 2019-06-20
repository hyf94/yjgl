package com.sucsoft.yjgl.util;

import android.widget.Toast;

import com.sucsoft.yjgl.core.jsbridge.Callback;
import com.sucsoft.yjgl.core.jsbridge.IBridge;
import com.tencent.smtt.sdk.WebView;

import org.json.JSONException;
import org.json.JSONObject;

public class TestImpl implements IBridge {

    public static void test(WebView webView, JSONObject param, final Callback callback){

        //解析参数
        String message = param.optString("msg");

        //执行native方法
        Toast.makeText(webView.getContext(), message, Toast.LENGTH_SHORT).show();

        //回调，并传参
        if (null != callback) {
            try {
                JSONObject object = new JSONObject();
                object.put("status", "1");
                object.put("message", "成功！");

                callback.apply(object);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void threadTest(final WebView webView, final JSONObject param, final Callback callback){

        new Thread(new Runnable() {
            @Override
            public void run() {
            try {
                //模拟异步操作
                Thread.sleep(3000);

                //执行native 方法
                Toast.makeText(webView.getContext(), param.optString("msg"), Toast.LENGTH_SHORT).show();

                //回调，并传参
                if (null != callback) {
                    JSONObject object = new JSONObject();
                    object.put("status", "1");
                    object.put("message", "成功！");

                    callback.apply(object);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            }
        }).start();
    }

}
