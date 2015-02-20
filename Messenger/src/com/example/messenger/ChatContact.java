package com.example.messenger;

public class ChatContact {
	//private variables
	
	int _id;
	String _user;
	String _chatContact;
	boolean _priv;
	
	// Empty constructor
	public ChatContact(){
			
	}
	
	// constructor
	public ChatContact(int id, String user, String chatContact, boolean priv){
		this._id = id;
		this._user = user;
		this._chatContact = chatContact;
		this._priv = priv;

	}
	// constructor
	public ChatContact(String user, String chatContact, boolean priv){
		this._user = user;
		this._chatContact = chatContact;
		this._priv = priv;
	}
	
	// getting ID
	public int getID(){
		return this._id;
	}
		
	// setting id
	public void setID(int id){
		this._id = id;
	}
		
	// getting User
	public String getUser(){
		return this._user;
	}
			
	// setting User
	public void setUser(String user){
		this._user = user;
	}
	
	// getting name
	public String getChatContact(){
		return this._chatContact;
	}
		
	// setting name
	public void setChatContact(String chatContact){
		this._chatContact = chatContact;
	}
	
	// getting name
	public boolean isPrivate(){
		return this._priv;
	}
			
	// setting name
	public void setPrivate(boolean priv){
		this._priv = priv;
	}
}
