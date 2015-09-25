package com.shareplus.models;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StreamCorruptedException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.shareplus.controllers.SharePlusWiFiService;

public class Communicate implements Runnable {
	public static final String TAG = "Communication";
	public Socket client = null;
	public Scanner InStream = null;
	public PrintWriter outStream = null;
	public String CurrentCommand = "";
	public Context Con = null;

	public Communicate(Socket S, Context C) {
		client = S;
		Con = C;
	}

	@Override
	public void run() {
		try {
			Log.i(TAG, "Socketcreated");
			if (client.isConnected() && ((SharePlusWiFiService) Con).isWiFiOn) {
				InStream = new Scanner(client.getInputStream());
				outStream = new PrintWriter(client.getOutputStream());
				sendCommand("Handshake");
				getCommand();
			}
		} catch (StreamCorruptedException e) {
			Log.i(TAG, "Error in Stream reading and writing");
			e.printStackTrace();
		} catch (IOException e) {
			Log.i(TAG, "Stream IO error");
			e.printStackTrace();
		}
	}

	public void getCommand() {
		while (true) {
			if (InStream.hasNext() && ((SharePlusWiFiService) Con).isWiFiOn) {
				CurrentCommand = InStream.nextLine();
				if (CurrentCommand.equals("Handshake")) { // Both client and
															// server
					if (((SharePlusWiFiService) Con).isMaster) {
						sendCommand("StartSharePlus"); // To client
						Log.i(TAG, "Handshake");
					} else {
						sendCommand("MAC"); // To server
						sendCommand(((SharePlusWiFiService) Con)
								.getMacAddress());
					}
				}
				if (CurrentCommand.equals("MAC")) { // Server
					((SharePlusWiFiService) Con).setMacToIpAddress(InStream
							.nextLine(), client.getInetAddress().toString());
				}
				if (CurrentCommand.equals("StartSharePlus")) { // Client
					((SharePlusWiFiService) Con).askConnect();
					Log.i(TAG, "StartSharePlus");
				}
				if (CurrentCommand.equals("connectNo")) { // Server
					((SharePlusWiFiService) Con).removeDevice(client
							.getInetAddress().toString());
				}
				if (CurrentCommand.equals("connectYes")) { // Server
					((SharePlusWiFiService) Con).setDeviceConnected(client
							.getInetAddress().toString());
					Log.i(TAG, "ConnectYes");

				}
				if (CurrentCommand.equals("Rejected")) { // Client
					((SharePlusWiFiService) Con).KillClient();
				}
				if (CurrentCommand.equals("DeviceID")) { // Client
					((SharePlusWiFiService) Con).deviceId = InStream.nextLine();
				}
				if (CurrentCommand.equals("PlayType")) { // Client
					((SharePlusWiFiService) Con).PlayType = InStream.nextLine();
				}
				if (CurrentCommand.equals("NoOfDevices")) { // Client
					((SharePlusWiFiService) Con).NoOfDevices = Integer
							.valueOf(InStream.nextLine());
				}
				if (CurrentCommand.equals("ShutDown")) { // Both server and
															// client
					if (((SharePlusWiFiService) Con).isMaster) { // Server side
						((SharePlusWiFiService) Con).PlayShutDownCommand();
						((SharePlusWiFiService) Con).PlayShutDown();
						((SharePlusWiFiService) Con).KillServer();
					} else {
						((SharePlusWiFiService) Con).PlayShutDown(); // Client
																		// Side
						((SharePlusWiFiService) Con).KillClient();
					}
				}
				if (CurrentCommand.equals("StartDisplay")) { // Server side
					((SharePlusWiFiService) Con).StartPlayActivity();
				}
				if (CurrentCommand.equals("Data")) { // Client side
				// Sharable ShareFile = new Sharable();
					long fileLength = 0;
					Log.i(TAG, "Data");
					try {
						CurrentCommand = InStream.nextLine();
						ArrayList<String> FilePath = new ArrayList<String>();
						File f = new File(
								Environment.getExternalStorageDirectory() + "/"
										+ Environment.DIRECTORY_DOWNLOADS + "/"
										+ "SharePlus/Shared/" + CurrentCommand);
						File dirs = new File(f.getParent());
						if (!dirs.exists()) {
							dirs.mkdirs();
						}
						f.createNewFile();
						fileLength = Long.parseLong(InStream.nextLine());
						InputStream inputstream = client.getInputStream();
						copyFile(inputstream, new FileOutputStream(f),
								fileLength);
						// FilePath.add(f.getAbsolutePath());
						// ShareFile.setFilePath(FilePath);
						// ((UserGroupSelect)
						// Con).CallingIntent.putExtra("SHAREITEMS", ShareFile);
						((SharePlusWiFiService) Con).FilePath = f
								.getAbsolutePath();
						sendCommand("StartDisplay");
						((SharePlusWiFiService) Con).StartPlayActivity();
						// Con.startActivity(((UserGroupSelect)
						// Con).CallingIntent);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} else if (!client.isConnected()) {
				return;
			}
		}
	}

	public void sendCommand(String Command) {
		outStream.println(Command);
		outStream.flush();
	}

	public void sendSharableData(String Command) {
		try {
			int len = 0;
			long fileLength = 0;
			File f = new File(((SharePlusWiFiService) Con).ShareFile
					.getFilePath().get(0));
			outStream.println(Command);
			Log.i(TAG, "Sending Data");
			outStream.flush();
			outStream.println(((SharePlusWiFiService) Con).ShareFile
					.getFileName().get(0)
					+ "."
					+ ((SharePlusWiFiService) Con).ShareFile.getFileType().get(
							0));
			outStream.flush();
			fileLength = f.length();
			outStream.println(fileLength);
			outStream.flush();

			OutputStream outputStream = client.getOutputStream();
			ContentResolver cr = Con.getContentResolver();
			InputStream inputStream = null;
			inputStream = cr.openInputStream(Uri.fromFile(f));

			sendCommand("SendInit");
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {						
			}
			while (fileLength > 1024) {
				
//				if (InStream.hasNext()
//						&& InStream.nextLine().equals("SendPacket")) {
					byte buf[] = new byte[1024];
					len = inputStream.read(buf);
					outputStream.write(buf, 0, len-1);
					fileLength -= 1024;
					
					while(InStream.hasNext() && !InStream.nextLine().equals("SendPacket")){
						
					}
					
//				} else {
//					break;
//				}
			}
			if (fileLength <= 1024 && fileLength > 0) {
//					while(InStream.hasNext() && !InStream.nextLine().equals("SendPacket")){
//					
//					}
//				if (InStream.hasNext()
//						&& InStream.nextLine().equals("SendPacket")) {
					byte buf[] = new byte[(int) fileLength];
					len = inputStream.read(buf);
					outputStream.write(buf, 0, len-1);
//				}
			}
			outputStream.flush();
			inputStream.close();
			Log.i(TAG, "Data sent");
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void disconnect() {
		try {
			client.close();
		} catch (IOException e) {
			Log.i(TAG, "Error closing connection");
			e.printStackTrace();
		}
	}

	public boolean copyFile(InputStream inputStream, OutputStream out,
			long fileLength) {
		int len;
		try {
			if (InStream.hasNext() && InStream.nextLine().equals("SendInit")) {
				while (fileLength >= 1024) {
					
					
					byte buf[] = new byte[1024];
					len = inputStream.read(buf);
					out.write(buf, 0, len - 1);
					fileLength -= 1024;
					sendCommand("SendPacket");
				}
				if (fileLength < 1024 && fileLength > 0) {
					sendCommand("SendPacket");
					
					byte buf[] = new byte[(int) fileLength];
					len = inputStream.read(buf);
					out.write(buf, 0, len - 1);
				}
			}
			out.close();
		} catch (IOException e) {
			Log.d(TAG, e.toString());
			return false;
		}
		return true;
	}
}
