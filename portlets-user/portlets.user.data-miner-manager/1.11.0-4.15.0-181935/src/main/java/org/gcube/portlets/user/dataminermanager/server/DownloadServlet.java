package org.gcube.portlets.user.dataminermanager.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.gcube.common.storagehub.model.items.FolderItem;
import org.gcube.data.analysis.dataminermanagercl.server.util.ServiceCredentials;
import org.gcube.portlets.user.dataminermanager.server.storage.ItemDownload;
import org.gcube.portlets.user.dataminermanager.server.storage.StorageUtil;
import org.gcube.portlets.user.dataminermanager.shared.Constants;
import org.gcube.portlets.user.dataminermanager.shared.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Download Servlet
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class DownloadServlet extends HttpServlet {

	private static final long serialVersionUID = 5389118370656932343L;

	private static Logger logger = LoggerFactory.getLogger(DownloadServlet.class);

	public DownloadServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		createResponse(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		createResponse(request, response);
	}

	private void createResponse(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		try {
			logger.debug("DownloadServlet()");

			HttpSession session = req.getSession();

			if (session == null) {
				logger.error("Error getting the download session, no session valid found: " + session);
				resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
						"ERROR-Error getting the user session, no session found" + session);
				return;
			}
			logger.debug("DownloadServlet() session id: " + session.getId());
			String scopeGroupId = req.getHeader(Constants.CURR_GROUP_ID);
			if (scopeGroupId == null || scopeGroupId.isEmpty()) {
				scopeGroupId = req.getParameter(Constants.CURR_GROUP_ID);
				if (scopeGroupId == null || scopeGroupId.isEmpty()) {
					logger.error(
							"CURR_GROUP_ID is null, it is a mandatory parameter in custom servlet: " + scopeGroupId);
					throw new ServletException(
							"CURR_GROUP_ID is null, it is a mandatory parameter in custom servlet: " + scopeGroupId);
				}
			}

			String itemId = req.getParameter("itemId");
			logger.info("DownloadServlet(): [scopeGroupId=" + scopeGroupId + ",ItemId=" + itemId + "]");
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(req, scopeGroupId);
			StorageUtil filesStorage = new StorageUtil();
			ItemDownload itemDownload = filesStorage.getItemDownload(serviceCredentials.getUserName(), itemId);
			logger.debug("ItemDownload: " + itemDownload);
			if (itemDownload == null) {
				logger.error("This type of item does not support download operation");
				throw new ServletException("This type of item does not support download operation");

			} else {
				if (itemDownload.getInputStream() == null) {
					logger.error("This type of item does not support download operation");
					throw new ServletException("This type of item does not support download operation");
				} else {
					String fileName;
					if (itemDownload.getItemDescription() == null) {
						fileName = "filename";
						resp.setHeader("Content-Disposition", "attachment;filename=\"" + fileName + "\"");
						resp.setHeader("Content-Type", "application/force-download");
					} else {
						if (itemDownload.getItemDescription().getName() == null
								|| itemDownload.getItemDescription().getName().isEmpty()) {
							if (itemDownload.getItemDescription().getType() != null && itemDownload.getItemDescription()
									.getType().compareTo(FolderItem.class.getSimpleName()) == 0) {
								fileName = "folder.zip";
								resp.setHeader("Content-Disposition", "attachment;filename=\"" + fileName + "\"");
								resp.setHeader("Content-Type", "application/zip");
							} else {
								fileName = "filename";
								resp.setHeader("Content-Disposition", "attachment;filename=\"" + fileName + "\"");
								resp.setHeader("Content-Type", "application/force-download");
							}
						} else {
							if (itemDownload.getItemDescription().getType() != null && itemDownload.getItemDescription()
									.getType().compareTo(FolderItem.class.getSimpleName()) == 0) {
								fileName = itemDownload.getItemDescription().getName() + ".zip";
								resp.setHeader("Content-Disposition", "attachment;filename=\"" + fileName + "\"");
								resp.setHeader("Content-Type", "application/zip");

							} else {
								fileName = itemDownload.getItemDescription().getName();
								resp.setHeader("Content-Disposition", "attachment;filename=\"" + fileName + "\"");
								resp.setHeader("Content-Type", "application/force-download");
							}
						}
					}
					logger.debug("Content-Disposition: "+resp.getHeader("Content-Disposition"));
					logger.debug("Content-Type: "+resp.getHeader("Content-Type"));
					logger.debug("DownloadServlet filename: " + fileName);
					stream(itemDownload.getInputStream(), resp.getOutputStream());
				}
			}
		} catch (ServiceException e) {
			logger.error("DownloadServlet():" + e.getLocalizedMessage(), e);
			throw new ServletException(e.getLocalizedMessage(), e);
		} catch (Throwable e) {
			logger.error("DownloadServlet(): " + e.getLocalizedMessage(), e);
			throw new ServletException("Download item error: " + e.getLocalizedMessage(), e);
		}
	}

	private long stream(InputStream input, OutputStream output) throws IOException {
		try (ReadableByteChannel inputChannel = Channels.newChannel(input);
				WritableByteChannel outputChannel = Channels.newChannel(output);) {
			ByteBuffer buffer = ByteBuffer.allocateDirect(10240);
			long size = 0;

			while (inputChannel.read(buffer) != -1) {
				buffer.flip();
				size += outputChannel.write(buffer);
				buffer.clear();
			}

			return size;
		}
	}
}