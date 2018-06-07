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
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class WithPhotoTemplate extends Composite implements SelectableItem {

	private static PhotoTemplateUiBinder uiBinder = GWT
			.create(PhotoTemplateUiBinder.class);

	interface PhotoTemplateUiBinder extends UiBinder<Widget, WithPhotoTemplate> {
	}
	PickItemsDialog owner;
	private int currDisplayIndex;
	
	@UiField
	FocusPanel focusDiv;
	@UiField
	Image avatarImage;
	@UiField
	HTML contentArea;
	
	private String id;
	private boolean isGroup;
	
	public WithPhotoTemplate(PickItemsDialog owner, ItemBean user, int displayIndex) {
		initWidget(uiBinder.createAndBindUi(this));
		this.owner = owner;
		currDisplayIndex = displayIndex;
		if (user.getThumbnailURL() != null)
			avatarImage.setUrl(user.getThumbnailURL());
		
		avatarImage.setPixelSize(30, 30);
		contentArea.setHTML(user.getAlternativeName());
		this.id = user.getId();
		this.isGroup = user.isItemGroup();
		
	}
	
	@UiHandler("focusDiv")
	void onMouseOver(MouseOverEvent e) {
		owner.select(currDisplayIndex);
	}

	@Override
	public String getItemName() {
		return contentArea.getText();
	}
	@Override
	public String getItemId() {
		return this.id;
	}
	@Override
	public boolean isGroup() {
		return this.isGroup;
	}
}
