/**
 *
 */
package org.gcube.common.workspacetaskexecutor.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Properties;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * The Class WsUtil.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Apr 26, 2018
 */
public class WsUtil {

	private static Logger logger = LoggerFactory.getLogger(WsUtil.class);


	/**
	 * Gets the workspace.
	 *
	 * @param username the username
	 * @return the workspace
	 * @throws InternalErrorException the internal error exception
	 * @throws HomeNotFoundException the home not found exception
	 * @throws WorkspaceFolderNotFoundException the workspace folder not found exception
	 */
	public static Workspace getWorkspace(String username) throws InternalErrorException, HomeNotFoundException, WorkspaceFolderNotFoundException{
		logger.trace("Get Workspace");
		Validate.notNull(username, "The username is null");
		return HomeLibrary.getUserWorkspace(username);
	}


	/**
	 * Gets the item.
	 *
	 * @param username the username
	 * @param itemId the item id
	 * @return the item
	 * @throws InternalErrorException the internal error exception
	 * @throws HomeNotFoundException the home not found exception
	 * @throws WorkspaceFolderNotFoundException the workspace folder not found exception
	 * @throws ItemNotFoundException the item not found exception
	 */
	public static WorkspaceItem getItem(String username, String itemId) throws InternalErrorException, HomeNotFoundException, WorkspaceFolderNotFoundException, ItemNotFoundException{
		logger.trace("Get Workspace Item");
		Validate.notNull(itemId, "The itemId is null");
		return getWorkspace(username).getItem(itemId);

	}


	/**
	 * Gets the properties.
	 *
	 * @param item the item
	 * @return the properties
	 */
	public static Map<String, String> getProperties(WorkspaceItem item) {

		Properties properties;
		try {
			properties = item.getProperties();
			if (properties == null)
				return null;
			return properties.getProperties();
		}
		catch (InternalErrorException e) {
			return null;
		}
	}


	/**
	 * Gets the properties.
	 *
	 * @param item the item
	 * @param propertyName the property name
	 * @return the properties
	 */
	public static String getPropertyValue(WorkspaceItem item, String propertyName){

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
	 */
	public static boolean setPropertyValue(WorkspaceItem item, String propertyName, String propertyValue){

		Map<String, String> properties = getProperties(item);
		try {

			if(properties==null){
				properties = new HashMap<String, String>();
			}

			Properties propertiesOBJ = item.getProperties();
			properties.put(propertyName, propertyValue);
			propertiesOBJ.addProperties(properties);
			logger.debug("Added properties "+properties+" to item: "+item.getId());
			return true;
		}
		catch (InternalErrorException e) {
			return false;
		}

	}

}
