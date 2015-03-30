package com.example.messenger;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import oracle.jdbc.driver.OracleResultSet;
import org.jivesoftware.smack.AndroidConnectionConfiguration;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.SmackAndroid;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.RosterPacket;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.ReportedData.Row;
import org.jivesoftware.smackx.bytestreams.socks5.provider.BytestreamsProvider;
import org.jivesoftware.smackx.filetransfer.FileTransfer.Status;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;
import org.jivesoftware.smackx.packet.DelayInformation;
import org.jivesoftware.smackx.ping.packet.Ping;
import org.jivesoftware.smackx.ping.packet.Pong;
import org.jivesoftware.smackx.provider.DiscoverInfoProvider;
import org.jivesoftware.smackx.provider.DiscoverItemsProvider;
import org.jivesoftware.smackx.search.UserSearchManager;
import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.format.DateUtils;
import android.util.Log;

public class Connect extends Application {
	
	private AndroidConnectionConfiguration connConfig;
	private XMPPConnection connection;
	private Roster roster;
	private ArrayList<States> contactList = new ArrayList<States>();
	private String HOST = LoginActivity.localIP;
	private int PORT = 5222;
	private String SERVICE = "localhost";
	private Handler mHandler = new Handler();
	private String fromName;
	private Message message;
	private boolean userExist = false;
	private String toast;
	public static boolean userVerification;
	public static boolean connectionStatus;
	public boolean disconnect;
	private Timer timer;
	private ConnectionListener cl;
	private byte barr[];
	static DatabaseHandler db;
	public static ArrayList<States> chatContactList = new ArrayList<States>(); 
	public static boolean DayMillis;
	public String actualTime;
	
	private Connection dbconn;
	private String DBUSERNAME = "openfire";
	private String DBPASSWORD = "1234";
	private String DBNAME = "XE";
	private int DBPORT = 1521;
	private String DBuserA;
	private String DBuserB;
	private ArrayList<OneComment> history = new ArrayList<OneComment>();
	
	private static Context context;
	private int countMsj=0;
	private String notiMsj="",
			cadenMsj="";
	static boolean notificacionLeida = false,
			exitList = false;
	private ArrayList<String> notiConver = new ArrayList<String>();
	private NotificationCompat.Builder mBuilder;
	private NotificationManager mNotifyMgr;
	
	
	private static ArrayList<CounterMsj> counterMsjContact = new ArrayList<CounterMsj>();
	

	public void conn(){
		
		new Thread(new Runnable() {
	        public void run() {

     	context = getApplicationContext(); 
     	SmackAndroid.init(context);//Con este codigo iniciamos los procesos que involucran a Smack en Android
     	db = new DatabaseHandler(context);

		// Create a connection 
		connConfig = new AndroidConnectionConfiguration(HOST, PORT, SERVICE);
        connection = new XMPPConnection(connConfig); 
        
        
		try {
        	// Connect 
			connection.connect();
			disconnect = false;
			connectionStatus = true;
			userVerification = true;
			Log.d("Connect",  "Succesfully connected to: " + connection.getHost());
        }catch (XMPPException ex) {
        	connectionStatus = false;
            Log.d("Connect",  "Failed to connect to server");
            setConnection(null);
            setPresence(null);
            setKeepAlive(null);
            setConnectionListener();
            if(LoginActivity.pref.getBoolean("firstRun", false) == true){
            	setReconnectionTimer();
            }
            ex.printStackTrace();
            
        }
		
		if(connection.isConnected() == true){ //Aqui quizas podamos usar el comando de isConnected()
			try {	
				//User login
				connection.login(LoginActivity.pref.getString("username", "default"), LoginActivity.pref.getString("password", "default")); 
				Log.d("Connect",  "Logged in as: " + connection.getUser());
				connectionStatus = true;
				disconnect = false;
				// Set the status to available 
				Presence presence = new Presence(Presence.Type.available); 
				presence.setStatus(LoginActivity.pref.getString("state", "Online"));// Este es el estatus del usuario
				connection.sendPacket(presence);
				
				roster = connection.getRoster();
				
				Log.d("Connect",  "Roster subscription mode set to: " + roster.getSubscriptionMode());
    	   
     	   
			} catch (XMPPException ex) {
 			// TODO Auto-generated catch block
				Log.d("Connect",  "Failed to Login to server");
				ex.printStackTrace();
				connectionStatus = false;
				userVerification = false;
				setConnection(null);
	            setPresence(null);
	            setKeepAlive(null);
	            setConnectionListener();
			}
			
			if(connectionStatus == true){
				setConnection(connection);
				setPresence(connection);
				setKeepAlive(connection);   
				setConnectionListener();
	        	setRosterListener();
	        	setRosterExists();
	        	setFileTransferListener();
	        	DBGetChats();
			}
		}  
		SyncContacs();
		Intent intent = new Intent("my-event");
   	 	// add data
    	intent.putExtra("message", "data"); 
    	LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
	        }
	    }).start();
	}
	
	public static String TimeConverter(String time){
		String timeConverted = " ";
		
		if(!(time.equals(" "))){
			long timeAgo = timeStringtoMilis(time);
			long now = System.currentTimeMillis();
			
			Log.d("Connect","TimeAgo: "+timeAgo+". Now: "+now);
			Log.d("Connect","TimeAgo: " + String.valueOf(now - timeAgo));
			
			if(now - timeAgo >= 86400000){
				DayMillis = false;
			}else{
				DayMillis = true;
			}
			
			timeConverted = (String) DateUtils.getRelativeDateTimeString(context, timeAgo, DateUtils.SECOND_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, DateUtils.FORMAT_ABBREV_ALL);
		}
		
		
		return timeConverted;
	}
	
	private static long timeStringtoMilis(String time) {
		long milis = 0;
		
		try {
			SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date 	= sd.parse(time);
			milis 		= date.getTime();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return milis;
	}
	
	public void setRosterExists(){
		
		Collection<RosterEntry> entries = roster.getEntries();
		// En esta parte es que chekeamos si algun usuario nos ha borrado para borrarlo.
		for (RosterEntry entry : entries) {
			Log.d("Connect","User in roster: " + entry.getUser() + ". Type: " + entry.getType());
			
			if(entry.getType().toString().equals("none")){
				Log.d("Connect","Roster user found as 'none': " + entry.getUser());
				if(RosterExist(entry.getUser()) == false){
					Log.d("Connect","User: " + entry.getUser() + ". Has been deleted because found 'none' in roster");
					
					DeleteContact(entry.getUser());
					
					
					States _state = new States(entry.getUser(), false);
					Contacts(_state);
					
					DBDeleteContact(_state.getName());
					
					
					DatabaseHandler ndb = new DatabaseHandler(context);
		    		List<ChatContact> chatContacts = ndb.getAllChatContacts(); 
		    		
		    		for (ChatContact cn : chatContacts) {
		    			Log.d("Connect","User: "+ cn.getUser() + " Contact: "+ cn.getChatContact());
		    			if((cn.getUser().equals(LoginActivity.pref.getString("username", "default")+"@localhost")) && (cn.getChatContact().equals(_state.getName()))){
		    				ndb.deleteChatContact(new ChatContact(cn.getID(), "deleted", "deleted", false));
		    				Log.d("Connect","Contact: "+cn.getChatContact()+"Deleted from chatContact internat DB");
		    			}
		    		}
		    		ndb.close();
					
					DatabaseHandler db = new DatabaseHandler(context);
					List<Contact> contacts = db.getAllContacts();
					
					for (Contact cn : contacts) { 
						
			        	Log.d("Connect","User: "+ cn.getUsuario() + " Contact: "+ cn.getContacto());
						if((cn.getUsuario().equals(LoginActivity.pref.getString("username", "default")+"@localhost")) && (cn.getContacto().equals(_state.getName()))){
							db.deleteContact(new Contact(cn.getID(), "deleted", "deleted", false)); 
							Log.d("Connect"," RemoteUser: " + _state.getName() + " Has been deleted from Contact localDB");
						}
					}
					db.close();//este codigo es nuevo
				}
			}
		}
		//Esta parte es para no borrar a un usuario que me agrego mientras yo estaba desconectado
		if(RosterExist(LoginActivity.pref.getString("username", "default")+"@localhost") == true){
			Log.d("Connect","Me encontre en la lista de usuario a no borrar... borrrando...");
			RosterDelete(LoginActivity.pref.getString("username", "default")+"@localhost");
		}
	}
	
	public void setRosterListener(){
		
		roster.addRosterListener(new RosterListener() {      //Esta parte hay que moverla de lugar      	  
      	  
			@Override
			public void entriesAdded(final Collection<String> addresses) {
				// TODO Auto-generated method stub
				Log.d("Advertecia1", " se anadio a alguien: "+ addresses.toString());
				String contact = addresses.toString();
				contact = contact.substring(1, contact.length() - 1);
				final String updateAddContact = contact;
				
				Log.d("Advertecia2", " se anadio a alguien: "+ contact);
				DatabaseHandler db = new DatabaseHandler(context);
				db.addContact(new Contact(LoginActivity.pref.getString("username", "default")+"@localhost", contact, false));
				db.close();//este codigo es nuevo

				DBInsertContact(contact, false);
				
				if(AddContactActivity.isRunning){
					mHandler.post(new Runnable() {
						public void run() {
							AddContactActivity.updateAddUserList(updateAddContact);
						}
 	               });
				}
				
			}

			@Override
			public void entriesDeleted(Collection<String> addresses) {
				// TODO Auto-generated method stub
				Log.d("Advertecia", " entrada borrada: "+addresses.toString());
				
			}

			@Override
			public void entriesUpdated(Collection<String> addresses) {
				// TODO Auto-generated method stub
				Log.d("Advertecia", " entradas actualizadas"+addresses.toString());
				
			}

			@Override
			public void presenceChanged(Presence addresses) {
				// TODO Auto-generated method stub
				if(addresses.getStatus() != null){
					Log.d("Advertecia", " cambio de estado user: "+addresses.getFrom()+"Estado: "+addresses.getStatus().toString());
				}
			}
              // Ignored events public void entriesAdded(Collection<String> addresses) {}
          });
	}

	public void setReconnectionTimer(){
		
		timer = new Timer();
		
		timer.schedule(new TimerTask() {
			  @Override
			  public void run() {
			
				Log.d("Connect","Reconnection Timer Started");
			    conn();
			    if(connection.isConnected() == true){
			    	
			    	mHandler.post(new Runnable() {
	 	                public void run() {
	 	                	
	 	                	String state = new String();
	 	                	state = "Online";
	        			
	        			
	 	                	if(ChatListActivity.isRunning == true){
	 	                		String ChatList = "Chat List";
	 	                		Log.d("Connect RS","ChatListActivity is running");
	 	                		ChatListActivity.updateGlobal(ChatList);
	                		
	 	                	}	
	                	
	 	                	if(ChatEntryActivity.isRunning == true && roster.getPresence(ChatEntryActivity.remoteUsername).getStatus() != null ){
	 	                		Log.d("Connect RS","ChatEntryActivity is running");
	 	                		state = roster.getPresence(ChatEntryActivity.remoteUsername).getStatus().toString();
	 	                		ChatEntryActivity.updateGlobal(state);	

	 	                	}
	                	
	 	                	if(ChatEntryActivity.isRunning == true && roster.getPresence(ChatEntryActivity.remoteUsername).getStatus() == null ){
	 	                		Log.d("Connect RS","ChatEntryActivity is running");
	 	                		state = "Offline";
	 	                		ChatEntryActivity.updateGlobal(state);

	 	                	}
	                	
	 	                	if(StatusActivity.isRunning == true){
	 	                		Log.d("Connect RS","StatusActivity is running");
	 	                		StatusActivity.updateStatus(LoginActivity.pref.getString("state", "Online"));
                		
	 	                	}
	 	                }
 	                	
	 	              	});
			    	}
			  }
			}, 12*1000);
	}
	
	public void UpdateStatus(String status){
	
		Presence presence = new Presence(Presence.Type.available); 
		presence.setStatus(status);// Este es el estatus del usuario
		connection.sendPacket(presence);
	
	}
	
	//Aca escuchamos los ping que manda el servidor para verificar si seguimos vivos y le respondemos.               
	public void setKeepAlive(final XMPPConnection connection){
	
		if(connection != null){
			
			connection.addPacketListener(new PacketListener() {
		    	
			    @Override
			    public void processPacket(Packet packet) {
			    	Log.d("Privacy", " Paso algo con el pong");
			        connection.sendPacket(new Pong((Ping) packet));
			        
			    }}, new PacketFilter() {

			    @Override
			    public boolean accept(Packet packet) {
			    	Log.d("Privacy", " SE ENVIO UN PING");
			        return packet instanceof Ping;
			    }}); 
		}
		 
	}
	
	//Este metedo es el que nos indica si hubo un error en la conexion y tambien nos reconecta.
	public void setConnectionListener(){
		
		if(connection != null){
			
			connection.addConnectionListener(cl = new ConnectionListener() {

	            @Override
	            public void reconnectionSuccessful() {
	           	 
	                Log.i("Reconnection","Successfully reconnected to the XMPP server.");
	                connectionStatus = true;
	                //Tenemos que mandar el estatus que tenia el usuario antes de que se desconectara
	          
	                Presence presence = new Presence(Presence.Type.available); 
	 				 presence.setStatus(LoginActivity.pref.getString("state", "Online"));
	 				 connection.sendPacket(presence);
	 			
	 				Log.d("Connect",  "Logged in as: " + connection.getUser());
	 				
	 				 //Este handler nos muestra de manera visual cuando la aplicacion se esta reconectando
	 				 mHandler.post(new Runnable() {
	 	                public void run() {
	 	                	
	 	                	String state = new String();
	 	        			state = "Online";
	 	        			
	 	        			
	 	        			
	 	              if(ChatListActivity.isRunning == true){
	 	            	  String ChatList = "Chat List";
	 	                		Log.d("Connect RS","ChatListActivity is running");
	 	                		ChatListActivity.updateGlobal(ChatList);
	 	                		
	 	        			}  	
	 	                	
	 	                	if(ChatEntryActivity.isRunning == true && roster.getPresence(ChatEntryActivity.remoteUsername).getStatus() != null ){
	 	                		Log.d("Connect RS","ChatEntryActivity is running");
	 	        				state = roster.getPresence(ChatEntryActivity.remoteUsername).getStatus().toString();
	 	                		ChatEntryActivity.updateGlobal(state);	

	 	        			}
	 	                	
	 	                	if(ChatEntryActivity.isRunning == true && roster.getPresence(ChatEntryActivity.remoteUsername).getStatus() == null ){
	 	                		Log.d("Connect RS","ChatEntryActivity is running");
	 	                		state = "Offline";
	 	                		ChatEntryActivity.updateGlobal(state);

	 	        			}
	 	                	
	 	                	if(StatusActivity.isRunning == true){
	 	                		Log.d("Connect RS","StatusActivity is running");
		                		StatusActivity.updateStatus(LoginActivity.pref.getString("state", "Online"));
		                		
		        			}
	 	                }
	 	                	
	 	              });

	            }

	            @Override
	            public void reconnectionFailed(Exception arg0) {
	                Log.i("RecoverConnection","Failed to reconnect to the XMPP server.");
	                connectionStatus = false;
	            }

	            @Override
	            public void reconnectingIn(int seconds) {
	            	
	                Log.i("Reconnection","Reconnecting in " + seconds + " seconds.");
	                if(disconnect == true){
	                		connection.removeConnectionListener(cl);
	                		Log.i("Reconnection","im in!" );
	                		
	                	}
	        			
	            }

	            @Override
	            public void connectionClosedOnError(Exception arg0) {
	                Log.i("Reconnection","Connection to XMPP server was lost.");
	                connectionStatus = false;
	    			
	    			 mHandler.post(new Runnable() {
		                public void run() {
		                	String state = new String();
		        			state = "Reconnecting...";
		        			
		                	if(ChatEntryActivity.isRunning == true){
		        				
		            			ChatEntryActivity.updateGlobal(state);
		        			}
		                	
		                	if(ChatListActivity.isRunning == true){
		        				
		                		ChatListActivity.updateGlobal(state);
		        			}
		                	
		                	if(StatusActivity.isRunning == true){
		        				
		                		StatusActivity.updateStatus(state);
		        			}
		                }
		                	
		              });
	    			
	            }

	            @Override
	            public void connectionClosed() {
	                Log.i("Reconnection","XMPP connection was closed.");
	                connectionStatus = false;

	            }
	        });
			
		}
		
         
        
	}
	
	
	public void setFileTransferListener(){
		
		ProviderManager.getInstance().addIQProvider("query","http://jabber.org/protocol/bytestreams", new BytestreamsProvider());
		ProviderManager.getInstance().addIQProvider("query","http://jabber.org/protocol/disco#items", new DiscoverItemsProvider());
		ProviderManager.getInstance().addIQProvider("query","http://jabber.org/protocol/disco#info", new DiscoverInfoProvider());
		
		FileTransferManager manager = new FileTransferManager(connection);
		manager.addFileTransferListener(new FileTransferListener() {
		   public void fileTransferRequest(final FileTransferRequest request) {
		      new Thread(){
		         @Override
		         public void run() {
		            IncomingFileTransfer transfer = request.accept();
		            File mf = Environment.getExternalStorageDirectory();
		            File file = new File(mf.getAbsoluteFile()+"/DCIM/Camera/" + transfer.getFileName());
		            try{
		                transfer.recieveFile(file);
		                while(!transfer.isDone()) {
		                   try{
		                      Thread.sleep(1000L);
		                   }catch (Exception e) {
		                      Log.e("", e.getMessage());
		                   }
		                   if(transfer.getStatus().equals(Status.error)) {
		                      Log.e("ERROR!!! ", transfer.getError() + "");
		                   }
		                   if(transfer.getException() != null) {
		                      transfer.getException().printStackTrace();
		                   }
		                }
		             }catch (Exception e) {
		                Log.e("", e.getMessage());
		            }
		         };
		       }.start();
		    }
		 });
	}
	
	public void fileTransfer(String filenameWithPath, String contact){
		
		FileTransferManager manager = new FileTransferManager(connection);
		OutgoingFileTransfer transfer = manager.createOutgoingFileTransfer(contact+"/Smack");
		File file = new File(filenameWithPath);
		try {
		   transfer.sendFile(file, "test_file");
		} catch (XMPPException e) {
		   e.printStackTrace();
		}
		while(!transfer.isDone()) {
		   if(transfer.getStatus().equals(Status.error)) {
		      Log.d("FileTransfer","ERROR!!! " + transfer.getError());
		   } else if (transfer.getStatus().equals(Status.cancelled)
		                    || transfer.getStatus().equals(Status.refused)) {
		      Log.d("FileTransfer","Cancelled!!! " + transfer.getError());
		   }
		   try {
		      Thread.sleep(1000L);
		   } catch (InterruptedException e) {
		      e.printStackTrace();
		   }
		}
		if(transfer.getStatus().equals(Status.refused) || transfer.getStatus().equals(Status.error)
		 || transfer.getStatus().equals(Status.cancelled)){
		   Log.d("FileTransfer","refused cancelled error " + transfer.getError());
		} else {
		   Log.d("FileTransfer","Success");
		}
	}
	
//Metodo que nos permite tener actualizaciones de tipo presencia
public void setPresence(final XMPPConnection connection) {
	
    if (connection != null) {
      // Add a packet listener to get precenses sent to us
    	 PacketFilter presenceFilter = new PacketTypeFilter(Presence.class); 
    	 connection.addPacketListener(new PacketListener(){ 
    	  @Override
    	  public void processPacket(Packet packet) {
                 final Presence presence = (Presence)packet;
                 //estos son datos del packete de presencia que llega
                 
                 Log.d("Privacy","Presence packet came From: " + presence.getFrom() + ", To: "+presence.getTo()+", Estatus: "+presence.getStatus());
                
                 if(ChatEntryActivity.isRunning == true){
                 mHandler.post(new Runnable() {
 	                public void run() {
 	                		
 	                	ChatEntryActivity.updateStatus(presence.getFrom(), presence);
 	                	   	                	
 	                }
 	              });
                 }
                 
                 
                 
                 if (presence.getType() == Presence.Type.available) {
                      Log.d("Privacy","User: " + presence.getFrom() + ". User is Online");//Cuando esta conectado
                 }
                 else if (presence.getType() == Presence.Type.unavailable) {
                      Log.d("Privacy","User: " + presence.getFrom() + ". User is Offline");//Cuando no esta conectado
                 }
                 
                 if (presence.getType() == Presence.Type.subscribe){
               	  Log.d("Privacy","User: " + presence.getFrom() + ". Permission to subscribe has arrived");
               	  Presence subscribed = new Presence (Presence.Type.subscribed);
               	  subscribed.setTo(presence.getFrom());
               	  connection.sendPacket(subscribed);
               	  Log.d("Privacy","User: " + presence.getFrom() + ".subscribed has been sent");
               	  userExist = false;
               	  roster = connection.getRoster();
               	  if(roster.getEntry(presence.getFrom())==null){
               		userExist = false;  
               	  }else{
               		  userExist = true;
               	  }
                  
                  if(userExist == false){
                	  Presence subscribe = new Presence (Presence.Type.subscribe);
                   	  subscribe.setTo(presence.getFrom());
                   	  connection.sendPacket(subscribe);
                      Log.d("Privacy","User: " + presence.getFrom() + ".subscribe has been sent");
                  }             	
               	  
                 }
                 
                 if (presence.getType() == Presence.Type.subscribed) {
                     Log.d("Privacy","User: " + presence.getFrom() +  ". Acknowlege subscribed has arrived");
                     
                     /*mHandler.post(new Runnable() {
      	                public void run() {
      	                	if(AddContactActivity.isRunning == true){
      	                		AddContactActivity.AddUserUpdate(presence.getFrom().toString());
      	                		
      	                	} 	                	
      	                }
      	              });*/
                     
                     
                }
                 
                 if (presence.getType() == Presence.Type.unsubscribe) {
                	 Log.d("Privacy","User: " + presence.getFrom() +  ". unsubscribe has arrived");

                 }
             }
         }, presenceFilter);
  }
}

/*Contador de mesajes sin leer por cada contacto que se muestra en el ChatListActivity */	
public void addMsjCounter(String contact, boolean priva){
	boolean existContacto=false;

	CounterMsj NuevaEntrada;
	
	if(counterMsjContact.size()==0){
		NuevaEntrada = new CounterMsj(contact, 1, priva);
		counterMsjContact.add(NuevaEntrada);
	}else{
		
		for(int i = 0;i<counterMsjContact.size();i++){			
			if((counterMsjContact.get(i).getContact().equals(contact)) && (counterMsjContact.get(i).isPrivate() == priva)){
				existContacto=true;
				counterMsjContact.get(i).setCount(counterMsjContact.get(i).getCount()+1);
				Log.d("Contador",String.valueOf(counterMsjContact.get(i).getCount()));
			}
		}
		
		if(existContacto==false){
			NuevaEntrada = new CounterMsj(contact, 1, priva); //aqui se repite nuevamente lo mismo de arriva
			counterMsjContact.add(NuevaEntrada);
		}
			
	}
}

/*Retorna el contador del contacto recibido*/
public static int MsjCounter(String contact, boolean priva){
    		
	for(int i = 0;i<counterMsjContact.size();i++){			
		if((counterMsjContact.get(i).getContact().equals(contact)) && (counterMsjContact.get(i).isPrivate() == priva)){
			return counterMsjContact.get(i).getCount();
		}  
	}	
		return 0;
}


/*Este metodo reinicia el contador de mesajes sin leer que se muestra en el ChatListActivity, si 
 * se le envia true reiniciara la lista completa y con false solo del contacto que reciba*/
public void ClearMsjCounter(String contact, boolean priva)
{
	if(counterMsjContact.size() != 0){
	
		for(int i = 0;i<counterMsjContact.size();i++){			
			if((counterMsjContact.get(i).getContact().equals(contact)) && (counterMsjContact.get(i).isPrivate() == priva)){
				counterMsjContact.get(i).setCount(0);
			}
		}		
	}
}


//Esta funcion permite ver los mensaje entrantes, los imprime en el logcat y en su respectiva ventana.
	public void setConnection(XMPPConnection connection) {
		
	    if (connection != null) {
	      // Add a packet listener to get messages sent to us
	      PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
	      connection.addPacketListener(new PacketListener() {
	        @Override
	        public void processPacket(Packet packet) {
	          message = (Message) packet;
	          
	          SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	          actualTime = sdf.format(new Date());
	          
	          DelayInformation inf = null;
	          try {
	              inf = (DelayInformation)packet.getExtension("x","jabber:x:delay");
	          } catch (Exception e) {
	              e.getStackTrace();
	          }
	          // get offline message timestamp
	          if(inf!=null){
	        	  Date date = inf.getStamp();
	        	  SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        	  String offlineTimeStamp = formatter.format(date);
	        	  actualTime = offlineTimeStamp;
	              Log.d("Connect","offlineTimeStamp: "+ offlineTimeStamp);
	          }
	          
	          if (message.getBody() != null) {
	            fromName = StringUtils.parseBareAddress(message.getFrom());
	            Log.i("Connect ", " Text Recieved: " + message.getBody() + " from: " +  fromName);
	            mHandler.post(new Runnable() {
	                public void run() {
	                	
	                	String privacy = message.getBody().substring(message.getBody().length() - 3, message.getBody().length() - 2);
	                	Log.d("Connect","Privacy: "+ privacy);
	                	boolean priva = false;
	                	if(privacy.equals("p")){
	                		priva = true;
	                	}
	                	int countDown = Integer.valueOf(message.getBody().substring(message.getBody().length() - 2, message.getBody().length()));
	                	String ChatMessage = message.getBody().substring(0, message.getBody().length() - 3); // esto es para que no muestre el countDown
	                	
	                	
	                	if(privacy.equals("d")){
	                		//Buscar en la base de datos interna para borrar el mensaje.
	                		int ID = 0;
	                		DatabaseHandler db = new DatabaseHandler(context);
							List<MessageDB> messages = db.getAllMessages(); 
							for (MessageDB cn : messages) {
								if((cn.getToJid().equals(LoginActivity.pref.getString("username", "default")+"@localhost") && cn.getFromJid().equals(fromName) || cn.getToJid().equals(fromName) && cn.getFromJid().equals(LoginActivity.pref.getString("username", "default")+"@localhost")) && cn.getBody().substring(0, cn.getBody().length() - 3).equals(ChatMessage)){
									ID = cn.getID();
									db.deleteMessage(new MessageDB(ID, "delete", "delete", "delete", "delete", false));
									db.close();
									if(ChatEntryActivity.isRunning && ChatEntryActivity.remoteUsername.equals(fromName)){
										ChatEntryActivity.deleteMessage(ChatMessage);
									}
									if(ChatListActivity.isRunning){
										ChatListActivity.deleteMessageUpdate(fromName, ChatMessage);
									}
								}
					        }
	                	}else{
	                	DatabaseHandler db = new DatabaseHandler(context);
						
						db.addMessage(new MessageDB(fromName, LoginActivity.pref.getString("username", "default")+"@localhost", actualTime, message.getBody(), priva));
						db.close();
						
						DatabaseHandler ndb = new DatabaseHandler(context);
						List<MessageDB> messages = ndb.getAllMessages(); 
						int ID = 0;
						for (MessageDB cn : messages) {
							ID = cn.getID();
							Log.d("Connect","ID: "+ ID);
				        }
						Log.d("Connect","ID Last ID: "+ ID);
						ndb.close();
						
						//States st = new States("default", false);//inicializando
						boolean good = false;
						
						DatabaseHandler database = new DatabaseHandler(context);
						List<Contact> contacts = database.getAllContacts(); 
						database.close();
						for (Contact cn : contacts) {
							Log.d("Connect"," FOR1 User: "+ cn.getUsuario() + " Contact: "+ cn.getContacto() +" State: "+cn.getState());
							if((cn.getUsuario().equals(LoginActivity.pref.getString("username", "default")+"@localhost")) && (cn.getContacto().equals(fromName))){
								//st = new States(cn.getID(), cn.getContacto(), cn.getState());
								good = true;
								Log.d("Connect","INNNNNNN");
							}
						}
						
						if(good/*st.isSelected() == true*/){
							Log.d("Connect","INNNNNNN2");
						 boolean Exist = false;
						    
						    DatabaseHandler nndb = new DatabaseHandler(context);
							List<ChatContact> chatContacts = nndb.getAllChatContacts(); 
							for (ChatContact cn : chatContacts) {
								Log.d("Connect","FOR2 User: "+ cn.getUser() + " Contact: "+ cn.getChatContact() + " Privacy: " + priva);
								if((cn.getChatContact().equals(fromName)) && (cn.getUser().equals(LoginActivity.pref.getString("username", "default")+"@localhost") && (cn.isPrivate() == priva))){
									Exist = true;
									Log.d("Connect","User EXIST: " + priva);
								}
							}
							
							if(Exist == false || chatContacts.isEmpty()){//Esto es lo que hay que arreglar
								nndb.addChatContact(new ChatContact(LoginActivity.pref.getString("username", "default")+"@localhost", fromName, priva));
								Log.d("Connect","User Added to chatcontacs: " + priva);
							}
							
							nndb.close();
						}

						if(ChatListActivity.isRunning == true){
							ChatListActivity.updateChatList(fromName, priva);	
	                	}

						if(!(fromName.equals(ChatEntryActivity.remoteUsername))){
							addMsjCounter(fromName, priva);
							ChatListActivity.updateCounter(fromName, priva);
	                	} 

	                	if(ChatEntryActivity.isRunning == true){
	                		
	                		if(countDown != 0){
	                			Log.d("ChatEntryActivity", "CountDown: "+ countDown);
	                			ChatEntryActivity.addItems(fromName, "Priva Message", countDown, ID, actualTime, priva);
	                			
	                		}else{
	                			ChatEntryActivity.addItems(fromName, ChatMessage, countDown, ID, actualTime, priva);
	                		}
	                	}

	                	if(ChatListActivity.isRunning == true){
	                		ChatListActivity.updateList(fromName, message.getBody(), priva);		
	                	}

	                	/*//Sub-sistema encargado de las notificaciones.
	                	if(!(fromName.equals(ChatEntryActivity.remoteUsername)) && (existInContactList(fromName))){
	                		Log.d("Connect","Notification process started");
	                		_Notification(message.getBody(), fromName);
	        	        }*/

	                	}
	                }
	              });
	          }
	        }
	      }, filter);
	    }
	  }
	
	/*public void _Notification(String Mjs, String Contacto){
		
		int countDown = Integer.valueOf(Mjs.substring(Mjs.length() - 2, Mjs.length()));
		Mjs = Mjs.substring(0, Mjs.length() - 2);
    	
    	if(countDown > 0 ){
    		Mjs = "Priva Message";
    	}
		
		
		++countMsj;	//Contador de mensajes para la notificacion	
		Intent resultIntent; //Intent que es llamado por la notificacion
		
		//Reinicio de los contadores mensaje y conversaciones - 3 Etapa
		if(notificacionLeida==true){
			countMsj=1;
			notificacionLeida=false;
			notiConver.clear();
		}
		
		//Contador de las conversaciones, atravez de array que guarda y no permite que se repita el fromName de los mensajes recibidos.
		if(countMsj==1){
			notiConver.add(Contacto);
		}else{
			Log.d("notiCountConver 1",String.valueOf(countMsj));
			
			for(int i = 0;i<notiConver.size();i++){
				Log.d("notiCountConver 2",String.valueOf(notiConver.size())+" "+String.valueOf(i));
				Log.d("notiCountConver 2.5",String.valueOf(notiConver.size())+" "+notiConver.get(i).toString()+" "+Contacto);
				if(notiConver.get(i).toString().equals(Contacto)){
					Log.d("notiCountConver 3","Esta en la lista y no se agrega");
					exitList=true;
				}
			}
			
			
			if(exitList==false){
				notiConver.add(Contacto);
				Log.d("notiCountConver 4","add list"+Contacto);
			}else{
				exitList=false;
			}
		}
		
		
		//Establece el formato de la notificacion cuando solo hay una converacion
		if(countMsj>1){
			Log.d("Privacy",String.valueOf(notiConver.size()));
			cadenMsj=String.valueOf(countMsj-1)+" more messages";
			notiMsj=Contacto.substring(0, Contacto.length()-10) + ": " + Mjs;
		}else{
			cadenMsj="";
			notiMsj=Contacto.substring(0, Contacto.length()-10) + ": " + Mjs;
		}
		
		//Establece la accion del intent y cuando hay mas de una conversacion
		if(notiConver.size()>1){
			notiMsj=String.valueOf(countMsj)+" messages / " + String.valueOf(notiConver.size() + " conversations");
			cadenMsj="";
			resultIntent = new Intent(context, ChatListActivity.class);
		}else{
	    	resultIntent = new Intent(context, ChatEntryActivity.class);
	    	resultIntent.putExtra("remoteUsername", Contacto);
		}
		
		//Mesanje que se muestra en la barra superior cuando llega la notificacion
		CharSequence cs = Contacto.substring(0, Contacto.length()-10)+": "+Mjs;
		
	        		//Creación de la notificación según los parametros establecidos en los condicionales.
	            	 mBuilder = new NotificationCompat.Builder(context)
	       		     .setSmallIcon(R.drawable.ic_launcher)
	       		     .setTicker(cs)
	       		     .setContentInfo(cadenMsj)
	       		     .setContentTitle("Priva Messeger")
	       		     .setContentText(notiMsj);
	            	
	            	//Establece que atravez del intent -> la activity -> Connect, que la notificacion se ha leido
	            	//asi la primera condicional en el sub-sistema se cumpla y reinicie los contadores.
	            	
	            	// 1 Etapa
	            	resultIntent.putExtra("notiLeido", true);
	            	
	           		   PendingIntent resultPendingIntent =
	           				     PendingIntent.getActivity(
	           				     context,
	           				     0,
	           				     resultIntent,
	           				     PendingIntent.FLAG_UPDATE_CURRENT
	           				 );

	           		   mBuilder.setContentIntent(resultPendingIntent); 
	           		   
	           		   mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	           			 mNotifyMgr.notify(1, mBuilder.build());
	}

	public void ClearNotification(){
		if(mNotifyMgr != null){
			mNotifyMgr.cancel(1);
		}
	}
	
	//Este metodo te indica si el contacto enviado existe en la lista de contactos.
	public boolean existInContactList(String Contacto, boolean priva){
		Log.d("Connect","Counter, looking if contact exists");
		if(!(contactList.size()==0)){
			for(int i=0; i < contactList.size(); i++){
				if((contactList.get(i).getName().equals(Contacto))&&(contactList.get(i).isSelected() == priva)){
					Log.d("Connect","Counter, contact exists");
					return true;
				}
			}
		}
		Log.d("Connect","Counter, contact does not exists");
	return false;
	}*/
	
	//Este metodo permite agregar contactos
	public void AddContact(String addUser){
	
		try{
			toast = null;//Este null sirve para que cuando se agreguen 2 usuarios consecutivos se pueda diferenciar si existe o no.
			userExist = false;//Todo este codigo se utiliza para ver si el contacto existe en el contexto global.
			UserSearchManager search = new UserSearchManager(connection);  
            Form searchForm = search.getSearchForm("search."+connection.getServiceName());
            Form answerForm = searchForm.createAnswerForm();  
            answerForm.setAnswer("Username", true);  
            answerForm.setAnswer("search", addUser);  
            org.jivesoftware.smackx.ReportedData data = search.getSearchResults(answerForm,"search."+connection.getServiceName());  
        if(data.getRows() != null)
            {
                Iterator<Row> it = data.getRows();
                while(it.hasNext())
                {
                    Row row = it.next();
                    Iterator iterator = row.getValues("jid");
                    if(iterator.hasNext())
                    {
                        String value = iterator.next().toString();
                        Log.i("Iteartor values......"," "+value);
                        
                        if(value.equals(addUser + "@localhost")){//Este if lo utilizamos para asegurarnos de que la busqueda sea correcta y cuando se busque ju no encuentre a juan
                        	Log.d("Privacy",value+" = "+addUser+"@localhost");
                        	userExist = true;//aqui afirmamos que el contacto existe dentro del servidor	
                        }   
                    }  
                }                
            }
        if(userExist == false){
        	
        	Log.i("Privacy","El usuario: " + addUser + " no existe");
        	toast = "The user: '" + addUser + "' does not exist";
        	
        }
        else{
			 roster = connection.getRoster();
             Collection<RosterEntry> entries = roster.getEntries();
             userExist = false;
             for (RosterEntry entry : entries) {
               if(entry.getUser().equals(addUser + "@localhost")){
           			userExist=true;
                	Log.i("Privacy","User: " + addUser + " already exist");
                	toast = "The user: '" + addUser + "' already exist";
                	
                }
             }
             if(userExist == false){
            	 
            	 InsertRoster(addUser+"@localhost");
            	 roster.createEntry(addUser + "@localhost", addUser, null); //Lo que esta en null es para saber si pertenece a un grupo
				 Log.d("AddCOntactDB","Add conctact successfull");
             }
			
        }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d("Privacy","User'"+addUser+ "'was unable to add");
		}
	
	}
	
	//Este metodo devuelve el toast respectivo a la clase NuevoConctacto
    public String getToast(){
    	return toast;
    }
    
    public XMPPConnection getConnection(){
    	return connection;
    }
	//Aqui hacemos que la aplicacion se desconecte 
	public void Disconect(){
		try {
			Log.d("Connect","Disconect has been called");
			if((connection != null) && (connection.isConnected() == true)){
				connection.disconnect();
			}
			//ClearNotification();
			disconnect = true;
			if(this.timer != null){
				this.timer.cancel();
			}
			

		} catch (Exception e) {
			Log.d("Connect","No fue posible desconectar la APP.");
			e.printStackTrace();
		}
	}


	//Metodo que nos permite enviar mensajes
	public void ChatMessage(String remoteUsername, String textMessage) {	
		try {
			Chat chat = connection.getChatManager().createChat(remoteUsername  , new MessageListener() {//+ "@localhost"
				@Override
				public void processMessage(Chat chat, Message message) {
					System.out.println("Received message: " + message);
				}
         	});
         	chat.sendMessage(textMessage);
		}
        catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	    }
	}


	//En este paso buscamos todas las entradas de nuestra lista de contactos
	public void Contacts(States states) {
		//Esta parte se puede mejorar en caso de que no haya conexion
		Log.d("ConnectContact","user: "+states.getName()+" Boolean: "+states.isSelected());
		if(states.selected == true){
			contactList.add(states);
		}else{
			ArrayList<States> tempList = new ArrayList<States>();
			for(int i = 0;i<contactList.size(); i ++){
				if(!(contactList.get(i).getName().equals(states.getName()))){
					tempList.add(contactList.get(i));
				}
	
			}
			
			contactList.clear();
			contactList = tempList;

		}
		for(int i = 0; i<contactList.size(); i ++){
			Log.d("ConnectContactsFor","Lista user: "+contactList.get(i).getName()+" Boolean: "+contactList.get(i).isSelected());
		}
		
	}
	
	//Este metodo sirve para borrar un contacto de la lista de roster. Se borra el contacto de ambas listas.
	public void DeleteContact(String deleteUser){
		
		try{
			RosterPacket packet = new RosterPacket();
			packet.setType(IQ.Type.SET);
			RosterPacket.Item item  = new RosterPacket.Item(deleteUser, null);
			item.setItemType(RosterPacket.ItemType.remove);
			packet.addRosterItem(item);
			connection.sendPacket(packet);
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d("Privacy","User was unable to delete");
	    }
	}

	public ArrayList<States> getContacts(){
		return contactList;
	}
	
	//public void clearList(){
	//	contactList.clear();
	//}
	
	//Este metodo actualmente no se usa
	public ArrayList<OneComment> getHist( String userA, String userB){
		history.clear();
		DBuserA = userA;//+"@localhost"
		DBuserB = userB;//+"@localhost";
		Log.d("privacy",DBuserA);
		Log.d("privacy",DBuserB);
		OneComment Msj;

		
		try{
			
			Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
			DriverManager.setLoginTimeout(0);
			
			dbconn = DriverManager.getConnection("jdbc:oracle:thin:@"+HOST+":"+DBPORT+":"+DBNAME, DBUSERNAME, DBPASSWORD);
			Statement st = dbconn.createStatement();
			
			ResultSet resultado = st.executeQuery("select * from MessageList where (fromjid = '"+DBuserB+"' and tojid = '"+DBuserA+"') or (fromjid = '"+DBuserA+"' and tojid = '"+DBuserB+"') ORDER BY ID ASC"); //ResultSet resultado = st.executeQuery("select * from ofID");  ooo "select * from ofmessagearchive"
			
			if (resultado==null){
				Log.d("Resul", "Null");
			}
			
			
			while (resultado.next()){
				if(resultado.getString(1).equals(DBuserB)){
				     Msj = new OneComment(false,resultado.getString(4));
				     history.add(Msj);
				     Log.d("DB", Msj.comment);
				    }else{
				     Msj = new OneComment(true,resultado.getString(4));
				     history.add(Msj);
				     Log.d("DB", Msj.comment);
				    }
			}
					
			
			Log.d("Connect", "Getting message history success");
			st.close();
			dbconn.close();
			
			}catch(Exception e){
			Log.d("Connect", "Getting message history failed");
			e.printStackTrace();
			}
			
		return history;

	}
	
	public void SyncHistory(){
		try{
			Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
			DriverManager.setLoginTimeout(0);
			
			dbconn = DriverManager.getConnection("jdbc:oracle:thin:@"+HOST+":"+DBPORT+":"+DBNAME, DBUSERNAME, DBPASSWORD);
			Statement st = dbconn.createStatement();
			
			ResultSet resultado = st.executeQuery("select * from MessageList where fromjid = '"+LoginActivity.pref.getString("username", "default")+"@localhost"+"' or tojid = '"+LoginActivity.pref.getString("username", "default")+"@localhost"+"' ORDER BY ID ASC");
			
			if (resultado==null){
				Log.d("Resul", "Null");
			}
			
			while (resultado.next()){
				Log.d("Connect",resultado.getString(1)+resultado.getString(2)+resultado.getString(3)+resultado.getString(4));
				DatabaseHandler db = new DatabaseHandler(context);
				db.addMessage(new MessageDB(resultado.getString(1), resultado.getString(2),resultado.getString(3),resultado.getString(4), false));
				db.close();
			}
					
			
			Log.d("Connect", "Sync message history success");
			st.close();
			dbconn.close();
			
			}catch(Exception e){
			Log.d("Connect", "Sync message history failed");
			e.printStackTrace();
			}
	}
	
	public ArrayList<String> DBUpdateContactList(String DBuserA, String DBuserB ){
		
		String lastMessage = new String();
		String lastMessageDate = new String();
		lastMessage = " ";
		lastMessageDate = " ";
		ArrayList<String> Data = new ArrayList<String>();

		ArrayList<MessageDB> userMessages = new ArrayList<MessageDB>();
			
		DatabaseHandler db = new DatabaseHandler(context);
		List<MessageDB> messages = db.getAllNonPrivateMessages();
		db.close();
		for (MessageDB cn : messages) {
	        	
			if((cn.getFromJid().equals(DBuserA)) && (cn.getToJid().equals(DBuserB)) || (cn.getFromJid().equals(DBuserB)) && (cn.getToJid().equals(DBuserA))){
				userMessages.add(cn);
				//Log.d("Connect","FromJid: "+cn.getFromJid()+". ToJid: "+cn.getToJid()+". Body: "+cn.getBody());
			}
	    }
			
		Log.d("Connect","remote: "+DBuserA+"  local: "+LoginActivity.pref.getString("username", "default")+"@localhost");
		if(!(userMessages.isEmpty())){
			String sender = userMessages.get(userMessages.size()-1).getFromJid();
			lastMessage = userMessages.get(userMessages.size()-1).getBody().toString();
			lastMessageDate = userMessages.get(userMessages.size()-1).getSentDate().toString();
			
			String countDown = lastMessage.substring(lastMessage.length() - 2, lastMessage.length());
			if((countDown.equals("99")) && !(sender.equals(DBuserB))){
				lastMessage = userMessages.get(userMessages.size()-2).getBody().toString();
				lastMessageDate = userMessages.get(userMessages.size()-2).getSentDate().toString();
			}
		}
			
		Log.d("Connect","LastMessage: "+lastMessage);
		Log.d("Connect","LastMessageDate: "+lastMessageDate);

		Data.add(0,lastMessage);
		Data.add(1,lastMessageDate);
		return Data;
	}
	
	
	public void DBInsertMessage(String fromjid, String tojid, String sentdate, String body ){ 
		int countDown = Integer.valueOf(body.substring(body.length() - 2, body.length()));
		if(countDown > 0 && countDown < 60){
			Log.d("Connect", "Message was not inserted to External DB because of Privacy");
		}else {
			try{			 
				Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
				DriverManager.setLoginTimeout(0);
				dbconn = DriverManager.getConnection("jdbc:oracle:thin:@"+HOST+":"+DBPORT+":"+DBNAME, DBUSERNAME, DBPASSWORD);
				Statement st = dbconn.createStatement();
	      
				st = dbconn.createStatement();
	    
				st.execute("INSERT INTO MessageList VALUES ('"+fromjid+"','"+tojid+"','"+sentdate+"','"+body+"',seq_message.nextval)");
         
	              
				Log.d("Connect", "A message has been inserted to the remote DB");
				st.close();
				dbconn.close();
	    
			}catch(Exception e){
				Log.d("Connect", "Failed to insert a message to the remote DB");
				e.printStackTrace();
			}
		}
	 }
	
public void DBDeleteMessage(String date){
		
		String localuser = LoginActivity.pref.getString("username", "default")+"@localhost";
		try{			 
			Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
			DriverManager.setLoginTimeout(0);
			dbconn = DriverManager.getConnection("jdbc:oracle:thin:@"+HOST+":"+DBPORT+":"+DBNAME, DBUSERNAME, DBPASSWORD);
			Statement st = dbconn.createStatement();
      
			st = dbconn.createStatement();
    
			st.execute("DELETE FROM MessageList WHERE SENTDATE = '"+date+"' AND (FROMJID = '"+localuser+"' OR TOJID = '"+localuser+"')");
     
			Log.d("Connect", "A message has been from the remote DB");
			st.close();
			dbconn.close();
    
		}catch(Exception e){
			Log.d("Connect", "Failed to delete message from the remote DB");
			e.printStackTrace();
		}
	}
	
	
	public void DBInsertContact(String userA, boolean state){
		
		int finalState;
		
		if(state){
			finalState = 1;
		}else{
			finalState = 0;
		}
		
	   try{
	    
	    Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
	    DriverManager.setLoginTimeout(0);
	    dbconn = DriverManager.getConnection("jdbc:oracle:thin:@"+HOST+":"+DBPORT+":"+DBNAME, DBUSERNAME, DBPASSWORD);
	    Statement st = dbconn.createStatement();
	      
	    st = dbconn.createStatement();
	     
	    st.execute("INSERT INTO ListContact VALUES ('"+LoginActivity.pref.getString("username", "default")+"@localhost"+"','"+userA+"', '"+finalState+"')");
                   
	    Log.d("Connect", "User"+userA+" has been added to the remote DB");
	    st.close();
	    dbconn.close();
	    
	    }catch(Exception e){
	    Log.d("Connect", "User has fail to add to the remote DB");
	    e.printStackTrace();
	    }
	   
	 }
	
	public void DBUpdateContact(String username, boolean state){
		
		int finalState;
		
		if(state){
			finalState = 1;
		}else{
			finalState = 0;
		}
		
		try{
			Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
			DriverManager.setLoginTimeout(0);
			dbconn = DriverManager.getConnection("jdbc:oracle:thin:@"+HOST+":"+DBPORT+":"+DBNAME, DBUSERNAME, DBPASSWORD);
			Statement st = dbconn.createStatement();
      
			ResultSet resultado = st.executeQuery("select * from ListContact where usuario='"+LoginActivity.pref.getString("username", "default")+"@localhost"+"' AND CONTACTO = '"+username+"'");
			
			int i = 0;
			while(resultado.next()){
				i++;
			}
			
			Log.d("Connect", "i size: "+i);
			
			if(i >= 1){
				st.execute("UPDATE LISTCONTACT SET STATE = '"+finalState+"' WHERE (USUARIO = '"+LoginActivity.pref.getString("username", "default")+"@localhost"+"' and CONTACTO = '"+username+"')");
			}else{
				st.execute("INSERT INTO LISTCONTACT VALUES ('"+LoginActivity.pref.getString("username", "default")+"@localhost"+"','"+username+"','"+finalState+"')");
			}
			
		    st.close();
			dbconn.close();
    
    		}catch(Exception e){
    			Log.d("Connect", "Fail :( User Upste COntact List Failed");
    			e.printStackTrace();
    		}
	}
	
	public void DBDeleteContact(String userA){
	   
	   try{
	    
	    Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
	    DriverManager.setLoginTimeout(0);
	    dbconn = DriverManager.getConnection("jdbc:oracle:thin:@"+HOST+":"+DBPORT+":"+DBNAME, DBUSERNAME, DBPASSWORD);
	    Statement st = dbconn.createStatement();
	      
	    st = dbconn.createStatement();

	    st.execute("DELETE FROM ListContact WHERE usuario = '"+LoginActivity.pref.getString("username", "default")+"@localhost"+"'and contacto = '"+userA+"'");
                   
	    Log.d("Connect", "User"+userA+" has been deleted from the remote DB");
	    
	    st.close();
	    dbconn.close();
	    
	    }catch(Exception e){
	    Log.d("Connect", "Deleting user from remote DB fail");
	    e.printStackTrace();
	    }
	   
	 }
	
	public void SyncChats(ArrayList<States> chatList){
		Log.d("Connect","Syncing Chats");
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
			DriverManager.setLoginTimeout(0);
			dbconn = DriverManager.getConnection("jdbc:oracle:thin:@"+HOST+":"+DBPORT+":"+DBNAME, DBUSERNAME, DBPASSWORD);
			Statement st = dbconn.createStatement();
			
			st.executeQuery("DELETE FROM CHATLIST WHERE USERNAME='"+LoginActivity.pref.getString("username", "default")+"@localhost"+"'");
			st.close();
			
			Statement statement = dbconn.createStatement();
			
			for(int j = 0; j < chatList.size(); j++){
				int temp;
				temp = chatList.get(j).isSelected()? 1 : 0;
				statement.execute("INSERT INTO CHATLIST VALUES (chat_sequence.nextval,'"+chatList.get(j).getName()+"','"+LoginActivity.pref.getString("username", "default")+"@localhost"+"','"+temp+"')");
			}
			statement.close();
			
			dbconn.close();
		} catch (Exception e) {
			// TODO: handle exception
			Log.d("Connect","Error Syncing Chats");
			e.getStackTrace();
		}
	}
	
	public void DBGetChats(){
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
			DriverManager.setLoginTimeout(0);
			dbconn = DriverManager.getConnection("jdbc:oracle:thin:@"+HOST+":"+DBPORT+":"+DBNAME, DBUSERNAME, DBPASSWORD);
			Statement st = dbconn.createStatement();
			
			ResultSet resultado = st.executeQuery("select * from CHATLIST where username='"+LoginActivity.pref.getString("username", "default")+"@localhost"+"' order by 1 ASC");
			
			DatabaseHandler db = new DatabaseHandler(this);
			List<ChatContact> chatContacts = db.getAllChatContacts(); 
			for (ChatContact cn : chatContacts) { 
				if((cn.getUser().equals(LoginActivity.pref.getString("username", "default")+"@localhost"))){
					db.deleteChatContact(new ChatContact(cn.getID(), LoginActivity.pref.getString("username", "default")+"@localhost", cn.getChatContact(), cn.isPrivate())); 
					Log.d("Connect DBGetChats","Contact: "+cn.getChatContact());
				}
			}
			
			while(resultado.next()){;
				int priv = resultado.getInt("PRIVATE");
				boolean finalPriv;
				if(priv == 1){
					finalPriv = true;
				}else{
					finalPriv = false;
				}
				db.addChatContact(new ChatContact(LoginActivity.pref.getString("username", "default")+"@localhost", resultado.getString(2), finalPriv));
			}
			
			db.close();	
			st.close();
			dbconn.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.getStackTrace();
		}
	}
	public void SyncContacs(){
		contactList.clear();	
		
		if(connectionStatus == false){
			DatabaseHandler db = new DatabaseHandler(context);
			List<Contact> contacts = db.getAllContacts();
			db.close();
			for (Contact cn : contacts) {
				if((cn.getUsuario().equals(LoginActivity.pref.getString("username", "default")+"@localhost") && cn.getState() == true)){
					States _state = new States(cn.getContacto().toString(), cn.getState());
					contactList.add(_state);
					Log.d("Connect","User added to the contactList from the local DB: " + _state.getName() + " State: " + _state.isSelected());
				}
			} 
		}else{
			try{
				Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
				DriverManager.setLoginTimeout(0);
				dbconn = DriverManager.getConnection("jdbc:oracle:thin:@"+HOST+":"+DBPORT+":"+DBNAME, DBUSERNAME, DBPASSWORD);
				Statement st = dbconn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
				
				ResultSet resultado = st.executeQuery("select * from ListContact where usuario='"+LoginActivity.pref.getString("username", "default")+"@localhost"+"'");
				
				DatabaseHandler dbDelete = new DatabaseHandler(context);
				List<Contact> dbcontacts = dbDelete.getAllContacts();
					
				for (Contact cn : dbcontacts) {
					if(cn.getUsuario().equals(LoginActivity.pref.getString("username", "default")+"@localhost")){
						dbDelete.deleteContact(new Contact(cn.getID(), "deleted", "deleted", false));
					}
				}
				dbDelete.close();
					
				DatabaseHandler dbAddContact = new DatabaseHandler(context);
				while(resultado.next()){
					String contact = resultado.getString("CONTACTO");
					int state = resultado.getInt("STATE");
					boolean finalState;
					if(state == 1){
						finalState = true;
					}else{
						finalState = false;
					}
					dbAddContact.addContact(new Contact(LoginActivity.pref.getString("username", "default")+"@localhost", contact, finalState));
				}
				
				dbAddContact.close();
				st.close();
				dbconn.close();
				
				}catch(Exception e){
				Log.d("Connect", "Adding user to the remote DB fail");
				e.printStackTrace();
				}
		}
		
		DatabaseHandler db = new DatabaseHandler(context);
		List<Contact> contacts = db.getAllContacts();
		db.close();
		for (Contact cn : contacts) {
			if((cn.getUsuario().equals(LoginActivity.pref.getString("username", "default")+"@localhost") && cn.getState() == true)){
				States _state = new States(cn.getContacto().toString(), cn.getState());
				contactList.add(_state);
				Log.d("Connect","User added FINAL: " + _state.getName() + " State: " + _state.isSelected());
			}
		} 
	}
	
	public boolean DBEmailExists(String userForLook){
		
		boolean exists = false;
		
		try{
	    
			Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
			DriverManager.setLoginTimeout(0);
			dbconn = DriverManager.getConnection("jdbc:oracle:thin:@"+HOST+":"+DBPORT+":"+DBNAME, DBUSERNAME, DBPASSWORD);
			Statement st = dbconn.createStatement();
	      
			st = dbconn.createStatement();

			ResultSet resultado = st.executeQuery("SELECT COUNT(email) FROM ofuser WHERE email ='"+userForLook+"'");
	              
			int i = 0;
	    
			while (resultado.next()){
				i = resultado.getInt("COUNT(email)");
			}
			Log.d("DBExternal", i+" user have this email: "+userForLook+"");
			st.close();
			dbconn.close();
	    
			if(i >= 1){
				return exists = true;
			}
	    
	    	}catch(Exception e){
	    		Log.d("ConectarDB", "Fail :(");
	    		e.printStackTrace();
	    	}
		return exists;
	 }
	
	public boolean RosterExist(String user){
		
		try{
	    
			Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
			DriverManager.setLoginTimeout(0);
			dbconn = DriverManager.getConnection("jdbc:oracle:thin:@"+HOST+":"+DBPORT+":"+DBNAME, DBUSERNAME, DBPASSWORD);
			Statement st = dbconn.createStatement();
	      
			st = dbconn.createStatement();

			ResultSet resultado = st.executeQuery("SELECT rosterUser FROM RosterList");
	              
			while (resultado.next()){
				if(resultado.getString(1).equals(user)){
					Log.d("Connect","Found user OJO: "+resultado.getString(1));
					return true;
				}
			}
			
			st.close();
			dbconn.close();
			
	    	}catch(Exception e){
	    		Log.d("Connect", "Fail :( RosterExist");
	    		e.printStackTrace();
	    	}
		Log.d("Connect","No me encontre en la lista de usuarios a no borrar ");
		return false;
	 }
	

	public void InsertRoster(String user){
	
		try{
    
			Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
			DriverManager.setLoginTimeout(0);
			dbconn = DriverManager.getConnection("jdbc:oracle:thin:@"+HOST+":"+DBPORT+":"+DBNAME, DBUSERNAME, DBPASSWORD);
			Statement st = dbconn.createStatement();
      
			st = dbconn.createStatement();

			st.execute("INSERT INTO RosterList VALUES ('"+user+"')");
              
			st.close();
			dbconn.close();
    
    		}catch(Exception e){
    			Log.d("Connect", "Fail :( InsertRoster");
    			e.printStackTrace();
    		}

	}
	
	public void RosterDelete(String user){
		
		try{
    
			Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
			DriverManager.setLoginTimeout(0);
			dbconn = DriverManager.getConnection("jdbc:oracle:thin:@"+HOST+":"+DBPORT+":"+DBNAME, DBUSERNAME, DBPASSWORD);
			Statement st = dbconn.createStatement();
      
			st = dbconn.createStatement();

			st.execute("DELETE FROM RosterList WHERE rosterUser = '"+LoginActivity.pref.getString("username", "default")+"@localhost"+"'");
              
		    st.close();
			dbconn.close();
    
    		}catch(Exception e){
    			Log.d("Connect", "Fail :( InsertRoster");
    			e.printStackTrace();
    		}

	}
	
	public String PasswordRecovery(String email){
		
		String username = new String();
		
		try{
    
			Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
			DriverManager.setLoginTimeout(0);
			dbconn = DriverManager.getConnection("jdbc:oracle:thin:@"+HOST+":"+DBPORT+":"+DBNAME, DBUSERNAME, DBPASSWORD);
			Statement st = dbconn.createStatement();
      
			st = dbconn.createStatement();

			//PreparedStatement ps = dbconn.prepareStatement("update ofuser set plainpassword = '' where username = '"+email+"' ");
			
			st.execute("UPDATE OFUSER SET PLAINPASSWORD = 'default', ENCRYPTEDPASSWORD = '' WHERE EMAIL='"+email+"'");

			ResultSet resultado = st.executeQuery("SELECT * FROM OFUSER");
			
			while (resultado.next()){
				Log.d("Connect","In WHILE change password process");
				if(resultado.getString(5).equals(email)){
					Log.d("Connect","In IF change password process");
					username = resultado.getString(1);
					Log.d("Connect","User: "+username+" change password process");
					break;
				}
			}
			
		    st.close();
			dbconn.close();
    
    		}catch(Exception e){
    			Log.d("Connect", "Fail :( PasswordRecovery");
    			e.printStackTrace();
    		}
		return username;

	}
	
	//Upload photo to the remote DB
		public void uploadImage(String imagePath, String date) throws Exception{
		
			try{
				Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
				DriverManager.setLoginTimeout(0);
				dbconn = DriverManager.getConnection("jdbc:oracle:thin:@"+HOST+":"+DBPORT+":"+DBNAME, DBUSERNAME, DBPASSWORD);

				dbconn.setAutoCommit(false); 
				
				int rows = 0;
				FileInputStream fin = null;
				OutputStream out = null;
			    ResultSet rs = null;
				Statement stmt = null;
				oracle.sql.BLOB photo = null;
			    stmt = dbconn.createStatement();

			    rows = stmt.executeUpdate("insert into imgtable(id, photo, username, lastupdate) values (seq_photo.nextval, empty_blob(), '"+LoginActivity.pref.getString("username", "default")+"@localhost"+"', '"+date+"' )");
			    Log.d("Prueba",rows + " rows inserted");

			    rs = stmt.executeQuery("select photo from  imgtable where username = '" + LoginActivity.pref.getString("username", "default")+"@localhost" + "' for update nowait");
			    rs.next();
			    photo = ((OracleResultSet) rs).getBLOB(1);
			     
			    fin = new FileInputStream(imagePath);
			    out = photo.getBinaryOutputStream();
			    // Get the optimal buffer size from the BLOB
			    byte[] buffer = new byte[photo.getBufferSize()];
			    int length = 0;
			    Log.d("Prubea","Uploading... ");
			    
			    while ((length = fin.read(buffer)) != -1) {
			    	
			        out.write(buffer, 0, length);
			    }

			    Log.d("Prubea","Picture Uploaded successfully");
			 
			    
			    out.close();
			    fin.close();
			    rs.close();
			    stmt.close();
			    dbconn.commit();
			    dbconn.close();
							
				}catch (Exception e) {
					Log.d("Connect", "Fail :( Uploading image");
					e.printStackTrace();
					throw e;
				}
		}

		//Download an Users image
		public void downloadImage(String lastupdate, String username){
			Log.d("Connect","downloadImage Started");
			try{
				Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
				DriverManager.setLoginTimeout(0);
				Connection con = DriverManager.getConnection(
				"jdbc:oracle:thin:@"+HOST+":"+DBPORT+":"+DBNAME, DBUSERNAME, DBPASSWORD);
				Statement st = con.createStatement();

				ResultSet resultado = st.executeQuery("SELECT * FROM IMGTABLE");
				
				while (resultado.next()){
					if((resultado.getString(3).equals(username)) && (resultado.getString(4).equals(lastupdate))){
						
						Log.d("Connect","Downloading Image...");
						Blob b = resultado.getBlob(2);
						barr = new byte[(int)b.length()];//an array is created but contains no data
						barr = b.getBytes(1,(int)b.length());
						
						/*FileOutputStream fout = new FileOutputStream("/storage/emulated/0/DCIM/Camera/testout.JPG");
						fout.write(barr);
								    
						fout.close();*/
						Log.d("Connect","Download COmpleted");
						break;
					}
					
					
				}
				
				DatabaseHandler db = new DatabaseHandler(this);
				Photo photo = new Photo(username, Bitmap.createScaledBitmap(convertBlobToBitmap(barr), 300, 350, true), lastupdate );
				Log.d("Connect","Deleting user: " + username + " from internal DB");
				db.deletePhotoByUserName(username);
				Log.d("Connect","Adding photo  to local DB");
				db.addPhoto(photo);
				db.close();
							
				}catch (Exception e) {
					Log.d("Connect","An error occurr while downloading image from external DB");
					e.printStackTrace();	
				
				}

		}
		
		
		public Bitmap convertBlobToBitmap(byte[] blobByteArray){       
		      Bitmap tempBitmap=null;        
		      if(blobByteArray!=null)
		      tempBitmap = BitmapFactory.decodeByteArray(blobByteArray, 0, blobByteArray.length);
		      Log.d("Byte[] a Bitmap", "Byte "+blobByteArray);
		      return tempBitmap;
		  }
		
		public static Connection getOracleConnection() throws Exception {
		    String driver = "oracle.jdbc.driver.OracleDriver";
		    String url = "jdbc:oracle:thin:@10.0.0.11:1521:XE";
		    String username = "openfire";
		    String password = "1234";

		    Class.forName(driver); // load Oracle driver
		    Connection conn = DriverManager.getConnection(url, username, password);
		    return conn;
		  }//Esta parte aun no se usa.
		
		public void GetLastPhotoUpdate(String username){
			Log.d("Connect","In GetLastPhotoUpdate");
			Log.d("Connect","username: "+ username);
			String lastupdate = " ";
			
			try {
				Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
				DriverManager.setLoginTimeout(0);
				dbconn = DriverManager.getConnection("jdbc:oracle:thin:@"+HOST+":"+DBPORT+":"+DBNAME, DBUSERNAME, DBPASSWORD);
				Statement st = dbconn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
				
				ResultSet resultado = st.executeQuery("select * from IMGTABLE where username = '"+username+"' ORDER BY ID ASC"); //ResultSet resultado = st.executeQuery("select * from ofID");  ooo "select * from ofmessagearchive"
				
				if (resultado == null){
					Log.d("Resul", "Null");
				}
				
				
				while (resultado.next()){
					if(resultado.isLast()){
						lastupdate = resultado.getString(4).toString();
						Log.d("Connect","LastUpdate: "+lastupdate);
					}
				}
					
				Log.d("Connect", "Getting lastUpdate success");
				st.close();
				dbconn.close();
				
				try {
					Photo photo = new Photo();
					DatabaseHandler db = new DatabaseHandler(this);
					photo = db.getPhotoDBByUserName(username);
					db.close();
					
					Log.d("Connect","LastUpdate photo: " + photo.getLastUpdate());
					if(!(lastupdate.equals(photo.getLastUpdate()))){
						Log.d("Connect","Looking for newer photo");
						downloadImage(lastupdate, username);
						
					}else{
						Log.d("Connect","photo is up to date");
					}
					
				} catch (Exception e) {
					// TODO: handle exception
					e.getStackTrace();
					Log.d("Connect", "photo of user NOT found in internal DB");
					downloadImage(lastupdate, username);
				}
				
				

			} catch (Exception e) {
				// TODO: handle exception
				e.getStackTrace();
				Log.d("Connect", "No photo found in external DB");
			}
			
		}
		
		
		
		public String DBGetUsernameWithEmail(String email){

			String username = new String();
				
			try{
					
				Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
				DriverManager.setLoginTimeout(0);
				dbconn = DriverManager.getConnection("jdbc:oracle:thin:@"+HOST+":"+DBPORT+":"+DBNAME, DBUSERNAME, DBPASSWORD);
				Statement st = dbconn.createStatement();
					
					
				ResultSet resultado = st.executeQuery("SELECT * FROM OFUSER WHERE EMAIL = '"+email+"'"); 
					
				if (resultado==null){
					Log.d("Resul", "Null");
				}
					
				while (resultado.next()){	
					if(resultado.getString(5).equals(email)){
						username = resultado.getString(1);
						break;
					}
				}

				st.close();
				dbconn.close();
					
				}catch(Exception e){
					Log.d("Connect", "Getting username with email failed!");
					e.printStackTrace();
				}
			return username;
		}

}
