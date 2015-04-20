import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.NCSARequestLog;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class LoadJettyServer {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		// create server instance
		Server server = new Server(8080);
		// servlet context
		ServletContextHandler context = new ServletContextHandler(
				ServletContextHandler.SESSIONS);
		context.setContextPath("/RestExchange");
		server.setHandler(context);
		context.addServlet(new ServletHolder(new Api()), "/api/*");

		// start server
		server.start();
		server.join();
	}

}
