package org.gcube.vomanagement.usermanagement.ws;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.gcube.vomanagement.usermanagement.exception.GroupRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.UserManagementPortalException;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.exception.UserRetrievalFault;
import org.gcube.vomanagement.usermanagement.impl.liferay.LiferayRoleManager;
import org.gcube.vomanagement.usermanagement.impl.liferay.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.RoleModel;
import org.gcube.vomanagement.usermanagement.model.UserModel;
import org.gcube.vomanagement.usermanagement.ws.utils.UserModelRoleListCoupleWrapper;

@WebService(name = "LiferaySOAPIdentityProvider", serviceName = "LiferaySOAPIdentityProvider")

public class LiferaySOAPIdentityProvider 
{
	LiferayRoleManager liferayRoleManager;
	LiferayUserManager liferayUserManager;
	
	public LiferaySOAPIdentityProvider() 
	{
		this.liferayRoleManager = new LiferayRoleManager();
		this.liferayUserManager = new LiferayUserManager();
	}
	
	
	@WebMethod
	public List<UserModelRoleListCoupleWrapper> listGroupsAndRolesByUserCustomField(String userCustomFieldName, String userCustomFieldValue) throws UserManagementPortalException, UserManagementSystemException, GroupRetrievalFault, UserRetrievalFault {
		
		List<UserModel> userModelList = this.liferayUserManager.getAllUsers();
		List<UserModelRoleListCoupleWrapper> wrapper = new ArrayList<UserModelRoleListCoupleWrapper>();
	
		for (UserModel userModel : userModelList)
		{
			Map<String, String> costomAttributes = userModel.getCustomAttrsMap();
			String value = costomAttributes.get(userCustomFieldName);
			
			if (value != null && value.equals(userCustomFieldValue))
			{
				List<RoleModel> rolesList = this.liferayRoleManager.listRolesByUser(userModel.getUserId());
				wrapper.add(new UserModelRoleListCoupleWrapper(userModel, rolesList));
			}
			
		}
		
		return wrapper;
	}

}
