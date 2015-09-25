package com.shareplus.adapters;

import java.util.ArrayList;
import java.util.zip.Inflater;
import com.shareplus.R;
import android.content.Context;
import android.provider.Browser;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shareplus.controllers.Browse;
import com.shareplus.models.Browse_ListView_ItemData;

public class Browse_ListView_Adapter extends BaseAdapter {

	public static ArrayList<Browse_ListView_ItemData> FileList;
	Context context;
	Animation animation1;
	Animation animation2;
	ImageView CB;
	public static ArrayList<String> SelectedFiles = new ArrayList<String>();

	public Browse_ListView_Adapter(
			ArrayList<Browse_ListView_ItemData> fileList, Context context) {
		FileList = fileList;
		this.context = context;
		animation1 = AnimationUtils.loadAnimation(context,
				R.anim.browse_to_middle);
		animation2 = AnimationUtils.loadAnimation(context,
				R.anim.browse_from_middle);
	}

	@Override
	public int getCount() {
		return FileList.size();
	}

	@Override
	public Browse_ListView_ItemData getItem(int Position) {
		return FileList.get(Position);
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
			Browse_ListView_ItemData Data = FileList.get(Position);
			convertView = inflater.inflate(R.layout.browse_item, null);
			ImageView folder_icon = (ImageView) convertView
					.findViewById(R.id.folder_icon);
			TextView file_size = (TextView) convertView
					.findViewById(R.id.file_size);
			TextView file_name = (TextView) convertView
					.findViewById(R.id.file_name);
			ImageView subfolder_icon = (ImageView) convertView
					.findViewById(R.id.subfolder_button);
			folder_icon.setTag(Position);
			file_name.setText(Data.getFolderFileName());
			if (!Data.isFolder()) {
				if (Data.isChecked()) {
					folder_icon.setImageResource(R.drawable.ic_action_accept);
					folder_icon.setBackgroundColor(context.getResources()
							.getColor(android.R.color.holo_blue_dark));
				} else {
					folder_icon.setImageResource(Data.getIconPath());
					folder_icon.setBackgroundColor(context.getResources()
							.getColor(android.R.color.transparent));
				}
				file_size.setText(Data.getFileDetails());
				folder_icon.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						CB = (ImageView) v;
						CB.clearAnimation();
						CB.setAnimation(animation1);
						CB.startAnimation(animation1);
						SetAnimationListners();
						FileList.get((Integer) CB.getTag()).setIsChecked(
								!FileList.get((Integer) CB.getTag())
										.isChecked());
						if (FileList.get((Integer) CB.getTag()).isChecked()
								&& !SelectedFiles.contains(FileList.get(
										(Integer) CB.getTag()).getPath())) {
							SelectedFiles.add(FileList.get(
									(Integer) CB.getTag()).getPath());
						} else if (!FileList.get((Integer) CB.getTag())
								.isChecked()
								&& SelectedFiles.contains(FileList.get(
										(Integer) CB.getTag()).getPath())) {
							SelectedFiles.remove(FileList.get(
									(Integer) CB.getTag()).getPath());
						}
						((Browse)context).enableShare();						
					}
				});
			} else {
				folder_icon.setImageResource(Data.getIconPath());
				subfolder_icon.setImageResource(Data.getSubfolderIconPath());
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
					if (FileList.get((Integer) CB.getTag()).isChecked()) {
						CB.setImageResource(R.drawable.ic_action_accept);
						CB.setBackgroundColor(context.getResources().getColor(
								android.R.color.holo_blue_dark));
					} else {
						CB.setImageResource(FileList.get((Integer) CB.getTag())
								.getIconPath());
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
