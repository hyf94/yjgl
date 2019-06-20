package com.sucsoft.yjgl.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.sucsoft.yjgl.core.AppManager;

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //将Activity实例添加到AppManager的堆栈
        AppManager.getAppManager().addActivity(this);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //将Activity实例从AppManager的堆栈中移除
        AppManager.getAppManager().finishActivity(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
