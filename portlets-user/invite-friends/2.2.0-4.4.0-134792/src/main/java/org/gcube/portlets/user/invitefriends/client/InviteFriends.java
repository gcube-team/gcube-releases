package org.gcube.portlets.user.invitefriends.client;

import org.gcube.portlets.widgets.inviteswidget.client.ui.InviteWidget;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;
/**
 * 
 * @author Massimiliano Assante, ISTI-CNR
 *
 */
public class InviteFriends implements EntryPoint {
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		RootPanel.get("invite-friends-DIV").add(new InviteWidget());
	}
}
