package org.gcube.portlets.user.questions.client;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portal.databook.shared.UserInfo;
import org.gcube.portlets.user.questions.client.resources.Images;
import org.gcube.portlets.user.questions.client.ui.DisplayBadge;
import org.gcube.portlets.widgets.wsmail.client.forms.MailForm;

import com.github.gwtbootstrap.client.ui.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class VREManagersPanel  extends Composite {
	
	private final QuestionsServiceAsync service = GWT.create(QuestionsService.class);
	public static final String DISPLAY_NAME =  "Questions? Ask the managers";

	private Image loadingImage;
	private Image postToImage;	
	private Button messageManagers = new Button();

	private VerticalPanel mainPanel = new VerticalPanel();
	private ArrayList<UserInfo> managers;
	public VREManagersPanel() {
		super();
		Images images = GWT.create(Images.class);
		loadingImage = new Image(images.membersLoader().getSafeUri());
		postToImage =  new Image(images.postToIcon().getSafeUri());
		
		mainPanel.add(loadingImage);
		showLoader();
		service.getManagers(new AsyncCallback<ArrayList<UserInfo>>() {

			@Override
			public void onSuccess(ArrayList<UserInfo> users) {
				managers = users;
				mainPanel.clear();
				mainPanel.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
				mainPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
				mainPanel.setStyleName("questions-frame");
				HTML name = new HTML(DISPLAY_NAME);
				
				name.setStyleName("questions-title");
				HorizontalPanel hp = new HorizontalPanel();
				hp.add(name);
				postToImage.setStyleName("manager-post-image");
				postToImage.setTitle("Message privately to the Managers");
			//	hp.add(postToImage);
				mainPanel.add(hp);
				if (users == null || users.isEmpty()) {
					mainPanel.add(new HTML("<div class=\"frame\" style=\"font-size: 16px;\">Ops, something went wrong. Please <a href=\"javascript: location.reload();\">reload<a/> this page.</div>"));
				} else {
					for (int i = 0; i < users.size(); i++) {
						mainPanel.add(new DisplayBadge(users.get(i)));
					}
					if (users.size() > 1)
						messageManagers.setText("Message managers");
					else 
						messageManagers.setText("Message manager");
				}
				SimplePanel bPanel = new SimplePanel();
				bPanel.setStyleName("manager-action");
				bPanel.setWidget(messageManagers);
				mainPanel.add(bPanel);
			}

			@Override
			public void onFailure(Throwable caught) {
				mainPanel.add(new HTML("<div class=\"nofeed-message\">" +
						"Sorry, looks like something is broken with the server connection<br> " +
						"Please check your connection and try refresh this page.</div>"));

			}
		});
		initWidget(mainPanel);

		messageManagers.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				final List<String> listToLogin = new ArrayList<String>();
				for (UserInfo user : managers) {
					listToLogin.add(user.getUsername());
				}
				
				GWT.runAsync(new RunAsyncCallback() {
					@Override
					public void onSuccess() {
						new MailForm(listToLogin);
					}
					public void onFailure(Throwable reason) {
						Window.alert("Could not load this component: " + reason.getMessage());
					}   
				});				
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
}
