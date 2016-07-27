package it.eng.rdlab.um.ldap.group.service;

import it.eng.rdlab.um.ldap.LdapBasicConstants;
import it.eng.rdlab.um.ldap.LdapModelGenerator;
import it.eng.rdlab.um.ldap.group.bean.LdapGroupModel;
import it.eng.rdlab.um.ldap.group.bean.LdapGroupModelWrapper;
import it.eng.rdlab.um.ldap.service.exceptions.LdapManagerException;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LdapGroupModelGenerator  implements LdapModelGenerator
{
	private Log log;
	
	public LdapGroupModelGenerator() 
	{
		log = LogFactory.getLog(this.getClass());
	}
	
	@SuppressWarnings ("unchecked")
	@Override
	public LdapGroupModel generate (String dn,Attributes attributes, boolean validate) throws NamingException, LdapManagerException
	{

		log.debug("Creating new group Model");
		LdapGroupModel response = new LdapGroupModel();
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
			else if (attributeId.equals(LdapGroupModel.GROUP_CN)) response.setGroupName(values.next().toString());
			else if (attributeId.equals(LdapGroupModel.DESCRIPTION)) response.setDescription(values.next().toString());
			else if (attributeId.equals(LdapGroupModel.MEMBERS_DN))
			{
				
				while (values.hasMore())
				{
					String memberDn = values.next().toString();
					log.debug("Adding member "+memberDn);
					response.addMemberDN(memberDn);
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
			if (!new LdapGroupModelWrapper(response).validateData()) throw new LdapManagerException("Received not valid data");
		}
		
		
		log.debug("Model generated");
		return response;

	}


}
