package com.sucsoft.yjgl.gqt;


import android.os.Handler;
import android.util.Log;

import com.gqt.bean.CallListener;


public class VideoCallListener implements CallListener {

    Handler handler;

    public VideoCallListener(Handler handler){
        this.handler = handler;
    }

    private static final String TAG = "M4";

    @Override
    public void onCallOutGoing(int i, String s) {
        Log.i(TAG, "开始拨打: " + s);
        handler.sendMessage(handler.obtainMessage(3, i, 0, s));
    }

    @Override
    public void onCallInComing(int i, String s) {
        Log.i(TAG, "对方拨打: " + s);
        handler.sendMessage(handler.obtainMessage(2, i, 1, s));
    }

    @Override
    public void onCallInCall(int i, String s) {
        Log.i(TAG, "对方接听: " + s);
        handler.sendMessage(handler.obtainMessage(1, i, 1, s));
    }

    @Override
    public void onCallIDLE() {
        Log.i(TAG, "空闲状态（挂断）: ");
        handler.sendMessage(handler.obtainMessage(0, -1, -1, ""));
    }

    @Override
    public void onCallError(int i, String s) {
        Log.i(TAG, "onCallError: " + s);
        handler.sendMessage(handler.obtainMessage(-2, i, -1, s));
    }

    @Override
    public void onCallRefused(int i) {
        Log.i(TAG, "onCallRefused: ");
        handler.sendMessage(handler.obtainMessage(-1, i, 0, ""));
    }
}
