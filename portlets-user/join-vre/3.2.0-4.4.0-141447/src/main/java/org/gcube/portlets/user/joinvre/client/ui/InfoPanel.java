package org.gcube.portlets.user.joinvre.client.ui;

import org.gcube.portlets.user.joinvre.shared.VRE;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Modal;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class InfoPanel extends Composite {
	private static InfoPanelUiBinder uiBinder = GWT.create(InfoPanelUiBinder.class);

	interface InfoPanelUiBinder extends UiBinder<Widget, InfoPanel> {
	}
	
	@UiField Modal m;
	@UiField HTML description;
	@UiField Button close;
	private VRE vre;

	public InfoPanel(VRE toDisplay) {
		initWidget(uiBinder.createAndBindUi(this));
		vre = toDisplay;
	}

	public void show() {
		m.setTitle("Scope of " + vre.getName());
		description.setHTML(vre.getDescription());
		m.add(description);
		m.show();
	}
	
	@UiHandler("close")
	void handleClick(ClickEvent e) {
		m.hide();
	 }
}
