/**
 *
 */
package org.gcube.portlets.user.workspace.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.gcube.common.homelibary.model.items.type.FolderItemType;
import org.gcube.common.homelibary.model.items.type.WorkspaceItemType;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.folder.FolderItem;
import org.gcube.common.homelibrary.home.workspace.folder.items.Image;
import org.gcube.common.portal.PortalContext;
import org.gcube.portlets.user.workspace.client.ConstantsExplorer;
import org.gcube.portlets.user.workspace.client.util.ImageRequestType;
import org.gcube.portlets.user.workspace.server.util.WsUtil;


/**
 * The Class ImageServlet.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Nov 30, 2016
 */
public class ImageServlet extends HttpServlet{

	private static final long serialVersionUID = -8423345575690165644L;

	protected static Logger logger = Logger.getLogger(ImageServlet.class);


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init() throws ServletException {
		super.init();

		logger.trace("Workspace ImageServlet ready.");
	}


	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		String imageId = req.getParameter("id");
		String imageType = req.getParameter("type");
		String contextID = req.getParameter(ConstantsExplorer.CONTEXT_ID);

		logger.info("request image id: "+imageId+", type: "+imageType +", "+ConstantsExplorer.CONTEXT_ID+ ": "+contextID);

		ImageRequestType requestType = null;

		if (imageType == null){
			logger.warn("No request type specified, return the complete image");
			requestType = ImageRequestType.IMAGE;
		} else requestType = ImageRequestType.valueOf(imageType);


		Workspace wa = null;
		try {
			String currentScope= PortalContext.getConfiguration().getCurrentScope(contextID);
			logger.info("For ContextID: "+contextID +", read scope from Portal Context: "+currentScope);
			wa = WsUtil.getWorkspace(req, currentScope);
		} catch (Exception e) {
			logger.error("Error during workspace retrieving", e);
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Error during workspace retrieving");
			return;
		}

		if (wa == null) {
			logger.error("Error, no workspace in session");
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Error, no workspace in session");
			return;
		}

		WorkspaceItem item;
		try {
			item = wa.getItem(imageId);
		} catch (ItemNotFoundException e) {
			logger.error("Error, no images found", e);
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Error, no images found");
			return;
		}

		if (item.getType() != WorkspaceItemType.FOLDER_ITEM)  {
			logger.error("Error wrong item type, expected FOLDER_ITEM, found "+item.getType());
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Error wrong item type, expected FOLDER_ITEM, found "+item.getType());
			return;
		}

		FolderItem folderItem = (FolderItem) item;
		FolderItemType itemType = folderItem.getFolderItemType();

		if (itemType != FolderItemType.EXTERNAL_IMAGE
				&& itemType != FolderItemType.IMAGE_DOCUMENT)  {
			logger.error("Error wrong folder item type, expected EXTERNAL_IMAGE or IMAGE_DOCUMENT, found "+itemType);
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error wrong folder item type, expected EXTERNAL_IMAGE or IMAGE_DOCUMENT, found "+itemType);
			return;
		}


		Image image = (Image)folderItem;

		// Get the MIME type of the image
		String mimeType = image.getMimeType();

		// Set content type
		resp.setContentType(mimeType);

		// Set content size
		try {
			/*
			long size = (requestType==ImageRequestType.IMAGE)?image.getLength():image.getThumbnailLength();
			resp.setContentLength((int)size);
			*/
			long size = requestType==ImageRequestType.IMAGE?image.getLength():0;
			if(size == 0){
				logger.warn("Image or Thumbnail size is 0, skipping set content lenght");
			}else
				resp.setContentLength((int)size);
		} catch (InternalErrorException e) {
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Error in image lenght retrieving");
			return;
		}

		// Send the content
		try {
			OutputStream out = resp.getOutputStream();
			InputStream in = requestType==ImageRequestType.IMAGE?image.getData():image.getThumbnail();
			IOUtils.copy(in, out);
			in.close();
			out.close();
		} catch (InternalErrorException e) {
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Error in image data retrieving");
			return;
		}
	}

}
