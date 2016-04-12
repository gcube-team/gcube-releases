package it.eng.rdlab.um.ldap.role.service;

import it.eng.rdlab.um.ldap.LdapBasicConstants;
import it.eng.rdlab.um.ldap.LdapModelGenerator;
import it.eng.rdlab.um.ldap.role.bean.LdapRoleModel;
import it.eng.rdlab.um.ldap.role.bean.LdapRoleModelWrapper;
import it.eng.rdlab.um.ldap.service.exceptions.LdapManagerException;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LdapRoleModelGenerator  implements LdapModelGenerator
{
	private Log log;
	
	public LdapRoleModelGenerator() 
	{
		log = LogFactory.getLog(this.getClass());
	}
	
	@SuppressWarnings ("unchecked")
	@Override
	public LdapRoleModel generate (String dn,Attributes attributes, boolean validate) throws NamingException, LdapManagerException
	{

		log.debug("Creating new role Model");
		LdapRoleModel response = new LdapRoleModel();
		response.setDN(dn);
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
			else if (attributeId.equals(LdapRoleModel.ROLE_CN)) response.setRoleName(values.next().toString());
			else if (attributeId.equals(LdapRoleModel.DESCRIPTION)) response.setDescription(values.next().toString());
			else if (attributeId.equals(LdapRoleModel.ROLE_OCCUPANT))
			{
				
				while (values.hasMore())
				{
					String roleOccupantDn = values.next().toString();
					log.debug("Adding member "+roleOccupantDn);
					response.addRoleOccupantDN(roleOccupantDn);
				}
			}
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
			if (!new LdapRoleModelWrapper(response).validateData()) throw new LdapManagerException("Received not valid data");
		}
		
		
		log.debug("Model generated");
		return response;

	}


}
