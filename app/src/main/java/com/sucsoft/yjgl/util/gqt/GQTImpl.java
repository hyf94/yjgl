package com.sucsoft.yjgl.util.gqt;

import android.app.Activity;
import android.content.Intent;
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



}