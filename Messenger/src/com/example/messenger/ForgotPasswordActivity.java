package com.example.messenger;

import java.util.Random;
import javax.mail.internet.InternetAddress;
import org.jivesoftware.smack.AndroidConnectionConfiguration;
import org.jivesoftware.smack.SmackAndroid;
import org.jivesoftware.smack.XMPPConnection;
import android.support.v7.app.ActionBarActivity;
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

public class ForgotPasswordActivity extends ActionBarActivity {
	
	private Mail mail;
	private EditText userEmailEdit;
	private String HOST = LoginActivity.localIP;
	private int PORT = 5222;
	private String SERVICE = "localhost";
	private XMPPConnection connection;
	private Handler mHandler = new Handler();
	private Context mContext;
	private ProgressDialog Asycdialog;
	
	public static Activity forgotPasswordActivity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_forgot_password);

		mContext = this;
		forgotPasswordActivity = this;
		setTitle("Priva Messenger");
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}
	
	private boolean validateEmail(String userEmail){
		try {
			InternetAddress internetAddress = new InternetAddress(userEmail);
			internetAddress.validate();
			return true;
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return false;
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

	public void sendEmail(View v){
		
		userEmailEdit = (EditText) findViewById(R.id.emailET);
    	final String userEmail = userEmailEdit.getText().toString();
    	
    	if(!(isNetworkConnected())){
    		//There is no network avaliable
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
    		if(!(validateEmail(userEmail))){
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
    		}else{
    			new Thread(new Runnable() {
    				public void run() {
    					mHandler.post(new Runnable() {
    						public void run() {
    							Asycdialog = new ProgressDialog(ForgotPasswordActivity.this);
    							Asycdialog.setMessage("Sending Email...");
    							Asycdialog.setCanceledOnTouchOutside(false);
    							Asycdialog.show();
		            	
    						}
    					});
    	        	
    	        	try {
    	        		Context context = getApplicationContext();
			        	SmackAndroid.init(context);
 
    	        		AndroidConnectionConfiguration connConfig = new AndroidConnectionConfiguration(HOST, PORT, SERVICE);
    	        		connection = new XMPPConnection(connConfig);
    	        		connection.connect();
    	        		connection.disconnect();
    	        		
    	        		if(((Connect) getApplication()).DBEmailExists(userEmail)){
        	        		Log.d("FortgotPass", "Found email in remote DB: " + userEmail);
        	        		Random random = new Random();
        	        		final String code = Integer.toString(random.nextInt(99999 - 10001) + 100000);
    			
        	        		Log.d("FortgotPass", "Generated code: " + code);

        	        		mail = new Mail("","");
        	        		String[] toArr = {userEmail}; 
        	        		mail.setTo(toArr); 
        	        		mail.setFrom("priva-no-reply@gmail.com"); 
        	        		mail.setSubject("Password Recovery Code - Priva Messenger"); 
        	        		mail.setBody("Priva Messenger Account Support\n\n\n\nTo start the password resetting" +
        	        				" process of your Priva Messenger account which email is: "+userEmail+" copy the " +
        	        				"following code: '"+code+"' and enter it in the 'Enter Code' box in the application " +
        	        				"and follow the steps that are indicated. \n\n\n\nIf you received this email by error, " +
        	        				"it's likely that another user entered your email address by mistake while trying to reset " +
        	        				"its password. If you did not sent a Password Recovery Email you don’t need to take any further " +
        	        				"action and can ignore this email without problems. \n\n\n\nSincerely, Team Priva Messenger Account " +
        	        				"Support.  Please do not reply to this email address. To solve an issue or learn more about Priva " +
        	        				"Messenger, visit our website: http://privamessenger.byethost16.com/");
    	      
        	        		Log.d("FortgotPass", "Email was send to: " + userEmail);
    	      
        	        		try { 
        	        			mail.send();
        	        				
        	        			mHandler.post(new Runnable() {
        	        				public void run() {
    	            
        				                Asycdialog.dismiss();
        				                	
        				                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
    					                alertDialogBuilder.setIcon(R.drawable.ic_launcher);
    					    			alertDialogBuilder.setTitle("Email Was Successfully Send!");
    					    			alertDialogBuilder
    					    				.setMessage("The email was successfully sent to: " + userEmail)
    					    				.setCancelable(false)
    					    				.setNeutralButton("Got it!",new DialogInterface.OnClickListener() {
    					    					public void onClick(DialogInterface dialog,int id) {
    					    						dialog.cancel();	
    					    						Intent intent = new Intent(ForgotPasswordActivity.this, RePasswordActivity.class);
    			    				                intent.putExtra("email", userEmail);
    			    				                intent.putExtra("code", code);
    			    				                startActivity(intent);
    					    					}
    					    				});
    					    			AlertDialog alertDialog = alertDialogBuilder.create();
    					    			alertDialog.show();
        				            }
        				        });
        	        		}catch(Exception e) { 
        	        			Log.d("ForgotPass", "Could not send email");  
        	        			mHandler.post(new Runnable() {
    				                public void run() {
    				                	Asycdialog.dismiss();
    				                	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
    				                    alertDialogBuilder.setIcon(R.drawable.ic_launcher);
    				    				alertDialogBuilder.setTitle("Error Sending Email!");
    				    				alertDialogBuilder
    				    					.setMessage("An error has ocurred while sending an email to: " + userEmail)
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
        	        	}else{
        	        		Log.d("ForgotPass", "The email does not exist"); 
        	        		
        	        		mHandler.post(new Runnable() {
    			                public void run() {   
    			                	Asycdialog.dismiss();
    			                	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
    			                    alertDialogBuilder.setIcon(R.drawable.ic_launcher);
    			    				alertDialogBuilder.setTitle("The Email Does Not Exists!");
    			    				alertDialogBuilder
    			    					.setMessage("The email does not exist in our database. Make sure you typed the correct email of your account.")
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
        	        	}
    	        	} catch (Exception e) {
    	        		// TODO: handle exception
    	        		mHandler.post(new Runnable() {
			                public void run() {
			                	Asycdialog.dismiss();
			                	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
			                    alertDialogBuilder.setIcon(R.drawable.ic_launcher);
			    				alertDialogBuilder.setTitle("Server Down!");
			    				alertDialogBuilder
			    					.setMessage("The connection to our server has failed")
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
    	        		e.getStackTrace();
    	        	}
    	        }
    		}).start();
    		}}
	}
}
