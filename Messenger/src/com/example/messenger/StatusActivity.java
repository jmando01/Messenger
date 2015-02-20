package com.example.messenger;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;


public class StatusActivity extends Activity {

	public static String state;
	
	public static TextView textView;
	
	public static boolean isRunning;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_status);
		
		setTitle("Priva Messenger");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		isRunning = true;
		
		textView = (TextView) findViewById(R.id.currentstatus);
		textView.setText(LoginActivity.pref.getString("state", "Online"));
		
		String [] status = new String [] {"Online", "Busy", "At the phone", "At Work", "Driving", "Offline"} ;
		
		ListView listView = (ListView) findViewById(R.id.statuslv);
		
		listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, status));
		
		listView.setOnItemClickListener(new OnItemClickListener() {
						public void onItemClick(AdapterView<?> parent, View view,
								int position, long id) {
							// When clicked, show a toast with the TextView text
							if(Connect.connectionStatus == true){
								state = (String) parent.getItemAtPosition(position);
								
								textView.setText(state);
								
								Editor ed = LoginActivity.pref.edit();
								ed.putString("state", state);
						    	ed.commit();
								
								//Connect.statusglobal = state;
								
								((Connect) getApplication()).UpdateStatus(state);
								
								Toast.makeText(getApplicationContext(),
										"Clicked on : " + state, Toast.LENGTH_LONG)
										.show();
							}else{
								Toast.makeText(getApplicationContext(),
										"There is no connection...", Toast.LENGTH_LONG)
										.show();
							}
						}
					});
		
	}
	

	@Override
    protected void onPause(){
		super.onPause();
		Log.d("StatusActivity","Estoy en el onPause");
		isRunning = false;
	}
	
	public static void updateStatus(String state){
		textView.setText(state);
	}
	
	public void BotonDelActionBar(MenuItem item){
		Intent intent = new Intent(this, AddContactActivity.class);
		startActivity(intent);
        isRunning = false;
        finish();
	}
  	
  	public void StatusBtn(MenuItem item){
		Intent intent = new Intent(this, StatusActivity.class);
		startActivity(intent);
	}
  	
  	public void PerfilBtn(MenuItem item){
		Intent intent = new Intent(this, OptionsActivity.class);
		startActivity(intent);
        isRunning = false;
        finish();
	}
  	
  	public void ContactBtn(MenuItem item){
		Intent intent = new Intent(this, ContactActivity.class);
		startActivity(intent);
        isRunning = false;
        finish();
	}
  	
  	public void chatListBtn(MenuItem item){
		Intent intent = new Intent(this, ChatListActivity.class);
		startActivity(intent);
        isRunning = false;
        finish();
	}

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.status, menu);
		return true;
	}
}
