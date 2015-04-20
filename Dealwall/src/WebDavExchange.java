import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import jcifs.dcerpc.msrpc.netdfs;

import org.apache.commons.codec.binary.Base64;
//import org.eclipse.jetty.servlets.EventSource.Emitter;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

import com.independentsoft.webdav.exchange.Scope;
import com.independentsoft.webdav.exchange.WebdavClient;
import com.independentsoft.webdav.exchange.Appointment;
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
	private static final Logger LOG = Log.getLogger(WebDavExchange.class);

	public WebDavExchange(String uri, String userName, String password) {
		this.uri = uri;
		this.username = userName;
		this.password = password;
		// System.out.println(uri);
		this.client = new WebdavClient(uri + "/" + this.username,
				this.username, this.password);

	}

	public int checkAuth() // check user valid/invalid user auth
	{
		try {
			Mailbox myMailbox = client.getMailbox();
			return 1;

		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}

	}

	// fetch email from inbox
	public ArrayList<HashMap<String, String>> getEmails(String sortdirection,
			int fieldcount, String inputoperator, String inputquery)
			throws WebdavException, ParseException {
		Mailbox myMailbox = client.getMailbox();
		LOG.info("get Email WedDaV from Inbox called");
		List<PropertyName> propertyNames = new ArrayList<PropertyName>();
		propertyNames.add(MessagePropertyName.MESSAGE_ID);
		propertyNames.add(MessagePropertyName.UID);
		propertyNames.add(MessagePropertyName.DATE_RECEIVED);
		// propertyNames.add(MessagePropertyName.)
		Select select = new Select(propertyNames);
		From from = new From(myMailbox.getInbox());
		Where where = new Where();

		// set datetime
		SimpleDateFormat formatter = new SimpleDateFormat(
				"yyyy-MM-dd'T'hh:mm:sss");
		String dateInString = inputquery;
		// System.out.println("fate in str" + dateInString);
		Date date = formatter.parse(dateInString);
		//System.out.println(date);

		// set conditions
		Condition condition1 = new Condition(MessagePropertyName.CONTENT_CLASS,
				Operator.EQUALS, ContentClassType.MESSAGE);
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
			order = new OrderBy(MessagePropertyName.DATE_RECEIVED, Order.ASC);
		else if (sortdirection.equals("Descending"))
			order = new OrderBy(MessagePropertyName.DATE_RECEIVED, Order.DESC);

		// set no of emails to be fetched
		String rr;
		if (fieldcount == 0) {
			rr = "0-" + String.valueOf(fieldcount);
		} else {
			rr = "0-" + String.valueOf(fieldcount - 1);
		}
		RowRange range = new RowRange();
		range.addRange(rr);

		SqlQuery sqlQuery = new SqlQuery(select, from, where, order);
		MultiStatus multiStatus = client.search(sqlQuery, range);

		SearchResult searchResult = new SearchResult(multiStatus, propertyNames);
		SearchResultRecord[] allRecords = searchResult.getRecords(); // email
																		// records

		ArrayList<HashMap<String, String>> myList = new ArrayList<HashMap<String, String>>();
		LOG.info("getEmail no of records found is: {}", allRecords.length);
		LOG.info("getEmail iterate over ");
		for (int i = 0; i < allRecords.length; i++) // iterate over email
													// records
		{
			// System.out.println("Message URL=" + allRecords[i].getUrl());
			HashMap<String, String> records = new HashMap<String, String>();
			Property[] property = allRecords[i].getProperties();
			// System.out.println("ContentClass=" + property[0].getValue());
			// System.out.println("m_id=" + property[0].getValue());
			// System.out.println("uu_id=" + property[1].getValue());
			LOG.info("getEmail fetched message_url: ", allRecords[i].getUrl());
			records.put(property[1].getName(), property[1].getValue());
			LOG.info("getEmail its  uid: ", property[1].getValue());
			String datestring = property[2].getValue();
			datestring = HelperClass.getDateInFormat(datestring);
			records.put("dateTimeReceived", datestring);
			records.put("m_url", allRecords[i].getUrl());
			myList.add(records);

		}
		return myList;

	}

	public List<HashMap<String, String>> getEvents(String startDate) throws WebdavException,
			ParseException {
		
		LOG.info("get events WedDaV method   called");
		Mailbox myMailbox = client.getMailbox();
		List<PropertyName> propertyNames = new ArrayList<PropertyName>();
		propertyNames.add(AppointmentPropertyName.UID);
		propertyNames.add(AppointmentPropertyName.START_DATE);
		propertyNames.add(AppointmentPropertyName.CREATION_DATE);
		Select select = new Select(propertyNames);
		From from = new From(myMailbox.getCalendar());
		Where where = new Where();
		Calendar localCalendar = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat(
				"yyyy-MM-dd'T'hh:mm:sss");
		String dateInString = startDate;
		Date date = formatter.parse(dateInString);
		//System.out.println(date);

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

		SearchResult searchResult = new SearchResult(multiStatus, propertyNames);
		SearchResultRecord[] allRecords = searchResult.getRecords();
		List<String> ids = new ArrayList<String>();
		
		List<HashMap<String, String>> events=new ArrayList<HashMap<String,String>>();
		LOG.info("getEvents  no of records found is: {}", allRecords.length);
		LOG.info("getEvents iterate over ");
		for (int i = 0; i < allRecords.length; i++) {
			//System.out.println("Appointment URL=" + allRecords[i].getUrl());
			HashMap<String, String> event_map=new HashMap<String, String>();
			event_map.put("eventURL", allRecords[i].getUrl());
			
			// appoitnment's properties
			Property[] property = allRecords[i].getProperties();
			event_map.put("UID", property[0].getValue());
			String creatio_date=property[2].getValue();
			creatio_date = HelperClass.getDateInFormat(creatio_date);
			event_map.put("dateTimeReceived", creatio_date);
			//System.out.println("date=" + property[1].getValue());
			LOG.info("event url: {}",allRecords[i].getUrl());
			LOG.info("event uuid: {}",property[0].getValue());
			events.add(event_map);
		}
		return events;

	}

	public EmailInfo fetchParticularEmail(String uuid) throws WebdavException,
			NotFoundException, MoreThanOneFound {

		Mailbox myMailbox = client.getMailbox();
		List<PropertyName> propertyNames = HelperClass
				.getEmailMessagePropertyNamesForEmailFetch();

		Select select = new Select(propertyNames);
		From from = new From(myMailbox.getInbox());
		Where where = new Where();

		Condition condition1 = new Condition(MessagePropertyName.CONTENT_CLASS,
				Operator.EQUALS, ContentClassType.MESSAGE);
		Condition condition2 = new Condition(MessagePropertyName.UID,
				Operator.EQUALS, uuid);

		where.add(condition1);
		where.add(LogicalOperator.AND);
		where.add(condition2);

		SqlQuery sqlQuery = new SqlQuery(select, from, where);
		MultiStatus multiStatus = client.search(sqlQuery);

		SearchResult searchResult = new SearchResult(multiStatus, propertyNames);
		SearchResultRecord[] allRecords = searchResult.getRecords();
		if (allRecords.length == 0) {
			throw new NotFoundException("this message does not exist");
		} else {
			SearchResultRecord rr = allRecords[0];
			Property[] property = allRecords[0].getProperties();
			// System.out.println(property[1].getName());
			HashMap<String, String> records = new HashMap<String, String>();
			ArrayList<HashMap<String, String>> attachments = getAttchementInfo(rr
					.getUrl());
			String subject = property[0].getValue().trim();
			String body = property[1].getValue().trim();
			String from_email = property[2].getValue().trim();
			String to_str = property[3].getValue().trim();
			List<String> to = Arrays.asList(to_str.split(";"));
			String cc_str = property[4].getValue().trim();
			List<String> cc = Arrays.asList(cc_str.split(";"));
			String textd = property[5].getValue().trim();
			String htmld = property[7].getValue().trim();
			String dater = property[8].getValue().trim();
			dater = HelperClass.getDateInFormat(dater);
			String id = property[9].getValue().trim();

			EmailInfo email_body = new EmailInfo(id, from_email, subject, to,
					cc, dater, textd, htmld, attachments);
			// System.in.read();
			return email_body;
		}

	}

	public ArrayList<EmailInfo> fetchEmails(List<String> uuids)
			throws WebdavException, NotFoundException, MoreThanOneFound,
			IOException {
		LOG.info("fetch email called with uids: {}", uuids.toString());
		ArrayList<EmailInfo> emailsList = new ArrayList<EmailInfo>();
		for (String uid : uuids) {
			LOG.info("fetch email called with uid: {}", uid);
			String url_mailbox = this.uri + this.username;
			//System.out.println("url mailbox:" + url_mailbox);
			Mailbox myMailbox = client.getMailbox(url_mailbox);
			List<PropertyName> propertyNames = HelperClass
					.getEmailMessagePropertyNamesForEmailFetch();

			Select select = new Select(propertyNames);
			From from = new From(myMailbox.getInbox());
			Where where = new Where();

			Condition condition1 = new Condition(
					MessagePropertyName.CONTENT_CLASS, Operator.EQUALS,
					ContentClassType.MESSAGE);

			Condition condition2 = new Condition(MessagePropertyName.UID,
					Operator.EQUALS, uid.trim());

			where.add(condition1);
			where.add(LogicalOperator.AND);
			where.add(condition2);

			SqlQuery sqlQuery = new SqlQuery(select, from, where);
			MultiStatus multiStatus = client.search(sqlQuery);

			SearchResult searchResult = new SearchResult(multiStatus,
					propertyNames);
			SearchResultRecord[] allRecords = searchResult.getRecords();
			LOG.info("fetchEmail for uuid:{}  no of records found: {}",
					allRecords.length, uuids.toString());
			if (allRecords.length == 0) {
				LOG.info("fetchEmail does not exist uuid: {}", uid);
				throw new NotFoundException("this message does not exist");
			} else if (allRecords.length >= 2) {
				LOG.info(
						"fetchEmail  uuid: {} is associated with more than one email urls 1. {}   2. {} ",
						uid, allRecords[0].getUrl(), allRecords[1].getUrl());
				throw new MoreThanOneFound(
						"this uuid is associated with more than one message");
				// System.in.read();
			} else {
				// System.out.println("url" + allRecords[0].getUrl());
				LOG.info("fetchEmail  uid: {}  has email url is: {} ", uid,
						allRecords[0].getUrl());
				SearchResultRecord rr = allRecords[0];
				Property[] property = allRecords[0].getProperties();
				ArrayList<HashMap<String, String>> attachments = getAttchementInfo(rr
						.getUrl());
				EmailInfo email_body = HelperClass
						.getEmailBodyFromProperty(property);
				email_body.attachments = attachments;
				emailsList.add(email_body);
			}
		}
		return emailsList;

	}

	public ArrayList<EmailInfo> fetchEmailsLike(List<String> uuids)
			throws WebdavException, NotFoundException, MoreThanOneFound,
			IOException {
		LOG.info("fetch email called with uids: {}", uuids.toString());
		ArrayList<EmailInfo> emailsList = new ArrayList<EmailInfo>();
		for (String uid : uuids) {
			LOG.info("fetch email called with uid: {}", uid);
			String url_mailbox = this.uri + this.username;
			// System.out.println("url mailbox:" + url_mailbox);
			Mailbox myMailbox = client.getMailbox(url_mailbox);
			List<PropertyName> propertyNames = HelperClass
					.getEmailMessagePropertyNamesForEmailFetch();
			Select select = new Select(propertyNames);
			From from = new From(myMailbox.getRoot(),Scope.DEEP);
			Where where = new Where();

			Condition condition1 = new Condition(
					MessagePropertyName.CONTENT_CLASS, Operator.EQUALS,
					ContentClassType.MESSAGE);

			Like condition2 = new Like(MessagePropertyName.UID, uid.trim());
			where.add(condition1);
			where.add(LogicalOperator.AND);
			where.add(condition2);

			SqlQuery sqlQuery = new SqlQuery(select, from, where);
			MultiStatus multiStatus = client.search(sqlQuery);

			SearchResult searchResult = new SearchResult(multiStatus,
					propertyNames);
			SearchResultRecord[] allRecords = searchResult.getRecords();
			LOG.info("fetchEmail no of records found: {} for uid:",
					allRecords.length, uid.toString());
			if (allRecords.length == 0) {
				LOG.info("fetchEmail does not exist uuid: {}", uid);
				throw new NotFoundException("non existing uuid: "+uid);
			} else if (allRecords.length >= 2) {
				LOG.info(
						"fetchEmail  uuid: {} is associated with more than one email urls 1. {}   2. {} ",
						uid, allRecords[0].getUrl(), allRecords[1].getUrl());

				// System.in.read();
				int len = allRecords.length;
				int flag = 0;
				for (int k = 0; k < len; k++) {
					// LOG.info("fetchEmail  more than one found check which is right one ",uid));
					LOG.debug(
							"fetchEmail check for more tha one   uid: {}  has email url is: {} ",
							uid, allRecords[k].getUrl());
					// LOG.info("fetchEmail  uid: {}  has email url is: {} ",uid,allRecords[0].getUrl());
					SearchResultRecord rr = allRecords[k];
					Property[] property = allRecords[k].getProperties();
					// System.out.println(property[1].getName());
					EmailInfo emailBody = HelperClass
							.getEmailBodyFromProperty(property);

					if (emailBody.id.equals(uid)) {
						flag++;
						ArrayList<HashMap<String, String>> attachments = getAttchementInfo(rr
								.getUrl());
						emailBody.attachments = attachments;
						// System.in.read();
						// return email_body;
						emailsList.add(emailBody);
						LOG.info("fetchEmail uuid: {} added to result ",
								emailBody.id);
					}
				}
				if (flag >= 2) {
					throw new MoreThanOneFound(
							"more than message exist this with uid:{}" + uid);
				}

			} else {
				// System.out.println("url" + allRecords[0].getUrl());
				LOG.info("fetchEmail  uid: {}  has email url is: {} ", uid,
						allRecords[0].getUrl());
				SearchResultRecord rr = allRecords[0];
				Property[] property = allRecords[0].getProperties();
				ArrayList<HashMap<String, String>> attachments = getAttchementInfo(rr
						.getUrl());
				EmailInfo emailBody = HelperClass
						.getEmailBodyFromProperty(property);
				emailBody.attachments = attachments;
				emailsList.add(emailBody);
				LOG.info("fetchEmail uuid: {} added to result ", emailBody.id);
			}
		}
		return emailsList;

	}

	public void sendEmail(EmailResponse emailBody, String guid)
			throws WebdavException, MalformedURLException, IOException {
		LOG.info("sendEmail called");
		Message message = new Message();
		message.setSubject(emailBody.subject);
		message.setBody(emailBody.textBody);
		message.setHtmlBody(emailBody.htmlBody);
		message.setFrom(emailBody.from);
		ArrayList<String> to = emailBody.to;
		String toEmailStr = HelperClass.formString(to, ";");
		String ccEmailStr = HelperClass.formString(emailBody.cc, ";");
		message.setTo(toEmailStr);
		message.setCc(ccEmailStr);
		ArrayList<HashMap<String, String>> attachments = emailBody.attachments;
		for (HashMap<String, String> entry : attachments) {
			String sourceType = entry.get("sourceType");
			//System.out.println("spurcetype:" + sourceType);
			if (sourceType.equalsIgnoreCase("url")) {
				String url = entry.get("url");
				//System.out.println(url);
				String file_name = entry.get("name");
				InputStream input = new URL(url).openStream();
				message.addAttachment(input, file_name);
			} else if (sourceType.equalsIgnoreCase("content")) {
				// String url=entry.get("url");
				String content = entry.get("content");
				//System.out.println(content);
				byte[] decodedBytes = Base64.decodeBase64(content);
				InputStream is = new ByteArrayInputStream(decodedBytes);
				String file_name = entry.get("name");
				// InputStream input = new URL(url).openStream();
				message.addAttachment(is, file_name);
			}
		}
		// guid=UUID.randomUUID().toString();
		// System.out.println("guid:" + guid);
		Property myProperty = new Property(CREATED_ID_PROPERTY_DEF,
				"urn:schemas:mailheader:", guid);
		client.sendMessage(message, myProperty);
		// System.out.println(message.getID());
		LOG.info("sendEmail email sent to: {}", toEmailStr);

	}

	public ArrayList<HashMap<String, String>> getAttchementInfo(
			String message_url) throws WebdavException {

		// WebdavClient client = new
		// WebdavClient("https://myserver/exchange/emailaddress", "username",
		// "password");

		Attachment[] attachments = client.getAttachments(message_url);
		ArrayList<HashMap<String, String>> attachment_list = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> records = new HashMap<String, String>();
		for (int k = 0; k < attachments.length; k++) {
			HashMap<String, String> single_att = new HashMap<String, String>();
			String atachmentUrl = attachments[k].getUrl();
			String atachmentFileName = attachments[k].getFileName();
			String content_type = attachments[k].getMimeType();
			// String content_type=attachments[k].getExtension()();
			single_att.put("attachment_url", atachmentUrl);

			if (atachmentFileName == null) {
				atachmentFileName = atachmentUrl.substring(atachmentUrl
						.lastIndexOf("/") + 1);
				// System.out.println(atachmentFileName);
			}
			if (content_type == null) {
				single_att.put("contentType", "application/octet-stream");
			} else {
				single_att.put("contentType", content_type);
			}
			single_att.put("attachment_fileName", atachmentFileName);
			attachment_list.add(single_att);
		}
		return attachment_list;

	}

	public String[] search_email_uuid(String id) throws WebdavException,
			InterruptedException, NotFoundException, MoreThanOneFound {

		Thread.sleep(1000);
		Mailbox myMailbox = client.getMailbox(uri + "/" + username);
		PropertyName customPropertyName1 = new PropertyName(
				CREATED_ID_PROPERTY_DEF, "urn:schemas:mailheader:");

		// PropertyName customPropertyName2 = new
		// PropertyName(CREATED_ID_PROPERTY_DEF, "");
		List<PropertyName> propertyNames = new ArrayList<PropertyName>();
		propertyNames.add(MessagePropertyName.UID);
		// propertyNames.add(MessagePropertyName.UID);

		Select select = new Select(propertyNames);
		From from = new From(myMailbox.getSentItems());
		Where where = new Where();

		Condition condition1 = new Condition(MessagePropertyName.CONTENT_CLASS,
				Operator.EQUALS, ContentClassType.MESSAGE);

		Condition condition2 = new Condition(customPropertyName1,
				Operator.EQUALS, id);

		where.add(condition1);
		where.add(LogicalOperator.AND);
		where.add(condition2);

		SqlQuery sqlQuery = new SqlQuery(select, from, where);
		MultiStatus multiStatus = client.search(sqlQuery);

		SearchResult searchResult = new SearchResult(multiStatus, propertyNames);
		SearchResultRecord[] allRecords = searchResult.getRecords();
		String uuid = "";
		String res[]=new String[2];
		if (allRecords.length == 0) {
			LOG.warn("sendEmail custome property guid: {} not found", id);
			throw new NotFoundException(
					"email send but, its guid is not found in email");
		} else if (allRecords.length >= 2) {
			LOG.warn(
					"sendEmail custome property guid: {} is associated with more than email 1.EmailURL: {}  1.EmailURL: {}",
					id, allRecords[0].getUrl(), allRecords[1].getUrl());
			throw new MoreThanOneFound(
					"email send but its guid is associated with more than one email");
		} else {
			//System.out.println("lenght of email" + allRecords.length);
			SearchResultRecord rr = allRecords[0];
			//System.out.println("url" + allRecords[0].getUrl());
			Property[] property = allRecords[0].getProperties();
			//System.out.println("uid" + property[0].getValue());
			uuid = property[0].getValue();
			res[0]=rr.getUrl();
			res[1]=uuid;
			
		}
		return res;
	}

	public String[] sendEvent(EventJsonRequest request, String uuid)
			throws ParseException, WebdavException, IOException,
			MalformedURLException, InterruptedException {

		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss");
		Date startDate = dateFormat.parse(request.start);
		Date endDate = dateFormat.parse(request.end);

		Appointment appointment1 = new Appointment();
		appointment1.setSubject(request.subject);
		appointment1.setBody(request.textBody);
		appointment1.setHtmlBody(request.htmlBody);
		appointment1.setStartDate(startDate);
		appointment1.setEndDate(endDate);
		// appointment1.setRecurring(false);
		// appointment1.setHtmlBody(request.htmlBody);
		appointment1.setLocation(request.location);
		StringBuilder required_string = new StringBuilder();
		int i = 0;
		for (String to_email : request.requiredAttendees) {
			//System.out.println("to:" + to_email);
			if (i == 0) {
				required_string.append(to_email);
			} else {
				required_string.append(";" + to_email);
			}
			i++;
		}
		if (request.requiredAttendees.size() != 0) {
			appointment1.setTo(required_string.toString());
		}
		StringBuilder optional_string = new StringBuilder();
		i = 0;
		for (String cc_email : request.optionalAttendees) {
			//System.out.println("cc:" + cc_email);
			if (i == 0) {
				optional_string.append(cc_email);
			} else {
				optional_string.append(";" + cc_email);
			}
			i++;
		}
		if (request.optionalAttendees.size() != 0) {
			appointment1.setCc(optional_string.toString());
		}

		ArrayList<HashMap<String, String>> attachments = request.attachments;
		for (HashMap<String, String> entry : attachments) {
			String file_name = entry.get("name");
			String url = entry.get("url");
			InputStream input = new URL(url).openStream();
			appointment1.addAttachment(input, file_name);

		}
		// client.sendMeetingRequest(appointment1);
		MultiStatus multiStatus = client.createItem(appointment1);

		String appointmentUrl = multiStatus.getResponses()[0].getHRef();
		String res[]=new String[2];
		// Property LP = client.getProperty(appointmentUrl, "UID");
		Property propertyUUID = client.getProperty(appointmentUrl,
				AppointmentPropertyName.UID);
		String uid = propertyUUID.getValue();
		res[0]=appointmentUrl;
		res[1]=uid;
		LOG.info("New Event Created with url:{} and uuid: {}",appointmentUrl,uid);
		return res;
	}

	public ArrayList<EventJsonRequest> getEventsinfo(List<String> ids)
			throws WebdavException, NotFoundException, MoreThanOneFound {
		ArrayList<EventJsonRequest> eventsList = new ArrayList<EventJsonRequest>();
		for (String id : ids) {
			//System.out.println("id: " + id);
			Mailbox myMailbox = client.getMailbox();
			List<PropertyName> propertyNames = HelperClass
					.getEventAppointmentPropertyNames();

			Select select = new Select(propertyNames);
			From from = new From(myMailbox.getCalendar());
			Where where = new Where();

			Condition condition1 = new Condition(
					MessagePropertyName.CONTENT_CLASS, Operator.EQUALS,
					ContentClassType.APPOINTMENT);

			Condition condition2 = new Condition(AppointmentPropertyName.UID,
					Operator.EQUALS, id);

			where.add(condition1);
			where.add(LogicalOperator.AND);
			where.add(condition2);

			SqlQuery sqlQuery = new SqlQuery(select, from, where);
			MultiStatus multiStatus = client.search(sqlQuery);

			SearchResult searchResult = new SearchResult(multiStatus,
					propertyNames);
			SearchResultRecord[] allRecords = searchResult.getRecords();
			if (allRecords.length == 0) {
				throw new NotFoundException("this message does not exist");
			} else if (allRecords.length >= 2) {
				throw new MoreThanOneFound(
						"this uuid is associated with more than one message");
			} else {
				SearchResultRecord rr = allRecords[0];
				Property[] property = allRecords[0].getProperties();
				// System.out.println(property[1].getName());
				ArrayList<HashMap<String, String>> attachments = getAttchementInfo(rr
						.getUrl());
				EventJsonRequest ej = HelperClass
						.getEventBodyFromProperty(property);
				ej.attachments = attachments;
				eventsList.add(ej);
			}

		}
		return eventsList;

	}

	public ArrayList<EventJsonRequest> getEventsinfoLike(List<String> ids)
			throws WebdavException, NotFoundException, MoreThanOneFound {
		ArrayList<EventJsonRequest> eventsList = new ArrayList<EventJsonRequest>();
		LOG.info("fetch events information webdav method called");
		for (String id : ids) {
			LOG.info("fetch events information webdav  for uuid:{}",id);
			Mailbox myMailbox = client.getMailbox();
			List<PropertyName> propertyNames = HelperClass
					.getEventAppointmentPropertyNames();

			Select select = new Select(propertyNames);
			From from = new From(myMailbox.getCalendar());
			Where where = new Where();

			Condition condition1 = new Condition(
					MessagePropertyName.CONTENT_CLASS, Operator.EQUALS,
					ContentClassType.APPOINTMENT);

			Condition condition2 = new Condition(AppointmentPropertyName.UID,
					Operator.EQUALS, id);

			where.add(condition1);
			where.add(LogicalOperator.AND);
			where.add(condition2);

			SqlQuery sqlQuery = new SqlQuery(select, from, where);
			MultiStatus multiStatus = client.search(sqlQuery);

			SearchResult searchResult = new SearchResult(multiStatus,
					propertyNames);
			SearchResultRecord[] allRecords = searchResult.getRecords();
			if (allRecords.length == 0) {
				throw new NotFoundException("this message does not exist");
			} else if (allRecords.length >= 2) {
				LOG.info(
						"getEventsLike  uuid: {} is associated with more than one email urls 1. {}   2. {} ",
						id, allRecords[0].getUrl(), allRecords[1].getUrl());

				// System.in.read();
				int len = allRecords.length;
				int flag = 0;
				for (int k = 0; k < len; k++) {
					// LOG.info("fetchEmail  more than one found check which is right one ",uid));
					LOG.info(
							"getEventsLike check for more tha one   uid: {}  has email url is: {} ",
							id, allRecords[0].getUrl());
					// LOG.info("fetchEmail  uid: {}  has email url is: {} ",uid,allRecords[0].getUrl());
					SearchResultRecord rr = allRecords[k];
					Property[] property = allRecords[k].getProperties();
					EventJsonRequest ej = HelperClass
							.getEventBodyFromProperty(property);
					if (ej.id.equals(id)) {
						flag++;
						ArrayList<HashMap<String, String>> attachments = getAttchementInfo(rr
								.getUrl());
						ej.attachments = attachments;
						eventsList.add(ej);
					}
				}
				if (flag >= 2) {

					// System.out.println(property[1].getName());
					throw new MoreThanOneFound(
							"this uuid: {} is associated with more than one events"
									+ id);
				}
			} else {
				SearchResultRecord rr = allRecords[0];
				Property[] property = allRecords[0].getProperties();
				ArrayList<HashMap<String, String>> attachments = getAttchementInfo(rr
						.getUrl());

				EventJsonRequest ej = HelperClass
						.getEventBodyFromProperty(property);
				ej.attachments = attachments;
				eventsList.add(ej);
			}

		}
		return eventsList;

	}

	public HashMap<String, String> attchment(String uid, String attachmentUrl)
			throws WebdavException, NotFoundException {
		//System.out.println("attchment called");
		HashMap<String, String> tt = getAttachmentFromUUID(uid, uid,
				attachmentUrl);
		if (tt.isEmpty()) {
			throw new NotFoundException("no exits");
		} else {
			return tt;
		}
	}

	public HashMap<String, String> getAttachmentFromUUID(String type,
			String uid, String attachmentUrl) throws WebdavException,
			NotFoundException {
		//System.out.println("getattchmentfromuuid called");
		LOG.info("getAttachmentFromUUID called ");
		Mailbox myMailbox = client.getMailbox(uri + "/" + username);

		// PropertyName customPropertyName1 = new
		// PropertyName(CREATED_ID_PROPERTY_DEF, "urn:schemas:mailheader:");//
		// user defined property
		List<PropertyName> propertyNames = new ArrayList<PropertyName>();

		// Condition condition1 ;
		// Like condition2;
		Select select = null;
		From from = null;
		From from2 = null;
		Where where = null;
		/*
		 * if(type.equalsIgnoreCase("email")){
		 * propertyNames.add(MessagePropertyName.UID); select = new
		 * Select(propertyNames); from = new From(myMailbox.getSentItems());
		 * Condition condition1 = new
		 * Condition(MessagePropertyName.CONTENT_CLASS, Operator.EQUALS,
		 * ContentClassType.MESSAGE); Like condition2 = new
		 * Like(MessagePropertyName.UID, uid); where = new Where();
		 * where.add(condition1); where.add(LogicalOperator.AND);
		 * where.add(condition2);
		 * 
		 * } else if(type.equalsIgnoreCase("event")) { select = new
		 * Select(propertyNames); from = new From(myMailbox.getInbox());
		 * propertyNames.add(AppointmentPropertyName.UID); Condition condition1
		 * = new Condition(AppointmentPropertyName.CONTENT_CLASS,
		 * Operator.EQUALS, ContentClassType.APPOINTMENT); Like condition2 = new
		 * Like(AppointmentPropertyName.UID, uid); where = new Where();
		 * where.add(condition1); where.add(LogicalOperator.AND);
		 * where.add(condition2); }
		 */

		propertyNames.add(MessagePropertyName.UID);
		select = new Select(propertyNames);
		from = new From(myMailbox.getCalendar());
		Condition condition1 = new Condition(
				AppointmentPropertyName.CONTENT_CLASS, Operator.EQUALS,
				ContentClassType.APPOINTMENT);
		// Like condition1 =new Like(MessagePropertyName.UID, uid);
		Like condition2 = new Like(AppointmentPropertyName.UID, uid);
		where = new Where();
		where.add(condition1);
		where.add(LogicalOperator.OR);
		where.add(condition2);
		// SqlQuery ss=new SqlQuery(select, from, where)
		SqlQuery sqlQuery = new SqlQuery(select, from, where);
		MultiStatus multiStatus = client.search(sqlQuery);

		SearchResult searchResult = new SearchResult(multiStatus, propertyNames);
		SearchResultRecord[] allRecords = searchResult.getRecords();
		String uuid = "";
		if (allRecords.length == 0) {
			throw new NotFoundException("uuid does not exist");
		} else {
			SearchResultRecord rr = allRecords[0];
			String url = rr.getUrl();

			ArrayList<HashMap<String, String>> attachment_list = getAttchementInfo(url);
			// Property[] property = allRecords[0].getProperties();
			// uuid=property[0].getValue();
			int flag = 0;
			HashMap<String, String> attachment = new HashMap<String, String>();
			for (HashMap<String, String> entry : attachment_list) {
				String file_name = entry.get("attachment_fileName");
				String attch_url = entry.get("attachment_url");
				//System.out.println("att: " + attch_url);
				if (attch_url.equals(attachmentUrl)) {
					attachment = entry;
				//	System.out.println("found");
					return attachment;
				}
			}
		}
		return null;
	}

	public String getEmailUrl(String uuid) throws WebdavException,
			MoreThanOneFound, NotFoundException {
		Mailbox myMailbox = client.getMailbox(uri + "/" + username);
		//System.out.println("uuid" + uuid);
		// PropertyName customPropertyName1 = new
		// PropertyName(CREATED_ID_PROPERTY_DEF, "urn:schemas:mailheader:");//
		// user defined property
		List<PropertyName> propertyNames = new ArrayList<PropertyName>();

		// Condition condition1 ;
		// Like condition2;
		Select select = null;
		From from = null;
		From from2 = null;
		Where where = null;
		propertyNames.add(MessagePropertyName.UID);
		select = new Select(propertyNames);
		from = new From(myMailbox.getInbox());
		Condition condition1 = new Condition(MessagePropertyName.CONTENT_CLASS,
				Operator.EQUALS, ContentClassType.MESSAGE);
		Condition condition2 = new Condition(MessagePropertyName.UID,
				Operator.EQUALS, uuid);
		where = new Where();
		where.add(condition1);
		where.add(LogicalOperator.AND);
		where.add(condition2);

		SqlQuery sqlQuery = new SqlQuery(select, from, where);
		MultiStatus multiStatus = client.search(sqlQuery);

		SearchResult searchResult = new SearchResult(multiStatus, propertyNames);
		SearchResultRecord[] allRecords = searchResult.getRecords();
		String message_url = "";
		if (allRecords.length == 1) {

			SearchResultRecord rr = allRecords[0];
			message_url = rr.getUrl();

		} else if (allRecords.length == 0) {
			throw new NotFoundException("this uuid does not exist");
		} else {

			throw new MoreThanOneFound(
					"more than one message associated with this uuid");
		}
		return message_url;
	}

	public String getEmailUrlLike(String uuid) throws WebdavException,
			MoreThanOneFound, NotFoundException {
		Mailbox myMailbox = client.getMailbox(uri + "/" + username);
	//	System.out.println("uuid" + uuid);
		// PropertyName customPropertyName1 = new
		// PropertyName(CREATED_ID_PROPERTY_DEF, "urn:schemas:mailheader:");//
		// user defined property
		List<PropertyName> propertyNames = new ArrayList<PropertyName>();

		// Condition condition1 ;
		// Like condition2;
		Select select = null;
		From from = null;
		From from2 = null;
		Where where = null;
		propertyNames.add(MessagePropertyName.UID);
		select = new Select(propertyNames);
		from = new From(myMailbox.getInbox());
		Condition condition1 = new Condition(MessagePropertyName.CONTENT_CLASS,
				Operator.EQUALS, ContentClassType.MESSAGE);
		Like condition2 = new Like(MessagePropertyName.UID, uuid);
		where = new Where();
		where.add(condition1);
		where.add(LogicalOperator.AND);
		where.add(condition2);

		SqlQuery sqlQuery = new SqlQuery(select, from, where);
		MultiStatus multiStatus = client.search(sqlQuery);

		SearchResult searchResult = new SearchResult(multiStatus, propertyNames);
		SearchResultRecord[] allRecords = searchResult.getRecords();
		String message_url = "";
		if (allRecords.length == 1) {

			SearchResultRecord rr = allRecords[0];
			message_url = rr.getUrl();

		} else if (allRecords.length == 0) {
			throw new NotFoundException("this uuid does not exist");
		} else {
			int flag = 0;
			LOG.info(
					"getEmailUrlLike found more than one email associated with one uuid: {}",
					uuid, allRecords[0].getUrl(), allRecords[1].getUrl());
			for (int i = 0; i < allRecords.length; i++) {
				LOG.info(
						"getEmailUrlLike check for more tha one   uid: {}  has email url is: {} ",
						uuid, allRecords[i].getUrl());
				// LOG.info("fetchEmail  uid: {}  has email url is: {} ",uid,allRecords[0].getUrl());
				SearchResultRecord rr = allRecords[i];
				Property[] property = allRecords[i].getProperties();
				// System.out.println(property[1].getName());
				String uuid_check = property[0].getValue();
				if (uuid_check.equals(uuid)) {
					LOG.info(
							"getEmailUrlLike matched uuid is: {} for given uuid:{} and url is: {}",
							uuid_check, uuid,rr.getUrl());
					flag++;
					message_url = allRecords[i].getUrl();
				}
			}
			if (flag >= 2) {

				throw new MoreThanOneFound(
						"more than one email associated with this uuid: "
								+ uuid);
			}
		}
		return message_url;
	}

	public String getEventlUrl(String uuid) throws WebdavException,
			MoreThanOneFound, NotFoundException {
		Mailbox myMailbox = client.getMailbox(uri + "/" + username);
		List<PropertyName> propertyNames = new ArrayList<PropertyName>();
		Select select = null;
		From from = null;
		From from2 = null;
		Where where = null;
		propertyNames.add(MessagePropertyName.UID);
		select = new Select(propertyNames);
		from = new From(myMailbox.getCalendar());
		Condition condition1 = new Condition(
				AppointmentPropertyName.CONTENT_CLASS, Operator.EQUALS,
				ContentClassType.APPOINTMENT);
		Condition condition2 = new Condition(AppointmentPropertyName.UID,
				Operator.EQUALS, uuid);
		where = new Where();
		where.add(condition1);
		where.add(LogicalOperator.AND);
		where.add(condition2);

		SqlQuery sqlQuery = new SqlQuery(select, from, where);
		MultiStatus multiStatus = client.search(sqlQuery);

		SearchResult searchResult = new SearchResult(multiStatus, propertyNames);
		SearchResultRecord[] allRecords = searchResult.getRecords();
		String message_url = "";
		if (allRecords.length == 1) {

			SearchResultRecord rr = allRecords[0];
			message_url = rr.getUrl();

		} else if (allRecords.length == 0) {
			throw new NotFoundException("this uuid does not exist");
		} else {
			throw new MoreThanOneFound(
					"more than one message associated with this uuid");
		}
		return message_url;
	}

	public String getEventlUrlLike(String uuid) throws WebdavException,
			MoreThanOneFound, NotFoundException {
		Mailbox myMailbox = client.getMailbox(uri + "/" + username);
		List<PropertyName> propertyNames = new ArrayList<PropertyName>();
		Select select = null;
		From from = null;
		From from2 = null;
		Where where = null;
		propertyNames.add(MessagePropertyName.UID);
		select = new Select(propertyNames);
		from = new From(myMailbox.getCalendar());
		Condition condition1 = new Condition(
				AppointmentPropertyName.CONTENT_CLASS, Operator.EQUALS,
				ContentClassType.APPOINTMENT);
		Condition condition2 = new Condition(AppointmentPropertyName.UID,
				Operator.EQUALS, uuid);
		where = new Where();
		where.add(condition1);
		where.add(LogicalOperator.AND);
		where.add(condition2);

		SqlQuery sqlQuery = new SqlQuery(select, from, where);
		MultiStatus multiStatus = client.search(sqlQuery);

		SearchResult searchResult = new SearchResult(multiStatus, propertyNames);
		SearchResultRecord[] allRecords = searchResult.getRecords();
		String message_url = "";
		if (allRecords.length == 1) {

			SearchResultRecord rr = allRecords[0];
			message_url = rr.getUrl();
			LOG.info(
					"getEmailUrlLike single email url found:{} for uid:{}",
					rr.getUrl(),uuid);
			

		} else if (allRecords.length == 0) {
			throw new NotFoundException("this uuid does not exist");
		} else {
			int flag = 0;
			LOG.info(
					"getEmailUrlLike found more than one email associated with one uuid: {}",
					uuid, allRecords[0].getUrl(), allRecords[1].getUrl());
			for (int i = 0; i < allRecords.length; i++) {

				SearchResultRecord rr = allRecords[i];
				Property[] property = allRecords[i].getProperties();
				String uuid_check = property[0].getValue();
				LOG.info(
						"getEventUrlLike checking given uuid:{}  fetched uuid: {}",
						uuid, uuid_check);
				if (uuid_check.equals(uuid)) {
					flag++;
					LOG.info(
							"getEventUrlLike matched uuid is: {} for given uuid:{}",
							uuid_check, uuid);
					message_url = allRecords[i].getUrl();
				}
			}
			if (flag >= 2) {

				throw new MoreThanOneFound(
						"more than one email associated with this uuid: "
								+ uuid);
			}
		}
		return message_url;
	}

	public Attachment getAttachmentFromMessageURL_1(String owner_url,
			String attachment_url) throws WebdavException, NotFoundException, MoreThanOneFound {
		LOG.info("getAttachmentFromMessageURL_1 owner_url: {}  attachment_url: {}",owner_url,attachment_url);
		Attachment[] attachments = client.getAttachments(owner_url.trim());
		int i = 0;
		Attachment at = null;
		//System.out.println("attachment lenght"+attachments.length);
		for (int k = 0; k < attachments.length; k++) {
			//at = attachments[k];
			String atachmentUrl = attachments[k].getUrl();
			String atachmentFileName = attachments[k].getFileName();
			if (atachmentUrl.equals(attachment_url)) {
				at=attachments[k];
				//return at;
				i++;
			}
		}
		if (i==0) {
			throw new NotFoundException("attachment not found");
		}else if(i>=2)
		{
			throw new MoreThanOneFound("more than attachments found");
		}
		else
			return at;
		//return at;
	}
	
	public Attachment getAttachmentFromMessageURL(String owner_url,
			String attachment_url) throws WebdavException, NotFoundException {
		LOG.info("getAttachmentFromMessageURL owner_url: {}  attachment_url: {}",owner_url,attachment_url);
		Attachment[] attachments = client.getAttachments(owner_url);
		int i = 0;
		Attachment at = null;
		for (int k = 0; k < attachments.length; k++) {
			
			String atachmentUrl = attachments[k].getUrl();
			String atachmentFileName = attachments[k].getFileName();
			if (atachmentUrl.equals(attachment_url)) {
				i = 1;
			  at = attachments[k];
			}
		}
		if (at == null) {
			throw new NotFoundException("attachment not found");
		}
		return at;
	}


	public ArrayList<EmailInfo> fetchEmailsBasedOnUrls(List<String> urls)
			throws WebdavException, NotFoundException, MoreThanOneFound,
			IOException, ParseException {
		LOG.info("fetch email called with urls: {}", urls.toString());
		ArrayList<EmailInfo> emailsList = new ArrayList<EmailInfo>();
		for (String url : urls) {
			// url = URLEncoder.encode(url, "UTF-8");
			LOG.info("fetch email called with url: {}", url);
			ArrayList<HashMap<String, String>> attachments = getAttchementInfo(url);
			Message message = client.getMessage(url);
			if (message == null) {
				throw new NotFoundException(
						"corresonding email could not be found to this email");
			}
			EmailInfo email_body = HelperClass.getEmailBodyFromMessage(message);
			email_body.attachments = attachments;
			emailsList.add(email_body);
		}
		return emailsList;
	}

	public ArrayList<EventJsonRequest> getEventsinfoURL(List<String> urls)
			throws WebdavException, NotFoundException, ParseException {
		LOG.info("fetch event called with urls: {}", urls.toString());
		ArrayList<EventJsonRequest> eventList = new ArrayList<EventJsonRequest>();
		for (String url : urls) {
			// url = URLEncoder.encode(url, "UTF-8");
			LOG.info("fetch email called with url: {}", url);
			ArrayList<HashMap<String, String>> attachments = getAttchementInfo(url);
			Appointment appointment = client.getAppointment(url);
			if (appointment == null) {
				throw new NotFoundException(
						"corresonding email could not be found to this email");
			}
			EventJsonRequest ej = HelperClass
					.getEventbodyFromAppointment(appointment);
			ej.attachments = attachments;
			eventList.add(ej);
		}
		return eventList;
	}

	public ArrayList<HashMap<String, String>> searchSentItem(
			String sortdirection, int fieldcount, String inputoperator,
			String inputquery) throws WebdavException, ParseException {
		Mailbox myMailbox = client.getMailbox();
		List<PropertyName> propertyNames = new ArrayList<PropertyName>();
		propertyNames.add(MessagePropertyName.MESSAGE_ID);
		propertyNames.add(MessagePropertyName.UID);
		propertyNames.add(MessagePropertyName.DATE_RECEIVED);
		// propertyNames.add(MessagePropertyName.)
		Select select = new Select(propertyNames);
		From from = new From(myMailbox.getSentItems());
		Where where = new Where();

		// set datetime
		SimpleDateFormat formatter = new SimpleDateFormat(
				"yyyy-MM-dd'T'hh:mm:sss");
		String dateInString = inputquery;
		// System.out.println("fate in str" + dateInString);
		Date date = formatter.parse(dateInString);
		//System.out.println(date);

		// set conditions
		Condition condition1 = new Condition(MessagePropertyName.CONTENT_CLASS,
				Operator.EQUALS, ContentClassType.MESSAGE);
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
			order = new OrderBy(MessagePropertyName.DATE_RECEIVED, Order.ASC);
		else if (sortdirection.equals("Descending"))
			order = new OrderBy(MessagePropertyName.DATE_RECEIVED, Order.DESC);

		// set no of emails to be fetched
		String rr;
		if (fieldcount == 0) {
			rr = "0-" + String.valueOf(fieldcount);
		} else {
			rr = "0-" + String.valueOf(fieldcount - 1);
		}
		RowRange range = new RowRange();
		range.addRange(rr);

		SqlQuery sqlQuery = new SqlQuery(select, from, where, order);
		MultiStatus multiStatus = client.search(sqlQuery, range);

		SearchResult searchResult = new SearchResult(multiStatus, propertyNames);
		SearchResultRecord[] allRecords = searchResult.getRecords(); // email
																		// records

		ArrayList<HashMap<String, String>> myList = new ArrayList<HashMap<String, String>>();
		LOG.info("getEmail no of records found is: {}", allRecords.length);
		LOG.info("getEmail iterate over ");
		for (int i = 0; i < allRecords.length; i++) // iterate over email
													// records
		{
			// System.out.println("Message URL=" + allRecords[i].getUrl());
			HashMap<String, String> records = new HashMap<String, String>();
			Property[] property = allRecords[i].getProperties();
		
			LOG.info("getEmail message_url: ", allRecords[i].getUrl());
			records.put(property[1].getName(), property[1].getValue());
			LOG.info("getEmail uid: ", property[1].getValue());
			String datestring = property[2].getValue();
			datestring = HelperClass.getDateInFormat(datestring);
			records.put("dateTimeReceived", datestring);
			records.put("m_url", allRecords[i].getUrl());
			myList.add(records);

		}
		return myList;

	}

	public ArrayList<HashMap<String, String>> searchInboxSentitem(String sortdirection,
			int fieldcount, String inputoperator, String inputquery)
			throws WebdavException, ParseException {
		Mailbox myMailbox = client.getMailbox();
		String []search= new String[2];
		search[0]=myMailbox.getInbox();
		search[1]=myMailbox.getSentItems();
		//System.err.println(search[0]);
		//System.err.println(search[1]);
		List<PropertyName> propertyNames = new ArrayList<PropertyName>();
		propertyNames.add(MessagePropertyName.MESSAGE_ID);
		propertyNames.add(MessagePropertyName.UID);
		propertyNames.add(MessagePropertyName.DATE_RECEIVED);
		// propertyNames.add(MessagePropertyName.)
		Select select = new Select(propertyNames);
		From from = new From(myMailbox.getRoot(),Scope.DEEP);
		
		Where where = new Where();

		// set datetime
		SimpleDateFormat formatter = new SimpleDateFormat(
				"yyyy-MM-dd'T'hh:mm:sss");
		String dateInString = inputquery;
		// System.out.println("fate in str" + dateInString);
		Date date = formatter.parse(dateInString);
	//	System.out.println(date);

		// set conditions
		Condition condition1 = new Condition(MessagePropertyName.CONTENT_CLASS,
				Operator.EQUALS, ContentClassType.MESSAGE);
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
			order = new OrderBy(MessagePropertyName.DATE_RECEIVED, Order.ASC);
		else if (sortdirection.equals("Descending"))
			order = new OrderBy(MessagePropertyName.DATE_RECEIVED, Order.DESC);

		// set no of emails to be fetched
		String rr;
		if (fieldcount == 0) {
			rr = "0-" + String.valueOf(fieldcount);
		} else {
			rr = "0-" + String.valueOf(fieldcount - 1);
		}
		RowRange range = new RowRange();
		range.addRange(rr);

		SqlQuery sqlQuery = new SqlQuery(select, from, where, order);
		MultiStatus multiStatus = client.search(sqlQuery, range);

		SearchResult searchResult = new SearchResult(multiStatus, propertyNames);
		SearchResultRecord[] allRecords = searchResult.getRecords(); // email
																		// records

		ArrayList<HashMap<String, String>> myList = new ArrayList<HashMap<String, String>>();
		LOG.info("getEmail no of records found is: {}", allRecords.length);
		LOG.info("getEmail iterate over ");
		for (int i = 0; i < allRecords.length; i++) // iterate over email
													// records
		{
			// System.out.println("Message URL=" + allRecords[i].getUrl());
			HashMap<String, String> records = new HashMap<String, String>();
			Property[] property = allRecords[i].getProperties();
		
			LOG.info("getEmail message_url: ", allRecords[i].getUrl());
			records.put(property[1].getName(), property[1].getValue());
			LOG.info("getEmail uid: ", property[1].getValue());
			String datestring = property[2].getValue();
			datestring = HelperClass.getDateInFormat(datestring);
			records.put("dateTimeReceived", datestring);
			records.put("m_url", allRecords[i].getUrl());
			myList.add(records);

		}
		return myList;

	}

	public ArrayList<HashMap<String, String>> deepSearch(String sortdirection,
			int fieldcount, String inputoperator, String inputquery)
			throws WebdavException, ParseException {
		Mailbox myMailbox = client.getMailbox();
		List<PropertyName> propertyNames = new ArrayList<PropertyName>();
		propertyNames.add(MessagePropertyName.MESSAGE_ID);
		propertyNames.add(MessagePropertyName.UID);
		propertyNames.add(MessagePropertyName.DATE_RECEIVED);
		// propertyNames.add(MessagePropertyName.)
		Select select = new Select(propertyNames);
		From from = new From(myMailbox.getRoot(),Scope.DEEP);
		Where where = new Where();

		// set datetime
		SimpleDateFormat formatter = new SimpleDateFormat(
				"yyyy-MM-dd'T'hh:mm:sss");
		String dateInString = inputquery;
		// System.out.println("fate in str" + dateInString);
		Date date = formatter.parse(dateInString);
		//System.out.println(date);

		// set conditions
		Condition condition1 = new Condition(MessagePropertyName.CONTENT_CLASS,
				Operator.EQUALS, ContentClassType.MESSAGE);
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
			order = new OrderBy(MessagePropertyName.DATE_RECEIVED, Order.ASC);
		else if (sortdirection.equals("Descending"))
			order = new OrderBy(MessagePropertyName.DATE_RECEIVED, Order.DESC);

		// set no of emails to be fetched
		String rr;
		if (fieldcount == 0) {
			rr = "0-" + String.valueOf(fieldcount);
		} else {
			rr = "0-" + String.valueOf(fieldcount - 1);
		}
		RowRange range = new RowRange();
		range.addRange(rr);

		SqlQuery sqlQuery = new SqlQuery(select, from, where, order);
		MultiStatus multiStatus = client.search(sqlQuery, range);

		SearchResult searchResult = new SearchResult(multiStatus, propertyNames);
		SearchResultRecord[] allRecords = searchResult.getRecords(); // email
																		// records

		ArrayList<HashMap<String, String>> myList = new ArrayList<HashMap<String, String>>();
		LOG.info("getEmail no of records found is: {}", allRecords.length);
		LOG.info("getEmail iterate over ");
		for (int i = 0; i < allRecords.length; i++) // iterate over email
													// records
		{
			// System.out.println("Message URL=" + allRecords[i].getUrl());
			HashMap<String, String> records = new HashMap<String, String>();
			Property[] property = allRecords[i].getProperties();
		
			LOG.info("getEmail message_url: ", allRecords[i].getUrl());
			records.put(property[1].getName(), property[1].getValue());
			LOG.info("getEmail uid: ", property[1].getValue());
			String datestring = property[2].getValue();
			datestring = HelperClass.getDateInFormat(datestring);
			records.put("dateTimeReceived", datestring);
			records.put("m_url", allRecords[i].getUrl());
			myList.add(records);

		}
		return myList;

	}
}
