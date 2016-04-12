package org.gcube.portlets.admin.vredefinition.client.presenter;


import com.extjs.gxt.ui.client.widget.LayoutContainer;

import com.google.gwt.user.client.ui.Widget;

public class VREStartWizardPresenter implements Presenter {
	public interface Display {
		Widget asWidget();
	}
	
	private final Display display;
	
	public VREStartWizardPresenter(Display display){
		this.display = display;
	}
	
	public void go(LayoutContainer container){
		container.removeAll();
		container.add(display.asWidget());
		container.layout();
	}


	public boolean doSave() {
		// TODO Auto-generated method stub
		return true;
	}


	public Widget display() {
		// TODO Auto-generated method stub
		return display.asWidget();
	}
}
