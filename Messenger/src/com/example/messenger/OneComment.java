package com.example.messenger;

public class OneComment {
	public boolean left;
	public String comment;
	int countDown;
	int ID;
	public String date;
	
	public OneComment(boolean left, String comment) {
		super();
		this.left = left;
		this.comment = comment;
	}
	
	public OneComment(boolean left, String comment, int countDown, int ID, String date) {
		super();
		this.left = left;
		this.comment = comment;
		this.countDown = countDown;
		this.ID = ID;
		this.date = date;
	}
	
	// getting ID
		public String getComment(){
			return this.comment;
		}
		
		// setting id
		public void setComment(String comment){
			this.comment = comment;
		}
		
		// getting name
		public int getCountDown(){
			return this.countDown;
		}
		
		// setting name
		public void setCountDown(int countDown){
			this.countDown = countDown;
		}
		
		// getting phone number
		public int getID(){
			return this.ID;
		}
		
		// setting phone number
		public void setID(int ID){
			this.ID = ID;
		}
		
		// getting phone number
		public String getDate(){
			return this.date;
		}
				
		// setting phone number
		public void setDate(String date){
			this.date = date;
		}
}
