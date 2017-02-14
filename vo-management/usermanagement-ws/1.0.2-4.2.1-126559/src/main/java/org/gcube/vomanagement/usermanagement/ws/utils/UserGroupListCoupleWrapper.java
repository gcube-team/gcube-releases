package org.gcube.vomanagement.usermanagement.ws.utils;

import java.util.ArrayList;
import java.util.List;

import org.gcube.vomanagement.usermanagement.model.GroupModel;
import org.gcube.vomanagement.usermanagement.model.UserModel;

public class UserGroupListCoupleWrapper 
{
	private UserModel userModel;
	private List<GroupModel>  groupModels;
	
	public UserGroupListCoupleWrapper() {
		this (new UserModel(), new ArrayList<GroupModel>());
	}
	
	public UserGroupListCoupleWrapper(UserModel userModel, List<GroupModel> groupMopdels) 
	{
		this.userModel = userModel;
		this.groupModels = groupMopdels;
	}

	public UserModel getUserModel() {
		return userModel;
	}

	public void setUserModel(UserModel userModel) {
		this.userModel = userModel;
	}

	public  List<GroupModel> getGroupModels() {
		return groupModels;
	}

	public void setGroupModels(List<GroupModel> groupModels) {
		this.groupModels = groupModels;
	}
	
	
}
