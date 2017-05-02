package org.gcube.portlets.admin.invitessent.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.gcube.portal.databook.shared.Invite;
import org.gcube.portal.databook.shared.InviteStatus;
import org.gcube.portlets.admin.invitessent.client.ui.InvitesTable;

import com.github.gwtbootstrap.client.ui.CellTable;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.NavTabs;
import com.github.gwtbootstrap.client.ui.TabPane;
import com.github.gwtbootstrap.client.ui.TabPanel;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
/**
*
* @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
*/
public class InvitesSent implements EntryPoint {
	private final InvitesServiceAsync invitesService = GWT.create(InvitesService.class);
	private VerticalPanel mainPanel = new VerticalPanel();

	private TabPanel navTabs = new TabPanel();
	private TabPane allInvites = new TabPane("All invites");
	private TabPane pendingInvites = new TabPane("Pending invites");
	private TabPane acceptedInvites = new TabPane("Accepted invites");
	
	public void onModuleLoad() {
		showLoader();
		InviteStatus[] statuses = {InviteStatus.PENDING, InviteStatus.ACCEPTED, InviteStatus.REJECTED, InviteStatus.RETRACTED};
		invitesService.getInvites(statuses, new AsyncCallback<ArrayList<Invite>>() {

			@Override
			public void onSuccess(ArrayList<Invite> invites) {
				mainPanel.clear();
				allInvites.add(new InvitesTable(invites));				
			}
			@Override
			public void onFailure(Throwable caught) {
				showProblems();
			}
		});
		
		navTabs.add(allInvites);
		navTabs.add(pendingInvites);
		navTabs.add(acceptedInvites);
		
		allInvites.setActive(true);
		navTabs.selectTab(0);
				
		// Add it to the root panel.
		RootPanel.get("sent-invites-div").add(navTabs);
		
		InviteStatus[] pending = {InviteStatus.PENDING};
		invitesService.getInvites(pending, new AsyncCallback<ArrayList<Invite>>() {

			@Override
			public void onSuccess(ArrayList<Invite> invites) {
				mainPanel.clear();
				pendingInvites.add(new InvitesTable(invites));				
			}
			@Override
			public void onFailure(Throwable caught) {
				showProblems();
			}
		});
		
		InviteStatus[] accepted = {InviteStatus.ACCEPTED};
		invitesService.getInvites(accepted, new AsyncCallback<ArrayList<Invite>>() {

			@Override
			public void onSuccess(ArrayList<Invite> invites) {
				mainPanel.clear();
				acceptedInvites.add(new InvitesTable(invites));				
			}
			@Override
			public void onFailure(Throwable caught) {
				showProblems();
			}
		});
	}
	public static final String loading = GWT.getModuleBaseURL() + "../images/loader.gif";

	private void showLoader() {
		mainPanel.clear();
	
		mainPanel.setHorizontalAlignment(HasAlignment.ALIGN_CENTER);
		mainPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		mainPanel.add(new Image(loading));
	}

	private void showProblems() {
		mainPanel.clear();
		mainPanel.add(new HTML("<div class=\"nofeed-message\">" +
				"Ops! There were problems while retrieving Invites!. <br> " +
				"Looks like we are not able to communicate with the infrastructure,<br> (or your session expired)<br> please try again in a short while or refresh the page.</div>"));
	}
}
