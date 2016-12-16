/**
 * 
 */
package org.gcube.portlets.user.workspace.client.workspace.folder.item;

import java.util.Date;

import org.gcube.portlets.user.workspace.client.workspace.GWTProperties;
import org.gcube.portlets.user.workspace.client.workspace.GWTWorkspaceFolder;
import org.gcube.portlets.user.workspace.client.workspace.GWTWorkspaceItemAction;
import org.gcube.portlets.user.workspace.client.workspace.folder.GWTFolderItem;
import org.gcube.portlets.user.workspace.client.workspace.folder.GWTFolderItemType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class GWTExternalImage extends GWTFolderItem implements IsSerializable, GWTImage{
	
	protected String imageUrl;
	protected String thumbnailUrl;
	
	protected int thumbnailWidth;
	protected int thumbnailHeight;
	protected long thumbnailLenght;
	
	protected int width;
	protected int height;
	protected String mimeType;
	
	protected GWTExternalImage()
	{}
	
	public GWTExternalImage(Date creationTime, String id, GWTProperties properties, String name, String owner, 
			String description, Date lastModificationTime, GWTWorkspaceItemAction lastAction,
			GWTWorkspaceFolder parent, String imageUrl, String thumbnailUrl, int width, int height, long length, int thumbnailWidth, int thumbnailHeight, long thumbnailLenght, String mimeType) {
		super(id, name, description, owner, creationTime, properties, lastModificationTime, lastAction, parent, length);
		
		this.imageUrl = imageUrl;
		this.thumbnailUrl = thumbnailUrl;
		this.width = width;
		this.height = height;
		this.mimeType = mimeType;
		this.thumbnailHeight = thumbnailHeight;
		this.thumbnailWidth = thumbnailWidth;
		this.thumbnailLenght = thumbnailLenght;
	}
	
	//ADDED BY FRANCESCO
	public GWTExternalImage(String imageUrl, String thumbnailUrl, int width, int height, long length, int thumbnailWidth, int thumbnailHeight, long thumbnailLenght, String mimeType) {
//		super(id, name, description, owner, creationTime, null, null, null, parent, length);
		
		this.imageUrl = imageUrl;
		this.thumbnailUrl = thumbnailUrl;
		this.width = width;
		this.height = height;
		this.mimeType = mimeType;
		this.thumbnailHeight = thumbnailHeight;
		this.thumbnailWidth = thumbnailWidth;
		this.thumbnailLenght = thumbnailLenght;
	}

	@Override
	public GWTFolderItemType getFolderItemType() {
		return GWTFolderItemType.EXTERNAL_IMAGE;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getThumbnailUrl(){
		return GWT.getModuleBaseURL()+thumbnailUrl;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String getImageUrl(){
		return GWT.getModuleBaseURL()+imageUrl;
	}


	/**
	 * {@inheritDoc}
	 */
	public int getWidth() {
		return width;
	}


	/**
	 * {@inheritDoc}
	 */
	public int getHeight() {
		return height;
	}


	/**
	 * {@inheritDoc}
	 */
	public String getMimeType() {
		return mimeType;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getThumbnailWidth() {
		return thumbnailWidth;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getThumbnailHeight() {
		return thumbnailHeight;
	}

	/**
	 * {@inheritDoc}
	 */
	public long getThumbnailLenght() {
		return thumbnailLenght;
	}
}
