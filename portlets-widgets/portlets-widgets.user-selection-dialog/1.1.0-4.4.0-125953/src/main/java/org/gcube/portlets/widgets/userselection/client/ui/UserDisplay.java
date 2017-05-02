package org.gcube.portlets.widgets.userselection.client.ui;



import org.gcube.portlets.widgets.userselection.client.events.SelectedUserEvent;
import org.gcube.portlets.widgets.userselection.shared.ItemSelectableBean;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class UserDisplay extends Composite {

	private static LikedTemplateUiBinder uiBinder = GWT
			.create(LikedTemplateUiBinder.class);

	interface LikedTemplateUiBinder extends UiBinder<Widget, UserDisplay> {
	}
	
	@UiField
	Image avatarImage;
	@UiField
	HTML contentArea;
	HandlerManager eventBus;
	ItemSelectableBean user;
	public UserDisplay(ItemSelectableBean user, HandlerManager eventBus) {
		initWidget(uiBinder.createAndBindUi(this));
		this.user = user;
		this.eventBus = eventBus;
		if (user.getIconURL() != null)
			avatarImage.setUrl(user.getIconURL());
		
		avatarImage.setPixelSize(30, 30);
		contentArea.setHTML("<span class=\"user-selection-link\" style=\"font-size:16px;\">"+user.getName()+"</span> ");
	}
	
	@UiHandler("contentArea") 
	void onSeeMoreClick(ClickEvent e) {
		eventBus.fireEvent(new SelectedUserEvent(user));
	}
}
