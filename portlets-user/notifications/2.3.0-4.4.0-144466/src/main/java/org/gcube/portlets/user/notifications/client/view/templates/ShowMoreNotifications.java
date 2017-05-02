package org.gcube.portlets.user.notifications.client.view.templates;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class ShowMoreNotifications extends Composite {

	private static ShowMoreNotificationsUiBinder uiBinder = GWT
			.create(ShowMoreNotificationsUiBinder.class);

	interface ShowMoreNotificationsUiBinder extends	UiBinder<Widget, ShowMoreNotifications> {
	}
	
	
	@UiField HTML caption;
	@UiField HTMLPanel panel;


	public ShowMoreNotifications() {
		initWidget(uiBinder.createAndBindUi(this));
		panel.getElement().getStyle().setMarginTop(10, Unit.PX);
		caption.addStyleName("new-notifications-show");		
		caption.getElement().getStyle().setBackgroundColor("transparent");
		caption.getElement().getStyle().setFontSize(14, Unit.PX);
		caption.setHTML("Show more notifications");
		//hiding as not needed anymore (the user click)
		panel.getElement().getStyle().setVisibility(Visibility.HIDDEN);
	}	
	
	@UiHandler("caption")
	void onClick(ClickEvent e) {		
	}
}
