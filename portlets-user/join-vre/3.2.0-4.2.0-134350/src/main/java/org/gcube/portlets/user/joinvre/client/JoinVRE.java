package org.gcube.portlets.user.joinvre.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.gcube.portlets.user.joinvre.client.responsive.ResponsivePanel;
import org.gcube.portlets.user.joinvre.shared.VRE;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 */
public class JoinVRE implements EntryPoint {
	Logger logger = Logger.getLogger(JoinVRE.class.getName());
	public static final String GET_OID_PARAMETER = "siteId";
	private final JoinServiceAsync joinService = GWT.create(JoinService.class);

	public void onModuleLoad() {
		checkIsReferral();		
	}
	/**
	 * first async callback
	 */
	private void checkIsReferral() {
		logger.log(Level.INFO,"checkIsReferral()");
		if (getSiteLiferayId() == null) {
			displayVREs();
		}
		else {
			Long vreId = -1L;
			try {
				vreId = Long.parseLong(getSiteLiferayId());
			}
			catch (Exception ex) {
				logger.log(Level.WARNING, "site id is not a number " + ex.getMessage());
				return;
			}
			joinService.getSelectedVRE(vreId, new AsyncCallback<VRE>() {
				@Override
				public void onFailure(Throwable caught) {
					logger.log(Level.SEVERE,"getSelectedVRE error " + caught.getMessage());
					Window.alert("Server error");
				}
				@Override
				public void onSuccess(final VRE vre) {
					logger.log(Level.INFO, "A VRE was Returned");	
					if (vre == null) {
						GWT.log("A VRE Returned is null");	
						displayVREs();
						return;
					}
					else {
						ResponsivePanel rp = null;
						logger.log(Level.INFO, "A Valid VRE was Returned");	
						switch (vre.getUserBelonging()) {
						case BELONGING:
							logger.log(Level.INFO, "User is Belonging");	
							Location.assign(vre.getFriendlyURL());
							break;
						case PENDING:
							logger.log(Level.INFO, "User is Pending");	
							rp = displayVREs();
							break;
						default: //Not belonging
							logger.log(Level.INFO, "User is NOT Belonging");	
							rp = displayVREs();
							checkInvitation(vre, vre.getId(), rp);
							break;
						}
					}
				}
			});			
		}
	}
	private ResponsivePanel displayVREs() {
		ResponsivePanel toReturn = new ResponsivePanel();
		RootPanel.get("JoinVRE-Container").add(toReturn);
		return toReturn;
	}
	/**
	 * check if it has to show just one feed
	 * @return
	 */
	private String getSiteLiferayId() {
		return Window.Location.getParameter(GET_OID_PARAMETER);
	}
	/**
	 * 
	 * @param vre
	 * @param groupId
	 * @param rp
	 */
	private void checkInvitation(final VRE vre, final long groupId, final ResponsivePanel rp) {
		joinService.isExistingInvite(groupId, new AsyncCallback<String>() {
			@Override
			public void onSuccess(String inviteId) {
				//inviteId != null = there exist an invite
				if (inviteId != null) {
					rp.showInviteDialog(vre, inviteId);
				} else
					rp.requestMembership(vre);
			}
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Server error");				
			}
		});
	}

}
