package com.sucsoft.yjgl.gqt;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class GQTUtils {
	/**
	 * 检测网络是否连接
	 * */
	public static boolean isNetworkAvailable(Context context) {
		try {
			ConnectivityManager connectivity = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {

				NetworkInfo info = connectivity.getActiveNetworkInfo();
				if (info != null && info.isConnected()) {

					if (info.getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}
}
