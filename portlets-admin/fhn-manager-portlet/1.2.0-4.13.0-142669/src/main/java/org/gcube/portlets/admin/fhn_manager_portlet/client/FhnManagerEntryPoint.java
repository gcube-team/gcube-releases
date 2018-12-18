package org.gcube.portlets.admin.fhn_manager_portlet.client;

import java.util.logging.Logger;

import org.gcube.portlets.admin.fhn_manager_portlet.client.event.NavigationPanelStatusChangeEvent;
import org.gcube.portlets.admin.fhn_manager_portlet.client.wdigets.Navigator;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.Constants;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.ObjectType;

import com.github.gwtbootstrap.client.ui.Divider;
import com.github.gwtbootstrap.client.ui.NavHeader;
import com.github.gwtbootstrap.client.ui.WellNavList;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class FhnManagerEntryPoint implements EntryPoint {

	private static Logger logger = Logger.getLogger(FhnManagerEntryPoint.class+"");
	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	public static  final FhnManagerServiceAsync managerService = GWT.create(FhnManagerService.class);

	private static final DockLayoutPanel dock=new DockLayoutPanel(Unit.PX);

	public static final SimpleLayoutPanel centralContainer=new SimpleLayoutPanel();
	
	public static WellNavList pinnedResourcesContainer;
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		
		RootPanel.get(Constants.APPLICATION_DIV_IDENTIFIER).add(getMain());
		FhnManagerController.eventBus.fireEvent(new NavigationPanelStatusChangeEvent(ObjectType.REMOTE_NODE));
//		
//		
//		ClientScopeHelper.getService().setScope(Location.getHref(), new AsyncCallback<Boolean>() {
//			@Override
//			public void onSuccess(Boolean result) {
//				
//			}				
//			@Override
//			public void onFailure(Throwable caught) {	
//			}
//		});

		
		
		
//		Button button=new Button("Click me");
//		button.addClickHandler(new ClickHandler() {
//			
//			@Override
//			public void onClick(ClickEvent event) {
//				FhnManagerController.eventBus.fireEvent(new ShowCreationFormEvent(ObjectType.REMOTE_NODE));
//			}
//		});
//		
//		
//		
//		RootPanel.get(Constants.APPLICATION_DIV_IDENTIFIER).add(button);
	}


	private Widget getMain(){
		dock.setHeight("800px");
		
		dock.addWest(getNavigator(),150);
		dock.addEast(getPinnedResources(),150);
		dock.add(centralContainer);
		dock.ensureDebugId("cwDockPanel");
		return dock;
	}

	
	private Widget getNavigator(){
		
		return new Navigator();
	}
		
	
	private Widget getPinnedResources(){
		
		pinnedResourcesContainer=new WellNavList();
		pinnedResourcesContainer.add(new NavHeader("Pinned Resources"));
		pinnedResourcesContainer.add(new Divider());
		return pinnedResourcesContainer;
	}
}
