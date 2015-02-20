package com.example.messenger;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class OptionsActivity extends Activity {
	
	private ImageView imageView;
	private boolean takeWithCamera = false;
	private Bitmap imageBitmap;
	private Photo photo;
	private Handler mHandler = new Handler();
	private Context mContext;
	
	static final int REQUEST_IMAGE_CAPTURE = 1;
	static boolean isRunning;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_options);
		
		Log.d("OptionActivity", "Oncreate started");
		setTitle("Priva Messenger");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		mContext = this;
		isRunning = true;
		imageView = (ImageView) findViewById(R.id.myPhoto);
		
		try {
			Log.d("OptionActivity", "Getting this user photo");
			Photo photo = new Photo();
			photo = GetPhotoFromLocalDB();
			imageView.setImageBitmap(Bitmap.createScaledBitmap(photo.getPhoto(), 300, 350, true));
		} catch (Exception e) {
			Log.d("OptionActivity", "User photo not found");
			imageView.setImageResource(R.drawable.ic_perfil); // en caso de que no encuentre una foto mia le pongo la de default
			e.getStackTrace();
		}
		
		imageView.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
				final CharSequence[] items = {
		                "Open Gallery", "Take a Picture"
		        };

		        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		        builder.setIcon(R.drawable.ic_launcher);
		        builder.setTitle("Make your selection!");
		        builder.setItems(items, new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int item) {
		                // Do something with the selection
		            	if(item == 0){
		            		Log.d("OptionActivity", "GalleryBtn has been pressed");
		            	    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		                    intent.setType("image/*");
		                    startActivityForResult(Intent.createChooser(intent, "Select File"), 1);
		            	}
		            	
		            	if(item == 1){
		            		Log.d("OptionActivity", "CameraBtn has been pressed");
		            		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		            		if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
		            			takeWithCamera = true;
		            			startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
		            		}
		            	}
		            }
		        });
		        AlertDialog alert = builder.create();
		        alert.show();
			}
		});
		
		String [] options = new String [] {"Information", "Tell a Friend", "Account", "About"} ;
		
		
		ListView listView = (ListView) findViewById(R.id.optionList);
		
		listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, options));
		
		listView.setOnItemClickListener(new OnItemClickListener() {
						public void onItemClick(AdapterView<?> parent, View view,
								int position, long id) {
							// When clicked, show a toast with the TextView text
							
							if(position == 0){
								final CharSequence[] items = {
						                "List of Q&A", "Contact Us"
						        };

						        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
						        builder.setIcon(R.drawable.ic_launcher);
						        builder.setTitle("Account Options!");
						        builder.setItems(items, new DialogInterface.OnClickListener() {
						            public void onClick(DialogInterface dialog, int item) {
						                // Do something with the selection
						            	if(item == 0){
						            		
						            	}
						            	
						            	if(item == 1){
						            		Intent intent = new Intent(OptionsActivity.this, ESupportActivity.class);
						            		startActivity(intent);
						            	}
						            }
						        });
						        AlertDialog alert = builder.create();
						        alert.show();
							}
							
							if(position == 1){
								
							}
							
							if(position == 2){
								final CharSequence[] items = {
						                "Delete Account", "Change Password", "Logout"
						        };

						        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
						        builder.setIcon(R.drawable.ic_launcher);
						        builder.setTitle("Account Options!");
						        builder.setItems(items, new DialogInterface.OnClickListener() {
						            public void onClick(DialogInterface dialog, int item) {
						                // Do something with the selection
						            	if(item == 0){
						            		
						            	}
						            	
						            	if(item == 1){
						            		
						            	}
						            	
						            	if(item == 2){
						            		if(LoginActivity.pref.getBoolean("firstRun", false) == true){
						            			
						            			Editor ed = LoginActivity.pref.edit();
						            			ed.putBoolean("firstRun", false);
						            			ed.commit();
						            			
						            			Log.d("OptionsActivity","SharedPref pass");
						            			
						            		}
						            			
						            		((Connect) getApplication()).Disconect();
						            		//((Connect) getApplication()).ClearNotification();
						            		
						            		if(LoginActivity.class != null){
						            			LoginActivity.loginActivity.finish();
						            		}
						            		
						            		ChatListActivity.mainActivity.finish();
						            		finish();
						            		
						            		Intent intent = new Intent(OptionsActivity.this, LoginActivity.class);
						            		startActivity(intent);
						            	}
						            }
						        });
						        AlertDialog alert = builder.create();
						        alert.show();
							}
							
							if(position == 3){
								
							}
							
							String option = (String) parent.getItemAtPosition(position);
							
							Toast.makeText(getApplicationContext(),
									"Clicked on : " + option, Toast.LENGTH_LONG)
									.show();
						}
					});
	}

	//Obtiene los datos que retorna el intent al tomar la foto.
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("OptionsActivity", "requestCode: "+ requestCode + ". resultCode: " + resultCode);
			
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date = sdf.format(new Date());
		
		
		if(resultCode==0){
			takeWithCamera = false;
		}
			
		if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && takeWithCamera) {
			Log.i("OptionsActivity", "Tomada desde la camara");
				
			takeWithCamera = false;
			
	
			// Find the last picture
			String[] projection = new String[]{
			    MediaStore.Images.ImageColumns._ID,
			    MediaStore.Images.ImageColumns.DATA,
			    MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
			    MediaStore.Images.ImageColumns.DATE_TAKEN,
			    MediaStore.Images.ImageColumns.MIME_TYPE
			    };
			final Cursor cursor = getContentResolver()
			        .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, 
			               null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");

			// Put it in the image view
			if (cursor.moveToFirst()) {
			    String imageLocation = cursor.getString(1);
			    File imageFile = new File(imageLocation);
			    if (imageFile.exists()) {   // TODO: is there a better way to do this?
			    	
			        imageBitmap = BitmapFactory.decodeFile(imageLocation);
			        
			        photo = new Photo(LoginActivity.pref.getString("username", "default")+"@localhost", Bitmap.createScaledBitmap(imageBitmap, 300, 350, true), date);

					UpdloadThreat(imageLocation, date);
			    }
			} 
		    
		}else if( requestCode == 1 && resultCode == RESULT_OK && !takeWithCamera) {
		    	
		    Log.i("OptionActivity", "Tomada desde la galleria de fotos");
		    	
		    //Aqui esta la Direccion
		    Uri selectedImageUri = data.getData();
		    String tempPath = getPath(selectedImageUri, this);         
	        Log.d("OptionActivity", "Path: " + getPath(selectedImageUri, this));
	            
	        
	        BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
	        imageBitmap = BitmapFactory.decodeFile(tempPath, btmapOptions);
	        photo = new Photo(LoginActivity.pref.getString("username", "default")+"@localhost", Bitmap.createScaledBitmap(imageBitmap, 300, 350, true), date);           
 
		    UpdloadThreat(tempPath, date);

		}
	}
		
	public String getPath(Uri uri, Activity activity) {
		
        String[] projection = { MediaColumns.DATA };
	    Cursor cursor = activity.getContentResolver().query(uri, projection, null, null, null);
	    int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
	    cursor.moveToFirst();
	    return cursor.getString(column_index);
	}
	
	public void DeletePhotoFromLocalDB(){
		DatabaseHandler db = new DatabaseHandler(this);
		db.deletePhotoByUserName(LoginActivity.pref.getString("username", "default")+"@localhost");
		db.close();
	}
	
	public void AddPhotoToLocalDB(Photo photo){
		DatabaseHandler db = new DatabaseHandler(this);
		db.addPhoto(photo);
		db.close();
	}
	
	public Photo GetPhotoFromLocalDB(){
		Photo photo = new Photo();
		DatabaseHandler db = new DatabaseHandler(this);
		photo = db.getPhotoDBByUserName(LoginActivity.pref.getString("username", "default")+"@localhost");
		db.close();
		
		return photo;
	}
	
	public void UpdloadThreat(final String imagePath, final String date){
		new Thread(new Runnable() {
	        public void run() {
	        	
					try {
						Log.d("OptionActivity", "Inserting photo to external DB");
						((Connect) getApplication()).uploadImage(imagePath, date);
						Log.d("OptionActivity", "Deleting photo to internal DB");
						DeletePhotoFromLocalDB();
						Log.d("OptionActivity", "Inserting photo to internal DB");
					    AddPhotoToLocalDB(photo);
					    if(OptionsActivity.isRunning){
					    	mHandler.post(new Runnable() {
			 	                public void run() {
					    	imageView.setImageBitmap(Bitmap.createScaledBitmap(imageBitmap, 300, 350, true));
			 	                }	
			 	            });
					    }
					    
					} catch (Exception e) {
						// TODO Auto-generated catch block
						Log.d("OptionActivity", "An error ocurred inserting the user photo");
						e.printStackTrace();
					}
				
	        }
	    }).start();
	}
	
	//En esta parte limpiamos la lista donde estan los contactos que queremos en caso de que haga login otra persona.
    @Override
	protected void onResume(){
		super.onResume();
		Log.d("OptionsActivity","Onresume() has been called");
		isRunning = true;
		//((Connect) this.getApplication()).clearList();// Verificar esta parte porque creo que ya la lista se limpia antes de llenarse.
	}
    
    @Override
	protected void onPause(){
		super.onPause();
		Log.d("OptionsActivity","OnPause() has been called");
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
		getMenuInflater().inflate(R.menu.options, menu);
		return true;
	}
}
