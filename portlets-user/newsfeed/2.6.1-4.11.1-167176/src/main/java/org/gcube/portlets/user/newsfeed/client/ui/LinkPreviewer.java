package org.gcube.portlets.user.newsfeed.client.ui;

import org.gcube.portlets.widgets.imagepreviewerwidget.client.ui.Carousel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * Link Previewer class to show a link a preview.
 * @author Costantino Perciante at ISTI-CNR
 * @author Massimiliano Assante at ISTI-CNR
 */
public class LinkPreviewer extends Composite {

	// is the preview image still there ?
	private boolean imageRemoved = false;

	@UiField
	HTML titleArea;
	@UiField
	HTML urlText;
	@UiField
	HTML descText;	
	@UiField
	Image image;

	// saved file name
	private final String fileName;

	private static LinkPreviewUiBinder uiBinder = GWT
			.create(LinkPreviewUiBinder.class);

	interface LinkPreviewUiBinder extends UiBinder<Widget, LinkPreviewer> {
	}

	public LinkPreviewer(String title, String titleDesc, String host, String linkThumbUrl, String url) {
		initWidget(uiBinder.createAndBindUi(this));
		if (linkThumbUrl == null || linkThumbUrl.equals("null")){

			image.removeFromParent();
			imageRemoved = true;

		}
		else {

			image.setUrl(linkThumbUrl);
			image.setWidth("80px");

		}

		// save the filename info
		fileName = title;
		titleArea.setHTML("<a class=\"link\" target=\"_blank\" href=\"" + url + "\">"+title+"</a> <span style=\"color: #333;\"> - " + host+ "</span>");
		urlText.setHTML((url.length() > 70) ? url.substring(0, 70)+"..." : url);
		String desc = titleDesc;
		descText.setHTML((desc.length() > 256) ? desc.substring(0, 256)+"..." : desc);		

	}

	/**
	 * Open the carousel when the user clicks on the preview's image.
	 * @param carousel
	 */
	public void onImageClickOpenCarousel(final Carousel carousel) {

		if(imageRemoved)
			return;

		// change cursor type on hover
		image.getElement().getStyle().setCursor(Cursor.POINTER);

		// add handler
		image.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				carousel.show();

			}
		});
	}

	/**
	 * Open the carousel when the user clicks on the previews' file name.
	 * @param carousel
	 */
	public void onFileNameClickOpenCarousel(final Carousel carousel) {

		String nameToShow = fileName.length() > 40 ? fileName.substring(0, 40) + "..." : fileName;
		titleArea.setHTML("<a class=\"link\" >"+ nameToShow +"</a>");
		titleArea.setTitle(fileName);
		
		titleArea.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				carousel.show();

			}
		});

	}
}
