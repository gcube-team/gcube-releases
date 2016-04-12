package org.gcube.portlets.widgets.imagepreviewerwidget.client;

import com.github.gwtbootstrap.client.ui.Image;

/**
 * This class allows to build the image to show within the carousel. Along the image itself, there are 
 * the following other information:
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
	 * The image to show.
	 */
	private Image image;

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
	 * Build an enhanced image from a url.
	 * @param imageUrl the url of the image.
	 */
	public EnhancedImage(String imageUrl){

		super();
		this.image = new Image(imageUrl);
		this.titleToShow = imageUrl;
		this.toolTipToShow = imageUrl;
		this.downloadUrl = imageUrl;

	}

	/**
	 * Build an enhanced image from another image.
	 * @param image the image to show
	 */
	public EnhancedImage(Image image){

		super();
		this.image = image;
		this.titleToShow = image.getUrl();
		this.toolTipToShow = image.getUrl();
		this.downloadUrl = image.getUrl();

	}

	/**
	 * Build an enhanced image from an image but allows to customize the title of the image and its tooltip.
	 * @param image
	 * @param titleToShow
	 * @param toolTipToShow
	 */
	public EnhancedImage(Image image, String titleToShow, String toolTipToShow) {

		super();
		this.image = image;
		this.titleToShow = titleToShow;
		this.toolTipToShow = toolTipToShow;
		this.downloadUrl = image.getUrl();

	}

	/**
	 * Build an enhanced image from an image but allows to customize the other properties.
	 * @param image
	 * @param titleToShow
	 * @param toolTipToShow
	 * @param download url
	 */
	public EnhancedImage(Image image, String titleToShow, String toolTipToShow, String downloadUrl) {

		super();
		this.image = image;
		this.titleToShow = titleToShow;
		this.toolTipToShow = toolTipToShow;
		this.downloadUrl = downloadUrl;
	}

	public Image getImage() {

		return image;

	}
	public void setImage(Image image) {

		this.image = image;

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
	public String getImageUrl(){

		return this.image.getUrl();

	}

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	@Override
	public String toString() {
		return "EnhancedImage [image=" + image + ", titleToShow=" + titleToShow
				+ ", toolTipToShow=" + toolTipToShow + ", downloadUrl="
				+ downloadUrl + "]";
	}

}
