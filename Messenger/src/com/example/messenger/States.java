package com.example.messenger;

public class States {

	int ID;
	String name = null;
	boolean selected = false;
	
	// Empty constructor
	public States(){		
	}
	
	//constructor
	public States(int ID, String name, boolean selected) {
		super();
		this.ID = ID;
		this.name = name;
		this.selected = selected;
	}
	
	//constructor
	public States(String name, boolean selected) {
		super();
		this.name = name;
		this.selected = selected;
	}
	
	public int getID() {
		return ID;
	}

	public void setID(int ID) {
		this.ID = ID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
}
