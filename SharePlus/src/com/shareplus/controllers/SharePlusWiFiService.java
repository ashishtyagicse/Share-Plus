package com.shareplus.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.shareplus.broadcastreceivers.SharePlusWiFiBroadcast;
import com.shareplus.models.Client;
import com.shareplus.models.Communicate;
import com.shareplus.models.Server;
import com.shareplus.models.Sharable;

public class SharePlusWiFiService extends Service {

	public static final String TAG = "SharePlusWiFiService";
	public static final String TAGUGS = "UserGroupSelect";
	public static final String TAGAR = "AfterRequest";
	public static final String TAGPLAY = "Play";
	public static final String TAGAB = "SPSlertDialog";
	private WifiP2pManager WiFiManager;
	private Channel WiFiChannel;
	private BroadcastReceiver WiFiReceiver;
	private IntentFilter Filter;
	public static boolean isWiFiOn = false;
	public static boolean isPeerDiscoveryOn = false;
	private Map<String, Context> clients = new ConcurrentHashMap<String, Context>();
	private final IBinder SPbinder = new LocalBinder();
	public boolean peerRequested = false;
	public ArrayList<WifiP2pDevice> SharingWith = new ArrayList<WifiP2pDevice>();
	public ArrayList<String> SharingIp = new ArrayList<String>();
	public ArrayList<String> SharingDeviceStatus = new ArrayList<String>();
	public String ShareMode = "SINGLE";
	public static boolean isClientCreated = false;
	public Client ClientSocket = null;
	private Server ServerSocket = null;
	public static boolean isMaster = false;
	private boolean startShareConnectUpdate = false;
	public String deviceId = "1";
	public Sharable ShareFile = new Sharable();
	public String FilePath = "";
	public String PlayType = "NONPLAYTYPE";
	public int NoOfDevices = 2;

	@Override
	public void onCreate() {
		Toast.makeText(this, "Service created do initial work",
				Toast.LENGTH_SHORT).show();
		super.onCreate();
		
		Filter = new IntentFilter();
		Filter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
		Filter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
		Filter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
		Filter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
		WiFiManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
		WiFiChannel = WiFiManager.initialize(this, getMainLooper(), null);
		WiFiReceiver = new SharePlusWiFiBroadcast(WiFiManager, WiFiChannel,
				this);
		registerReceiver(WiFiReceiver, Filter);
		TurnPeerDiscoveryOn();
	}	
	
	public void WiFiStatus(boolean Status) {
		isWiFiOn = Status;
		if (!Status) {	
			isPeerDiscoveryOn = false;
			peerRequested = false;
			isMaster = false;
			isClientCreated = false;
			startShareConnectUpdate = false;
			KillClient();
			KillServer();
			if (((UserGroupSelect)clients.get(TAGUGS)) != null) {
				Toast.makeText(getApplicationContext(), "Wifi Off Check Wifi", Toast.LENGTH_LONG).show();
				((UserGroupSelect)clients.get(TAGUGS)).finish();
			}
			if (((UserGroupSelect)clients.get(TAGAR)) != null) {
				Toast.makeText(getApplicationContext(), "Wifi Off Check Wifi", Toast.LENGTH_LONG).show();
				((UserGroupSelect)clients.get(TAGAR)).finish();
			}
			if (((UserGroupSelect)clients.get(TAGPLAY)) != null) {
				Toast.makeText(getApplicationContext(), "Wifi Off Check Wifi", Toast.LENGTH_LONG).show();
				((UserGroupSelect)clients.get(TAGPLAY)).finish();
			}
		} else {
			isPeerDiscoveryOn = false;
			TurnPeerDiscoveryOn();
		}
	}

	public void TurnPeerDiscoveryOn() {
		if (!isPeerDiscoveryOn && isWiFiOn) {
			Toast.makeText(this, "discovery truning on", Toast.LENGTH_SHORT)
					.show();
			WiFiManager.discoverPeers(WiFiChannel,
					new WifiP2pManager.ActionListener() {
						@Override
						public void onSuccess() {
							isPeerDiscoveryOn = true;
							if (clients.get(TAGUGS) != null) {
								((UserGroupSelect) clients.get(TAGUGS)).T
										.setText("Searching....");
								((UserGroupSelect) clients.get(TAGUGS)).P
										.setVisibility(View.VISIBLE);
								Toast.makeText(SharePlusWiFiService.this,
										"Peer discovery Success",
										Toast.LENGTH_SHORT).show();
							}
						}

						@Override
						public void onFailure(int reason) {
							isPeerDiscoveryOn = false;
							if (clients.get(TAGUGS) != null) {
								((UserGroupSelect) clients.get(TAGUGS)).T
										.setText("Peer discovery Failed!");
								((UserGroupSelect) clients.get(TAGUGS)).P
										.setVisibility(View.INVISIBLE);
								Toast.makeText(
										SharePlusWiFiService.this,
										"Peer discovery Failed! Check Connection",
										Toast.LENGTH_SHORT).show();
							}
						}
					});
		}
	}

	public void RequestPeers(boolean flag){
		if (flag) {
			peerRequested = true;
		}
		if (isWiFiOn) {
			if (isPeerDiscoveryOn) {
				WiFiManager.requestPeers(WiFiChannel, WiFiPeerListListener);						
			} else {
				TurnPeerDiscoveryOn();
			}
		}
		
	}

	public void StopPeerRequest() {
		peerRequested = false;
	}

	public PeerListListener WiFiPeerListListener = new PeerListListener() {
		@Override
		public void onPeersAvailable(WifiP2pDeviceList peers) {
			if (peerRequested) {
				if (clients.get(TAGUGS) != null) {
					((UserGroupSelect) clients.get(TAGUGS))
							.WiFiPeersDetected(peers);
				}
			}
		}
	};

	public void StartShareRequest(ArrayList<WifiP2pDevice> Share, String Mode) {
		SharingWith = Share;
		for (WifiP2pDevice D : Share) {
			SharingIp.add("0");
			SharingDeviceStatus.add("Connecting");
		}
		ShareFile = ((UserGroupSelect) clients.get(TAGUGS)).ShareFile;
		if (ServerSocket == null && isWiFiOn) {
			ServerSocket = new Server("8881", this);
			ServerSocket.Start();
		}
		isMaster = true;
		Thread t = new Thread() {
			public void run() {
				Intent I = new Intent(getApplicationContext(),
						AfterRequest.class);
				I.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				getApplicationContext().startActivity(I);
			}
		};
		t.start();

		if (Mode != null || Mode != "") {

			if (Mode.equals("BROADCAST")) {
				ShareMode = "BROADCAST";
			} else {
				ShareMode = "MULTIPLE";
			}
			if (Mode.equals("SINGLE")) {
				ShareMode = "SINGLE";
			}
			for (WifiP2pDevice device : SharingWith) {
				WifiP2pConfig config = new WifiP2pConfig();
				config.deviceAddress = device.deviceAddress;
				config.groupOwnerIntent = 15;
				config.wps.setup = WpsInfo.PBC;
				WiFiManager.cancelConnect(WiFiChannel, null);
				WiFiManager.connect(WiFiChannel, config,
						new WifiP2pManager.ActionListener() {
							@Override
							public void onSuccess() {
								Toast.makeText(SharePlusWiFiService.this,
										"Connection Requested",
										Toast.LENGTH_SHORT).show();
							}

							@Override
							public void onFailure(int reason) {
								Toast.makeText(SharePlusWiFiService.this,
										"Connection Failed", Toast.LENGTH_SHORT)
										.show();
							}
						});

			}
		}
	}

	public void confirmConnection(boolean Yes) {
		if (Yes) {
			ClientSocket.ClientChannel.sendCommand("connectYes");
		} else {
			ClientSocket.ClientChannel.sendCommand("connectNo");
			KillClient();
		}
	}

	public boolean askConnect() {
		Thread t = new Thread() {
			public void run() {
				Intent I = new Intent(getApplicationContext(), SPAlertDialog.class);
				I.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				getApplicationContext().startActivity(I);
			}
		};
		t.start();
		return false;
	}

	public String getMacAddress() {
		WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = manager.getConnectionInfo();
		return info.getMacAddress().substring(3);
	}

	public void setMacToIpAddress(String Mac, String IP) {
		int i = 0;
		for (WifiP2pDevice D : SharingWith) {
			if (D.deviceAddress.substring(3).toLowerCase().equals(Mac.toLowerCase())) {
				SharingIp.set(i, IP);
				break;
			}
			i++;
		}
	}

	public void getSharingConnectionStatus() {
		startShareConnectUpdate = true;
		if (clients.get(TAGAR) != null) {
			((AfterRequest) clients.get(TAGAR)).WiFiPeersStatus(SharingWith,
					SharingIp);
		}
	}

	public void stopSharingConnectionStatus() {
		startShareConnectUpdate = false;
	}

	public void setDeviceConnected(String IP) {
		if(SharingIp != null && SharingIp.indexOf(IP) > -1 && SharingDeviceStatus!= null){
			SharingDeviceStatus.set(SharingIp.indexOf(IP), "Connected");				
			if (startShareConnectUpdate) {
				if (clients.get(TAGAR) != null) {					
					((AfterRequest) clients.get(TAGAR)).runOnUiThread(new Runnable() {
					    public void run() {
					    	((AfterRequest) clients.get(TAGAR)).WiFiPeersStatus(
									SharingWith, SharingDeviceStatus);        
					    }
					});				 
				}
			}
		} else if (SharingIp == null || SharingIp.size() < 1 || SharingDeviceStatus == null || SharingDeviceStatus.size() < 1){
			if (clients.get(TAGAR) != null) {
				((AfterRequest) clients.get(TAGAR)).finish();
				Toast.makeText(getApplicationContext(), "No device ready for connection", Toast.LENGTH_LONG).show();
			}
		}
		
	}

	public void removeDevice(String IP) {
		if(SharingIp != null && SharingIp.indexOf(IP) > -1 && SharingDeviceStatus!= null){
			SharingDeviceStatus.set(SharingIp.indexOf(IP), "Rejected");
//			SharingIp.set(SharingIp.indexOf(IP), "Rejected");
			if (startShareConnectUpdate) {
				if (clients.get(TAGAR) != null) {
					 ((AfterRequest) clients.get(TAGAR)).WiFiPeersStatus(SharingWith, SharingIp);
				}
			}
			for (Communicate C : ServerSocket.ClientChannels) {
				if (C.client.getInetAddress().toString().equals(IP)) {
					C.disconnect();
					ServerSocket.ClientChannels.remove(C);
					break;
				}
			}
			//invalidate the start share option or close the activity
		} else if (SharingIp == null || SharingIp.size() < 1 || SharingDeviceStatus == null || SharingDeviceStatus.size() < 1){
			if (clients.get(TAGAR) != null) {
				((AfterRequest) clients.get(TAGAR)).finish();
				Toast.makeText(getApplicationContext(), "No device found", Toast.LENGTH_LONG).show();
			}
		}		
	}

	public void StartDataShare() {
		int i = 0;
		for (String S : SharingDeviceStatus) {
			if (!S.equals("Connected")) {
				for (Communicate C : ServerSocket.ClientChannels) {
					if (C.client.getInetAddress().toString()
							.equals(SharingIp.get(i))) {
						C.sendCommand("Rejected");
						break;
					}
				}
				removeDevice(SharingIp.get(i));
			}
			i++;
		}
		i = 2;
		for (Communicate C : ServerSocket.ClientChannels) {
			C.sendCommand("DeviceID");
			C.sendCommand(String.valueOf(i));
			C.sendCommand("PlayType");
			C.sendCommand(ShareFile.getPlayType());
			C.sendCommand("NoOfDevices");
			C.sendCommand(String.valueOf(ServerSocket.ClientChannels.size()));
			C.sendSharableData("Data");
			i++;
		}
		deviceId = "1";
		NoOfDevices = ServerSocket.ClientChannels.size();
		FilePath = ShareFile.getFilePath().get(0);
		PlayType = ShareFile.getPlayType();
	}

	public void StartPlayActivity() {
		
		Thread t = new Thread() {
			public void run() {
				Intent I = new Intent(getApplicationContext(), PlayActivity.class);
				I.putExtra("DeviceID", deviceId);
				I.putExtra("FilePath", FilePath);
				I.putExtra("ShareType", PlayType);
				I.putExtra("NoOfDevices", NoOfDevices);
				I.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				getApplicationContext().startActivity(I);
			}
		};
		t.start();
	}

	public void PlayShutDownCommand() {
		if (isMaster) {
			for (Communicate C : ServerSocket.ClientChannels) {
				C.sendCommand("ShutDown");
			}
		} else {
			ClientSocket.ClientChannel.sendCommand("ShutDown");
		}
	}

	public void PlayShutDown() {
		if (clients.get(TAGPLAY) != null) {
			((PlayActivity) clients.get(TAGPLAY)).finish();
		}
	}

	public void KillServer() {
		if (ServerSocket != null) {
			for (Communicate C : ServerSocket.ClientChannels) {
				C.disconnect();
			}
			try {
				ServerSocket.Server.close();
			} catch (IOException e) {
				Log.i(TAG, "Server Socket error", e);
			}
			WiFiManager.cancelConnect(WiFiChannel, null);
			ServerSocket = null;
			isMaster = false;
		}
	}

	public void KillClient() {
		if (ClientSocket != null) {
			ClientSocket.ClientChannel.disconnect();
			WiFiManager.cancelConnect(WiFiChannel, null);
			ClientSocket = null;
		}
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Toast.makeText(this, "Service unbinded", Toast.LENGTH_SHORT).show();
		return super.onUnbind(intent);
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(WiFiReceiver);
		if (clients.get(TAGUGS) != null) {
			((UserGroupSelect) clients.get(TAGUGS)).CheckWifi(false);
		}
		if (!isMaster) {
			KillClient();
		} else {
			KillServer();
		}
		if (clients.get(TAGPLAY) != null) {
			((PlayActivity) clients.get(TAGPLAY)).finish();
		}
		Toast.makeText(this, "Service destroyed", Toast.LENGTH_SHORT).show();
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		Toast.makeText(this, "Service binded", Toast.LENGTH_SHORT).show();
		TurnPeerDiscoveryOn();
		return SPbinder;
	}

	public void removeService(String Tag) {
		if (clients.get(Tag) != null && !Tag.equals(TAGAB)) {
			clients.remove(Tag);
		}
	}

	public class LocalBinder extends Binder {
		public SharePlusWiFiService getService(String Tag, Activity activity) {
			if (clients.get(Tag) == null && !Tag.equals(TAGAB)) {
				clients.put(Tag, activity);
			}
			return SharePlusWiFiService.this;
		}
	}
}
