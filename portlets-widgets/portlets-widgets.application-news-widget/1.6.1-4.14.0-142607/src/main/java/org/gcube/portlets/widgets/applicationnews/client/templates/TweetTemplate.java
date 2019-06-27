/**
 * 
 */
package org.gcube.portlets.widgets.applicationnews.client.templates;

import org.gcube.portal.databook.shared.Feed;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author massi
 *
 */
public class TweetTemplate extends Composite {

	private static TweetTemplateUiBinder uiBinder = GWT.create(TweetTemplateUiBinder.class);

	interface TweetTemplateUiBinder extends UiBinder<Widget, TweetTemplate> {
	}
	private static final int MAX_SHOWTEXT_LENGTH = 256;
	@UiField
	HTML contentArea;
	@UiField
	HTML seeMore;
	@UiField
	HTML timeArea;
	@UiField
	HTML likeArea;
	@UiField
	HTML commentArea;
	@UiField
	HTML messageArea;
	@UiField
	Image avatarImage;
	@UiField
	HTMLPanel mainHTML;
	@UiField 
	VerticalPanel previewPanel;
	@UiField
	Label messageSeparator;

	/**
	 *  used when fetching tweets from server
	 * @param myUserInfo
	 * @param myFeed
	 * @param isUsers
	 * @param eventBus
	 */
	public TweetTemplate(Feed feed) {
		initWidget(uiBinder.createAndBindUi(this));

		if (feed.getLinkTitle() != null && feed.getLinkTitle().compareTo("") != 0 ) {
			previewPanel.add(new LinkPreviewer(feed.getLinkTitle(), feed.getLinkDescription(), feed.getLinkHost(), feed.getUriThumbnail(), feed.getUri()));
		}

		likeArea.setHTML("<a>Like</a>");

		commentArea.setHTML("<a>Reply</a>");

		String feedText = feed.getDescription();
		if ( (! feedText.startsWith("<span")) && feedText.length() > MAX_SHOWTEXT_LENGTH) {
			feedText = feedText.substring(0, MAX_SHOWTEXT_LENGTH) + "...";
			seeMore.setHTML("<a class=\"seemore\"> See More </a>");
		}

		//			messageSeparator.setVisible(false);
		contentArea.setHTML("<span class=\"link\">"+feed.getFullName()+"</span> " + feedText);
	
		String vreName = feed.getVreid().substring(feed.getVreid().lastIndexOf("/")+1);
		messageArea.setHTML("<a> go App [" +vreName + "]</a>");



		avatarImage.setUrl(feed.getThumbnailURL());
		avatarImage.setPixelSize(50, 50);
		try {
			String formattedTime = DateTimeFormat.getFormat("MMMM dd, h:mm a").format(feed.getTime());
			timeArea.setHTML(formattedTime);
		}
		catch (NumberFormatException e) {
		}
		catch (Exception e) {
			timeArea.setHTML("just now");
		}
	}
	public void setcontentAreaStyle(String cssclass) {
		contentArea.getElement().getParentElement().getParentElement().setClassName("div-table-col content visible");
	}
}
