package org.gcube.portlets.admin.accountingmanager.server;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portlets.admin.accountingmanager.server.export.CSVManager;
import org.gcube.portlets.admin.accountingmanager.server.export.JSONManager;
import org.gcube.portlets.admin.accountingmanager.server.export.XMLManager;
import org.gcube.portlets.admin.accountingmanager.server.state.AccountingStateData;
import org.gcube.portlets.admin.accountingmanager.server.util.ServiceCredentials;
import org.gcube.portlets.admin.accountingmanager.shared.Constants;
import org.gcube.portlets.admin.accountingmanager.shared.data.AccountingType;
import org.gcube.portlets.admin.accountingmanager.shared.exception.ServiceException;
import org.gcube.portlets.admin.accountingmanager.shared.export.ExportDescriptor;
import org.gcube.portlets.admin.accountingmanager.shared.export.ExportType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 *         Export Servlet
 * 
 */
public class ExportServlet extends HttpServlet {
	private static final long serialVersionUID = -1838255772767180518L;
	private static Logger logger = LoggerFactory.getLogger(ExportServlet.class);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ExportServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		createResponse(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		createResponse(request, response);
	}

	private void createResponse(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		try {
			logger.info("ExportServlet");

			HttpSession session = request.getSession();

			if (session == null) {
				logger.error("Error getting the session, no session valid found: "
						+ session);
				response.sendError(
						HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
						"ERROR-Error getting the user session, no session found "
								+ session);
				return;
			}
			logger.debug("ExportServlet session id: " + session.getId());

			ServiceCredentials serviceCredentials;

			try {
				String scopeGroupId = request
						.getParameter(Constants.CURR_GROUP_ID);
			
				//String currUserId = request
				//			.getParameter(Constants.CURR_USER_ID);
				//serviceCredentials = SessionUtil.getServiceCredentials(request,
				//		scopeGroupId, currUserId);
				serviceCredentials = SessionUtil.getServiceCredentials(request,
						scopeGroupId);
				
				ScopeProvider.instance.set(serviceCredentials.getScope());

			} catch (ServiceException e) {
				logger.error(
						"Error retrieving credentials:"
								+ e.getLocalizedMessage(), e);
				e.printStackTrace();
				throw new ServletException(e.getLocalizedMessage());
			}

			String exportType = request
					.getParameter(Constants.EXPORT_SERVLET_TYPE_PARAMETER);
			String accountingType = request
					.getParameter(Constants.EXPORT_SERVLET_ACCOUNTING_TYPE_PARAMETER);

			logger.debug("Request: [exportType=" + exportType
					+ ", accountingType=" + accountingType + "]");

			AccountingStateData accountingStateData = SessionUtil
					.getAccountingStateData(session,
							serviceCredentials, AccountingType.valueOf(accountingType));
			if (accountingStateData == null) {
				logger.error("No series present in session for this accounting type: "
						+ accountingType);
				throw new ServletException(
						"No series present in session for this accounting type: "
								+ accountingType);
			}

			ExportType exportT = ExportType.valueOf(exportType);
			if (exportT == null) {
				logger.error("Invalid Export Type Request: " + exportType);
				throw new ServletException("Invalid Export Type Request: "
						+ exportType);
			}

			ExportDescriptor exportDescriptor = null;
			switch (exportT) {
			case CSV:
				CSVManager csvManager = new CSVManager(
						serviceCredentials.getUserName());
				exportDescriptor = csvManager.download(accountingStateData);
				response.setContentType("text/csv");
				break;
			case JSON:
				JSONManager jsonManager = new JSONManager(
						serviceCredentials.getUserName());
				exportDescriptor = jsonManager.download(accountingStateData);
				response.setContentType("application/json");
				break;
			case XML:
				XMLManager xmlManager = new XMLManager(
						serviceCredentials.getUserName());
				exportDescriptor = xmlManager.download(accountingStateData);
				response.setContentType("text/xml");
				break;
			default:
				logger.error("Export Type not supported: " + exportType);
				throw new ServletException("Export Type not supported: "
						+ exportType);
			}
			logger.debug("ExportDescriptor: " + exportDescriptor);
			response.setHeader("Content-Disposition", "attachment; filename=\""
					+ exportDescriptor.getCsvModel().getName()
					+ exportDescriptor.getFileExtension() + "\"");
			response.setHeader("Content-Length", String
					.valueOf(exportDescriptor.getPath().toFile().length()));
			OutputStream out = response.getOutputStream();
			Files.copy(exportDescriptor.getPath(), out);
			out.flush();
			out.close();

			try {
				Files.delete(exportDescriptor.getPath());
			} catch (IOException e) {
				logger.error("Error in deleting temp file: "
						+ e.getLocalizedMessage());
				e.printStackTrace();
				throw new ServiceException("Error deleting temp file: "
						+ e.getLocalizedMessage(), e);
			}

			return;

		} catch (Throwable e) {
			logger.error("Error in ExportServlet: " + e.getLocalizedMessage());
			e.printStackTrace();
			throw new ServletException("Error: " + e.getLocalizedMessage(), e);

		}
	}

}
