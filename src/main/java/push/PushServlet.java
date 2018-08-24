
/*
 * HowOpenSource.com
 * Copyright (C) 2015 admin@howopensource.com
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package push;

import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.util.Random;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Main servlet that does everything. Receive and send messages. Send last
 * messages to JSP, etc. Better split it between two servlets.
 *
 */
@SuppressWarnings("serial")
@WebServlet("/h2push")
public class PushServlet extends HttpServlet {

	private static final boolean someChange = false;

	@Override
	public void destroy() {
		// Stops thread and clears queue and stores
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// This is for loading home page when user comes for the first time

		// Check that it is SSE request

		if ("text/event-stream".equals(request.getHeader("Accept"))) {

			// This a Tomcat specific - makes request asynchronous
			// request.setAttribute("org.apache.catalina.ASYNC_SUPPORTED", true);

			// Set header fields
			response.setContentType("text/event-stream");
			response.setHeader("Cache-Control", "no-cache");
			response.setHeader("Connection", "keep-alive");
			response.setCharacterEncoding("UTF-8");
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (!someChange) {
				PrintWriter writer = response.getWriter();
				writer.write("data: " + System.currentTimeMillis() + "\n\n");
				writer.flush();
			}

		}

	}

}