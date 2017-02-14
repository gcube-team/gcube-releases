package org.gcube.portlets.admin.policydefinition.vaadin.listeners;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window;

public class CloseButtonListener implements Button.ClickListener {

	private static final long serialVersionUID = -1108919293654366874L;

	@Override
	public void buttonClick(ClickEvent event) {
		((Window) event.getComponent().getWindow().getParent()).removeWindow(event.getComponent().getWindow());
	}

}
