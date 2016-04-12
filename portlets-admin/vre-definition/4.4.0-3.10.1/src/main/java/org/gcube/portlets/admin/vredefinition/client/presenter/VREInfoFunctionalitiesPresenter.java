package org.gcube.portlets.admin.vredefinition.client.presenter;

import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.google.gwt.user.client.ui.Widget;

public class VREInfoFunctionalitiesPresenter {

	public interface Display {
		Widget asWidget();
	}
	
	private final Display display;
	
	public VREInfoFunctionalitiesPresenter(Display display){
		this.display = display;
	}
	
	public void go(LayoutContainer container){
		container.removeAll();
		container.add(display.asWidget());
		container.layout();
	}
}
