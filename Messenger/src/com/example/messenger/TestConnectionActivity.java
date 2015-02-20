package com.example.messenger;

import org.jivesoftware.smack.AndroidConnectionConfiguration;
import org.jivesoftware.smack.SmackAndroid;
import org.jivesoftware.smack.XMPPConnection;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

public class TestConnectionActivity extends ActionBarActivity {

	private String HOST = LoginActivity.localIP;
	private int PORT = 5222;
	private String SERVICE = "localhost";
	private XMPPConnection connection;
	private Handler mHandler = new Handler();
	private ProgressDialog Asycdialog;
	private TextView internet;
	private TextView server;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_connection);
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setTitle("Priva Messenger");
		internet = (TextView) findViewById(R.id.internetConn);
		server = (TextView) findViewById(R.id.serverConn);
	}
	
	private boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null) {
			// There are no active networks.
		    return false;
		} else
			return true;
	}
	
	public void Refresh(View v){
		new Thread(new Runnable() {
	        public void run() {
	        	mHandler.post(new Runnable() {
	                public void run() {
	                	Asycdialog = new ProgressDialog(TestConnectionActivity.this);
	                	Asycdialog.setMessage("Testing Connections...");
	                	Asycdialog.setCanceledOnTouchOutside(false);
	                	Asycdialog.show();
	                }
	            });
	        	   
	        	Context context = getApplicationContext();
	        	SmackAndroid.init(context);
	
	        	try {
	        		AndroidConnectionConfiguration connConfig = new AndroidConnectionConfiguration(HOST, PORT, SERVICE);
	        		connection = new XMPPConnection(connConfig);
	        		connection.connect();
	        		connection.disconnect();
	        		
	        		mHandler.post(new Runnable() {
	        			public void run() {
	        				//hide the dialog
	        				server.setText(Html.fromHtml("<font color=#0000FF>Online</font>"));
	        				Asycdialog.dismiss();
	        			}
	        		});
	        	} catch (Exception e) {
	        		mHandler.post(new Runnable() {
	        			public void run() {
	        				server.setText(Html.fromHtml("<font color=#ff0000>Offline</font>"));
	        				Asycdialog.dismiss();
	        			}
	        		});
	        		e.printStackTrace();
	        	}
	        	
	        	if(isNetworkConnected()){
	        		mHandler.post(new Runnable() {
	        			public void run() {
	        				internet.setText(Html.fromHtml("<font color=#0000FF>Online</font>"));
	        				Asycdialog.dismiss();
	        			}
	        		});	
	        	}else{
	        		mHandler.post(new Runnable() {
	        			public void run() {
	        				internet.setText(Html.fromHtml("<font color=#ff0000>Offline</font>"));
	        				Asycdialog.dismiss();
	        			}
	        		});	
	        	}     	
	       }
	    }).start();
	}
}