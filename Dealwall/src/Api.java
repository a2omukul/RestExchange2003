//package login;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

import com.independentsoft.webdav.exchange.WebdavException;

import java.util.UUID;

/**
 * Servlet implementation class Login
 */

public class Api extends HttpServlet implements IConstants {
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = Log.getLogger(Api.class);

	// private Throwable ex;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Api() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		LOG.info("Got GET request from {} for {}", request.getRemoteAddr(),
				request.getRequestURL());
		String urlType = request.getPathInfo();
		if (urlType.contains("login")) { // call getLogin handler
			LOG.info("Get Login called api with url: {}",
					request.getRequestURL());
			getLogin(request, response);
		} else if (urlType.contains("email")) { // call Email handler
			LOG.info("Get email called api with url: {}",
					request.getRequestURI());
			getEmail(request, response);
		} else if (urlType.contains("events")) {
			LOG.info("Get events called api with url: {}",
					request.getRequestURI());
			getEvents(request, response);
		} else if (urlType.contains("attachments")) {
			LOG.info("Get attachment api called with url: {}",
					request.getRequestURI());
			getAttchmentsUrl(request, response);
		}

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		LOG.info("Got request from {} for {}", request.getRemoteAddr(),
				request.getRequestURL());
		String urlType = request.getPathInfo();
		if (urlType.contains("email")) {
			LOG.info("Post email api called with url: {}",
					request.getRequestURL());
			postEmail(request, response);
		} else if (urlType.contains("getEmailFormInboxSentItems")) {
			LOG.info("Get getEmailFormInboxSentItems api called with url: {}",
					request.getRequestURI());
			getEmailFormInboxSentItems(request, response);
		} else {
		}
	}

	protected void doPut(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		LOG.info("Got request from {} for {}", request.getRemoteAddr(),
				request.getRequestURL());
		String urlType = request.getPathInfo();
		if (urlType.contains("email")) {
			// getLogin(request, response);
			LOG.info("Put email api called with url: {}",
					request.getRequestURL());
			putEmail(request, response);
		} else if (urlType.contains("events")) {
			LOG.info("Put events api called with url: {}",
					request.getRequestURL());// call postEmail handler
			putEvent(request, response);
		} else {

		}

	}

	protected void getLogin(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// fetch request header
		try {

			RequestHeader rh = new RequestHeader();
			rh.setHeader(request);
			LOG.info(
					"getLogin servlet method called with request headers exchangeUrl:{}  user:{}  password:{}",
					rh.getHost(), rh.getUsername(), rh.getPassword());
			// WebDavExchange
			WebDavExchange wd = new WebDavExchange(rh.getHost(),
					rh.getUsername(), rh.getPassword());
			int status = wd.checkAuth();
			if (status == 1) {
				LoginResponse lg = new LoginResponse(SUCCESS_CODE, SUCCESS);
				ObjectMapper mapper = new ObjectMapper();
				response.setContentType("application/json");
				response.setStatus(SUCCESS_CODE);
				PrintWriter out = response.getWriter();
				mapper.writeValue(out, lg);
				LOG.info("Exchange server login success");
			} else {
				ErrorResponse rr = new ErrorResponse(
						0,
						INVALID_CREDENTIALS,
						"InvalidCredentials",
						"Unable to logon to the remote server as the credentials supplied are incorrect");
				response.setContentType("application/json");
				response.setStatus(INVALID_CREDENTIALS);
				ObjectMapper mapper_error = new ObjectMapper();
				PrintWriter out = response.getWriter();
				mapper_error.writeValue(out, rr);
				LOG.warn("Error in getLogin method due invalid credentials");
			}
		} catch (HeaderNotFound ee) {
			ee.printStackTrace();
			ErrorResponse rr = new ErrorResponse(HEADER_CODE, BAD_REQUEST_CODE,
					HEADER_ERROR, ee.getMessage());
			response.setContentType("application/json");
			response.setStatus(HEADER_CODE);
			ObjectMapper mapper_error = new ObjectMapper();
			PrintWriter out = response.getWriter();
			mapper_error.writeValue(out, rr);
			LOG.warn("Error in getLogin method due to request header");
		} catch (Exception e) {
			// TODO: handle exception
			ErrorResponse rr = new ErrorResponse(INTERNAL_ERROR_CODE,
					INTERNAL_EXCEPTION_CODE, INTERNAL_SERVER_ERROR,
					"exception:" + e.getMessage());
			response.setContentType("application/json");
			response.setStatus(INTERNAL_EXCEPTION_CODE);
			ObjectMapper mapper_error = new ObjectMapper();
			PrintWriter out = response.getWriter();
			mapper_error.writeValue(out, rr);
			LOG.warn(
					"Error in getLogin method due to internal server error: {}",
					e.getMessage());

		}

	}

	protected void getEmail(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// System.out.println("email post called");
		PrintWriter out = response.getWriter();
		response.setContentType("application/json");
		ObjectMapper mapper = new ObjectMapper();
		try {
			// int flag = 0;
			RequestHeader rh = new RequestHeader();
			rh.setHeader(request);
			LOG.info(
					"getEmail servlet method called with request headers exchangeUrl:{}  user:{}  password:{}",
					rh.getHost(), rh.getUsername(), rh.getPassword());
			Map<String, String[]> parameters = request.getParameterMap();

			// System.out.println(parameters.toString());
			// System.out.println(request.getQueryString());
			if (parameters.containsKey("id")) {
				String u_id = request.getParameter("id");
				LOG.info("getEmail id values: {}", u_id);
				if (u_id != null && !u_id.isEmpty()) {
					WebDavExchange wb = new WebDavExchange(rh.getHost(),
							rh.getUsername(), rh.getPassword());
					List<String> uidLists = Arrays.asList(u_id.split(","));
					LOG.info("getEmail id values as list: {}", uidLists);
					if (uidLists.contains(null) || uidLists.contains("")) {
						ErrorResponse rr = new ErrorResponse(
								INCORRECT_PARAMETER_CODE, BAD_REQUEST_CODE,
								INCORRECT_PARAMETER, INCORRECT_PARAMETER_ID);
						response.setStatus(400);
						mapper.writeValue(out, rr);
						LOG.warn("Error in getEmail method due to id values,id values should not contain empty string");
					} else {
						ArrayList<EmailInfo> emailList;
						emailList = wb.fetchEmailsLike(uidLists);
						mapper.writeValue(out, emailList);
						response.setStatus(SUCCESS_CODE);
						LOG.info("getEmail successfully executed");
					}
				} else {
					ErrorResponse rr = new ErrorResponse(
							INCORRECT_PARAMETER_CODE, BAD_REQUEST_CODE,
							INCORRECT_PARAMETER, "ID values empty");
					response.setStatus(BAD_REQUEST_CODE);
					mapper.writeValue(out, rr);
					LOG.warn("Error in getEmail method due to id values,id values should not contain empty string");
				}
			} else if (parameters.containsKey("url")) {

				String rrr = request.getQueryString();
				String[] rrrr = rrr.split("url=");
				if (rrrr.length == 1 || rrrr.length == 0) {
					ErrorResponse rr = new ErrorResponse(
							INCORRECT_PARAMETER_CODE, BAD_REQUEST_CODE,
							BAD_REQUEST, INCORRECT_PARAMETER_URL);
					response.setStatus(BAD_REQUEST_CODE);
					mapper.writeValue(out, rr);
					LOG.warn("Error in getEmail method due to id values,id values should not contain empty string");
				} else {
					String urls = rrrr[1];

					// System.out.println(urls);
					LOG.info("getEmail url values: {}", urls);
					if (urls != null && !urls.isEmpty()) {
						WebDavExchange wb = new WebDavExchange(rh.getHost(),
								rh.getUsername(), rh.getPassword());
						List<String> urlsList = Arrays.asList(urls.split(","));
						LOG.info("getEmail id values as list: {}", urlsList);
						if (urlsList.contains(null) || urlsList.contains("")) {
							ErrorResponse rr = new ErrorResponse(
									INCORRECT_PARAMETER_CODE, BAD_REQUEST_CODE,
									BAD_REQUEST, INCORRECT_PARAMETER_URL);
							response.setStatus(400);
							mapper.writeValue(out, rr);
							LOG.warn("Error in getEmail method due to id values,id values should not contain empty string");
						} else {
							ArrayList<EmailInfo> emailList;
							emailList = wb.fetchEmailsBasedOnUrls(urlsList);
							response.setStatus(SUCCESS_CODE);
							mapper.writeValue(out, emailList);
							LOG.info("getEmail successfully executed");
						}
					} else {
						ErrorResponse rr = new ErrorResponse(
								INCORRECT_PARAMETER_CODE, BAD_REQUEST_CODE,
								BAD_REQUEST, "ID values empty");
						response.setStatus(BAD_REQUEST_CODE);
						mapper.writeValue(out, rr);
						LOG.warn("Error in getEmail method due to id values,id values should not contain empty string");
					}
				}
			} else {
				ErrorResponse rr = new ErrorResponse(INCORRECT_PARAMETER_CODE,
						BAD_REQUEST_CODE, BAD_REQUEST, "provide either id/url");
				response.setStatus(BAD_REQUEST_CODE);
				mapper.writeValue(out, rr);
				LOG.warn("Error in getEmail method due to id values,id values should not contain empty string");
			}

			// System.out.println(rh.toString());
		} catch (NotFoundException e) {
			LOG.warn(
					"Error in getEmail method due to some uid may not be existing in exchange server Error: {}",
					e.getStackTrace());
			ErrorResponse rr = new ErrorResponse(NOT_FOUND_CODE,
					BAD_REQUEST_CODE, NOT_FOUND, e.getMessage());
			response.setStatus(BAD_REQUEST_CODE);
			mapper.writeValue(out, rr);

		} catch (WebdavException e) {
			// TODO: handle exception
			LOG.warn(
					"Error in getEmail method due to WebDavExchange library exception: {}",
					e.getStackTrace());
			ErrorResponse rr = new ErrorResponse(WEB_DAV_EXCEPTION,
					INTERNAL_EXCEPTION_CODE, INTERNAL_SERVER_ERROR,
					"WebDavException:" + e.getMessage());
			response.setStatus(INTERNAL_EXCEPTION_CODE);
			mapper.writeValue(out, rr);

		} catch (HeaderNotFound e) {
			ErrorResponse rr = new ErrorResponse(HEADER_CODE, BAD_REQUEST_CODE,
					HEADER_ERROR, "header not found");
			response.setStatus(BAD_REQUEST_CODE);
			mapper.writeValue(out, rr);
		} catch (MoreThanOneFound e) {
			LOG.warn(
					"Error in getEmail method due to some uid may be  associated with more than message exchange server Error: {}",
					e.getStackTrace());
			ErrorResponse rr = new ErrorResponse(MORE_THAN_ONE_FOUND_CODE,
					BAD_REQUEST_CODE, MORE_THAN_ONE_FOUND, "exception:"
							+ e.getMessage());
			response.setStatus(BAD_REQUEST_CODE);
			mapper.writeValue(out, rr);

		} catch (Exception e) {
			// TODO: handle exception
			LOG.warn("Error in getEmail method due exception:{}",
					e.getStackTrace());
			ErrorResponse rr = new ErrorResponse(INTERNAL_ERROR_CODE,
					INTERNAL_EXCEPTION_CODE, INTERNAL_SERVER_ERROR,
					"exception:" + e.getMessage());
			response.setStatus(500);
			mapper.writeValue(out, rr);
		}
	}

	protected void getEmailFormInboxSentItems(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// System.out.println("email post called");
	}

	protected void postEmail(HttpServletRequest request,
			HttpServletResponse response) throws ServletException,
			JsonParseException, IOException {
		// System.out.println("email post called");
		try {
			// int flag = 0;
			RequestHeader rh = new RequestHeader();
			rh.setHeader(request);
			LOG.info(
					"postEmail servlet method called with request headers exchangeUrl:{}  user:{}  password:{}",
					rh.getHost(), rh.getUsername(), rh.getPassword());
			response.setContentType("application/json");
			PrintWriter out = response.getWriter();
			// System.out.println(rh.toString());
			BufferedReader br = new BufferedReader(request.getReader());
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			LOG.info("post email request body: {}", sb.toString());
			String sortdirection;
			int fieldcount;
			String inputquery = "";
			String field;
			String inputoperator = "";
			ObjectMapper mapper = new ObjectMapper();
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode rootNode = objectMapper.readTree(sb.toString());
			JsonNode sdNode = rootNode.path("sortDirection");
			// System.out.println("sortDirection = " + sdNode.asText());
			sortdirection = sdNode.asText();
			JsonNode fdNode = rootNode.path("fieldCount");
			// System.out.println("fieldCount = " + fdNode.asInt());
			fieldcount = fdNode.asInt();
			JsonNode criterianode = rootNode.path("criteria");
			Iterator<JsonNode> elements = criterianode.iterator();
			while (elements.hasNext()) {
				JsonNode je = elements.next();
				JsonNode QU = je.path("query");
				inputquery = QU.asText();
				JsonNode op = je.path("operator");
				inputoperator = op.asText();
				JsonNode fieldnode = je.path("field");
				field = fieldnode.asText();
			}

			String type = request.getParameter("type");

			ArrayList<HashMap<String, String>> items = null;
			WebDavExchange wd = new WebDavExchange(rh.getHost(),
					rh.getUsername(), rh.getPassword());
			if (type == null || type.isEmpty()) {
				ErrorResponse rr = new ErrorResponse(
						INCORRECT_PARAMETER_CODE, BAD_REQUEST_CODE,
						INCORRECT_PARAMETER,
						"please provide search type such /email?type=Inbox");
				response.setStatus(BAD_REQUEST_CODE);
				ObjectMapper mapper_error = new ObjectMapper();
				// PrintWriter out = response.getWriter();
				mapper_error.writeValue(out, rr);
			} else {
				if (type.equals(INBOX)) {
					LOG.info("post email INBOX search strategy");
					items = wd.getEmails(sortdirection, fieldcount,
							inputoperator, inputquery);
				} else if (type.equals(SENT_ITEM)) {
					LOG.info("post email SENT_ITEM search strategy called");
					items = wd.searchSentItem(sortdirection, fieldcount,
							inputoperator, inputquery);
				} else if (type.equals(DEEP_SEARCH)) {
					LOG.info("post email DEEP_SEARCH search strategy called");
					items = wd.deepSearch(sortdirection, fieldcount,
							inputoperator, inputquery);
				} else if (type.equals(INBOX_SENTITEM)) {
					LOG.info("post email INBOX_SENTITEM search strategy called");
					ArrayList<HashMap<String, String>> item1;
					item1 = wd.getEmails(sortdirection, fieldcount,
							inputoperator, inputquery);
					ArrayList<HashMap<String, String>> item2;
					item2 = wd.searchSentItem(sortdirection, fieldcount,
							inputoperator, inputquery);
					ArrayList<HashMap<String, String>> items_s = new ArrayList<HashMap<String, String>>();
					items_s.addAll(item1);
					items_s.addAll(item2);
					items = items_s;

				} else {
					ErrorResponse rr = new ErrorResponse(
							INCORRECT_PARAMETER_CODE, BAD_REQUEST_CODE,
							INCORRECT_PARAMETER,
							"Request parameter type incorrect ");
					response.setStatus(BAD_REQUEST_CODE);
					ObjectMapper mapper_error = new ObjectMapper();
					// PrintWriter out = response.getWriter();
					mapper_error.writeValue(out, rr);
				}
			}
			int no_of_record = items.size();
			String id = UUID.randomUUID().toString();
			EmailGetResponse er = new EmailGetResponse(id, 0, no_of_record,
					no_of_record, items);
			ObjectMapper res_mapper = new ObjectMapper();
			response.setStatus(200);
			res_mapper.writeValue(out, er);

		} catch (WebdavException we) {
			we.printStackTrace();
			ErrorResponse rr = new ErrorResponse(WEB_DAV_EXCEPTION, 500,
					"InternalServerError", "WebDavException:" + we.getMessage());
			response.setStatus(500);
			ObjectMapper mapper_error = new ObjectMapper();
			PrintWriter out = response.getWriter();
			mapper_error.writeValue(out, rr);

		} catch (ParseException pe) {
			pe.printStackTrace();
			ErrorResponse rr = new ErrorResponse(PARSER_ERROR_CODE, 400,
					PARSER_ERROR, "Parser Error");
			response.setStatus(400);
			ObjectMapper mapper_error = new ObjectMapper();
			PrintWriter out = response.getWriter();
			mapper_error.writeValue(out, rr);
		} catch (JsonProcessingException je) {
			// TODO: handle exception
			ErrorResponse rr = new ErrorResponse(INTERNAL_ERROR_CODE, 400,
					"BadRequest", "Json Error");
			response.setStatus(400);
			ObjectMapper mapper_error = new ObjectMapper();
			PrintWriter out = response.getWriter();
			mapper_error.writeValue(out, rr);
		} catch (HeaderNotFound e) {
			// TODO: handle exception
			// ErrorResponse rr = new
			// ErrorResponse(0,400,"InternalServerError","WebDavException:"+e.getMessage());
			// response.setStatus(500);
			// mapper.writeValue(out, rr);
			ErrorResponse rr = new ErrorResponse(HEADER_CODE, 400,
					"BadRequest", "header not found");
			response.setStatus(400);
			ObjectMapper mapper_error = new ObjectMapper();
			PrintWriter out = response.getWriter();
			mapper_error.writeValue(out, rr);
		} catch (Exception e) {
			// TODO: handle exception
			LOG.info("exception:" + e.toString());
			ErrorResponse rr = new ErrorResponse(INTERNAL_ERROR_CODE, 500,
					INTERNAL_SERVER_ERROR, "exception:" + e.getMessage());
			response.setStatus(500);
			ObjectMapper mapper_error = new ObjectMapper();
			PrintWriter out = response.getWriter();
			mapper_error.writeValue(out, rr);
		}

	}

	protected void getAttchments(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String path = request.getQueryString();
		LOG.info("getAttachment called with path:{}", path);

		if (path.contains("attachmentURL")) {
			getAttchmentsUrl(request, response);
		}
		LOG.info("REQUESTED URL", request.getPathInfo());
		ObjectMapper mapper = new ObjectMapper();

		try {
			RequestHeader rh = new RequestHeader();
			rh.setHeader(request);
			String attachment_url = request.getParameter("id").trim();
			System.out.println("attachment_url" + attachment_url);
			String uuid = request.getParameter("ownerID");
			System.out.println("ownerID: " + uuid);
			int flag = 0;
			WebDavExchange wb = new WebDavExchange(rh.getHost(),
					rh.getUsername(), rh.getPassword());
			String email_url = wb.getEmailUrlLike(uuid);
			System.out.println("email_url: " + email_url);
			if (email_url.equals("") || email_url.isEmpty()) {
				String event_url = wb.getEventlUrlLike(uuid);
				System.out.println("event_url: " + event_url);
				if (event_url.equals("") || event_url.isEmpty()) {
					response.setContentType("application/json");
					PrintWriter out = response.getWriter();
					ErrorResponse rr = new ErrorResponse(
							INCORRECT_PARAMETER_CODE, BAD_REQUEST_CODE,
							INCORRECT_PARAMETER, "incorect request parameter");
					response.setStatus(BAD_REQUEST_CODE);
					mapper.writeValue(out, rr);
				} else {
					com.independentsoft.webdav.exchange.Attachment attachment = wb
							.getAttachmentFromMessageURL(event_url,
									attachment_url);
					String atachmentUrl = attachment.getUrl();
					String atachmentFileName = attachment.getFileName();
					String content_type = attachment.getMimeType();
					System.out.println("attachement file name: "
							+ atachmentFileName);
					long size = attachment.getSize();
					if (atachmentFileName == null) {
						atachmentFileName = atachmentUrl.substring(atachmentUrl
								.lastIndexOf("/") + 1);
					}
					OutputStream os = response.getOutputStream();
					wb.client.download(atachmentUrl, os);
					if (content_type == null) {
						response.setContentType("application/octet-stream");
					} else {
						response.setContentType(content_type);
					}
					response.addHeader("Content-Disposition",
							"attachment; filename=" + atachmentFileName);
					// Copy the contents of the file to the output stream
					response.setContentLength((int) size);

				}
			} else {
				com.independentsoft.webdav.exchange.Attachment attachment = wb
						.getAttachmentFromMessageURL(email_url, attachment_url);
				String atachmentUrl = attachment.getUrl();
				String atachmentFileName = attachment.getFileName();
				String content_type = attachment.getMimeType();
				System.out.println("attachement file name: "
						+ atachmentFileName);
				long size = attachment.getSize();
				if (atachmentFileName == null) {
					atachmentFileName = atachmentUrl.substring(atachmentUrl
							.lastIndexOf("/") + 1);
				}
				OutputStream os = response.getOutputStream();
				wb.client.download(atachmentUrl, os);
				if (content_type == null) {
					response.setContentType("application/octet-stream");
				} else {
					response.setContentType(content_type);
				}
				response.addHeader("Content-Disposition",
						"attachment; filename=" + atachmentFileName);
				response.setContentLength((int) size);

			}

		} catch (NotFoundException e) {
			e.printStackTrace();
			PrintWriter out = response.getWriter();
			response.setContentType("application/json");
			ErrorResponse rr = new ErrorResponse(ATTACHMENT_NOT_FOUND,
					BAD_REQUEST_CODE, ATTACHMENT_NOT_FOUND_CODE, e.getMessage());
			response.setStatus(400);
			mapper.writeValue(out, rr);
		} catch (MoreThanOneFound e) {
			e.printStackTrace();
			PrintWriter out = response.getWriter();
			response.setContentType("application/json");
			ErrorResponse rr = new ErrorResponse(1002, 500, "BadRequest",
					e.getMessage());
			response.setStatus(500);
			mapper.writeValue(out, rr);
		} catch (WebdavException e) {
			e.printStackTrace();
			PrintWriter out = response.getWriter();
			response.setContentType("application/json");
			ErrorResponse rr = new ErrorResponse(1005, 500, "BadRequest",
					e.getMessage());
			response.setStatus(500);
			mapper.writeValue(out, rr);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			PrintWriter out = response.getWriter();
			response.setContentType("application/json");
			ErrorResponse rr = new ErrorResponse(1010, 500,
					INTERNAL_SERVER_ERROR, "exception: " + e.getMessage());
			response.setStatus(500);
			mapper.writeValue(out, rr);
		}
	}

	protected void getAttchments_new(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		System.out.println("get attchment called");
		String path = request.getQueryString();
		ObjectMapper mapper = new ObjectMapper();
		try {
			RequestHeader rh = new RequestHeader();
			rh.setHeader(request);
			String attachmenturl = "";
			String messageurl = "";
			String messageid = "";
			String[] att_owner_url = path.split("&");
			WebDavExchange wb = new WebDavExchange(rh.getHost(),
					rh.getUsername(), rh.getPassword());
			if (path.contains("attachmentURL") || path.contains("id")) {
				if (path.contains(attachmenturl)) {
					String attachment_url = att_owner_url[0].trim();
					String[] attlist = attachment_url.split("attachmentURL=");
					attachmenturl = attlist[1].trim();
					String message_url = att_owner_url[1].trim();
					String[] messagesplit = message_url.split("ownerURL=");
					messageurl = messagesplit[1].trim();
				} else {
					String attachment_url = att_owner_url[0].trim();
					String[] attlist = attachment_url.split("id");
					attachmenturl = attlist[1].trim();
					String message_url = att_owner_url[1].trim();
					String[] messagesplit = message_url.split("ownerID=");
					messageid = messagesplit[1].trim();
					String email_url = wb.getEmailUrlLike(messageid);
					System.out.println("email_url: " + email_url);

				}

			} else {

			}

		} catch (NotFoundException e) {
			e.printStackTrace();
			PrintWriter out = response.getWriter();
			response.setContentType("application/json");
			ErrorResponse rr = new ErrorResponse(ATTACHMENT_NOT_FOUND,
					BAD_REQUEST_CODE, ATTACHMENT_NOT_FOUND_CODE, e.getMessage());
			response.setStatus(400);
			mapper.writeValue(out, rr);
		} catch (MoreThanOneFound e) {
			e.printStackTrace();
			PrintWriter out = response.getWriter();
			response.setContentType("application/json");
			ErrorResponse rr = new ErrorResponse(1002, 500, "BadRequest",
					e.getMessage());
			response.setStatus(500);
			mapper.writeValue(out, rr);
		} catch (WebdavException e) {
			e.printStackTrace();
			PrintWriter out = response.getWriter();
			response.setContentType("application/json");
			ErrorResponse rr = new ErrorResponse(1005, 500, "BadRequest",
					e.getMessage());
			response.setStatus(500);
			mapper.writeValue(out, rr);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			PrintWriter out = response.getWriter();
			response.setContentType("application/json");
			ErrorResponse rr = new ErrorResponse(1010, 500,
					INTERNAL_SERVER_ERROR, "exception: " + e.getMessage());
			response.setStatus(500);
			mapper.writeValue(out, rr);
		}
	}

	protected void getEvents(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		// System.out.println("get event called");
		PrintWriter out = response.getWriter();
		response.setContentType("application/json");
		ObjectMapper mapper = new ObjectMapper();
		try {
			Map<String, String[]> parameters = request.getParameterMap();
			RequestHeader rh = new RequestHeader();
			rh.setHeader(request);
			LOG.info(
					"getEvent servlet method called with request headers exchangeUrl:{}  user:{}  password:{}",
					rh.getHost(), rh.getUsername(), rh.getPassword());
			if (parameters.containsKey("startDate")) {
				String[] startDateList = parameters.get("startDate");
				String startDate = startDateList[0];
				System.out.println(startDate);
				WebDavExchange wb = new WebDavExchange(rh.getHost(),
						rh.getUsername(), rh.getPassword());
				List<HashMap<String, String>> ids = wb.getEvents(startDate);
				// System.err.println(ids.toString());
				// EventsIdResponse er = new EventsIdResponse(ids);
				response.setStatus(200);
				mapper.writeValue(out, ids);

			} else if (parameters.containsKey("id")) {
				String u_id = request.getParameter("id");
				if (u_id != null && !u_id.isEmpty()) {
					WebDavExchange wb = new WebDavExchange(rh.getHost(),
							rh.getUsername(), rh.getPassword());
					List<String> uidLists = Arrays.asList(u_id.split(","));
					if (uidLists.contains(null) || uidLists.contains("")) {
						ErrorResponse rr = new ErrorResponse(
								INCORRECT_PARAMETER_CODE, 400,
								INCORRECT_PARAMETER, "request not correct");
						response.setStatus(400);
						mapper.writeValue(out, rr);
					} else {
						ArrayList<EventJsonRequest> eventsList;
						eventsList = wb.getEventsinfoLike(uidLists);
						response.setStatus(200);
						mapper.writeValue(out, eventsList);
					}
				}

			} else if (parameters.containsKey("url")) {

				String path = request.getQueryString();
				String[] pathlist = path.split("url=");
				String urlss = pathlist[1];
				// String[] urlsList=urlss.split(",");

				if (urlss != null && !urlss.isEmpty()) {
					WebDavExchange wb = new WebDavExchange(rh.getHost(),
							rh.getUsername(), rh.getPassword());
					List<String> urlsLists = Arrays.asList(urlss.split(","));
					if (urlsLists.contains(null) || urlsLists.contains("")) {
						ErrorResponse rr = new ErrorResponse(
								INCORRECT_PARAMETER_CODE, 400,
								INCORRECT_PARAMETER,
								"reuquest url paraemeter incorrect");
						response.setStatus(400);
						mapper.writeValue(out, rr);
					} else {
						ArrayList<EventJsonRequest> eventsList;
						eventsList = wb.getEventsinfoURL(urlsLists);
						response.setStatus(200);
						mapper.writeValue(out, eventsList);
					}

				} else {
					ErrorResponse rr = new ErrorResponse(
							INCORRECT_PARAMETER_CODE, 400, INCORRECT_PARAMETER,
							"url not provided");
					response.setStatus(400);
					mapper.writeValue(out, rr);
				}
			} else {
				ErrorResponse rr = new ErrorResponse(INCORRECT_PARAMETER_CODE,
						400, INCORRECT_PARAMETER, "Request format mismatch");
				response.setStatus(400);
				mapper.writeValue(out, rr);

			}
		} catch (HeaderNotFound e) {
			ErrorResponse rr = new ErrorResponse(HEADER_CODE, 400,
					HEADER_ERROR, "header not found");
			response.setStatus(400);
			mapper.writeValue(out, rr);
		} catch (WebdavException e) {
			ErrorResponse rr = new ErrorResponse(0, 400, "BadRequest",
					"web dav exception");
			response.setStatus(400);
			mapper.writeValue(out, rr);
		} catch (ParseException e) {
			ErrorResponse rr = new ErrorResponse(0, 400, "BadRequest",
					"parser exception");
			response.setStatus(400);
			mapper.writeValue(out, rr);
		} catch (NotFoundException ex) {
			ErrorResponse rr = new ErrorResponse(0, 400, "BadRequest",
					"email not found");
			response.setStatus(400);
			mapper.writeValue(out, rr);
		} catch (MoreThanOneFound ex) {
			ErrorResponse rr = new ErrorResponse(0, 500, "InternalServerError",
					ex.getMessage());
			response.setStatus(400);
			mapper.writeValue(out, rr);
		}
	}

	protected void putEmail(HttpServletRequest request,
			HttpServletResponse response) throws ServletException,
			JsonParseException, IOException {
		PrintWriter out = response.getWriter();
		response.setContentType("application/json");
		ObjectMapper mapper = new ObjectMapper();
		try {
			RequestHeader rh = new RequestHeader();
			rh.setHeader(request);
			LOG.info(
					"getEmail servlet method called with request headers exchangeUrl:{}  user:{}  password:{}",
					rh.getHost(), rh.getUsername(), rh.getPassword());
			String line;
			StringBuffer jd = new StringBuffer();
			BufferedReader reader = request.getReader();
			while ((line = reader.readLine()) != null)
				jd.append(line);
			// System.out.println(jd.toString());
			LOG.info("putEmail request body: {}");
			EmailResponse emailReq = mapper.readValue(jd.toString(),
					EmailResponse.class);
			// System.out.println(emailReq.id+emailReq.from+emailReq.body+emailReq.subject);
			WebDavExchange we = new WebDavExchange(rh.getHost(),
					rh.getUsername(), rh.getPassword());
			String guid = UUID.randomUUID().toString();
			we.sendEmail(emailReq, guid);
			String [] email_res = we.search_email_uuid(guid);
			response.setStatus(SUCCESS_CODE);
			SuccesResponse res = new SuccesResponse(200, SUCCESS, email_res[1],email_res[0]);
			mapper.writeValue(out, res);

		} catch (WebdavException ex) {
			ex.printStackTrace();
			ErrorResponse rr = new ErrorResponse(WEB_DAV_EXCEPTION,
					INTERNAL_EXCEPTION_CODE, INTERNAL_SERVER_ERROR,
					"exception:" + ex.getMessage());
			response.setStatus(INTERNAL_EXCEPTION_CODE);
			mapper.writeValue(out, rr);
		} catch (NotFoundException e) {
			// TODO: handle exception
			e.printStackTrace();
			ErrorResponse rr = new ErrorResponse(NOT_FOUND_CODE,
					BAD_REQUEST_CODE, NOT_FOUND, "exception:" + e.getMessage());
			response.setStatus(BAD_REQUEST_CODE);
			mapper.writeValue(out, rr);
		} catch (MoreThanOneFound e) {
			e.printStackTrace();
			ErrorResponse rr = new ErrorResponse(MORE_THAN_ONE_FOUND_CODE,
					INTERNAL_EXCEPTION_CODE, MORE_THAN_ONE_FOUND, "exception:"
							+ e.getMessage());
			response.setStatus(INTERNAL_EXCEPTION_CODE);
			mapper.writeValue(out, rr);
		} catch (IOException e) {
			e.printStackTrace();
			ErrorResponse rr = new ErrorResponse(INTERNAL_ERROR_CODE,
					INTERNAL_EXCEPTION_CODE, INTERNAL_SERVER_ERROR,
					"exception:" + e.getMessage());
			response.setStatus(INTERNAL_EXCEPTION_CODE);
			mapper.writeValue(out, rr);
		} catch (InterruptedException e) {
			e.printStackTrace();
			ErrorResponse rr = new ErrorResponse(INTERNAL_ERROR_CODE,
					INTERNAL_EXCEPTION_CODE, INTERNAL_SERVER_ERROR,
					"exception:" + e.getMessage());
			response.setStatus(500);
			mapper.writeValue(out, rr);
		} catch (HeaderNotFound e) {
			ErrorResponse rr = new ErrorResponse(HEADER_CODE, BAD_REQUEST_CODE,
					HEADER_ERROR, "header not found");
			response.setStatus(BAD_REQUEST_CODE);
			mapper.writeValue(out, rr);
		}

	}

	protected void putEvent(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		System.out.println("event put called");
		PrintWriter out = response.getWriter();
		response.setContentType("application/json");
		ObjectMapper mapper = new ObjectMapper();
		try {
			RequestHeader rh = new RequestHeader();
			rh.setHeader(request);
			LOG.info(
					"putEvent servlet method called with request headers exchangeUrl:{}  user:{}  password:{}",
					rh.getHost(), rh.getUsername(), rh.getPassword());
			String line;
			StringBuffer jd = new StringBuffer();
			BufferedReader reader = request.getReader();
			while ((line = reader.readLine()) != null)
				jd.append(line);
			System.out.println(jd.toString());
			EventJsonRequest EventReq = mapper.readValue(jd.toString(),
					EventJsonRequest.class);
			// System.out.println(emailReq.id+emailReq.from+emailReq.body+emailReq.subject);
			WebDavExchange we = new WebDavExchange(rh.getHost(),
					rh.getUsername(), rh.getPassword());
			String guid = UUID.randomUUID().toString();

			// we.getEvents("2014-04-12T23:20:50.52");
			String[] event_res = we.sendEvent(EventReq, guid);
			// String uuid=we.search_event_uuid(guid);
			response.setStatus(200);
			SuccesResponse res = new SuccesResponse(200, "success", event_res[1],event_res[0]);
			mapper.writeValue(out, res);

		} catch (WebdavException ex) {
			ex.printStackTrace();
			ErrorResponse rr = new ErrorResponse(0, 500, "InternalServerError",
					"exception:" + ex.getMessage());
			response.setStatus(500);
			mapper.writeValue(out, rr);
		} catch (IOException e) {
			e.printStackTrace();
			ErrorResponse rr = new ErrorResponse(0, 500, "InternalServerError",
					"exception:" + e.getMessage());
			response.setStatus(500);
			mapper.writeValue(out, rr);
		} catch (ParseException e) {
			e.printStackTrace();
			ErrorResponse rr = new ErrorResponse(0, 500, "InternalServerError",
					"exception:" + e.getMessage());
			response.setStatus(500);
			mapper.writeValue(out, rr);
		} catch (HeaderNotFound e) {

			ErrorResponse rr = new ErrorResponse(4002, 400, "BadRequest",
					"header not found");
			response.setStatus(400);
			mapper.writeValue(out, rr);
		} catch (InterruptedException e) {
			// TODO: handle exception
			// ErrorResponse rr = new
			// ErrorResponse(0,400,"InternalServerError","WebDavException:"+e.getMessage());
			// response.setStatus(500);
			// mapper.writeValue(out, rr);
			ErrorResponse rr = new ErrorResponse(4010, 400, "BadRequest",
					"header not found");
			response.setStatus(400);
			mapper.writeValue(out, rr);
		}

	}

	protected void sendAttachment(HttpServletRequest request,
			HttpServletResponse response,
			com.independentsoft.webdav.exchange.Attachment attachment)
			throws ServletException {

	}

	protected void getAttchmentsUrl(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// System.out.println("get attchment called");
		LOG.info("getAttachment api called");
		ObjectMapper mapper = new ObjectMapper();

		try {
			RequestHeader rh = new RequestHeader();
			rh.setHeader(request);
			String query = request.getQueryString();
			String[] att_owner_url = query.split("&");
			LOG.info("getAttachment api path={}", query);
			// String [] att_owner_url=query.split("&");
			String attachment_url = att_owner_url[0].trim();
			String[] attlist = attachment_url.split("attachmentURL=");
			attachment_url = attlist[1].trim();
			String messageurl = att_owner_url[1].trim();
			String[] messagesplit = messageurl.split("ownerURL=");
			messageurl = messagesplit[1].trim();
			int flag = 0;
			WebDavExchange wb = new WebDavExchange(rh.getHost(),
					rh.getUsername(), rh.getPassword());
			// System.out.println("email_url: " + messageurl);
			LOG.info("ower url={}  attchment url={}", messageurl,
					attachment_url);
			if (!messageurl.equals("") || !messageurl.isEmpty()
					|| !attachment_url.equals("") || !attachment_url.isEmpty()) {
				com.independentsoft.webdav.exchange.Attachment attachment = wb
						.getAttachmentFromMessageURL_1(messageurl,
								attachment_url);
				String atachmentUrl = attachment.getUrl();
				String atachmentFileName = attachment.getFileName();
				String content_type = attachment.getMimeType();

				long size = attachment.getSize();
				if (atachmentFileName == null) {
					atachmentFileName = atachmentUrl.substring(atachmentUrl
							.lastIndexOf("/") + 1);
				}
				OutputStream os = response.getOutputStream();
				wb.client.download(atachmentUrl, os);
				if (content_type == null) {
					response.setContentType("application/octet-stream");
				} else {
					response.setContentType(content_type);
				}
				response.addHeader("Content-Disposition",
						"attachment; filename=" + atachmentFileName);
				// Copy the contents of the file to the output stream
				response.setContentLength((int) size);
				LOG.info("attchemntID resquest for successfully executed");
			} else {
				response.setContentType("application/json");
				PrintWriter out = response.getWriter();
				ErrorResponse rr = new ErrorResponse(INCORRECT_PARAMETER_CODE,
						400, INCORRECT_PARAMETER, "incorrect request parameter");
				response.setStatus(400);
				mapper.writeValue(out, rr);
			}

		} catch (NotFoundException e) {
			e.printStackTrace();
			PrintWriter out = response.getWriter();
			response.setContentType("application/json");
			ErrorResponse rr = new ErrorResponse(1001, 400, NOT_FOUND,
					e.getMessage());
			response.setStatus(400);
			mapper.writeValue(out, rr);
		} catch (MoreThanOneFound e) {
			e.printStackTrace();
			PrintWriter out = response.getWriter();
			response.setContentType("application/json");
			ErrorResponse rr = new ErrorResponse(MORE_THAN_ONE_FOUND_CODE,
					INTERNAL_EXCEPTION_CODE, MORE_THAN_ONE_FOUND,
					e.getMessage());
			response.setStatus(INTERNAL_EXCEPTION_CODE);
			mapper.writeValue(out, rr);
		} catch (WebdavException e) {
			e.printStackTrace();
			PrintWriter out = response.getWriter();
			response.setContentType("application/json");
			ErrorResponse rr = new ErrorResponse(1005, 500,
					WEB_DAV_EXCEPTION_MESSAGE, e.getMessage());
			response.setStatus(500);
			mapper.writeValue(out, rr);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			PrintWriter out = response.getWriter();
			response.setContentType("application/json");
			ErrorResponse rr = new ErrorResponse(1010, 500,
					INTERNAL_SERVER_ERROR, "exception:" + e.getMessage());
			response.setStatus(500);
			mapper.writeValue(out, rr);
		}
	}

}
