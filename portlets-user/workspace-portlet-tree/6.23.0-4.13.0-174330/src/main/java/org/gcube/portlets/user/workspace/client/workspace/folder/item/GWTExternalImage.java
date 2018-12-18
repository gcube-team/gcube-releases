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
 * The Class GWTExternalImage.
 *
 * @author Federico De Faveri defaveri@isti.cnr.it
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

	/**
	 * Instantiates a new GWT external image.
	 */
	protected GWTExternalImage()
	{}

	/**
	 * Instantiates a new GWT external image.
	 *
	 * @param creationTime the creation time
	 * @param id the id
	 * @param properties the properties
	 * @param name the name
	 * @param owner the owner
	 * @param description the description
	 * @param lastModificationTime the last modification time
	 * @param lastAction the last action
	 * @param parent the parent
	 * @param imageUrl the image url
	 * @param thumbnailUrl the thumbnail url
	 * @param width the width
	 * @param height the height
	 * @param length the length
	 * @param thumbnailWidth the thumbnail width
	 * @param thumbnailHeight the thumbnail height
	 * @param thumbnailLenght the thumbnail lenght
	 * @param mimeType the mime type
	 */
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



	/**
	 * ADDED by Francesco
	 * Instantiates a new GWT external image.
	 *
	 * @param id the id
	 * @param name the name
	 * @param imageUrl the image url
	 * @param thumbnailUrl the thumbnail url
	 * @param width the width
	 * @param height the height
	 * @param length the length
	 * @param thumbnailWidth the thumbnail width
	 * @param thumbnailHeight the thumbnail height
	 * @param thumbnailLenght the thumbnail lenght
	 * @param mimeType the mime type
	 */
	public GWTExternalImage(String id, String name, String imageUrl, String thumbnailUrl, int width, int height, long length, int thumbnailWidth, int thumbnailHeight, long thumbnailLenght, String mimeType) {
		this.id = id;
		this.name = name;
		this.imageUrl = imageUrl;
		this.thumbnailUrl = thumbnailUrl;
		this.width = width;
		this.height = height;
		this.mimeType = mimeType;
		this.thumbnailHeight = thumbnailHeight;
		this.thumbnailWidth = thumbnailWidth;
		this.thumbnailLenght = thumbnailLenght;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.workspace.folder.GWTFolderItem#getFolderItemType()
	 */
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("GWTExternalImage [imageUrl=");
		builder.append(imageUrl);
		builder.append(", thumbnailUrl=");
		builder.append(thumbnailUrl);
		builder.append(", thumbnailWidth=");
		builder.append(thumbnailWidth);
		builder.append(", thumbnailHeight=");
		builder.append(thumbnailHeight);
		builder.append(", thumbnailLenght=");
		builder.append(thumbnailLenght);
		builder.append(", width=");
		builder.append(width);
		builder.append(", height=");
		builder.append(height);
		builder.append(", mimeType=");
		builder.append(mimeType);
		builder.append("]");
		return builder.toString();
	}


}
