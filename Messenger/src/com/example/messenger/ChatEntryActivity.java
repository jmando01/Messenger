package com.example.messenger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.LastActivityManager;
import org.jivesoftware.smackx.packet.LastActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class ChatEntryActivity extends Activity {
	
	private Roster roster;
	private Handler mHandler = new Handler();
	private Intent intent;
	private int GlobalID = 0;
	private CountDownTimer CDT;
	private Button enterBtn;
	private String textMessage;
	private EditText editText1;
	private ArrayList<OneComment> history = new ArrayList<OneComment>();
	private TextView remoteUser;
	private TextView localUser;
	private String privacy;
	
	private static boolean priva;
	private static com.example.messenger.ChatArrayAdapter adapter;
	private static ListView lv;
	
	public static String remoteUsername;
	public static TextView status;
	public static boolean isRunning;
	public static Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_chat_entry);
		
		context = this;
		isRunning = true;
		history.clear();		
		intent = getIntent();
		remoteUsername = intent.getStringExtra("remoteUsername");
		priva = intent.getBooleanExtra("priva", false);
		privacy = priva? "p" : "n";
		enterBtn = (Button)findViewById(R.id.send_btn);
		setTitle(remoteUsername.substring(0, remoteUsername.indexOf("@")));
		
		((Connect) this.getApplication()).ClearMsjCounter(remoteUsername, priva);

		remoteUser = (TextView) findViewById(R.id.remote_user);
		remoteUser.setText(" ");
		
		localUser = (TextView) findViewById(R.id.local_user);
		localUser.setText(" ");
		
		status = (TextView) findViewById(R.id.status);
		
		editText1 = (EditText) findViewById(R.id.ipad);	
		
		//setContactImage();
		setContactStatus();

		lv = (ListView) findViewById(R.id.listView1);
		lv.setDivider(null);
		adapter = new ChatArrayAdapter(getApplicationContext(), R.layout.listitem_chat);
		lv.setAdapter(adapter);
		
		getHistory();
		
		lv.setOnItemClickListener(new OnItemClickListener() {
        	@Override
        	public void onItemClick(AdapterView<?> a, View v, int position, long id) { 
        		Object o = lv.getItemAtPosition(position);
            	final OneComment oneComment = (OneComment)o;
            
            	if(oneComment.getCountDown() == 0 || oneComment.getCountDown() == 99 ){
            		Toast.makeText(ChatEntryActivity.this, " "+Connect.TimeConverter(oneComment.getDate()), Toast.LENGTH_LONG).show();
            	}
        		
        		if(oneComment.getCountDown() != 0 && oneComment.getCountDown() < 60){
        			
        			
        			DatabaseHandler db = new DatabaseHandler(context);
        			MessageDB message = db.getMessageDB(oneComment.getID());
        			db.close();
        			
        			String ChatMessage = message.getBody().substring(0, message.getBody().length() - 3);
        			
        			GlobalID = oneComment.getID();
        			
        			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    alertDialogBuilder.setIcon(R.drawable.ic_launcher);
            		alertDialogBuilder.setTitle("Self-Destruct Message: " + oneComment.getCountDown()+ "'s");
            		alertDialogBuilder
            			.setMessage(ChatMessage)
            			.setCancelable(false)
            			.setNeutralButton("Close!",new DialogInterface.OnClickListener() {
            				public void onClick(final DialogInterface dialog,int id) {
            					adapter.removeItem(oneComment);
    							adapter.notifyDataSetChanged();
    							CDT.cancel();
    							DatabaseHandler db = new DatabaseHandler(context);
    			    			db.deleteMessage(new MessageDB(GlobalID, "delete", "delete", "delete", "delete", priva)); 
    			    			db.close();
            					dialog.cancel();
            				}
            			});
            			final AlertDialog alertDialog = alertDialogBuilder.create();
            			alertDialog.show();
            			
            		CDT = new CountDownTimer(oneComment.getCountDown()*1000, 1000) {
    						public void onTick(final long millisUntilFinished) {   
    							Log.d("ChatEntryActivity","CounterDown seconds remaining: " + millisUntilFinished / 1000);      
    						}

    						public void onFinish() {
    							Log.d("ChatEntryActivity","CounterDown Finish!");
    							adapter.removeItem(oneComment);
    							adapter.notifyDataSetChanged();
    							DatabaseHandler db = new DatabaseHandler(context);
    			    			db.deleteMessage(new MessageDB(GlobalID, "delete", "delete", "delete", "delete", priva)); 
    			    			db.close();
    			    			alertDialog.dismiss();
    			    			GlobalID = 0;	
    						}
    				}.start(); 
        		}
        	}  
        });
		
		lv.setOnItemLongClickListener(new OnItemLongClickListener(){
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, final View view, int position, long id) {
				Object o = lv.getItemAtPosition(position);
	            final OneComment oneComment = (OneComment)o;
	            	
				final CharSequence[] items = {
					"Delete Message", "Close"
			    };

			    AlertDialog.Builder builder = new AlertDialog.Builder(context);
			    builder.setTitle("Make your selection");
			    builder.setItems(items, new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int item) {
			            if(item == 0){
			            	adapter.removeItem(oneComment);
    						adapter.notifyDataSetChanged();
    						if(oneComment.getID() != -1){
    							DatabaseHandler db = new DatabaseHandler(context);
        			    		db.deleteMessage(new MessageDB(oneComment.getID(), "delete", "delete", "delete", "delete", priva)); 
        			    		db.close();
        			    		((Connect) getApplication()).DBDeleteMessage(oneComment.getDate());
    						}
    			    		dialog.dismiss();
			            }
			            	
			            if(item == 1){
    			    		dialog.dismiss();
			            }
			            	
			        }
			   });
			   AlertDialog alert = builder.create();
			   alert.show();
					
			   return true;
		}} );

		enterBtn.setOnLongClickListener(new OnLongClickListener() { 
	        @Override
	        public boolean onLongClick(View v) {
	        	
	        	final CharSequence[] items = {
		                "3's", "4's","5's", "6's","7's",
		        };

		        AlertDialog.Builder builder = new AlertDialog.Builder(context);
		        builder.setTitle("Make your selection");
		        builder.setItems(items, new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int item) {
		            	String countDown = new String();
		            	if(item == 0){
		            		countDown = "03";
		            	}
		            	if(item == 1){
		            		countDown = "04";
		            	}
		            	if(item == 2){
		            		countDown = "05";
		            	}
		            	if(item == 3){
		            		countDown = "06";
		            	}
		            	if(item == 4){
		            		countDown = "07";
		            	}       

		            	if(Connect.connectionStatus != false){
			    			
			    			textMessage = editText1.getText().toString();//Enviamos el mensaje escrito al destino
			    			String temp = textMessage;
			    			textMessage = textMessage + privacy + countDown;
			    			if(!(textMessage.equals("")) && (textMessage.length() > 3)){
			    				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			    				
			    				new Thread(new Runnable() {
			    			        public void run() {
			    			        	((Connect) getApplication()).ChatMessage(remoteUsername, textMessage);
			    			        }
			    			    }).start();
			    				
			    				Toast.makeText(getApplicationContext(), "Priva Message has been sent.",
						        Toast.LENGTH_LONG).show();
			    				
			    				DatabaseHandler db = new DatabaseHandler(context);
								List<MessageDB> messages = db.getAllMessages();
								db.close();
								int ID = 0;
								for (MessageDB cn : messages) {
									ID = cn.getID();
						        }
								Log.d("Connect","ID Last ID: "+ ID);
								
								adapter.add(new OneComment(false, temp, 99, -1, sdf.format(new Date())));
								editText1.setText("");
								lv.setSelection(lv.getAdapter().getCount()-1); 
			    			}
			    		}else{
			    			Toast.makeText(getApplicationContext(), "There is no connection, wait for reconnection...",
			            	Toast.LENGTH_LONG).show();
			    			status.setText("Reconnecting...");
			    		}
		            }
		        });
		        AlertDialog alert = builder.create();
		        alert.show();

	            return true;
	        }
	    });

		editText1.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
			// If the event is a key-down event on the "enter" button
				if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER && Connect.connectionStatus != false)) {
					// Perform action on key press
					SendText(v);
					return true;			
				}
				return false;
			}
		});
		
	}
	
	public void SendText(View v){
		if (!priva){
			if(Connect.connectionStatus != false){
				textMessage = editText1.getText().toString();
				textMessage = textMessage + privacy + "00";
				if(!(textMessage.equals("")) && (textMessage.length() > 3)){ //Aqui se puede eliminar la parte de que si no esta vacio.
					//Esta parte deberia ir dentro de un try en caso de que el mensaje no se envie.
					final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					
					new Thread(new Runnable() {
				        public void run() {
				        	((Connect) getApplication()).ChatMessage(remoteUsername, textMessage);
				        	((Connect) getApplication()).DBInsertMessage(LoginActivity.pref.getString("username", "default")+"@localhost", remoteUsername, sdf.format(new Date()), textMessage);
				        }
				    }).start();

					DatabaseHandler ndb = new DatabaseHandler(context);
					List<MessageDB> messages = ndb.getAllMessages(); 
					int ID = 0;
					for (MessageDB cn : messages) {
						ID = cn.getID();
			        }
					Log.d("Connect","ID Last ID: "+ ID);
					
					DatabaseHandler db = new DatabaseHandler(context);
					db.addMessage(new MessageDB(LoginActivity.pref.getString("username", "default")+"@localhost", remoteUsername,sdf.format(new Date()),textMessage, priva));
					db.close();
					
					adapter.add(new OneComment(false, editText1.getText().toString(), 00, ID, sdf.format(new Date())));
					editText1.setText("");
					lv.setSelection(lv.getAdapter().getCount()-1);
				}
			}else{
				Toast.makeText(getApplicationContext(), "There is no connection, wait for reconnection...",
	        	Toast.LENGTH_LONG).show();
				status.setText("Reconnecting...");
			}
		}else{
			if(Connect.connectionStatus != false){
				textMessage = editText1.getText().toString();
				textMessage = textMessage + privacy + "03";// se debe agregar un boton para que esto se una constante opcional.
				if(!(textMessage.equals("")) && (textMessage.length() > 3)){ //Aqui se puede eliminar la parte de que si no esta vacio.
					//Esta parte deberia ir dentro de un try en caso de que el mensaje no se envie.
					final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

					if(!adapter.isEmpty()){
                		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                        alertDialogBuilder.setIcon(R.drawable.ic_launcher);
            			alertDialogBuilder.setTitle("Warning!");
            			alertDialogBuilder
            				.setMessage("You still have unread messages if you continue this message will be deleted.")
            				.setCancelable(false)
            				.setPositiveButton("Continue",new DialogInterface.OnClickListener() {
            					public void onClick(final DialogInterface dialog,int id) {
            						new Thread(new Runnable() {
            					        public void run() {
            					        	((Connect) getApplication()).ChatMessage(remoteUsername, textMessage);
            					        	mHandler.post(new Runnable() {
            					                public void run() {
            					                	DatabaseHandler db = new DatabaseHandler(context);
            					                	for(int i = 0; i<adapter.getCount();i++){
            					                		db.deleteMessage(new MessageDB(adapter.getItem(i).getID(), "delete", "delete", "delete", "delete", priva)); 
            					                	}
            					                	db.close();
            					                	onBackPressed();
            					                	dialog.cancel();
            					                }
            					            });
            					        }
            					    }).start();
            					}
            				})
            				.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
        					public void onClick(DialogInterface dialog,int id) {
        						dialog.cancel();
        						}
            				});
            			AlertDialog alertDialog = alertDialogBuilder.create();
            			alertDialog.show();
                	}else{
                		new Thread(new Runnable() {
					        public void run() {
					        	((Connect) getApplication()).ChatMessage(remoteUsername, textMessage);
					        	mHandler.post(new Runnable() {
					                public void run() {
					                	onBackPressed();
					                }
					            });
					        }
					    }).start();
                	}    
				}
			}else{
				Toast.makeText(getApplicationContext(), "There is no connection, wait for reconnection...",
	        	Toast.LENGTH_LONG).show();
				status.setText("Reconnecting...");
			}
		}
	}
	
	public String getLastSeen() throws XMPPException{
        LastActivity activity = LastActivityManager.getLastActivity(((Connect) getApplication()).getConnection(), remoteUsername);
            
        long millis = activity.lastActivity * 1000;
        millis = System.currentTimeMillis() - millis;
        Date date = new Date(millis);  
      	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      	String offlineTimeStamp = formatter.format(date);
      	    
      	offlineTimeStamp = (String) DateUtils.getRelativeDateTimeString(context, millis, DateUtils.SECOND_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, DateUtils.FORMAT_ABBREV_ALL);
      	Log.d("LAST ACTIVITY2", offlineTimeStamp );
      	return offlineTimeStamp;
    }
	
	public void setContactStatus(){
		
		if(Connect.connectionStatus == false){
			status.setText("Reconnecting...");			
		}else{
			roster = ((Connect) this.getApplication()).getConnection().getRoster();
			if(roster.getPresence(remoteUsername).getType() == Presence.Type.available ){
				status.setText(roster.getPresence(remoteUsername).getStatus().toString());
			}else{
				try {
					status.setText("Last seen: "+getLastSeen());
				} catch (Exception e) {
					status.setText("Offline");
				}
			}
		}
	}
	
	public void setContactImage(){
		
		new Thread(new Runnable() {	
			public void run() {  	
			    try {
			        Log.d("Connect","Setting icon photo from internal DB");
			    	DatabaseHandler db = new DatabaseHandler(context);
			    	final Photo photo = db.getPhotoDBByUserName(remoteUsername);
			    	db.close();
			    	 if(ChatEntryActivity.isRunning){
						 mHandler.post(new Runnable() {
					 	     public void run() {
					 	    	 Resources res = getResources();
					 		     BitmapDrawable icon = new BitmapDrawable (res, photo.getPhoto());
					 		     getActionBar().setIcon(icon);
					 	     }	
					 	 });
					}
			    			
			    }catch (Exception e) {
			    	Log.d("OptionActivity", "User: " + remoteUsername + " image not found in the internal DB");
			    	getActionBar().setIcon(R.drawable.ic_perfil);
			    }
			        	
			    if(Connect.connectionStatus == true){
					try {
						Log.d("Connect","Cheking if photo is up to date");
						((Connect) getApplication()).GetLastPhotoUpdate(remoteUsername);
						DatabaseHandler db = new DatabaseHandler(context);
						Log.d("Connect","Getting photo from internal DB");
				    	final Photo photo = db.getPhotoDBByUserName(remoteUsername);
				    	db.close();
				    	if(ChatEntryActivity.isRunning){
							   mHandler.post(new Runnable() {
								   public void run() {
					 	               Resources res = getResources();
					 	               BitmapDrawable icon = new BitmapDrawable (res, photo.getPhoto());
					 	               getActionBar().setIcon(icon);
					 	           }	
					 	       });
						}	
					}catch (Exception e) {
						Log.d("OptionActivity", "An error ocurred setting icon photo from the internal DB");
						e.printStackTrace();
					}
			   }
		}
		}).start();
	}
	
	public void getHistory(){
		
		if(priva){
			DatabaseHandler db = new DatabaseHandler(context);
			List<MessageDB> messages = db.getAllPrivateMessages(); 
			db.close();
			for (MessageDB cn : messages) {	
				Log.d("ChatEntryActivity","Priva msg:" +cn.getBody());
				if((cn.getFromJid().equals(remoteUsername)) && (cn.getToJid().equals(LoginActivity.pref.getString("username", "default")+"@localhost")) || (cn.getFromJid().equals(LoginActivity.pref.getString("username", "default")+"@localhost")) && (cn.getToJid().equals(remoteUsername))){
					if((cn.getFromJid().equals(remoteUsername)) && (cn.getToJid().equals(LoginActivity.pref.getString("username", "default")+"@localhost"))){
						int countDown = Integer.valueOf(cn.getBody().substring(cn.getBody().length() - 2, cn.getBody().length()));
						if(countDown != 0 && countDown < 60){
		                	Log.d("ChatEntryActivity", "CountDown: "+ countDown);
		                	adapter.add(new OneComment(true, "Priva Message", countDown, cn.getID(), cn.getSentDate()));
		                			
		                }else if (countDown < 60){
		                	String ChatMessage = cn.getBody().substring(0, cn.getBody().length() - 3); // esto es para que no muestre el countDown
		                	adapter.add(new OneComment(true, ChatMessage, 0 , cn.getID(), cn.getSentDate()));
		                }
					}else{
						int countDown = Integer.valueOf(cn.getBody().substring(cn.getBody().length() - 2, cn.getBody().length()));
						if(countDown == 99){//Esto s epuede quitar mas adelante
			                Log.d("ChatEntryActivity", "CountDown: "+ countDown);
			                adapter.add(new OneComment(false, "Priva Message", countDown, cn.getID(), cn.getSentDate()));
						}else{
							String ChatMessage = cn.getBody().substring(0, cn.getBody().length() - 3);
							adapter.add(new OneComment(false, ChatMessage, 0, cn.getID(), cn.getSentDate()));
						}
					}
				}
			      }
			lv.setSelection(lv.getAdapter().getCount()-1);
		}else{
			DatabaseHandler db = new DatabaseHandler(context);
			List<MessageDB> messages = db.getAllNonPrivateMessages(); 
			db.close();
			for (MessageDB cn : messages) {	
				Log.d("ChatEntryActivity","NonPriva msg:" +cn.getBody());
				if((cn.getFromJid().equals(remoteUsername)) && (cn.getToJid().equals(LoginActivity.pref.getString("username", "default")+"@localhost")) || (cn.getFromJid().equals(LoginActivity.pref.getString("username", "default")+"@localhost")) && (cn.getToJid().equals(remoteUsername))){
					if((cn.getFromJid().equals(remoteUsername)) && (cn.getToJid().equals(LoginActivity.pref.getString("username", "default")+"@localhost"))){
						int countDown = Integer.valueOf(cn.getBody().substring(cn.getBody().length() - 2, cn.getBody().length()));
						if(countDown != 0 && countDown < 60){
		                	Log.d("ChatEntryActivity", "CountDown: "+ countDown);
		                	adapter.add(new OneComment(true, "Priva Message", countDown, cn.getID(), cn.getSentDate()));
		                			
		                }else if (countDown < 60){
		                	String ChatMessage = cn.getBody().substring(0, cn.getBody().length() - 3); // esto es para que no muestre el countDown
		                	adapter.add(new OneComment(true, ChatMessage, 0 , cn.getID(), cn.getSentDate()));
		                }
					}else{
						int countDown = Integer.valueOf(cn.getBody().substring(cn.getBody().length() - 2, cn.getBody().length()));
						if(countDown == 99){//Esto s epuede quitar mas adelante
			                Log.d("ChatEntryActivity", "CountDown: "+ countDown);
			                adapter.add(new OneComment(false, "Priva Message", countDown, cn.getID(), cn.getSentDate()));
						}else{
							String ChatMessage = cn.getBody().substring(0, cn.getBody().length() - 3);
							adapter.add(new OneComment(false, ChatMessage, 0, cn.getID(), cn.getSentDate()));
						}
					}
				}
			      }
			lv.setSelection(lv.getAdapter().getCount()-1);
		}
	}
	
	public static void updateStatus(String remoteuser, Presence presence){
		String state = new String();
		
		if(remoteuser.contains("/")){
			remoteuser = remoteuser.substring(0, remoteuser.indexOf('/'));
		}
		if (presence.getType() == Presence.Type.unavailable) {
            state = "Offline";
        }
		if ((presence.getType() == Presence.Type.available) && (presence.getStatus() != null) ) {
			state = presence.getStatus().toString();
        }
		if ((presence.getType() == Presence.Type.available) && (presence.getStatus() == null) ) {
			state = "Online";
        }
		if(state.isEmpty()){
			state = "Online";
		}
		
		status = (TextView) ChatEntryActivity.status.findViewById(R.id.status);
		
		if(remoteUsername.equals(remoteuser)){
			status.setText(state);
		}
	}
	
	public static void updateGlobal(String state){
		status = (TextView) ChatEntryActivity.status.findViewById(R.id.status);
		status.setText(state);
	}
	
	public static void addItems(String fromName, String message, int countDown, int ID, String date, boolean privacy) {
		String remote = remoteUsername;

		if((fromName.equals(remote)) && (priva == privacy)){
			Log.d("Privacy","Llego un mensaje: " + message + "Para el usuario: " + LoginActivity.pref.getString("username", "default") + " del usuario: " + fromName );
			adapter.add(new OneComment(true,  message, countDown, ID, date));
			lv.setSelection(lv.getAdapter().getCount()-1);
		}
 	}
	
	public static void selfDestruct(final int ID, final int countDown, final OneComment item){
		new CountDownTimer(countDown*1000, 1000) {
			public void onTick(final long millisUntilFinished) {   
				Log.d("ChatEntryActivity","CounterDown seconds remaining: " + millisUntilFinished / 1000);      
			}

			public void onFinish() {
				Log.d("ChatEntryActivity","CounterDown Finish!");
				adapter.removeItem(item);
				adapter.notifyDataSetChanged();
				DatabaseHandler db = new DatabaseHandler(context);
    			db.deleteMessage(new MessageDB(ID, "delete", "delete", "delete", "delete", priva)); 
    			db.close();
			}
		}.start();       
	}
	
	@Override
  	public void onBackPressed() {
  		super.onBackPressed();
  		isRunning = false;
  		remoteUsername = null;    
  		if(GlobalID != 0){
  			CDT.cancel();
  			DatabaseHandler db = new DatabaseHandler(context);
			db.deleteMessage(new MessageDB(GlobalID, "delete", "delete", "delete", "delete", priva)); 
			db.close();
  		}
  	}
	
	@Override
	protected void onResume(){
		super.onResume();
		Log.d("ChatEntryActivity","Onresume() has been called");
		remoteUsername = intent.getStringExtra("remoteUsername");
		Log.d("ChatEntryActivity","OnResume() remoteUsername: "+ remoteUsername );
		isRunning = true;
	}
	    
	@Override
	protected void onPause(){
		super.onPause();
		Log.d("ChatEntryActivity","OnPause() has been called");
		remoteUsername = null;	
		isRunning = false;
	}
}
