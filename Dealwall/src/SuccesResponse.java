public class SuccesResponse {
	/**
 * 
 */
	int status_code;
	public String message;
	public String UUID;
	public String m_url;

	public SuccesResponse() {
	}

	public SuccesResponse(int id, String msg, String UUID,String url) {
		this.status_code = id;
		this.message = msg;
		this.UUID = UUID;
		this.m_url=url;
	}

	public int getStatus_code() {
		return status_code;
	}

	public void setStatus_code(int status_code) {
		this.status_code = status_code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}

class LoginResponse {
	/**
 * 
 */
	int status_code;
	public String message;

	// public String UUID;

	public LoginResponse() {
	}

	public LoginResponse(int id, String msg) {
		this.status_code = id;
		this.message = msg;
		// this.UUID=UUID;
	}

	public int getStatus_code() {
		return status_code;
	}

	public void setStatus_code(int status_code) {
		this.status_code = status_code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
