package com.shareplus.controllers;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import com.shareplus.R;
import com.shareplus.adapters.Browse_ListView_Adapter;
import com.shareplus.models.Browse_ListView_ItemData;
import com.shareplus.models.ExternalStorages;
import com.shareplus.models.ExternalStorages.StorageInfo;
import com.shareplus.models.Sharable;

import android.R.bool;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class Browse extends Activity {

	public static final String LAST_KNOWN_PATH = "LastPath";
	public static final String ROOT_PATH = "RootPath";
	public static final String LAST_SELECTED_FILES = "SelectedFiles";
	protected static final String TAG = "Browse";
	public String InitPath = "";
	private ArrayList<Browse_ListView_ItemData> FileList = new ArrayList<Browse_ListView_ItemData>();
	public ArrayList<ArrayList<Browse_ListView_ItemData>> Stack = new ArrayList<ArrayList<Browse_ListView_ItemData>>();
	private Browse_ListView_Adapter Adapter;
	private ArrayList<String> SelectedFiles = new ArrayList<String>();
	private Sharable ShareFile = null;
	private int MenuFlag = 1;
	private boolean FromShareMenu = false;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		boolean ServiceRunning = false;
		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (SharePlusWiFiService.class.getName().equals(service.service.getClassName())) {
	        	ServiceRunning = true;	        	
	        }
	    }
	    if (!ServiceRunning) {
	    	Thread t = new Thread(){
	    		public void run(){
	    			Intent StartService = new Intent(getApplicationContext(), SharePlusWiFiService.class);
	    			StartService.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    			getApplicationContext().startService(StartService);
	    		}
	    	};
	    	t.start();
		}
		
		
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		String action = intent.getAction();
		if (Intent.ACTION_SEND.equals(action)) {
			if (extras.containsKey(Intent.EXTRA_STREAM)) {				
				SelectedFiles.add(extras.get(Intent.EXTRA_STREAM).toString()
						.replaceFirst("file://", ""));
				FromShareMenu = true;
				PrepareShareble();
			}
		} else {

			setContentView(R.layout.activity_browse);
			int MaxSize = 0;
			SelectedFiles.clear();
			MaxSize = PreferenceManager.getDefaultSharedPreferences(this)
					.getInt(LAST_SELECTED_FILES, 0);
			for (int Count = 0; Count < MaxSize; Count++) {
				SelectedFiles.add(PreferenceManager
						.getDefaultSharedPreferences(this).getString(
								LAST_SELECTED_FILES + Count + 1, ""));
			}
			if (SelectedFiles.size() > 0) {
				MenuFlag = 0;
				invalidateOptionsMenu();
			}

			String Lpath = PreferenceManager.getDefaultSharedPreferences(this)
					.getString(LAST_KNOWN_PATH, "");
			InitPath = PreferenceManager.getDefaultSharedPreferences(this)
					.getString(ROOT_PATH, "");
			SetHandler(this);
			if (Lpath.endsWith("/")) {
				Lpath = Lpath.substring(0, Lpath.length() - 1);
			}
			if (Lpath != "" && !Lpath.equals(InitPath)) {
				ArrayList<String> Build = new ArrayList<String>();
				String ParentPath = Lpath;
				while (!ParentPath.equals(InitPath)) {
					ParentPath = ParentPath.substring(0,
							ParentPath.length() - 1);
					ParentPath = ParentPath.substring(0,
							ParentPath.lastIndexOf("/"));
					Build.add(ParentPath);
				}
				Collections.reverse(Build);
				Stack.clear();
				for (String s : Build) {
					if (s.equals(InitPath)) {
						GetList("");
					} else {
						GetList(s);
					}
					Stack.add((ArrayList<Browse_ListView_ItemData>) FileList
							.clone());
				}
			}
			if (Lpath.equals(InitPath)) {
				FillList("");
			} else {
				FillList(Lpath);
			}
		}
	}

	private void FillList(String initPath) {
		GetList(initPath);
		Adapter.notifyDataSetChanged();
	}

	private void SetHandler(Context context) {
		ListView FileListView = (ListView) findViewById(R.id.folderlist);
		Adapter = new Browse_ListView_Adapter(FileList, context);
		FileListView.setAdapter(Adapter);
		FileListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (FileList.get(position).isFolder()
						&& FileList.get(position).getIconPath() != R.drawable.emptyfolder) {
					SelectedFiles = Browse_ListView_Adapter.SelectedFiles;
					Browse.this.navigate(FileList, "DOWN",
							FileList.get(position).getPath());
				} else if (!FileList.get(position).isFolder()
						&& FileList.get(position).getIconPath() != R.drawable.emptyfolder) {
					ViewGroup test = (ViewGroup) view;
					Log.i(TAG, "Test Child Count: " + test.getChildCount());
					View folder_icon = test.getChildAt(0);
					folder_icon.performClick();
				}
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (!FromShareMenu) {
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				if (Stack.size() == 0) {
					SelectedFiles.clear();
					FileList.clear();
					Stack.clear();
					PreferenceManager.getDefaultSharedPreferences(this).edit()
							.putInt(LAST_SELECTED_FILES, SelectedFiles.size())
							.commit();
					PreferenceManager.getDefaultSharedPreferences(this).edit()
							.putString(ROOT_PATH, "").commit();
					PreferenceManager.getDefaultSharedPreferences(this).edit()
							.putString(LAST_KNOWN_PATH, "").commit();
					return super.onKeyDown(keyCode, event);
				} else {
					navigate(null, "UP", "");
					return true;
				}
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	public void navigate(ArrayList<Browse_ListView_ItemData> list,
			String Direction, String ParentPath) {
		TextView T = (TextView) findViewById(R.id.currentpath);
		if (Direction == "UP" && Stack.size() != 0) {
			ParentPath = T.getText().toString();
			ParentPath = ParentPath.substring(0, ParentPath.length() - 1);
			ParentPath = ParentPath.substring(0, ParentPath.lastIndexOf("/"));
			T.setText(ParentPath);
			FileList.clear();
			FileList.addAll(Stack.get(Stack.size() - 1));
			for (Browse_ListView_ItemData Data : FileList) {
				if (isChecked(Data.getPath())) {
					Data.setIsChecked(true);
				} else {
					Data.setIsChecked(false);
				}
			}
			Stack.remove(Stack.size() - 1);
			Adapter.notifyDataSetChanged();
		} else if (Direction == "DOWN") {
			Stack.add((ArrayList<Browse_ListView_ItemData>) list.clone());
			T.setText(ParentPath);
			FillList(ParentPath);
		}
	}

	private void GetList(String Pathstring) {
		TextView T = (TextView) findViewById(R.id.currentpath);
		FileList.clear();
		if (Pathstring == "") {
			int I = 0;
			ExternalStorages S = new ExternalStorages();
			List<StorageInfo> infolist = S.getStorageList();
			for (StorageInfo storage : infolist) {
				if (I == 0) {
					File f = new File(storage.path);
					if (f.getParent() == null) {
						T.setText("");
						InitPath = "";
					} else {
						T.setText(f.getParent());
						InitPath = f.getParent();
					}
				}
				int drawable = 0;
				if (storage.getDisplayName().equals("Internal SD card")) {
					drawable = R.drawable.phone;
				} else {
					drawable = R.drawable.sdcard;
				}
				FileList.add(new Browse_ListView_ItemData(storage
						.getDisplayName(), "", storage.path, drawable,
						R.drawable.open, true, false));
				I++;
			}

			// ArrayList<String> Storages = GetExternalStorage();
			// if (Storages != null) {
			// for (String St : Storages) {
			// if (I == 0) {
			// T.setText(St);
			// InitPath = St.substring(0, St.length() - 1);
			// } else if (I == 1) {
			// FileList.add(new Browse_ListView_ItemData(
			// "Internal Storage", "", St, R.drawable.phone,
			// R.drawable.open, true, false));
			// } else {
			// FileList.add(new Browse_ListView_ItemData("SD Card "
			// + I, "", St, R.drawable.sdcard,
			// R.drawable.open, true, false));
			// }
			// I++;
			// }
			// }
		} else {
			String state = Environment.getExternalStorageState();
			if (Environment.MEDIA_MOUNTED.equals(state)
					|| Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
				File Path = new File(Pathstring + "/");
				if (Path.exists()) {
					File[] files = Path.listFiles();
					Arrays.sort(files);
					if (files == null) {
						FileList.clear();
						T.setText(Pathstring + "/");
						FileList.add(new Browse_ListView_ItemData("...", "",
								Pathstring, R.drawable.emptyfolder,
								R.drawable.emptynavigation, true, false));
					} else {
						T.setText(Pathstring + "/");
						for (File file : files) {
							if (file.isDirectory()
									&& !file.getName().startsWith(".")) {

								if (file.canRead()
										&& (file.listFiles().length > 0)) {
									boolean Flag = false;
									Search: {
										for (File F : file.listFiles()) {
											if (F.isDirectory()) {
												Flag = true;
												break Search;
											}
										}
									}
									if (Flag) {
										FileList.add(new Browse_ListView_ItemData(
												file.getName(), "", file
														.getAbsolutePath(),
												R.drawable.folderfolder,
												R.drawable.open, true, false));
									} else {
										FileList.add(new Browse_ListView_ItemData(
												file.getName(), "", file
														.getAbsolutePath(),
												R.drawable.folder,
												R.drawable.open, true, false));
									}
								} else {
									FileList.add(new Browse_ListView_ItemData(
											file.getName(), "", file
													.getAbsolutePath(),
											R.drawable.emptyfolder,
											R.drawable.emptynavigation, true,
											false));
								}
							}
						}
						for (File file : files) {
							if (file.isFile()
									&& !file.getName().startsWith(".")) {
								if (file.canRead()) {
									FileList.add(new Browse_ListView_ItemData(
											file.getName(),
											readableFileSize(file.length()),
											file.getAbsolutePath(),
											getIcon(file.getAbsolutePath()),
											R.drawable.emptynavigation, false,
											isChecked(file.getAbsolutePath())));
								} else {
									FileList.add(new Browse_ListView_ItemData(
											file.getName(),
											readableFileSize(file.length()),
											file.getAbsolutePath(),
											R.drawable.noreadfile,
											R.drawable.emptynavigation, false,
											isChecked(file.getAbsolutePath())));
								}
							}
						}
					}

				} else {
					GetList("");
				}
			}
		}
	}

	private boolean isChecked(String Path) {
		if (SelectedFiles.contains(Path)) {
			return true;
		} else {
			return false;
		}
	}

	private String readableFileSize(long size) {
		if (size <= 0)
			return "0";
		final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
		int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
		return new DecimalFormat("#,##0.##").format(size
				/ Math.pow(1024, digitGroups))
				+ " " + units[digitGroups];
	}

	private int getIcon(String FileType) {
		String FileExt = FileType.substring(FileType.lastIndexOf(".") + 1);
		int resid;
		try {
			resid = R.drawable.class.getField(FileExt).getInt(null);
			return resid;
		} catch (Exception e) {
			return R.drawable.documentgeneric;
		}
	}

	// private ArrayList<String> GetExternalStorage() {
	// // String state = Environment.getExternalStorageState();
	// ArrayList<String> Storage = new ArrayList<String>();
	// if (Environment.MEDIA_MOUNTED.equals(state)
	// || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
	// File primaryExternalStorage = Environment
	// .getExternalStorageDirectory();
	// String externalStorageRootDirPath = primaryExternalStorage
	// .getParent();
	//
	// if (externalStorageRootDirPath == null) {
	// Storage.add(primaryExternalStorage.getAbsolutePath() + "/");
	// Storage.add(primaryExternalStorage.getAbsolutePath());
	// } else {
	// File externalStorageRoot = new File(externalStorageRootDirPath);
	// File[] files = externalStorageRoot.listFiles();
	// if (files == null) {
	// Storage.add(externalStorageRoot.getAbsolutePath() + "/");
	// Storage.add(primaryExternalStorage.getAbsolutePath());
	// } else {
	// Storage.add(externalStorageRoot.getAbsolutePath() + "/");
	// for (File file : files) {
	// if (file.isDirectory() && file.canRead()
	// && (file.listFiles().length > 0)) {
	// Storage.add(file.getAbsolutePath());
	// }
	// }
	// }
	// }
	// }
	//
	// ExternalStorages S = new ExternalStorages();
	// List<StorageInfo> infolist = S.getStorageList();
	// for (StorageInfo storage : infolist) {
	//
	// }
	//
	// return Storage;
	// }

	public void enableShare() {
		SelectedFiles = Browse_ListView_Adapter.SelectedFiles;
		if (SelectedFiles.size() > 0) {
			MenuFlag = 0;
			invalidateOptionsMenu();
		} else {
			MenuFlag = 1;
			invalidateOptionsMenu();
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!FromShareMenu) {
			getMenuInflater().inflate(R.menu.browse_actionbar, menu);

			if (MenuFlag == 0) {
				menu.findItem(R.id.sharefiles).setVisible(true);
				menu.findItem(R.id.uncheckall).setVisible(true);
			} else {
				menu.findItem(R.id.sharefiles).setVisible(false);
				menu.findItem(R.id.uncheckall).setVisible(false);
			}
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.create_User_Profile) {
			System.out.println("in create profile");
			Intent goToCreateProfileActivity = new Intent(this,com.shareplus.controllers.CreateUpdateUserProfile.class);
			startActivity(goToCreateProfileActivity);			
		}
		if (id == R.id.aboutSharePlus) {
			System.out.println("in create profile");
			Intent goToCreateProfileActivity = new Intent(this, AboutSharePlus.class);
			startActivity(goToCreateProfileActivity);			
		}
		
		if (id == R.id.sharefiles) {
			PrepareShareble();
		}

		if (id == R.id.uncheckall) {
			SelectedFiles.clear();
			PreferenceManager.getDefaultSharedPreferences(this).edit()
					.putInt(LAST_SELECTED_FILES, SelectedFiles.size()).commit();
			for (ArrayList<Browse_ListView_ItemData> Frame : Stack) {
				for (Browse_ListView_ItemData Data : Frame) {
					if (Data.isChecked()) {
						Data.setIsChecked(false);
					}
				}
			}
			for (Browse_ListView_ItemData Data : FileList) {
				if (Data.isChecked()) {
					Data.setIsChecked(false);
				}
			}
			if (ShareFile != null) {
				ShareFile = null;
			}
			Adapter.notifyDataSetChanged();
			MenuFlag = 1;
			invalidateOptionsMenu();
		}
		return super.onOptionsItemSelected(item);
	}

	private void PrepareShareble() {
		ShareFile = new Sharable();
		ArrayList<String> FilePath = new ArrayList<String>();
		ArrayList<String> FileName = new ArrayList<String>();
		ArrayList<String> FileType = new ArrayList<String>();
		if (SelectedFiles.size() == 1) {
			String s = SelectedFiles.get(0);
			String FileExt = s.substring(s.lastIndexOf(".") + 1);
			FileExt.toLowerCase();
			int flag = 0;
			if (FileExt.equals("wav") || FileExt.equals("ogg")
					|| FileExt.equals("amr") || FileExt.equals("mp3")
					|| FileExt.equals("wmv")) {
				FilePath.add(s);
				int i = s.lastIndexOf(".");
				if (i < 1) {
					i = s.length();
				}
				FileName.add(s.substring(s.lastIndexOf("/"),i));
				FileType.add(FileExt);
				ShareFile.setFilePath(FilePath);
				ShareFile.setFileName(FileName);
				ShareFile.setFileType(FileType);
				ShareFile.setPlayable(true);
				ShareFile.setPlayType("MUSIC");
				flag = 1;
			} else if (FileExt.equals("mpeg") || FileExt.equals("mpg")
					|| FileExt.equals("mp4") || FileExt.equals("3gp")
					|| FileExt.equals("avi")) {
				FilePath.add(s);
				int i = s.lastIndexOf(".");
				if (i < 1) {
					i = s.length();
				}
				FileName.add(s.substring(s.lastIndexOf("/"),i));
				FileType.add(FileExt);
				ShareFile.setFilePath(FilePath);
				ShareFile.setFileName(FileName);
				ShareFile.setFileType(FileType);
				ShareFile.setPlayable(true);
				ShareFile.setPlayType("VIDEO");
				flag = 1;
			} else if (FileExt.equals("jpg") || FileExt.equals("jpeg")
					|| FileExt.equals("png") || FileExt.equals("gif")) {
				FilePath.add(s);
				int i = s.lastIndexOf(".");
				if (i < 1) {
					i = s.length();
				}
				FileName.add(s.substring(s.lastIndexOf("/"),i));
				FileType.add(FileExt);
				ShareFile.setFilePath(FilePath);
				ShareFile.setFileName(FileName);
				ShareFile.setFileType(FileType);
				ShareFile.setPlayable(true);
				ShareFile.setPlayType("IMAGE");
				flag = 1;
			} else {
				FilePath.add(s);
				int i = s.lastIndexOf(".");
				if (i < 1) {
					i = s.length();
				}
				FileName.add(s.substring(s.lastIndexOf("/"),i));
				FileType.add(FileExt);
				ShareFile.setFilePath(FilePath);
				ShareFile.setFileName(FileName);
				ShareFile.setFileType(FileType);
				ShareFile.setPlayable(false);
				ShareFile.setPlayType("NONPLAYTYPE");
				flag = 0;
			}
			if (flag != 1) {
				ShareFile.setShareMethod("SHARE");
				SetShareMode();
			} else {
				SetShareMethod();
			}

		} else {
			String FileExt = "";
			for (String s : SelectedFiles) {
				FileExt = s.substring(s.lastIndexOf(".") + 1);
				FilePath.add(s);
				FileName.add(s.substring(s.lastIndexOf("/"),
						s.lastIndexOf(".") - 1));
				FileType.add(FileExt);
			}
			ShareFile.setPlayable(false);
			ShareFile.setPlayType("NONPLAYTYPE");
			ShareFile.setFilePath(FilePath);
			ShareFile.setFileName(FileName);
			ShareFile.setFileType(FileType);
			ShareFile.setShareMethod("SHARE");
			SetShareMode();
		}
	}

	private void SetShareMethod() {
		CharSequence SHAREMETHODS[] = new CharSequence[] { "Play", "Share",
				"Share and Play" };
		AlertDialog.Builder Method = new AlertDialog.Builder(this);
		Method.setTitle(R.string.methodtitle);
		Method.setItems(SHAREMETHODS, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (ShareFile != null) {
					switch (which) {
					case 0:
						ShareFile.setShareMethod("PLAY");
						break;
					case 1:
						ShareFile.setShareMethod("SHARE");
						break;
					case 2:
						ShareFile.setShareMethod("PLAYSHARE");
						break;
					default:
						break;
					}
					Browse.this.SetShareMode();
				}
			}
		});
		Method.show();
	}

	private void SetShareMode() {
		CharSequence SHAREMODES[] = new CharSequence[] { "Single User",
				"Multiple Users", "Broadcast all in Range" };
		AlertDialog.Builder Mode = new AlertDialog.Builder(this);
		Mode.setTitle(R.string.modetitle);
		Mode.setItems(SHAREMODES, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (ShareFile != null) {
					switch (which) {
					case 0:
						ShareFile.setShareMode("SINGLE");
						break;
					case 1:
						ShareFile.setShareMode("MULTIPLE");
						break;
					case 2:
						ShareFile.setShareMode("BROADCAST");
						break;
					default:
						break;
					}
					StartUserSelect();
				}
			}
		});
		Mode.show();
	}

	private void StartUserSelect() {
		Intent I = new Intent(this, UserGroupSelect.class);
		I.putExtra("SHAREITEMS", ShareFile);
		startActivity(I);
	}

	@Override
	protected void onDestroy() {
		if (!FromShareMenu) {
			TextView T = (TextView) findViewById(R.id.currentpath);
			String Path = T.getText().toString();
			PreferenceManager.getDefaultSharedPreferences(this).edit()
					.putString(ROOT_PATH, InitPath).commit();
			PreferenceManager.getDefaultSharedPreferences(this).edit()
					.putString(LAST_KNOWN_PATH, Path).commit();
			PreferenceManager.getDefaultSharedPreferences(this).edit()
					.putInt(LAST_SELECTED_FILES, SelectedFiles.size()).commit();
			for (int Count = 0; Count < SelectedFiles.size(); Count++) {
				PreferenceManager
						.getDefaultSharedPreferences(this)
						.edit()
						.putString(LAST_SELECTED_FILES + Count + 1,
								SelectedFiles.get(Count)).commit();
			}
		}
		super.onDestroy();
	}

}
