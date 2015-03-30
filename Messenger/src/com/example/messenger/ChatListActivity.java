package com.example.messenger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ChatListActivity extends Activity {
	
		public static ArrayList<States> chatContactList = new ArrayList<States>(); 
		public static ArrayList<ItemDetails> image_details = new ArrayList<ItemDetails>();
		public static ItemListBaseAdapter adapter = null;
		public static boolean isRunning; 
		public static TextView status;
		public static Activity mainActivity;
		public static Context context;
		public static States st;
		
		private Editor ed;
		private TextView contacts;
		private TextView lastActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatlist);
        
        Log.d("ChatListActivity","Estoy en el onCreate");
        setTitle("Priva Messenger");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        contacts = (TextView) findViewById(R.id.contacts);
		contacts.setText("");
		lastActivity = (TextView) findViewById(R.id.last_activity);
		lastActivity.setText("");
		status = (TextView) findViewById(R.id.status);
		status.setText("Chat List");
        context = this;
        mainActivity = this;
        isRunning = true;
        
        SyncChatHistory();
    }
    
   /* @Override
    protected void onNewIntent(Intent intent){
    	super.onNewIntent(intent);
    	setIntent(intent);
    }*/
    
    @Override
    protected void onResume(){
		super.onResume();
		Log.d("ChatListActivity","Estoy en el onResume");
		isRunning = true;
		
		//Intent intent = getIntent();
		//boolean notifLeida = intent.getBooleanExtra("notiLeido", false);
		
		/*if(notifLeida){
			Connect.notificacionLeida=true;
			((Connect) this.getApplication()).ClearNotification();
		}*/
		
		if(Connect.connectionStatus != true){
			status.setText("Reconnecting...");
		}else{
			status.setText("Chat List");
		}
		crearLista();
	}
    
    @Override
    protected void onPause(){
		super.onPause();
		Log.d("ChatListActivity","Estoy en el onPause");
		isRunning = false;
		
		DatabaseHandler db = new DatabaseHandler(this);
		List<ChatContact> chatContacts = db.getAllChatContacts(); 
		for (ChatContact cn : chatContacts) {
			if((cn.getUser().equals(LoginActivity.pref.getString("username", "default")+"@localhost"))){
				db.deleteChatContact(new ChatContact(cn.getID(), "delete", "delete", false)); 
				Log.d("ChatListActivity","Contact: "+cn.getChatContact());
			}
		}
		for (int i = 0; i<chatContactList.size(); i++){
			Log.d("ChatListActivity OnPause","Contact: "+chatContactList.get(i).getName()+" isPrivate: "+ chatContactList.get(i).isSelected());
			db.addChatContact(new ChatContact(LoginActivity.pref.getString("username", "default")+"@localhost", chatContactList.get(i).getName(),chatContactList.get(i).isSelected()));
		}
		db.close();
		
		if(Connect.connectionStatus){
			new Thread(new Runnable() {
		        public void run() {
		        	((Connect) getApplication()).SyncChats(chatContactList);
		        }
		    }).start();
			
		}
	}
    
   	@Override
   	public void onBackPressed() {
   		super.onBackPressed();
   		isRunning = false;
   		Log.d("ChatListActivity","OnBackPress has been clicked");   
   	}
    
    @Override
    protected void onDestroy(){
		super.onDestroy();
		Log.d("ChatListActivity","Estoy en el onDestroy");
		isRunning = false;
		((Connect) this.getApplication()).Disconect();
		//((Connect) this.getApplication()).ClearNotification();
	}
   
    //Esta metedo sirve para actualizar el ultimo mensaje que algun contacto envio. Los mensajes que se pueden ver en la lista de contactos
    public static void updateList(String fromName, String message, boolean priva){
    	int countDown = Integer.valueOf(message.substring(message.length() - 2, message.length()));
    	message = message.substring(0, message.length() - 3);
    	
    	if(countDown > 0 ){
    		message = "Priva Message";
    	}
    	
    	for(int i = 0; i < image_details.size(); i++){
    		if((fromName.equals(image_details.get(i).getName())) && (image_details.get(i).isPrivate() == priva)){
    			if(message.length() > 65){
    				image_details.get(i).setMessage(priva? "Priva Conversation" : message.toString().substring(0, 65)+"...");
    			}else{
    				image_details.get(i).setMessage(priva? "Priva Conversation" : message.toString());
    			}
    			
    			ItemDetails item = new ItemDetails();
    			item = image_details.get(i);
    			image_details.remove(i);
    			image_details.add(0,item);
    		}
    	}
    	
    	for(int i = 0; i < chatContactList.size(); i++){
    		if((fromName.equals(chatContactList.get(i).getName())) && (chatContactList.get(i).isSelected() == priva)){
    			States item = new States();
    			item = chatContactList.get(i);
    			deleteFromChatList(chatContactList.get(i).getName(), chatContactList.get(i).isSelected());
    			chatContactList.add(0,item);
    		}
    	}
    	adapter.notifyDataSetChanged();
    }
    
    
    public static void updateCounter(String fromName, boolean priva){
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	
    	for(int i = 0; i < image_details.size(); i++){
    		if(fromName.equals(image_details.get(i).getName()) && (image_details.get(i).isPrivate() == priva)){
    			image_details.get(i).setCounter(String.valueOf(Connect.MsjCounter(fromName, priva)));
    			image_details.get(i).setDate(TimeFix(Connect.TimeConverter(sdf.format(new Date()))));
    			adapter.notifyDataSetChanged(); 
    		}
    	}
    }
    
    public static void updateGlobal(String state){
		//Esta funcion permite cambiar el estado de la connection en el textview
		status = (TextView) ChatListActivity.status.findViewById(R.id.status);
		status.setText(state);
	}
    
    // Este metodo se utiliza para agregar los contactos a la lista de chats
    public static void updateChatList(String contact, boolean priva){

    	DatabaseHandler db = new DatabaseHandler(context);
		List<Contact> contacts = db.getAllContacts(); 
		db.close();
		for (Contact cn : contacts) {
			Log.d("ChatListActivity","User: "+ cn.getUsuario() + " Contact: "+ cn.getContacto() +" State: "+cn.getState());
			if((cn.getUsuario().equals(LoginActivity.pref.getString("username", "default")+"@localhost")) && (cn.getContacto().equals(contact))){
				st = new States(cn.getID(), cn.getContacto(), cn.getState());
			}
		}
		
		boolean found = false;
		
		for(int i = 0; i<chatContactList.size(); i++){
			Log.d("ChatListActivity","FOR chatList user: "+chatContactList.get(i).getName() + ". Privacy: " + chatContactList.get(i).isSelected());
			if((chatContactList.get(i).getName().equals(contact)) && (chatContactList.get(i).isSelected() == priva)){
				found = true;
			}
		}
		
		if((found == false) && (st.isSelected() == true) ){
			Log.d("ChatListActivity","User: "+st.getName() + " has been added to the chatList" + ". Privacy: " + priva);
			chatContactList.add(new States(st.getName(), priva));
			
			ItemDetails item_details = new ItemDetails();
	    	item_details.setName(contact);
	    	ArrayList<String> temp = ((Connect) context.getApplicationContext()).DBUpdateContactList(contact, LoginActivity.pref.getString("username", "default")+"@localhost");
	    	
	    	int countDown = Integer.valueOf(temp.get(0).substring(temp.get(0).length() - 2, temp.get(0).length()));
	    	String message = temp.get(0).substring(0, temp.get(0).length() - 3);
	    	
	    	if(countDown > 0 ){
	    		message = "Priva Message";
	    	}	
	    	
	    	if(message.length() > 65){
	    		message = message.toString().substring(0, 65)+"...";			
	    	}

	    	item_details.setMessage(priva? "Priva Conversation" : message);
	    	item_details.setDate(TimeFix(Connect.TimeConverter(temp.get(1))));
	    	item_details.setCounter(String.valueOf(Connect.MsjCounter(contact, priva)));
	    	item_details.setImage(priva ? 2:1);
	    	item_details.setPrivate(priva);
	    	
	    	image_details.add(0, item_details);
	    	
	    	for(int i = 0; i < chatContactList.size(); i++){
	    		if((contact.equals(chatContactList.get(i).getName())) && (chatContactList.get(i).isSelected() == priva)){
	    			
	    			States item = new States();
	    			item = chatContactList.get(i);
	    			deleteFromChatList(chatContactList.get(i).getName(), chatContactList.get(i).isSelected());
	    			chatContactList.add(0,item);
	    		}
	    	}
	    	adapter.notifyDataSetChanged();	
		}
    }
    
    public static void deleteFromChatList(String contact, boolean priva){
    	Log.d("ChatListActivity","User deleted from  chatContactList: "+contact);
    	ArrayList<States> tempList = new ArrayList<States>();
		for(int i = 0;i<chatContactList.size(); i ++){
			if(!((chatContactList.get(i).getName().equals(contact)) && (chatContactList.get(i).isSelected() == priva))){
				tempList.add(chatContactList.get(i));
			}
		}
		chatContactList.clear();
		chatContactList = tempList;
    }

    public void crearLista(){
    	
    	image_details = GetSearchResults();
    	final ListView lv1 = (ListView) findViewById(R.id.listV_main);
    	adapter = new ItemListBaseAdapter(this, image_details);
        lv1.setAdapter(adapter);
        
        lv1.setOnItemClickListener(new OnItemClickListener() {
        	@Override
        	public void onItemClick(AdapterView<?> a, View v, int position, long id) { 
        		Object o = lv1.getItemAtPosition(position);
            	ItemDetails obj_itemDetails = (ItemDetails)o;
        		
        		Intent intent = new Intent(v.getContext(), ChatEntryActivity.class);
        	    intent.putExtra("remoteUsername", obj_itemDetails.getName());
        	    intent.putExtra("priva", obj_itemDetails.isPrivate());
        	    startActivity(intent);
        	    isRunning = false;
        	}  
        });
        
        lv1.setOnItemLongClickListener(new OnItemLongClickListener(){
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, final View view, int position, long id) {
				Object o = lv1.getItemAtPosition(position);
            	final ItemDetails obj_itemDetails = (ItemDetails)o;
            	
				final CharSequence[] items = {
						"Send a Message", "Delete Chat", "Priva Conversation"
		        };

		        AlertDialog.Builder builder = new AlertDialog.Builder(context);
		        builder.setTitle("Make your selection");
		        builder.setItems(items, new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int item) {
		            	if(item == 0){
		            		Intent intent = new Intent(view.getContext(), ChatEntryActivity.class);
		            	    intent.putExtra("remoteUsername", obj_itemDetails.getName());
		            	    startActivity(intent);
		            	}
		            	
		            	if(item == 1){
		            		adapter.removeItem(obj_itemDetails);
		            		
		            		DatabaseHandler db = new DatabaseHandler(context);
		            		List<ChatContact> chatContacts = db.getAllChatContacts(); 
		            		for (ChatContact cn : chatContacts) {
		            			Log.d("ChatListActivity","User: "+ cn.getUser() + " Contact: "+ cn.getChatContact());
		            			if((cn.getUser().equals(LoginActivity.pref.getString("username", "default")+"@localhost")) && (cn.getChatContact().equals(obj_itemDetails.getName()))){
		            				db.deleteChatContact(new ChatContact(cn.getID(), LoginActivity.pref.getString("username", "default")+"@localhost", obj_itemDetails.getName(), cn.isPrivate())); 
		            				
		            			}
		            		}
		            		deleteFromChatList(obj_itemDetails.getName(), obj_itemDetails.isPrivate() );
		            		Log.d("ChatListActivity","User deleted from DB and chatContactList: "+obj_itemDetails.getName());
		            		db.close();
		            	}
		            	
		            	if(item == 2){
		            		
		            		boolean userExist = false;
		            	    
		            	    DatabaseHandler db = new DatabaseHandler(context);
		            		List<ChatContact> chatContacts = db.getAllChatContacts();
		            		db.close();
		            		for (ChatContact cn : chatContacts) {
		            			if((cn.getChatContact().equals(obj_itemDetails.getName())) && (cn.getUser().equals(LoginActivity.pref.getString("username", "default")+"@localhost")) && (cn.isPrivate() == true)){
		            				userExist = true;
		            				Log.d("ChatListActivity", "User EXIST!");
		            			}
		            		}
		            		
		            		if(userExist == false){
		            			States state = new States(obj_itemDetails.getName(), true);
			            		chatContactList.add(0, state);
		            		}

		            		Intent intent = new Intent(view.getContext(), ChatEntryActivity.class);
		            	    intent.putExtra("remoteUsername",obj_itemDetails.getName());
		            	    intent.putExtra("priva", true);
		            	    startActivity(intent);

		            	    isRunning = false; 
		            	}
		            	
		            	Toast.makeText(ChatListActivity.this, "You have chosen : " + obj_itemDetails.getName() + ". Item #: " + item, Toast.LENGTH_LONG).show();
		            }
		        });
		        AlertDialog alert = builder.create();
		        alert.show();
				
				return true;
			}} );
    }
  	
    
    private ArrayList<ItemDetails> GetSearchResults(){
    	ArrayList<ItemDetails> results = new ArrayList<ItemDetails>();
    	
    		chatContactList.clear();
    		
    		DatabaseHandler db = new DatabaseHandler(this);
    		List<ChatContact> chatContacts = db.getAllChatContacts(); 
    		db.close();
    		for (ChatContact cn : chatContacts) {
    			if((cn.getUser().equals(LoginActivity.pref.getString("username", "default")+"@localhost"))){
    				States state = new States(cn.getID(), cn.getChatContact(), cn.isPrivate());
    				if(isContactBlocked(state.getName())){
    					chatContactList.add(state);
    					Log.d("ChatListActivity","Local User Contact: "+cn.getChatContact() + "isPrivate: "+cn.isPrivate());
    				}
    			}
    		}
  		
  		for(int i = 0; i < chatContactList.size(); i++){
  			
  			ItemDetails item_details = new ItemDetails();
  	    	item_details.setName(chatContactList.get(i).getName());
  	    	ArrayList<String> temp = ((Connect) this.getApplication()).DBUpdateContactList(chatContactList.get(i).getName(), LoginActivity.pref.getString("username", "default")+"@localhost");
  	    	
  	    	int countDown = 0;
  	    	String message = " ";

  	    	if(!(temp.get(0).equals(" "))){
  	    	countDown = Integer.valueOf(temp.get(0).substring(temp.get(0).length() - 2, temp.get(0).length()));
	    	message = temp.get(0).substring(0, temp.get(0).length() - 3);
  	    	}
  	    	
	    	if(countDown > 0 ){
	    		message = "Priva Message";
	    	}
	    	
	    	if(message.length() > 65){
	    		message = message.toString().substring(0, 65)+"...";			
	    	}
  	    	
  	    	item_details.setMessage(message);
  	    	item_details.setDate(TimeFix(Connect.TimeConverter(temp.get(1))));
  	    	item_details.setCounter(String.valueOf(Connect.MsjCounter(chatContactList.get(i).getName(), chatContactList.get(i).isSelected())));
  	    	item_details.setImage(chatContactList.get(i).isSelected() ? 2:1);
  	    	item_details.setPrivate(chatContactList.get(i).isSelected());

  	    	results.add(item_details);
  		}
    	return results;  	
    }
  	
    public static String TimeFix(String time){
    	
    	String fixDate = time;
    	
    	if(time.contains(",")){
        	if(Connect.DayMillis){
        		fixDate = fixDate.substring(fixDate.indexOf(',') + 1, fixDate.length());
        	}
    	}  	
    	return fixDate;
    }
    
    public boolean isContactBlocked(String contact){
    	DatabaseHandler datab = new DatabaseHandler(context);
		List<Contact> contacts = datab.getAllContacts(); 
		datab.close();
		for (Contact cn : contacts) {
			if((cn.getUsuario().equals(LoginActivity.pref.getString("username", "default")+"@localhost")) && (cn.getContacto().equals(contact))){
				return cn.getState();
			}
		}
		return false;
    }
    
    public void SyncChatHistory(){
    	if(LoginActivity.pref.getBoolean(LoginActivity.pref.getString("username", "default")+"@localhost", true)){
    		((Connect) this.getApplication()).SyncHistory();
    		
    		ed = LoginActivity.pref.edit();
        	ed.putBoolean(LoginActivity.pref.getString("username", "default")+"@localhost", false);
    		ed.commit();
    	}
    }
    
    public static void deleteMessageUpdate(String fromName, String message){
    	
    	ArrayList<MessageDB> userMessages = new ArrayList<MessageDB>();
		
		DatabaseHandler db = new DatabaseHandler(context);
		List<MessageDB> messages = db.getAllNonPrivateMessages();
		db.close();
		for (MessageDB cn : messages) {
	        	
			if((cn.getFromJid().equals(fromName)) && (cn.getToJid().equals(LoginActivity.pref.getString("username", "default")+"@localhost")) || (cn.getFromJid().equals(LoginActivity.pref.getString("username", "default")+"@localhost")) && (cn.getToJid().equals(fromName))){
				userMessages.add(cn);
			}
	    }
		
		String lastMessage = userMessages.get(userMessages.size()-1).getBody().substring(0, userMessages.get(userMessages.size()-1).getBody().length() - 3);;
		String lastMessageDate = userMessages.get(userMessages.size()-1).getSentDate().toString();
		
    	for(int i = 0; i < image_details.size(); i++){
    		if((fromName.equals(image_details.get(i).getName())) && (image_details.get(i).getMessage().equals(message))){
    			image_details.get(i).setMessage(lastMessage);
    			image_details.get(i).setDate(TimeFix(Connect.TimeConverter(lastMessageDate)));
    		}
    	}
    	adapter.notifyDataSetChanged();
    }
    
  	public void BotonDelActionBar(MenuItem item){
		Intent intent = new Intent(this, AddContactActivity.class);
		startActivity(intent);
        isRunning = false;
	}
  	
  	public void StatusBtn(MenuItem item){
		Intent intent = new Intent(this, StatusActivity.class);
		startActivity(intent);
        isRunning = false;
	}
  	
  	public void PerfilBtn(MenuItem item){
		Intent intent = new Intent(this, OptionsActivity.class);
		startActivity(intent);
        isRunning = false;
	}
  	
  	public void ContactBtn(MenuItem item){
		Intent intent = new Intent(this, ContactActivity.class);
		startActivity(intent);
        isRunning = false;
	}
  	
  	public void chatListBtn(MenuItem item){
		Intent intent = new Intent(this, ChatListActivity.class);
		startActivity(intent);
	}

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.chat_list, menu);
		return true;
	}
}