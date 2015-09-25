package com.shareplus.controllers;


import java.util.ArrayList;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.os.Bundle;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.shareplus.R;
import com.shareplus.adapters.UserGroup_ListView_Adapter;
import com.shareplus.controllers.SharePlusWiFiService.LocalBinder;
import com.shareplus.models.Browse_ListView_ItemData;
import com.shareplus.models.Sharable;
import com.shareplus.models.UserGroupSelect_ListView_ItemData;

public class UserGroupSelect extends Activity {

	public static final String TAG = "UserGroupSelect";
	private ArrayList<Browse_ListView_ItemData> FileList = new ArrayList<Browse_ListView_ItemData>();
	private UserGroup_ListView_Adapter Adapter;
	private int SelectModeFlag = 0;
	public Sharable ShareFile = new Sharable();
	public int MenuFlag = 0;
	private int MenuFlag1;
	public TextView T;
	public ProgressBar P;
	public int FlagRequested = 0;
	public boolean isMaster = false;
	public Intent CallingIntent = null;
	private ArrayList<UserGroupSelect_ListView_ItemData> UserList = new ArrayList<UserGroupSelect_ListView_ItemData>();
	
	
	
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
			SPService = LB.getService(TAG ,UserGroupSelect.this);
			ServiceStatus = true;
			SPService.RequestPeers(true);
			//SPService.peerRequested = true;
		}
	};
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		boolean ServiceRunning = false;
		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (SharePlusWiFiService.class.getName().equals(service.service.getClassName())) {
	        	ServiceRunning = true;	        	
	        }
	    }
	    if (!ServiceRunning) {
	    	Thread t = new Thread(){
	    		public void run(){
	    			Intent StartService = new Intent(getApplicationContext(), SharePlusWiFiService.class);
	    			StartService.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    			getApplicationContext().startService(StartService);
	    		}
	    	};
	    	t.start();
		}
		
		setContentView(R.layout.activity_usergroupselect);
		T = (TextView) findViewById(R.id.usersearchprogress);
		P = (ProgressBar) findViewById(R.id.progressBar);
		T.setText("Serching.....");
		UserList.clear();
		Intent I = getIntent();
		ShareFile = (Sharable) I.getParcelableExtra("SHAREITEMS");		
		if(ShareFile != null){
			if (ShareFile.getShareMode().equals("SINGLE")) {
				SelectModeFlag = 0;
			} else if (ShareFile.getShareMode().equals("MULTIPLE")) {
				SelectModeFlag = 1;
			} else if (ShareFile.getShareMode().equals("BROADCAST")) {
				SelectModeFlag = 2;
			}
		}
		MenuFlag = 1;
		invalidateOptionsMenu();
		SetHandler(this);
		CallingIntent = new Intent(this,AfterRequest.class);
		CallingIntent.putExtra("SHAREITEMS", I.getParcelableExtra("SHAREITEMS"));		
	}
	
	private void SetHandler(Context context) {
		ListView UserListView = (ListView) findViewById(R.id.userlist);
		Adapter = new UserGroup_ListView_Adapter(UserList, context,
				SelectModeFlag);
		UserListView.setAdapter(Adapter);
		UserListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ViewGroup VG = (ViewGroup) view;
				View icon = VG.getChildAt(0);
				icon.performClick();
			}
		});
	}

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
		bindService(I, SPServiceConnection,BIND_NOT_FOREGROUND);		
		super.onResume();
	}

	public void CheckWifi(boolean Flag) {
		if(!Flag){
			Toast.makeText(
					this,
					"Please Turn on the WiFi and then return to the application",
					Toast.LENGTH_SHORT).show();
			UserList.clear();
			Adapter.notifyDataSetChanged();
			this.finish();
		}
	}

	public void stoppeer() {
		if(ServiceStatus){
			SPService.StopPeerRequest();
		}
		if(UserList.size() != 0){
			T.setText("Found following wifi users");
		} else {
			T.setText("No WiFi User found!");
		}
		MenuFlag1 = 0;
		invalidateOptionsMenu();
	}

	public void WiFiPeersDetected(WifiP2pDeviceList List) {
		UserList.clear();
		T.setText("Searchig...");
		if (List == null || List.getDeviceList().size() < 1) {
			MenuFlag = 1;
			invalidateOptionsMenu();
		} else {
			for (WifiP2pDevice device : List.getDeviceList()) {
				UserGroupSelect_ListView_ItemData data = new UserGroupSelect_ListView_ItemData();
				data.setDevice(device);
				data.setDeviceName(device.deviceName);
				data.setDeviceIP(device.deviceAddress);
				UserList.add(data);
				Adapter.notifyDataSetChanged();
			}
		}
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.usergroupselect_actionbar, menu);
		if (MenuFlag == 0) {
			menu.findItem(R.id.startshare).setVisible(true);
		} else {
			menu.findItem(R.id.startshare).setVisible(false);
		}
		if (MenuFlag1 == 1) {
			menu.findItem(R.id.stoppeersdiscovery).setIcon(
					R.drawable.ic_action_remove);
			menu.findItem(R.id.stoppeersdiscovery).setTitle("STOP");
		} else {
			menu.findItem(R.id.stoppeersdiscovery).setIcon(
					android.R.drawable.ic_menu_search);
			menu.findItem(R.id.stoppeersdiscovery).setTitle("SEARCH");
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.stoppeersdiscovery) {
			if (item.getTitle().equals("STOP")) {
				stoppeer();
			} else {
				if(ServiceStatus){
					SPService.RequestPeers(true);
					MenuFlag1 = 1;
					invalidateOptionsMenu();
				}
			}
		}
		if (id == R.id.startshare) {
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
	
	private void StartShareProcess() {
		if (Adapter.SelectedDevices.size() != 0) {
			FlagRequested = 1;
			if(ServiceStatus){
				SPService.StopPeerRequest();
				SPService.StartShareRequest(Adapter.SelectedDevices, ShareFile.getShareMode());
			}
			isMaster = true;
		}
	}		

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onPrepareOptionsMenu(menu);
	}
}
