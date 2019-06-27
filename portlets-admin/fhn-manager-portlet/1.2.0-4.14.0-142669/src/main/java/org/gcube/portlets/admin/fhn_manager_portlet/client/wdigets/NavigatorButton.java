package org.gcube.portlets.admin.fhn_manager_portlet.client.wdigets;

import org.gcube.portlets.admin.fhn_manager_portlet.client.FhnManagerController;
import org.gcube.portlets.admin.fhn_manager_portlet.client.event.NavigationPanelStatusChangeEvent;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.ObjectType;

import com.github.gwtbootstrap.client.ui.NavLink;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

public class NavigatorButton extends NavLink{

	public NavigatorButton(final ObjectType type) {
		super(type.getLabel());
		this.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				FhnManagerController.eventBus.fireEvent(new NavigationPanelStatusChangeEvent(type));
				
			}
		});
	}
	
	
}
