package com.shareplus.controllers;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.view.KeyEvent;
import android.widget.ImageView;

import com.shareplus.R;
import com.shareplus.controllers.SharePlusWiFiService.LocalBinder;

public class PlayActivity extends Activity {
	protected static final String TAG = "Play";

	
	private SharePlusWiFiService SPService = null; 
	private boolean ServiceStatus = false;


	private ServiceConnection SPServiceConnection = new ServiceConnection() {		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			SPService = null;
			ServiceStatus = false;
		}		
		@Override
		public void onServiceConnected(ComponentName name, IBinder SPbinder) {
			LocalBinder LB = (LocalBinder) SPbinder;
			SPService = LB.getService(TAG ,PlayActivity.this);
			ServiceStatus = true;	
			
		}
	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play);
		Intent I = getIntent();
		if (I.hasExtra("ShareType") && I.getStringExtra("ShareType").equals("IMAGE")) {
			ImageView IV = (ImageView) findViewById(R.id.PlayImage);
			int DeviceID = Integer.parseInt(I.getStringExtra("DeviceID"));
			String FilePath = I.getStringExtra("FilePath");
			int NoOfDevices = I.getIntExtra("NoOfDevices", 2);
			Bitmap originalBm = BitmapFactory.decodeFile(FilePath); 
			Bitmap BM = Bitmap.createBitmap(originalBm ,(originalBm.getWidth() / NoOfDevices) * (DeviceID -1),0 ,(originalBm.getWidth() / NoOfDevices) ,originalBm.getHeight());
			IV.setImageBitmap(BM);
		}
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if(ServiceStatus){
				if (SPService.isMaster) {
					SPService.PlayShutDownCommand();
					SPService.KillServer();	
				} else {
					SPService.PlayShutDownCommand();
					SPService.KillClient();
				}	
				SPService.PlayShutDown();
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	

	@Override
	protected void onPause() {
		if(ServiceStatus){
			if(ServiceStatus){
				if (SPService.isMaster) {
					SPService.PlayShutDownCommand();
					SPService.KillServer();	
				} else {
					SPService.PlayShutDownCommand();
					SPService.KillClient();
				}	
				SPService.PlayShutDown();
			}
			SPService.removeService(TAG);
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
}
