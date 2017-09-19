package org.gcube.portlets.admin.vredefinition.client;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

import org.gcube.portlets.admin.vredefinition.shared.Functionality;
import org.gcube.portlets.admin.vredefinition.shared.VREDescriptionBean;
import org.gcube.portlets.admin.vredefinition.shared.exception.VREDefinitionException;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("VREDefinitionServiceImpl")
public interface VREDefinitionService extends RemoteService {
	
	/**
	 * Retrieve VRE (that is going to be created) information
	 * @return an hashmap with a VREDefinitionBean object, a vre designer and 
	 * some vre managers (if any).
	 */
	Map<String, Serializable> getVRE();

	/**
	 * Request the vre functionalities to show (the returned object is a tree like structure that could be navigated)
	 * @param isEdit
	 * @return
	 * @throws VREDefinitionException
	 */
	ArrayList<Functionality> getFunctionality(boolean isEdit) throws VREDefinitionException;
	
	/**
	 * Create a VRE with the definition information included into the description bean and
	 * with the functionalities set into the functionalities array list
	 * @param bean
	 * @param functionalities
	 * @return
	 * @throws VREDefinitionException
	 */
	boolean setVRE(VREDescriptionBean bean, ArrayList<Functionality> functionalities) throws VREDefinitionException; 

}
