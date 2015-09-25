package com.shareplus.controllers;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import com.shareplus.R;
import com.shareplus.controllers.SharePlusWiFiService.LocalBinder;

public class SPAlertDialog extends Activity{
	private SharePlusWiFiService SPService = null; 
	private boolean ServiceStatus = false;
	public static final String TAG = "SPSlertDialog";
	
	private ServiceConnection SPServiceConnection = new ServiceConnection() {		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			SPService = null;
			ServiceStatus = false;
		}		
		@Override
		public void onServiceConnected(ComponentName name, IBinder SPbinder) {
			LocalBinder LB = (LocalBinder) SPbinder;
			SPService = LB.getService(TAG ,SPAlertDialog.this);
			ServiceStatus = true;
		}
	};
	
	@Override
	protected void onPause() {
		if(ServiceStatus){			
			unbindService(SPServiceConnection);
		}
		super.onPause();
	}

	@Override
	protected void onResume() {		
		Intent I = new Intent(this, SharePlusWiFiService.class);
		bindService(I, SPServiceConnection, 0);	
		super.onResume();
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Toast.makeText(this, "inside dialog", Toast.LENGTH_SHORT).show();
		Builder builder = new AlertDialog.Builder(this);
	    builder.setTitle("Connect Share+");
	    builder.setMessage("Allow Remote share plus to connect to this device?");
	    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	        	SPService.confirmConnection(true);
	            dialog.dismiss();
	            SPAlertDialog.this.finish();
	        }
	    });
	    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
	        @Override
	        public void onClick(DialogInterface dialog, int which) {
	        	SPService.confirmConnection(false);
	            dialog.dismiss();
	            SPAlertDialog.this.finish();
	        }
	    });
	    AlertDialog alert = builder.create();
	    alert.setIcon(R.drawable.ic_launcher);
	    alert.show();
	}
}
