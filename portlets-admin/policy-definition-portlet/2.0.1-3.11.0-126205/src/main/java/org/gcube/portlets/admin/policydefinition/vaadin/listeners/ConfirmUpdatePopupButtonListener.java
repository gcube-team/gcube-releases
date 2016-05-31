package org.gcube.portlets.admin.policydefinition.vaadin.listeners;

import org.gcube.portlets.admin.policydefinition.vaadin.components.UpdatePolicyConfirmComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

public class ConfirmUpdatePopupButtonListener implements Button.ClickListener {

	private static Logger logger = LoggerFactory.getLogger(ConfirmUpdatePopupButtonListener.class);
	private static final long serialVersionUID = -1108919293654366874L;
	
	private String service;
	private Table policyTable;
	private TextField startTimeTextField;
	private TextField endTimeTextField;
	private PopupDateField startPopupDateField;
	private PopupDateField endPopupDateField;
	private CheckBox permitCheckBox;
	private CheckBox overridePermit;
	private CheckBox overrideDate;
	private CheckBox overrideTime;
	private Button cancel;

	public ConfirmUpdatePopupButtonListener(String service, Table policyTable, TextField startTimeTextField,
			TextField endTimeTextField, PopupDateField startPopupDateField,
			PopupDateField endPopupDateField, CheckBox permitCheckBox, 
			CheckBox overridePermit, CheckBox overrideDate, CheckBox overrideTime, Button cancel) {
		super();
		this.service = service;
		this.policyTable = policyTable;
		this.startTimeTextField = startTimeTextField;
		this.endTimeTextField = endTimeTextField;
		this.startPopupDateField = startPopupDateField;
		this.endPopupDateField = endPopupDateField;
		this.permitCheckBox = permitCheckBox;
		this.overrideDate = overrideDate;
		this.overridePermit = overridePermit;
		this.overrideTime = overrideTime;
		this.cancel = cancel;
	}

	@Override
	public void buttonClick(ClickEvent event) {
		Window mainWindow = event.getComponent().getApplication().getMainWindow();
		try {			
			UpdatePolicyConfirmComponent removeComponent = new UpdatePolicyConfirmComponent(service, policyTable, startTimeTextField,
					endTimeTextField, startPopupDateField,
					endPopupDateField, permitCheckBox, 
					overridePermit, overrideDate, overrideTime,
					cancel);
			Window popup = new Window("Confirm changes", removeComponent); 
			popup.setWidth((removeComponent.getWidth()+10)+"px");
			popup.setHeight((removeComponent.getHeight()+20)+"px");
			popup.setModal(true);
			popup.setCloseShortcut(KeyCode.ESCAPE, null);
			((Window) event.getComponent().getApplication().getMainWindow()).addWindow(popup);
		} catch (Exception e) {
			logger.error("Error updating policies", e);
			mainWindow.showNotification("Internal server error updating policy", Notification.TYPE_ERROR_MESSAGE);	
		}
	}

}
