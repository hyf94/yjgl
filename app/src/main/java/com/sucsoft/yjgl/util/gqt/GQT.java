package com.sucsoft.yjgl.util.gqt;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.gqt.helper.Constant;
import com.gqt.helper.GQTHelper;
import com.gqt.helper.GroupEngine;
import com.gqt.helper.RegisterEngine;
import com.sucsoft.yjgl.MainActivity;
import com.sucsoft.yjgl.core.jsbridge.Callback;
import com.sucsoft.yjgl.gqt.RegisterCatcher;

import org.json.JSONObject;

import static com.gqt.sipua.ui.Settings.context;

public class GQT {

    private Context context = null;
    private GroupEngine groupEngine = null;
    private RegisterEngine registerEngine = null;
    private RegisterCatcher rcatcher;
    public void speak(Context context, final Callback callback){
        Log.i("speak","--------说话");
        groupEngine = GQTHelper.getInstance().getGroupEngine();    //获取对讲模块实例
        groupEngine.makeGroupCall(true);
        //执行native方法
        Toast.makeText(context, "说话", Toast.LENGTH_SHORT).show();

        //回调，并传参
        if (null != callback) {
            try {
                JSONObject object = new JSONObject();
                object.put("status", "1");
                object.put("message", "说话！");

                callback.apply(object);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void stopSpeak(Context context, final Callback callback){
        Log.i("stopSpeak","--------暂停说话");
        groupEngine = GQTHelper.getInstance().getGroupEngine();    //获取对讲模块实例
        groupEngine.makeGroupCall(false);
        //执行native方法
        Toast.makeText(context, "暂停说话", Toast.LENGTH_SHORT).show();

        //回调，并传参
        if (null != callback) {
            try {
                JSONObject object = new JSONObject();
                object.put("status", "1");
                object.put("message", "暂停说话！");

                callback.apply(object);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
