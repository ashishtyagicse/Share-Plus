package com.shareplus.models;

import android.os.Parcel;
import android.os.Parcelable;

public class userProfile implements Parcelable{
public String firstName="";
public String lastName="";
public String contactNum="";
public String userName="";
public String getFirstName() {
	return firstName;
}
public void setFirstName(String firstName) {
	this.firstName = firstName;
}
public String getLastName() {
	return lastName;
}
public void setLastName(String lastName) {
	this.lastName = lastName;
}
public String getContactNum() {
	return contactNum;
}
public void setContactNum(String contactNum) {
	this.contactNum = contactNum;
}
public String getUserName() {
	return userName;
}
public void setUserName(String userName) {
	this.userName = userName;
}

public userProfile(String fName, String lName, String num, String uName){
	this.firstName=fName;
	this.lastName = lName;
	this.userName=uName;
	this.contactNum = num;
}
@Override
public int describeContents() {
	// TODO Auto-generated method stub
	return 0;
}
@Override
public void writeToParcel(Parcel dest, int flags) {
	// TODO Auto-generated method stub
	
}
}
