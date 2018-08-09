package org.gcube.portlets.user.questions.client;

import java.util.ArrayList;

import org.gcube.common.portal.GCubePortalConstants;
import org.gcube.portal.databook.client.GCubeSocialNetworking;
import org.gcube.portlets.user.questions.client.resources.Images;
import org.gcube.portlets.user.questions.shared.GroupDTO;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Paragraph;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.resources.ButtonSize;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;

public class GroupsPanel extends Composite {

	private final QuestionsServiceAsync service = GWT.create(QuestionsService.class);

	private Image loadingImage;
	// main panel
	private VerticalPanel mainPanel = new VerticalPanel();


	public GroupsPanel() {
		super();
		initWidget(mainPanel);

		// set main panel width
		mainPanel.setWidth("100%");

		Images images = GWT.create(Images.class);
		loadingImage = new Image(images.membersLoader().getSafeUri());

		// show loaders for the panels
		showLoader(mainPanel);

		service.getGroups(new AsyncCallback<ArrayList<GroupDTO>>() {

			@Override
			public void onSuccess(ArrayList<GroupDTO> groups) {
				mainPanel.clear();
				mainPanel.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
				mainPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
				int i = 0;
				for (GroupDTO g : groups) {
					if (i == 1)
						mainPanel.add(new Paragraph("Groups in this VRE:"));
					Button toAdd = new Button(g.getGroupName());

					toAdd.setType(ButtonType.LINK);
					toAdd.setSize(ButtonSize.DEFAULT);
					toAdd.setHref(g.getViewGroupURL());		
					mainPanel.add(toAdd);

					if (g.isManager()) {
						toAdd.setSize(ButtonSize.LARGE);
					}
					toAdd.setCustomIconStyle("fa fa-users");
					i++;
				}

			}

			@Override
			public void onFailure(Throwable caught) {
				mainPanel.clear();
				mainPanel.add(new HTML("<div class=\"nofeed-message\">" +
						"Sorry, looks like something is broken with the server connection<br> " +
						"Please check your connection and try refresh this page.</div>"));

			}
		});

	}



	private void showLoader(VerticalPanel panel) {
		panel.clear();
		panel.setWidth("100%");
		panel.setHorizontalAlignment(HasAlignment.ALIGN_CENTER);
		panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		panel.add(loadingImage);
	}
}
