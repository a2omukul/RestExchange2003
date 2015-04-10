
public class ErrorResponse {
	 public int code;
	 public int httpCode;
	 public String message;
	 public String messageDetail;
	 public ErrorResponse(int code,int httpCode,String message, String messageDetails)
	 {
		 this.code=code;
		 this.httpCode=httpCode;
		 this.message=message;
		 this.messageDetail=messageDetails;
		 
	 }
}
