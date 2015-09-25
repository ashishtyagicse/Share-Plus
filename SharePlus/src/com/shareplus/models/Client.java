package com.shareplus.models;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;


public class Client {
	public static final String TAG = "Client";
	Context context = null;
	String host;
	int port = 0;
	public Communicate ClientChannel = null;
	
	public Client(String Host, int Port, Context C){
		this.host = Host;
		this.port = Port;
		this.context = C;
	}
	
	public void Start() {
		FileServerAsyncTask Ex = new FileServerAsyncTask(host,port,context);
		Ex.execute();			
	}	
	
	public class FileServerAsyncTask extends AsyncTask<String, Void, Void> {
		int Port = 0;
		String Host = "";
		Context context = null;
			
		public FileServerAsyncTask(String host, int port, Context C) {
			Host = host;
			Port = port;			
			context = C;
	}

		@Override
		protected Void doInBackground(String... params) {
				Socket socket = new Socket();
			try {				
				socket.bind(null);				
				Log.i(TAG, "Craeting Client Socket", null);
				socket.setReuseAddress(true);
				socket.connect((new InetSocketAddress(Host, Port)), 50000);
				ClientChannel = new Communicate(socket,context);
				Thread X = new Thread(ClientChannel);
				X.start();
			} catch (IOException e) {
				Log.i(TAG, "Client socket error", e);
				try {
					socket.close();
				} catch (IOException e1) {
					Log.i(TAG, "Client socket close error", e1);
				}
				return null;				
			}
			return null;	
		}		
	}
}

