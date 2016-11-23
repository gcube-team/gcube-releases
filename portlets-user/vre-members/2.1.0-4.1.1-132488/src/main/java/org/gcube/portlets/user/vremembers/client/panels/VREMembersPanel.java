package org.gcube.portlets.user.vremembers.client.panels;

import java.util.ArrayList;

import org.gcube.portal.databook.client.GCubeSocialNetworking;
import org.gcube.portal.databook.client.util.Encoder;
import org.gcube.portlets.user.vremembers.client.MembersService;
import org.gcube.portlets.user.vremembers.client.MembersServiceAsync;
import org.gcube.portlets.user.vremembers.client.ui.DisplayBadge;
import org.gcube.portlets.user.vremembers.shared.BelongingUser;
import org.gcube.portlets.user.vremembers.shared.VREGroup;

import com.github.gwtbootstrap.client.ui.PageHeader;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

public class VREMembersPanel extends FlowPanel {

	private final MembersServiceAsync vreMemberService = GWT.create(MembersService.class);

	public VREMembersPanel() {
		//if showing a VRE Group
		if (getGroupShowId() != null) {
			String teamId = Encoder.decode(getGroupShowId());
			if (teamId.compareTo("-100")==0) {
				vreMemberService.getVREManagers(new AsyncCallback<VREGroup>() {

					@Override
					public void onFailure(Throwable caught) {
						add(new HTML("<div class=\"nofeed-message\">" +
								"Sorry, looks like something is broken with the server connection<br> " +
								"Please check your connection and try refresh this page.</div>"));					
					}

					@Override
					public void onSuccess(VREGroup group) {
						clear();
						PageHeader toAdd = new PageHeader();
						toAdd.setText("VRE managers");
						toAdd.setSubtext("The moderators of this VRE");
						add(toAdd);
						if (group.getUsers().size() > 0)
							showMembers(group.getUsers());			
						else
							add(new HTML("<div class=\"nofeed-message\">There are no VRE Managers in this VRE, this is weird please report this issue.</div>"));				
					}
				});
			} else {
				vreMemberService.getVREGroupUsers(teamId, new AsyncCallback<VREGroup>() {

					@Override
					public void onFailure(Throwable caught) {
						add(new HTML("<div class=\"nofeed-message\">" +
								"Sorry, looks like something is broken with the server connection<br> " +
								"Please check your connection and try refresh this page.</div>"));					
					}

					@Override
					public void onSuccess(VREGroup group) {
						clear();
						PageHeader toAdd = new PageHeader();
						toAdd.setText(group.getName());
						toAdd.setSubtext(group.getDescription());
						add(toAdd);
						if (group.getUsers().size() > 0)
							showMembers(group.getUsers());			
						else
							add(new HTML("<div class=\"nofeed-message\">This group has no members, VRE Managers can define VRE groups and associate members to these groups.</div>"));				
					}
				});
			}
		} else { //show all the VRE Members
			vreMemberService.getSiteUsers(new AsyncCallback<ArrayList<BelongingUser>>() {
				@Override
				public void onSuccess(ArrayList<BelongingUser> users) {
					clear();
					showMembers(users);
				}

				@Override
				public void onFailure(Throwable caught) {
					add(new HTML("<div class=\"nofeed-message\">" +
							"Sorry, looks like something is broken with the server connection<br> " +
							"Please check your connection and try refresh this page.</div>"));

				}
			});
		}
	}
	private void showMembers(ArrayList<BelongingUser> users) {
		if (users == null || users.isEmpty()) {
			add(new HTML("<div class=\"frame\" style=\"font-size: 16px;\">Ops, something went wrong. Please <a href=\"javascript: location.reload();\">reload<a/> this page.</div>"));
		} else {
			for (int i = 0; i < users.size(); i++) {

				add(new DisplayBadge(users.get(i)));
			}
		}
	}
	/**
	 * check if it has to show a group
	 * @return
	 */
	private String getGroupShowId() {
		return Window.Location.getParameter(Encoder.encode(GCubeSocialNetworking.GROUP_MEMBERS_OID));
	}
	
}
