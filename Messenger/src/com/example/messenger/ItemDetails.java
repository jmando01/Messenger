package com.example.messenger;

public class ItemDetails {
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public int getImage() {
		return image;
	}
	public void setImage(int image) {
		this.image = image;
	}
	public String getCounter(){
		return counter;
	}
	public void setCounter(String counter){
		this.counter = counter;
	}
	public boolean isPrivate(){
		return priva;
	}
	public void setPrivate(boolean priva){
		this.priva = priva;
	}
	
	public ItemDetails(String name, String message, String date, String counter, boolean priva, int image){
		this.name = name;
		this.message = message;
		this.date = date;
		this.counter = counter;
		this.priva = priva;
		this.image = image;
	}
	
	public ItemDetails(){
		
	}
	
	private String name ;
	private String message;
	private String date;
	private String counter;
	private boolean priva;
	private int image;
}
