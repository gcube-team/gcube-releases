package org.gcube.portlets.admin.fhn_manager_portlet.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gcube.portlets.admin.fhn_manager_portlet.client.event.CreateElementEvent;
import org.gcube.portlets.admin.fhn_manager_portlet.client.event.CreateElementEventHandler;
import org.gcube.portlets.admin.fhn_manager_portlet.client.event.NavigationPanelStatusChangeEvent;
import org.gcube.portlets.admin.fhn_manager_portlet.client.event.NavigationPanelStatusChangeEventHandler;
import org.gcube.portlets.admin.fhn_manager_portlet.client.event.OpenPinnedResourceEvent;
import org.gcube.portlets.admin.fhn_manager_portlet.client.event.OpenPinnedResourceEventHandler;
import org.gcube.portlets.admin.fhn_manager_portlet.client.event.PinResourceEvent;
import org.gcube.portlets.admin.fhn_manager_portlet.client.event.PinResourceEventHandler;
import org.gcube.portlets.admin.fhn_manager_portlet.client.event.RefreshGridEvent;
import org.gcube.portlets.admin.fhn_manager_portlet.client.event.RefreshGridEvent.RefreshGridOptions;
import org.gcube.portlets.admin.fhn_manager_portlet.client.event.RefreshGridEventHandler;
import org.gcube.portlets.admin.fhn_manager_portlet.client.event.RemoveElementEvent;
import org.gcube.portlets.admin.fhn_manager_portlet.client.event.RemoveElementEventHandler;
import org.gcube.portlets.admin.fhn_manager_portlet.client.event.RemovePinnedEvent;
import org.gcube.portlets.admin.fhn_manager_portlet.client.event.RemovePinnedEventHandler;
import org.gcube.portlets.admin.fhn_manager_portlet.client.event.RetrievedDescribedResource;
import org.gcube.portlets.admin.fhn_manager_portlet.client.event.RetrievedDescribedResourceEventHandler;
import org.gcube.portlets.admin.fhn_manager_portlet.client.event.ShowCreationFormEvent;
import org.gcube.portlets.admin.fhn_manager_portlet.client.event.ShowCreationFormEventHandler;
import org.gcube.portlets.admin.fhn_manager_portlet.client.event.ShowMessageEvent;
import org.gcube.portlets.admin.fhn_manager_portlet.client.event.ShowMessageEventHandler;
import org.gcube.portlets.admin.fhn_manager_portlet.client.event.StartNodeEvent;
import org.gcube.portlets.admin.fhn_manager_portlet.client.event.StartNodeEventHandler;
import org.gcube.portlets.admin.fhn_manager_portlet.client.event.StopNodeEvent;
import org.gcube.portlets.admin.fhn_manager_portlet.client.event.StopNodeEventHandler;
import org.gcube.portlets.admin.fhn_manager_portlet.client.wdigets.AsyncLoader;
import org.gcube.portlets.admin.fhn_manager_portlet.client.wdigets.Loader;
import org.gcube.portlets.admin.fhn_manager_portlet.client.wdigets.Navigator;
import org.gcube.portlets.admin.fhn_manager_portlet.client.wdigets.PinnedResource;
import org.gcube.portlets.admin.fhn_manager_portlet.client.wdigets.PopUpDetails;
import org.gcube.portlets.admin.fhn_manager_portlet.client.wdigets.PopupMessage;
import org.gcube.portlets.admin.fhn_manager_portlet.client.wdigets.data.AdvancedGrid;
import org.gcube.portlets.admin.fhn_manager_portlet.client.wdigets.data.DataContainer;
import org.gcube.portlets.admin.fhn_manager_portlet.client.wdigets.forms.Wizard;
import org.gcube.portlets.admin.fhn_manager_portlet.client.wdigets.forms.WizardConfiguration;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.ObjectType;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.DescribedResource;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.RemoteNode;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.ServiceProfile;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.Storable;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.VMProvider;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.VMTemplate;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

public class FhnManagerController {

	private static Logger logger = Logger.getLogger(FhnManagerController.class+"");

	/** The Constant eventBus. */
	public final static HandlerManager eventBus = new HandlerManager(null);

	static{
		eventBus.addHandler(NavigationPanelStatusChangeEvent.TYPE, new NavigationPanelStatusChangeEventHandler(){
			@Override
			public void onSelectedResourceType(NavigationPanelStatusChangeEvent event) {
				changeMainPanelType(event.getType());			
			}
		});

		eventBus.addHandler(RefreshGridEvent.TYPE, new RefreshGridEventHandler() {

			@Override
			public void onRefreshGrid(RefreshGridEvent event) {
				refreshGrid(event.getOptions(),event.getTheDataContainer());

			}
		});

		eventBus.addHandler(RemoveElementEvent.TYPE, new RemoveElementEventHandler() {

			@Override
			public void onRemoveElement(RemoveElementEvent theEvent) {
				removeElement(theEvent.getType(), theEvent.getToRemoveId(),theEvent.getFlags(),theEvent.getCascade());
			}
		});

		eventBus.addHandler(CreateElementEvent.TYPE, new CreateElementEventHandler() {

			@Override
			public void onCreateElement(CreateElementEvent theEvent) {
				create(theEvent.getType(),theEvent.getFields(),theEvent.getCascade());
			}
		});

		eventBus.addHandler(ShowCreationFormEvent.TYPE, new ShowCreationFormEventHandler() {

			@Override
			public void onShowCreationForm(ShowCreationFormEvent theEvent) {
				showCreationForm(theEvent.getType());
			}
		});

		eventBus.addHandler(PinResourceEvent.TYPE, new PinResourceEventHandler() {

			@Override
			public void onPinResource(PinResourceEvent theEvent) {
				pinResource(theEvent.getSelectedResource());
			}
		});

		eventBus.addHandler(OpenPinnedResourceEvent.TYPE, new OpenPinnedResourceEventHandler() {

			@Override
			public void onOpenPinnedResource(OpenPinnedResourceEvent theEvent) {
				openPinned(theEvent.getToOpen());
			}
		});

		eventBus.addHandler(StartNodeEvent.TYPE, new StartNodeEventHandler() {

			@Override
			public void onStartNode(StartNodeEvent theEvent) {
				startNode(theEvent.getNodeId(), theEvent.getCascade());
			}
		});

		eventBus.addHandler(StopNodeEvent.TYPE, new StopNodeEventHandler() {

			@Override
			public void onStopNode(StopNodeEvent theEvent) {
				stopNode(theEvent.getToStopNodeId(),theEvent.getCascade());
			}
		});

		eventBus.addHandler(RemovePinnedEvent.TYPE, new RemovePinnedEventHandler() {

			@Override
			public void onRemovePinnedResource(Widget toRemove,Storable theResource) {
				unpinResource(toRemove, theResource);				
			}
		});

		eventBus.addHandler(ShowMessageEvent.TYPE, new ShowMessageEventHandler() {

			@Override
			public void onShowMesasge(ShowMessageEvent theEvent) {
				showMessage(theEvent.getTitle(),theEvent.getMessage());
			}
		});
		
		eventBus.addHandler(RetrievedDescribedResource.TYPE, new RetrievedDescribedResourceEventHandler() {
			
			@Override
			public void onRetrievedDescribedResource(RetrievedDescribedResource theEvent) {
				logger.fine("Handling described resource. Event is "+theEvent);
				storeDescribedResource(theEvent.getDescribedResource());
			}
		});
		
	}

	//***************** LOGIC

	
	private static void storeDescribedResource(DescribedResource toStore){
		logger.fine("Storing "+toStore);
		alreadyPinned.put(toStore.getTheObject(), toStore);
		showLoading("Pin resource", "Pinning resource to side pannel..");		
		FhnManagerEntryPoint.pinnedResourcesContainer.add(new PinnedResource(toStore.getTheObject()));
		hideLoading();
	}
	

	private static void changeMainPanelType(ObjectType type){
		showLoading("Please wait","Preparing view");
		try{
			AdvancedGrid toShow=AdvancedGrid.getCentralGrid(type);
			toShow.getTheWidget().setWidth("100%");
			toShow.getTheWidget().setHeight("100%");
			FhnManagerEntryPoint.centralContainer.setWidget(toShow.getTheWidget());
			Navigator.setActive(type);
			hideLoading();
			toShow.fireRefreshData();
		}catch(Throwable t){
			logger.log(Level.SEVERE,"Failed to change main section ",t);
		}
		
	}

	private static void refreshGrid(final RefreshGridOptions options,final DataContainer theDataContainer){
		logger.log(Level.FINE,"Refresh Grid Event, type : "+options.getType());
		showLoading("Refresh Grid", "Gathering information..");
		FhnManagerEntryPoint.managerService.listResources(options.getType(), options.getFilters(), new AsyncCallback<Set<Storable>>() {

			@Override
			public void onFailure(Throwable caught) {
				logger.log(Level.SEVERE, "Failed refresh grid", caught);
				hideLoading();
				showMessage("Unexpected error", "Please try again. If problem persists contact administration.");
			}

			@Override
			public void onSuccess(Set<Storable> result) {
				logger.log(Level.FINE,"Received data "+result.size());
				theDataContainer.setData(castStorableList(options.getType(), result));
				logger.log(Level.FINE,"Data set to grid");
				hideLoading();
			}

		});

	}

	private static void removeElement(ObjectType type,String id, Map<String,Boolean> flags,GwtEvent cascadeEvent){		
		FhnManagerEntryPoint.managerService.removeObject(type, id, flags, new AsyncLoader("Remove element", "Sending request", cascadeEvent));
	}

	private static void create(ObjectType type,Map<String,String> fields, GwtEvent cascadeEvent){	
		logger.fine("Create "+type+" parameters are :");
			for(Entry<String,String> param: fields.entrySet())
				logger.fine(param.getKey()+" : "+param.getValue());
		FhnManagerEntryPoint.managerService.createObject(type, fields, new AsyncLoader("Create element", "Sending request", cascadeEvent));		
	}

	private static void showCreationForm(ObjectType type){
		if(type.equals(ObjectType.REMOTE_NODE))
			new Wizard(WizardConfiguration.CREATE_REMOTE_NODE);
		else	showMessage("Show creation form for "+type.getLabel(),"To implement");
	}

	private static void pinResource(Storable toPin){
		try{
			logger.log(Level.FINE,"Pinning "+toPin);
			if(!alreadyPinned.containsKey(toPin)){				
				FhnManagerEntryPoint.managerService.getDetails(toPin, new AsyncLoader("Load "+toPin.getName()+" information.", "Sending request", new RetrievedDescribedResource(null)));
			}else{
				showMessage("Pin resource", toPin.getName()+" [ID : "+toPin.getKey()+"] already pinned on right panel.");
			}
		}catch(Throwable t){
			logger.log(Level.SEVERE,"Error : ",t);
		}
	}

	private static void unpinResource(Widget toUnpin,Storable theResource){
		showLoading("Unpin resource", "Removing from side pannel..");
		toUnpin.removeFromParent();		
		alreadyPinned.remove(theResource);
		hideLoading();
	}


	private static void openPinned(Storable toOpen){
		new PopUpDetails(alreadyPinned.get(toOpen));
	}

	private static void startNode(String nodeId,GwtEvent cascadeEvent){		
		FhnManagerEntryPoint.managerService.startNode(nodeId, new AsyncLoader("Start Node", "Sending request", cascadeEvent));
	}

	private static void stopNode(String nodeId,GwtEvent cascadeEvent){		
		FhnManagerEntryPoint.managerService.stopNode(nodeId, new AsyncLoader("Stop Node", "Sending request", cascadeEvent));
	}

	private static List<? extends Storable> castStorableList(ObjectType type,Collection<Storable> theCollection){		
		switch(type){
		case REMOTE_NODE : {ArrayList<RemoteNode> toReturn=new ArrayList<RemoteNode>();
		for(Storable s:theCollection)toReturn.add((RemoteNode) s);
		return toReturn;}
		case SERVICE_PROFILE: {ArrayList<ServiceProfile> toReturn=new ArrayList<ServiceProfile>();
		for(Storable s:theCollection)toReturn.add((ServiceProfile) s);
		return toReturn;}
		case VM_PROVIDER : {ArrayList<VMProvider> toReturn=new ArrayList<VMProvider>();
		for(Storable s:theCollection)toReturn.add((VMProvider) s);
		return toReturn;}
		case VM_TEMPLATES : {ArrayList<VMTemplate> toReturn=new ArrayList<VMTemplate>();
		for(Storable s:theCollection)toReturn.add((VMTemplate) s);
		return toReturn;}
		}
		return null;
	}


	private static final void showMessage(String title,String message){
		PopupMessage msg=new PopupMessage(title,message);
		msg.show();
	}


	//********************************* Pinned resources

	private static final HashMap<Storable,DescribedResource> alreadyPinned=new HashMap<Storable,DescribedResource>();

	// ******************************** LOADER 

	private static Loader theLoader=null;
	private static void showLoading(String title,String msg){
		theLoader=new Loader(title,msg);		
		theLoader.show();
	}	

	private static void hideLoading(){
		theLoader.hide();
		theLoader=null;
	}
}
