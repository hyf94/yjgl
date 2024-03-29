package com.sucsoft.yjgl.util.gqt;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.gqt.bean.CallType;
import com.gqt.helper.CallEngine;
import com.gqt.helper.GQTHelper;
import com.sucsoft.yjgl.activity.CallActivity;
import com.sucsoft.yjgl.activity.GQTActivity;
import com.sucsoft.yjgl.activity.TalkBackActivity;
import com.sucsoft.yjgl.core.AppManager;
import com.sucsoft.yjgl.core.jsbridge.Callback;
import com.sucsoft.yjgl.core.jsbridge.IBridge;
import com.tencent.smtt.sdk.WebView;

import org.json.JSONObject;

public class GQTImpl implements IBridge{
    private static GQT gqt =null;
    private static CallEngine callEngine =GQTHelper.getInstance().getCallEngine();
    public static void speak(WebView webView, JSONObject param, final Callback callback){
        gqt = new GQT();
        gqt.speak(webView.getContext(),callback);
    }
    public static void stopSpeak(WebView webView, JSONObject param, final Callback callback){
        gqt = new GQT();
        gqt.stopSpeak(webView.getContext(),callback);
    }
    public static void call(WebView webView, JSONObject param, final Callback callback){
        callEngine =GQTHelper.getInstance().getCallEngine();
        String number = param.optString("number");
        String type = param.optString("type");
        if (type.equals("video")){
            callEngine.makeCall(CallType.VIDEOCALL, number);
        }else {
            callEngine.makeCall(CallType.VOICECALL, number);
        }
        Log.i("call",number);

    }


    public static void start(WebView webView, JSONObject param, final Callback callback){
        Class activity;
        if (param.optString("activity").equals("call")){
            activity = CallActivity.class;
        }else {
            activity = TalkBackActivity.class;
        }
        AppManager.getAppManager().currentActivity().startActivity(new Intent(AppManager.getAppManager().currentActivity(), activity));
    }


    public static void registerGQT(WebView webView, JSONObject param, final Callback callback){
        GQTRegister gqtRegister;
        String IP = "39.106.217.160";
//        String IP = "222.85.128.185";
        int PORT = 7080;

        String username = param.optString("username");
        String password = param.optString("password");
        gqtRegister = new GQTRegister(AppManager.getAppManager().currentActivity());
        gqtRegister.init(username, password, IP, PORT);
//        gqtRegister.init("800013", "800013", "39.106.217.160", PORT);
    }

    public static void callPhone(WebView webView, JSONObject param, final Callback callback){
        String phoneNumber = param.optString("phoneNumber");
        Uri data = Uri.parse(phoneNumber);
        Intent intent = new Intent(Intent.ACTION_CALL,data);
        AppManager.getAppManager().currentActivity().startActivity(intent);
        if (ActivityCompat.checkSelfPermission(AppManager.getAppManager().currentActivity(), android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            AppManager.getAppManager().currentActivity().startActivity(intent);
            //这个超连接,java已经处理了，webview不要处理
        }else{
            //申请权限
            ActivityCompat.requestPermissions(AppManager.getAppManager().currentActivity(), new String[]{Manifest.permission.CALL_PHONE},1);
        }
    }


}
