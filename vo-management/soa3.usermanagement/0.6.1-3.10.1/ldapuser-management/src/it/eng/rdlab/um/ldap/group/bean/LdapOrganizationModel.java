package it.eng.rdlab.um.ldap.group.bean;

import it.eng.rdlab.um.group.beans.GroupModel;
import it.eng.rdlab.um.ldap.LdapModelConstants;

import java.util.ArrayList;
import java.util.List;

public class LdapOrganizationModel extends GroupModel implements LdapModelConstants
{
	public static final String OBJECT_CLASS_DOMAIN = "dcObject",
								OBJECT_CLASS_ORGANIZATION = "organization";
	
//	public static final String 	ORGANIZATION_DESCRIPTION = "description",
//								ORGANIZATION_ASSOCIATED_NAME = "associatedName";
	
	public static final String 	ORGANIZATION_NAME= "o";
	
	public LdapOrganizationModel() 
	{
		super ();
		ArrayList<String> objectClasses = new ArrayList<String>();
		objectClasses.add(OBJECT_CLASS_TOP);
		objectClasses.add(OBJECT_CLASS_DOMAIN);
		objectClasses.add(OBJECT_CLASS_ORGANIZATION);
		super.addObject(OBJECT_CLASSES, objectClasses);
		this.setId("");
	}
	
	
	public LdapOrganizationModel (String organizationDN, String organizationName, List<String> objectClasses)
	{
		super (organizationDN,"",organizationName,"");

		
		if (objectClasses == null)
		{
			objectClasses = new ArrayList<String>();
			objectClasses.add(OBJECT_CLASS_TOP);
			objectClasses.add(OBJECT_CLASS_DOMAIN);
			objectClasses.add(OBJECT_CLASS_ORGANIZATION);
			super.addObject(OBJECT_CLASSES, objectClasses);
		}
		
		else
		{
			if (!objectClasses.contains(OBJECT_CLASS_TOP)) objectClasses.add(OBJECT_CLASS_TOP);
			
			if (!objectClasses.contains(OBJECT_CLASS_ORGANIZATION)) objectClasses.add(OBJECT_CLASS_ORGANIZATION);

			if (!objectClasses.contains(OBJECT_CLASS_DOMAIN)) this.addObjectClass(OBJECT_CLASS_DOMAIN);
		}

		
		if (organizationDN == null) this.setId("");
		
		if (organizationName == null) this.setGroupName("");

	}

	public void setOrganizationDN(String organizationDN) 
	{
		super.setId(organizationDN);
	}
	
	public String getOrganizationDN() 
	{
		return super.getId();
	}
	
	
	public void setOrganizationName(String organizationName) 
	{
		super.setGroupName(organizationName);
	}
	
	public String getOrganizationName() 
	{
		return super.getGroupName();
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

	public void addExtraAttribute (String name, String value)
	{
		super.addObject(name, value);
	}
	
}
