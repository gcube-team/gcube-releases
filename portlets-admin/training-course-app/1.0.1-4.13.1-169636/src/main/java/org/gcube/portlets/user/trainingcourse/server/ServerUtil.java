package org.gcube.portlets.user.trainingcourse.server;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portal.trainingmodule.TrainingModuleManager;
import org.gcube.portal.trainingmodule.shared.TrainingCourseDTO;
import org.gcube.portlets.user.trainingcourse.shared.TrainingContact;
import org.gcube.portlets.user.trainingcourse.shared.TrainingCourseObj;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.exception.UserRetrievalFault;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.portal.service.UserLocalServiceUtil;


// TODO: Auto-generated Javadoc
/**
 * The Class ServerUtil.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 31, 2018
 */
public class ServerUtil {
	
	
	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(ServerUtil.class);
	
	/** The um. */
	private static UserManager um = new LiferayUserManager();

	/**
	 * Checks if is within portal.
	 *
	 * @return true if you're running into the portal, false if in development
	 */
	public static boolean isWithinPortal() {
		try {
			UserLocalServiceUtil.getService();
			return true;
		}
		catch (Exception ex) {
			logger.trace("Development Mode ON");
			return false;
		}
	}
	

	/**
	 * To training course.
	 *
	 * @param dto the dto
	 * @param scope the scope
	 * @return the training course
	 */
	public static TrainingCourseObj toTrainingCourse(TrainingCourseDTO dto, String scope){
		
		TrainingCourseObj tc = null;
		
		if(dto!=null) {
			
			tc = new TrainingCourseObj(dto.getInternalId(), dto.getTitle(), dto.getDescription(), dto.getCommitment(), dto.getLanguages(), dto.getScope(), dto.getOwnerLogin(), dto.getWorkspaceFolderId(), dto.getWorkspaceFolderName(), dto.getCreatedBy(), dto.isCourseActive(), dto.getSharedWith());
			
			if(dto.getSharedWith()!=null) {
				List<TrainingContact> users = new ArrayList<>(dto.getSharedWith().size());
				List<TrainingContact> groups = new ArrayList<>(1);
				for (String login : dto.getSharedWith()) {
					TrainingContact contact = resolveContact(login,scope);
					if(contact.isGroup()) {
						groups.add(contact);
					}else
						users.add(contact);
				}
				tc.setUserSharedWith(users);
				tc.setGroupSharedWith(groups);
			}
			
		}
		
		return tc;
		
	}

	
	/**
	 * Scope to HL group.
	 *
	 * @param scope the scope
	 * @return the string
	 */
	public static String scopeToHLGroup(String scope) {
		
		if(scope==null)
			return null;
		
		String contextAsGroup = scope.substring(1,scope.length()).replaceAll("/", TrainingModuleManager.HL_GROUP_SEPARATOR);
		logger.debug("Scope "+scope+ " to group: "+contextAsGroup);
		return contextAsGroup;
		
	}
	


	/**
	 * Group HL to scope.
	 *
	 * @param group the group
	 * @return the string
	 */
	public static String groupHLToScope(String group) {
		
		if(group==null)
			return null;
		
		String groupAsScope = group.replaceAll(TrainingModuleManager.HL_GROUP_SEPARATOR, "/");
		groupAsScope="/"+groupAsScope;
		logger.debug("Group "+group+ " to scope: "+groupAsScope);
		return groupAsScope;
		
	}

	
	/**
	 * Resolve contact.
	 *
	 * @param portalLogin the portal login
	 * @param scope the scope
	 * @return the training contact
	 */
	public static TrainingContact resolveContact(String portalLogin, String scope){

		if(portalLogin==null)
			return new TrainingContact(portalLogin, "", "", "", false);

		if (isWithinPortal()) { //INTO PORTAL

			try {
				
				if(um==null)
					um = new LiferayUserManager();
				
				GCubeUser curr = um.getUserByUsername(portalLogin);
				return new TrainingContact(portalLogin, curr.getFullname(), curr.getEmail(), curr.getUserAvatarURL(), false);

			} catch (UserManagementSystemException e) {
				logger.warn("UserManagementSystemException, during getting fullname for: "+portalLogin);
			} catch (UserRetrievalFault e) {
				logger.warn("UserRetrievalFault, during getting fullname for: "+portalLogin);
			}catch (Exception e) {
				logger.error("An error occurred in getUserFullName "+e,e);
	
			}
			//CHEKING IF IT IS A GROUP
			String scopeAsGroup = scopeToHLGroup(scope);
			if(scopeAsGroup!=null && scopeAsGroup.compareToIgnoreCase(portalLogin)==0) {
				String fullName = scopeAsGroup.substring(scopeAsGroup.lastIndexOf(TrainingModuleManager.HL_GROUP_SEPARATOR)+1,scopeAsGroup.length());
				return new TrainingContact(portalLogin, fullName, "", "", true);
			}
			
			return new TrainingContact(portalLogin, "", "", "", false);
			
		}else{
			logger.trace("DEVELOPEMENT MODE ON");
			logger.trace("Returning input login: "+portalLogin);
			//CHEKING IF IT IS A GROUP
			String scopeAsGroup = scopeToHLGroup(scope);
			if(scopeAsGroup!=null && scopeAsGroup.compareToIgnoreCase(portalLogin)==0) {
				String fullName = scopeAsGroup.substring(scopeAsGroup.lastIndexOf(TrainingModuleManager.HL_GROUP_SEPARATOR)+1,scopeAsGroup.length());
				return new TrainingContact(portalLogin, fullName, "", "", true);
			}else
				return new TrainingContact(portalLogin, "", "", "", false);
		}
	}


}
