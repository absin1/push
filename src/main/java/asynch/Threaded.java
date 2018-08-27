package asynch;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = { "/threaded" }, asyncSupported = true)
public class Threaded extends HttpServlet {
	private Map<String, AsyncContext> asyncContexts = new ConcurrentHashMap<String, AsyncContext>();
	private boolean running;
	// Thread that waits for new message and then redistribute it
	private Thread notifier = new Thread(new Runnable() {

		@Override
		public void run() {
			while (running) {
				// Sends the message to all the AsyncContext's response
				for (AsyncContext asyncContext : asyncContexts.values()) {
					try {
						System.out.println("Request received");
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						System.out.println("Request done");
						PrintWriter writer = asyncContext.getResponse().getWriter();
						writer.println();
						writer.flush();
					} catch (Exception e) {
						// In case of exception remove context from map
						asyncContexts.values().remove(asyncContext);
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
	}

	@Override
	public void init() throws ServletException {
		// TODO Auto-generated method stub
		super.init();
		running = true;
		notifier.start();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
		final String id = UUID.randomUUID().toString();// improper(req);
		request.setAttribute("org.apache.catalina.ASYNC_SUPPORTED", true);
		final AsyncContext ac = request.startAsync();
		ac.addListener(new AsyncListener() {

			@Override
			public void onComplete(AsyncEvent event) throws IOException {
				asyncContexts.remove(id);
			}

			@Override
			public void onError(AsyncEvent event) throws IOException {
				asyncContexts.remove(id);
			}

			@Override
			public void onStartAsync(AsyncEvent event) throws IOException {
				// Do nothing
			}

			@Override
			public void onTimeout(AsyncEvent event) throws IOException {
				asyncContexts.remove(id);
			}
		});

		// Put context in a map
		asyncContexts.put(id, ac);
	}

	private void improper(HttpServletRequest req) {
		AsyncContext startAsync = req.startAsync();
		startAsync.start(new Runnable() {
			@Override
			public void run() {
				System.out.println("Request received " + req.getSession().getId());
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("Request done" + req.getSession().getId());
				startAsync.complete();
			}

		});
	}
}
