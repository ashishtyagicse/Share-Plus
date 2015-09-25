package com.shareplus.models;

import android.net.wifi.p2p.WifiP2pDevice;

public class UserGroupSelect_ListView_ItemData {
	private String DeviceName;
	private String DeviceIP;
	private WifiP2pDevice Device;
	private boolean isSelected;

	public UserGroupSelect_ListView_ItemData(String devicename, String deviceip, WifiP2pDevice device, boolean IsSelected) {
		super();
		DeviceName = devicename;
		DeviceIP = deviceip;
		Device = device;
		isSelected = IsSelected;
	}

	public UserGroupSelect_ListView_ItemData() {

	}

	public String getDeviceName() {
		return DeviceName;
	}

	public void setDeviceName(String devicename) {
		this.DeviceName = devicename;
	}

	public String getDeviceIP() {
		return DeviceIP;
	}

	public void setDeviceIP(String deviceip) {
		this.DeviceIP = deviceip;
	}
	
	public WifiP2pDevice getDevice() {
		return Device;
	}

	public void setDevice(WifiP2pDevice device) {
		this.Device = device;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	
}
