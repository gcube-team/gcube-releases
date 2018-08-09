package org.gcube.portlets.user.workspace.client.workspace.folder.item;

public interface GWTImage {

	public String getThumbnailUrl();

	public String getImageUrl();

	/**
	 * @return the width
	 */
	public int getWidth();

	/**
	 * @return the height
	 */
	public int getHeight();

	/**
	 * @return the length
	 */
	public long getLength();

	/**
	 * @return the mimeType
	 */
	public String getMimeType();

	/**
	 * @return the thumbnailWidth
	 */
	public int getThumbnailWidth();

	/**
	 * @return the thumbnailHeight
	 */
	public int getThumbnailHeight();

	/**
	 * @return the thumbnailLength
	 */
//	public long getThumbnailLenght();

}