package org.gcube.portlets.user.gcubeloggedin.client.ui;

import org.gcube.portlets.user.gcubeloggedin.client.LoggedinServiceAsync;
import org.gcube.portlets.user.gcubeloggedin.shared.VObject;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Heading;
import com.github.gwtbootstrap.client.ui.Hero;
import com.github.gwtbootstrap.client.ui.Image;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class AboutView extends Composite {

	private static int MAX_CHAR_DESC = 400;
	private static String SEE_LESS = "See less";
	private static String SEE_MORE = "See more";

	private static AboutViewUiBinder uiBinder = GWT
			.create(AboutViewUiBinder.class);

	interface AboutViewUiBinder extends UiBinder<Widget, AboutView> {
	}

	public AboutView() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	private String vreDescription;
	@UiField Image vreImage;
	@UiField Heading vreName;
	@UiField HTML description;
	@UiField Button seeMore;
	@UiField Button editButton;
	@UiField Hero mainPanel;
	private EditDescriptionModal mod;

	public AboutView(VObject vobj, LoggedinServiceAsync loggedinService) {
		initWidget(uiBinder.createAndBindUi(this));
		this.vreDescription = vobj.getDescription();
		vreName.setText(vobj.getName());
		vreImage.setUrl(vobj.getImageURL());
		String desc = vreDescription = vobj.getDescription();
		if (desc.length() > MAX_CHAR_DESC) {
			desc = desc.substring(0, MAX_CHAR_DESC) + " ...";
			//description.getElement().setInnerHTML(desc);
			description.setHTML(desc);
			seeMore.setVisible(true);
			seeMore.setText(SEE_MORE);
		} else 
			description.setHTML(desc);
		description.addStyleName("vre-description");
		if (vobj.isManager()) {
			editButton.setVisible(true);
		}
		mod = new EditDescriptionModal(vobj.getName(), vobj.getDescription());
	}	
	boolean open = false;
	@UiHandler("seeMore")
	void onSeemore(ClickEvent e) {
		GWT.log(seeMore.getText());
		if (!open) {
			description.setHTML(vreDescription);
			seeMore.setText(SEE_LESS);
			open = true;
		} else {
			description.setHTML(vreDescription.substring(0, MAX_CHAR_DESC) + " ...");
			seeMore.setText(SEE_MORE);
			open = false;
		}
	}

	@UiHandler("editButton")
	void onEditButton(ClickEvent e) {
		mod.show();
	}



	/**
	 * 
	 * @return
	 */
	public static Widget getLoadingHTML() {
		return new LoadingText();
	}

}
