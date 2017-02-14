package org.gcube.vomanagement.usermanagement.ws.utils;

import java.util.ArrayList;
import java.util.List;

import org.gcube.vomanagement.usermanagement.model.RoleModel;

public class StringRoleListCoupleWrapper 
{
	private String string;
	private List<RoleModel> roleModels;
	
	public StringRoleListCoupleWrapper ()
	{
		this ("",new ArrayList<RoleModel>());
	}
	
	public StringRoleListCoupleWrapper(String string, List<RoleModel> roleModels) 
	{
		this.string = string;
		this.roleModels = roleModels;
	}

	public String getString() {
		return string;
	}

	public void setString(String string) {
		this.string = string;
	}

	public  List<RoleModel> getRoleModels() {
		return roleModels;
	}

	public void setRoleModels( List<RoleModel> roleModels) {
		this.roleModels = roleModels;
	}


	
	
	
}
