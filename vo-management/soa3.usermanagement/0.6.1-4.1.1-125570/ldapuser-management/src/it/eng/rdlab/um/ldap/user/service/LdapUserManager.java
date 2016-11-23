package it.eng.rdlab.um.ldap.user.service;

import it.eng.rdlab.um.exceptions.UserManagementSystemException;
import it.eng.rdlab.um.exceptions.UserRetrievalException;
import it.eng.rdlab.um.ldap.LdapBasicConstants;
import it.eng.rdlab.um.ldap.LdapDataModelWrapper;
import it.eng.rdlab.um.ldap.service.LdapManager;
import it.eng.rdlab.um.ldap.service.exceptions.LdapManagerException;
import it.eng.rdlab.um.ldap.user.bean.LdapUserModel;
import it.eng.rdlab.um.ldap.user.bean.LdapUserModelWrapper;
import it.eng.rdlab.um.user.beans.UserModel;
import it.eng.rdlab.um.user.service.UserManager;

import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LdapUserManager implements UserManager, LdapBasicConstants
{

	private Log log;
	private LdapManager ldapManager;
	private String baseDn;
	
	public LdapUserManager (String baseDn) throws NamingException
	{
		this.log = LogFactory.getLog(this.getClass());
		this.ldapManager = LdapManager.getInstance();
		this.baseDn = baseDn != null ? baseDn : "";
	}
	
	@Override
	public boolean createUser(UserModel usermodel) throws UserManagementSystemException 
	{
		this.log.debug("Creating user with dn "+usermodel.getFullname());
		LdapDataModelWrapper dataModelWrapper = new LdapUserModelWrapper(usermodel);
		
		try 
		{
			return this.ldapManager.createDataElement(dataModelWrapper);
		} 
		catch (LdapManagerException e) 
		{
			throw new UserManagementSystemException("unable to create the user",e);
		}
	
	}
	
	@Override
	public UserModel getUser(String userId) throws UserManagementSystemException, UserRetrievalException 
	{
		this.log.debug("Getting user with dn "+userId);
		
		try
		{
			Attributes attributes = this.ldapManager.getData(userId);
			
			if (attributes == null)
			{
				this.log.debug("No results found");
				return null;
			}
			else
			{
				return LdapUserModelGenerator.generate(userId, attributes, true);
			}
		} 
			catch (LdapManagerException e)
		{
			this.log.error("Unable to contact the Ldap server",e);
			throw new UserRetrievalException("Unable to contact the Ldap server",e);
		} 
		catch (NamingException e)
		{
				this.log.error("Generic LDAP error",e);
				throw new UserManagementSystemException("Unable to contact the Ldap server",e);
		}

	}
	

	
	@Override
	public boolean deleteUser(String userId) throws UserManagementSystemException, UserRetrievalException 
	{
		this.log.debug("Deleting user with dn "+userId);
		try 
		{
			boolean response = this.ldapManager.deleteData(userId);
			this.log.debug("Operation completed with result ");
			return response;
		} 
		catch (LdapManagerException e) 
		{
			this.log.error("Unable to complete the operation",e);
			throw new UserRetrievalException("Unable to complete the operation",e);
		}
	}

	@Override
	public boolean updateUser(UserModel user) throws UserManagementSystemException, UserRetrievalException 
	{
		this.log.debug("Updating user");
		String userId = user.getFullname();
		UserModel oldModel = getUser(userId);
		
		if (oldModel == null)
		{
			this.log.error("No user found");
			return false;
		}
		else
		{
			this.log.debug("Performing update operation...");
			boolean response = false;
			try {
				response = this.ldapManager.updateData(new LdapUserModelWrapper(oldModel), new LdapUserModelWrapper(user), new LdapUserDataModelComparator());
				this.log.debug("Operation completed with response "+response);
				return true;
			} 
			catch (NamingException e) 
			{
				this.log.error("Operation not completed",e);
				throw new UserManagementSystemException("Operation not completed",e);
			}
			
		}
	
	}


	@Override
	public List<UserModel> listUsers() throws UserManagementSystemException, UserRetrievalException 
	{
		this.log.debug("Generic search");
		UserModel dummyFilter = new LdapUserModel();
		dummyFilter.setFullname(this.baseDn);
		return listUsers(dummyFilter);
	}

	@Override
	public List<UserModel> listUsers(UserModel filter) throws UserManagementSystemException, UserRetrievalException 
	{
		this.log.debug("Filtered search");
		if (filter.getFullname() == null || filter.getFullname().length() == 0) filter.setFullname(this.baseDn);
		
		try 
		{
		
			NamingEnumeration<SearchResult> answer = this.ldapManager.searchData(new LdapUserModelWrapper(filter));
			List<UserModel> response = new ArrayList<UserModel>();
				
			while (answer.hasMoreElements())
			{
				SearchResult a = answer.nextElement();
				Attributes attributes = a.getAttributes();
				UserModel um = LdapUserModelGenerator.generate(a.getNameInNamespace(), attributes, true);
				response.add(um);
			}
			
			 
			return response;

		} catch (LdapManagerException e)
		{
			this.log.error("Unable to complete the search ",e);
			throw new UserRetrievalException("Unable to complete the search ",e);
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
