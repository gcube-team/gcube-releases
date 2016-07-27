package it.eng.rdlab.um.ldap.user.service;

import it.eng.rdlab.um.ldap.LdapBasicConstants;
import it.eng.rdlab.um.ldap.role.bean.LdapRoleModel;
import it.eng.rdlab.um.ldap.service.exceptions.LdapManagerException;
import it.eng.rdlab.um.ldap.user.bean.LdapUserModel;
import it.eng.rdlab.um.ldap.user.bean.LdapUserModelWrapper;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LdapUserModelGenerator 
{
	
	
	@SuppressWarnings ("unchecked")
	public static LdapUserModel generate (String dn,Attributes attributes, boolean validate) throws NamingException, LdapManagerException
	{
		Log log = LogFactory.getLog(LdapUserModelGenerator.class);
		LdapUserModel response = new LdapUserModel();
		response.setFullname(dn);
		log.debug("Creating new user Model");
		NamingEnumeration<Attribute> resultAttributes = (NamingEnumeration<Attribute>) attributes.getAll();
		while (resultAttributes.hasMore())
		{
			Attribute attribute = resultAttributes.next();
			String attributeId = attribute.getID();
			log.debug("Attribute id = "+attributeId);
			NamingEnumeration<?> values = attribute.getAll();
			
			if (attributeId.equals(LdapBasicConstants.OBJECT_CLASS))
			{
				log.debug("Generating class object list");
				while (values.hasMore())
				{
					String value = (String) values.next();
					log.debug("Found value "+value);
					response.addObjectClass(value);
				}
				
			}
			else if (attributeId.equals(LdapUserModel.PASSWORD))
			{
				values.next();
				response.setEncryptedPasswordLabel();
			}
			else if (attributeId.equals(LdapUserModel.UID)) response.setUserId(values.next().toString());
			else 
			{
				log.debug("Updating attribute map...");
				
				if (values.hasMore())
				{
					Object value = values.next();
					log.debug("Found value "+value);
					if (value instanceof String) response.addExtraAttribute(attributeId,(String) value);
					else log.warn("Not string value found in "+attributeId);
				}

			}
			
		}

		if (validate)
		{
			log.debug("Validate result...");
			if (!new LdapUserModelWrapper(response).validateData()) throw new LdapManagerException("Received not valid data");
		}
		
		
		log.debug("Model generated");
		return response;

	}


}
