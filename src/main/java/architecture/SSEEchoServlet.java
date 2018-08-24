package architecture;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = { "/simplesse" })
public class SSEEchoServlet extends HttpServlet {
	private static final long serialVersionUID = -5067880808754161339L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		Integer userId = Integer.parseInt(req.getParameter("user"));
		// set content type
		Boolean sendSSE = false;
		while (!sendSSE) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			sendSSE = PushStatusSingleton.getInstance().getIsUpdate().get(userId);
			if (sendSSE == null)
				sendSSE = false;
		}

		res.setContentType("text/event-stream");
		res.setCharacterEncoding("UTF-8");
		PrintWriter writer = res.getWriter();
		PushStatusSingleton.getInstance().getIsUpdate().put(userId, false);
		// send SSE
		writer.write("data: " + "true" + "\n\n");

	}
}
