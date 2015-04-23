package com.example.messenger;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class AddContactActivity extends Activity {

	private EditText addUserEdit;
    private String addUser;
    private String toast;
    private String note;
    private ProgressDialog progress;
    
	public static Context context;
	public static ListView listView;
	public static Activity activity;
	public static ArrayList<States> contactList = new ArrayList<States>();
	public static MyCustomAdapter dataAdapter = null;
	
    public static boolean isRunning;
    public static boolean localDBExist;
    

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_add_contact);
		
		Log.d("AddContactActivity","Estoy en el onCreate");
		setTitle("Priva Messenger");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		context = this;
		isRunning = true;
		activity = this;
		
		displayListView();
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		Log.d("AddContactActivity","Estoy en el onPause");
		isRunning = false;
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		Log.d("AddContactActivity","Estoy en el onDestroy");
		isRunning = false;	
	}
	
	private void displayListView() {
		
		contactList.clear();

		DatabaseHandler db = new DatabaseHandler(context);
		List<Contact> contacts = db.getAllContacts();
		db.close();
			
		for (Contact cn : contacts) {
			if(cn.getUsuario().equals(LoginActivity.pref.getString("username", "default")+"@localhost")){
				States state = new States(cn.getContacto(), cn.getState());
				contactList.add(state);
			}
		}
		
		// create an ArrayAdaptar from the String Array
		dataAdapter = new MyCustomAdapter(context, R.layout.state_info, contactList);
		listView = (ListView) findViewById(R.id.listView1);
		// Assign adapter to ListView
		listView.setAdapter(dataAdapter);
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			//En esta parte se muestra un  toast cuando se le hace click al usuario en la lista de contactos(al usuario y no al checkbox)
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// When clicked, show a toast with the TextView text
				States state = (States) parent.getItemAtPosition(position);
				Toast.makeText(context,
						"Clicked on : " + state.getName(), Toast.LENGTH_LONG)
						.show();
			}
		});
	}
	
	public static void updateAddUserList(String contact){
		States state = new States(contact, false);
		dataAdapter.add(state);
		Toast.makeText(context, "The user: " + contact +". has added you.", Toast.LENGTH_LONG).show();
	}

	public class MyCustomAdapter extends ArrayAdapter<States> {

		private ArrayList<States> stateList;
		public MyCustomAdapter(Context context, int textViewResourceId,

		ArrayList<States> stateList) {
			super(context, textViewResourceId, stateList);
			this.stateList = new ArrayList<States>();
			this.stateList.addAll(stateList);
		}
		
		@Override
		public void add(States state) {
			stateList.add(state);
			super.add(state);
		}
		
		private class ViewHolder {
			CheckBox name;
			ImageButton imageDelete;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder = null;

			Log.v("ConvertView", String.valueOf(position));

			if (convertView == null) {

				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				convertView = vi.inflate(R.layout.state_info, null);

				holder = new ViewHolder();
				holder.name = (CheckBox) convertView.findViewById(R.id.checkBox1);
				holder.imageDelete = (ImageButton) convertView.findViewById(R.id.imageButton1);
				holder.imageDelete.setFocusable(false);
				holder.imageDelete.setClickable(false);

				convertView.setTag(holder);

				holder.name.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						
						CheckBox cb = (CheckBox) v;
						States _state = (States) cb.getTag();
						
						if(Connect.connectionStatus == true){
						
							if (cb.isChecked() == true){
								progress = new ProgressDialog(context);
							    progress.setMessage("Wait while adding contact...");
							    progress.show();
							    
								note = "Has been added to the contact list";
								((Connect) getApplication()).DBUpdateContact(_state.getName(), true);
							
								DatabaseHandler db = new DatabaseHandler(context);
								List<Contact> contacts = db.getAllContacts(); 
								localDBExist = false;
								for (Contact cn : contacts) {
									Log.d("AddContactActivity","User: "+ cn.getUsuario() + " Contact: "+ cn.getContacto() +" State: "+cn.getState());
									if((cn.getUsuario().equals(LoginActivity.pref.getString("username", "default")+"@localhost")) && (cn.getContacto().equals(_state.getName()))){
										db.updateContact(new Contact(cn.getID(), cn.getUsuario(), cn.getContacto(), true));
										localDBExist = true;
										Log.d("AddContactActivity CHECK TRUE","ENCONTRADO: "+ cn.getState());
									}	
								}
							
								if(localDBExist == false){
									db.addContact(new Contact(LoginActivity.pref.getString("username", "default")+"@localhost", _state.getName(), true));
									Log.d("AddContactActivity ","El Contacto no existe y se agrego a la DB local ");
								}
								db.close();
								
							}else{
								progress = new ProgressDialog(context);
							    progress.setMessage("Wait while blocking contact...");
							    progress.show();
							    
								note = "Has been eliminated from the contact list";
								((Connect) getApplication()).DBUpdateContact(_state.getName(), false);
								ChatListActivity.deleteFromChatList(_state.getName(), true);
								ChatListActivity.deleteFromChatList(_state.getName(), false);
							
								Log.d("AddContactActivity","User to delete name: " + _state.getName());
								DatabaseHandler db = new DatabaseHandler(context);
								List<Contact> contacts = db.getAllContacts(); 
								for (Contact cn : contacts) {
									Log.d("AddContactActivity","User: "+ cn.getUsuario() + " Contact: "+ cn.getContacto() +" State: "+cn.getState());
									if((cn.getUsuario().equals(LoginActivity.pref.getString("username", "default")+"@localhost")) && (cn.getContacto().equals(_state.getName()))){
										db.updateContact(new Contact(cn.getID(), LoginActivity.pref.getString("username", "default")+"@localhost", _state.getName(), false)); 
										Log.d("AddContactActivity CHECK FALSE","ENCONTRADO: "+ cn.getState());
									}
								}
								List<ChatContact> chatContacts = db.getAllChatContacts(); 
								for (ChatContact cn : chatContacts) {
									Log.d("AddContactActivity","User: "+ cn.getUser() + " Contact: "+ cn.getChatContact());
									if((cn.getUser().equals(LoginActivity.pref.getString("username", "default")+"@localhost")) && (cn.getChatContact().equals(_state.getName()))){
										db.deleteChatContact(new ChatContact(cn.getID(), LoginActivity.pref.getString("username", "default")+"@localhost", _state.getName(), cn.isPrivate())); 

									}
								}
								
								db.close();//este codigo es nuevo
							
							}
							
							Toast.makeText(getApplicationContext(),cb.getText() + " -> "+ note, Toast.LENGTH_LONG)
							.show();

							_state.setSelected(cb.isChecked());
							((Connect) getApplication()).Contacts(_state);
							progress.dismiss();
						
						}else{
							cb.toggle();
							Toast.makeText(getApplicationContext(), "There is no connection, wait for reconnection...",
							Toast.LENGTH_LONG).show();
							}
						}
					});
				
				holder.imageDelete.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						if(Connect.connectionStatus != false){
							
							ImageButton ib = (ImageButton) v;
							States _state = (States) ib.getTag();
						
							try{
								((Connect) getApplication()).DeleteContact(_state.getName());
								_state.setSelected(false);
								((Connect) getApplication()).Contacts(_state);
								((Connect) getApplication()).DBDeleteContact(_state.getName());
								
								DatabaseHandler db = new DatabaseHandler(context);
								List<Contact> contacts = db.getAllContacts(); 
								for (Contact cn : contacts) {
						        	Log.d("AddContactActivityImagDelete","User: "+ cn.getUsuario() + " Contact: "+ cn.getContacto());
									if((cn.getUsuario().equals(LoginActivity.pref.getString("username", "default")+"@localhost")) && (cn.getContacto().equals(_state.getName()))){
										db.deleteContact(new Contact(cn.getID(), "deleted", "deleted", false));
										ChatListActivity.deleteFromChatList(cn.getContacto(), true);
										ChatListActivity.deleteFromChatList(cn.getContacto(), false);
										Log.d("AddContactActivityImagDelete","LocalUser: " +LoginActivity.pref.getString("username", "default")+"@localhost"+ " RemoteUser: " + _state.getName() + " ID: " + cn.getID() );
									}
								}
								
								List<ChatContact> chatContacts = db.getAllChatContacts(); 
								for (ChatContact cn : chatContacts) {
									Log.d("AddContactActivity","User: "+ cn.getUser() + " Contact: "+ cn.getChatContact());
									if((cn.getUser().equals(LoginActivity.pref.getString("username", "default")+"@localhost")) && (cn.getChatContact().equals(_state.getName()))){
										db.deleteChatContact(new ChatContact(cn.getID(), LoginActivity.pref.getString("username", "default")+"@localhost", _state.getName(), cn.isPrivate())); 
									}
								}
								
								db.close();//este codigo es nuevo
								
								Toast.makeText(getApplicationContext(),
								"The user: "+ _state.getName() + " has been deleted", Toast.LENGTH_LONG)
								.show();

								new AlertDialog.Builder(context)
							    .setTitle("An Entry Has Been Deleted")
							    .setMessage("The user: "+_state.getName()+". Has been deleted.")
							    .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
							        public void onClick(DialogInterface dialog, int which) { 
							            // continue with delete
							        	displayListView();
							        }
							     })
							    .setCancelable(false)
							    .setIcon(android.R.drawable.ic_dialog_alert)
							    .show();
	
							}catch(Exception e){
								Toast.makeText(getApplicationContext(), "User could not be deleted try again...",
										Toast.LENGTH_LONG).show();
							}

						}else{
						Toast.makeText(getApplicationContext(), "There is no connection, wait for reconnection...",
						Toast.LENGTH_LONG).show();
						}
					}
				});
				
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			States state = stateList.get(position);

			holder.name.setText(state.getName().substring(0,state.getName().indexOf("@")));
			holder.name.setChecked(state.isSelected());
			holder.name.setTag(state);
			holder.imageDelete.setTag(state);
			return convertView;
		}	
	}

	public void AddUser(View v){		
		if(Connect.connectionStatus != false){
			progress = new ProgressDialog(context);
		    progress.setMessage("Wait while adding contact...");
		    progress.show();
		    
			addUserEdit = (EditText) findViewById(R.id.adduser);
			addUser = addUserEdit.getText().toString();
			addUser = addUser.toLowerCase(Locale.getDefault());
	    	 
			((Connect) this.getApplication()).AddContact(addUser);

			toast = ((Connect) this.getApplication()).getToast();
	    	 
			if(toast == null){
				progress.dismiss();
				Toast.makeText(getApplicationContext(), "The user: '" + addUser + "' has been added",
						Toast.LENGTH_LONG).show();
			}else{
				progress.dismiss();
				Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_LONG).show();
			}
			
		}else{
			Toast.makeText(getApplicationContext(), "There is no connection, wait for reconnection...",
		    Toast.LENGTH_LONG).show();
		}	
	}
	
	public void BotonDelActionBar(MenuItem item){
		Intent intent = new Intent(this, AddContactActivity.class);
		startActivity(intent);
	}
  	
  	public void StatusBtn(MenuItem item){
		Intent intent = new Intent(this, StatusActivity.class);
		startActivity(intent);
        isRunning = false;
        finish();
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
		getMenuInflater().inflate(R.menu.add_contact, menu);
		return true;
	}
}
