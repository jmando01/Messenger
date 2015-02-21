package com.example.messenger;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {

	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "localDBManager";

	// Message table name
	private static final String MESSAGE_ARCHIVE = "messageArchive";
	// Contacts Table
	private static final String TABLE_CONTACTS = "contacts";
	// Chat Contacts Table
	private static final String TABLE_CHAT_CONTACTS = "chat_contacts";
	// Picture table
	private static final String TABLE_PHOTO = "photo";
	
	
	
	
	// Message Table Columns names
	private static final String KEY_ID = "id";
	private static final String KEY_FROMJID = "fromjid";
	private static final String KEY_TOJID = "tojid";
	private static final String KEY_SENTDATE = "sentdate";
	private static final String KEY_BODY = "body";
	
	//Contact Table Columns names
	private static final String KEY_USER = "user";
	private static final String KEY_CONTACT = "contact";
	private static final String KEY_STATE = "state";
	
	//ChatContact
	private static final String KEY_CHAT_CONTACT = "chatContact";
	
	//Photo Table Columns names
	private static final String KEY_USERNAME = "username";
	private static final String KEY_PHOTO = "photo";
	private static final String KEY_LASTUPDATE = "lastupdate";
	

	
	
	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_MESSAGE_ARCHIVE_TABLE = "CREATE TABLE " + MESSAGE_ARCHIVE + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," 
				+ KEY_FROMJID + " TEXT,"
				+ KEY_TOJID + " TEXT,"
				+ KEY_SENTDATE + " TEXT,"
				+ KEY_BODY + " TEXT,"
				+ KEY_STATE + " INTEGER "+ ")";
		db.execSQL(CREATE_MESSAGE_ARCHIVE_TABLE);
		
		String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_USER + " TEXT,"
				+ KEY_CONTACT + " TEXT," + KEY_STATE + " INTEGER " + ")";
		db.execSQL(CREATE_CONTACTS_TABLE);
		
		String CREATE_CHAT_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CHAT_CONTACTS + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_USER + " TEXT,"
				+ KEY_CHAT_CONTACT + " TEXT, " + KEY_STATE + " INTEGER " + ")";
		db.execSQL(CREATE_CHAT_CONTACTS_TABLE);
		
		String CREATE_PHOTO_TABLE = "CREATE TABLE " + TABLE_PHOTO + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," 
				+ KEY_USERNAME + " TEXT,"
				+ KEY_PHOTO + " BLOB,"
				+ KEY_LASTUPDATE + " TEXT"+ ")";
		db.execSQL(CREATE_PHOTO_TABLE);
		
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + MESSAGE_ARCHIVE);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHAT_CONTACTS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHOTO);
		// Create tables again
		onCreate(db);
	}

	/**
	 * All CRUD(Create, Read, Update, Delete) Operations
	 */

	// Adding new message
	void addMessage(MessageDB message) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_FROMJID, message.getFromJid()); // Contact Name
		values.put(KEY_TOJID, message.getToJid()); // Contact Phone
		values.put(KEY_SENTDATE, message.getSentDate());
		values.put(KEY_BODY, message.getBody());
		values.put(KEY_STATE, (message.isPrivate()) ? 1 : 0);
		
		// Inserting Row
		db.insert(MESSAGE_ARCHIVE, null, values);
		db.close(); // Closing database connection
	}	
		
	// Adding new contact
	void addContact(Contact contact) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_USER, contact.getUsuario()); // Contact Name
		values.put(KEY_CONTACT, contact.getContacto()); // Contact Phone
		values.put(KEY_STATE, (contact.getState()) ? 1 : 0);
		// Inserting Row
		db.insert(TABLE_CONTACTS, null, values);
		db.close(); // Closing database connection
	}
	
		// Adding new Chat Contact
	void addChatContact(ChatContact chatContact) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_USER, chatContact.getUser());
		values.put(KEY_CHAT_CONTACT, chatContact.getChatContact());
		values.put(KEY_STATE, (chatContact.isPrivate()) ? 1 : 0);
				
		// Inserting Row
		db.insert(TABLE_CHAT_CONTACTS, null, values);
		db.close(); // Closing database connection
	}
	
	// Adding new Photo
		void addPhoto(Photo photo) {
			SQLiteDatabase db = this.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put(KEY_USERNAME, photo.getUserName()); 
			values.put(KEY_PHOTO, photo.getPhotoForDB()); 
			values.put(KEY_LASTUPDATE, photo.getLastUpdate());
			// Inserting Row
			db.insert(TABLE_PHOTO, null, values);
			db.close(); // Closing database connection
		}
		
		
		

	// Getting single message
	MessageDB getMessageDB(int id) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(MESSAGE_ARCHIVE, new String[] { KEY_ID,
				KEY_FROMJID, KEY_TOJID, KEY_SENTDATE, KEY_BODY, KEY_STATE  }, KEY_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();

		MessageDB message = new MessageDB(Integer.parseInt(cursor.getString(0)),
				cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), (cursor.getInt(5) != 0) );
		// return message
		return message;
	}
		
		// Getting single contact
		Contact getContact(int id) {
			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.query(TABLE_CONTACTS, new String[] { KEY_ID,
					KEY_USER, KEY_CONTACT, KEY_STATE }, KEY_ID + "=?",
					new String[] { String.valueOf(id) }, null, null, null, null);
			if (cursor != null)
				cursor.moveToFirst();
			Contact contact = new Contact(Integer.parseInt(cursor.getString(0)),
					cursor.getString(1), cursor.getString(2), (cursor.getInt(3) != 0));
			// return contact
			return contact;
		}
			
		// Getting single Chatcontact
		ChatContact getChatContact(int id) {
			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.query(TABLE_CHAT_CONTACTS, new String[] { KEY_ID,
					KEY_USER, KEY_CHAT_CONTACT, KEY_STATE }, KEY_ID + "=?",
						new String[] { String.valueOf(id) }, null, null, null, null);
		if (cursor != null)
				cursor.moveToFirst();
			ChatContact chatContact = new ChatContact(Integer.parseInt(cursor.getString(0)),
					cursor.getString(1), cursor.getString(2), (cursor.getInt(3) != 0));
			// return contact
			return chatContact;
		}
		
		// Getting single Photo
				Photo getPhotoDBByID(int id) {
					SQLiteDatabase db = this.getReadableDatabase();

					Cursor cursor = db.query(TABLE_PHOTO, new String[] { KEY_ID,
							KEY_USERNAME, KEY_PHOTO, KEY_LASTUPDATE }, KEY_ID + "=?",
							new String[] { String.valueOf(id) }, null, null, null);
					if (cursor != null)
						cursor.moveToFirst();
					
					Photo photo =new Photo();
					photo = new Photo(Integer.parseInt(cursor.getString(0)),
							cursor.getString(1), photo.convertBlobToBitmap( cursor.getBlob(2)), cursor.getString(3));
					// return message
					return photo;
				}
				
				// Getting single Photo
				Photo getPhotoDBByUserName(String userName) {
					SQLiteDatabase db = this.getReadableDatabase();

					Cursor cursor = db.query(TABLE_PHOTO, new String[] { KEY_ID,
							KEY_USERNAME, KEY_PHOTO, KEY_LASTUPDATE }, KEY_USERNAME + "=?",
							new String[] { String.valueOf(userName) }, null, null, null);
					if (cursor != null)
						cursor.moveToFirst();
					
					Photo photo =new Photo();
					photo = new Photo(Integer.parseInt(cursor.getString(0)),
							cursor.getString(1), photo.convertBlobToBitmap( cursor.getBlob(2)), cursor.getString(3));
					// return message
					return photo;
				}
		
		
	
	// Getting All messages
	public List<MessageDB> getAllMessages() {
		List<MessageDB> messageList = new ArrayList<MessageDB>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + MESSAGE_ARCHIVE;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				MessageDB message = new MessageDB();
				message.setID(Integer.parseInt(cursor.getString(0)));
				message.setFromJid(cursor.getString(1));
				message.setToJid(cursor.getString(2));
				message.setSentDate(cursor.getString(3));
				message.setBody(cursor.getString(4));
				message.setPrivate((cursor.getInt(5) != 0));
				
				// Adding contact to list
				messageList.add(message);
			} while (cursor.moveToNext());
		}

		// return contact list
		return messageList;
	}
		
	// Getting All Contacts
	public List<Contact> getAllContacts() {
		List<Contact> contactList = new ArrayList<Contact>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Contact contact = new Contact();
				contact.setID(Integer.parseInt(cursor.getString(0)));
				contact.setUsuario(cursor.getString(1));
				contact.setContacto(cursor.getString(2));
				contact.setState((cursor.getInt(3) != 0));
				
				// Adding contact to list
				contactList.add(contact);
			} while (cursor.moveToNext());
		}
		// return contact list
		return contactList;
	}
		

	// Getting All ChatContacts
	public List<ChatContact> getAllChatContacts() {
		List<ChatContact> chatContactList = new ArrayList<ChatContact>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_CHAT_CONTACTS;
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				ChatContact chatContact = new ChatContact();
				chatContact.setID(Integer.parseInt(cursor.getString(0)));
				chatContact.setUser(cursor.getString(1));
				chatContact.setChatContact(cursor.getString(2));
				chatContact.setPrivate((cursor.getInt(3) != 0));
				// Adding contact to list
				chatContactList.add(chatContact);
			} while (cursor.moveToNext());
		}
		// return contact list
		return chatContactList;
	}
	
	// Getting All photo
	public List<Photo> getAllPhoto() {
		List<Photo> photoList = new ArrayList<Photo>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_PHOTO;
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Photo photo = new Photo();
				photo.setID(Integer.parseInt(cursor.getString(0)));
				photo.setUsername(cursor.getString(1));
				photo.setPhotoForDB(cursor.getBlob(2));
				photo.setLastUpdate(cursor.getString(3));
				// Adding contact to list
				photoList.add(photo);
			} while (cursor.moveToNext());
		}
		// return contact list
		return photoList;
	}
	
	// Updating single message
	public int updateMessage(MessageDB message) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_FROMJID, message.getFromJid());
		values.put(KEY_TOJID, message.getToJid());
		values.put(KEY_SENTDATE, message.getSentDate());
		values.put(KEY_BODY, message.getBody());
		values.put(KEY_STATE, (message.isPrivate()) ? 1 : 0);
		// updating row
		return db.update(MESSAGE_ARCHIVE, values, KEY_ID + " = ?",
				new String[] { String.valueOf(message.getID()) });
	}
		
	// Updating single contact
	public int updateContact(Contact contact) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_USER, contact.getUsuario());
		values.put(KEY_CONTACT, contact.getContacto());
		values.put(KEY_STATE, (contact.getState()) ? 1 : 0);
		// updating row
		return db.update(TABLE_CONTACTS, values, KEY_ID + " = ?",
				new String[] { String.valueOf(contact.getID()) });
	}

	
	// Updating single chatContact
	public int updateChatContact(ChatContact chatContact) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_USER, chatContact.getUser());
		values.put(KEY_CHAT_CONTACT, chatContact.getChatContact());
		values.put(KEY_STATE, (chatContact.isPrivate()) ? 1 : 0);
		// updating row
		return db.update(TABLE_CHAT_CONTACTS, values, KEY_ID + " = ?",
				new String[] { String.valueOf(chatContact.getID()) });
	}

	// Updating single photo
	public int updatePhoto(Photo photo) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_USERNAME, photo.getUserName());
		values.put(KEY_PHOTO, photo.getPhotoForDB());
		values.put(KEY_PHOTO, photo.getLastUpdate());
		// updating row
		return db.update(TABLE_PHOTO, values, KEY_ID + " = ?",
				new String[] { String.valueOf(photo.getID()) });
	}

	
	// Deleting single message
	public void deleteMessage(MessageDB message) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(MESSAGE_ARCHIVE, KEY_ID + " = ?",
				new String[] { String.valueOf(message.getID()) });
		db.close();
	}
		
	// Deleting single contact
	public void deleteContact(Contact contact) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_CONTACTS, KEY_ID + " = ?",
				new String[] { String.valueOf(contact.getID()) });
		db.close();
	}
	
	// Deleting single chatContact
	public void deleteChatContact(ChatContact chatContact) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_CHAT_CONTACTS, KEY_ID + " = ?",
				new String[] { String.valueOf(chatContact.getID()) });
		db.close();
	}
	
	// Deleting single photo
	public void deletePhotoByID(Photo photo) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_PHOTO, KEY_ID + " = ?",
				new String[] { String.valueOf(photo.getID()) });
		db.close();
	}
	
	// Deleting single photo
		public void deletePhotoByUserName(String userName) {
			SQLiteDatabase db = this.getWritableDatabase();
			db.delete(TABLE_PHOTO, KEY_USERNAME + " = ?",
					new String[] { String.valueOf(userName) });
			db.close();
		}

	// Getting messages Count
	public int getMessagesCount() {
		String countQuery = "SELECT  * FROM " + MESSAGE_ARCHIVE;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		cursor.close();

		// return count
		return cursor.getCount();
	}
		
	// Getting contacts Count
	public int getContactsCount() {
		String countQuery = "SELECT  * FROM " + TABLE_CONTACTS;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		cursor.close();
		// return count
		return cursor.getCount();
	}
	
	// Getting chatcontacts Count
	public int getChatContactsCount() {
		String countQuery = "SELECT  * FROM " + TABLE_CHAT_CONTACTS;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		cursor.close();
		// return count
		return cursor.getCount();
	}
	
	// Getting photo Count
	public int getPhotoCount() {
		String countQuery = "SELECT  * FROM " + TABLE_PHOTO;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		cursor.close();
		// return count
		return cursor.getCount();
	}
						
}