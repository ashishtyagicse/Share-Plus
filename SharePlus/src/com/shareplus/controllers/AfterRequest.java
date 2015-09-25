package com.shareplus.controllers;


import java.util.ArrayList;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.shareplus.R;
import com.shareplus.adapters.UserGroup_ListView_Adapter;
import com.shareplus.controllers.SharePlusWiFiService.LocalBinder;
import com.shareplus.models.UserGroupSelect_ListView_ItemData;


public class AfterRequest extends Activity {
	protected static final String TAG = "AfterRequest";
//	private Sharable ShareFile = new Sharable();
//	private boolean isMaster = false;
//	private Communicate connection = null;
//	
	
	
	public TextView T;
	public ProgressBar P;
	private UserGroup_ListView_Adapter Adapter;
	public int MenuFlag = 0;
	public ArrayList<UserGroupSelect_ListView_ItemData> UserList = new ArrayList<UserGroupSelect_ListView_ItemData>();
	
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
			SPService = LB.getService(TAG ,AfterRequest.this);
			ServiceStatus = true;	
			SPService.getSharingConnectionStatus();			
		}
	};
	
	@Override
	protected void onPause() {
		if(ServiceStatus){
			SPService.removeService(TAG);
			unbindService(SPServiceConnection);
		}
		super.onPause();
	}

	@Override
	protected void onResume() {		
		Intent I = new Intent(this, SharePlusWiFiService.class);
		bindService(I, SPServiceConnection, BIND_NOT_FOREGROUND);	
		super.onResume();
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_afterrequested);
		
		T = (TextView) findViewById(R.id.userconnectprogress);
		P = (ProgressBar) findViewById(R.id.connectprogressBar);
		T.setText("Connecting.....");
		UserList.clear();
		SetHandler(this);
	}
	
	private void SetHandler(Context context) {
		ListView UserListView = (ListView) findViewById(R.id.connectinguserlist);
		Adapter = new UserGroup_ListView_Adapter(UserList, context,	4);
		UserListView.setAdapter(Adapter);
	}
	
	
	public void WiFiPeersStatus(ArrayList<WifiP2pDevice> List, ArrayList<String> IPList) {
		UserList.clear();
		int i = 0;
		for (WifiP2pDevice device : List) {
			UserGroupSelect_ListView_ItemData data = new UserGroupSelect_ListView_ItemData();
			data.setDevice(device);
			data.setDeviceName(device.deviceName);
			if(IPList.get(i).equals("Connected")){
				data.setDeviceIP("Connected");
			}else if(IPList.get(i).equals("Rejected")){
				data.setDeviceIP("Connection Refused");  
			}else {
				data.setDeviceIP("Connecting....");
			} 
			UserList.add(data);
			Adapter.notifyDataSetChanged();
			i++;
		}
	}

	private void StartShareProcess() {
		if(ServiceStatus){
			SPService.stopSharingConnectionStatus();
			SPService.StartDataShare();
		}
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onPrepareOptionsMenu(menu);
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.afterrequest_actionbar, menu);
		if (MenuFlag == 0) {
			menu.findItem(R.id.startconnectedshare).setVisible(true);
		} else {
			menu.findItem(R.id.startconnectedshare).setVisible(false);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.startconnectedshare) {
			StartShareProcess();
		}
		return super.onOptionsItemSelected(item);
	}	
	

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if(ServiceStatus){			
				SPService.KillServer();
			}
		}
		return super.onKeyDown(keyCode, event);
	}
}
		
	