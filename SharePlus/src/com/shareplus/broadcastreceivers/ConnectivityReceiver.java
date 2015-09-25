package com.shareplus.broadcastreceivers;

import com.shareplus.controllers.SharePlusWiFiService;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.widget.Toast;

public class ConnectivityReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		WifiManager connMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);          
        if (connMgr != null && connMgr.isWifiEnabled()){            
        	boolean ServiceRunning = false;
    		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
    	        if (SharePlusWiFiService.class.getName().equals(service.service.getClassName())) {
    	        	ServiceRunning = true;	        	
    	        }
    	    }
        	if(!ServiceRunning){        	
        		Intent StartService = new Intent(context, SharePlusWiFiService.class);
        		StartService.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        		context.startService(StartService);
        	}
        } 	
	}
}
