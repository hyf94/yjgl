package com.sucsoft.yjgl.gqt;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.gqt.bean.CallType;
import com.gqt.helper.CallEngine;
import com.gqt.helper.GQTHelper;
import com.sucsoft.yjgl.activity.AudioActivity;
import com.sucsoft.yjgl.activity.VideoActivity;
import com.sucsoft.yjgl.core.AppManager;

public class VideoCallHandler {

    private final String TAG = "M4";

    private Context context;
    private CallEngine callEngine;
    private AlertDialog dialog;
    private ProgressDialog progressDialog;

    public VideoCallHandler(Context context){
        this.context = context;
    }

    public CallEngine startListening(){
        callEngine = GQTHelper.getInstance().getCallEngine();
        callEngine.registerCallListener(new VideoCallListener(handler));
        return callEngine;
    }

    /***
     * call engine handler，拨打、接听之类的回调
     */
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                //对方接听
                case 1:
                    onReceived(msg.obj.toString(), msg.arg1);
                    break;
                //对方拨打
                case 2:
                    onCallIn(msg.obj.toString(), msg.arg1);
                    break;
                //onCallOutGoing
                case 3:
                    onCallOut(msg.obj.toString(), msg.arg1);
                    break;
                //空闲状态（挂断）
                case 0:
                    onHangUp(msg.obj.toString());
                    break;
                //对方拒绝
                case -1:
                    onRefused(msg.obj.toString());
                    break;
                //发生错误
                case -2:
                    onError(msg.obj.toString());
                    break;
            }
        }
    };

    /***
     * 对方接听通话回调
     * @param number 对方number
     * @param type 通话类型
     */
    private void onReceived(String number, int type){

        Activity currentActivity = AppManager.getAppManager().currentActivity();

        Log.i(TAG, "onReceived: currentActivity.getTitle: " + currentActivity.getTitle());
        Log.i(TAG, "onReceived: currentActivity.getLocalClassName: " + currentActivity.getLocalClassName());
        Log.i(TAG, "onReceived: currentActivity.getPackageName: " + currentActivity.getPackageName());
        Log.i(TAG, "onReceived: currentActivity.getResources.getClass.getName: " + currentActivity.getResources().getClass().getName());

        if(progressDialog != null){
            progressDialog.dismiss();
        }

        switch (type){

            //视频通话，跳转activity
            case CallType.VIDEOCALL:
                Intent videoIntent = new Intent(currentActivity, VideoActivity.class);
                videoIntent.putExtra("number", number);
                currentActivity.startActivity(videoIntent);
                break;

            //语音通话
            case CallType.VOICECALL:
                Intent audioIntent = new Intent(currentActivity, AudioActivity.class);
                audioIntent.putExtra("number", number);
                currentActivity.startActivity(audioIntent);
                break;

            //会议
            case CallType.CONFERENCE:
                break;

            //广播
            case CallType.BROADCAST:
                break;
            default:
        }
    }

    private void onHangUp(String number){
        Toast.makeText(context,"通话结束", Toast.LENGTH_LONG).show();
        if(progressDialog != null){
            progressDialog.cancel();
        }
    }

    private void onRefused(String number){
        Log.i(TAG, "onRefused: ");
        Toast.makeText(context,"对方拒绝通话", Toast.LENGTH_LONG).show();
        if(progressDialog != null){
            progressDialog.cancel();
        }
    }

    private void onError(String number){
        Toast.makeText(context,"通话发生错误", Toast.LENGTH_LONG).show();
        if(dialog != null){
            dialog.dismiss();
        }
        if(progressDialog != null){
            progressDialog.cancel();
        }
    }

    /***
     * 挂掉对方通话
     * @param number 对方number
     * @param type 通话类型
     */
    private void refuse(String number, int type){
        callEngine.hangupCall(type, number);
    }

    /***
     * 回应通话
     * @param number 对方number
     * @param type 通话类型
     */
    private void answer(String number, int type){
        callEngine.answerCall(type, number);
    }

    /***
     * 对方拨打
     * @param string 对方number
     * @param type 通话类型
     */
    private void onCallIn(final String string, final int type){
        Log.i(TAG, "onCallIn: ======================================================");
        AlertDialog.Builder builder = new AlertDialog.Builder(AppManager.getAppManager().currentActivity());

        builder.setTitle("通话");
        builder.setMessage("来自" + string + "的通话请求" );
        builder.setPositiveButton("接听", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface iDialog, int which) {
                iDialog.dismiss();
                answer(string, type);
            }
        });
        builder.setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface iDialog, int which) {
                iDialog.dismiss();
                refuse(string, type);
            }
        });
        dialog = builder.show();
    }

    /***
     * 开始拨打对方
     * @param string 对方number
     * @param type 通话类型
     */
    private void onCallOut(final String string, final int type){
        Log.i(TAG, "onCallOut: ======================================================");
        progressDialog = new ProgressDialog(AppManager.getAppManager().currentActivity());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置进度条的形式为圆形转动的进度条
        progressDialog.setCancelable(false);// 设置是否可以通过点击Back键取消
        progressDialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
        progressDialog.setTitle("拨号中");
        progressDialog.setMessage("等待对方接听："+ string);

        // 监听cancel事件
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface iDialog) {
                // TODO Auto-generated method stub
                callEngine.hangupCall(type, string);
            }
        });
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消拨号",
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface iDialog, int which) {
                    // TODO Auto-generated method stub
                    iDialog.cancel();
                }
            });
        progressDialog.show();
    }

}
