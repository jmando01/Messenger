package com.example.messenger;

public class Contact {
	
	//private variables
	int _id;
	String _usuario;
	String _contacto;
	boolean _state;
	
	// Empty constructor
	public Contact(){
		
	}
	// constructor
	public Contact(int id, String usuario, String contacto, Boolean state){
		this._id = id;
		this._usuario = usuario;
		this._contacto = contacto;
		this._state = state;

	}
	
	// constructor
	public Contact(String usuario, String contacto, Boolean state){
		this._usuario = usuario;
		this._contacto = contacto;
		this._state = state;

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
	public String getUsuario(){
		return this._usuario;
	}
	
	// setting name
	public void setUsuario(String usuario){
		this._usuario = usuario;
	}
	
	// getting phone number
	public String getContacto(){
		return this._contacto;
	}
	
	// setting phone number
	public void setContacto(String contacto){
		this._contacto = contacto;
	}
	
	// getting phone number
	public Boolean getState(){
		return this._state;
	}
		
	// setting phone number
	public void setState(Boolean state){
		this._state = state;
	}
}
