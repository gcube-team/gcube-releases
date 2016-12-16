package it.eng.rdlab.um.ldap.user.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import it.eng.rdlab.um.ldap.LdapDataModelWrapper;
import it.eng.rdlab.um.ldap.service.LdapGenericDataModelComparator;
import it.eng.rdlab.um.ldap.user.bean.LdapUserModel;

public class LdapUserDataModelComparator extends LdapGenericDataModelComparator 
{
	
	private Log log = null;

	public LdapUserDataModelComparator() 
	{
		this.log = LogFactory.getLog(LdapUserDataModelComparator.class);
	}

	@Override
	public List<ModificationItem> compare(LdapDataModelWrapper dataModel1,LdapDataModelWrapper dataModel2) 
	{
		List<ModificationItem> response = new ArrayList<ModificationItem>();
		log.debug("Generating single value attributes maps...");
		Map<String, String> singleValueAttributes1 = dataModel1.getAttributeMap();
		Map<String, String> singleValueAttributes2 = dataModel2.getAttributeMap();
		log.debug("Maps generated");
		log.debug("Loading password Parameters");
		String password1 = singleValueAttributes1.remove(LdapUserModel.PASSWORD);
		String password2 = singleValueAttributes2.remove(LdapUserModel.PASSWORD);
		log.debug("Password Parameters loaded");
		comparePasswordValue(response, password1, password2);
		compareSingleValueAttributes (response,singleValueAttributes1,singleValueAttributes2);
		log.debug("Generating multi value attributes maps...");
		Map<String, List<String>> multiValueAttributes1 = dataModel1.getListAttributeMap();
		Map<String, List<String>> multiValueAttributes2 = dataModel2.getListAttributeMap();
		log.debug("Maps generated");
		compareMultiValueAttributes (response,multiValueAttributes1,multiValueAttributes2);
		log.debug("Restoring password values");
		if (password1 != null) singleValueAttributes1.put(LdapUserModel.PASSWORD,password1);
		if (password2 != null) singleValueAttributes2.put(LdapUserModel.PASSWORD,password1);
		log.debug("Password values restored");
		return response;
	}
	
	public void comparePasswordValue (List<ModificationItem> response, String password1, String password2)
	{
		log.debug("Comparing password values");
		boolean password2Set = true;
		
		if (password2 == null || password2.equals(LdapUserModel.ENCRYPTED_PASSWORD_LABEL)) password2Set = false;
		
		log.debug("New password set "+password2Set);
		
		if (password2Set)
		{
			boolean password1Set = true;
			
			if (password1 == null || password1.equals(LdapUserModel.ENCRYPTED_PASSWORD_LABEL)) password1Set = false;
			
			log.debug("Old password data present "+password1Set);
			
			if (!password1Set || !password2.equals(password1))
			{
				log.debug("User password changed");
				response.add(new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute(LdapUserModel.PASSWORD, password2)));
			}
			else
			{
				log.debug("Old password equal to new password, the password won't be modified");
			}
		}
		else
		{
			log.debug("No password modification requested");
		}
		
	}
	
	

}
