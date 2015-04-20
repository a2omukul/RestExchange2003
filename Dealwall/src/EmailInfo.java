import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EmailInfo {
	public String id;
	public String from;
	public String subject;

	public List<String> to;
	public List<String> cc;
	public String dateTimeReceived;
	public String htmlBody;
	public String textBody;
	public ArrayList<HashMap<String, String>> attachments;

	public EmailInfo() {
		// just there, need by Jackson library
	}

	public EmailInfo(String id, String from, String subject, List<String> to,
			List<String> cc, String dateTimeReceived,
			String textbody, String htmlbody,
			ArrayList<HashMap<String, String>> attachments) {
		this.id = id;
		this.from = from;
	
		this.dateTimeReceived = dateTimeReceived;
		this.subject = subject;
		this.htmlBody = htmlbody;
		this.textBody = textbody;
		this.attachments = attachments;
		this.to = to;
		this.cc = cc;

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	

	

	public List<String> getTo() {
		return to;
	}

	public void setTo(List<String> to) {
		this.to = to;
	}

	public List<String> getCc() {
		return cc;
	}

	public void setCc(List<String> cc) {
		this.cc = cc;
	}

	public String getDateTimeReceived() {
		return dateTimeReceived;
	}

	public void setDateTimeReceived(String dateTimeReceived) {
		this.dateTimeReceived = dateTimeReceived;
	}

	public String getHtmlBody() {
		return htmlBody;
	}

	public void setHtmlBody(String htmlBody) {
		this.htmlBody = htmlBody;
	}

	public String getTextBody() {
		return textBody;
	}

	public void setTextBody(String textBody) {
		this.textBody = textBody;
	}

	public ArrayList<HashMap<String, String>> getAttachments() {
		return attachments;
	}

	public void setAttachments(ArrayList<HashMap<String, String>> attachments) {
		this.attachments = attachments;
	}

}
