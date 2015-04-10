import java.io.BufferedReader;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


//Hold request header data
public class RequestHeader implements IConstants {
	private String host;
	private String username;
	private String password;
	
	public RequestHeader()
	{
		
	}

	public RequestHeader(String host,String user,String password)
	{
		this.host=host;
		this.username=user;
		this.password=password;
	}
	
	public String  getHost()
	{
		return host;
	}
	
	public String  getUsername()
	{
		return username;
	}
	public String  getPassword()
	{
		
		return password;
	}
	
	public  void  setHost(String host)
	{
			this.host=host;
	}
	
	public void  setUsername(String user)
	{
		this.username=user;
	}
	public void  setPassword(String password)
	{
		this.password=password;
	}
	
	@Override
    public String toString(){
        return getHost() + ", "+getUsername()+ ", " +getPassword();
    }
	
	public void setHeader(HttpServletRequest request) throws HeaderNotFound 
	{
		int flag = 0;
		//RequestHeader rh = new RequestHeader();
		//rh.setHeader(request);
		Enumeration headerNames = request.getHeaderNames();

		while (headerNames.hasMoreElements()) {
			String key = (String) headerNames.nextElement();
			// System.out.println(key);
			//int flag=0;
			if (key.equals(HOST)) {
				flag++;
				//host=request.getHeader("http://"+key+"/exchange/");
				String hh=request.getHeader(key);
				host="http://"+hh+"/exchange/";
				System.out.println(hh);
				//System.out.println(request.getHeader(key));
				System.out.println(host);
			} else if (key.equals(USERNAME)) {
				flag++;
				username=request.getHeader(key);
			} else if (key.equals(PASSWORD)) {
				flag++;
				password=request.getHeader(key);
			}
			
		}
		if(flag!=3)
		{
			throw new HeaderNotFound("Reaquest header not found");
		}

	}
}
