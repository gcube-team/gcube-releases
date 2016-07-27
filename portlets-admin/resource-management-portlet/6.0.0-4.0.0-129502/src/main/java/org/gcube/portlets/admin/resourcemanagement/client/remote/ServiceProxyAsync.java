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
 * Filename: ServiceProxyAsync.java
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

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The asynchronous representation of {@link ServiceProxy} interface.
 * Needed to implement the server side async RPC.
 *
 * @author Daniele Strollo (ISTI-CNR)
 */
public interface ServiceProxyAsync {

	void emptyCache(AsyncCallback<Void> callback);
	void setUseCache(boolean flag, AsyncCallback<Void> callback);

	void setSuperUser(boolean superUser, AsyncCallback<Void> callback);

	void setCurrentScope(String scope, AsyncCallback<Void> callback);

	void initStatus(AsyncCallback<CurrentStatus> callback);

	void initScopes(boolean doClean, AsyncCallback<Void> callback);

	void addResourcesToScope(final String resType, final List<String> resourceIDs, final String scope, AsyncCallback<Tuple<String>> callback);
	
	void removeResourcesFromScope(final String resType, final List<String> resourceIDs, final String scope, AsyncCallback<Tuple<String>> callback);

	void deploy(final List<String> ghnsID, final List<String> servicesID, AsyncCallback<String> callback);
	void checkDeployStatus(String scope, String deployID, AsyncCallback<Tuple<String>> callback);

	void getResourcesModel(String scope, String type, String subType, final List<Tuple<String>> additionalMaps, AsyncCallback<List<ResourceDescriptor>> callback);

	void getGenericResourceDescriptor(String scope, String resID, AsyncCallback<ResourceDescriptor> callback);

	/**
	 * @see ServiceProxy#getAvailableScopes()
	 */
	void getAvailableScopes(AsyncCallback<List<String>> callback);

	void getAvailableAddScopes(AsyncCallback<List<String>> callback);

	void getResourceTypeTree(String scope,
			AsyncCallback<HashMap<String, ArrayList<String>>> callback);

	void getRelatedResources(String type, String id, String scope, AsyncCallback<List<String>> callback);

	/**
	 * @see ServiceProxy#getResourcesByType(String, String)
	 */
	void getResourcesByType(String scope, String type, AsyncCallback<List<String>> callback);

	/**
	 * @see ServiceProxy#getResourcesBySubType(String, String, String)
	 */
	void getResourcesBySubType(String scope, String type, String subtype, AsyncCallback<List<String>> callback);


	/**
	 * @see ServiceProxy#getWSResources(String)
	 */
	void getWSResources(String scope, AsyncCallback<List<String>> callback);

	/**
	 * @see ServiceProxy#getResourceByID(String, String, String)
	 */
	void getResourceByID(String scope, String type, String resID, AsyncCallback<CompleteResourceProfile> callback);

	void createGenericResource(
			final String id,
			final String name,
			final String description,
			final String body,
			final String subType,
			AsyncCallback<String> callback);

	void updateGenericResource(
			final String id,
			final String name,
			final String description,
			final String body,
			final String subType,
			AsyncCallback<Void> callback);

	void getGenericResourcePlugins(AsyncCallback<Map<String, GenericResourcePlugin>> callback);
	


	/***********************************************************
	 * RESOURCE OPERATIONS
	 **********************************************************/
	void doOperation(SupportedOperations opCode, String scope, List<ResourceDescriptor> resources, AsyncCallback<Void> callback) throws Exception;
	
}
