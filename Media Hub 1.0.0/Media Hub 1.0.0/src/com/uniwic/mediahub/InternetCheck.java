package com.uniwic.mediahub;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class InternetCheck {
	boolean internetflag;
	Context context;
	public InternetCheck(Context context) {
		this.context=context;
	}
	public boolean checkNetworkStatus() {

		try {
			final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
			NetworkInfo ni = connMgr.getActiveNetworkInfo();
			final android.net.NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			final android.net.NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

			if (wifi.isAvailable() && wifi.isConnected()) {
			    internetflag = true;
			
			} else if (mobile.isAvailable() && mobile.isConnected()) {
				internetflag = true;
			
			} else if (ni != null && ni.isAvailable() && ni.isConnected()) {
				internetflag = true;
			
			} else {
				internetflag = false;
			}
		}catch(Exception e){
		}
		return internetflag;
	}
	

}
