package com.shareplus.controllers;

import com.shareplus.models.userProfile;


import com.shareplus.R;
import android.R.drawable;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;

public class CreateUpdateUserProfile extends Activity {
	public String fname = "";
	public String lname = "";
	public String uname = "";
	public String contactNum = "";
	static userProfile user = null;
	EditText textField;
	public static int RESULT_LOAD_IMAGE = 100;
	public static String TAG = "createUpdateUserProfile";

	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		setContentView(com.shareplus.R.layout.activity_create_user_profile);
	}

	/**
	 * Use this method to fetch all data entered by user to create profile
	 * 
	 * @param v
	 * 
	 */
	public void createProfile(View v) {
		// fetch all data entered by user
		Log.d(TAG, "in create profile");
		textField = (EditText) findViewById(com.shareplus.R.id.firstNameTxt);
		fname = textField.getText().toString();
		Log.d(TAG, "in create profile fname is " + fname);
		textField = (EditText) findViewById(com.shareplus.R.id.lastNameTxt);
		lname = textField.getText().toString();
		Log.d(TAG, "in create profile lname is " + lname);
		textField = (EditText) findViewById(com.shareplus.R.id.userNameTxt);
		uname = textField.getText().toString();
		Log.d(TAG, "in create profile uname is " + uname);
		textField = (EditText) findViewById(com.shareplus.R.id.contactPhTxt);
		contactNum = textField.getText().toString();
		Log.d(TAG, "in create profile contactNum is " + contactNum);
		user = new userProfile(fname, lname, contactNum, uname);
		Toast.makeText(getApplicationContext(),
				"Profile is created for " + fname, Toast.LENGTH_SHORT).show();
		Log.d(TAG, "in create profile object created ");
		Intent goBackToBrowsePage = new Intent(getBaseContext(), Browse.class);
		goBackToBrowsePage.putExtra("uName", uname);
		goBackToBrowsePage.putExtra("userProfile", user);
		startActivity(goBackToBrowsePage);
		// Bundle b = new Bundle();
		// b.put
	}

	public void fetchImageFromLib(View v) {		
		Intent i = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(i, RESULT_LOAD_IMAGE);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

//		if (requestCode == 1 && resultCode == RESULT_OK
//				&& null != data) {			
			 if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();
                ImageButton imgButton = (ImageButton) findViewById(R.id.fetchProfilePicId);
                imgButton.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                imgButton.setScaleType(ScaleType.FIT_XY);
            
//			ImageButton imgButton = (ImageButton) findViewById(com.shareplus.R.id.fetchProfilePicId);
//			imgButton.setImageResource(com.shareplus.R.drawable.face1);
		}
	}
}