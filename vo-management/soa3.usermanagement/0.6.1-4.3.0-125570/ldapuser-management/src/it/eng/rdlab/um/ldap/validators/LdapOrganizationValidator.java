package it.eng.rdlab.um.ldap.validators;

import it.eng.rdlab.um.ldap.Utils;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LdapOrganizationValidator 
{

	public static boolean validate(String dn, List<String> objectClassesList, Map<String, String> attributeMap) 
	{
		Log log = LogFactory.getLog(LdapOrganizationValidator.class);
		log.debug("Checking object classes list...");
		
		if (dn==null || Utils.parseDN(dn)==null)
		{
			log.debug("Distinguished name not valid");
			return false;
		}
		else return true;
	}

}
