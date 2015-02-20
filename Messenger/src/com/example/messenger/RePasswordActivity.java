package com.example.messenger;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.AndroidConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import android.support.v7.app.ActionBarActivity;
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

public class RePasswordActivity extends ActionBarActivity {

	private Intent intent;
	private String HOST = LoginActivity.localIP;
	private int PORT = 5222;
	private String SERVICE = "localhost";
	private XMPPConnection connection;
	private Handler mHandler = new Handler();
	private Context mContext;
	private ProgressDialog Asycdialog;
	private EditText codeEdit;
	private EditText passEdit;
	private EditText reWriteEdit;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_re_password);
		
		intent = getIntent();
		mContext = this;
		setTitle("Priva Messenger");
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);	
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
	
	@Override
	public void onBackPressed() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setIcon(R.drawable.ic_launcher);
		alertDialogBuilder.setTitle("Code Will Expired!");
		alertDialogBuilder
			.setMessage("If you quit the present window your code to reset your password will expired. Are you sure you want to continue?")
			.setCancelable(false)
			.setPositiveButton("Yes!",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					dialog.cancel();
					finish();
				}
			});
		alertDialogBuilder.setNegativeButton("No!",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				dialog.cancel();
			}
		});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}
	
	public void RecoverPhaseEnd(View v){
		
		codeEdit = (EditText) findViewById(R.id.code);
    	final String code = codeEdit.getText().toString();
    	passEdit = (EditText) findViewById(R.id.pass);
    	final String pass = passEdit.getText().toString();
    	reWriteEdit = (EditText) findViewById(R.id.rewrite);
    	final String reWrite = reWriteEdit.getText().toString();

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
        	new Thread(new Runnable() {
    	        public void run() {
    	        	mHandler.post(new Runnable() {
		                public void run() {
		                	Asycdialog = new ProgressDialog(RePasswordActivity.this);
		                	Asycdialog.setMessage("Resetting Password...");
		                	Asycdialog.setCanceledOnTouchOutside(false);
		                	Asycdialog.show();
		                }
		            });
        	try{
        		AndroidConnectionConfiguration connConfig = new AndroidConnectionConfiguration(HOST, PORT, SERVICE);
        		connection = new XMPPConnection(connConfig);
        		connection.connect();
        		Log.d("RePassword","Connection to the server successfull");

        		if((intent.getStringExtra("code").equals(code)) && (pass.equals(reWrite))){
        			Log.d("RePassword","In IF, everything correct");
        			try {
        				connection.login(((Connect) getApplication()).PasswordRecovery(intent.getStringExtra("email")), "default");
        				Log.d("RePassword","Login to server successfull");
    				
        				AccountManager am = new AccountManager(connection);
        				am.changePassword(pass);
        				
        				try {
        					connection.disconnect();
						} catch (Exception e) {
							// TODO: handle exception
							Log.d("RePasswordActivity","Error disconecting from server");
						}
        				
        				mHandler.post(new Runnable() {
			                public void run() {
			                	Asycdialog.dismiss();
			                	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
			                    alertDialogBuilder.setIcon(R.drawable.ic_launcher);
			    				alertDialogBuilder.setTitle("Password Reset Successfull!");
			    				alertDialogBuilder
			    					.setMessage("The password has been reseted successfully.")
			    					.setCancelable(false)
			    					.setNeutralButton("Got it!",new DialogInterface.OnClickListener() {
			    						public void onClick(DialogInterface dialog,int id) {
			    							dialog.cancel();
			    							ForgotPasswordActivity.forgotPasswordActivity.finish();
						                	finish();
			    						}
			    					});
			    				AlertDialog alertDialog = alertDialogBuilder.create();
			    				alertDialog.show();
			                }
			            });
    				
        				Log.d("RePassword","Change password successfull");
        			}catch (Exception e) {
        				// TODO: handle exception
        				Log.d("RePassword","Resetting Password Failed");
        				mHandler.post(new Runnable() {
			                public void run() {
			                	Asycdialog.dismiss();
			                	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
			                    alertDialogBuilder.setIcon(R.drawable.ic_launcher);
			    				alertDialogBuilder.setTitle("Resetting Password Failed!");
			    				alertDialogBuilder
			    					.setMessage("There's been an error resetting your password in our database. Please try again.")
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
    		
        		if(!(pass.equals(reWrite))){
        			Log.d("RePassword","The password are not the same");
        			
        			mHandler.post(new Runnable() {
		                public void run() {
    
		                	Asycdialog.dismiss();
		                	
		                	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
		                    alertDialogBuilder.setIcon(R.drawable.ic_launcher);
		    				alertDialogBuilder.setTitle("Password Mismatch!");
		    				alertDialogBuilder
		    					.setMessage("The passwords are not the same. Please try again.")
		    					.setCancelable(false)
		    					.setNeutralButton("Got it!",new DialogInterface.OnClickListener() {
		    						public void onClick(DialogInterface dialog,int id) {
		    							passEdit.setText("");
		    							reWriteEdit.setText("");
		    							dialog.cancel();
		    						}
		    					});
		    					AlertDialog alertDialog = alertDialogBuilder.create();
		    					alertDialog.show();
		                }
		            });
        		}else if(!(intent.getStringExtra("code").equals(code))){
        			Log.d("RePassword","Invalid code");
        			mHandler.post(new Runnable() {
		                public void run() {
		                	Asycdialog.dismiss();
		                	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
		                    alertDialogBuilder.setIcon(R.drawable.ic_launcher);
		    				alertDialogBuilder.setTitle("Wrong Code!");
		    				alertDialogBuilder
		    					.setMessage("The code you typed is incorrect")
		    					.setCancelable(false)
		    					.setNeutralButton("Got it!",new DialogInterface.OnClickListener() {
		    						public void onClick(DialogInterface dialog,int id) {
		    							codeEdit.setText("");
		    							dialog.cancel();
		    						}
		    					});
		    				AlertDialog alertDialog = alertDialogBuilder.create();
		    				alertDialog.show();
		                }
		            });
        		}
        	}catch (Exception e) {		
        		Log.d("RePassword","Connection to the server failed");
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
}
