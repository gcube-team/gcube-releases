package org.gcube.portlets.admin.fhn_manager_portlet.client.wdigets.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.portlets.admin.fhn_manager_portlet.client.FhnManagerController;
import org.gcube.portlets.admin.fhn_manager_portlet.client.event.RefreshGridEvent;
import org.gcube.portlets.admin.fhn_manager_portlet.client.wdigets.ButtonBar;
import org.gcube.portlets.admin.fhn_manager_portlet.client.wdigets.ButtonBar.ButtonBarElement;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.ObjectType;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.RemoteNode;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.ServiceProfile;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.Storable;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.VMProvider;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.VMTemplate;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class AdvancedGrid <T extends Storable> implements DataContainer<T>{


	private AdvancedGridConfiguration configuration;
	private VerticalPanel thePanel;
	private StorableGrid<T> theGrid;
	

	private AdvancedGrid(final AdvancedGridConfiguration configuration) {
		this.configuration=configuration;
		
		thePanel=new VerticalPanel();
		thePanel.setSpacing(2);
//		thePanel.setBorderWidth(2);
		thePanel.setHeight("100%");
		thePanel.setWidth("100%");		
		

		final ButtonBar buttonBar=new ButtonBar(this,configuration.getManagedObjectType());
		thePanel.add(buttonBar);

		if(configuration.getManagedObjectType().equals(ObjectType.REMOTE_NODE)){
			buttonBar.setVisibleState(ButtonBarElement.START, true);
			buttonBar.setVisibleState(ButtonBarElement.STOP, true);
		}
		
		
		//*************GRID
		theGrid=new StorableGrid<T>(configuration);

		
//		FhnManagerController.eventBus.fireEvent(getRefreshEvent());
		thePanel.add(theGrid.getTheWidget());
	}
	

	
	
	@Override
	public Widget getTheWidget() {
		return thePanel;
	}
	
//	public VerticalPanel getThePanel() {
//		
//	}

	public RefreshGridEvent getRefreshEvent(){
		return new RefreshGridEvent(theGrid.getRefreshOption(), this);
	}
	
	@Override
	public void fireRefreshData() {
		FhnManagerController.eventBus.fireEvent(getRefreshEvent());
	}
	
	@Override
	public T getSelected() {		
		return theGrid.getSelected();
	}
	
	@Override
	public boolean hasSelection() {
		return theGrid.hasSelection();
	}
	
	@Override
	public void setData(List<T> toSet) {
		theGrid.setData(toSet);		
	}
	
	@Override
	public void setFilters(Map<String, String> toSet) {
		theGrid.setFilters(toSet);
	}
	
	
	///******************* STATIC LOGIC 
	
	private static HashMap<ObjectType,AdvancedGrid<? extends Storable>> grids=new HashMap<ObjectType, AdvancedGrid<? extends Storable>>();
	
	public static AdvancedGrid<? extends Storable> getCentralGrid(ObjectType gridType){
		if(grids.get(gridType)==null)
			switch(gridType){
			case SERVICE_PROFILE:{
				
				AdvancedGrid<ServiceProfile> toAdd=new AdvancedGrid<ServiceProfile>(AdvancedGridConfiguration.SERVICE_PROFILE);
				grids.put(gridType, toAdd);
				break;}
			case REMOTE_NODE : {AdvancedGrid<RemoteNode> toAdd=new AdvancedGrid<RemoteNode>(AdvancedGridConfiguration.REMOTE_NODE);
				grids.put(gridType, toAdd);
				break;}
			case VM_PROVIDER: {AdvancedGrid<VMProvider> toAdd=new AdvancedGrid<VMProvider>(AdvancedGridConfiguration.VM_PROVIDER);
			grids.put(gridType, toAdd);
			break;}
			case VM_TEMPLATES: {AdvancedGrid<VMTemplate> toAdd=new AdvancedGrid<VMTemplate>(AdvancedGridConfiguration.VM_TEMPLATE);
			grids.put(gridType, toAdd);
			break;}
			}
		return grids.get(gridType);
	}
}
