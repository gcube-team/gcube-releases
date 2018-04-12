package org.gcube.portlets.user.gcubewidgets.client;

import org.gcube.portlets.user.gcubewidgets.client.popup.GCubeDialog;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class WidgetFactory implements EntryPoint {

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		//showSample2();
	}
	
	

//	/**
//	 * 
//	 * @return .
//	 */
//	public Widget getStackPanel() {
//		// Get the images
//		Image images = (Image) GWT.create(Image.class);
//
//		// Create a new stack panel
//		StackPanel stackPanel = new StackPanel();
//		stackPanel.setWidth("200px");
//		
//		
//		stackPanel.add();
//
//		// Add a list of filters
//		String filtersHeader = getHeaderString(
//				constants.cwStackPanelFiltersHeader(), images.filtersgroup());
//		stackPanel.add(createFiltersItem(), filtersHeader, true);
//
//		// Add a list of contacts
//		String contactsHeader = getHeaderString(
//				constants.cwStackPanelContactsHeader(), images.contactsgroup());
//		stackPanel.add(createContactsItem(images), contactsHeader, true);
//
//		// Return the stack panel
//		stackPanel.ensureDebugId("cwStackPanel");
//		return stackPanel;
//	}
}
