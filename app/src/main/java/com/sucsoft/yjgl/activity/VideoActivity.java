package com.sucsoft.yjgl.activity;

import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gqt.bean.CallType;
import com.gqt.helper.CallEngine;
import com.gqt.helper.GQTHelper;
import com.sucsoft.yjgl.R;

public class VideoActivity extends BaseActivity implements View.OnClickListener {

    private Button hangup_button, switch_button, rotate_button;
    private TextView textView;
    private SurfaceView meView, heView;
    private CallEngine callEngine;
    private String number;
    private int cameraOrientation = 180;
    private boolean isCameraFront = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        //标题栏
        getSupportActionBar().setTitle(R.string.video_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //初始化组件
        hangup_button = findViewById(R.id.video_hangup);
        switch_button = findViewById(R.id.video_switch);
        rotate_button = findViewById(R.id.video_rotate);
        textView = findViewById(R.id.video_textview);
        meView = findViewById(R.id.video_me_surface);
        heView = findViewById(R.id.video_he_surface);

        //设置组件
        number = getIntent().getStringExtra("number");
        textView.setText(number);
        hangup_button.setOnClickListener(this);
        switch_button.setOnClickListener(this);
        rotate_button.setOnClickListener(this);

        //获取call engine
        callEngine = GQTHelper.getInstance().getCallEngine();
        callEngine.setDisplayOrientation(cameraOrientation);

        //初始化surface view
        initSurfaceView();
    }

    /***
     * 初始化surfaceview 需要在created之后调用startvideo
     */
    private void initSurfaceView(){
        heView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Log.i("M4", "surfaceDestroyed: ");
            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                //设置接收对端H264回调接口
                GQTHelper.getInstance().getCallEngine().startVideo(meView, heView,false);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                       int height) {
                Log.i("M4", "surfaceChanged: ");
            }
        });
    }

    @Override
    public void onClick(View v) {
        Log.i("M4", "onClick: " + v.getId());
        switch (v.getId()){

            /*
             * 挂断按钮，停止video，通知服务器已挂断
             * */
            case R.id.video_hangup:
                Log.i("M4", "video_hangup: ------------------------------------");
                callEngine.stopVideo();
                callEngine.hangupCall(CallType.VIDEOCALL, number);
                break;

            /*
             * 切换前后置摄像头，需要判断当前使用的是哪一个
             * */
            case R.id.video_switch:
                Log.i("M4", "video_switch: ------------------------------------");
                if (isCameraFront) {
                    callEngine.switchCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
                    isCameraFront = false;
                } else {
                    callEngine.switchCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
                    isCameraFront = true;
                }
                break;

            /*
             * 旋转画面
             * */
            case R.id.video_rotate:
                Log.i("M4", "video_rotate: ------------------------------------");
                cameraOrientation = cameraOrientation + 90 == 360 ? 0 : cameraOrientation + 90;
                Log.i("cameraOrientation：",cameraOrientation+"");
                callEngine.setDisplayOrientation(cameraOrientation);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        callEngine.stopVideo();
        callEngine.hangupCall(CallType.VIDEOCALL, number);
    }
}
