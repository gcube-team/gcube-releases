package org.gcube.portlets.user.shareupdates.client.view;

import org.gcube.portlets.user.shareupdates.shared.LinkPreview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.github.gwtbootstrap.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

/**
 * Class used to show a link preview.
 * @author Massimiliano Assante at ISTI-CNR
 *
 */
public class LinkPreviewer extends Composite {
	
	private static final String HTTP_ERROR_301 = "Moved Permanently";
	
	private static LinkPreviewUiBinder uiBinder = GWT
			.create(LinkPreviewUiBinder.class);

	interface LinkPreviewUiBinder extends UiBinder<Widget, LinkPreviewer> {
	}
	
	private ShareUpdateForm parent;
	
	private LinkPreview toShow;
	
	private boolean showImage = true;
	
	@UiField 
	HTML closeImage;
	@UiField 
	ImageSwitcher switcher;
	@UiField
	HTML titleArea;
	@UiField
	HTML urlText;
	@UiField
	HTML descText;
	@UiField
	CheckBox hideCheckBox;
	@UiField
	CheckBox hideImageCheckBox;

	public LinkPreviewer(ShareUpdateForm parent, LinkPreview toShow) {
		initWidget(uiBinder.createAndBindUi(this));
		closeImage.setStyleName("su-closeImage");
		closeImage.setTitle("Cancel");
		this.parent = parent;
		this.toShow = toShow;
		
		String title = toShow.getTitle();
		String desc = toShow.getDescription();
		if (title.compareTo(HTTP_ERROR_301) == 0) {
			this.toShow.setTitle(title = "HTTP Link");
			this.toShow.setDescription(desc = "");
			this.showImage = false;
		}	
		titleArea.setHTML("<a class=\"link\" target=\"_blank\" href=\"" + title + "\">" + title + "</a> <span style=\"color: #333;\"> - " + toShow.getHost() + "</span>");
		descText.setHTML((desc.length() > 256) ? desc.substring(0, 256)+"..." : desc);
		String url = toShow.getUrl();
		urlText.setHTML((url.length() > 80) ? url.substring(0, 80)+"..." : url);
		
		switcher.setImages(toShow.getImageUrls());
	}

	public ImageSwitcher getSwitcher() {
		return switcher;
	}
	
	@UiHandler("closeImage") 
	void onDeleteFeedClick(ClickEvent e) {
		parent.cancelLinkPreview();
	}	
	
	@UiHandler("hideImageCheckBox") 
	void onHideImageClick(ClickEvent e) {
		switcher.setVisible(!hideImageCheckBox.getValue());
	}
	
	@UiHandler("hideCheckBox") 
	void onClick(ClickEvent e) {
		descText.setVisible(!hideCheckBox.getValue());
	}
	
	public String getLinkTitle() {
		return toShow.getTitle();		
	}
	public String getLinkDescription() {
		return hideCheckBox.getValue() ? "" : toShow.getDescription();
	}
	public String getUrl() {
		return toShow.getUrl();
	}
	public String getHost() {
		return toShow.getHost();
	}
	public String getUrlThumbnail() {
		if (!showImage)
			return null;
		return switcher.getSelectedImageURL();
	}
}
