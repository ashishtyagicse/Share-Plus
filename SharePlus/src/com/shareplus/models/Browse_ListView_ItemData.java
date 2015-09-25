package com.shareplus.models;

public class Browse_ListView_ItemData {
	private String FolderFileName;
	private String FileDetails;
	private String Path;
	private int IconPath;
	private int SubfolderIconPath;
	private boolean isFolder = false;
	private boolean isChecked = false;

	public Browse_ListView_ItemData(String folderFileName, String fileDetails,
			String path, int iconPath, int subfolderIconPath, boolean isFolder,
			boolean isChecked) {
		super();
		FolderFileName = folderFileName;
		FileDetails = fileDetails;
		Path = path;
		IconPath = iconPath;
		SubfolderIconPath = subfolderIconPath;
		this.isFolder = isFolder;
		this.isChecked = isChecked;
	}

	public Browse_ListView_ItemData() {

	}

	public String getFolderFileName() {
		return FolderFileName;
	}

	public void setFolderFileName(String folderFileName) {
		this.FolderFileName = folderFileName;
	}

	public String getFileDetails() {
		return FileDetails;
	}

	public void setFileDetails(String fileDetails) {
		this.FileDetails = fileDetails;
	}

	public String getPath() {
		return Path;
	}

	public void setPath(String path) {
		this.Path = path;
	}

	public int getIconPath() {
		return IconPath;
	}

	public void setIconPath(int iconPath) {
		this.IconPath = iconPath;
	}

	public int getSubfolderIconPath() {
		return SubfolderIconPath;
	}

	public void setSubfolderIconPath(int subfolderIconPath) {
		this.SubfolderIconPath = subfolderIconPath;
	}

	public boolean isFolder() {
		return isFolder;
	}

	public void setIsFolder(boolean isFolder) {
		this.isFolder = isFolder;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setIsChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}
}
