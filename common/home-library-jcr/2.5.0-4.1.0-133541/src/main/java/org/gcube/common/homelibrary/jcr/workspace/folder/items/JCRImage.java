package org.gcube.common.homelibrary.jcr.workspace.folder.items;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.gcube.common.homelibary.model.items.type.NodeProperty;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.folder.items.Image;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.homelibrary.jcr.workspace.servlet.JCRSession;
import org.gcube.common.homelibrary.jcr.workspace.util.MetaInfo;
import org.gcube.common.homelibrary.jcr.workspace.util.WorkspaceItemUtil;
import org.gcube.common.homelibrary.model.exceptions.RepositoryException;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

import ij.ImagePlus;

public class JCRImage extends JCRFile implements Image {

	private static final String noPreviewImg 		= "/no-thumbnail.png";
	private static final int THUMB_HEIGHT	 	= 200;
	private static final int THUMB_WIDTH 		= 281;

	protected static Logger logger = LoggerFactory.getLogger(JCRImage.class);


	public JCRImage(JCRWorkspace workspace, ItemDelegate delegate)  throws RepositoryException  {
		super(workspace, delegate);
	}


	public JCRImage(JCRWorkspace workspace, ItemDelegate delegate, InputStream is) throws RemoteBackendException, RepositoryException, IOException, InternalErrorException {
		super(workspace, delegate, is);
		//		setProperties(delegate, super.info);
	}


	public JCRImage(JCRWorkspace workspace, ItemDelegate itemDelegate, MetaInfo info) throws InternalErrorException  {
		super(workspace, itemDelegate, info);
		//		setProperties(itemDelegate, info);		
	}

	//	private void setProperties(ItemDelegate itemDelegate, MetaInfo info) {
	//		Map<NodeProperty, String> content = itemDelegate.getContent();
	//		int[] thumbnailSize = info.getImageMeta().getThumbnailSize();
	//		
	//		content.put(NodeProperty.IMAGE_WIDTH, new XStream().toXML(info.getImageMeta().getWidth()));
	//		content.put(NodeProperty.IMAGE_HEIGHT, new XStream().toXML(info.getImageMeta().getHeight()));
	//		content.put(NodeProperty.THUMBNAIL_WIDTH,  new XStream().toXML(thumbnailSize[0]));
	//		content.put(NodeProperty.THUMBNAIL_HEIGHT, new XStream().toXML(thumbnailSize[1]));	
	//	}


	@Override
	public int getWidth() {
		Integer width = 0;
		try{
			width = (int) new XStream().fromXML(itemDelegate.getContent().get(NodeProperty.IMAGE_WIDTH));
		}catch (Exception e) {
			logger.error("Width not set for item " + itemDelegate.getPath());
		}
		return width;

	}

	@Override
	public int getHeight() {
		Integer height = 0;
		try{
			height = (int) new XStream().fromXML(itemDelegate.getContent().get(NodeProperty.IMAGE_HEIGHT));
		}catch (Exception e) {
			logger.error("Height not set for item " + itemDelegate.getPath());
		}
		return height;

	}

	@Override
	public InputStream getThumbnail() throws InternalErrorException {
		//		InputStream dataImage = null;
		int[] thumbnailSize = null;
		InputStream thumbnailStream = null;
		ImagePlus imagePlus = null;
		try{
			//			String remotePath = itemDelegate.getContent().get(NodeProperty.REMOTE_STORAGE_PATH);
			//			dataImage = getStorage().getRemoteFile(remotePath);
			String path = getPublicLink();
			imagePlus = WorkspaceItemUtil.getImgePlus(path);
			//			imagePlus = WorkspaceItemUtil.getImgePlus(dataImage);
			int width = imagePlus.getWidth();
			int height = imagePlus.getHeight();

			thumbnailSize = WorkspaceItemUtil.getThumbnailDimension(width, height);

			thumbnailStream = WorkspaceItemUtil.getThumbnailAsPng(imagePlus, thumbnailSize);

			int thumbWidth = thumbnailSize[0];
			int thumbHeight = thumbnailSize[1];

			if ((getHeight() != height) && (getWidth()!= width))
				setDimensions(thumbWidth, thumbHeight, width, height);

		}catch (Exception e) {

			//			thumbnailSize[0] = thumb_width;
			//			thumbnailSize[1] = thumb_height;
			thumbnailStream = JCRImage.class.getResourceAsStream(noPreviewImg);
		}

		return	thumbnailStream;
	}

	private void setDimensions(int thumbWidth, int thumbHeight, int width, int height) {
		JCRSession servlets = null;
		try {
			servlets = new JCRSession(getPortalLogin(), false);
			Map<NodeProperty, String> content = itemDelegate.getContent();
			content.put(NodeProperty.IMAGE_WIDTH, new XStream().toXML(width));
			content.put(NodeProperty.IMAGE_HEIGHT, new XStream().toXML(height));
			content.put(NodeProperty.THUMBNAIL_WIDTH, new XStream().toXML(thumbWidth));
			content.put(NodeProperty.THUMBNAIL_HEIGHT, new XStream().toXML(thumbHeight));
//			System.out.println("SAVE " + itemDelegate.toString());
			itemDelegate.setContent(content);
			servlets.saveItem(itemDelegate);
		}catch (Exception e) {
			logger.error("Impossible to set dimensions for " + itemDelegate.getPath());
		}finally {
			if (servlets!=null)
				servlets.releaseSession();
		} 

	}


	@Override
	public int getThumbnailWidth() {
		Integer thumbWidth = THUMB_WIDTH;
		try{
			thumbWidth = (int) new XStream().fromXML(itemDelegate.getContent().get(NodeProperty.THUMBNAIL_WIDTH));
		}catch (Exception e) {
			logger.error("Thumbnail widht not set for item " + itemDelegate.getPath());
		}
		return thumbWidth;
	}

	@Override
	public int getThumbnailHeight() {
		Integer thumbHeight = THUMB_HEIGHT;
		try{
			thumbHeight = (int) new XStream().fromXML(itemDelegate.getContent().get(NodeProperty.THUMBNAIL_HEIGHT));
		}catch (Exception e) {
			logger.error("Thumbnail Height not set for item " + itemDelegate.getPath());
		}
		return thumbHeight;

	}


	@Override
	public void updateInfo(JCRSession servlets, MetaInfo info) throws InternalErrorException {

		logger.info("Udpate info in image " + itemDelegate.getPath());
		//		setProperties(itemDelegate, info); 

		super.updateInfo(servlets, info);
	}


}
