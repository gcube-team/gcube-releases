package org.gcube.portlets.admin.vredefinition.client;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.portlets.admin.vredefinition.client.model.VREFunctionalityModel;
import org.gcube.portlets.admin.vredefinition.shared.ExternalResourceModel;
import org.gcube.portlets.admin.vredefinition.shared.VREDescriptionBean;
import org.gcube.portlets.admin.vredefinition.shared.exception.VREDefinitionException;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("VREDefinitionServiceImpl")
public interface VREDefinitionService extends RemoteService {
	ArrayList<ExternalResourceModel> getResourceCategoryByFunctionality(String funcId);
	/**
	 * @return VRE Existing Names
	 */
	String[] getExistingNames();
	
	Map<String, Object> getVRE() throws VREDefinitionException;
	
	/**
	 * @param name VRE name 
	 * @param description VRE description
	 * @param designer VRE designer
	 * @param manager VRE manager
	 * @param fromDate VRE start date
	 * @param toDate VRE end date
	 * @return OK string if the operation is performed correctly
	 * @throws VREDefinitionException 
	 */
	String setVRE(VREDescriptionBean bean, String[] functionalityIDs, HashMap<String, List<ExternalResourceModel>> funcToExternalResources) throws VREDefinitionException; 
	
		
	VREFunctionalityModel getFunctionality(boolean isEdit) throws VREDefinitionException;

	Map<String,Object> isEditMode();

}
