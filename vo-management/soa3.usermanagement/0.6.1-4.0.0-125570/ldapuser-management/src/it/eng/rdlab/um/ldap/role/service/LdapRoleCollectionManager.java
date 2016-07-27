package it.eng.rdlab.um.ldap.role.service;

import it.eng.rdlab.um.beans.GenericModel;
import it.eng.rdlab.um.exceptions.RoleRetrievalException;
import it.eng.rdlab.um.exceptions.UserManagementSystemException;
import it.eng.rdlab.um.role.beans.RoleModel;
import it.eng.rdlab.um.role.service.RoleManager;
import it.eng.rdlab.um.ldap.LdapAbstractModelWrapper;
import it.eng.rdlab.um.ldap.LdapBasicConstants;
import it.eng.rdlab.um.ldap.LdapModelGenerator;
import it.eng.rdlab.um.ldap.service.LdapManager;
import it.eng.rdlab.um.ldap.service.exceptions.LdapManagerException;

import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class LdapRoleCollectionManager implements RoleManager, LdapBasicConstants
{
	private Log log;
	protected String baseDn;
	protected LdapManager ldapManager;
	
	public LdapRoleCollectionManager (String baseDn) throws NamingException
	{
		this.log = LogFactory.getLog(this.getClass());
		this.ldapManager = LdapManager.getInstance();
		this.baseDn = baseDn != null ? baseDn : "";
	}

	@Override
	public boolean deleteRole(String roleId) throws UserManagementSystemException, RoleRetrievalException 
	{
		this.log.debug("Deleting role with dn "+roleId);
		try 
		{
			this.ldapManager.deleteData(roleId);
			this.log.debug("Operation completed with result ");
			return true;
		} 
		catch (LdapManagerException e) 
		{
			this.log.error("Unable to complete the operation",e);
			throw new RoleRetrievalException("Unable to complete the operation",e);
		}
	}

	
	protected GenericModel generateRole (String roleId,LdapModelGenerator generator) throws LdapManagerException, NamingException
	{
		this.log.debug("Getting role with dn "+roleId);
		Attributes attributes = this.ldapManager.getData(roleId);
		
		if (attributes == null)
		{
			this.log.debug("No results found");
			return null;
		}
		else
		{
			return generator.generate(roleId, attributes, true);
		}
	}
	

	protected List<RoleModel> internalListRoles(LdapAbstractModelWrapper wrapper, LdapModelGenerator generator) throws RoleRetrievalException 
	{
		try 
		{
			NamingEnumeration<SearchResult> answer = this.ldapManager.searchData(wrapper);
			List<RoleModel> response = new ArrayList<RoleModel>();
				
			while (answer.hasMoreElements())
			{
				SearchResult a = answer.nextElement();
				Attributes attributes = a.getAttributes();
				RoleModel gm = (RoleModel) generator.generate(a.getNameInNamespace(), attributes, true);
				response.add(gm);
			}
			
			 
			return response;

		} catch (LdapManagerException e)
		{
			this.log.error("Unable to complete the search ",e);
			throw new RoleRetrievalException("Unable to complete the search ",e);
		}
		catch (NamingException e)
		{
			this.log.error("Unable to generate the results ",e);
			throw new RoleRetrievalException("Unable to complete the search ",e);
		}
	}

	@Override
	public void close()
	{
		try 
		{
			this.ldapManager.close();
		} 
		catch (NamingException e) 
		{
			this.log.error("Unable to close the connection ",e);
		}
	}



}
