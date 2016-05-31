package org.gcube.portlets.user.vremembers.client.panels;

import java.util.ArrayList;

import org.gcube.portlets.user.vremembers.client.MembersService;
import org.gcube.portlets.user.vremembers.client.MembersServiceAsync;
import org.gcube.portlets.user.vremembers.client.ui.DisplayBadge;
import org.gcube.portlets.user.vremembers.shared.BelongingUser;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;

public class VREMembersPanel  extends Composite {
	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final MembersServiceAsync vreMemberService = GWT.create(MembersService.class);

	public static final String loading = GWT.getModuleBaseURL() + "../images/members-loader.gif";

	private Image loadingImage;

	private VerticalPanel mainPanel = new VerticalPanel();
	public VREMembersPanel() {
		super();
		loadingImage = new Image(loading);
		mainPanel.add(loadingImage);
		showLoader();
		vreMemberService.getOrganizationUsers(new AsyncCallback<ArrayList<BelongingUser>>() {

			@Override
			public void onSuccess(ArrayList<BelongingUser> users) {
				mainPanel.clear();
				mainPanel.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
				mainPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
				if (users == null || users.isEmpty()) {
					mainPanel.add(new HTML("<div class=\"frame\" style=\"font-size: 16px;\">Ops, something went wrong. Please <a href=\"javascript: location.reload();\">reload<a/> this page.</div>"));
				} else {
					Grid grid = new Grid(users.size()/4+1, 4);
					mainPanel.add(grid);

					for (int i = 0; i < users.size(); i++) {
						grid.setWidget(i/4, i%4, new DisplayBadge(users.get(i)));
					}
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				mainPanel.add(new HTML("<div class=\"nofeed-message\">" +
						"Sorry, looks like something is broken with the server connection<br> " +
						"Please check your connection and try refresh this page.</div>"));

			}
		});
		initWidget(mainPanel);

	}


	private void showLoader() {
		mainPanel.clear();
		mainPanel.setWidth("100%");
		mainPanel.setHeight("300px");
		mainPanel.setHorizontalAlignment(HasAlignment.ALIGN_CENTER);
		mainPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		mainPanel.add(loadingImage);
	}
}
