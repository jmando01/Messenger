package com.example.messenger;

public class CounterMsj {
	
	private String contacto;
	private int cantidadMsj;
	private boolean priva;
	
	public CounterMsj(){
		
	}
	
	public CounterMsj(String _contacto, int _cantidadMsj, boolean priva){
		this.contacto = _contacto;
		this.cantidadMsj = _cantidadMsj;
		this.priva = priva;
	}
	
	public String getContact(){
		return contacto;
	}
	public void setContact(String contact){
		this.contacto = contact;
	}
	public int getCount(){
		return cantidadMsj;
	}
	public void setCount(int NuevoValor){
		this.cantidadMsj = NuevoValor;
	}
	public boolean isPrivate(){
		return priva;
	}
	public void setPrivate(boolean priva){
		this.priva = priva;
	}
}
