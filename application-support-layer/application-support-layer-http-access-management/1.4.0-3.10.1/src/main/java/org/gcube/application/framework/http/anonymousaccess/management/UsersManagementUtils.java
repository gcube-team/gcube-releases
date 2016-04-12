package org.gcube.application.framework.http.anonymousaccess.management;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.util.ASLGroupModel;
import org.gcube.application.framework.core.util.Settings;
import org.gcube.vomanagement.usermanagement.exception.GroupRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.impl.liferay.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.impl.liferay.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.GroupModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UsersManagementUtils {
	
	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(UsersManagementUtils.class);
	
//	String umServiceLocation = new String();
	
	public UsersManagementUtils() {
//		StringBuffer fileData = new StringBuffer(1000);
//        BufferedReader reader = null;
//		try {
//			reader = new BufferedReader(new FileReader(Settings.getInstance().getProperty("sharedDir")+ File.separator +  "UMServiceLocation.config"));
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			logger.error("Exception:", e);
//		}
//		int numRead = 0;
//		char[] buf = new char[1024];
//		try {
//			while((numRead=reader.read(buf)) != -1){
//			    String readData = String.valueOf(buf, 0, numRead);
//			    fileData.append(readData);
//			    buf = new char[1024];
//			}
//		} catch (IOException e) {
//			logger.error("Exception:", e);
//		}
//        try {
//			reader.close();
//		} catch (IOException e) {
//			logger.error("Exception:", e);
//		}
//        umServiceLocation = fileData.toString().trim();
	}
	
	public ArrayList<String> getAllScopes(ASLSession session) {
		ArrayList<String> groupsNames = new ArrayList<String>();
		LiferayGroupManager liferayGroupManager = new LiferayGroupManager();
		try {
			List<GroupModel> groupList = liferayGroupManager.listGroups();
			for (GroupModel groupModel : groupList) {
				logger.debug("Group NAME " + groupModel.getGroupName());
				if(!liferayGroupManager.getRootVO().getGroupId().equals(groupModel.getGroupId())){
					String scope = liferayGroupManager.getScope(groupModel.getGroupId());
					if (scope.startsWith("/")) 
						groupsNames.add(scope);
				}
			}
		} 
		catch (UserManagementSystemException e) {
			logger.error("Exception:", e);
		} catch (GroupRetrievalFault e) {
			logger.error("Exception:", e);
		}
		return groupsNames;
	}
	
	
	public String getRootVO() {
		LiferayGroupManager liferayGroupManager = new LiferayGroupManager();
		try {
			String rootVo = liferayGroupManager.getRootVO().getGroupId();
			String rootVoName = getScope(rootVo);
			logger.debug("RootVo returning: " + rootVoName);
			return rootVoName;
		} catch (GroupRetrievalFault e) {
			logger.error("Exception:", e);
		} catch (UserManagementSystemException e) {
			logger.error("Exception:", e);
		}
		return null;
	}

	
	public String getUserId(String username) {
		String userId = null;
		try {
			LiferayUserManager liferayUserManager = new LiferayUserManager();
			userId = liferayUserManager.getUserId(username);
		} catch (UserManagementSystemException e) {
			logger.error("Exception:", e);
		}
		return userId;
	}
	
	
	public List<ASLGroupModel> listGroupsByUser(String userId) {
		List<ASLGroupModel> groupsByUser = new ArrayList<ASLGroupModel>();
		try {
			LiferayGroupManager liferayGroupManager = new LiferayGroupManager();
			List<GroupModel> groups = liferayGroupManager.listGroupsByUser(userId);
			if(groups==null)
				logger.debug("User is in no group.");
			for(GroupModel group : groups){
				ASLGroupModel grm = new ASLGroupModel();
				grm.setGroupId(new Long(group.getGroupId()));
				grm.setGroupName(group.getGroupName());
				grm.setDescription(group.getDescription());
				groupsByUser.add(grm);
			}
		} 
		catch (UserManagementSystemException e) {
			logger.error("Exception:", e);
		}
		return groupsByUser;
	}
	
	public List<ASLGroupModel> listGroups()  {
		List<ASLGroupModel> groups = new ArrayList<ASLGroupModel>();
		try {
			LiferayGroupManager liferayGroupManager = new LiferayGroupManager();
			List<GroupModel> groupsArray = liferayGroupManager.listGroups();
			for(GroupModel group : groupsArray){
				ASLGroupModel gm = new ASLGroupModel();
				gm.setGroupId(new Long(group.getGroupId()));
				gm.setGroupName(group.getGroupName());
				gm.setDescription(group.getDescription());
				groups.add(gm);
			}
		} catch (UserManagementSystemException e) {
			logger.error("Exception:", e);
		}
		return groups;
	}
	
	
	public boolean isVO(String groupId) {
		try {
			LiferayGroupManager liferayGroupManager = new LiferayGroupManager();
			return liferayGroupManager.isVO(groupId);
		} 
		catch (GroupRetrievalFault e) {
			logger.error("Exception:", e);
		} catch (UserManagementSystemException e) {
			logger.error("Exception:", e);
		}
		return false;
	}
	
	
	public boolean isRootVO(String groupId) {
		try {
			LiferayGroupManager liferayGroupManager = new LiferayGroupManager();
			return liferayGroupManager.isRootVO(groupId);
		} catch (GroupRetrievalFault e) {
			logger.error("Exception:", e);
		} catch (UserManagementSystemException e) {
			logger.error("Exception:", e);
		}
		return false;
	}
	
	
	public String getScope(String groupId) {
		try {
			LiferayGroupManager liferayGroupManager = new LiferayGroupManager();
			String scope = liferayGroupManager.getScope(groupId);
			return scope;
		} catch (GroupRetrievalFault e) {
			logger.error("Exception:", e);
		} catch (UserManagementSystemException e) {
			logger.error("Exception:", e);
		}
		return null;
	}
	
	
	public String getGroupParentId(String groupId) {
		try {
			LiferayGroupManager liferayGroupManager = new LiferayGroupManager();
			long parentId = liferayGroupManager.getGroupParentId(groupId);
			return Long.toString(parentId);
		} catch (GroupRetrievalFault e) {
			logger.error("Exception:", e);
		} catch (UserManagementSystemException e) {
			logger.error("Exception:", e);
		}
		return null;
	}
	
	
}
