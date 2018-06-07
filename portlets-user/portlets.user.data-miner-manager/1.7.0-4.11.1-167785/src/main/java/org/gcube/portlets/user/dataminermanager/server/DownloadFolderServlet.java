package org.gcube.portlets.user.dataminermanager.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.gcube.portlets.user.dataminermanager.server.storage.StorageUtil;
import org.gcube.data.analysis.dataminermanagercl.server.util.ServiceCredentials;
import org.gcube.portlets.user.dataminermanager.shared.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 *         Download Folder Servlet
 * 
 */
public class DownloadFolderServlet extends HttpServlet {
	private static final long serialVersionUID = -1838255772767180518L;
	private static Logger logger = LoggerFactory
			.getLogger(DownloadFolderServlet.class);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DownloadFolderServlet() {
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
			logger.debug("DownloadFolderServlet");

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
			logger.debug("DownloadFolderServlet session id: " + session.getId());

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

			} catch (Exception e) {
				logger.error(
						"Error retrieving credentials:"
								+ e.getLocalizedMessage(), e);
				throw new ServletException(e.getLocalizedMessage());
			}

			String itemId = request
					.getParameter(Constants.DOWNLOAD_FOLDER_SERVLET_ITEM_ID_PARAMETER);
			String folderName = request
					.getParameter(Constants.DOWNLOAD_FOLDER_SERVLET_FOLDER_NAME_PARAMETER);
			logger.debug("Request: [itemId=" + itemId + ", folderName="
					+ folderName + "]");

			File tmpZip = StorageUtil.zipFolder(
					serviceCredentials.getUserName(), itemId);
			response.setHeader("Content-Disposition", "attachment; filename=\""
					+ folderName + ".zip\"");
			response.setContentType("application/zip");
			response.setHeader("Content-Length",
					String.valueOf(tmpZip.length()));

			OutputStream out = response.getOutputStream();

			FileInputStream fileTmpZip = new FileInputStream(tmpZip);
			IOUtils.copy(fileTmpZip, response.getOutputStream());
			out.flush();
			out.close();
			fileTmpZip.close();
			tmpZip.delete();
			return;

		} catch (Throwable e) {
			logger.error("Error in DownloadFolderServlet: "
					+ e.getLocalizedMessage());
			e.printStackTrace();
			throw new ServletException("Error:" + e.getLocalizedMessage(), e);

		}
	}

}
