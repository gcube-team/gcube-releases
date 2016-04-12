package it.eng.rdlab.um.ldap.group.service;

import it.eng.rdlab.um.ldap.LdapBasicConstants;
import it.eng.rdlab.um.ldap.LdapModelGenerator;
import it.eng.rdlab.um.ldap.group.bean.LdapOrganizationModel;
import it.eng.rdlab.um.ldap.group.bean.LdapOrganizationModelWrapper;
import it.eng.rdlab.um.ldap.service.exceptions.LdapManagerException;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LdapOrganizationModelGenerator implements LdapModelGenerator
{
	private Log log;
	
	public LdapOrganizationModelGenerator() 
	{
		log = LogFactory.getLog(this.getClass());
	}
	
	@SuppressWarnings ("unchecked")
	@Override
	public LdapOrganizationModel generate (String dn,Attributes attributes, boolean validate) throws NamingException, LdapManagerException
	{
		log.debug("Creating new organization Model");
		LdapOrganizationModel response = new LdapOrganizationModel();
		response.setOrganizationDN(dn);
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
			else if (attributeId.equals(LdapOrganizationModel.ORGANIZATION_NAME)) response.setOrganizationName(values.next().toString());
//			else if (attributeId.equals(LdapOrganizationModel.ORGANIZATION_DESCRIPTION)) response.setDescription(values.next().toString());
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
			if (!new LdapOrganizationModelWrapper(response).validateData()) throw new LdapManagerException("Received not valid data");
		}
		
		
		log.debug("Model generated");
		return response;

	}


}
