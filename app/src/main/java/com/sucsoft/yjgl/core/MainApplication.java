package com.sucsoft.yjgl.core;

import android.app.Application;
import android.util.Log;

import com.gqt.helper.GQTHelper;
import com.sucsoft.yjgl.gqt.VideoCallHandler;
import com.sucsoft.yjgl.util.gqt.GQTRegister;
import com.tencent.smtt.sdk.QbSdk;

//import org.litepal.LitePal;


public class MainApplication extends Application {

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
            @Override
            public void onViewInitFinished(boolean arg0) {
                // TODO Auto-generated method stub
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                Log.i("app", " onViewInitFinished is " + arg0);
            }

            @Override
            public void onCoreInitFinished() {
                // TODO Auto-generated method stub
            }
        };
        //x5内核初始化接口
        QbSdk.initX5Environment(getApplicationContext(), cb);

        //初始化GQT sdk
        GQTHelper.getInstance().initAppContext(this);

        //GQT 初始化监听回调
        new VideoCallHandler(getApplicationContext()).startListening();
    }



    @Override
    public void onTerminate() {

        GQTHelper.getInstance().quit();
        super.onTerminate();
    }


}
