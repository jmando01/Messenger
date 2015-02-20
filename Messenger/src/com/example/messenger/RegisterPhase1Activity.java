package com.example.messenger;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import org.jivesoftware.smack.AndroidConnectionConfiguration;
import org.jivesoftware.smack.SmackAndroid;
import org.jivesoftware.smack.XMPPConnection;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class RegisterPhase1Activity extends ActionBarActivity {

	private EditText userEmailEdit;
	private String userEmail;
	private TextView signupEmail;
	private TextView signupPolicy;
	private Context mContext;
	private String HOST = LoginActivity.localIP;
	private int PORT = 5222;
	private String SERVICE = "localhost";
	private XMPPConnection connection;
	private Handler mHandler = new Handler();
	private ProgressDialog Asycdialog;
	
	public static Activity registerPhase1Activity;
	public static boolean isRunning;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_register_phase1);
		
		Log.d("RegisterPhase1","OnCreate has been called");
		mContext = this;
		registerPhase1Activity = this;
		isRunning = true;
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setTitle("Priva Messenger - Sign Up!");
		
		signupEmail = (TextView) findViewById(R.id.signupEmail);
		signupPolicy = (TextView) findViewById(R.id.signupPolicy);
		signupEmail.setText(Html.fromHtml("Make sure you correctly enter you email address as this will be used in the future to recover your password if you forget and receive important information about our services.")); 
		signupPolicy.setText(Html.fromHtml("By creating an account, you agree to the <a href=\"http://privamessenger.byethost16.com/termsofservice.html\">Terms of Service </a> and you acknowledge that you have read the <a href=\"http://privamessenger.byethost16.com/privacypolicy.html\">Privacy Policy</a>."));
		signupPolicy.setMovementMethod(LinkMovementMethod.getInstance());
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
	
	public void registerPhase2(final View v) {
		Log.d("RegisterPhase1","Continue botton has been pressed");
    	
    	userEmailEdit = (EditText) findViewById(R.id.email);
    	userEmail = userEmailEdit.getText().toString();
    	
    	if(!(isNetworkConnected())){
    		Log.d("RegisterPhase1","Not network detected");
    		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
            alertDialogBuilder.setIcon(R.drawable.ic_launcher);
			alertDialogBuilder.setTitle("No Network Avaliable!");
			alertDialogBuilder
				.setMessage("Your device is not connected to the Internet. Please check your connection and try again.")
				.setCancelable(false)
				.setNeutralButton("Got it!",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						dialog.cancel();
					}
				});
			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
    	}else{
    		new Thread(new Runnable() {
    	        public void run() {
    	        	mHandler.post(new Runnable() {
		                public void run() {
		                	Asycdialog = new ProgressDialog(RegisterPhase1Activity.this);
		                	Asycdialog.setMessage("Checking Email Avaliability...");
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
        		Log.d("RegisterPhase1","Connection to server OK");
        		
        		try {
            		InternetAddress internetAddress = new InternetAddress(userEmail);
            		internetAddress.validate();
            		Log.d("RegisterPhase1","Email validation OK");
            		
            		if(((Connect) getApplication()).DBEmailExists(userEmail)){
            			Log.d("RegisterPhase1","Email already exist in DB");
            			mHandler.post(new Runnable() {
                			public void run() {
                				Asycdialog.dismiss();
            			
                				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
                				alertDialogBuilder.setIcon(R.drawable.ic_launcher);
                				alertDialogBuilder.setTitle("Email In Use!");
                				alertDialogBuilder
                					.setMessage("The following email is already being used: "+userEmail)
                					.setCancelable(false)
                					.setNeutralButton("Got it!",new DialogInterface.OnClickListener() {
                						public void onClick(DialogInterface dialog,int id) {
                							userEmailEdit.setText("");
                							dialog.cancel();
                						}
                					});
                				AlertDialog alertDialog = alertDialogBuilder.create();
                				alertDialog.show();
                			}
        	            });
            		}else{
                	Intent intent = new Intent(v.getContext(), RegisterActivity.class);
                    intent.putExtra("Email", userEmail);
                    startActivity(intent);  
                    Asycdialog.dismiss();
                    Log.d("RegisterPhase1","All OK going to next activity");
                    isRunning = false;
            		}
            		
            	} catch (AddressException e) {

            		Log.d("RegisterPhase1","Invalid email");
            		mHandler.post(new Runnable() {
            			public void run() {
            				Asycdialog.dismiss();
            				
            				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
            				alertDialogBuilder.setIcon(R.drawable.ic_launcher);
            				alertDialogBuilder.setTitle("Invalid Email!");
            				alertDialogBuilder
            					.setMessage("This email is not valid: "+userEmail)
            					.setCancelable(false)
            					.setNeutralButton("Got it!",new DialogInterface.OnClickListener() {
            						public void onClick(DialogInterface dialog,int id) {
            							userEmailEdit.setText("");
            							dialog.cancel();
            						}
            					});
            				AlertDialog alertDialog = alertDialogBuilder.create();
            				alertDialog.show();
            			}
    	            });
            			
            			e.printStackTrace();
            	}

        	} catch (Exception e) {
        		Log.d("RegisterPhase1","Server is Down");
        		mHandler.post(new Runnable() {
        			public void run() {
        				Asycdialog.dismiss();
	                	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
	                    alertDialogBuilder.setIcon(R.drawable.ic_launcher);
	    				alertDialogBuilder.setTitle("Server Down!");
	    				alertDialogBuilder
	    					.setMessage("There's an error with the connection to our server.")
	    					.setCancelable(false)
	    					.setNeutralButton("Got it!",new DialogInterface.OnClickListener() {
	    						public void onClick(DialogInterface dialog,int id) {
	    							dialog.cancel();
	    						}
	    					});
	    				AlertDialog alertDialog = alertDialogBuilder.create();
	    				alertDialog.show();
                    
        			}
        		});
        		e.printStackTrace();
        	}
    	        }
        	}).start();
    	}
    } 
	
	@Override
	protected void onResume(){
		super.onResume();
		Log.d("RegisterPhase1Activity","Onresume() has been called");
		isRunning = true;
	}
    
    @Override
	protected void onPause(){
		super.onPause();
		Log.d("RegisterPhase1Activity","OnPause() has been called");
		isRunning = false;	
	}
}
