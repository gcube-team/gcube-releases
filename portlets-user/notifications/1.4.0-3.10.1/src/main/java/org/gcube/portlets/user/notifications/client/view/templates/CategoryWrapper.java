package org.gcube.portlets.user.notifications.client.view.templates;

import static com.google.gwt.query.client.GQuery.$;

import java.util.ArrayList;
import java.util.HashMap;

import org.gcube.portal.databook.shared.NotificationChannelType;
import org.gcube.portal.databook.shared.NotificationType;
import org.gcube.portlets.user.gcubewidgets.client.elements.Span;
import org.gcube.portlets.user.notifications.shared.NotificationPreference;
import org.gcube.portlets.widgets.switchbutton.client.SwitchButton;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.github.gwtbootstrap.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Massimiliano Assante ISTI-CNR
 *
 */
public class CategoryWrapper extends Composite {

	private static CategoryWrapperUiBinder uiBinder = GWT
			.create(CategoryWrapperUiBinder.class);

	interface CategoryWrapperUiBinder extends UiBinder<Widget, CategoryWrapper> {
	}

	@UiField Span categoryLabel;
	@UiField SwitchButton switchButton;
	@UiField CheckBox portalCheckbox;
	@UiField CheckBox emailCheckbox;
	@UiField VerticalPanel categoryPanel;

	ArrayList<NotificationPreferenceView> myPreferences = new ArrayList<NotificationPreferenceView>();

	public CategoryWrapper(String catName, 	ArrayList<NotificationPreference> preferences) {
		initWidget(uiBinder.createAndBindUi(this));
		categoryLabel.setText(catName);
	
		//set the view depending on the model
		boolean atLeastOnePreferenceOn = false;
		for (NotificationPreference pref : preferences) {
			NotificationPreferenceView toAdd = new NotificationPreferenceView(pref);
			categoryPanel.add(toAdd);
			myPreferences.add(toAdd);
			if (pref.getSelectedChannels()[0] != null && !atLeastOnePreferenceOn) { //if none were selected you get an array of size 1 having null
				atLeastOnePreferenceOn = true;				
			}
		}
		if (!atLeastOnePreferenceOn)
			setOff();
		else
			switchButton.setValue(true);
		
		switchButton.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				if (event.getValue()) {
					for (int i = categoryPanel.getWidgetCount()-1; i >= 0; i--) {
						$(categoryPanel.getWidget(i)).slideDown(300);
						$(portalCheckbox).fadeIn(300);
						$(emailCheckbox).fadeIn(300);
					}
				} else {
					for (int i = categoryPanel.getWidgetCount()-1; i >= 0; i--) {
						$(categoryPanel.getWidget(i)).slideUp(300);
						$(portalCheckbox).fadeOut(300);
						$(emailCheckbox).fadeOut(300);
					}
				}
			}
		});

		emailCheckbox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				for (NotificationPreferenceView notPref : myPreferences) {
					notPref.setEmailPrefValue(event.getValue());
				}				
			}
		});

		portalCheckbox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				for (NotificationPreferenceView notPref : myPreferences) {
					notPref.setPortalPrefValue(event.getValue());
				}				
			}
		});
	}
	public void setOff() {
		for (int i = categoryPanel.getWidgetCount()-1; i >= 0; i--) {
			$(categoryPanel.getWidget(i)).slideUp(0);
			$(portalCheckbox).fadeOut(50);
			$(emailCheckbox).fadeOut(50);
		}
		switchButton.setValue(false);
	}
	
	/**
	 * @return the selected notification channels in the view
	 */
	public HashMap<NotificationType, NotificationChannelType[]> getSelectedChannels() {
		HashMap<NotificationType, NotificationChannelType[]> toReturn = new HashMap<NotificationType, NotificationChannelType[]>();
		if (switchButton.getValue()) {			
			for (NotificationPreferenceView notPref : myPreferences) 
				if (notPref.getSelectedChannels() != null && notPref.getSelectedChannels().length > 0) 
					toReturn.put(notPref.getNotificationType(), notPref.getSelectedChannels());		
				else
					toReturn.put(notPref.getNotificationType(), new NotificationChannelType[0]);	//none were selected	
		} 
		else { //all OFF
			for (NotificationPreferenceView notPref : myPreferences) 
				toReturn.put(notPref.getNotificationType(), new NotificationChannelType[0]);
		}
		return toReturn;
	}
}
