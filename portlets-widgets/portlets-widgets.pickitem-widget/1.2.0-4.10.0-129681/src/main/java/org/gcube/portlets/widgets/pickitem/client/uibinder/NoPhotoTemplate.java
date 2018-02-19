package org.gcube.portlets.widgets.pickitem.client.uibinder;

import org.gcube.portlets.widgets.pickitem.client.dialog.PickItemsDialog;
import org.gcube.portlets.widgets.pickitem.shared.ItemBean;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class NoPhotoTemplate extends Composite implements SelectableItem {

	private static NoPhotoTemplateUiBinder uiBinder = GWT
			.create(NoPhotoTemplateUiBinder.class);

	interface NoPhotoTemplateUiBinder extends UiBinder<Widget, NoPhotoTemplate> {
	}

	PickItemsDialog owner;
	private int currDisplayIndex;
	
	@UiField
	FocusPanel focusDiv;
	@UiField
	HTML contentArea;
	
	public NoPhotoTemplate(PickItemsDialog owner, ItemBean user, int displayIndex) {
		initWidget(uiBinder.createAndBindUi(this));
		this.owner = owner;
		currDisplayIndex = displayIndex;
		contentArea.setHTML(user.getAlternativeName());
		
		
	}
	
	@UiHandler("focusDiv")
	void onMouseOver(MouseOverEvent e) {
		owner.select(currDisplayIndex);
	}

	@Override
	public String getItemName() {
		return contentArea.getText();
	}
	

}
