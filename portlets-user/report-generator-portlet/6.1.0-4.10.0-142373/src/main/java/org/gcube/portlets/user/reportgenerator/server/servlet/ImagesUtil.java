package org.gcube.portlets.user.reportgenerator.server.servlet;

import org.gcube.common.homelibary.model.items.type.FolderItemType;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.folder.FolderItem;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalImage;
import org.gcube.common.homelibrary.home.workspace.folder.items.GCubeItem;

public class ImagesUtil {
	public static final String GIF = "image/gif";
	public static final String PNG = "image/png";
	public static final String JPEG = "image/jpeg";
	public static final String JPG = "image/jpg";	
	public static final String TIFF = "image/tiff";
	public static final String BMP = "image/bmp";
	/**
	 * return a string for the file extension given a mimetype
	 * 
	 * @param bi the basketItem 
	 * @return a string for the file extension given a mimetype
	 */
	public static String getImageExtension(FolderItem bi) {

		String mimetype = "";
		if (bi.getFolderItemType()==FolderItemType.EXTERNAL_IMAGE){
			ExternalImage image = (ExternalImage) bi;
			mimetype = image.getMimeType();			
		}

		if (bi.getFolderItemType()==FolderItemType.IMAGE_DOCUMENT){
			GCubeItem image = (GCubeItem) bi;
			try {
				mimetype = image.getMimeType();
			} catch (InternalErrorException e) {
				e.printStackTrace();
			}	
		}

		if (mimetype.equals(GIF))
			return "gif";
		else if (mimetype.equals(PNG))
			return "png";
		else if (mimetype.equals(JPG))			
			return "jpg";
		else if (mimetype.equals(JPEG))
			return "jpg";
		else if (mimetype.equals(TIFF))
			return "png";
		else if (mimetype.equals(BMP))
			return "bmp";
		else 
			return "jpg";
	}
}
