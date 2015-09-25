package com.shareplus.models;
import java.util.ArrayList;
import android.os.Parcel;
import android.os.Parcelable;


public class Sharable implements Parcelable{
	private boolean Playable = false;
	private String PlayType = "NONPLAYTYPE"; // MUSIC, VIDEO, IMAGE, NONPLAYTYPE
	private String ShareMethod = "";
	private String ShareMode = "";
	private ArrayList<String> FilePath = new ArrayList<String>();
	private ArrayList<String> FileName = new ArrayList<String>();
	private ArrayList<String> FileType = new ArrayList<String>();

	public boolean isPlayable() {
		return Playable;
	}

	public void setPlayable(boolean playable) {
		Playable = playable;
	}

	public String getPlayType() {
		return PlayType;
	}

	public void setPlayType(String playType) {
		PlayType = playType;
	}

	public String getShareMethod() {
		return ShareMethod;
	}

	public void setShareMethod(String shareMethod) {
		ShareMethod = shareMethod;
	}

	public String getShareMode() {
		return ShareMode;
	}

	public void setShareMode(String shareMode) {
		ShareMode = shareMode;
	}
	
	public ArrayList<String> getFilePath() {
		return FilePath;
	}

	public void setFilePath(ArrayList<String> filePath) {
		FilePath = filePath;
	}

	public ArrayList<String> getFileName() {
		return FileName;
	}

	public void setFileName(ArrayList<String> fileName) {
		FileName = fileName;
	}

	public ArrayList<String> getFileType() {
		return FileType;
	}

	public void setFileType(ArrayList<String> fileType) {
		FileType = fileType;
	}
	
	public Sharable(boolean playable, String playType, String shareMethod,
			String shareMode, ArrayList<String> filePath,
			ArrayList<String> fileName, ArrayList<String> fileType) {
		super();
		Playable = playable;
		PlayType = playType;
		ShareMethod = shareMethod;
		ShareMode = shareMode;
		FilePath = filePath;
		FileName = fileName;
		FileType = fileType;
	}

	public Sharable() {
	}

	public Sharable(Parcel p) {
		this.Playable = p.readByte() != 0;
		this.PlayType = p.readString();
		this.ShareMethod = p.readString();
		this.ShareMode = p.readString();
		p.readStringList(this.FilePath);
		p.readStringList(this.FileName);
		p.readStringList(this.FileType);
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeByte((byte) (this.Playable ? 1 : 0));
		dest.writeString(this.PlayType);
		dest.writeString(this.ShareMethod);
		dest.writeString(this.ShareMode);
		dest.writeStringList(this.FilePath);
		dest.writeStringList(this.FileName);
		dest.writeStringList(this.FileType);
	}

	@Override
	public int describeContents() {
		return 0;
	}
	
	public static final Parcelable.Creator<Sharable> CREATOR = new Parcelable.Creator<Sharable>() {
		public Sharable createFromParcel(Parcel p) {
			return new Sharable(p);
		}

		public Sharable[] newArray(int size) {
			return new Sharable[size];
		}
	};
	
}
