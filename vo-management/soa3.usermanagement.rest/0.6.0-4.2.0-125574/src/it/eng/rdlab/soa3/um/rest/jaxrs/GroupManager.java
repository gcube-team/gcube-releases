package it.eng.rdlab.soa3.um.rest.jaxrs;

import it.eng.rdlab.soa3.um.rest.bean.GroupModel;
import it.eng.rdlab.soa3.um.rest.conf.ConfigurationManager;
import it.eng.rdlab.soa3.um.rest.exceptions.UMJSONParserException;
import it.eng.rdlab.soa3.um.rest.impl.GroupManagerImpl;
import it.eng.rdlab.soa3.um.rest.jaxrs.bean.GroupJaxbBean;
import it.eng.rdlab.soa3.um.rest.jaxrs.bean.GroupsJaxbBean;
import it.eng.rdlab.soa3.um.rest.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import com.sun.jersey.api.core.HttpContext;


/**
 * 
 * This class is a RESTful WS that manages operations on groups
 * 
 * @author Ermanno Travaglino
 * @version 1.0
 * 
 */
@Path("/groupmanager")
public class GroupManager 
{
	private GroupManagerImpl groupManager;
	private ConfigurationManager cm;
	private  Log logger;

	public GroupManager() throws Exception 
	{
		logger  = LogFactory.getLog(this.getClass());
		cm = ConfigurationManager.getInstance();
		this.groupManager = new GroupManagerImpl(ConfigurationManager.getInstance().getLdapUrl());
	}

	/**
	  * Creates group by groupName (and optionally by organizationName)
	  *  
	  * @param groupBean GroupJaxbBean
	  * @return group creation process response
	  * @throws WebApplicationException
	  * 
	  */
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createGroup(GroupJaxbBean groupBean, @Context HttpContext context )
	{
		
		String groupName = groupBean.getGroupName();
		String organizationName = groupBean.getOrganizationName();
		String description = groupBean.getDescription();
		
		String groupNameMng = groupManager.createGroup(groupName, organizationName,description, cm.getLdapUserDN(), cm.getLdapPwd());


		if(groupNameMng == null)
		{
			logger.error("Group was not created" +  groupName);
			

			throw new WebApplicationException(
					Response.status(Status.BAD_REQUEST)
					.entity("Group  "
							+  groupName + " was not created")
							.build());
		}else
		{
			logger.debug("Group has been created successfully "  + groupName);
			
			return Response.status(Status.OK).entity("Group has been created successfully ").build();	
		}
	}

	/**
	  * Gets group by groupName and organizationName
	  *  
	  * @param groupName String 
	  * @param organizationName String (optional)
 	  * @return the group json object
	  * @throws WebApplicationException,JsonGenerationException,UMJSONParserException,IOException
	  * 
	  */
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/{groupName}/organization/{organizationName}")
	public Response getGroup(@PathParam("groupName")String groupName, @PathParam("organizationName")String organizationName, @Context HttpContext context )
	{
		logger.debug("Getting group " + groupName);
		GroupModel group = this.groupManager.getGroup(groupName, organizationName, cm.getLdapUserDN(), cm.getLdapPwd());
		
		if(group == null)
		{
			logger.error("Group "+  groupName +" doesn't exist");
			

			throw new WebApplicationException(
					Response.status(Status.NOT_FOUND)
					.entity("Group "+  groupName +" doesn't exist")
							.build());
		}else
		{
			logger.debug("Group has been created successfully "  + groupName);
			try {
				String value = Utils.getMapper().writeValueAsString(new GroupJaxbBean(group.getGroupName(),organizationName, group.getDescription()));
				
				return Response.status(Status.OK).entity(value).build();
			} catch (JsonGenerationException e) {
				
				logger.error("get group unsuccessful due to json parse error  ");
				throw new UMJSONParserException("Unable to generate JSON ", e);
			} catch (JsonMappingException e) {
				logger.error("get group unsuccessful due to json parse error  ");
				throw new UMJSONParserException("Unable to map JSON ", e);
			} catch (IOException e) {
				logger.error("get group unsuccessful due to json parse error  ");
				throw new UMJSONParserException(
						"IO Exception while parsing JSON ", e);
			}	
		}
		
	}
	
	
	/**
	  * Gets group by groupName
	  *  
	  * @param groupName String 
	  * @response.200.mediaType application/json
	  * @return the group json object
	  * @throws WebApplicationException,JsonGenerationException,UMJSONParserException,IOException
	  * 
	  */
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/{groupName}")
	public Response getGroup(@PathParam("groupName")String groupName, @Context HttpContext context )
	{
		return getGroup(groupName, null, context);
	}
	
	/**
	  * Lists groups by organizationName
	  *  
	  * @param organizationName String (optional) 
	  * @return the groups String object
	  * @throws WebApplicationException,JsonGenerationException,UMJSONParserException,IOException
	  * 
	  */
	@GET
	@Path("/groups/{organizationName}")
	@Produces(MediaType.APPLICATION_JSON)
	public String listGroups(@PathParam("organizationName")String organizationName, @Context HttpContext context )
	{
		
		List<GroupModel> groups = this.groupManager.listGroupsByOrganization(organizationName,cm.getLdapUserDN(), cm.getLdapPwd());


		Iterator<GroupModel> groupsIter = groups.iterator();

		List<String> groupNames= new ArrayList<String>();

		while(groupsIter.hasNext())
		{
			groupNames.add(groupsIter.next().getGroupName());
		}
		if(groupNames.isEmpty()){
			logger.error(" Organization " + organizationName + " does not contain any configured groups ");
			throw new WebApplicationException(Response.status(Status.NOT_FOUND)
					.entity(" Organization " + organizationName + " does not contain any configured groups ")
					.build());
		}
		
		try {
			String response = Utils.getMapper().writeValueAsString(new GroupsJaxbBean(groupNames));
			
			return response;
		} catch (JsonGenerationException e) {
			
			logger.error("get groups unsuccessful due to json parse error  ");
			throw new UMJSONParserException("Unable to generate JSON ", e);
		} catch (JsonMappingException e) {
			
			logger.error("get groups unsuccessful due to json parse error  ");
			throw new UMJSONParserException("Unable to map JSON ", e);
		} catch (IOException e) {
			
			logger.error("get groups unsuccessful due to json parse error  ");
			throw new UMJSONParserException(
					"IO Exception while parsing JSON ", e);
		}
		finally
		{
			

		}
	}
	
	/**
	  * Lists groups
	  *  
	  * @return the groups String object
	  * @throws WebApplicationException,JsonGenerationException,UMJSONParserException,IOException
	  * 
	  */
	@GET
	@Path("/groups")
	@Produces(MediaType.APPLICATION_JSON)
	public String listGroups(@Context HttpContext context )
	{
		return listGroups(null, context);
	}
	
	/**
	  * Deletes group by groupName (and optionally by organizationName)
	  *  
	  * @param groupName String
	  * @param organizationName String (optional) 
	  * @return group deletion process response
	  * 
	  */
	@DELETE
	@Path("/{groupName}/{organizationName:[^;/]*}")
	public Response deleteGroup(@PathParam("groupName")String groupName, @PathParam("organizationName")String organizationName,  @Context HttpContext context )
	{
		if(organizationName=="") organizationName = cm.getLdapBase();
		boolean isDeleted = this.groupManager.deleteGroup(groupName,organizationName,cm.getLdapUserDN(), cm.getLdapPwd());

		if(isDeleted)
		{

			logger.debug("Delete group successful: " + isDeleted);
			return Response.status(Status.OK)
					.entity("Group "+ groupName+  " deleted successfully ").build();
		}else
		{
			logger.error("Delete group unsuccessful ");

			return Response.status(Status.BAD_REQUEST)
					.entity("Group cannot be deleted  ").build();
		}
	}

	/**
	  * Deletes groups by organizationName
	  *  
	  * @param organizationName String 
	  * @response.200.mediaType application/json
	  * @return groups deletion process response
	  * 
	  */
	@DELETE
	@Path("/groups/{organizationName}")
	public Response deleteAllGroups(@PathParam("organizationName")String organizationName,  @Context HttpContext context )
	{

		boolean isDeleted = this.groupManager.deleteGroups(organizationName, cm.getLdapUserDN(), cm.getLdapPwd());


		if(isDeleted)
		{
			logger.debug("Delete groups successful: " + isDeleted);


			return Response.status(Status.OK)
					.entity("Groups deleted successfully ").build();
		}else{
			logger.error("Delete groups unsuccessful ");

			return Response.status(Status.BAD_REQUEST)
					.entity("Groups for organization " + organizationName  + " cannot be deleted  ").build();
		}
	}

	/**
	  * Deletes groups
	  *  
	  * @return groups deletion process response
	  * 
	  */
	@DELETE
	@Path("/groups")
	public Response deleteAllGroups(@Context HttpContext context )
	{
		return deleteAllGroups(null, context);
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateGroup(GroupJaxbBean groupBean, @Context HttpContext context )
	{
		logger.debug("Updating group "+groupBean.getGroupName()+ " for oganization "+groupBean.getOrganizationName());
		String organizationName = groupBean.getOrganizationName();
		GroupModel group = new GroupModel();
		group.setDescription(groupBean.getDescription());
		group.setGroupName(groupBean.getGroupName());
		
		if (this.groupManager.updateGroup(group, organizationName, cm.getLdapUserDN(), cm.getLdapPwd()))
		{
			return Response.status(Status.OK).entity("Group "+ groupBean.getGroupName() +" successfully updated for organization "+ organizationName).build();
		}
		else
		{
			return Response.status(Status.NOT_MODIFIED).entity("failed to update group "+groupBean.getGroupName()+" for organization "+ organizationName).build();
		}
	}
	
//	/**
//	  * Updates groups by groupName and organizationName
//	  *  
//	  * @param groupsBean GroupsJaxbBean
//	  * @param organizationName String 
//	  * @return group updating process response
//	  * 
//	  */
//	@POST
//	@Path("/groups/{organizationName}")
//	@Consumes(MediaType.APPLICATION_JSON)
//	public Response updateGroups(@PathParam("organizationName")String organizationName, GroupsJaxbBean groupsBean,  @Context HttpContext context )
//	{
//		
//		List<String> groups = groupsBean.getGroups();
//		
//		boolean isadded = this.groupManager.updateGroupsOfOrganization(groups, organizationName,cm.getLdapUserDN(), cm.getLdapPwd());
//
//		if(!isadded)
//		{
//			return Response.status(Status.NOT_MODIFIED).entity("failed to update groups for organization "+ organizationName).build();
//		}else
//		{
//
//			return Response.status(Status.OK).entity("Groups updated successfully for organization "+ organizationName).build();
//		}
//	}
//	
//	/**
//	  * Updates groups by groupName
//	  *  
//	  * @param groupsBean GroupsJaxbBean
//	  * @return group updating process response
//	  * 
//	  */
//	@POST
//	@Path("/groups")
//	@Consumes(MediaType.APPLICATION_JSON)
//	public Response updateGroups(GroupsJaxbBean groupsBean,  @Context HttpContext context )
//	{
//		return updateGroups(null, groupsBean, context);
//	}
//	
	

}
