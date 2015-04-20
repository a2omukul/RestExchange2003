import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EventJsonRequest {
	public String id;
	public String subject;
	public String textBody;
	public String htmlBody;
	public String start;
	public String end;
	public String location;
	public List<String> requiredAttendees;
	public List<String> optionalAttendees;
	public ArrayList<HashMap<String, String>> attachments;

	public EventJsonRequest() {

	}

	public EventJsonRequest(String id, String subject, String textBody,
			String htmlBody, String start, String end, String location,
			List<String> requiredAttendees, List<String> optionalAttendees,
			ArrayList<HashMap<String, String>> attachments) {
		super();
		this.id = id;
		this.subject = subject;
		this.textBody = textBody;
		this.htmlBody = htmlBody;
		this.start = start;
		this.end = end;
		this.location = location;
		this.requiredAttendees = requiredAttendees;
		//System.out.println(this.requiredAttendees);
		this.optionalAttendees = optionalAttendees;
		this.attachments = attachments;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getTextBody() {
		return textBody;
	}

	public void setTextBody(String textBody) {
		this.textBody = textBody;
	}

	public String getHtmlBody() {
		return htmlBody;
	}

	public void setHtmlBody(String htmlBody) {
		this.htmlBody = htmlBody;
	}

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public List<String> getRequiredAttendees() {
		return requiredAttendees;
	}

	public void setRequiredAttendees(List<String> requiredAttendees) {
		this.requiredAttendees = requiredAttendees;
	}

	public List<String> getOptionalAttendees() {
		return optionalAttendees;
	}

	public void setOptionalAttendees(List<String> optionalAttendees) {
		this.optionalAttendees = optionalAttendees;
	}

	public ArrayList<HashMap<String, String>> getAttachments() {
		return attachments;
	}

	public void setAttachments(ArrayList<HashMap<String, String>> attachments) {
		this.attachments = attachments;
	}

}
