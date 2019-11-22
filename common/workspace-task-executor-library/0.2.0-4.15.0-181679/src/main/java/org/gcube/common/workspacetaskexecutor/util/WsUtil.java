/**
 *
 */
package org.gcube.common.workspacetaskexecutor.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.storagehubwrapper.server.StorageHubWrapper;
import org.gcube.common.storagehubwrapper.server.tohl.Workspace;
import org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * The Class WsUtil.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * 
 * Sep 6, 2019
 */
public class WsUtil {

	private static Logger logger = LoggerFactory.getLogger(WsUtil.class);
	
	private Workspace theWorkspace;

	/**
	 * Gets the workspace from storage hub.
	 *
	 * @param scope the scope
	 * @param token the token
	 * @return the workspace from storage hub
	 * @throws Exception             the exception
	 */
	public Workspace getWorkspaceFromStorageHub(String scope, String token) throws Exception {
		StorageHubWrapper storageHubWrapper = new StorageHubWrapper(scope, token, false, false, true);
		return storageHubWrapper.getWorkspace();
	}
	
	/**
	 * Check owner.
	 *
	 * @throws Exception the exception
	 */
	private void checkInitParameters() throws Exception {
		
		String scope = ScopeProvider.instance.get();
		Validate.notNull(scope, "The scope is null. You must set a valid scope by "+ScopeProvider.class.getSimpleName());
		String token = SecurityTokenProvider.instance.get();
		Validate.notNull(token, "The user token is null. You must set a valid token by "+SecurityTokenProvider.class.getSimpleName());
	}
	
	
	/**
	 * Gets the workspace.
	 *
	 * @return the workspace
	 * @throws Exception the exception
	 */
	public Workspace getWorkspace() throws Exception{
		
		if(theWorkspace==null) {
			checkInitParameters();
			String scope = ScopeProvider.instance.get();
			String token = SecurityTokenProvider.instance.get();
			theWorkspace = getWorkspaceFromStorageHub(scope, token);
		}
		return theWorkspace;
			
	}
	

	/**
	 * Gets the properties.
	 *
	 * @param item the item
	 * @return the properties
	 * @throws Exception the exception
	 */
	public Map<String, String> getProperties(WorkspaceItem item) throws Exception {

		Validate.notNull(item, "The input "+WorkspaceItem.class.getSimpleName()+" instance is null");
		Workspace workspace = getWorkspace();
		
		Map<String, Object> map;
		if(item.getPropertyMap()==null) {
			try {
				map = workspace.getMetadata(item.getId());
			}
			catch (Exception e) {
				//silent
				return null;
			}
		}else {
			map = item.getPropertyMap().getValues();
		}
		
		return toMapString(map);
	}
	
	
	/**
	 * Gets the property value.
	 *
	 * @param item the item
	 * @param propertyName the property name
	 * @return the property value
	 * @throws Exception the exception
	 */
	public String getPropertyValue(WorkspaceItem item, String propertyName) throws Exception{

		Validate.notNull(item, "The input "+WorkspaceItem.class.getSimpleName()+" instance is null");
		Map<String, String> properties = getProperties(item);

		if(properties==null)
			return null;

		return properties.get(propertyName);

	}
	
	
	/**
	 * Sets the property value.
	 *
	 * @param item the item
	 * @param propertyName the property name
	 * @param propertyValue the property value
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	public boolean setPropertyValue(WorkspaceItem item, String propertyName, String propertyValue) throws Exception{

		Validate.notNull(item, "The input "+WorkspaceItem.class.getSimpleName()+" instance is null");
		
		Map<String, String> properties = getProperties(item);
		try {

			if(properties==null){
				properties = new HashMap<String, String>();
			}
			
			properties.put(propertyName, propertyValue);
			Map<String, Object> map = toMapObject(properties);
			Workspace workspace = getWorkspace();
			workspace.updateMetadata(item.getId(), map);
			logger.info("Added properties "+properties+" to item: "+item.getId());
			return true;
		}
		catch (Exception e) {
			logger.warn("Error occurred on updating the metadata for item id: "+item.getId(),e);
			return false;
		}

	}
	
	
	/**
	 * To map object.
	 *
	 * @param map the map
	 * @return the map
	 */
	public static Map<String, Object> toMapObject(Map<String, String> map) {
		
		if(map==null)
			return null;
		
		Map<String,Object> newMap = new HashMap<String, Object>(map.size());
		newMap.putAll(map);
		return newMap;

	}
	
	/**
	 * To map string.
	 *
	 * @param map the map
	 * @return the map
	 */
	public static Map<String, String> toMapString(Map<String, Object> map) {

		Map<String, String> newMap = new HashMap<String, String>(map.size());
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			if (entry.getValue() instanceof String) {
				newMap.put(entry.getKey(), (String) entry.getValue());
			}
		}
		return newMap;
	}

}
