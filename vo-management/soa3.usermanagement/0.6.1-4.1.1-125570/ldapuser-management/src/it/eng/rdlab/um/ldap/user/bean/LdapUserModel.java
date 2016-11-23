package it.eng.rdlab.um.ldap.user.bean;

import it.eng.rdlab.um.ldap.LdapModelConstants;
import it.eng.rdlab.um.user.beans.UserModel;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * An user is an Ldap leaf with the following features:
 * 
 * 	ObjectClass at least "person" (probably with the extra fields provided by "inetOrgPerson" class
 * 	Fields unique and in string format
 * 	
 * 	The fullName of an element is the DN and the id is the uuid element
 * 	The other parameters are:
 * 	-- cn (common name) - mandatory 
 * 	-- sn (surname) - mandatory
 * 	
 * @author Ciro Formisano
 *
 */
public class LdapUserModel extends UserModel implements LdapModelConstants
{
	
	public static final String 	OBJECT_CLASS_PERSON = "person",
								OBJECT_CLASS_INETORGPERSON = "inetOrgPerson",
								OBJECT_CLASS_ORGANIZATIONALPERSON = "organizationalPerson";
		
	public static final String 	COMMON_NAME = "cn",
								SURNAME = "sn",
								PASSWORD = "userPassword",
								UID = "uid",
								EMAIL = "mail",
								CERTIFICATE = "givenName";
	
	public static final String ENCRYPTED_PASSWORD_LABEL = "ENCRYPTED_PASSWORD";
	
	public LdapUserModel ()
	{
		super ();
		initObjects();
	}
	
	private void initObjects ()
	{
		ArrayList<String> objectClasses = new ArrayList<String>();
		objectClasses.add(OBJECT_CLASS_TOP);
		objectClasses.add(OBJECT_CLASS_PERSON);
		objectClasses.add(OBJECT_CLASS_INETORGPERSON);
		super.addObject(OBJECT_CLASSES, objectClasses);
		this.setId("");
	}
	
	public LdapUserModel (String uuid,String dn, List<String> objectClasses)
	{
		super (uuid,dn);
		
		if (objectClasses == null)
		{
			objectClasses = new ArrayList<String>();
			objectClasses.add(OBJECT_CLASS_TOP);
			objectClasses.add(OBJECT_CLASS_PERSON);
			objectClasses.add(OBJECT_CLASS_INETORGPERSON);
		}
		else
		{
			if (!objectClasses.contains(OBJECT_CLASS_TOP))
			{
				objectClasses.add(OBJECT_CLASS_TOP);
			}
			
			if (!objectClasses.contains(OBJECT_CLASS_PERSON))
			{
				objectClasses.add(OBJECT_CLASS_PERSON);
			}
			
			if (!objectClasses.contains(OBJECT_CLASS_INETORGPERSON))
			{
				objectClasses.add(OBJECT_CLASS_INETORGPERSON);
			}
		}
		
		super.addObject(OBJECT_CLASSES, objectClasses);
		
		if (dn == null) this.setId("");
		if (uuid == null) this.setFullname("");

	}
	
	public void setCN (String cn)
	{	
		super.addObject(COMMON_NAME, cn);
	}
	
	public void setSN (String sn)
	{
		super.addObject(SURNAME, sn);
	}
	

	
	public String getCN ()
	{	
		return super.getStringObject(COMMON_NAME);
	}
	
	public String getSN ()
	{
		return super.getStringObject(SURNAME);
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
	
	public void setEncryptedPasswordLabel ()
	{
		super.setPassword(ENCRYPTED_PASSWORD_LABEL.toCharArray());
	}
	
	public void addExtraAttribute (String name, String value)
	{
		super.addObject(name, value);
	}
	


}
