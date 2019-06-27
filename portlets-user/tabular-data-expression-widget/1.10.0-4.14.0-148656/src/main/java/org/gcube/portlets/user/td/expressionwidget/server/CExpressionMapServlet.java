/**
 * 
 */
package org.gcube.portlets.user.td.expressionwidget.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.portlets.user.td.expressionwidget.shared.exception.ExpressionParserException;
import org.gcube.portlets.user.td.gwtservice.server.SessionUtil;
import org.gcube.portlets.user.td.gwtservice.server.util.ServiceCredentials;
import org.gcube.portlets.user.td.gwtservice.shared.Constants;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTServiceException;
import org.gcube.portlets.user.td.widgetcommonevent.shared.expression.C_Expression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class CExpressionMapServlet extends HttpServlet {

	private static final long serialVersionUID = 1352918477757397708L;

	protected static Logger logger = LoggerFactory
			.getLogger(CExpressionMapServlet.class);

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		handleRequest(req, resp);
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		handleRequest(req, resp);
	}

	protected void handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		logger.info("CExpressionMapServlet");
		long startTime = System.currentTimeMillis();

		HttpSession session = request.getSession();

		if (session == null) {
			logger.error("Error getting the upload session, no session valid found: "
					+ session);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"ERROR-Error getting the user session, no session found"
							+ session);
			return;
		}
		logger.info("CExpressionMapServlet import session id: "
				+ session.getId());
		
		@SuppressWarnings("unused")
		ServiceCredentials serviceCredentials;

		String scopeGroupId = request.getHeader(Constants.CURR_GROUP_ID);
		if (scopeGroupId == null || scopeGroupId.isEmpty()) {
			scopeGroupId = request.getParameter(Constants.CURR_GROUP_ID);
			if (scopeGroupId == null || scopeGroupId.isEmpty()) {
				logger.error("CURR_GROUP_ID is null, it is a mandatory parameter in custom servlet: "
						+ scopeGroupId);
				throw new ServletException(
						"CURR_GROUP_ID is null, it is a mandatory parameter in custom servlet: "
								+ scopeGroupId);
			}
		}

		try {
			serviceCredentials = SessionUtil.getServiceCredentials(request,
					scopeGroupId);

		} catch (TDGWTServiceException e) {
			logger.error(
					"Error retrieving credentials:" + e.getLocalizedMessage(),
					e);
			throw new ServletException(e.getLocalizedMessage());
		}

		ObjectInputStream in = new ObjectInputStream(request.getInputStream());
		C_Expression exp=null;
		try {
			exp = (C_Expression) in.readObject();
			logger.debug("CExpressionMapServlet Exp: "+exp);
		} catch (ClassNotFoundException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST,
					"ERROR-Error retreiving expression in request: "+e.getLocalizedMessage());
			
		}

		Expression expression=null;
		try {
			expression = parse(exp);
		} catch (ExpressionParserException e) {
			logger.error(e.getLocalizedMessage());
			response.sendError(HttpServletResponse.SC_BAD_REQUEST,
					"ERROR-Error parsing expression: "+e.getLocalizedMessage());
		}
		logger.trace("Expression: " + expression.toString());

		response.setContentType("Content-Type: application/java-object");
		response.setStatus(HttpServletResponse.SC_OK);

		ObjectOutputStream oos = new ObjectOutputStream(
				response.getOutputStream());
		oos.writeObject(expression);
		oos.flush();
		oos.close();
		logger.trace("Response in " + (System.currentTimeMillis() - startTime));
	}

	protected Expression parse(C_Expression exp) throws ExpressionParserException {
		C_ExpressionParser parser=new C_ExpressionParser();
		return parser.parse(exp);
	}

}
