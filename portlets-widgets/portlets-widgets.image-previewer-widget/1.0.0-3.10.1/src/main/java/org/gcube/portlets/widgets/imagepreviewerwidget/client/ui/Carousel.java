package org.gcube.portlets.widgets.imagepreviewerwidget.client.ui;

import java.util.List;

import org.gcube.portlets.widgets.imagepreviewerwidget.client.EnhancedImage;
import org.gcube.portlets.widgets.imagepreviewerwidget.client.resources.Resources;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Image;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.constants.IconSize;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
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
	Image shownImage;

	@UiField
	Image loadingImage;

	@UiField
	HorizontalPanel horizontaFooterPanel;

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

		// set alignment of the horizontal panel's children
		horizontaFooterPanel.setCellHorizontalAlignment(closeButton, HorizontalPanel.ALIGN_CENTER);
		horizontaFooterPanel.setCellHorizontalAlignment(downloadButton, HorizontalPanel.ALIGN_CENTER);
		horizontaFooterPanel.setCellHorizontalAlignment(prevButton, HorizontalPanel.ALIGN_LEFT);
		horizontaFooterPanel.setCellHorizontalAlignment(nextButton, HorizontalPanel.ALIGN_RIGHT);

		// set central buttons' cell widths to be equal as well as their widths
		horizontaFooterPanel.setCellWidth(downloadButton, "200px");
		horizontaFooterPanel.setCellWidth(closeButton, "200px");
		downloadButton.setWidth("140px");
		closeButton.setWidth("140px");
		
		// set vertical alignment
		horizontaFooterPanel.setCellVerticalAlignment(nextButton, VerticalPanel.ALIGN_MIDDLE);
		horizontaFooterPanel.setCellVerticalAlignment(prevButton,  VerticalPanel.ALIGN_MIDDLE);

		// set icons
		downloadButton.setIcon(IconType.DOWNLOAD);
		closeButton.setIcon(IconType.COLLAPSE);
		prevButton.setIcon(IconType.CHEVRON_LEFT);
		nextButton.setIcon(IconType.CHEVRON_RIGHT);

		// set icons'size
		prevButton.setIconSize(IconSize.LARGE);
		nextButton.setIconSize(IconSize.LARGE);

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
		
		// set shownImage mouse icon to pointer
		shownImage.getElement().getStyle().setCursor(Cursor.POINTER); 
	}

	@UiHandler("closeButton")
	public void hideOnClick(ClickEvent e){

		mainModalPanel.hide();

	}

	@UiHandler("downloadButton")
	/**
	 * When the user pushes this button, try to download the file.
	 * @param e
	 */
	public void downloadOnClick(ClickEvent e){

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
				currentPreviewPosition == 0 ? listOfAttachmentsToShow.size() - 1 : currentPreviewPosition - 1;

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

		// show loading image and hide the shown one
		loadingImage.setVisible(true);
		shownImage.setVisible(false);

		// when image is download ...
		shownImage.addLoadHandler(new LoadHandler() {

			@Override
			public void onLoad(LoadEvent event) {

				// swap
				shownImage.setVisible(true);
				loadingImage.setVisible(false);

			}
		});

		// fetch the image from the url
		shownImage.setUrl(listOfAttachmentsToShow.get(currentPreviewPosition).getImageUrl());

		// change image tooltip
		shownImage.setTitle(listOfAttachmentsToShow.get(currentPreviewPosition).getToolTipToShow());

		// change the title to the modal
		String shownTitle = listOfAttachmentsToShow.get(currentPreviewPosition).getTitleToShow().length() > 50 ?
				listOfAttachmentsToShow.get(currentPreviewPosition).getTitleToShow().substring(0, 50) + "..." :
					listOfAttachmentsToShow.get(currentPreviewPosition).getTitleToShow();
		mainModalPanel.setTitle(shownTitle);
		
		// to set the tooltip we have to lookup the title in the header (it's a <h3> element)
		NodeList<Element> list = mainModalPanel.getElement().getElementsByTagName("h3");
		list.getItem(0).setTitle(listOfAttachmentsToShow.get(currentPreviewPosition).getTitleToShow());
		
	}

}
