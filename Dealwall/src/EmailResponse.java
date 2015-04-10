import java.util.ArrayList;
import java.util.HashMap;


public class EmailResponse {
	public String id;
	public String from;
	public String subject;
	public String body;
	public ArrayList<String> to;
	public ArrayList<String> cc;
	public String dateTimeReceived;
	public String htmlBody;
	public String textBody;
	public ArrayList<HashMap<String, String>> attachments;
	 public EmailResponse() {
	        //just there, need by Jackson library
	    }
	public EmailResponse(String id, String from, String subject,ArrayList<String>to,ArrayList<String> cc,String body,String dateTimeReceived,String textbody, String htmlbody,ArrayList<HashMap<String, String>> attachments)
	{
		this.id=id;
		this.from=from;
		this.body=body;
		this.dateTimeReceived=dateTimeReceived;
		this.subject=subject;
		this.htmlBody=htmlbody;
		this.textBody=textbody;
		this.attachments=attachments;
		this.to=to;
		this.cc=cc;
		
	}
}
