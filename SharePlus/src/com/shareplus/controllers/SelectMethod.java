package com.shareplus.controllers;

import java.io.File;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SelectMethod extends Activity{
	Button play, share, playshare;
	File file;
	
public void onCreate(Bundle savedInstanceState){
	super.onCreate(savedInstanceState);
	setContentView(com.shareplus.R.layout.select_method);
	
//	file = new File("/sdcard/Download/20141120180702262_0001.pdf");
	Intent I = getIntent();
	if (I.getBooleanExtra("Play", true)) {
		play = (Button)findViewById (com.shareplus.R.id.playbutton);
		play.performClick();
	}
	play = (Button)findViewById (com.shareplus.R.id.playbutton);
	share = (Button)findViewById (com.shareplus.R.id.sharebutton);
	playshare = (Button)findViewById (com.shareplus.R.id.playsharebutton);
	
	play.setOnClickListener(new View.OnClickListener() {

		@Override
		public void onClick(View arg0) {
			
//			Intent I = new Intent(Intent.ACTION_VIEW);
//			I.setDataAndType(Uri.fromFile(file), "application/pdf");
//			Intent intent = Intent.createChooser(I, "Openfile");
//			
//			try{
//				startActivity(intent);
//			}
//			catch(ActivityNotFoundException e){
//				e.printStackTrace();
//			}
//	
			File file = new File("/storage/emulated/0/A/between the raindrops.mp3");

			String url=file.getPath().toString();

			Uri uri = Uri.fromFile(file);

			Intent intent = new Intent(Intent.ACTION_VIEW);

			 

			if (url.toString().contains(".doc") || url.toString().contains(".docx")) {

			// Word document

			intent.setDataAndType(uri, "application/msword");

			} else if(url.toString().contains(".pdf")) {

			// PDF file

			intent.setDataAndType(uri, "application/pdf");

			} else if(url.toString().contains(".ppt") || url.toString().contains(".pptx")) {

			// Powerpoint file

			intent.setDataAndType(uri, "application/vnd.ms-powerpoint");

			} else if(url.toString().contains(".xls") || url.toString().contains(".xlsx")) {

			// Excel file

			intent.setDataAndType(uri, "application/vnd.ms-excel");

			} else if(url.toString().contains(".zip") || url.toString().contains(".rar")) {

			// WAV audio file

			intent.setDataAndType(uri, "application/x-wav");

			} else if(url.toString().contains(".rtf")) {

			// RTF file

			intent.setDataAndType(uri, "application/rtf");

			} else if(url.toString().contains(".wav") || url.toString().contains(".mp3")) {

			// WAV audio file

			intent.setDataAndType(uri, "audio/x-wav");

			} else if(url.toString().contains(".gif")) {

			// GIF file

			intent.setDataAndType(uri, "image/gif");

			} else if(url.toString().contains(".jpg") || url.toString().contains(".jpeg") || url.toString().contains(".png")) {

			// JPG file

			intent.setDataAndType(uri, "image/jpeg");

			} else if(url.toString().contains(".txt")) {

			// Text file

			intent.setDataAndType(uri, "text/plain");

			} else if(url.toString().contains(".3gp") || url.toString().contains(".mpg") || url.toString().contains(".mpeg") || url.toString().contains(".mpe") || url.toString().contains(".mp4") || url.toString().contains(".avi")) {

			// Video files

			intent.setDataAndType(uri, "video/*");

			}

			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			startActivity(intent);

			}
			
						
		}
);
	
	share.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
		}
	});
	
	playshare.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
		}
	});
}
}
