package com.example.messenger;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class Photo {
	//private variables
	private int _id;
	private String _username;
	private Bitmap _photo;
	private String _lastupdate;
		
	// Empty constructor
	public Photo(){
			
	}
		
	// constructor
	public Photo(int id, String username, Bitmap photo, String lastupdate){
		this._id = id;
		this._username = username;
		this._photo = photo;
		this._lastupdate = lastupdate;
	}
		
	// constructor
	public Photo(String username, Bitmap photo, String lastupdate){
		this._username = username;
		this._photo = photo;
		this._lastupdate = lastupdate;
	}
		
	//Convierte de  Blob(byte[]) a Bitmap.
	public Bitmap convertBlobToBitmap(byte[] blobByteArray) 
	{       
	    Bitmap tempBitmap=null;        
	    if(blobByteArray!=null)
	    tempBitmap = BitmapFactory.decodeByteArray(blobByteArray, 0, blobByteArray.length);
	    Log.d("Byte[] a Bitmap", "Byte "+blobByteArray);
	    return tempBitmap;
	}
		
	//Convierte de Bitmap a Blob(byte[]).
	public byte[] convertBitmapToBlob(Bitmap tempBitmap) 
	{
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		tempBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
		byte[] byteArray = stream.toByteArray();
		Log.d("Bitmap a Byte[]", "Byte "+byteArray);
		return byteArray;
	}
		
	// getting ID
	public int getID(){
		return this._id;
	}
		
	// setting id
	public void setID(int id){
		this._id = id;
	}
		
	// getting name
	public String getUserName(){
		return this._username;
	}
		
	// setting name
	public void setUsername(String userName){
		this._username = userName;
	}
		
	// getting photo
	public Bitmap getPhoto(){
		return this._photo;
	}
		
	// getting photo for DB
	public byte[] getPhotoForDB(){
		return convertBitmapToBlob(this._photo);
	}
		
	// setting photo
	public void setPhoto(Bitmap photo){
		this._photo = photo;
	}
		
	// setting photo for DB 
	public void setPhotoForDB(byte[] photo){
		this._photo = convertBlobToBitmap(photo);
	}

	// getting Last Update
	public String getLastUpdate(){
		return this._lastupdate;
	}
				
	// setting id
	public void setLastUpdate(String LastUpdate){
		this._lastupdate = LastUpdate;
	}

}