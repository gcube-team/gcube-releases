package org.gcube.portlets.user.notifications.client.view.templates;

import static com.google.gwt.query.client.GQuery.$;

import java.util.ArrayList;

import org.gcube.portal.databook.shared.NotificationChannelType;
import org.gcube.portal.databook.shared.NotificationType;
import org.gcube.portlets.user.notifications.shared.NotificationPreference;
import org.gcube.portlets.widgets.switchbutton.client.SwitchButton;

import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.Paragraph;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class NotificationPreferenceView extends Composite {

	private static NotificationPreferenceUiBinder uiBinder = GWT
			.create(NotificationPreferenceUiBinder.class);

	interface NotificationPreferenceUiBinder extends
	UiBinder<Widget, NotificationPreferenceView> {
	}

	@UiField Paragraph  prefType;
	@UiField Paragraph prefDesc;

	@UiField CheckBox portalCheckbox;
	@UiField CheckBox emailCheckbox;
	@UiField SwitchButton switchButton;
	
	NotificationPreference myPreference;

	public NotificationPreferenceView(NotificationPreference toDisplay) {
		initWidget(uiBinder.createAndBindUi(this));
		myPreference = toDisplay;
		updateViewValues(toDisplay);
		prefType.setText(toDisplay.getTypeLabel());
		prefDesc.setText(toDisplay.getTypeDesc());	

		switchButton.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				setCheckBoxValue(portalCheckbox, event.getValue());
				setCheckBoxValue(emailCheckbox, event.getValue());
				if (event.getValue()) {
					$(portalCheckbox).fadeIn(300);
					$(emailCheckbox).fadeIn(300);
				} else {
					$(portalCheckbox).fadeOut(300);
					$(emailCheckbox).fadeOut(300);
				}
			}
		});
	}
	/**
	 * called initially, report the preferences from the server in the view
	 * @param setting the preference
	 */
	private void updateViewValues(NotificationPreference setting) {
		NotificationChannelType[] currChannels = setting.getSelectedChannels();
		if (currChannels == null || currChannels.length == 0) {
			setCheckBoxValue(portalCheckbox, false);
			setCheckBoxValue(portalCheckbox, false);
		}
		else {
			for (int i = 0; i < currChannels.length; i++) {
				//GWT.log(currChannels[i].toString() + "-"+setting.getType());
				if (currChannels[i] == NotificationChannelType.PORTAL) {
					setCheckBoxValue(portalCheckbox, true);
				}
				if (currChannels[i] == NotificationChannelType.EMAIL) {
					setCheckBoxValue(emailCheckbox, true);
				}
			}
		}
		//if either one is true switch is ON
		boolean overAll = portalCheckbox.getValue() || emailCheckbox.getValue();
		switchButton.setValue(overAll);
		if (! overAll) {
			$(portalCheckbox).fadeOut(300);
			$(emailCheckbox).fadeOut(300);
		}
	}
	/**
	 * this was meant also to color the checkbox label depending on true or false, had no time to complete
	 * @param toSet the CheckBox instance to check
	 * @param value just set the value
	 */
	private void setCheckBoxValue(CheckBox toSet, boolean value) {
		toSet.setValue(value);
		//TODO next time, no time now
		//toSet.getElement().getElementsByTagName("label").getItem(0).setClassName(value ? "labelOn" : "labelOff");				
	}

	public NotificationType getNotificationType() {
		return myPreference.getType();
	}
	/**
	 * @return the selected notification channels in the view, null if none were selected
	 */
	public NotificationChannelType[] getSelectedChannels() {
		if (switchButton.getValue()) {
			ArrayList<NotificationChannelType> toReturn = new ArrayList<NotificationChannelType>();
			if (portalCheckbox.getValue())
				toReturn.add(NotificationChannelType.PORTAL);
			if (emailCheckbox.getValue())
				toReturn.add(NotificationChannelType.EMAIL);
			return toReturn.toArray(new NotificationChannelType[toReturn.size()]);
		} 
		else
			return new NotificationChannelType[0];
	}
	
	public void setPortalPrefValue(boolean value) {
		setCheckBoxValue(portalCheckbox, value);
	}
	public void setEmailPrefValue(boolean value) {
		setCheckBoxValue(emailCheckbox, value);
	}
}
