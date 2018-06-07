package org.gcube.portlets.admin.fhn_manager_portlet.client.wdigets.forms;

import java.util.HashMap;
import java.util.Map;

import org.gcube.portlets.admin.fhn_manager_portlet.client.wdigets.data.AdvancedGridConfiguration;
import org.gcube.portlets.admin.fhn_manager_portlet.client.wdigets.data.StorableGrid;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.Constants;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.ObjectType;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.RemoteNode;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.ServiceProfile;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.Storable;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.VMProvider;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.VMTemplate;

import com.google.gwt.user.client.ui.Widget;

public class ResourceSelectionStep <T extends Storable> implements StepDefinition{

	
	private StorableGrid<T> selectionGrid;
	private ObjectType type;
	private String title;
	
	public ResourceSelectionStep(ObjectType type){
		selectionGrid=new StorableGrid<T>(AdvancedGridConfiguration.byType(type));
		this.type=type;
		title="Select a "+type.getLabel();
	}
	
	@Override
	public Map<String, String> getDefinedFields() {
		HashMap<String,String> toReturn=new HashMap<String,String>();
		if(selectionGrid.hasSelection())
		switch(type){
		case  REMOTE_NODE :
			RemoteNode node=(RemoteNode) selectionGrid.getSelected();
			toReturn.put(Constants.REMOTE_NODE_ID,node.getId());
			toReturn.put(Constants.SERVICE_PROFILE_ID, node.getServiceProfileId());
			toReturn.put(Constants.VM_TEMPLATE_ID, node.getVmTemplateId());
			toReturn.put(Constants.VM_PROVIDER_ID, node.getVmProviderId());
		break;
		case SERVICE_PROFILE : ServiceProfile profile=(ServiceProfile) selectionGrid.getSelected();
			toReturn.put(Constants.SERVICE_PROFILE_ID, profile.getId());			
		break;
		case VM_PROVIDER : VMProvider provider=(VMProvider) selectionGrid.getSelected();
			toReturn.put(Constants.VM_PROVIDER_ID, provider.getId());
		break;
		case VM_TEMPLATES : VMTemplate template=(VMTemplate) selectionGrid.getSelected();
			toReturn.put(Constants.VM_TEMPLATE_ID, template.getId());
			toReturn.put(Constants.VM_PROVIDER_ID, template.getProviderId());
		break;
		}		
		return toReturn;
	}
	
	@Override
	public String getMessage() {
		if(!selectionGrid.hasSelection()) return "Select a "+type.getLabel();
		else return "Selected "+selectionGrid.getSelected().getName();
	}
	@Override
	public Widget getWidget() {
		return selectionGrid.getTheWidget();
	}
	
	public boolean isStepValid() {
		return selectionGrid.hasSelection();
	};
	
	@Override
	public void setStatus(Map<String, String> status) {
		selectionGrid.setFilters(status);
	}
	
	@Override
	public void onShowStep() {
		selectionGrid.fireRefreshData();
	}
	
	@Override
	public String getTitle() {
		return title;
	}
	
}
