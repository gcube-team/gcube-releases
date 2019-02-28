package org.gcube.portlets.user.shareupdates.client.view;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ImageSwitcher extends Composite {

	public static final String blank_image_url = GWT.getModuleBaseURL() + "../images/transparent.png";

	private ArrayList<String> imageUrls;
	private VerticalPanel mainPanel = new VerticalPanel();
	private HorizontalPanel controlPanel = new HorizontalPanel();
	private Image image = new Image();
	private HTML prev = new HTML(" << ");
	private HTML label = new HTML("1 of 1");
	private HTML next = new HTML(" >> ");
	private int currentIndex = 0;
	
	private boolean visible = true;

	public ImageSwitcher() {
		super();
		image.setWidth("80px");
		mainPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		mainPanel.setPixelSize(100, 100);
		mainPanel.add(image);

		controlPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		controlPanel.add(prev);
		controlPanel.add(label);
		controlPanel.add(next);

		mainPanel.add(controlPanel);

		prev.setStyleName("small-text-arrow");
		label.setStyleName("small-text");
		next.setStyleName("small-text-arrow");


		initWidget(mainPanel);

		next.addClickHandler(new ClickHandler() {			
			public void onClick(ClickEvent event) {
				if (imageUrls != null && currentIndex < imageUrls.size()) {
					currentIndex++;
					image.setUrl(imageUrls.get(currentIndex));
					label.setHTML((currentIndex+1)+" of " + imageUrls.size());
				}
			}
		});

		prev.addClickHandler(new ClickHandler() {			
			public void onClick(ClickEvent event) {
				if (imageUrls != null && currentIndex > 0) {
					currentIndex--;
					image.setUrl(imageUrls.get(currentIndex));
					label.setHTML((currentIndex+1)+" of " + imageUrls.size());
				}
			}
		});
	}



	protected void setImages(ArrayList<String> imageUrls) {
		this.imageUrls = imageUrls;
		if (imageUrls.size() == 0) {
			mainPanel.remove(controlPanel);
			return;
		}
		image.setUrl(imageUrls.get(0));
		GWT.log("images no. " + imageUrls.size());
		if (imageUrls.size() < 2) {
			prev.setVisible(false);
			next.setVisible(false);
		} else {
			enableSwitch(imageUrls);
		}			
	}

	private void enableSwitch(ArrayList<String> imageUrls) {
		label.setHTML("1 of " + imageUrls.size());
		currentIndex = 0;
	}

	protected String getSelectedImageURL() {
		if (!visible)
			return null;
		return image.getUrl();
	}
	/**
	 * because i don't want it to be removed from DOM
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
		if (visible) {
			image.getElement().getStyle().setVisibility(Visibility.VISIBLE);
			controlPanel.getElement().getStyle().setVisibility(Visibility.VISIBLE);
		}
		else {
			image.getElement().getStyle().setVisibility(Visibility.HIDDEN);
			controlPanel.getElement().getStyle().setVisibility(Visibility.HIDDEN);
		}
	}
}
