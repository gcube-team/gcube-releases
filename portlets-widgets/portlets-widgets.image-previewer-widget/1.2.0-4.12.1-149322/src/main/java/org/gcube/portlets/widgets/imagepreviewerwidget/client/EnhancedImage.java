package org.gcube.portlets.widgets.imagepreviewerwidget.client;

import org.gcube.portlets.widgets.imagepreviewerwidget.shared.Orientation;

/**
 * This class allows to build the image to show within the carousel. It contains:
 * <ul>
 * <li>title to show: a title to show in the header of the carousel;</li>
 * <li>tooltip : a tooltip shown on image hover event;</li>
 * <li>download url: in case of a file, this field can be used to download it.</li>
 * </ul> 
 * If tooltip/download url/title to show is not specified, its value will be equal to the image url.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class EnhancedImage {

	/**
	 * Title to show in the header of the carousel.
	 */
	private String titleToShow;

	/**
	 * Tooltip shown when the user passes over the image.
	 */
	private String toolTipToShow;

	/**
	 * The download url of the image/file.
	 */
	private String downloadUrl;
	
	/**
	 * The url of the image to show
	 */
	private String thumbnailUrl;

	/**
	 * The orientation
	 */
	private Orientation orientation = Orientation.UNDEFINED;

	/**
	 * Build an enhanced image from a url.
	 * @param imageUrl the url of the image.
	 */
	public EnhancedImage(String imageUrl){

		super();
		this.titleToShow = imageUrl;
		this.toolTipToShow = imageUrl;
		this.downloadUrl = imageUrl;
		this.thumbnailUrl = imageUrl;

	}

	/**
	 * Build an enhanced image from an image but allows to customize the title of the image and its tooltip.
	 * @param image
	 * @param titleToShow
	 * @param toolTipToShow
	 */
	public EnhancedImage(String url, String titleToShow, String toolTipToShow) {

		super();
		this.titleToShow = titleToShow;
		this.toolTipToShow = toolTipToShow;
		this.downloadUrl = url;
		this.thumbnailUrl = url;
	}

	/**
	 * Build an enhanced image from an image but allows to customize the other properties.
	 * @param image
	 * @param titleToShow
	 * @param toolTipToShow
	 * @param download url
	 */
	public EnhancedImage(String thumbnailUrl, String titleToShow, String toolTipToShow, String downloadUrl) {

		super();
		this.titleToShow = titleToShow;
		this.toolTipToShow = toolTipToShow;
		this.downloadUrl = downloadUrl;
		this.thumbnailUrl = thumbnailUrl;
		
	}

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}

	public String getTitleToShow() {
		return titleToShow;
	}

	public void setTitleToShow(String titleToShow) {
		this.titleToShow = titleToShow;
	}

	public String getToolTipToShow() {
		return toolTipToShow;
	}

	public void setToolTipToShow(String toolTipToShow) {
		this.toolTipToShow = toolTipToShow;
	}

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	/**
	 * @return the orientation
	 */
	public Orientation getOrientation() {
		return orientation;
	}

	/**
	 * @param orientation the orientation to set
	 */
	public void setOrientation(Orientation orientation) {
		this.orientation = orientation;
	}
	
	
	public String getImageUrl(){
		return this.thumbnailUrl != null? this.thumbnailUrl: this.downloadUrl;
	}

	@Override
	public String toString() {
		return "EnhancedImage [titleToShow=" + titleToShow + ", toolTipToShow="
				+ toolTipToShow + ", downloadUrl=" + downloadUrl
				+ ", thumbnailUrl=" + thumbnailUrl + ", orientation="
				+ orientation + "]";
	}

}
