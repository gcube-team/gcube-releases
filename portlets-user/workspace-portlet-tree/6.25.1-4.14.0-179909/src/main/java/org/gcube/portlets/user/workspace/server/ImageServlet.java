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
import org.gcube.common.portal.PortalContext;
import org.gcube.common.storagehubwrapper.server.StorageHubWrapper;
import org.gcube.common.storagehubwrapper.server.tohl.Workspace;
import org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem;
import org.gcube.common.storagehubwrapper.shared.tohl.exceptions.InternalErrorException;
import org.gcube.common.storagehubwrapper.shared.tohl.exceptions.ItemNotFoundException;
import org.gcube.common.storagehubwrapper.shared.tohl.items.FileItem;
import org.gcube.common.storagehubwrapper.shared.tohl.items.FileItemType;
import org.gcube.common.storagehubwrapper.shared.tohl.items.ImageFileItem;
import org.gcube.common.storagehubwrapper.shared.tohl.items.ItemStreamDescriptor;
import org.gcube.portlets.user.workspace.client.ConstantsExplorer;
import org.gcube.portlets.user.workspace.client.util.ImageRequestType;
import org.gcube.portlets.user.workspace.server.util.ThumbnailGenerator;
import org.gcube.portlets.user.workspace.server.util.WsUtil;
import org.gcube.portlets.user.workspace.shared.SessionExpiredException;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;


/**
 * The Class ImageServlet.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 * Oct 18, 2018
 */
public class ImageServlet extends HttpServlet{

	/**
	 *
	 */
	private static final int THUMB_MAX_SIZE = 300;

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
		String contextID = req.getParameter(ConstantsExplorer.CURRENT_CONTEXT_ID);
		boolean viewContent =  req.getParameter("viewContent")==null?false:req.getParameter("viewContent").equals("true");
		//String currUserId = req.getParameter(ConstantsExplorer.CURRENT_USER_ID);

		ImageRequestType requestType = getRequestType(imageType);
		logger.info("request image id: "+imageId+", type: "+requestType +", "+ConstantsExplorer.CURRENT_CONTEXT_ID+ ": "+contextID+", viewContent: "+viewContent);
		Workspace wa = null;
		try {

			if(WsUtil.isSessionExpired(req))
				throw new SessionExpiredException();

			GCubeUser gcubeUser = PortalContext.getConfiguration().getCurrentUser(req);
			StorageHubWrapper storageHubWrapper = WsUtil.getStorageHubWrapper(req, contextID, gcubeUser);
			wa = storageHubWrapper.getWorkspace();

			//wa = WsUtil.getWorkspace(req, contextID, gcubeUser);
		} catch (Exception e) {
			if (e instanceof SessionExpiredException){
				resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error the user session is expired");
				return;
			}
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
		} catch (Exception e) {
			logger.error("Error on getting image: ", e);
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Error on getting image");
			return;
		}

		if (item instanceof FileItem == false)  {
			logger.error("Error wrong item type, expected a file, found "+item.getType());
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Error wrong item type, expected a file, found "+item.getType());
			return;
		}

		FileItem folderItem = (FileItem) item;
		FileItemType itemType = folderItem.getFileItemType();

		if (itemType != FileItemType.IMAGE_DOCUMENT)  {
			logger.error("Error is wrong the file type, expected an "+FileItemType.IMAGE_DOCUMENT+", found "+itemType);
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error wrong file item type, expected EXTERNAL_IMAGE or IMAGE_DOCUMENT, found "+itemType);
			return;
		}

		ImageFileItem image = (ImageFileItem) folderItem;

		try {

			long size = 0;
			ItemStreamDescriptor streamDescr = null;
			String mimeType = null;
			switch (requestType) {
			case THUMBNAIL:

				try{
					streamDescr = wa.getThumbnailData(image.getId());
				}catch (Exception e) {
					logger.warn("Thumbnail data not available from StorageHub, creating it by the "+ThumbnailGenerator.class.getSimpleName() +" provided by the "+ImageServlet.class.getSimpleName());
					streamDescr = wa.downloadFile(image.getId(), image.getName(), null, null);
					InputStream thumbIs = ThumbnailGenerator.generateThumbnail(streamDescr.getStream(), image.getName(), THUMB_MAX_SIZE, THUMB_MAX_SIZE);
					streamDescr = new org.gcube.common.storagehubwrapper.shared.tohl.impl.StreamDescriptor(thumbIs, image.getName(), null, image.getMimeType());
				}
				mimeType = streamDescr.getMimeType()!=null?streamDescr.getMimeType():image.getMimeType();
				size = streamDescr.getSize()==null?0:streamDescr.getSize(); //reading size from byte.lenght
				logger.info("THUMBNAIL size: "+size);
				break;
			case IMAGE:
			default:
				//reading mimeType from source image
				mimeType = image.getMimeType();
				size = image.getSize()==null?0:image.getSize(); //reading size from image source
				streamDescr = wa.downloadFile(image.getId(), image.getName(), null, null);
				logger.info("IMAGE size is: "+size);
				break;
			}


			String fileName = image.getName();
			String contentDisposition = viewContent?"inline":"attachment";
			resp.setHeader("Content-Disposition", contentDisposition+"; filename=\"" + fileName + "\"" );

			logger.info("mimeType is: "+mimeType);
			resp.setContentType(mimeType);

			//if image/thumbnail size is 0, skipping setContentLength
			if(size!=0)
				resp.setContentLength((int)size);

			InputStream in = streamDescr.getStream();

			if(in==null)
				throw new InternalErrorException("Image/Thumbnail data is null for image: "+image.getName());

			OutputStream out = resp.getOutputStream();
			// Send the content
			IOUtils.copy(in, out);
			in.close();
			out.close();
			logger.info("stream copied correctly");
		} catch (Exception e) {
			logger.error("Error: ",e);
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Error on gettimg the image/thumbnail data");
			return;
		}
	}



	/**
	 * Gets the request type.
	 *
	 * @param imageType the image type
	 * @return the request type
	 */
	private ImageRequestType getRequestType(String imageType){

		if (imageType == null){
			logger.warn("No request type specified, return the complete image");
			return ImageRequestType.IMAGE;
		} else
			try{
				return ImageRequestType.valueOf(imageType);
			}catch (Exception e) {
				return ImageRequestType.IMAGE;
			}

	}

}
