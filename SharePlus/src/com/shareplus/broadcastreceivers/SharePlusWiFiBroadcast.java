package com.shareplus.broadcastreceivers;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.http.conn.util.InetAddressUtils;

import com.shareplus.controllers.SharePlusWiFiService;
import com.shareplus.controllers.UserGroupSelect;
import com.shareplus.models.Client;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.util.Log;
import android.widget.Toast;

public class SharePlusWiFiBroadcast extends BroadcastReceiver{

	private WifiP2pManager WiFiManager;
    private Channel WiFiChannel;
    private SharePlusWiFiService SPWiFiService;
    public Client Cli = null;
    
    static String address = "";
    
    
    
	public SharePlusWiFiBroadcast(WifiP2pManager wiFiManager, Channel channel, SharePlusWiFiService SPService) {
		super();
		this.WiFiManager = wiFiManager;
		this.WiFiChannel = channel;
		this.SPWiFiService = SPService;
	}

	@Override
	public void onReceive(Context context, Intent intent) {		
		String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {            	
            	SPWiFiService.WiFiStatus(true);            	
            } else {
            	SPWiFiService.WiFiStatus(false);
            }        	
        } else 
        if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
        	if (WiFiManager != null) {        		
        		SPWiFiService.RequestPeers(false);
        	}
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {        	
        	
        	NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
        	Toast.makeText(SPWiFiService, "Checking Connection is made or not " + networkInfo.isConnected()  , Toast.LENGTH_LONG).show();
            if (networkInfo.isConnected()) {            	          	
            	WiFiManager.requestConnectionInfo(WiFiChannel, new WifiP2pManager.ConnectionInfoListener() {				
    				@Override
    				public void onConnectionInfoAvailable(WifiP2pInfo info) {    					
    					if (info.groupFormed && info.isGroupOwner ){    						
    						Toast.makeText(SPWiFiService, "owner of group", Toast.LENGTH_LONG).show();
    					} else if(info.groupFormed && !SPWiFiService.isClientCreated) {    						
    						Client C = new Client(info.groupOwnerAddress.getHostAddress(), 8881, SPWiFiService);
    						C.Start();
    						SPWiFiService.isClientCreated = true;
    						SPWiFiService.ClientSocket = C;
    						}
						}				
    			});            	
            }
        	
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
        	Log.i("Test", "Test");
			
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////Commented code for testing purposes on different hardwares///////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

			
//        	Toast.makeText(SPWiFiService, "Device changed" , Toast.LENGTH_LONG).show();        	
//        	WifiP2pDevice device = (WifiP2pDevice) intent
//                    .getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
//        	Toast.makeText(SPWiFiService, "Device status -" + device.status , Toast.LENGTH_LONG).show();
//        	Toast.makeText(SPWiFiService, "Device address -" + device.deviceAddress , Toast.LENGTH_LONG).show();
//        	Toast.makeText(SPWiFiService, "Device name -" + device.deviceName , Toast.LENGTH_LONG).show();
//        	Toast.makeText(SPWiFiService, "Device owner -" + device.isGroupOwner() , Toast.LENGTH_LONG).show();
        	
        	
//        	if (device.status == WifiP2pDevice.CONNECTED) {            	          	
//            	WiFiManager.requestGroupInfo(WiFiChannel,new WifiP2pManager.GroupInfoListener() {					
//					@Override
//					public void onGroupInfoAvailable(WifiP2pGroup group) {
//						Toast.makeText(SPWiFiService, "From group is owner: " + group.isGroupOwner(), Toast.LENGTH_LONG).show();
//						
//						Toast.makeText(SPWiFiService, "From group owner name: " + group.getOwner().deviceName, Toast.LENGTH_LONG).show();
//						address = group.getOwner().deviceName;
////						Thread t = new Thread() {
////							public void run() {
////							
////								try {								
////									Inet4Address.getByName(address);
////									Toast.makeText(SPWiFiService, "From group owner address: " + address, Toast.LENGTH_LONG).show();
////								} catch (UnknownHostException e) {
////									// TODO Auto-generated catch block
////									e.printStackTrace();
////								}
////							
////							}
////						};
////						t.start();
//						
//						
//						WiFiManager.requestConnectionInfo(WiFiChannel, new WifiP2pManager.ConnectionInfoListener() {							
//							@Override
//							public void onConnectionInfoAvailable(WifiP2pInfo info) {
//								if (info.groupFormed) {
//									
//								
//								Toast.makeText(SPWiFiService, "From groupinfo is owner: " + info.isGroupOwner, Toast.LENGTH_LONG).show();
//								Toast.makeText(SPWiFiService, "From groupinfo owner address: " + info.groupOwnerAddress.getHostAddress(), Toast.LENGTH_LONG).show();
//								}
//							}
//						});						
//					}
//				}); 
//
//            	
//        		
//        		WiFiManager.requestConnectionInfo(WiFiChannel, new WifiP2pManager.ConnectionInfoListener() {				
//    				@Override
//    				public void onConnectionInfoAvailable(WifiP2pInfo info) {    					
//    					if (info.groupFormed && info.isGroupOwner ){    						
//    						Toast.makeText(SPWiFiService, "From Device owner of group", Toast.LENGTH_LONG).show();
//    					} else if(info.groupFormed && !SPWiFiService.isClientCreated) {
//    						Toast.makeText(SPWiFiService, "From Device slave", Toast.LENGTH_LONG).show();
//    						Toast.makeText(SPWiFiService, "Device owner Address: " + info.groupOwnerAddress.getHostAddress(), Toast.LENGTH_LONG).show();    						
//    						}
//						}				
//    			});            	
//            }
//        	
            
        	
            
            
        }
	}
}
