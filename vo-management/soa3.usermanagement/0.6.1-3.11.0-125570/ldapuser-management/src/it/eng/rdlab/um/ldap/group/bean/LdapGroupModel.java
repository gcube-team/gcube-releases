package it.eng.rdlab.um.ldap.group.bean;

import it.eng.rdlab.um.group.beans.GroupModel;
import it.eng.rdlab.um.ldap.LdapModelConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * An group is an Ldap leaf with the following features:
 * 
 * 	ObjectClass at least "posixGroup" or "groupOfNames" or "groupOfUniqueNames" 
 * 	
 * 	The id of the group is the DN and the common name (CN) is mandatory
 *  Specific classes are implemented for posixGroup, groupOfNames and groupOfUniqueNames
 * 	
 * @author Ciro Formisano
 *
 */
public class LdapGroupModel extends GroupModel implements LdapModelConstants
{
	public static final String OBJECT_CLASS_GROUP = "groupOfNames",
								MEMBERS_DN = "member";
	
	
	public static final String 	GROUP_CN = "cn",
								DESCRIPTION = "description",
								ROLE = "businessCategory";
					
								
	
	
	public LdapGroupModel ()
	{
		super ();
		ArrayList<String> objectClasses = new ArrayList<String>();
		objectClasses.add(OBJECT_CLASS_TOP);
		objectClasses.add(OBJECT_CLASS_GROUP);
		super.addObject(OBJECT_CLASSES, objectClasses);
		super.addObject(MEMBERS_DN, new ArrayList<String>());
		this.setId("");
	}
	
	public LdapGroupModel (GroupModel groupModel)
	{
		super (groupModel);
	}

	
	public LdapGroupModel (String groupDN, String groupCN, String description, List<String> objectClasses)
	{
		super (groupDN,"",groupCN,description);
		
		if (objectClasses == null)
		{
			objectClasses = new ArrayList<String>();
			objectClasses.add(OBJECT_CLASS_TOP);
			objectClasses.add(OBJECT_CLASS_GROUP);
			super.addObject(OBJECT_CLASSES, objectClasses);
		}
		else 
		{
			if (!objectClasses.contains(OBJECT_CLASS_TOP))
			{
				objectClasses.add(OBJECT_CLASS_TOP);
			}
			
			if (!objectClasses.contains(OBJECT_CLASS_GROUP))
			{
				this.addObjectClass(OBJECT_CLASS_GROUP);
			}
		}
		
		if (groupDN == null) this.setId("");
		
		if (groupCN == null) this.setGroupName("");

		super.addObject(MEMBERS_DN, new ArrayList<String>());
	}
	
	public void setDN (String groupDN)
	{	
		super.setId(groupDN);
	}
		
	public String getDN ()
	{
		return this.getId();
	}
	
	public static String getRole() {
		return ROLE;
	}
	
	public void setRole (String role)
	{	
		super.addObject(ROLE, role);
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
	public void addMemberDN (String memberDn)
	{
		((List<String>)super.getObject(MEMBERS_DN)).add(memberDn);
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getMemberDNS ()
	{
		return (List<String>) super.getObject(MEMBERS_DN);
	}
	
	public void addExtraAttribute (String name, String value)
	{
		super.addObject(name, value);
	}
	
	
	
}
