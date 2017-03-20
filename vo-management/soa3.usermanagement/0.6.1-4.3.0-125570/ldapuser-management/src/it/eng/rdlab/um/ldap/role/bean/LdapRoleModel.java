package it.eng.rdlab.um.ldap.role.bean;


import it.eng.rdlab.um.role.beans.RoleModel;
import it.eng.rdlab.um.ldap.LdapModelConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 
 * 	
 * @author Ermanno Travaglino
 *
 */
public class LdapRoleModel extends RoleModel implements LdapModelConstants
{
	public static final String OBJECT_CLASS_ROLE = "organizationalRole",
								ROLE_OCCUPANT = "roleOccupant";
	
	
	public static final String 	ROLE_CN = "cn",
								DESCRIPTION = "description";
					
								
	
	
	public LdapRoleModel ()
	{
		super ();
		ArrayList<String> objectClasses = new ArrayList<String>();
		objectClasses.add(OBJECT_CLASS_TOP);
		objectClasses.add(OBJECT_CLASS_ROLE);
		super.addObject(OBJECT_CLASSES, objectClasses);
		super.addObject(ROLE_OCCUPANT, new ArrayList<String>());
		this.setId("");
	}
	
	public LdapRoleModel (RoleModel roleModel)
	{
		super (roleModel);
	}

	
	public LdapRoleModel (String roleDN, String roleCN, String description, List<String> objectClasses)
	{
		super (roleDN,roleCN,description);
		
		if (objectClasses == null)
		{
			objectClasses = new ArrayList<String>();
			objectClasses.add(OBJECT_CLASS_TOP);
			objectClasses.add(OBJECT_CLASS_ROLE);
			super.addObject(OBJECT_CLASSES, objectClasses);
		}
		else 
		{
			if (!objectClasses.contains(OBJECT_CLASS_TOP))
			{
				objectClasses.add(OBJECT_CLASS_TOP);
			}
			
			if (!objectClasses.contains(OBJECT_CLASS_ROLE))
			{
				this.addObjectClass(OBJECT_CLASS_ROLE);
			}
		}
		
		if (roleDN == null) this.setId("");
		
		if (roleCN == null) this.setRoleName("");

		super.addObject(ROLE_OCCUPANT, new ArrayList<String>());
	}
	
	public void setDN (String roleDN)
	{	
		super.setId(roleDN);
	}
	

	
	public String getDN ()
	{
		return this.getId();
	}

	
	@SuppressWarnings("unchecked")
	public List<String> getObjectClasses ()
	{
		return (List<String>) super.getObject(OBJECT_CLASSES);
	}
	
	@SuppressWarnings("unchecked")
	public void addObjectClass (String objectClassName)
	{
		((List<String>) super.getObject(OBJECT_CLASSES)).add(objectClassName);
	}
	
	@SuppressWarnings("unchecked")
	public void addRoleOccupantDN (String roleOccupantDN)
	{
		((List<String>)super.getObject(ROLE_OCCUPANT)).add(roleOccupantDN);
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getRoleOccupantDNS ()
	{
		return (List<String>) super.getObject(ROLE_OCCUPANT);
	}
	
	public void addExtraAttribute (String name, String value)
	{
		super.addObject(name, value);
	}
	
	
	
}
