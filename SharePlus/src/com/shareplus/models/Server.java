package com.shareplus.models;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class Server {
	public static final String TAG = "Server";
	public static ArrayList<Communicate> ClientChannels = new ArrayList<Communicate>();
	public static ServerSocket Server = null;
	public String Port = "0";
	Context C = null;
	public Server(String port, Context C) {
		this.Port = port;
		this.C = C;
	}

	public void Start() {
		FileServerAsyncTask Ex = new FileServerAsyncTask(C);
		Ex.execute(Port);
	}

	public class FileServerAsyncTask extends AsyncTask<String, Void, Void> {
		Context C = null;
		public FileServerAsyncTask(Context c){
			C = c;
		}

		@Override
		protected Void doInBackground(String... params) {
			try {
				Log.i(TAG, "Creating Server socket", null);
				ServerSocket serverSocket = new ServerSocket(
						Integer.valueOf(params[0]));
				Server = serverSocket;
				serverSocket.setReuseAddress(true);
				Log.i(TAG, "Ready for accepting client connect", null);
				while(true){
					Socket client = serverSocket.accept();
					Communicate connection = new Communicate(client,C);
					ClientChannels.add(connection);
					Thread X = new Thread(connection);
					X.start();
				}
			} catch (IOException e) {
				Log.i(TAG, "Server Socket error", e);
				try {
					Server.close();
				} catch (IOException e1) {
					Log.i(TAG, "Server Socket error", e1);
				}
				return null;
			}
		}
	}
}
