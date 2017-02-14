package it.eng.rdlab.um.ldap.validators;

import it.eng.rdlab.um.ldap.Utils;
import it.eng.rdlab.um.ldap.user.bean.LdapUserModel;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LdapUserValidator
{


	public static boolean validate(String dn, List<String> objectClassesList, Map<String, String> attributeMap) 
	{
		Log log = LogFactory.getLog(LdapUserValidator.class);
		log.debug("Checking object classes list...");
		List<String> dnKeys = null;
		
		log.debug("Utils.parseDN(dn) -->"+Utils.parseDN(dn).size());
		if (dn==null || (dnKeys = Utils.parseDN(dn))==null)
		{
			log.error("Distinguished name not valid");
			return false;
		}
		else if (objectClassesList ==null || objectClassesList.size() == 0 || !objectClassesList.contains(LdapUserModel.OBJECT_CLASS_PERSON) || !objectClassesList.contains(LdapUserModel.OBJECT_CLASS_INETORGPERSON))
		{
			log.debug("Invalid object class list");
			return false;
		}
			
		else
		{
			log.debug("Checking user mandatory parameters dn, cn and sn");
			log.debug("DN = "+dn);
			boolean cn = attributeMap.get(LdapUserModel.COMMON_NAME) != null || dnKeys.contains(LdapUserModel.COMMON_NAME);
			boolean sn = attributeMap.get(LdapUserModel.SURNAME) != null || dnKeys.contains(LdapUserModel.SURNAME);
			log.debug("Common name = "+cn);
			log.debug("Surname = "+sn);
			return (cn && sn);
		
		}
	}
	



}
