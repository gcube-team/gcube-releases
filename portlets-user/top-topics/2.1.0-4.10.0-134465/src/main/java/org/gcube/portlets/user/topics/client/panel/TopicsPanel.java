package org.gcube.portlets.user.topics.client.panel;

import java.util.ArrayList;

import org.gcube.portlets.user.topics.client.TopicService;
import org.gcube.portlets.user.topics.client.TopicServiceAsync;
import org.gcube.portlets.user.topics.shared.HashtagsWrapper;

import com.github.gwtbootstrap.client.ui.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
	public static final int THRESHOLD_SHOW_HASHTAGS = 10; // show the first X ones
	private static final String NO_TOP_TOPICS_MESSAGE = "No Topics found in News Feed";

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
					int counter = 0;
					if (hashtags != null && !hashtags.isEmpty()) {
						for (String hashtag : hashtags) {
							counter ++;
							HTML toAdd = new HTML(hashtag);
							toAdd.addStyleName("hashtag-label");
							mainPanel.add(toAdd);

							if(counter > THRESHOLD_SHOW_HASHTAGS) // 11, 12...
								toAdd.setVisible(false);
						}

						// add a show all button if needed
						if(counter > THRESHOLD_SHOW_HASHTAGS){

							final Button showAllHashtags = new Button("Show All");

							showAllHashtags.addClickHandler(new ClickHandler() {

								@Override
								public void onClick(ClickEvent event) {

									int numberChildren = mainPanel.getWidgetCount();
									for (int i = THRESHOLD_SHOW_HASHTAGS; i < numberChildren; i++) { 

										mainPanel.getWidget(i).setVisible(true);

									}

									// hide the button
									showAllHashtags.setVisible(false);
								}
							});
							mainPanel.add(showAllHashtags);
						}

					}else{

						mainPanel.add(new HTML(NO_TOP_TOPICS_MESSAGE));

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
