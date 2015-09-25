package com.shareplus.adapters;

import java.util.ArrayList;
import java.util.zip.Inflater;
import com.shareplus.R;
import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.provider.Browser;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.webkit.WebView.FindListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shareplus.controllers.Browse;
import com.shareplus.controllers.UserGroupSelect;
import com.shareplus.models.Browse_ListView_ItemData;
import com.shareplus.models.UserGroupSelect_ListView_ItemData;

public class UserGroup_ListView_Adapter extends BaseAdapter {

	private ArrayList<UserGroupSelect_ListView_ItemData> UserList = new ArrayList<UserGroupSelect_ListView_ItemData>();
	Context context;
	Animation animation1;
	Animation animation2;
	ImageView CB;
	private int SelectSingle = 0;
	public ArrayList<WifiP2pDevice> SelectedDevices = new ArrayList<WifiP2pDevice>();
	

	public UserGroup_ListView_Adapter(
			ArrayList<UserGroupSelect_ListView_ItemData> userList, Context context, int selectsingle) {
		this.UserList = userList;
		this.context = context;
		this.SelectSingle = selectsingle;
		animation1 = AnimationUtils.loadAnimation(context,
				R.anim.browse_to_middle);
		animation2 = AnimationUtils.loadAnimation(context,
				R.anim.browse_from_middle);
	}

	@Override
	public int getCount() {
		return UserList.size();
	}

	@Override
	public UserGroupSelect_ListView_ItemData getItem(int Position) {
		return UserList.get(Position);
	}

	@Override
	public long getItemId(int Position) {
		return Position;
	}

	@Override
	public View getView(int Position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getApplicationContext().getSystemService(
						Context.LAYOUT_INFLATER_SERVICE);
		convertView = null;
		if (convertView == null) {
			UserGroupSelect_ListView_ItemData Data = UserList.get(Position);
			convertView = inflater.inflate(R.layout.userselect_item, null);			
			TextView DeviceName = (TextView) convertView
					.findViewById(R.id.device_name);
			DeviceName.setText(Data.getDeviceName());
			TextView DeviceIP = (TextView) convertView
					.findViewById(R.id.device_ip);
			DeviceIP.setText(Data.getDeviceIP());
			ImageView profilepic = (ImageView)convertView
					.findViewById(R.id.profilepic); 
			profilepic.setTag(Position);			
			if(SelectSingle != 4){
			profilepic.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						CB = (ImageView) v;
						CB.clearAnimation();
						CB.setAnimation(animation1);
						CB.startAnimation(animation1);
						SetAnimationListners();
						if (SelectSingle == 0) {
							UserList.get((Integer) CB.getTag()).setSelected(!UserList.get((Integer) CB.getTag()).isSelected());
							SelectedDevices.clear();
							if (UserList.get((Integer) CB.getTag()).isSelected()) {
								SelectedDevices.add(UserList.get((Integer) CB.getTag()).getDevice());								
							} 
						} else {
							UserList.get((Integer) CB.getTag()).setSelected(!UserList.get((Integer) CB.getTag()).isSelected());							
							if (UserList.get((Integer) CB.getTag()).isSelected()) {
								SelectedDevices.add(UserList.get((Integer) CB.getTag()).getDevice());								
							} else {
								SelectedDevices.remove(UserList.get((Integer) CB.getTag()).getDevice());
							}
						}
						if (SelectedDevices.size() != 0) {
							((UserGroupSelect) context).MenuFlag = 0;
							((UserGroupSelect) context).invalidateOptionsMenu();
						} else {
							((UserGroupSelect) context).MenuFlag = 1;
							((UserGroupSelect) context).invalidateOptionsMenu();
						}
					}
				});
			}
			if (SelectSingle == 2) {
				Data.setSelected(true);
				SelectedDevices.add(Data.getDevice());
				profilepic.setImageResource(R.drawable.ic_action_accept);
				profilepic.setBackgroundColor(context.getResources().getColor(
						android.R.color.holo_blue_dark));
			}
		}
		return convertView;
	}

	private void SetAnimationListners() {
		AnimationListener AnimList;
		AnimList = new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				if (animation == animation1) {
					if (UserList.get((Integer) CB.getTag()).isSelected()) {
						CB.setImageResource(R.drawable.ic_action_accept);
						CB.setBackgroundColor(context.getResources().getColor(
								android.R.color.holo_blue_dark));
					} else {
						CB.setImageResource(R.drawable.phone);
						CB.setBackgroundColor(context.getResources().getColor(
								android.R.color.transparent));
					}
					CB.clearAnimation();
					CB.setAnimation(animation2);
					CB.startAnimation(animation2);
				}
			}
		};
		animation1.setAnimationListener(AnimList);
	}
}
