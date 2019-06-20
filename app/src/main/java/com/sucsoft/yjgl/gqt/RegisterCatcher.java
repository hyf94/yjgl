package com.sucsoft.yjgl.gqt;

import android.os.Handler;

import com.gqt.bean.RegisterListener;

public class RegisterCatcher implements RegisterListener {
	Handler mHandler = null;
	public RegisterCatcher(Handler hd){
		mHandler = hd;
	}
	public void unRegister(){
		mHandler = null;
	}
	@Override
	public void onRegisterSuccess() {
		if(mHandler != null){
			mHandler.sendMessage(mHandler.obtainMessage(0));
		}
	}

	@Override
	public void onRegisterFailded(String reason) {
		if(mHandler != null){
			mHandler.sendMessage(mHandler.obtainMessage(1,reason));
		}
	}

}
