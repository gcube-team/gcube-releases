package org.gcube.portlets.widgets.imagepreviewerwidget.client.ui;

import java.util.List;

import org.gcube.portlets.widgets.imagepreviewerwidget.client.EnhancedImage;
import org.gcube.portlets.widgets.imagepreviewerwidget.client.ImageService;
import org.gcube.portlets.widgets.imagepreviewerwidget.client.ImageServiceAsync;
import org.gcube.portlets.widgets.imagepreviewerwidget.client.resources.Resources;
import org.gcube.portlets.widgets.imagepreviewerwidget.shared.Orientation;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Image;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.constants.Device;
import com.github.gwtbootstrap.client.ui.constants.IconSize;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A carousel for image/file previews.
 * @author Costantino Perciante at ISTI-CNR 
 * (costantino.perciante@isti.cnr.it)
 *
 */
public class Carousel extends Composite{

	private static CarouselUiBinder uiBinder = GWT
			.create(CarouselUiBinder.class);

	interface CarouselUiBinder extends UiBinder<Widget, Carousel> {
	}

	private final ImageServiceAsync imageServices = GWT.create(ImageService.class);

	@UiField
	Modal mainModalPanel;

	@UiField
	Button prevButton;

	@UiField
	Button nextButton;

	@UiField
	Button downloadButton;

	@UiField
	Button closeButton;

	@UiField
	Button downloadButtonSmart;

	@UiField
	Button closeButtonSmart;

	@UiField
	Image shownImage;

	@UiField
	Image loadingImage;

	@UiField
	HorizontalPanel commands;

	// list of enhanced images to show
	private List<EnhancedImage> listOfAttachmentsToShow;

	// index of the image shown
	private int currentPreviewPosition;

	// other resources
	private Resources resources = GWT.create(Resources.class);

	/**
	 * Build a carousel to show EnhancedImages.
	 * @param imagesToShow
	 */
	public Carousel() {

		initWidget(uiBinder.createAndBindUi(this));

		// add custom styles to gwt-bootstrap-modal (just once)
		mainModalPanel.addStyleName("modal-custom");
		((Element)mainModalPanel.getElement().getChildNodes().getItem(1)).addClassName("modal-body-custom");

		// set vertical alignment
		commands.setCellVerticalAlignment(nextButton, VerticalPanel.ALIGN_MIDDLE);
		commands.setCellVerticalAlignment(prevButton,  VerticalPanel.ALIGN_MIDDLE);

		// set alignment of the horizontal panel's children
		commands.setCellHorizontalAlignment(closeButton, HorizontalPanel.ALIGN_CENTER);
		commands.setCellHorizontalAlignment(downloadButton, HorizontalPanel.ALIGN_CENTER);
		commands.setCellHorizontalAlignment(closeButtonSmart, HorizontalPanel.ALIGN_CENTER);
		commands.setCellHorizontalAlignment(downloadButtonSmart, HorizontalPanel.ALIGN_CENTER);
		commands.setCellHorizontalAlignment(prevButton, HorizontalPanel.ALIGN_LEFT);
		commands.setCellHorizontalAlignment(nextButton, HorizontalPanel.ALIGN_RIGHT);

		// set sizes
		downloadButton.setWidth("90px");
		closeButton.setWidth("90px");
		downloadButtonSmart.setWidth("15px");
		closeButtonSmart.setWidth("15px");

		// set icons
		downloadButton.setIcon(IconType.DOWNLOAD_ALT);
		closeButton.setIcon(IconType.COLLAPSE);
		downloadButtonSmart.setIcon(IconType.DOWNLOAD_ALT);
		closeButtonSmart.setIcon(IconType.COLLAPSE);
		prevButton.setIcon(IconType.CHEVRON_LEFT);
		nextButton.setIcon(IconType.CHEVRON_RIGHT);

		// set icons'size
		prevButton.setIconSize(IconSize.LARGE);
		nextButton.setIconSize(IconSize.LARGE);

		// choose devices
		closeButtonSmart.setShowOn(Device.PHONE);
		downloadButtonSmart.setShowOn(Device.PHONE);
		downloadButton.setShowOn(Device.DESKTOP);
		closeButton.setShowOn(Device.DESKTOP);
		downloadButton.setHideOn(Device.PHONE);
		closeButton.setHideOn(Device.PHONE);

		// add some other css style
		downloadButton.addStyleName("buttons-style");
		closeButton.addStyleName("buttons-style");
		downloadButtonSmart.addStyleName("buttons-style");
		closeButtonSmart.addStyleName("buttons-style");

		//on user click on the image, go on
		shownImage.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				if(nextButton.isVisible())
					nextButton.click();

			}
		});

		// set url of the loading image
		loadingImage.setResource(resources.loadingImage());
	}

	@UiHandler("closeButton")
	public void hideOnClick(ClickEvent e){

		closeButtonClickHanderBody();

	}

	@UiHandler("closeButtonSmart")
	public void hideOnClickSmart(ClickEvent e){

		closeButtonClickHanderBody();

	}

	/**
	 * Close button click handler body
	 */
	private void closeButtonClickHanderBody(){

		mainModalPanel.hide();

	}

	@UiHandler("downloadButton")
	/**
	 * When the user pushes this button, try to download the file.
	 * @param e
	 */
	public void downloadOnClick(ClickEvent e){

		downloadButtonClickHanderBody();

	}

	@UiHandler("downloadButtonSmart")
	/**
	 * When the user pushes this button, try to download the file.
	 * @param e
	 */
	public void downloadOnClickSmart(ClickEvent e){

		downloadButtonClickHanderBody();

	}

	/**
	 * Download button click handler body
	 */
	public void downloadButtonClickHanderBody(){

		String downloadUrl = listOfAttachmentsToShow.get(currentPreviewPosition).getDownloadUrl();

		if(downloadUrl != null){
			Window.open(downloadUrl, "_blank", "");
		}

	}

	@UiHandler("prevButton")
	/**
	 * Show the previous image, if any.
	 * @param e
	 */
	public void onClickPrev(ClickEvent e){

		// evaluate prev index
		currentPreviewPosition = 
				currentPreviewPosition == 0 ? 
						listOfAttachmentsToShow.size() - 1 : currentPreviewPosition - 1;

						// show the image
						showImage(currentPreviewPosition);

	}

	@UiHandler("nextButton")
	/**
	 * Show the next image, if any.
	 * @param e
	 */
	public void onClickNext(ClickEvent e){

		// evaluate next index
		currentPreviewPosition = 
				currentPreviewPosition == listOfAttachmentsToShow.size() -1 ? 
						0 : currentPreviewPosition + 1;
		
		// show the image
		showImage(currentPreviewPosition);

	}

	/**
	 * Used to show this carousel (starting from the initial image).
	 */
	public void show(){

		mainModalPanel.show();

		// take the first image
		currentPreviewPosition = 0;

		// show the image
		showImage(currentPreviewPosition);
	}

	/**
	 * Used to show a specific image of this carousel.
	 */
	public void show(EnhancedImage image){

		// evaluate where this image is
		int index = evaluateImagePosition(image);

		if(index == -1)
			return;

		// take the first object
		currentPreviewPosition = index;

		// show the image
		showImage(currentPreviewPosition);

		// show the panel
		mainModalPanel.show();
	}

	/**
	 * Retrieve the index of such image.
	 * @param image
	 * @return -1 if no image matches
	 */
	private int evaluateImagePosition(EnhancedImage image) {

		for(int index = 0; index < listOfAttachmentsToShow.size(); index++){

			if(listOfAttachmentsToShow.get(index).equals(image))
				return index;

		}

		return -1;
	}

	/**
	 * Change the set of images to show.
	 * @param imagesToShow
	 */
	public void updateImages(List<EnhancedImage> imagesToShow){

		listOfAttachmentsToShow = imagesToShow;

	}

	/**
	 * Hide Previous and Next arrows of the carousel.
	 */
	public void hideArrows(){

		nextButton.setVisible(false);
		prevButton.setVisible(false);

	}

	/**
	 * Show image function.
	 * @param index the index of the image to show
	 */
	private void showImage(int index){

		showLoader();

		final EnhancedImage imageToShow = listOfAttachmentsToShow.get(index);
		final String url = imageToShow.getImageUrl();

		// when image is downloaded ...
		shownImage.addLoadHandler(new LoadHandler() {

			@Override
			public void onLoad(LoadEvent event) {

				// call only if undefined
				if(imageToShow.getOrientation().equals(Orientation.UNDEFINED)){

					imageServices.getImageOrientation(url, new AsyncCallback<Orientation>() {

						@Override
						public void onSuccess(Orientation result) {		
							imageToShow.setOrientation(result);
							setOrientation(imageToShow, result);
						}

						@Override
						public void onFailure(Throwable caught) {
							imageToShow.setOrientation(Orientation.DO_NOT_ROTATE);
							setOrientation(imageToShow, Orientation.DO_NOT_ROTATE);
						}
					});

				}else{
					setOrientation(imageToShow, imageToShow.getOrientation());
				}
			}
		});

		// fetch the image from the url
		shownImage.setUrl(url);

		// change image tooltip
		shownImage.setTitle(imageToShow.getToolTipToShow());

		// change the title to the modal
		String shownTitle = imageToShow.getTitleToShow().length() > 80 ?
				imageToShow.getTitleToShow().substring(0, 80) + "..." :
					imageToShow.getTitleToShow();
		mainModalPanel.setTitle(shownTitle);

		// to set the tooltip we have to lookup the title in the header (it's a <h3> element)
		NodeList<Element> list = mainModalPanel.getElement().getElementsByTagName("h3");
		list.getItem(0).setTitle(imageToShow.getTitleToShow());

		// change header style
		((Element)mainModalPanel.getElement().getChildNodes().getItem(0)).addClassName("modal-header-custom");
	}

	/**
	 * Show image loader
	 */
	protected void showLoader() {
		loadingImage.setVisible(true);
		shownImage.setVisible(false);
	}


	/**
	 * Remove image loader
	 */
	protected void removeLoader() {
		shownImage.setVisible(true);
		loadingImage.setVisible(false);
	}

	/**
	 * Set the orientation
	 * @param img
	 * @param o
	 */
	private void setOrientation(EnhancedImage img, Orientation o){

		// remove all the possible secondary styles..
		shownImage.removeStyleName("rotate-0");
		shownImage.removeStyleName("rotate-90");
		shownImage.removeStyleName("rotate-180");
		shownImage.removeStyleName("rotate-270");

		switch(o){

		case ROTATE_90: shownImage.addStyleName("rotate-90");
		break;

		case ROTATE_180: shownImage.addStyleName("rotate-180");
		break;

		case ROTATE_270: shownImage.addStyleName("rotate-270");
		break;

		default: shownImage.addStyleName("rotate-0");
		}
		
		// few ms are needed to remove/add the style, after that we remove the loading image
		Timer t = new Timer() {
			
			@Override
			public void run() {
				removeLoader();
			}
		};

		t.schedule(500);
	}

}
