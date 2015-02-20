package com.example.messenger;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class ContactActivity extends Activity implements OnClickListener, OnEditorActionListener, OnItemClickListener {
	
	private ListView mListView;
	private ItemListBaseAdapter mAdapter;
	private Button btnSearch, btnLeft;
	private EditText mtxt;
	private boolean userExist;
	private ArrayList<States> contactList = new ArrayList<States>();
	private ArrayList<ItemDetails> image_details = new ArrayList<ItemDetails>();
	
	static boolean isRunning;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_contact);
		
		setTitle("Priva Messenger");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		mListView = (ListView) findViewById(R.id.mListView);
		mAdapter = new ItemListBaseAdapter(this, image_details);
		btnSearch = (Button) findViewById(R.id.btnSearch);
		btnLeft = (Button) findViewById(R.id.btnLeft);
		mtxt = (EditText) findViewById(R.id.edSearch);
		
		DatabaseHandler db = new DatabaseHandler(this);
		List<Contact> contacts = db.getAllContacts(); 
		for (Contact cn : contacts) {
			if((cn.getUsuario().equals(LoginActivity.pref.getString("username", "default")+"@localhost")) && (cn.getState() == true)){
				States state = new States(cn.getContacto(), cn.getState());
				contactList.add(state);
			}
		}
		db.close();
		
		isRunning = true;
		
		mtxt.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (0 != mtxt.getText().length()) {
					String spnId = mtxt.getText().toString();
					setSearchResult(spnId);
				} else {
					setData();
				}
			}
		});
		btnLeft.setOnClickListener(this);
		btnSearch.setOnClickListener(this);
		setData();
	}

	ArrayList<String> mAllData;
	
	public void setData() {
		mAllData = new ArrayList<String>();
		mAdapter = new ItemListBaseAdapter(this, image_details);
		image_details.clear();

		for (int i = 0; i < contactList.size(); i++) {
			ItemDetails item = new ItemDetails();
			item.setName(contactList.get(i).getName());
			item.setImage(1);
			mAdapter.addItem(item);
			mAllData.add(contactList.get(i).getName() );
		}
		mListView.setOnItemClickListener(this);
		mListView.setAdapter(mAdapter);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnSearch:
			mtxt.setText("");
			setData();
			break;
		case R.id.btnLeft:
			break;
		}
	}

	public void setSearchResult(String str) {
		mAdapter = new ItemListBaseAdapter(this, image_details);
		image_details.clear();
		for (String temp : mAllData) {
			if (temp.toLowerCase().contains(str.toLowerCase())) {
				ItemDetails item = new ItemDetails();
				item.setName(temp);
				item.setImage(1);
				mAdapter.addItem(item);
			}
		}
		mListView.setAdapter(mAdapter);
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		return false;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		setResult(Activity.RESULT_CANCELED);
		isRunning = false;
	}
	
	@Override
    protected void onPause(){
		super.onPause();
		Log.d("ContactActivity","Estoy en el onPause");
		isRunning = false;
	}
	
	@Override
    protected void onResume(){
		super.onResume();
		Log.d("ContactActivity","Estoy en el onResume");
		isRunning = true;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		//String str = mAdapter.getItem(position);
		
		Object o = mListView.getItemAtPosition(position);
    	ItemDetails obj_itemDetails = (ItemDetails)o;
		
		Toast.makeText(this, obj_itemDetails.getName(), Toast.LENGTH_LONG).show();
		
		Intent intent = new Intent(arg1.getContext(), ChatEntryActivity.class);
	    intent.putExtra("remoteUsername", obj_itemDetails.getName());
	    intent.putExtra("priva", false);
	    startActivity(intent);
	    
	    userExist = false;
	    
	    DatabaseHandler db = new DatabaseHandler(this);
		List<ChatContact> chatContacts = db.getAllChatContacts(); 
		for (ChatContact cn : chatContacts) {
			Log.d("ContactActivity","User: "+ cn.getUser() + " Contact: "+ cn.getChatContact());
			if((cn.getChatContact().equals(obj_itemDetails.getName())) && (cn.getUser().equals(LoginActivity.pref.getString("username", "default")+"@localhost"))){
				userExist = true;
			}
		}
		//esta parte se tiene que arreglar porque si entro con otra cuenta que ya tiene un usuario agregado no me lo agrega
		
		if(userExist == false){
			db.addChatContact(new ChatContact(LoginActivity.pref.getString("username", "default")+"@localhost", obj_itemDetails.getName(), false));
		}
		db.close();
	    finish();
	    isRunning = false;
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
		getMenuInflater().inflate(R.menu.contact, menu);
		return true;
	}
}