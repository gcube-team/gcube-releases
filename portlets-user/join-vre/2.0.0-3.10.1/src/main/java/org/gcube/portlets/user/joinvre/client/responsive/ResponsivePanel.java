package org.gcube.portlets.user.joinvre.client.responsive;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.gcube.portlets.user.joinvre.client.JoinService;
import org.gcube.portlets.user.joinvre.client.JoinServiceAsync;
import org.gcube.portlets.user.joinvre.client.ui.VreThumbnail;
import org.gcube.portlets.user.joinvre.shared.VRE;
import org.gcube.portlets.user.joinvre.shared.VRECategory;

import com.github.gwtbootstrap.client.ui.PageHeader;
import com.github.gwtbootstrap.client.ui.Thumbnails;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ResponsivePanel extends Composite {
	
	private final JoinServiceAsync joinService = GWT.create(JoinService.class);
	public static final String loading = GWT.getModuleBaseURL() + "../images/vre-loader.gif";

	private VerticalPanel mainPanel = new VerticalPanel();

	public ResponsivePanel() {
		GWT.log("ResponsivePanel()");
		joinService.getVREs(new AsyncCallback<LinkedHashMap<VRECategory, ArrayList<VRE>>>() {
			@Override
			public void onSuccess(LinkedHashMap<VRECategory, ArrayList<VRE>> categories) {
				mainPanel.clear();
				if (categories == null || categories.isEmpty()) {
					showError("Ops, something went wrong");
				}
				else {
					for (VRECategory cat : categories.keySet()) {
						GWT.log("cat: " + cat.getName());
						PageHeader header = new PageHeader();
						header.setText(cat.getName());
						header.setSubtext(cat.getDescription());
						mainPanel.add(header);
						mainPanel.add(getVREThumbnails(categories, cat));
					}
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				showError("Sorry, looks like something is broken with the server connection");

			}

		});
		initWidget(mainPanel);
	}

	private Thumbnails getVREThumbnails(LinkedHashMap<VRECategory, ArrayList<VRE>> categories, VRECategory category) {
		ArrayList<VRE> vres = categories.get(category);
		Thumbnails toReturn = new Thumbnails();
		for (VRE vre : vres) {
			VreThumbnail thumb = new VreThumbnail(vre);
			toReturn.add(thumb);
		}
		return toReturn;
	}
	
	protected void showError(String message) {
		mainPanel.clear();
		mainPanel.add(new HTML("<div class=\"frame\" style=\"font-size: 16px;\">" + message + ". Please <a href=\"javascript: location.reload();\">reload</a> this page.</div>"));
	}
}



