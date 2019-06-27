package org.gcube.portlets.user.newsfeed.client.ui;

import org.gcube.portal.databook.shared.Attachment;
import org.gcube.portlets.widgets.imagepreviewerwidget.client.EnhancedImage;
import org.gcube.portlets.widgets.imagepreviewerwidget.client.ui.Carousel;

import com.github.gwtbootstrap.client.ui.Image;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * Class to show an attached file.
 * @author Costantino Perciante at ISTI-CNR
 *
 */
public class AttachmentPreviewer extends Composite {	

	private static AttachmentPreviewerUiBinder uiBinder = GWT
			.create(AttachmentPreviewerUiBinder.class);

	interface AttachmentPreviewerUiBinder extends
	UiBinder<Widget, AttachmentPreviewer> {
	}

	public AttachmentPreviewer() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField
	HTMLPanel attachmentContainer;

	@UiField
	Image imagePreview;

	@UiField
	Label fileNameLabel;

	@UiField
	Anchor downloadLabel;

	@UiField
	Anchor showPreviewLabel;

	@UiField
	Label labelSeparator;

	// save attachment
	private Attachment attachment;

	// carousel reference
	private Carousel carousel;

	// enhanced image associated with this carousel
	private EnhancedImage img;

	public AttachmentPreviewer(Attachment a) {

		// init
		initWidget(uiBinder.createAndBindUi(this));

		// save the attachment
		attachment = a;

		// set image preview
		imagePreview.setUrl(a.getThumbnailURL());
		imagePreview.setStyleName("image-preview-attachment");
		
		// set file name (be careful on file name length)
		String shownName = a.getName().length() > 21 ? a.getName().substring(0, 18) + "..." : a.getName();
		fileNameLabel.setText(shownName);
		fileNameLabel.setTitle(a.getName());

		// download label
		downloadLabel.setText("Download");
		downloadLabel.setHref(attachment.getUri());
		downloadLabel.setTarget("_blank");
		

		// preview in case of an image
		if(a.getMimeType().contains("image/")){

			showPreviewLabel.setText("Show");
			showPreviewLabel.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {

					if(img != null)
						carousel.show(img);

				}
			});

		}else{

			// hide the show button and the separator label
			showPreviewLabel.setVisible(false);
			labelSeparator.setVisible(false);
		}

		// style links
		downloadLabel.addStyleName("link");
		showPreviewLabel.addStyleName("link");

	}

	/**
	 * Change the width of this container.
	 * @param newWidth
	 * @param unit
	 */
	public void changeAttachmentWidth(int newWidth, Unit unit){

		attachmentContainer.getElement().getStyle().setWidth(newWidth, unit);

	}

	/**
	 * Open the carousel when the user clicks on the preview's image.
	 * @param carousel
	 */
	public void onImageClickOpenCarousel(final Carousel carousel) {

		// save it
		this.carousel = carousel;

		// change cursor type on hover
		imagePreview.getElement().getStyle().setCursor(Cursor.POINTER);

		// change tooltipe
		imagePreview.setTitle("Click for a preview");

		// add handler
		imagePreview.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				carousel.show();

			}
		});
	}

	/**
	 * Open the carousel and show this enhancedImage when the user clicks on the preview's image.
	 * @param carousel
	 * @param enhancedImage the image to show
	 */
	public void onImageClickOpenCarousel(final Carousel carousel, final EnhancedImage enhancedImage) {

		// save the carousel ref.
		this.carousel = carousel;

		// save img ref
		this.img = enhancedImage;

		// change cursor type on hover
		imagePreview.getElement().getStyle().setCursor(Cursor.POINTER);

		// change tooltipe
		imagePreview.setTitle("Click for a preview");

		// add handler
		imagePreview.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				if(img != null)
					carousel.show(img);

			}
		});
	}

}
