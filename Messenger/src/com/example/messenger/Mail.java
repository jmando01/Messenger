package com.example.messenger;

import java.util.Date;
import java.util.Properties;
import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import android.util.Log;

public class Mail extends javax.mail.Authenticator{
	
	  private String mail = "privamessenger@gmail.com", pass="privaaccount01";
	  private String _user; 
	  private String _pass; 
	 
	  private String[] _to; 
	  private String _from; 
	 
	  private String _port; 
	  private String _sport; 
	 
	  private String _host; 
	 
	  private String _subject; 
	  private String _body; 
	 
	  private boolean _auth; 
	   
	  private boolean _debuggable; 
	 
	  private Multipart _multipart;
	  
	  
	  public Mail() { 
		/* this._host = "smtp.gmail.com"; // default smtp server 
		 this._port = "465"; // default smtp port 
		 this._sport = "465"; // default socketfactory port */
		 
		 this._user = ""; // username 
		 this._pass = ""; // password 
		 this._from = ""; // email sent from 
		 this._subject = ""; // email subject 
		 this._body = ""; // email body 
		 
	 
		 this._debuggable = false; // debug mode on or off - default off 
		 this._auth = true; // smtp authentication - default on 
	 
		 this._multipart = new MimeMultipart(); 
	 
	    Log.d("Email", "Mail bien");
	    // There is something wrong with MailCap, javamail can not find a handler for the multipart/mixed part, so this bit needs to be added. 
	    MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap(); 
	    mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html"); 
	    mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml"); 
	    mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain"); 
	    mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed"); 
	    mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822"); 
	    CommandMap.setDefaultCommandMap(mc); 
	    Log.d("Email", "Mail bien0");
	  } 
	 
	  public Mail(String user, String passs) {
		  this(); 
	    this._user = mail; 
	    this._pass = pass; 
	  } 
	 
	  public boolean send() throws Exception { 
		  Log.d("Email", "Mail bien 1");
	    Properties props = _setProperties(); 
	    Log.d("Email", "Mail bien2");
	    if(!_user.equals("") && !_pass.equals("") && _to.length > 0 && !_from.equals("") && !_subject.equals("") && !_body.equals("")) { 
	      Session session = Session.getInstance(props, this); 
	 
	      MimeMessage msg = new MimeMessage(session); 
	 
	      msg.setFrom(new InternetAddress(_from)); 
	       
	      InternetAddress[] addressTo = new InternetAddress[_to.length]; 
	      for (int i = 0; i < _to.length; i++) { 
	        addressTo[i] = new InternetAddress(_to[i]); 
	      } 
	        msg.setRecipients(MimeMessage.RecipientType.TO, addressTo); 
	 
	      msg.setSubject(_subject); 
	      msg.setSentDate(new Date()); 
	 
	      // setup message body 
	      BodyPart messageBodyPart = new MimeBodyPart(); 
	      Log.d("Email", "Mail bien 2... Body:"+_body+"Subject: "+_subject+"Email: "+_from);
	      messageBodyPart.setText(_body); 
	      _multipart.addBodyPart(messageBodyPart); 
	 
	      // Put parts in message 
	      msg.setContent(_multipart); 
	 
	      // send email 
	      Transport.send(msg); 
	 
	      return true; 
	    } else { 
	      Log.d("Email Fail", "Do not send, because one entry are empty.");
	      return false; 
	    } 
	  } 
	 
	  public void addAttachment(String filename) throws Exception { 
	    BodyPart messageBodyPart = new MimeBodyPart(); 
	    DataSource source = new FileDataSource(filename); 
	    messageBodyPart.setDataHandler(new DataHandler(source)); 
	    messageBodyPart.setFileName(filename); 
	 
	    _multipart.addBodyPart(messageBodyPart); 
	  } 
	 
	  @Override 
	  public PasswordAuthentication getPasswordAuthentication() { 
	    return new PasswordAuthentication(_user, _pass); 
	  } 
	 
	  private Properties _setProperties() { 
		  
		  Log.d("Email", "Mail bien 1.1");
		    Properties props = new Properties(); 
		 
		    props.put("mail.smtp.host", "smtp.gmail.com"); 
		    
		    if(_debuggable) { 
		      props.put("mail.debug", "true"); 
		    } 
		 
		    if(_auth) { 
		      props.put("mail.smtp.auth", "true"); 
		    } 
		    Log.d("Email", "Mail bien 1.3");
		    props.put("mail.smtp.auth", "true");
		    props.put("mail.smtp.port", "465"); 
		    props.put("mail.smtp.socketFactory.port", "465"); 
		    props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); 
		    props.put("mail.smtp.socketFactory.fallback", "false"); 
		    Log.d("Email", "Mail bien 1.4");
		    return props; 
		  } 
	 
	  public void setBody(String _body) { 
	    this._body = _body; 
	  }  
		 
	  public void setFrom(String _from) { 
		this._from = _from; 
	  }  
	  
	  public void setSubject(String _Subject) { 
		this._subject = _Subject; 
	  }  

	  public void setTo(String[] _To) { 
			this._to = _To; 
		  } 
	  
	  /*public String PingServerSMTP()
	  {
			    String str = "";
			    try {
			        Process process = Runtime.getRuntime().exec(
			                "/system/bin/ping -c 8 " + url);
			        BufferedReader reader = new BufferedReader(new InputStreamReader(
			                process.getInputStream()));
			        int i;
			        char[] buffer = new char[4096];
			        StringBuffer output = new StringBuffer();
			        while ((i = reader.read(buffer)) > 0)
			            output.append(buffer, 0, i);
			        reader.close();

			        // body.append(output.toString()+"\n");
			        str = output.toString().length()+"";
			        // Log.d(TAG, str);
			    } catch (IOException e) {
			        // body.append("Error\n");
			        e.printStackTrace();
			    }
			    return str;
			
	  }*/
}
