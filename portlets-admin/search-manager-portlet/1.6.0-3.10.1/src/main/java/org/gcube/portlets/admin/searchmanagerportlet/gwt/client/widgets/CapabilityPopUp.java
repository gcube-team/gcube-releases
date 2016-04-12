package org.gcube.portlets.admin.searchmanagerportlet.gwt.client.widgets;

import org.gcube.portlets.user.gcubewidgets.client.popup.GCubeDialog;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;

public class CapabilityPopUp extends GCubeDialog {
	
	public CapabilityPopUp(boolean autoHide) {
		super(autoHide);
		setText("Add new Capability");
	}

	public void onClick(ClickEvent event) {
		hide();
	}
	
	public void addDock(String description)
	{
	      HTML msg = new HTML(description, true);
	     
	      DockLayoutPanel dock = new DockLayoutPanel(Unit.EM);
	      dock.addNorth(msg,2);
	      dock.setWidth("100%");
	      add(dock);
	}

}
