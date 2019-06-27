/****************************************************************************
 *  This software is part of the gCube Project.
 *  Site: http://www.gcube-system.org/
 ****************************************************************************
 * The gCube/gCore software is licensed as Free Open Source software
 * conveying to the EUPL (http://ec.europa.eu/idabc/eupl).
 * The software and documentation is provided by its authors/distributors
 * "as is" and no expressed or
 * implied warranty is given for its use, quality or fitness for a
 * particular case.
 ****************************************************************************
 * Filename: RemoteService.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.portlets.admin.resourcemanagement.client.remote;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.resourcemanagement.support.client.utils.CurrentStatus;
import org.gcube.resourcemanagement.support.shared.operations.SupportedOperations;
import org.gcube.resourcemanagement.support.shared.plugins.GenericResourcePlugin;
import org.gcube.resourcemanagement.support.shared.types.Tuple;
import org.gcube.resourcemanagement.support.shared.types.datamodel.CompleteResourceProfile;
import org.gcube.resourcemanagement.support.shared.types.datamodel.ResourceDescriptor;

import com.google.gwt.user.client.rpc.RemoteService;

/**
 * Interface of RPC servlet implementing the server side logics
 * of the application.
 * @author Daniele Strollo (ISTI-CNR)
 */
public interface ServiceProxy extends RemoteService {
	
	boolean enableSuperUserMode(String password);
	
	CurrentStatus initStatus();

	void emptyCache();

	void setUseCache(boolean flag);

	void initScopes(boolean doClean);

	void setSuperUser(boolean superUser);

	/**
	 * This way the servlet stores the scope in the session.
	 */
	void setCurrentScope(String scope);

	/**
	 * @return the list of all available scopes.
	 */
	List<String> getAvailableScopes();
	List<String> getAvailableAddScopes();


	/**
	 *
	 * @param resType
	 * @param resourceIDs
	 * @param scope
	 * @return the xml representation of the generated report (if not failed).
	 * it is a tuple consisting of:
	 * <br/>0) the reportID
	 * <br/>1) the resourceType
	 * <br/>2) the xmlrepresentation
	 * <br/>3) the html representation
	 * @throws Exception
	 */
	Tuple<String> addResourcesToScope(final String resType, final List<String> resourceIDs, final String scope) throws Exception;
	
	Tuple<String> removeResourcesFromScope(String resType, List<String> resourceIDs, String scope) throws Exception;


	String deploy(final List<String> ghnsID, final List<String> servicesID) throws Exception;
	Tuple<String> checkDeployStatus(String scope, String deployID) throws Exception;

	List<ResourceDescriptor> getResourcesModel(String scope, String type, String subType, final List<Tuple<String>> additionalMaps) throws Exception;
	/**
	 * Given a scope, provides the set of (Type, SubType) couples
	 * defining resources in the system.
	 * The key of returned hashmap is the type and the associated
	 * value is a list of string representing its related sub-types.
	 * @see org.gcube.portlets.admin.resourcemanagement.server.gcube.services.ISClientRequester#getResourcesTypes(org.gcube.common.core.scope.GCUBEScope)
	 * @param scope the scope of resources
	 * @return the hash of (type, list(subtypes))
	 */
	 HashMap<String, ArrayList<String>> getResourceTypeTree(String scope) throws Exception;

	/**
	 * Given a type retrieves its related resources.
	 * E.g. for GHN returns the RunningInstances on it.
	 * @param scope
	 * @param type
	 * @return
	 */
	List<String> getRelatedResources(String type, String id, String scope);

	/**
	 * For a given type returns all the resources defined in the IS having
	 * the chosen type.
	 * @param scope the scope of resources
	 * @param type the type of searched resources (e.g. GHN, RunningInstance, ...)
	 * @return list of XML profiles of resources found
	 */
	List<String> getResourcesByType(String scope, String type);

	/**
	 * Filters the resources in a given scope by their type and subtype.
	 * For example it allows to retrieve in a scope all the GHN (type)
	 * defined in a domain (its subtype).
	 * @param scope the scope of resources
	 * @param type the type of searched resources (e.g. GHN, RunningInstance, ...)
	 * @param subtype the subtype of search resources (e.g. for GHN is its domain).
	 * @return list of XML profiles of resources found
	 */
	List<String> getResourcesBySubType(String scope, String type, String subtype);

	/**
	 * Used for generic resource editing form to retrieve the profile.
	 * @param scope
	 * @param resID
	 * @return
	 * @throws Exception
	 */
	ResourceDescriptor getGenericResourceDescriptor(String scope, String resID) throws Exception;

	/**
	 * Returns the list of WSResources
	 * @param scope the scope of resources
	 * @return list of XML profiles of resources found
	 */
	List<String> getWSResources(String scope);

	/**
	 * Retrieves the XML profiles and its HTML representation of
	 * a resource given its unique identifier and its type.
	 * Notice that the ID is ensured to be unique for resources
	 * of the same type.
	 * @param scope the scope of the resource
	 * @param type the type of the searched resource (e.g. GHN, RunningInstance, ...)
	 * @param resID the unique identifier of the resource
	 * @return a couple of strings (XMLProfile, HTMLProfileRepresentation)
	 */
	CompleteResourceProfile getResourceByID(String scope, String type, String resID);


	String createGenericResource(
			final String id,
			final String name,
			final String description,
			final String body,
			final String subType)
	throws Exception;

	void updateGenericResource(
			final String id,
			final String name,
			final String description,
			final String body,
			final String subType)
	throws Exception;

	Map<String, GenericResourcePlugin> getGenericResourcePlugins() throws Exception;
	
	/***********************************************************
	 * RESOURCE OPERATIONS
	 **********************************************************/
	void doOperation(SupportedOperations opCode, String scope, List<ResourceDescriptor> resources)
	throws Exception;


	
}
