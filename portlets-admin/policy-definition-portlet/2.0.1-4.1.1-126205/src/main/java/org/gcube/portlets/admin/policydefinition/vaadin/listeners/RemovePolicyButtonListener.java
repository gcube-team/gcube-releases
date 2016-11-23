package org.gcube.portlets.admin.policydefinition.vaadin.listeners;

import java.util.Collection;

import org.gcube.portlets.admin.policydefinition.vaadin.components.PolicyTable;
import org.gcube.portlets.admin.policydefinition.vaadin.components.RemovePolicyConfirmComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

public class RemovePolicyButtonListener implements Button.ClickListener {

	private static Logger logger = LoggerFactory.getLogger(RemovePolicyButtonListener.class);
	private static final long serialVersionUID = -1108919293654366874L;
	
	private PolicyTable policies;

	public RemovePolicyButtonListener(PolicyTable policies) {
		super();
		this.policies = policies;
	}

	@Override
	public void buttonClick(ClickEvent event) {
		if(policies.getValue() == null || ((Collection<?>)policies.getValue()).size() == 0) return;
		if(policies.getValue() == null) return;
		Window mainWindow = event.getComponent().getApplication().getMainWindow();
		try {			
			RemovePolicyConfirmComponent removeComponent = new RemovePolicyConfirmComponent(policies);
			Window popup = new Window("Confirm changes", removeComponent); 
			popup.setWidth((removeComponent.getWidth()+10)+"px");
			popup.setHeight((removeComponent.getHeight()+20)+"px");
			popup.setModal(true);
			popup.setCloseShortcut(KeyCode.ESCAPE, null);
			((Window) event.getComponent().getApplication().getMainWindow()).addWindow(popup);
		} catch (Exception e) {
			logger.error("Error removing policies", e);
			mainWindow.showNotification("Internal server error removing policy", Notification.TYPE_ERROR_MESSAGE);	
		}
	}

}
