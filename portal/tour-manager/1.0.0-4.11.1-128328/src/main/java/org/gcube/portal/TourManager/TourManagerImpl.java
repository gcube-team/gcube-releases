package org.gcube.portal.TourManager;
import java.io.Serializable;

import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.util.ManagementUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.model.User;
import com.liferay.portal.security.auth.PrincipalThreadLocal;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.security.permission.PermissionThreadLocal;
import com.liferay.portal.service.UserLocalServiceUtil;


/**
 * Tour Manager Implementation class to handle users' guided tours into gCube portals.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class TourManagerImpl implements TourManager {

	/**
	 *  Logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(TourManagerImpl.class);

	/**
	 * This is the number type for group of text values custom fields (Liferay)
	 */
	private static final int GROUP_OF_TEXT_VALUES_TYPE_LIFERAY = 16;

	/**
	 * {@inheritDoc}
	 */
	public void setShowNextTime(String callerIdentifier, int versionNumber, boolean show, String username){

		logger.debug("[Tour-Manager] callerid is " + callerIdentifier + ", version number is " + versionNumber 
				+ " and show action is " + show);

		if(versionNumber <= 0)
			throw new IllegalArgumentException("[Tour-Manager] Version number must be greater than zero!");

		try{
			
			// set permission checker
			long adminId = LiferayUserManager.getAdmin().getUserId();
			PrincipalThreadLocal.setName(adminId);
			PermissionChecker permissionChecker = PermissionCheckerFactoryUtil.create(UserLocalServiceUtil.getUser(adminId));
			PermissionThreadLocal.setPermissionChecker(permissionChecker); 
			
			// User object reference
			User user = UserLocalServiceUtil.getUserByScreenName(ManagementUtils.getCompany().getCompanyId(), username);

			// check if it already exists
			if(!checkIfCustomFieldExists(user, callerIdentifier)){

				logger.debug("[Tour-Manager] Custom field with id "  + callerIdentifier  + 
						" doesn't exist yet. Going to create it for user " + username);

				// set the properties: we want checkbox type and group of text values, hidden
				UnicodeProperties properties = new UnicodeProperties();
				properties.put("display-type", "checkbox"); // checkbox type
				properties.put("hidden", "1"); // hidden
				properties.put("index-type", "0"); // not searchable
				properties.put("visible-with-update-permission", "0"); // not visible after updates

				// cast the version number to a string
				String[] values = new String[1];
				values[0] = String.valueOf(versionNumber);

				// create with this value (it will be unchecked!)
				createCustomNewAttribute(user, callerIdentifier, properties, GROUP_OF_TEXT_VALUES_TYPE_LIFERAY, values);

				logger.debug("[Tour-Manager] Custom field " +  callerIdentifier  + " created for user " + username);

			}else{

				// get the checked value (if any)
				String[] currentValues = (String[])getCustomFieldValues(user, callerIdentifier);

				// check the type of action to take
				if(show){

					// this is the case in which the portlet asks for showing this tour
					if(currentValues == null || currentValues.length == 0){

						// in this case the user can still see the tour, do nothing

					}else{

						/* 
						 in this case we need to check if the version matches:
						 if they do, the user already asked for hiding the tour, so do nothing
						 if they don't, we need to update the version since a new tour has to be shown
						 */

						int lastVersion = Integer.valueOf(currentValues[0]);

						if(lastVersion == versionNumber){
							logger.debug("[Tour-Manager] The user has already said that he doesn't want to see again this tour version");
							return;
						}
						else if(lastVersion < versionNumber){

							// we need to change the default value
							String[] newValue = new String[1];
							newValue[0] = String.valueOf(versionNumber);
							setAttributeNewDefaultValue(user, callerIdentifier, newValue);

							// we need to uncheck it for the current user (the other users will have it automatically unchecked)
							String[] uncheckValue = new String[1];
							updateCustomAttributeValues(user, callerIdentifier, uncheckValue);

						}else{
							// the version number is wrong...
							logger.debug("[Tour-Manager] The number version given seems to be old. The last available for this tour is " + lastVersion);
						}
					}

				}else{

					// this is the case in which the user asks for no longer see this tour
					if(currentValues == null || currentValues.length == 0){

						// in this case the user no longer wants to see the tour (check the value)
						String[] newValue = new String[1];
						newValue[0] = String.valueOf(versionNumber);
						updateCustomAttributeValues(user, callerIdentifier, newValue);

					}else{

						// this is something wrong...
						logger.debug("[Tour-Manager] Mmm... something wrong here");
					}
				}
			}
		}catch(Exception e){
			logger.debug("An error occured, sorry", e);
		}

	}

	/**
	 * {@inheritDoc}
	 * @throws Exception 
	 * @throws SystemException 
	 * @throws PortalException 
	 */
	public boolean isTourShowable(String callerIdentifier, int versionNumber, String currentUser) throws PortalException, SystemException, Exception{

		logger.debug("[Tour-Manager] callerid is " + callerIdentifier + ", asking if it can show the tour for version " + versionNumber);

		if(versionNumber <= 0)
			throw new IllegalArgumentException("[Tour-Manager] Version number must be greater than zero!");
		
		// set permission checker
		long adminId = LiferayUserManager.getAdmin().getUserId();
		PrincipalThreadLocal.setName(adminId);
		PermissionChecker permissionChecker = PermissionCheckerFactoryUtil.create(UserLocalServiceUtil.getUser(adminId));
		PermissionThreadLocal.setPermissionChecker(permissionChecker); 
		
		// User object reference
		User user = UserLocalServiceUtil.getUserByScreenName(ManagementUtils.getCompany().getCompanyId(), currentUser);

		String[] values = (String[])getCustomFieldValues(user, callerIdentifier);

		// unchecked values are not retrieved, so immediately return true
		if(values == null || values.length == 0)
			return true;
		else{

			// in this case we need to evaluate if that version can be shown (get the last element that is the only one to look at)
			// the returned versions are checked checkbox -> so the user said he doesn't want again the tour
			int latestVersion = Integer.parseInt(values[0]);

			if(latestVersion < versionNumber)
				return true;
		}
		return false;
	}

	/**
	 * Check if a custom field with customFieldName exists for the user
	 * @param user
	 * @param customFieldName
	 * @return
	 * @throws PortalException
	 * @throws SystemException
	 */
	private boolean checkIfCustomFieldExists(User user, String customFieldName) throws PortalException, SystemException{

		return user.getExpandoBridge().hasAttribute(customFieldName);
	}

	/**
	 * Try to create a custom attribute for this user.
	 * @param user
	 * @param customFieldName
	 * @param properties
	 * @param type the type according Liferay Expando Bridge (e.g., 16 for a group of text of values)
	 * @param defaultValues default values for the attribute
	 * @throws Exception on fail
	 */
	private void createCustomNewAttribute(User user, String customFieldName, UnicodeProperties properties, int type, 
			Serializable defaultValues) throws Exception{

		logger.debug("[Tour-Manager] Trying to create new custom field " + customFieldName + " for user " + user.getScreenName() + ". With properties " + properties
				+ ", type " + type + " and values " + defaultValues);
		
		// set its value
		user.getExpandoBridge().addAttribute(customFieldName, type, defaultValues);

		// set its properties
		user.getExpandoBridge().setAttributeProperties(customFieldName, properties);
	}

	/**
	 * Retrieve value(s) of a custom field.
	 * @param user
	 * @param customFieldName
	 * @return a list of checked values
	 * @throws PortalException
	 * @throws SystemException
	 * @throws Exception
	 */
	private Serializable getCustomFieldValues(User user, String customFieldName) throws PortalException, SystemException, Exception{

		logger.info("[Tour-Manager] Trying to get current values for custom field " + customFieldName + " for user " + user.getScreenName());
		
		return user.getExpandoBridge().getAttribute(customFieldName); 
	}

	/**
	 * Try to set new values for a given user's custom field.
	 * @param user
	 * @param customFieldName
	 * @param newValues
	 * @return true on success
	 * @throws PortalException
	 * @throws SystemException
	 * @throws Exception
	 */
	private void updateCustomAttributeValues(User user, String customFieldName, Serializable newValues) throws PortalException, SystemException, Exception{
		
		user.getExpandoBridge().setAttribute(customFieldName, newValues);
	}

	/**
	 * Change the default value(s) of this custom field.
	 * @param username
	 * @param customFieldName
	 * @param newValue
	 * @throws Exception 
	 * @throws SystemException 
	 * @throws PortalException 
	 */
	private void setAttributeNewDefaultValue(User user,
			String customFieldName, String[] newValue) throws PortalException, SystemException, Exception {

		logger.info("[Tour-Manager] Trying to set new values for custom field " + customFieldName);
		
		user.getExpandoBridge().setAttributeDefault(customFieldName, newValue);
	}

}
