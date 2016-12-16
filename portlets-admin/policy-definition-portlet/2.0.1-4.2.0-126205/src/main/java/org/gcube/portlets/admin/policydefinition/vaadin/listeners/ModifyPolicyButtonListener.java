package org.gcube.portlets.admin.policydefinition.vaadin.listeners;

import java.util.Collection;

import org.gcube.portlets.admin.policydefinition.common.util.PresentationHelper;
import org.gcube.portlets.admin.policydefinition.vaadin.components.ModifyPolicyComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

public class ModifyPolicyButtonListener implements Button.ClickListener {
	
	private static Logger logger = LoggerFactory.getLogger(ModifyPolicyButtonListener.class);
	private static final long serialVersionUID = -1108919293654366874L;
	private Table policiesTable;
	private String service;
	
	public ModifyPolicyButtonListener(Table policiesTable, String service) {
		super();
		this.policiesTable = policiesTable;
		this.service = service;
	}

	@Override
	public void buttonClick(ClickEvent event) {
		if(policiesTable.getValue() == null || ((Collection<?>)policiesTable.getValue()).size() == 0) return;
		Window mainWindow = event.getComponent().getApplication().getMainWindow();
		try {		
			ModifyPolicyComponent modifyComponent = new ModifyPolicyComponent(policiesTable, service);
			Window modifyPolicyPopup = new Window("Editing "+PresentationHelper.getPolicyNoun(policiesTable)+" for "+PresentationHelper.viewName(service), modifyComponent); 
			modifyPolicyPopup.setWidth((modifyComponent.getWidth()+15)+"px");
			modifyPolicyPopup.setHeight((modifyComponent.getHeight()+60)+"px");
			modifyPolicyPopup.setModal(true);
			modifyPolicyPopup.setCloseShortcut(KeyCode.ESCAPE, null);
			mainWindow.addWindow(modifyPolicyPopup);
		} catch (Exception e) {
			logger.error("Error modifying policies", e);
			mainWindow.showNotification("Internal server error modifying policy", Notification.TYPE_ERROR_MESSAGE);	
		}
		mainWindow.removeWindow(event.getComponent().getWindow());
	}

}
