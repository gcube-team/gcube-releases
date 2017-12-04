package org.gcube.portlets.user.notifications.client.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.gcube.portal.databook.shared.NotificationChannelType;
import org.gcube.portal.databook.shared.NotificationType;
import org.gcube.portlets.user.notifications.client.NotificationsServiceAsync;
import org.gcube.portlets.user.notifications.client.view.templates.CategoryWrapper;
import org.gcube.portlets.user.notifications.shared.NotificationPreference;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.ModalFooter;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;


/**
 * @author Massimiliano Assante ISTI-CNR
 *
 */
public class NotificationSettingsDialog extends Modal { 

	public static final String loading = GWT.getModuleBaseURL() + "../images/feeds-loader.gif";
	public static final String mailSentOK = GWT.getModuleBaseURL() + "../images/yes.png";
	public static final String mailSentNOK = GWT.getModuleBaseURL() + "../images/warning_blue.png";

	ArrayList<CategoryWrapper> myCategories = new ArrayList<CategoryWrapper>();
	private VerticalPanel placeholder = new VerticalPanel();
	private ModalFooter footer = new ModalFooter();
	private Button cancel = new Button("Cancel");
	private Button save = new Button("Save");

	public NotificationSettingsDialog(LinkedHashMap<String, ArrayList<NotificationPreference>> preferences, final NotificationsServiceAsync notificationService) {
		super();
		setAnimation(true);

		setTitle("Notification Settings");
		placeholder.setHorizontalAlignment(HasAlignment.ALIGN_CENTER);
		add(placeholder);
		
		for (String category : preferences.keySet()) {
			CategoryWrapper cat = new CategoryWrapper(category, preferences.get(category));
			add(cat);
			myCategories.add(cat);
		}	

		save.setType(ButtonType.PRIMARY);
		footer.add(save);
		footer.add(cancel);	
		add(footer);

		save.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				HashMap<NotificationType, NotificationChannelType[]> toStore = new HashMap<NotificationType, NotificationChannelType[]>();
				for (CategoryWrapper cat : myCategories) {
					for (NotificationType notType : cat.getSelectedChannels().keySet()) {
						toStore.put(notType, cat.getSelectedChannels().get(notType));
						//GWT.log(""+notType + " - " +  cat.getSelectedChannels().get(notType));
					}
				}
				notificationService.setUserNotificationPreferences(toStore, new AsyncCallback<Boolean>() {					
					@Override
					public void onSuccess(Boolean result) {
						showDeliveryResult(result);						
					}

					@Override
					public void onFailure(Throwable caught) {
						showDeliveryResult(false);								
					}
				});
			}
		});

		cancel.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				hide();				
			}
		});

	}

	private void showDeliveryResult(boolean success) {
		for (CategoryWrapper cat : myCategories) {
			cat.removeFromParent();
		}
		if (success) {
			placeholder.add(new HTML("<span style=\"font-size: 20px;\">Notifications Settings saved correctly</span>"));
			placeholder.add(new Image(mailSentOK));
		}
		else {
			placeholder.add(new Image(mailSentNOK));
			placeholder.add(new HTML("<span style=\"font-size: 20px;\">Sorry, there were problems contacting the server, please try again in a short while.</span>"));
		}

		Button close = new Button("Close");
		close.addClickHandler(new ClickHandler() {		
			@Override
			public void onClick(ClickEvent event) {
				hide();				
			}
		});
		footer.clear();
		footer.add(close);	
		add(footer);

	}

}
