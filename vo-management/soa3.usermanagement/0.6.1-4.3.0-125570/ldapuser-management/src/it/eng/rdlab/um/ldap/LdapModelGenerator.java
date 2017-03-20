package it.eng.rdlab.um.ldap;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import it.eng.rdlab.um.beans.GenericModel;
import it.eng.rdlab.um.ldap.service.exceptions.LdapManagerException;

public interface LdapModelGenerator 
{
	
	public GenericModel generate (String dn,Attributes attributes, boolean validate)  throws NamingException, LdapManagerException;

}
