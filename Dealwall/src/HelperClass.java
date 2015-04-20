import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.independentsoft.webdav.exchange.Appointment;
import com.independentsoft.webdav.exchange.Message;
import com.independentsoft.webdav.exchange.MessagePropertyName;
import com.independentsoft.webdav.exchange.AppointmentPropertyName;
import com.independentsoft.webdav.exchange.PropertyName;
import com.independentsoft.webdav.exchange.Property;

public class HelperClass {

	public HelperClass() {
		// TODO Auto-generated constructor stub
	}

	public static String formString(List<String> inputList, String str) {
		int n = inputList.size();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < n; i++) {
			if (i == n - 1) {
				sb.append(inputList.get(i));
			} else
				sb.append(inputList.get(i) + str);
		}
		return sb.toString();
	}

	public static String getDateInFormat(String dd) {
		// System.out.println("date: "+dd);
		if (dd.isEmpty() || dd == "" || dd == null) {
			return dd;
		} else {
			String[] ddd = dd.split("\\.");

			for (String d : ddd) {
				// System.out.println(d);
			}
			// System.out.println(ddd.toString());
			String new_Date = ddd[0] + "+00:00";

			return new_Date;
		}

	}

	public static String formatDate(Date dd) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat(
				"yyyy-MM-dd'T'hh:mm:sss");
		// String dateInString = dd;
		// System.out.println("fate in str" + dateInString);
		String dsd = formatter.format(dd);
		// System.out.println(dsd);
		return dsd.toString();

	}

	public static void setEmailBody() {

	}

	public static List<PropertyName> getEmailMessagePropertyNamesForEmailFetch() {
		List<PropertyName> propertyNames = new ArrayList<PropertyName>();
		propertyNames.add(MessagePropertyName.SUBJECT);
		propertyNames.add(MessagePropertyName.BODY);
		propertyNames.add(MessagePropertyName.FROM_EMAIL);
		// propertyNames.add(MessagePropertyName.TO);
		// propertyNames.add(MessagePropertyName.CC);
		propertyNames.add(MessagePropertyName.DISPLAY_TO);
		propertyNames.add(MessagePropertyName.DISPLAY_CC);
		propertyNames.add(MessagePropertyName.TEXT_DESCRIPTION);
		propertyNames.add(MessagePropertyName.HAS_ATTACHMENT);
		propertyNames.add(MessagePropertyName.HTML_DESCRIPTION);
		// propertyNames.add(MessagePropertyName.ATTACHMENT_FILE_NAME);
		propertyNames.add(MessagePropertyName.DATE_RECEIVED);
		propertyNames.add(MessagePropertyName.UID);
		// propertyNames.add(MessagePropertyName.);
		return propertyNames;
	}

	public static EmailInfo getEmailBodyFromProperty(Property[] property) {
		String subject = property[0].getValue().trim();
		// String body = property[1].getValue().trim();
		String from_email = property[2].getValue().trim();
		String to_str = property[3].getValue().trim();
		List<String> to = Arrays.asList(to_str.split(";"));
		String cc_str = property[4].getValue().trim();
		List<String> cc = Arrays.asList(cc_str.split(";"));
		String textd = property[5].getValue().trim();
		String htmld = property[7].getValue().trim();
		String dater = property[8].getValue().trim();
		// System.out.println("date:" + dater);
		dater = HelperClass.getDateInFormat(dater);
		String id = property[9].getValue().trim();
		EmailInfo email_body = new EmailInfo(id, from_email, subject, to, cc,
				dater, textd, htmld, null);
		return email_body;
	}

	public static EmailInfo getEmailBodyFromMessage(Message message)
			throws ParseException {
		String id = message.getUID();
		String from_email = message.getFromEmail();
		String subject = message.getSubject();
		String to_str = message.getTo();
		List<String> to = Arrays.asList(to_str.split(";"));
		String cc_str = message.getCc();
		List<String> cc = Arrays.asList(to_str.split(";"));
		// String body=message.getBody();
		String textd = message.getTextDescription();
		String htmld = message.getHtmlDescription();
		Date dater = message.getDateReceived();

		String dd = HelperClass.formatDate(dater);
		dd = HelperClass.getDateInFormat(dd);
		//System.out.println(dd);
		EmailInfo email_body = new EmailInfo(id, from_email, subject, to, cc,
				dd, textd, htmld, null);
		return email_body;
	}

	public static List<PropertyName> getEventAppointmentPropertyNames() {
		List<PropertyName> propertyNames = new ArrayList<PropertyName>();
		propertyNames.add(AppointmentPropertyName.SUBJECT);
		propertyNames.add(AppointmentPropertyName.BODY);
		// propertyNames.add(AppointmentPropertyName.TO);
		// propertyNames.add(AppointmentPropertyName.CC);
		propertyNames.add(MessagePropertyName.DISPLAY_TO);
		propertyNames.add(MessagePropertyName.DISPLAY_CC);
		propertyNames.add(AppointmentPropertyName.HTML_DESCRIPTION);
		propertyNames.add(AppointmentPropertyName.START_DATE);
		propertyNames.add(AppointmentPropertyName.END_DATE);
		propertyNames.add(AppointmentPropertyName.LOCATION);
		propertyNames.add(AppointmentPropertyName.UID);
		propertyNames.add(AppointmentPropertyName.ALL_ATTENDEES);
		propertyNames.add(AppointmentPropertyName.ID);
		propertyNames.add(AppointmentPropertyName.CREATED);
		propertyNames.add(AppointmentPropertyName.CREATION_DATE);
		// propertyNames.add(AppointmentPropertyName.);
		return propertyNames;
	}

	public static EventJsonRequest getEventBodyFromProperty(Property[] property) {

		String subject = property[0].getValue().trim();
		String body = property[1].getValue().trim();
		String htmld = property[4].getValue().trim();
		String to_str = property[2].getValue().trim();
		//System.out.println("to: " + to_str);
		List<String> requiredAttendees = Arrays.asList(to_str.split(","));
		for (String ss : requiredAttendees) {
			//System.out.println(ss);
		}
		//System.out.println(requiredAttendees.toString());
		String cc_str = property[3].getValue().trim();
		//System.out.println("cc: " + cc_str);
		List<String> optionalAttendees = Arrays.asList(cc_str.split(","));
		String startDate = property[5].getValue().trim();
		startDate = HelperClass.getDateInFormat(startDate);
		String endDate = property[6].getValue().trim();
		endDate = HelperClass.getDateInFormat(endDate);
		String location = property[7].getValue().trim();
		String uid = property[8].getValue().trim();
		//System.out.println("uid: " + uid);
		String all = property[9].getValue().trim();
	//	System.out.println("all: " + all);
		String idd = property[10].getValue().trim();
		//System.out.println("idd: " + idd);
	//	System.out.println("created: "+property[11].getValue());
	//	System.out.println("created: "+property[12].getValue());
		EventJsonRequest ej = new EventJsonRequest(uid, subject, body, htmld,
				startDate, endDate, location, requiredAttendees,
				optionalAttendees, null);
		return ej;
	}

	public static EventJsonRequest getEventbodyFromAppointment(
			Appointment appointment) throws ParseException {
		String subject = appointment.getSubject();
		String body = appointment.getBody();
		String htmld = appointment.getHtmlBody();
		String to_str = appointment.getTo();
		//System.out.println("to: " + to_str);
		List<String> requiredAttendees = Arrays.asList(to_str.split(","));
		for (String ss : requiredAttendees) {
			//System.out.println(ss);
		}
		//System.out.println(requiredAttendees.toString());
		String cc_str = appointment.getCc();
		//System.out.println("cc: " + cc_str);
		List<String> optionalAttendees = Arrays.asList(cc_str.split(","));
		Date startDate = appointment.getStartDate();
		String s_date = HelperClass.formatDate(startDate);
		s_date = HelperClass.getDateInFormat(s_date);
		Date endDate = appointment.getEndDate();
		String e_date = HelperClass.formatDate(endDate);
		e_date = HelperClass.getDateInFormat(e_date);
		String location = appointment.getLocation();
		String uid = appointment.getUID();
		//System.out.println("uid: " + uid);
		EventJsonRequest ej = new EventJsonRequest(uid, subject, body, htmld,
				s_date, e_date, location, requiredAttendees, optionalAttendees,
				null);
		return ej;

	}

}
