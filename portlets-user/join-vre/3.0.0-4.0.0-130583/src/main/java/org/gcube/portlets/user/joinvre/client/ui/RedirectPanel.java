package org.gcube.portlets.user.joinvre.client.ui;

import org.gcube.portlets.user.joinvre.shared.VRE;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Modal;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class RedirectPanel extends Composite {
	private static InfoPanelUiBinder uiBinder = GWT.create(InfoPanelUiBinder.class);

	interface InfoPanelUiBinder extends UiBinder<Widget, RedirectPanel> {
	}
	
	@UiField Modal m;
	@UiField HTML description;
	@UiField Button close;
	@UiField Button link;
	private VRE vre;

	public RedirectPanel(VRE toDisplay) {
		initWidget(uiBinder.createAndBindUi(this));
		vre = toDisplay;
	}

	public void show() {
		m.setTitle("Infrastructure Gateway notice for " + vre.getName());
		description.setHTML("Dear user, <br/>" +  vre.getName() + " is not hosted in this Gateway, "+ 
			"if you wish to enter or to request access, please click to the link below. We will redirect you to the D4Science Gateway hosting it.");	
		link.setHref(vre.getUrl());
		link.setTarget("_blank");
		link.setText("Take me to " + vre.getName());
		link.setBlock(true);
		link.getElement().getStyle().setMarginTop(25, Unit.PX);
		link.getElement().getStyle().setMarginBottom(15, Unit.PX);
		m.show();
	}
	
	@UiHandler("close")
	void handleClick(ClickEvent e) {
		m.hide();
	 }
}
