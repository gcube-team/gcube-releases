package org.gcube.vomanagement.usermanagement.ws.utils;

import java.util.ArrayList;
import java.util.List;

import org.gcube.vomanagement.usermanagement.model.RoleModel;
import org.gcube.vomanagement.usermanagement.model.UserModel;

public class UserModelRoleListCoupleWrapper 
{
	private UserModel userModel;
	private List<RoleModel>  roleModels;
	
	public UserModelRoleListCoupleWrapper ()
	{
		this (new UserModel(),new ArrayList<RoleModel>());
	}
	
	public UserModelRoleListCoupleWrapper(UserModel userModel,  List<RoleModel> roleModels) 
	{
		this.userModel = userModel;
		this.roleModels = roleModels;
	}

	public UserModel getUserModel() {
		return userModel;
	}

	public void setUserModel(UserModel userModel) {
		this.userModel = userModel;
	}

	public List<RoleModel> getRoleModels() {
		return roleModels;
	}

	public void setRoleModels(List<RoleModel> roleModels) {
		this.roleModels = roleModels;
	}


	
	
	
}
