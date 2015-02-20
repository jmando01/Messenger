package com.example.messenger;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;


public class ESupportActivity extends ActionBarActivity {
	
	private Mail mail;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_esupport);
		
		/*Log.d("Ping Server", m.PingServerSMTP());*/
	
	}
	
	public void SendEmail(View v){
		
		EditText editTextMessag = (EditText) findViewById(R.id.reportMessage);
		String message = editTextMessag.getText().toString();
		
		mail = new Mail("","");
		
	    String[] toArr = {"privaprueba01@gmail.com"}; 
	    mail.setTo(toArr); 
	    mail.setFrom("currentUserEmail@gmail.com"); //Esto como que no tiene sentido.
	    mail.setSubject("Support Report - Priva Messenger"); 
	    mail.setBody(message); 
	      
	    try { 
	    	/*m.addAttachment("/sdcard/filelocation"); */
	   
	        if(mail.send()){
	            Toast.makeText(this, "Email was sent successfully.", Toast.LENGTH_LONG).show(); 
	        }else{ 
	            Toast.makeText(this, "Email was not sent.", Toast.LENGTH_LONG).show(); 
	         } 
	        }catch(Exception e) { 
	          //Toast.makeText(MailApp.this, "There was a problem sending the email.", Toast.LENGTH_LONG).show(); 
	          Log.d("MailApp", "Could not send email", e); 
	          Toast.makeText(this, "Email was not sent.", Toast.LENGTH_LONG).show(); 
	          e.printStackTrace();
	        }		
	}
}
