package it.eng.rdlab.um.ldap.group.service;

import it.eng.rdlab.um.beans.GenericModel;
import it.eng.rdlab.um.exceptions.GroupRetrievalException;
import it.eng.rdlab.um.exceptions.UserManagementSystemException;
import it.eng.rdlab.um.group.beans.GroupModel;
import it.eng.rdlab.um.group.service.GroupManager;
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

public abstract class LdapCollectionManager implements GroupManager, LdapBasicConstants
{
	private Log log;
	protected String baseDn;
	protected LdapManager ldapManager;
	
	public LdapCollectionManager (String baseDn) throws NamingException
	{
		this.log = LogFactory.getLog(this.getClass());
		this.ldapManager = LdapManager.getInstance();
		this.baseDn = baseDn != null ? baseDn : "";
	}

	@Override
	public boolean deleteGroup(String groupId,boolean checkSubgroups) throws UserManagementSystemException, GroupRetrievalException 
	{
		this.log.debug("Deleting group with dn "+groupId);
		try 
		{
			boolean response = this.ldapManager.deleteData(groupId);
			this.log.debug("Operation completed with result ");
			return response;
		} 
		catch (LdapManagerException e) 
		{
			this.log.error("Unable to complete the operation",e);
			throw new GroupRetrievalException("Unable to complete the operation",e);
		}
	}

	
	protected GenericModel generateGroup (String groupId,LdapModelGenerator generator) throws LdapManagerException, NamingException
	{
		this.log.debug("Getting group with dn "+groupId);
		Attributes attributes = this.ldapManager.getData(groupId);
		
		if (attributes == null)
		{
			this.log.debug("No results found");
			return null;
		}
		else
		{
			return generator.generate(groupId, attributes, true);
		}
	}
	
	@Override
	public long getGroupParentId(String groupId) throws UserManagementSystemException, GroupRetrievalException 
	{
		return 0;
	}


	protected List<GroupModel> internalListGroups(LdapAbstractModelWrapper wrapper, LdapModelGenerator generator) throws UserManagementSystemException, GroupRetrievalException 
	{
		try 
		{
			NamingEnumeration<SearchResult> answer = this.ldapManager.searchData(wrapper);
			List<GroupModel> response = new ArrayList<GroupModel>();
				
			while (answer.hasMoreElements())
			{
				SearchResult a = answer.nextElement();
				Attributes attributes = a.getAttributes();
				GroupModel gm = (GroupModel) generator.generate(a.getNameInNamespace(), attributes, true);
				response.add(gm);
			}
			
			 
			return response;

		} catch (LdapManagerException e)
		{
			this.log.error("Unable to complete the search ",e);
			throw new GroupRetrievalException("Unable to complete the search ",e);
		}
		catch (NamingException e)
		{
			this.log.error("Unable to generate the results ",e);
			throw new UserManagementSystemException("Unable to complete the search ",e);
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
