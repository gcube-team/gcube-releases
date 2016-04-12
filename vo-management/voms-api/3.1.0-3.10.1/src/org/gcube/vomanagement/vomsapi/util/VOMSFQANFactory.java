package org.gcube.vomanagement.vomsapi.util;

public class VOMSFQANFactory 
{
	
	public static VOMSFQANInfo generateVOMSFQAN (String voms)
	{
		if(voms == null) throw new NullPointerException("VOMS role cannot be null.");
		if(voms.equals("")) throw new IllegalArgumentException("VOMS role cannot be an empty string.");
		
		String[] voName_groupRoleName = voms.split(":");
		if(voName_groupRoleName.length == 2)
		{
			String voName = voName_groupRoleName[0];
			String group_role = voName_groupRoleName[1];
			String[] groupName_RoleName = group_role.split("/Role=");
			if(groupName_RoleName.length != 2) throw new IllegalArgumentException("This is not a valid VOMS role structure.");
			if(!groupName_RoleName[0].startsWith("/")) throw new IllegalArgumentException("This is not a valid VOMS role structure.");
			
			return new VOMSRole(groupName_RoleName[0], voName, groupName_RoleName[1]);
			
		}
		else if (voName_groupRoleName.length == 1) return new VOMSInfo(voName_groupRoleName [0]);

		else return null;
	}

}
