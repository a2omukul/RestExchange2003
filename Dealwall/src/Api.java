//package login;

import java.awt.LinearGradientPaint;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.jaxrs.JsonParseExceptionMapper;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

import com.independentsoft.webdav.exchange.SearchResultRecord;
import com.independentsoft.webdav.exchange.WebdavException;

import java.util.UUID;


/**
 * Servlet implementation class Login
 */

public class Api extends HttpServlet implements IConstants {
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = Log.getLogger(Api.class);
	private Throwable ex;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Api() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
  
protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	  LOG.info("Got request from {} for {}",request.getRemoteAddr(), request.getRequestURL());
		String urlType=request.getPathInfo();  
		if (urlType.contains("login")) {        //call getLogin handler
			getLogin(request, response);
		} else if (urlType.contains("email")) {       //call Email handler
			getEmail(request, response);
		}else if (urlType.contains("events")) {
			getEvents(request, response);
		}

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		  
		String urlType = request.getPathInfo();
		if (urlType.contains("login")) {
			//getLogin(request, response);
		} else if (urlType.contains("email")) {    //call postEmail handler
			postEmail(request, response);
		}
		else{
			
		}
		
	}
	
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		  
		String urlType = request.getPathInfo();
		if (urlType.contains("email")) {
			//getLogin(request, response);
			putEmail(request, response);
		} else if (urlType.contains("events")) {    //call postEmail handler
			postEmail(request, response);
		}
		else{
			
		}
		
	}
	
	protected void getLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
		{
			//fetch request header
			try{
			RequestHeader rh = new RequestHeader();    
			rh.setHeader(request);
			//WebDavExchange 
			WebDavExchange wd = new WebDavExchange(rh.getHost(),rh.getUsername(),rh.getPassword());
			int status = wd.checkAuth();
			if (status == 1) {
						LoginResponse lg=new LoginResponse(200,"Success");
						ObjectMapper mapper = new ObjectMapper();
						response.setContentType("application/json");
						response.setStatus(200);
						PrintWriter out = response.getWriter();
						mapper.writeValue(out, lg);
			} else {
				ErrorResponse rr = new ErrorResponse(0,403,"InvalidCredentials","Unable to logon to the remote server as the credentials supplied are incorrect");
				response.setContentType("application/json");
				response.setStatus(403);
				ObjectMapper mapper_error = new ObjectMapper();
				PrintWriter out = response.getWriter();
				mapper_error.writeValue(out, rr);
			}
		}
		catch(HeaderNotFound ee){
			ee.printStackTrace();
			ErrorResponse rr = new ErrorResponse(0,400,"BadRequest","header not found");
			response.setContentType("application/json");
			response.setStatus(400);
			ObjectMapper mapper_error = new ObjectMapper();
			PrintWriter out = response.getWriter();
			mapper_error.writeValue(out, rr);
		}
		
		
			
	}
	
	protected void getEmail(HttpServletRequest request, HttpServletResponse response) throws ServletException,IOException {
		System.out.println("email post called");
		PrintWriter out = response.getWriter();
		response.setContentType("application/json");
		ObjectMapper mapper = new ObjectMapper();
		try{
				//int flag = 0;
				RequestHeader rh = new RequestHeader();
				rh.setHeader(request);
				String u_id=request.getParameter("id");
				if(u_id != null && !u_id.isEmpty()){
				   WebDavExchange wb=new WebDavExchange(rh.getHost(),rh.getUsername(), rh.getPassword());
				   EmailInfo em=wb.fetchParticularEmail(u_id);
				   mapper.writeValue(out, em);
					
				}else{
					      ErrorResponse rr = new ErrorResponse(0,400,"BadRequest","id not provided");
					      response.setStatus(400);
					     mapper.writeValue(out, rr);
				}
						
				//System.out.println(rh.toString());
		}catch(NotFoundException e){
			
			 ErrorResponse rr = new ErrorResponse(0,400,"BadRequest","email not found");
		     response.setStatus(400);
		    mapper.writeValue(out, rr);
		}catch (WebdavException e) {
			// TODO: handle exception
			 ErrorResponse rr = new ErrorResponse(0,500,"InternalServerError","WebDavException:"+e.getMessage());
		     response.setStatus(500);
		     mapper.writeValue(out, rr);
		}
		catch (HeaderNotFound e) {
			// TODO: handle exception
			// ErrorResponse rr = new ErrorResponse(0,400,"InternalServerError","WebDavException:"+e.getMessage());
		     //response.setStatus(500);
		    // mapper.writeValue(out, rr);
		     ErrorResponse rr = new ErrorResponse(0,400,"BadRequest","header not found");
			response.setStatus(400);
			mapper.writeValue(out, rr);
		}catch (Exception e) {
			// TODO: handle exception
			ErrorResponse rr = new ErrorResponse(0,500,"InternalServerError","exception:"+e.getMessage());
		     response.setStatus(500);
		     mapper.writeValue(out, rr);
		}
	}
	
	protected void postEmail(HttpServletRequest request, HttpServletResponse response) throws ServletException, JsonParseException,IOException {
		System.out.println("email post called");
		try{
		//int flag = 0;
		RequestHeader rh = new RequestHeader();
		rh.setHeader(request);
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		//System.out.println(rh.toString());
		BufferedReader br = new BufferedReader(request.getReader());
		StringBuilder sb = new StringBuilder();
		String line;
	
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}
		System.out.println(sb.toString());
		String sortdirection;
		int fieldcount;
		String inputquery = "";
		String field;
		String inputoperator = "";
		ObjectMapper mapper = new ObjectMapper();
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode rootNode = objectMapper.readTree(sb.toString());
		JsonNode sdNode = rootNode.path("sortDirection");
		System.out.println("sortDirection = " + sdNode.asText());
		sortdirection = sdNode.asText();
		JsonNode fdNode = rootNode.path("fieldCount");
		System.out.println("fieldCount = " + fdNode.asInt());
		fieldcount = fdNode.asInt();
		JsonNode criterianode = rootNode.path("criteria");
		Iterator<JsonNode> elements = criterianode.iterator();
		System.out.println(elements.toString());
		while (elements.hasNext()) {
			JsonNode je = elements.next();
			JsonNode QU = je.path("query");
			inputquery = QU.asText();
			JsonNode op = je.path("operator");
			inputoperator = op.asText();
			JsonNode fieldnode = je.path("field");
			field = fieldnode.asText();
		}
		WebDavExchange wd = new WebDavExchange(rh.getHost(),rh.getUsername(),rh.getPassword());
		ArrayList<HashMap<String, String>> items=wd.getEmails(sortdirection, fieldcount, inputoperator, inputquery);
		int no_of_record=items.size();
		String id=UUID.randomUUID().toString();
		EmailGetResponse er=new EmailGetResponse(id, 0, no_of_record, no_of_record, items);
		ObjectMapper res_mapper=new ObjectMapper();
		 response.setStatus(200);
	     res_mapper.writeValue(out, er);
		
		}
		catch(WebdavException we)
		{
			we.printStackTrace();
			ErrorResponse rr = new ErrorResponse(0,400,"InternalServerError","WebDavException:"+we.getMessage());
			response.setStatus(400);
			ObjectMapper mapper_error = new ObjectMapper();
			PrintWriter out = response.getWriter();
			mapper_error.writeValue(out, rr);
			
		}
		catch(ParseException pe)
		{
			pe.printStackTrace();
			ErrorResponse rr = new ErrorResponse(0,400,"BadRequest","Parser Error");
			response.setStatus(400);
			ObjectMapper mapper_error = new ObjectMapper();
			PrintWriter out = response.getWriter();
			mapper_error.writeValue(out, rr);
		}
		catch (JsonProcessingException je) {
			// TODO: handle exception
			ErrorResponse rr = new ErrorResponse(0,400,"BadRequest","Json Error");
			response.setStatus(400);
			ObjectMapper mapper_error = new ObjectMapper();
			PrintWriter out = response.getWriter();
			mapper_error.writeValue(out, rr);
		}
		catch (HeaderNotFound e) {
			// TODO: handle exception
			// ErrorResponse rr = new ErrorResponse(0,400,"InternalServerError","WebDavException:"+e.getMessage());
		     //response.setStatus(500);
		    // mapper.writeValue(out, rr);
		     ErrorResponse rr = new ErrorResponse(0,400,"BadRequest","header not found");
			response.setStatus(400);
			ObjectMapper mapper_error = new ObjectMapper();
			PrintWriter out = response.getWriter();
			mapper_error.writeValue(out, rr);
		}
		catch (Exception e) {
			// TODO: handle exception
			ErrorResponse rr = new ErrorResponse(0,500,"InternalserverError","Json Error");
			response.setStatus(500);
			ObjectMapper mapper_error = new ObjectMapper();
			PrintWriter out = response.getWriter();
			mapper_error.writeValue(out, rr);
		}
	
	
	}
	protected void getEvents(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			try{
			Map<String, String[]> parameters= request.getParameterMap();
			System.out.println("get even called");
			if(parameters.containsKey("startDate")){
				String[] startDateList= parameters.get("startDate");
				String startDate=startDateList[0];
				System.out.println(startDate);
				RequestHeader rh=new RequestHeader();
				rh.setHeader(request);
				WebDavExchange wb=new WebDavExchange(rh.getHost(),rh.getUsername(), rh.getPassword());
				wb.getEvents(startDate);
			}else{
				ErrorResponse rr = new ErrorResponse(0,400,"BadRequest","Request format mismatch");
				response.setContentType("application/json");
				response.setStatus(400);
				ObjectMapper mapper_error = new ObjectMapper();
				PrintWriter out = response.getWriter();
				mapper_error.writeValue(out, rr);
				
			}
			}catch(HeaderNotFound e)
			{
				
			}
	}
	
	protected void putEmail(HttpServletRequest request, HttpServletResponse response) throws ServletException, JsonParseException,IOException {
		System.out.println("email put called");
		PrintWriter out = response.getWriter();
		response.setContentType("application/json");
		ObjectMapper mapper = new ObjectMapper();
		try{
				RequestHeader rh = new RequestHeader();
				rh.setHeader(request);
				String line;
				StringBuffer jd =new StringBuffer();
				 BufferedReader reader = request.getReader();
				    while ((line = reader.readLine()) != null)
				    		jd.append(line);
				System.out.println(jd.toString());
				EmailResponse emailReq = mapper.readValue(jd.toString(), EmailResponse.class);
				//System.out.println(emailReq.id+emailReq.from+emailReq.body+emailReq.subject);
				WebDavExchange we=new WebDavExchange(rh.getHost(),rh.getUsername(), rh.getPassword());
				String guid=UUID.randomUUID().toString();
				we.sendEmail(emailReq,guid);
				String uuid=we.search_email_uuid(guid);
				 response.setStatus(200);
				 SuccesResponse res=new SuccesResponse(200,"success",uuid);
				 mapper.writeValue(out, res);
				
			}catch(WebdavException | IOException  | InterruptedException  ex){
				ex.printStackTrace();
				ErrorResponse rr = new ErrorResponse(0,500,"InternalServerError","exception:"+ex.getMessage());
				response.setStatus(500);
				mapper.writeValue(out, rr);
			}
		catch (HeaderNotFound e) {
			// TODO: handle exception
			// ErrorResponse rr = new ErrorResponse(0,400,"InternalServerError","WebDavException:"+e.getMessage());
		     //response.setStatus(500);
		    // mapper.writeValue(out, rr);
		     ErrorResponse rr = new ErrorResponse(0,400,"BadRequest","header not found");
			response.setStatus(400);
			mapper.writeValue(out, rr);
		}
			
		}



}
