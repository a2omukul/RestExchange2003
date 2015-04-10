import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
//import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;
import java.util.UUID;

import javax.xml.crypto.dsig.keyinfo.RetrievalMethod;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.jetty.servlets.EventSource.Emitter;

import com.independentsoft.webdav.exchange.AppointmentPropertyName;
import com.independentsoft.webdav.exchange.Attachment;
import com.independentsoft.webdav.exchange.Condition;
import com.independentsoft.webdav.exchange.ContentClassType;
import com.independentsoft.webdav.exchange.From;
import com.independentsoft.webdav.exchange.Like;
import com.independentsoft.webdav.exchange.LogicalOperator;
import com.independentsoft.webdav.exchange.Mailbox;
import com.independentsoft.webdav.exchange.Message;
import com.independentsoft.webdav.exchange.MessagePropertyName;
import com.independentsoft.webdav.exchange.MultiStatus;
import com.independentsoft.webdav.exchange.Operator;
import com.independentsoft.webdav.exchange.Order;
import com.independentsoft.webdav.exchange.OrderBy;
import com.independentsoft.webdav.exchange.Property;
import com.independentsoft.webdav.exchange.PropertyName;
import com.independentsoft.webdav.exchange.RowRange;
import com.independentsoft.webdav.exchange.SearchResult;
import com.independentsoft.webdav.exchange.SearchResultRecord;
import com.independentsoft.webdav.exchange.Select;
import com.independentsoft.webdav.exchange.SqlQuery;
import com.independentsoft.webdav.exchange.WebdavClient;
import com.independentsoft.webdav.exchange.WebdavException;
import com.independentsoft.webdav.exchange.Where;

;
/*
 WebDavExchange class handles exchange methods
 */

public class WebDavExchange implements IConstants {
	   
	 public WebdavClient client;
	 public String uri;
	 public String username;
	 public String password;
	 
	
	public WebDavExchange(String uri,String userName,String password)
	{
		this.uri=uri;
		this.username=userName;
		this.password=password;
		this.client= new WebdavClient(uri+"/"+this.username,this.username,this.password);
		
	}
	
	public int checkAuth()   //check user valid/invalid user auth
	{
		try {
			Mailbox myMailbox = client.getMailbox();
			return 1;
			
		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}
	
	}
	
	//fetch email from inbox
 public ArrayList<HashMap<String, String>> getEmails(String sortdirection, int fieldcount,String inputoperator,String inputquery) throws WebdavException,ParseException
 {

	
			Mailbox myMailbox = client.getMailbox();
			// add properties
			List<PropertyName> propertyNames = new ArrayList<PropertyName>();
			//propertyNames.add(MessagePropertyName.CONTENT_CLASS);
			//propertyNames.add(MessagePropertyName.SUBJECT);
			//propertyNames.add(MessagePropertyName.BODY);
			//propertyNames.add(MessagePropertyName.FROM_EMAIL);
			//propertyNames.add(MessagePropertyName.DATE_RECEIVED);
			propertyNames.add(MessagePropertyName.MESSAGE_ID);
			propertyNames.add(MessagePropertyName.UID);
			//propertyNames.add(MessagePropertyName.)
			Select select = new Select(propertyNames);
			From from = new From(myMailbox.getInbox());
			Where where = new Where();

			// set datetime
			Calendar localCalendar = Calendar.getInstance();
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy-MM-dd'T'hh:mm:ss");
			String dateInString = inputquery;
			Date date = formatter.parse(dateInString);
			System.out.println(date);

			// set conditions
			Condition condition1 = new Condition(
					MessagePropertyName.CONTENT_CLASS, Operator.EQUALS,
					ContentClassType.MESSAGE);
			Condition condition2 = null;

			if (inputoperator.equals(">="))
				condition2 = new Condition(MessagePropertyName.DATE_RECEIVED,
						Operator.GREATER_THEN_OR_EQUALS, date);

			else if (inputoperator.equals(">"))
				condition2 = new Condition(MessagePropertyName.DATE_RECEIVED,
						Operator.GREATER_THEN, date);

			else if (inputoperator.equals("<"))
				condition2 = new Condition(MessagePropertyName.DATE_RECEIVED,
						Operator.LESS_THEN, date);

			else if (inputoperator.equals("<="))
				condition2 = new Condition(MessagePropertyName.DATE_RECEIVED,
						Operator.LESS_THEN_OR_EQUALS, date);

			else if (inputoperator.equals("="))
				condition2 = new Condition(MessagePropertyName.DATE_RECEIVED,
						Operator.EQUALS, date);

			where.add(condition1);
			where.add(LogicalOperator.AND);
			where.add(condition2);

			OrderBy order = null;
			if (sortdirection.equals("Ascending"))
				order = new OrderBy(MessagePropertyName.DATE_RECEIVED,
						Order.ASC);
			else if (sortdirection.equals("Descending"))
				order = new OrderBy(MessagePropertyName.DATE_RECEIVED,
						Order.DESC);

			// set no of emails to be fetched
			String rr ;
			if (fieldcount==0){
				rr = "0-" + String.valueOf(fieldcount );
			}else{
				rr = "0-" + String.valueOf(fieldcount - 1);
			}
			RowRange range = new RowRange();
			range.addRange(rr);

			SqlQuery sqlQuery = new SqlQuery(select, from, where, order);
			MultiStatus multiStatus = client.search(sqlQuery, range);

			SearchResult searchResult = new SearchResult(multiStatus,
					propertyNames);
			SearchResultRecord[] allRecords = searchResult.getRecords(); // email
																			// records
			
			ArrayList<HashMap<String, String>> myList = new ArrayList<HashMap<String, String>>();

			for (int i = 0; i < allRecords.length; i++) // iterate over email
														// records
			{
				System.out.println("Message URL=" + allRecords[i].getUrl());
				HashMap<String, String> records = new HashMap<String, String>();
				 Property[] property = allRecords[i].getProperties();
				//searchResult ty=allRecords[i];
			    //	ty.
				System.out.println("ContentClass=" + property[0].getValue());
				// System.out.println("Subject=" + property[1].getValue());
				// System.out.println("Body=" + property[2].getValue());
				// System.out.println("FromEmail=" + property[3].getValue());
				 System.out.println("m_id=" + property[0].getValue());
				System.out.println("uu_id=" + property[1].getValue());
				// System.out.println("DateReceived=" +
				// toLocalTime(property[4].getValue()));
				//records.put(property[0].getName(), property[0].getValue());
				records.put(property[1].getName(), property[1].getValue());
				records.put("m_url", allRecords[i].getUrl());
				myList.add(records);
				
			}
			return myList;
		

	}

	public void getEvents(String startDate) {
		// WebdavClient client = new
		// WebdavClient("https://myserver/exchange/emailaddress", "username",
		// "password");
		try {
			Mailbox myMailbox = client.getMailbox(WEBDAV_HOST_URL + "/"
					+ username);
			// set property
			List<PropertyName> propertyNames = new ArrayList<PropertyName>();
			propertyNames.add(AppointmentPropertyName.CONTENT_CLASS);
			propertyNames.add(AppointmentPropertyName.START_DATE);
			propertyNames.add(AppointmentPropertyName.END_DATE);
			propertyNames.add(AppointmentPropertyName.SUBJECT);
			propertyNames.add(AppointmentPropertyName.BODY);

			Select select = new Select(propertyNames);
			From from = new From(myMailbox.getCalendar());
			Where where = new Where();
			Calendar localCalendar = Calendar.getInstance();
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy-MM-dd'T'hh:mm:ss");
			String dateInString = startDate;
			Date date = formatter.parse(dateInString);
			System.out.println(date);

			Condition condition1 = new Condition(
					AppointmentPropertyName.CONTENT_CLASS, Operator.EQUALS,
					ContentClassType.APPOINTMENT);
			Condition condition2 = new Condition(
					AppointmentPropertyName.START_DATE,
					Operator.GREATER_THEN_OR_EQUALS, date);
			// Condition condition3 = new
			// Condition(AppointmentPropertyName.END_DATE, Operator.LESS_THEN,
			// endDate);

			where.add(condition1);
			where.add(LogicalOperator.AND);
			where.add(condition2);

			SqlQuery sqlQuery = new SqlQuery(select, from, where);
			MultiStatus multiStatus = client.search(sqlQuery);

			SearchResult searchResult = new SearchResult(multiStatus,
					propertyNames);
			SearchResultRecord[] allRecords = searchResult.getRecords();

			for (int i = 0; i < allRecords.length; i++) {
				System.out.println("Appointment URL=" + allRecords[i].getUrl());

				// appoitnment's properties
				Property[] property = allRecords[i].getProperties();

				System.out.println("ContentClass=" + property[0].getValue());

				System.out.println("Subject=" + property[3].getValue());
				System.out.println("Body=" + property[4].getValue());
			}
		} catch (WebdavException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	 public EmailInfo  fetchParticularEmail(String uuid)throws WebdavException, NotFoundException
	    {
	     
	            Mailbox myMailbox = client.getMailbox();
	            List<PropertyName> propertyNames = new ArrayList<PropertyName>();
	           // propertyNames.add(MessagePropertyName.CONTENT_CLASS);
	            propertyNames.add(MessagePropertyName.SUBJECT);
	            propertyNames.add(MessagePropertyName.BODY);
	            propertyNames.add(MessagePropertyName.FROM_EMAIL);
	            propertyNames.add(MessagePropertyName.TO);
	            propertyNames.add(MessagePropertyName.CC);
	            //propertyNames.add(MessagePropertyName.DISPLAY_CC);
	            //propertyNames.add(MessagePropertyName.DISPLAY_TO);
	            propertyNames.add(MessagePropertyName.TEXT_DESCRIPTION);
	            propertyNames.add(MessagePropertyName.HAS_ATTACHMENT);
	            propertyNames.add(MessagePropertyName.HTML_DESCRIPTION);
	           // propertyNames.add(MessagePropertyName.ATTACHMENT_FILE_NAME);
	            propertyNames.add(MessagePropertyName.DATE_RECEIVED);
	            propertyNames.add(MessagePropertyName.UID);
	            // propertyNames.add(MessagePropertyName.);
	            
	            
	            Select select = new Select(propertyNames);
	            From from = new From(myMailbox.getInbox());
	            Where where = new Where();

	            Condition condition1 = new Condition(MessagePropertyName.CONTENT_CLASS, Operator.EQUALS, ContentClassType.MESSAGE);
	            Like condition2 = new Like(MessagePropertyName.UID, uuid);

	            where.add(condition1);
	            where.add(LogicalOperator.AND);
	            where.add(condition2);

	            SqlQuery sqlQuery = new SqlQuery(select, from, where);
	            MultiStatus multiStatus = client.search(sqlQuery);

	            SearchResult searchResult = new SearchResult(multiStatus, propertyNames);
	            SearchResultRecord[] allRecords = searchResult.getRecords();
	            if(allRecords.length==0){
	            	throw new NotFoundException("this message does not exist");
	            }
	            else{
	            	SearchResultRecord rr=allRecords[0];
	            	 Property[] property = allRecords[0].getProperties();
	            	 //System.out.println(property[1].getName());
	            	 HashMap<String, String> records = new HashMap<String, String>();
	            	ArrayList<HashMap<String, String>> attachments=getAttchementInfo(rr.getUrl());
	            	String subject=property[0].getValue().trim();
	            	String body=property[1].getValue().trim();
	            	String from_email=property[2].getValue().trim();
	            	String to_str=property[3].getValue().trim();
	            	List<String> to = Arrays.asList(to_str.split(";"));
	            	String cc_str=property[4].getValue().trim();
	            	List<String> cc = Arrays.asList(cc_str.split(";"));
	            	String textd=property[5].getValue().trim();
	            	String htmld=property[7].getValue().trim();
	            	String dater=property[8].getValue().trim();
	            	String id=property[9].getValue().trim();
	          
	            	 EmailInfo email_body=new EmailInfo(id, from_email, subject, to, cc, body, dater, textd, htmld, attachments);
	            		// System.in.read();
	            	 return email_body;
					}
	     
	  }
	 public void sendEmail(EmailResponse emailBody,String guid)throws WebdavException, MalformedURLException, IOException
	 {
		 //WebdavClient client = new WebdavClient("https://myserver/exchange/emailaddress", "username", "password");
        Message message = new Message();
         message.setSubject(emailBody.subject);
         message.setBody(emailBody.textBody);
         ArrayList<String> to=emailBody.to;
         for (String to_email : to) {
        	 System.out.println("to:"+to_email);
            message.setTo(to_email);
           }
         for (String cc_email : emailBody.cc) {
        	 System.out.println("cc:"+cc_email);
             message.setTo(cc_email);
            }
         	 ArrayList<HashMap<String, String>> attachments=emailBody.attachments;
         	 for (HashMap<String, String> entry : attachments) {
         		     String sourceType=  entry.get("sourceType");
         		    System.out.println("spurcetype:"+sourceType);
         		     if(sourceType.equalsIgnoreCase("url")){
         		    	 String url=entry.get("url");
         		    	 System.out.println(url);
         		    	 String file_name=entry.get("name");
         		    	InputStream input = new URL(url).openStream();
         		    	message.addAttachment(input, file_name);
         		     }
         		     else if(sourceType.equalsIgnoreCase("content")){
         		    	//String url=entry.get("url");
         		    	 String content=entry.get("content");
         		    	 System.out.println(content);
         		    	byte[] decodedBytes = Base64.decodeBase64(content);
         		    	InputStream is = new ByteArrayInputStream(decodedBytes);
        		    	String file_name=entry.get("name");
        		    	//InputStream input = new URL(url).openStream();
        		    	message.addAttachment(is, file_name);
         		     }
             }
         	//guid=UUID.randomUUID().toString();
         	 System.out.println("guid:"+guid);
         	Property myProperty = new Property(CREATED_ID_PROPERTY_DEF, "urn:schemas:mailheader:", guid);
         	client.sendMessage(message, myProperty);
         	//System.out.println(message.getID());
         	System.out.println("sent message");
         	
	 }
	   
	 public ArrayList<HashMap<String, String>> getAttchementInfo(String attachmentUrl)throws WebdavException
	 {
	
	            //WebdavClient client = new WebdavClient("https://myserver/exchange/emailaddress", "username", "password");

	            Attachment[] attachments = client.getAttachments(attachmentUrl);
	            ArrayList<HashMap<String, String>> attachment_list = new ArrayList<HashMap<String, String>>();
	            HashMap<String, String> records = new HashMap<String, String>();
	            for (int k = 0; k < attachments.length; k++)
	            {
	            	 HashMap<String, String> single_att = new HashMap<String, String>();
	            	String atachmentUrl = attachments[k].getUrl();
	                String atachmentFileName = attachments[k].getFileName();
	                String content_type=attachments[k].getMimeType();
	                //String content_type=attachments[k].getExtension()();
	                single_att.put("attachment_url", atachmentUrl);
	                single_att.put("attachment_fileName", atachmentFileName);
	                single_att.put("contentType", content_type);
	                attachment_list.add(single_att);
	             
	            }
	            return attachment_list;
	     
	 }
	 
	 public  String  search_email_uuid(String id) throws WebdavException, InterruptedException
	    {
	     
		 		Thread.sleep(1000);       
	            Mailbox myMailbox = client.getMailbox(uri+"/"+username);
	            PropertyName customPropertyName1 = new PropertyName(CREATED_ID_PROPERTY_DEF, "urn:schemas:mailheader:");// user defined property
	            List<PropertyName> propertyNames = new ArrayList<PropertyName>();
	            propertyNames.add(MessagePropertyName.UID);
	            
	            Select select = new Select(propertyNames);
	            From from = new From(myMailbox.getSentItems());
	            Where where = new Where();

	            Condition condition1 = new Condition(MessagePropertyName.CONTENT_CLASS, Operator.EQUALS, ContentClassType.MESSAGE);
	            Like condition2 = new Like(customPropertyName1, id);

	            where.add(condition1);
	            where.add(LogicalOperator.AND);
	            where.add(condition2);

	            SqlQuery sqlQuery = new SqlQuery(select, from, where);
	            MultiStatus multiStatus = client.search(sqlQuery);

	            SearchResult searchResult = new SearchResult(multiStatus, propertyNames);
	            SearchResultRecord[] allRecords = searchResult.getRecords();
	            String uuid="";
	            if(allRecords.length==0){
	            	
	            }
	            else{
	            	SearchResultRecord rr=allRecords[0];
	            	 Property[] property = allRecords[0].getProperties();
	            	 uuid=property[0].getValue();
	            }
	            return uuid;
	    }
	

}
