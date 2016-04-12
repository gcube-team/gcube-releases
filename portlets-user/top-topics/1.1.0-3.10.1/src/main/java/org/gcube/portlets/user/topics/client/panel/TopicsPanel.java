package org.gcube.portlets.user.topics.client.panel;

import java.util.ArrayList;

import org.gcube.portlets.user.topics.client.TopicService;
import org.gcube.portlets.user.topics.client.TopicServiceAsync;
import org.gcube.portlets.user.topics.shared.HashtagsWrapper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
/**
 * 
 * @author Massimiliano Assante, ISTI-CNR
 *
 */
public class TopicsPanel extends Composite {
	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final TopicServiceAsync topicsService = GWT.create(TopicService.class);

	public static final String loading = GWT.getModuleBaseURL() + "../images/topics-loader.gif";
	public static final String DISPLAY_NAME =  "Top Topics";

	private Image loadingImage;

	private VerticalPanel mainPanel = new VerticalPanel();

	public TopicsPanel() {
		loadingImage = new Image(loading);

		initWidget(mainPanel);
		showLoader();
		topicsService.getHashtags(new AsyncCallback<HashtagsWrapper>() {

			@Override
			public void onSuccess(HashtagsWrapper result) {
				mainPanel.clear();
				mainPanel.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
				mainPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);

				ArrayList<String> hashtags = result.getHashtags();
				if (hashtags == null) {
					showServError();
				}
				else {
					if (!result.isInfrastructure() && hashtags != null && !hashtags.isEmpty()) {
						mainPanel.setStyleName("trending-frame");
						HTML name = new HTML(DISPLAY_NAME);
						name.setStyleName("topic-title");
						mainPanel.add(name);
					}
					if (hashtags != null) {
						for (String hashtag : hashtags) {
							HTML toAdd = new HTML(hashtag);
							toAdd.addStyleName("hashtag-label");
							mainPanel.add(toAdd);
						}
					}		
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				showConnError();
			}
		});
	}

	private void showLoader() {
		mainPanel.clear();
		mainPanel.setWidth("100%");
		mainPanel.setHorizontalAlignment(HasAlignment.ALIGN_CENTER);
		mainPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		mainPanel.add(loadingImage);
	}

	private void showConnError() {
		mainPanel.clear();
		mainPanel.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		mainPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
		mainPanel.add(new HTML("<div class=\"nofeed-message\">" +
				"Sorry, looks like something is broken with the server connection<br> " +
				"Please check your connection and try refresh this page.</div>"));
	}

	private void showServError() {
		mainPanel.clear();
		mainPanel.add(new HTML("<div class=\"nofeed-message\">" +
				"Sorry, we have problems in our servers ...<br> " +
				"Please try in a while or report the issue.</div>"));
	}
}
