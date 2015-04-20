//IConstants holds fixed constant values
public interface IConstants {
	public final String WEBDAV_HOST_URL = "http://203.105.14.226/exchange/";
	public final String HOST = "exchangeURL";
	public final String USERNAME = "user";
	public final String PASSWORD = "password";
	public final String CREATED_ID_PROPERTY_DEF = "CREATED_ID_PROPERTY_DEF";
	public final String BAD_REQUEST = "BadRequest";
	public final String HEADER_ERROR = "HeaderPassedError";
	public final String INTERNAL_SERVER_ERROR = "InternalServerError";
	public final String MORE_THAN_ONE_FOUND = "MoreThanFoundMessage";
	public final String NOT_FOUND = "NoMessageFound";
	public final String INCORRECT_PARAMETER = "IncorrectRequestParameters";
	public final String INCORRECT_PARAMETER_ID = "Incorrect Request ID parameters";
	public final String INCORRECT_PARAMETER_URL = "Incorrect Request URL parameters";
	public final String SUCCESS = "Success";
	public final String PARSER_ERROR = "DateParserException";
	public final String WEB_DAV_EXCEPTION_MESSAGE = "WebDavException";
	public final String ATTACHMENT_NOT_FOUND_CODE = "Attchement not found/or not assoicated with this message";
	public final int INVALID_CREDENTIALS = 403;
	//public final int INVALID_CREDENTIALS = 403;
	public final int SUCCESS_CODE = 200;
	public final int BAD_REQUEST_CODE = 400;
	public final int INTERNAL_EXCEPTION_CODE = 500;
	public final int NOT_FOUND_CODE = 1001;
	public final int MORE_THAN_ONE_FOUND_CODE = 1002;
	public final int WEB_DAV_EXCEPTION = 1003;
	public final int INCORRECT_PARAMETER_CODE = 1005;
	public final int HEADER_CODE = 1004;
	public final int PARSER_ERROR_CODE = 1006;
	public final int INTERNAL_ERROR_CODE = 1010;
	public final int ATTACHMENT_NOT_FOUND=1007;
	public final String INBOX="Inbox";
	public final String SENT_ITEM="SentItem";
	public final String INBOX_SENTITEM="InboxSentItem";
	public final String DEEP_SEARCH="DeepSearch";
	
}
