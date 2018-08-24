package architecture;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/complex")
public class ReadUpdateComplexObject extends HttpServlet {

	private static final long serialVersionUID = 3815362752719535640L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Integer user = Integer.parseInt(req.getParameter("user"));
		ComplexObject complexObject = Model.getInstance().getComplexMap().get(user);
		resp.getWriter().append(complexObject.getName());
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String complex = req.getParameter("complex");
		String user = req.getParameter("user");
		Model.getInstance().getComplexMap().put(Integer.parseInt(user), new ComplexObject(complex));
		HashMap<String, Boolean> isUpdate = PushStatusSingleton.getInstance().getIsUpdate();
		for (String key : isUpdate.keySet()) {
			if (key.split("__")[0].equalsIgnoreCase(user))
				PushStatusSingleton.getInstance().getIsUpdate().put(key, true);
		}
	}
}
