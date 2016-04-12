package org.gcube.portlets.admin.vredefinition.client.presenter;

import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.google.gwt.user.client.ui.Widget;



public interface Presenter {
	
	public void go(LayoutContainer container);
	public boolean doSave();
	public Widget display();
	
}
