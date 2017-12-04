package org.gcube.portlets.user.joinvre.client.responsive;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.gcube.portlets.user.joinvre.client.JoinService;
import org.gcube.portlets.user.joinvre.client.JoinServiceAsync;
import org.gcube.portlets.user.joinvre.client.ui.AccessVREDialog;
import org.gcube.portlets.user.joinvre.client.ui.AccessViaInviteDialog;
import org.gcube.portlets.user.joinvre.client.ui.RequestMembershipDialog;
import org.gcube.portlets.user.joinvre.client.ui.VreThumbnail;
import org.gcube.portlets.user.joinvre.shared.VRE;
import org.gcube.portlets.user.joinvre.shared.VRECategory;

import com.github.gwtbootstrap.client.ui.PageHeader;
import com.github.gwtbootstrap.client.ui.Row;
import com.github.gwtbootstrap.client.ui.Tab;
import com.github.gwtbootstrap.client.ui.Thumbnails;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;

public class ResponsivePanel extends Composite {

	private final JoinServiceAsync joinService = GWT.create(JoinService.class);
	public static final String loading = GWT.getModuleBaseURL() + "../images/vre-loader.gif";
	private Row mainPanel = new Row();
	private ArrayList<VreThumbnail> myThumbnails = new ArrayList<VreThumbnail>();
	
	/**
	 * default constructor (No tabs)
	 */
	public ResponsivePanel() {
		GWT.log("ResponsivePanel()");
		joinService.getVREs(new AsyncCallback<LinkedHashMap<VRECategory, ArrayList<VRE>>>() {
			@Override
			public void onSuccess(LinkedHashMap<VRECategory, ArrayList<VRE>> categories) {
				populatePanel(categories);
			}
			@Override
			public void onFailure(Throwable caught) {
				showError("Sorry, looks like something is broken with the server connection");
			}
		});
		initWidget(mainPanel);
	}
	/**
	 * constructor used when one tab is clicked
	 * @param selectedTab
	 */
	public ResponsivePanel(Tab selectedTab) {
		final String tabName = selectedTab.getHeading();
		joinService.getPortalSitesMappedToVRE(tabName, new AsyncCallback<LinkedHashMap<VRECategory, ArrayList<VRE>>>() {
			@Override
			public void onSuccess(LinkedHashMap<VRECategory, ArrayList<VRE>> categories) {
				populatePanel(categories);
			}
			@Override
			public void onFailure(Throwable caught) {
				showError("Sorry, looks like something is broken with the server connection");
			}
		});
		initWidget(mainPanel);
	}
	/**
	 * constructor used when one category is selected
	 * @param category
	 */
	public ResponsivePanel(VRECategory category) {
		GWT.log("ResponsivePanel()");
		joinService.getVREsByCategory(category.getName(), new AsyncCallback<LinkedHashMap<VRECategory, ArrayList<VRE>>>() {
			@Override
			public void onSuccess(LinkedHashMap<VRECategory, ArrayList<VRE>> categories) {
				populatePanel(categories);
			}
			@Override
			public void onFailure(Throwable caught) {
				showError("Sorry, looks like something is broken with the server connection");
			}
		});
		initWidget(mainPanel);
	}
	/**
	 * constructor used when one organisation is selected
	 * @param category
	 */
	public ResponsivePanel(String organisation) {
		joinService.getVREsByOrganisation(organisation, new AsyncCallback<LinkedHashMap<VRECategory, ArrayList<VRE>>>() {
			@Override
			public void onSuccess(LinkedHashMap<VRECategory, ArrayList<VRE>> categories) {
				populatePanel(categories);
			}
			@Override
			public void onFailure(Throwable caught) {
				showError("Sorry, looks like something is broken with the server connection");
			}
		});
		initWidget(mainPanel);
	}

	private void populatePanel(LinkedHashMap<VRECategory, ArrayList<VRE>> categories) {
		mainPanel.clear();
		if (categories == null || categories.isEmpty()) {
			showError("Ops, something went wrong");
		}
		else {
			for (VRECategory cat : categories.keySet()) {
				GWT.log("cat: " + cat.getName());
				PageHeader header = new PageHeader();
				header.setText(cat.getName());
				if (cat.getDescription() == null || cat.getDescription().compareTo("") == 0  || cat.getDescription().compareTo("empty") == 0 )
					header.setSubtext("");
				else
					header.setSubtext(cat.getDescription());
				if (categories.get(cat).size() > 0) {
					mainPanel.add(header);
					mainPanel.add(getVREThumbnails(categories, cat));
				}
			}
		}
	}


	private Thumbnails getVREThumbnails(LinkedHashMap<VRECategory, ArrayList<VRE>> categories, VRECategory category) {
		ArrayList<VRE> vres = categories.get(category);
		Thumbnails toReturn = new Thumbnails();
		for (VRE vre : vres) {
			VreThumbnail thumb = new VreThumbnail(vre);
			myThumbnails.add(thumb);
			toReturn.add(thumb);
		}
		return toReturn;
	}

	protected void showError(String message) {
		mainPanel.clear();
		mainPanel.add(new HTML("<div class=\"frame\" style=\"font-size: 16px;\">" + message + ". Please <a href=\"javascript: location.reload();\">reload</a> this page.</div>"));
	}

	public void requestMembership(VRE vre) {
		switch (vre.getMembershipType()) {
		case RESTRICTED:
			RequestMembershipDialog modal = new RequestMembershipDialog(this, vre);
			modal.show();
			break;
		case OPEN:
			AccessVREDialog accessModal = new AccessVREDialog(vre);
			accessModal.show();
			break;
		default:
			break;
		}		 
	}

	public void showInviteDialog(VRE vre, String inviteId) {
		AccessViaInviteDialog modal = new AccessViaInviteDialog(vre, inviteId);
		modal.show();
	}

	public void setPending(VRE toSet) {
		for (VreThumbnail thumb : myThumbnails) {
			if (thumb.getMyVre().getinfraScope().compareTo(toSet.getinfraScope()) == 0) {
				thumb.setPending();
			}
		}
	}

}



