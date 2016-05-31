package it.eng.rdlab.soa3.um.rest.impl;

import it.eng.rdlab.soa3.um.rest.IUserManagementService.OrganizationManager;
import it.eng.rdlab.soa3.um.rest.bean.OrganizationModel;
import it.eng.rdlab.soa3.um.rest.conf.ConfigurationManager;
import it.eng.rdlab.soa3.um.rest.utils.Constants;
import it.eng.rdlab.soa3.um.rest.utils.Utils;
import it.eng.rdlab.um.exceptions.GroupRetrievalException;
import it.eng.rdlab.um.exceptions.UserManagementSystemException;
import it.eng.rdlab.um.group.beans.GroupModel;
import it.eng.rdlab.um.ldap.data.beans.LdapOrganizationalUnitDataModel;
import it.eng.rdlab.um.ldap.data.beans.LdapOrganizationalUnitDataModelWrapper;
import it.eng.rdlab.um.ldap.group.bean.LdapOrganizationModel;
import it.eng.rdlab.um.ldap.group.bean.LdapOrganizationModelWrapper;
import it.eng.rdlab.um.ldap.group.service.LdapOrganizationManager;
import it.eng.rdlab.um.ldap.service.LdapManager;
import it.eng.rdlab.um.ldap.service.exceptions.LdapManagerException;

import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import org.apache.log4j.Logger;

/**
 * This class is a layer between the RESTFul WS and the LDAPUserManagement for operations on organizations
 * 
 * @author Ermanno Travaglino
 * @version 1.0
 * 
 */
public class OrganizationManagerImpl implements OrganizationManager
{
	private Logger logger;
	private String ldapUrl = null;
	
	public OrganizationManagerImpl(String ldapUrl) 
	{
		this.ldapUrl = ldapUrl;
		logger = Logger.getLogger(this.getClass());
	}

	
	@Override
	public String createOrganization(String organizationName, String adminUserId, String adminPassword) 
	{
		logger.debug("Creating organization: "+organizationName);
		LdapOrganizationManager manager = null;
		String response = null;

		try 
		{
			Utils.initLdap(adminUserId, adminPassword,this.ldapUrl);
			manager = new LdapOrganizationManager(ConfigurationManager.getInstance().getLdapBase());

		} catch (NamingException e)
		{
			logger.error("Connection problem to LDAP", e);
			return null;
		}

		try 
		{
			LdapOrganizationModel organization = new LdapOrganizationModel();
			if(organizationName == null)
				organization.setOrganizationName(ConfigurationManager.getInstance().getLdapBase());
			else
				organization.setOrganizationName(organizationName);	

			String dn = Utils.organizationDNBuilder(organizationName);
			organization.setOrganizationDN(dn);
			boolean organizationCreation = true;
			logger.debug("Organization Name = "+organizationName);

			if(organizationName!=null){
				LdapOrganizationModel orgModel = new LdapOrganizationModel(Utils.organizationDNBuilder(organizationName),organizationName,null);
				LdapManager.getInstance().createDataElement(new LdapOrganizationModelWrapper(orgModel));
			}
			
			if (organizationCreation)
			{
				logger.debug("Organization "+organizationCreation+" created");
				LdapOrganizationalUnitDataModel groupOuDataModel = new LdapOrganizationalUnitDataModel(Utils.generateGroupsDN(organizationName),Constants.OU_GROUPS);
				LdapOrganizationalUnitDataModel peopleOuDataModel = new LdapOrganizationalUnitDataModel(Utils.generatePeopleDN(organizationName),Constants.OU_PEOPLE);
				LdapOrganizationalUnitDataModel roleOuDataModel = new LdapOrganizationalUnitDataModel(Utils.generateRolesDN(organizationName),Constants.OU_ROLES);
				boolean groupCreation = LdapManager.getInstance().createDataElement(new LdapOrganizationalUnitDataModelWrapper(groupOuDataModel));
				logger.debug("Group OU creation "+groupCreation);
				boolean peopleCreation = LdapManager.getInstance().createDataElement(new LdapOrganizationalUnitDataModelWrapper(peopleOuDataModel));
				logger.debug("People OU creation "+peopleCreation);
				boolean roleCreation = LdapManager.getInstance().createDataElement(new LdapOrganizationalUnitDataModelWrapper(roleOuDataModel));
				logger.debug("Role OU creation "+roleCreation);
				response =  groupCreation && peopleCreation && roleCreation ? organization.getOrganizationName() : null;
			}
			else 
			{
				logger.debug("Unable to create a new organization");
				response = null;
			}
		}
		catch (Exception e)
		{
			logger.debug("An error occourred during the operation",e);
		}

		manager.close();
		logger.debug("Operation completed with result "+response);
		return response;
	}

	@Override
	public boolean deleteOrganization(String organizationName, String adminUserId, String adminPassword) 
	{
		logger.debug("Deleting organization: "+organizationName);
		LdapOrganizationManager manager = null;

		boolean response = false;

		try 
		{

			if (!emptyOrganization (organizationName,adminUserId,adminPassword)) logger.warn("Some nodes are empty or it has been impossible to delete their content");
			Utils.initLdap(adminUserId, adminPassword,this.ldapUrl);
			boolean usersDeleted = LdapManager.getInstance().deleteData(Utils.generatePeopleDN(organizationName));
			logger.debug("Delete people node "+usersDeleted);
			boolean rolesDeleted = LdapManager.getInstance().deleteData(Utils.generateRolesDN(organizationName));
			logger.debug("Delete roles node "+rolesDeleted);
			boolean groupsDeleted = LdapManager.getInstance().deleteData(Utils.generateGroupsDN(organizationName));
			logger.debug("Delete groups node "+groupsDeleted);
			manager = new LdapOrganizationManager(Utils.organizationDNBuilder(organizationName));
			boolean isDeleted = usersDeleted&& rolesDeleted && groupsDeleted &&
															manager.deleteGroup(Utils.organizationDNBuilder(organizationName),false);
			
			if (isDeleted)
				logger.debug("Organization "+ organizationName + "has been deleted");
			else
				logger.debug("Organization "+ organizationName + "has not been deleted");
			
			manager.close();
			response = isDeleted;
		} 
		catch (NamingException e)
		{
			logger.error("Connection problem to LDAP", e);
			
		} catch (UserManagementSystemException e) {

			logger.error("Unable to delete organization contents",e);
		} catch (GroupRetrievalException e) 
		{
			logger.error("Unable to delete organization contents",e);
		} catch (LdapManagerException e)
		{
			logger.error("Unable to delete organization contents",e);
		}

		logger.debug("Operation completed with result "+response);
		return response;
	}

	private boolean emptyOrganization (String organizationName, String adminUserId, String adminPassword )
	{
		
		return new UserManagerImpl(ConfigurationManager.getInstance().getLdapUrl()).deleteUsers(organizationName, adminUserId, adminPassword) &
					new GroupManagerImpl(ConfigurationManager.getInstance().getLdapUrl()).deleteGroups(organizationName, adminUserId, adminPassword) &
					new RoleManagerImpl(ConfigurationManager.getInstance().getLdapUrl()).deleteRoles(organizationName, adminUserId, adminPassword);
	}
	
	@Override
	public int deleteOrganizations(String adminUserId, String adminPassword) 
	{
		logger.debug("Deleting all organizations");
		List<OrganizationModel> organizations = listOrganizations(adminUserId, adminPassword);
		int response = 0;
		boolean allDeleted = true;
		boolean atLeastOneDeleted = false;
		
		for (OrganizationModel organization : organizations)
		{
			String organizationName = organization.getOrganizationName();
			
			if (!emptyOrganization (organizationName,adminUserId,adminPassword)) logger.warn("Some nodes are empty or it has been impossible to delete their content");
		
			boolean singleResponse = deleteOrganization(organizationName, adminUserId, adminPassword);
			atLeastOneDeleted = atLeastOneDeleted | singleResponse;
			allDeleted = allDeleted & singleResponse;
		}
		
		if (allDeleted) response = 0;
		else if (atLeastOneDeleted) response = 1;
		else response = 2;
		
		logger.debug("Response "+response);
		return response;

	}


	@Override
	public OrganizationModel getOrganizationByName(String organizationName, String adminUserId, String adminPassword) 
	{
		logger.debug("Getting organization: "+organizationName);
		LdapOrganizationManager manager = null;
		OrganizationModel response = new OrganizationModel();

		try 
		{
			Utils.initLdap(adminUserId, adminPassword,this.ldapUrl);
			manager = new LdapOrganizationManager(ConfigurationManager.getInstance().getLdapBase());
		} catch (NamingException e)
		{
			logger.error("Connection problem to LDAP", e);
			return null;
		}


		try 
		{
			String dn = Utils.organizationDNBuilder(organizationName);
			GroupModel model = manager.getGroup(dn);

			response.setDescription(model.getDescription());
			response.setOrganizationId(model.getGroupId());
			response.setOrganizationName(model.getGroupName());

			return response;

		} catch (Exception e)
		{
			logger.debug("An error occourred during the operation",e);
			return null;
		}



	}



	@Override
	public List<OrganizationModel> listOrganizations(String adminUserId, String adminPassword) 
	{
		logger.debug("Listing all organizations");
		LdapOrganizationManager manager = null;
		List<OrganizationModel> response = null;

		try 
		{
			Utils.initLdap(adminUserId, adminPassword,this.ldapUrl);
			manager = new LdapOrganizationManager(ConfigurationManager.getInstance().getLdapBase());

		} catch (NamingException e)
		{
			logger.error("Connection problem to LDAP", e);
			return null;
		}


		try 
		{
			response = new ArrayList<OrganizationModel>();
			List<GroupModel> modelList = manager.listGroups();

			for (GroupModel currentOrganization : modelList)
			{
				String organizationDn = currentOrganization.getGroupId();
				logger.debug("Generating organization model for organization "+organizationDn);
				OrganizationModel organization = new OrganizationModel();
				
				organization.setParentOrganizationId(ConfigurationManager.getInstance().getLdapBase());
				organization.setDescription(organization.getDescription());
				organization.setOrganizationId(organizationDn);
				organization.setOrganizationName(organizationDn.substring(organizationDn.indexOf("=")+1, organizationDn.indexOf(",")).trim());
				logger.debug("Model generated");
				response.add(organization);
			}


		} catch (Exception e)
		{
			logger.debug("An error occourred during the operation",e);
		}

		manager.close();
		logger.debug("Operation completed with result "+response);
		return response;
	}


	@Override
	public boolean updateOrganization(OrganizationModel organization, String adminUserId, String adminPassword) 
	{
		logger.debug("Updating organization: "+organization.getOrganizationName());
		LdapOrganizationManager manager = null;
		boolean response = false;

		try 
		{
			Utils.initLdap(adminUserId, adminPassword,this.ldapUrl);
			manager = new LdapOrganizationManager(ConfigurationManager.getInstance().getLdapBase());

		} catch (NamingException e)
		{
			logger.error("Connection problem to LDAP", e);
			return false;
		}

		try 
		{
			LdapOrganizationModel organizationLdapModel = new LdapOrganizationModel();
			organizationLdapModel.setOrganizationDN(organization.getOrganizationId());
			organizationLdapModel.setDescription(organization.getDescription());
			response = manager.updateGroup(organizationLdapModel);
		}
		catch (Exception e)
		{
			logger.debug("An error occourred during the operation",e);
		}

		manager.close();
		logger.debug("Operation completed with result "+response);
		return response;

	}


	@Override
	public boolean existsOrganization(String organizationName, String adminUserId, String adminPassword) 
	{
		logger.debug("Checking if organization: "+organizationName+ " exists");
		boolean response = this.getOrganizationByName(organizationName, adminUserId, adminPassword) != null;
		logger.debug("Result "+response);
		return response;
	}




}
