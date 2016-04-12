package org.gcube.common.homelibrary.jcr.workspace.folder.items;

import ij.ImagePlus;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.gcube.common.homelibary.model.items.type.NodeProperty;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.folder.items.Image;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.homelibrary.jcr.workspace.servlet.JCRServlets;
import org.gcube.common.homelibrary.jcr.workspace.util.WorkspaceItemUtil;
import org.gcube.common.homelibrary.model.exceptions.RepositoryException;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

public class JCRImage extends JCRFile implements Image {

	private static final String noPreviewImg 		= "/no-thumbnail.png";
	private static final int thumb_height	 	= 200;
	private static final int thumb_width 		= 281;

	protected static Logger logger = LoggerFactory.getLogger(JCRImage.class);


	public JCRImage(JCRWorkspace workspace, ItemDelegate delegate)  throws RepositoryException  {
		super(workspace, delegate);
	}


	public JCRImage(JCRWorkspace workspace, ItemDelegate node, String mimeType, File tmpFile) throws RemoteBackendException, RepositoryException, IOException {
		super(workspace, node, mimeType, tmpFile);
		setProperties(node, tmpFile);
	}

	public JCRImage(JCRWorkspace workspace, ItemDelegate itemDelegate, String mimeType, File tmpFile, String path) throws RemoteBackendException, RepositoryException, IOException {
		super(workspace, itemDelegate, mimeType, tmpFile, path);
		setProperties(itemDelegate, tmpFile);
	}


	private void setProperties(ItemDelegate itemDelegate, File tmpFile) {

		//		System.out.println("***** SET PROPERTIES");
		int[] thumbnailSize = null;
		InputStream thumbnailStream = null;
		ImagePlus imagePlus = null;
		int width = 0;
		int height = 0;
		Map<NodeProperty, String> content = itemDelegate.getContent();

		try{
			imagePlus = WorkspaceItemUtil.getImgePlus(tmpFile);
			width = imagePlus.getWidth();
			height = imagePlus.getHeight();

			thumbnailSize = WorkspaceItemUtil.getThumbnailDimension(width, height);
			thumbnailStream = WorkspaceItemUtil.getThumbnailAsPng(imagePlus,
					thumbnailSize);

		}catch (Exception e) {
			thumbnailSize[0] = thumb_width;
			thumbnailSize[1] = thumb_height;
			thumbnailStream = JCRImage.class.getResourceAsStream(noPreviewImg);
		}

		content.put(NodeProperty.IMAGE_WIDTH, new XStream().toXML(width));
		content.put(NodeProperty.IMAGE_HEIGHT, new XStream().toXML(height));
		content.put(NodeProperty.THUMBNAIL_WIDTH,  new XStream().toXML(thumbnailSize[0]));
		content.put(NodeProperty.THUMBNAIL_HEIGHT, new XStream().toXML(thumbnailSize[1]));
		//		content.put(NodeProperty.THUMBNAIL_DATA, new XStream().toXML(binary));


		try {
			if (thumbnailStream!= null)
				thumbnailStream.close();
		} catch (IOException e) {
			logger.error("tmp file alredy closed");
		}

		if (imagePlus!=null)
			imagePlus.close();

	}


	@Override
	public int getWidth() {
		Integer width = (int) new XStream().fromXML(itemDelegate.getContent().get(NodeProperty.IMAGE_WIDTH));
		return width;
	}

	@Override
	public int getHeight() {
		Integer height = (int) new XStream().fromXML(itemDelegate.getContent().get(NodeProperty.IMAGE_HEIGHT));
		return height;
	}

	@Override
	public InputStream getThumbnail() throws InternalErrorException {
		InputStream dataImage = null;
		int[] thumbnailSize = null;
		InputStream thumbnailStream = null;
		ImagePlus imagePlus = null;
		int width = 0;
		int height = 0;

		try{

			String remotePath = itemDelegate.getContent().get(NodeProperty.REMOTE_STORAGE_PATH);
			dataImage = getStorage().getRemoteFile(remotePath);

			imagePlus = WorkspaceItemUtil.getImgePlus(dataImage);
			width = imagePlus.getWidth();
			height = imagePlus.getHeight();

			thumbnailSize = WorkspaceItemUtil.getThumbnailDimension(width, height);
			thumbnailStream = WorkspaceItemUtil.getThumbnailAsPng(imagePlus, thumbnailSize);

		}catch (Exception e) {

			//			thumbnailSize[0] = thumb_width;
			//			thumbnailSize[1] = thumb_height;
			thumbnailStream = JCRImage.class.getResourceAsStream(noPreviewImg);
		}

		return	thumbnailStream;
	}

	@Override
	public int getThumbnailWidth() {
		Integer thumbWidth = (int) new XStream().fromXML(itemDelegate.getContent().get(NodeProperty.THUMBNAIL_WIDTH));
		return thumbWidth;
	}

	@Override
	public int getThumbnailHeight() {
		Integer thumbHight = (int) new XStream().fromXML(itemDelegate.getContent().get(NodeProperty.THUMBNAIL_HEIGHT));
		return thumbHight;
	}

	//	@Override
	//	public long getThumbnailLength() throws InternalErrorException {
	//		Long thumbLenght = (Long) new XStream().fromXML(itemDelegate.getContent().get(NodeProperty.th));
	//		return thumbLenght;
	//		//		Session session = null;
	//		//		try {
	//		//			session = JCRRepository.getSession();
	//		//			Node node = session.getNodeByIdentifier(nodeId);
	//		//			return node.getProperty(THUMBNAIL_DATA).getBinary().getSize();
	//		//		} catch (Exception e) {
	//		//			throw new RuntimeException(e) ;
	//		//		} finally {
	//		//			session.logout();
	//		//		}	
	//
	//	}



	public void updateInfo(JCRServlets servlets, java.io.File tmp) throws InternalErrorException {

		logger.info("Udpate info in image " + itemDelegate.getPath());
		//update size and mime type in JCRFile
		super.updateInfo(servlets, tmp);

//		JCRServlets servlets = null;
		try {
//			servlets = new JCRServlets(portalLogin);
			setProperties(itemDelegate, tmp);
			servlets.saveItem(itemDelegate);
		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} 
//		finally {
//			servlets.releaseSession();
//		}
	}

}
