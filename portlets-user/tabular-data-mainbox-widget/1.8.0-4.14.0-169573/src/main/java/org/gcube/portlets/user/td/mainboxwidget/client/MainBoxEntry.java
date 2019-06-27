package org.gcube.portlets.user.td.mainboxwidget.client;

import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.tr.TabResource;
import org.gcube.portlets.user.td.gwtservice.shared.user.UserInfo;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.UIStateEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.UIStateType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.dataview.DataViewType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.TabResourceType;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;
import com.sencha.gxt.widget.core.client.container.Viewport;

/**
 * 
 * @author Giancarlo Panichi
 * 
 *
 */
public class MainBoxEntry implements EntryPoint {
	protected static final String JSP_TAG_ID = "tdp";
	private TRId trId;
	private TabResource tabResource;
	
	public void onModuleLoad() {
		// For example Tabular Resource 7 and table 402
		//trId = new TRId("58", TabResourceType.STANDARD, "1283");
		//trId = new TRId("77", TabResourceType.STANDARD, "1560");
		trId = new TRId("80", TabResourceType.STANDARD, "1757");
		
	
		
	    TDGWTServiceAsync.INSTANCE.hello(new AsyncCallback<UserInfo>() {

			@Override
			public void onFailure(Throwable caught) {
				Log.error("Error in hello(): "+caught);
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(UserInfo result) {
				Log.debug("Hello "+result);
				retrieveTabularResource();
			}
		});
		

	}
	
	public void retrieveTabularResource(){
		
		TDGWTServiceAsync.INSTANCE.getTabResourceInformation(trId, new AsyncCallback<TabResource>() {

			

			@Override
			public void onFailure(Throwable caught) {
				Log.error("Error in retrieveTabularResource(): "+caught);
				caught.printStackTrace();
				
			}

			@Override
			public void onSuccess(TabResource result) {
				Log.debug("TabResource Retrieved: "+result);
				tabResource=result;
				addInSession();
				
			}
		});
	}
	
	public void addInSession(){
		TDGWTServiceAsync.INSTANCE.setTabResource(tabResource,new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				Log.error("Error in  addInSession(): "+caught);
				caught.printStackTrace();
				
			}

			@Override
			public void onSuccess(Void result) {
				Log.debug("TabResource Set");
				loadDataView();
				
			}
		});

		
	}

	protected void loadDataView() {
		
		EventBus eventBus = new SimpleEventBus();

		try {
			MainBoxPanel mainBoxPanel = new MainBoxPanel("MainBoxPanel",
					eventBus);
			
			startInDevMode(mainBoxPanel);
			

			UIStateEvent uiStateEvent1 = new UIStateEvent(UIStateType.TR_OPEN, trId, DataViewType.GRID);
			eventBus.fireEvent(uiStateEvent1);


			Log.info("MainBoxPanel Added:" + mainBoxPanel);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	protected void startInDevMode(MainBoxPanel mainWidget) {
		try {
			RootPanel root = RootPanel.get(JSP_TAG_ID);
			Log.info("Root Panel: " + root);
			if (root == null) {
				Log.info("Div with id " + JSP_TAG_ID
						+ " not found, starting in dev mode");
				Viewport viewport = new Viewport();
				viewport.setWidget(mainWidget);
				viewport.onResize();
				RootPanel.get().add(viewport);
			} else {
				Log.info("Application div with id " + JSP_TAG_ID
						+ " found, starting in portal mode");
				/*PortalViewport viewport = new PortalViewport();
				Log.info("Created Viewport");
				viewport.setEnableScroll(false);
				viewport.setWidget(mainWidget);
				Log.info("Set Widget");
				Log.info("getOffsetWidth(): " + viewport.getOffsetWidth());
				Log.info("getOffsetHeight(): " + viewport.getOffsetHeight());
				viewport.onResize();
				root.add(viewport);
				Log.info("Added viewport to root");*/
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.error("Error in attach viewport:" + e.getLocalizedMessage());
		}
	}
}
