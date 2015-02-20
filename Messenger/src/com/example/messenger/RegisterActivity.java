package com.example.messenger;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.AndroidConnectionConfiguration;
import org.jivesoftware.smack.SmackAndroid;
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

public class RegisterActivity extends ActionBarActivity {
	
	private EditText usernameRegisterEdit;
    private EditText passwordRegisterEdit; 
    private EditText reTypePasswordRegisterEdit;
    private String usernameRegister;
    private String passwordRegister;
    private String reTypePasswordRegister;
    private String userEmail;
    private String HOST = LoginActivity.localIP;
	private int PORT = 5222;
	private String SERVICE = "localhost";
	private XMPPConnection connection;
	private Handler mHandler = new Handler();
	private Mail mail;
	private ProgressDialog Asycdialog;
	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_register);
		
		Log.d("RegisterActivity","Oncreate has been started");
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setTitle("Priva Messenger");
		mContext = this;
		Intent intent = getIntent();
		userEmail = intent.getStringExtra("Email");
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
	
	public void register(View v) { 
		
		Log.d("ResgisterActivity","Register button has been pressed");
		
		usernameRegisterEdit = (EditText) findViewById(R.id.username);
		passwordRegisterEdit = (EditText) findViewById(R.id.password);
		reTypePasswordRegisterEdit = (EditText) findViewById(R.id.retype_password);	
		usernameRegister = usernameRegisterEdit.getText().toString();
		passwordRegister = passwordRegisterEdit.getText().toString();
		reTypePasswordRegister = reTypePasswordRegisterEdit.getText().toString();
		
		Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
    	Matcher m = p.matcher(usernameRegister);
    	boolean b = m.find();
		
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
    		if(b){
    			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
            	alertDialogBuilder.setIcon(R.drawable.ic_launcher);
				alertDialogBuilder.setTitle("Invalid Username!");
				alertDialogBuilder
					.setMessage("The username can not contain special characters. Please try again")
					.setCancelable(false)
					.setNeutralButton("Got it!",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int id) {
							usernameRegisterEdit.setText("");
							dialog.cancel();
							
						}
					});
				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
    		}else{
    			if(!(passwordRegister.equals(reTypePasswordRegister))){
    				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
    				alertDialogBuilder.setIcon(R.drawable.ic_launcher);
    				alertDialogBuilder.setTitle("Password Mismatch!");
    				alertDialogBuilder
 						.setMessage("The passwords are not the same. Please try again.")
 						.setCancelable(false)
 						.setNeutralButton("Got it!",new DialogInterface.OnClickListener() {
 							public void onClick(DialogInterface dialog,int id) {
 								passwordRegisterEdit.setText("");
 								reTypePasswordRegisterEdit.setText("");
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
			                	Asycdialog = new ProgressDialog(RegisterActivity.this);
			                	Asycdialog.setMessage("Signing Up...");
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
				
			        		if(connection.isConnected()){		         
			        			try {
			        				//En caso de que la conexion sea diferente de null creamos el nuevo usuario en el servidor.       
			        				AccountManager am = new AccountManager(connection);
			                  
			        				Map<String, String> mp = new HashMap<String, String>();

			        				// adding or set elements in Map by put method key and value
			        				// pair
			        				mp.put("username", usernameRegister);
			        				mp.put("password", passwordRegister);
			        				mp.put("name", usernameRegister);
									mp.put("email", userEmail);

									// am.createAccount(mConfig.userName, mConfig.password);
									am.createAccount(usernameRegister, passwordRegister, mp);
								
									try {
										connection.disconnect();
									} catch (Exception e) {
										// TODO: handle exception
										Log.d("RegisterActivity","Error disconecting from server");
									}
								
								
									mail = new Mail("","");
					    		
									String[] toArr = {userEmail}; 
									mail.setTo(toArr); 
									mail.setFrom("priva-no-reply@gmail.com"); 
									mail.setSubject("Welcome! - Priva Messenger"); 
									mail.setBody("Priva Messenger Account Support\n\n\n\nWelcome to the Priva Messenger family! " +
	        	        				"You have registered an account with username: '"+usernameRegister+"' and email: '"+userEmail+"'." +
	        	        				" Please do not reply to this email address. To solve an issue or learn more about Priva " +
	        	        				"Messenger, visit our website: http://privamessenger.byethost16.com/");
	    	      
									try {
										mail.send();
									}catch (Exception e) {
										// TODO: handle exception
										mHandler.post(new Runnable() {
											public void run() {
												Asycdialog.dismiss();
												AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
												alertDialogBuilder.setIcon(R.drawable.ic_launcher);
												alertDialogBuilder.setTitle("Signed Up Successfull!");
												alertDialogBuilder
						    						.setMessage("This username has been signed up successfully: " + usernameRegister +" but an error occurr while sending a Welcome Email.")
						    						.setCancelable(false)
						    						.setNeutralButton("Got it!",new DialogInterface.OnClickListener() {
						    							public void onClick(DialogInterface dialog,int id) {
						    								dialog.cancel();
						    								RegisterPhase1Activity.registerPhase1Activity.finish();
						    								onBackPressed();
						    							}
						    						});
						    					AlertDialog alertDialog = alertDialogBuilder.create();
						    					alertDialog.show();
											}
										});
									}
	        	        		
									mHandler.post(new Runnable() {
										public void run() {
					                    	Asycdialog.dismiss();
					                    	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
					                    	alertDialogBuilder.setIcon(R.drawable.ic_launcher);
					    					alertDialogBuilder.setTitle("Signed Up Successfull!");
					    					alertDialogBuilder
					    						.setMessage("This username has been signed up successfully: " + usernameRegister)
					    						.setCancelable(false)
					    						.setNeutralButton("Got it!",new DialogInterface.OnClickListener() {
					    							public void onClick(DialogInterface dialog,int id) {
					    								dialog.cancel();
					    								RegisterPhase1Activity.registerPhase1Activity.finish();
					    								onBackPressed();
					    							}
					    						});
					    					AlertDialog alertDialog = alertDialogBuilder.create();
					    					alertDialog.show();
										}
									});
		 
									}catch (Exception e) {
										// En esta parte solo ocurre una excepcion solo si existe un usuario con el mismo nombre.
										// Nos aprovechamos de esto para mostrar si el usuario ya existe como un error.
										mHandler.post(new Runnable() {
											public void run() {
						                   	 	Asycdialog.dismiss();
						                    	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
						                    	alertDialogBuilder.setIcon(R.drawable.ic_launcher);
						                    	alertDialogBuilder.setTitle("Username Already Exists!");
						    					alertDialogBuilder
						    						.setMessage("This username already exists: " + usernameRegister)
						    						.setCancelable(false)
						    						.setNeutralButton("Got it!",new DialogInterface.OnClickListener() {
						    							public void onClick(DialogInterface dialog,int id) {
						    								usernameRegisterEdit.setText("");
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
			        	}catch (Exception e) {
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
	}
}
