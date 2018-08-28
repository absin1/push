package architecture;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = { "/simplesse" }, asyncSupported = true)
public class SSEEchoServlet extends HttpServlet {
	private static final long serialVersionUID = -5067880808754161339L;
	private Map<String, AsyncContext> asyncContexts = new ConcurrentHashMap<String, AsyncContext>();
	private boolean running;
	// Thread that waits for new message and then redistribute it
	private Thread notifier = new Thread(new Runnable() {

		@Override
		public void run() {
			while (running) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				// System.out.println("Total Listeners: " + asyncContexts.values().size());
				// Sends the message to all the AsyncContext's response
				for (AsyncContext asyncContext : asyncContexts.values()) {
					// System.out.println("Listening>>" + asyncContext);
					// set content type
					HttpServletRequest req = (HttpServletRequest) asyncContext.getRequest();
					HttpServletResponse res1 = (HttpServletResponse) asyncContext.getResponse();
					String user = req.getParameter("user");
					String id = req.getSession().getId();
					Boolean sendSSE = false;
					sendSSE = PushStatusSingleton.getInstance().getIsUpdate().get(user + "__" + id);
					if (sendSSE == null) {
						PushStatusSingleton.getInstance().getIsUpdate().put(user + "__" + id, false);
						sendSSE = false;
					}
					if (sendSSE) {
						HttpServletResponse httpResponse = (HttpServletResponse) res1;
						// httpResponse.addHeader("Access-Control-Allow-Origin", "*");
						httpResponse.setContentType("text/event-stream");
						httpResponse.setCharacterEncoding("UTF-8");
						PrintWriter writer;
						try {
							// writer = httpResponse.getWriter();
							writer = asyncContext.getResponse().getWriter();
							PushStatusSingleton.getInstance().getIsUpdate().put(user + "__" + id, false);
							// send SSE
							writer.write("data: " + "true" + "\n\n");
							// asyncContexts.values().remove(asyncContext);
							asyncContext.complete();
							System.out.println("Sent a push from " + asyncContext);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	});

	@Override
	public void destroy() {

		// Stops thread and clears queue and stores
		running = false;
		asyncContexts.clear();
		System.err.println("Servlet for pushing events destroyed");
	}

	@Override
	public void init() throws ServletException {
		// TODO Auto-generated method stub
		super.init();
		running = true;
		notifier.start();
		System.out.println("Servlet for pushing events started");
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		String id = req.getSession().getId();
		req.setAttribute("org.apache.catalina.ASYNC_SUPPORTED", true);
		HttpServletResponse httpResponse = (HttpServletResponse) res;
		httpResponse.addHeader("Access-Control-Allow-Origin", "*");
		final AsyncContext ac = req.startAsync();
		ac.addListener(new AsyncListener() {

			@Override
			public void onComplete(AsyncEvent event) throws IOException {
				asyncContexts.remove(id);
				System.out.println("Completed an existing listener and removed it from asynchContexts: " + ac
						+ ", taking the total listener count to " + asyncContexts.values().size());
			}

			@Override
			public void onError(AsyncEvent event) throws IOException {
				asyncContexts.remove(id);
				System.err.println("An existing listener went into error and was removed from asynchContexts: " + ac
						+ ", taking the total listener count to " + asyncContexts.values().size());
			}

			@Override
			public void onStartAsync(AsyncEvent event) throws IOException {
				// Do nothing
			}

			@Override
			public void onTimeout(AsyncEvent event) throws IOException {
				asyncContexts.remove(id);
				System.err.println("An existing listener timed out and was removed from asynchContexts: " + ac
						+ ", taking the total listener count to " + asyncContexts.values().size());

				HttpServletRequest req = (HttpServletRequest) ac.getRequest();
				HttpServletResponse res1 = (HttpServletResponse) ac.getResponse();
				String user = req.getParameter("user");
				String id = req.getSession().getId();
				Boolean sendSSE = false;
				sendSSE = PushStatusSingleton.getInstance().getIsUpdate().get(user + "__" + id);
				if (sendSSE == null) {
					PushStatusSingleton.getInstance().getIsUpdate().put(user + "__" + id, false);
					sendSSE = false;
				}
				HttpServletResponse httpResponse = (HttpServletResponse) res1;
				// httpResponse.addHeader("Access-Control-Allow-Origin", "*");
				httpResponse.setContentType("text/event-stream");
				httpResponse.setCharacterEncoding("UTF-8");
				PrintWriter writer;
				try {
					// writer = httpResponse.getWriter();
					writer = ac.getResponse().getWriter();
					PushStatusSingleton.getInstance().getIsUpdate().put(user + "__" + id, false);
					// send SSE
					writer.write("data: " + "false" + "\n\n");
					// asyncContexts.values().remove(ac);
					ac.complete();
					System.out.println("Sent a push from " + ac + " post timeout in order to reinitiate.");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		// Put context in a map
		asyncContexts.put(id, ac);
		System.out.println("Created a new listener and added it to asynchContexts: " + ac
				+ ", taking the total listener count to " + asyncContexts.values().size());
	}
}
