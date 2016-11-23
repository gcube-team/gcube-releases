package it.eng.rdlab.um.ldap.data.beans;

import java.util.ArrayList;

import it.eng.rdlab.um.group.beans.GroupModel;
import it.eng.rdlab.um.ldap.LdapModelConstants;

public class LdapOrganizationalUnitDataModel extends GroupModel implements LdapModelConstants
{
	public static final String 	OBJECT_CLASS_OU = "organizationalUnit";

	public static final String 	OU = "ou";
	
	public LdapOrganizationalUnitDataModel() 
	{
		super ();
		ArrayList<String> objectClasses = new ArrayList<String>();
		objectClasses.add(OBJECT_CLASS_OU);
		super.addObject(OBJECT_CLASSES, objectClasses);
		this.setId("");
	}
	
	public LdapOrganizationalUnitDataModel(String dn, String ou) 
	{
		super ();
		ArrayList<String> objectClasses = new ArrayList<String>();
		objectClasses.add(OBJECT_CLASS_OU);
		super.addObject(OBJECT_CLASSES, objectClasses);
		this.setId(dn);
		this.setGroupName(ou);
	}
}
