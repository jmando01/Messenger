package com.example.messenger;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends ActionBarActivity {
	
	private EditText usernameEdit;
    private EditText passwordEdit;
    private String username;
    private String password;
    private Editor ed;
    private Context context;
    private TextView signInTrouble;
    private ProgressDialog Asycdialog;
    public static CheckBox checkbox;
    public static boolean isRunning;
    public static SharedPreferences pref;
    public static Activity loginActivity;
    public static String localIP = "10.0.0.16";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_login);        
        
        Log.d("LoginActivity","Oncreate has started");
        loginActivity = this;
        isRunning = true; 
        checkbox = (CheckBox) findViewById(R.id.remember);
        signInTrouble = (TextView) findViewById(R.id.signInTrouble);
        signInTrouble.setText("Having trouble signing in?");
        context = this;
        setTitle("Priva Messenger");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        //Aca permitimos que se pueda acceder a internet desde la interfaz de usuario. Sin tener que crear un hilos.
        //Este problema debe resolverse mas adelante. Investigar...
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            Log.d("LoginActivity","Strict mode has been changed");
        }
        
        try {
        	((Connect) this.getApplication()).Disconect();
		}catch (Exception e) {
			// TODO: handle exception
			e.getStackTrace();
		}
         
        //Esta parte nos sirve para entrar a la sesion automaticamente.
        pref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        if(pref.getBoolean("firstRun", false)){

        	Log.d("LoginActivity","NOT first run");
        	
        	Asycdialog = new ProgressDialog(LoginActivity.this);
        	Asycdialog.setMessage("Logging In...");
    		Asycdialog.setCanceledOnTouchOutside(false);
    		Asycdialog.show();
        	
            username = pref.getString("username", "default");
			password = pref.getString("password", "default");

			try {
				((Connect) this.getApplication()).conn();
			}catch (Exception e) {
				// TODO: handle exception
				Log.d("LoginActivity"," AutoLogin Connect has failed");
				e.getStackTrace();
			}
        } 
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
	protected void onResume(){
		super.onResume();
		Log.d("LoginActivity","Onresume() has been called");
		isRunning = true;
		// Register mMessageReceiver to receive messages.
		  LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("my-event"));
	}
    
    // handler for received Intents for the "my-event" event 
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context lContext, Intent BRintent) {
        // Extract data included in the Intent
        String message = BRintent.getStringExtra("message");
        Log.d("receiver", " BR OK Got message: " + message); 
        
        if(!(isNetworkConnected()) && !(pref.getBoolean("firstRun", false))){
        	Asycdialog.dismiss();
    		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
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
        	//Se verifica si la conexion con el servidor fue exitosa, si los datos del usuario son correctos y si el rememberme no esta activo.
        	if((((Connect) getApplication()).getConnection().isConnected() == true) && (Connect.userVerification == true) && !(pref.getBoolean("firstRun", false)) ){
        	
        		Log.d("LoginActivity","User data and conection confirmed");
        		Intent mIntent = new Intent(LoginActivity.this, ChatListActivity.class);
        		startActivity(mIntent);
        		Asycdialog.dismiss();
        		isRunning = false;
 
        		if(checkbox.isChecked() == true){ 
        			Log.d("LoginActivity","Checkbox is true");
        			ed.putBoolean("firstRun", true);
        			ed.commit();      
        			finish();	
        		}
        	}
        
        	//Verifica si el rememberme esta activo.
        	if(pref.getBoolean("firstRun", false)){
        		Log.d("LoginActivity","Autologin successful with: " + username + ". and password: " + password); 
        		Intent Mintent = new Intent(LoginActivity.this, ChatListActivity.class);
        		startActivity(Mintent);
        		Asycdialog.dismiss(); 
        		isRunning = false;
        		finish();
        	}
        
        	//Verifica si no hubo conexion al servidor y el remember no esta activo.
        	if(((Connect) getApplication()).getConnection().isConnected() == false && !(pref.getBoolean("firstRun", false))){
        		Log.d("LoginActivity","Connection to the server failed");
        		Asycdialog.dismiss();
        		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        		alertDialogBuilder.setIcon(R.drawable.ic_launcher);
        		alertDialogBuilder.setTitle("Server Connection Error!");
        		alertDialogBuilder
        			.setMessage("An error has occurr while connecting to the server. Please try again.")
        			.setCancelable(false)
        			.setNeutralButton("Got it!",new DialogInterface.OnClickListener() {
        				public void onClick(DialogInterface dialog,int id) {
        					dialog.cancel();
        				}
    				});
        		AlertDialog alertDialog = alertDialogBuilder.create();
    			alertDialog.show();
        	}
        
        	//En caso de que la contrasena o el nombre de usuario sea incorrecta mostramos el aviso.
        	if((((Connect) getApplication()).getConnection().isConnected() == true) && (Connect.userVerification == false) && !(pref.getBoolean("firstRun", false))){
        		Log.d("LoginActivity","User or password is wrong");
        		Asycdialog.dismiss();
        		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        		alertDialogBuilder.setIcon(R.drawable.ic_launcher);
        		alertDialogBuilder.setTitle("Login Error!");
        		alertDialogBuilder
    				.setMessage("Your username or password is wrong. Please try again.")
    				.setCancelable(false)
    				.setNeutralButton("Got it!",new DialogInterface.OnClickListener() {
    					public void onClick(DialogInterface dialog,int id) {
    						dialog.cancel();
    					}
    				});
        		AlertDialog alertDialog = alertDialogBuilder.create();
        		alertDialog.show();
        		((Connect) getApplication()).Disconect();
        	}
        }
      }
    };
    
    @Override
	protected void onPause(){
		super.onPause();
		Log.d("LoginActivity","OnPause() has been called");
		isRunning = false;	
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
	}
    
  //Este es el OnClick Listener del proceso de Login.
    public void login(View v) { 
    	
    	Log.d("LoginActivity","Login button has been pressed");
    	Asycdialog = new ProgressDialog(LoginActivity.this);
    	Asycdialog.setMessage("Logging In...");
		Asycdialog.setCanceledOnTouchOutside(false);
		Asycdialog.show();

   	    usernameEdit = (EditText) findViewById(R.id.username);
        passwordEdit = (EditText) findViewById(R.id.password);
    	username = usernameEdit.getText().toString();
        password = passwordEdit.getText().toString();
        
        ed = pref.edit();
    	ed.putString("username", username);
    	ed.putString("password", password);
    	ed.commit();
    	
        //Nos conectamos al servidor con los datos de usuario.
        try {
        	((Connect) this.getApplication()).conn(); 
		}catch (Exception e) {
			// TODO: handle exception
			e.getStackTrace();
		} 
    } 

    public void register(View v) {
    	Log.d("LoginActivity","Register button has been pressed");
       	Intent intent = new Intent(this, RegisterPhase1Activity.class);
       	startActivity(intent);  	 
        isRunning = false;
    } 
    
    public void signInTrouble(final View v){
    	Log.d("LoginActivity","SignInTrouble button has been pressed");
    	final CharSequence[] items = {
                "I don't remember my password", "I don't remember my username", "I'm having other troubles"
        };
    	
    	AlertDialog.Builder builder = new AlertDialog.Builder(context);
    	 	builder.setIcon(R.drawable.ic_launcher);
	        builder.setTitle("Trouble Signing In?");
	        builder.setItems(items, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int item) {
	            	if(item == 0){
	            		Intent intent = new Intent(v.getContext(), ForgotPasswordActivity.class);
	            		startActivity(intent);
	            	    isRunning = false;
	            	}
	            	if(item == 1){
	            		Intent intent = new Intent(v.getContext(), ForgotUsernameActivity.class);
	            		startActivity(intent);
	            	    isRunning = false;
	            	}
	            	if(item == 2){
	            		Intent intent = new Intent(v.getContext(), TestConnectionActivity.class);
	            		startActivity(intent);
	            	    isRunning = false;
	            	}
	            }
	        });
	    AlertDialog alert = builder.create();
	    alert.show();	 
    }
}
