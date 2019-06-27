/**
 *
 */
package org.gcube.portlets.user.workspace.client.workspace.folder.item.gcube;

import java.util.Date;
import java.util.Map;

import org.gcube.portlets.user.workspace.client.workspace.GWTProperties;
import org.gcube.portlets.user.workspace.client.workspace.GWTWorkspaceFolder;
import org.gcube.portlets.user.workspace.client.workspace.GWTWorkspaceItemAction;
import org.gcube.portlets.user.workspace.client.workspace.folder.GWTFolderItemType;
import org.gcube.portlets.user.workspace.client.workspace.folder.item.GWTImage;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * @author Federico De Faveri defaveriAtisti.cnr.it
 *
 */
public class GWTImageDocument extends GWTDocument implements IsSerializable, GWTImage{

	protected String imageUrl;
	protected String thumbnailUrl;

	protected int thumbnailWidth;
	protected int thumbnailHeight;
	protected long thumbnailLenght;

	protected int width;
	protected int height;
	protected long lenght;

	protected GWTImageDocument()
	{}

	public GWTImageDocument(Date creationTime, String id, GWTProperties properties, String name, String owner,
			String description, Date lastModificationTime, GWTWorkspaceItemAction lastAction,
			GWTWorkspaceFolder parent,
			String imageUrl, String thumbnailUrl, int width, int height, int thumbnailWidth, int thumbnailHeight, long thumbnailLenght,
			long length, String mimeType, String oid, Map<String, GWTDocumentMetadata> metadata, Map<String,String> annotation,
			String collection, int numberOfAlternatives, int numberOfParts) {

		super(creationTime, id, properties, name, owner, description, lastModificationTime, lastAction, parent, length, oid,
				mimeType, metadata, annotation, collection, numberOfAlternatives, numberOfParts);

		this.imageUrl = imageUrl;
		this.thumbnailUrl = thumbnailUrl;
		this.width = width;
		this.height = height;
		this.lenght = length;

		this.thumbnailHeight = thumbnailHeight;
		this.thumbnailWidth = thumbnailWidth;
		this.thumbnailLenght = thumbnailLenght;

		this.mimeType = mimeType;
	}

	public String getThumbnailUrl(){
		return GWT.getModuleBaseURL()+thumbnailUrl;
	}

	public String getImageUrl(){
		return GWT.getModuleBaseURL()+imageUrl;
	}

	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @return the length
	 */
	public long getLength() {
		return lenght;
	}


	/**
	 * @return the thumbnailWidth
	 */
	public int getThumbnailWidth() {
		return thumbnailWidth;
	}

	/**
	 * @return the thumbnailHeight
	 */
	public int getThumbnailHeight() {
		return thumbnailHeight;
	}

//	/**
//	 * @return the thumbnailLength
//	 */
//	public long getThumbnailLenght() {
//		return thumbnailLenght;
//	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GWTFolderItemType getFolderItemType() {
		return GWTFolderItemType.IMAGE_DOCUMENT;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("GWTImageDocument [imageUrl=");
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
		builder.append(", lenght=");
		builder.append(lenght);
		builder.append("]");
		return builder.toString();
	}



}
