package org.gcube.spatial.data.geonetwork.extension;

import it.geosolutions.geonetwork.exception.GNLibException;
import it.geosolutions.geonetwork.exception.GNServerException;
import it.geosolutions.geonetwork.util.HTTPUtils;

import java.util.Collection;
import java.util.List;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.log4j.Logger;
import org.gcube.spatial.data.geonetwork.model.User;
import org.gcube.spatial.data.geonetwork.model.User.Profile;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class GNMetadataAdminExtension {

	private final static Logger LOGGER = Logger.getLogger(GNMetadataAdminExtension.class);

	private final static XMLOutputter outputter = new XMLOutputter(Format.getCompactFormat());

	private final static String CREATE_GROUP_METHOD="/srv/en/group.update";
	private final static String GROUP_LIST_METHOD="/srv/en/xml.group.list";
	private final static String USER_LIST_METHOD="/srv/en/xml.user.list";
	private final static String CREATE_USER_METHOD="/srv/en/user.update";
	private final static String GET_GROUPS_BY_USER="/srv/en/xml.usergroups.list";
	private final static String METADATA_SELECT="/srv/en/metadata.select";
	private final static String ASSIGN_MASSIVE_OWNERSHIP="/srv/en/metadata.massive.newowner";
	private final static String AVAILABLE_OWNERSHIP="/srv/en/xml.ownership.groups";
	private final static String METADATA_OWNERS="/srv/en/xml.ownership.editors";
	private final static String TRANSFER_OWNSERSHIP="/srv/en/xml.ownership.transfer";
	
	
	
	public static String allowedOwnershipTransfer(HTTPUtils connection, String gnServiceUrl, Integer userId) throws GNServerException{
		if(LOGGER.isDebugEnabled())
			LOGGER.debug("Getting available ownership transfer for user "+userId);
		Element request=new Element("request");
		request.addContent(new Element("id").setText(userId+""));
		return gnCall(connection,gnServiceUrl,request,AVAILABLE_OWNERSHIP);
	}
	
	public static String metadataOwners(HTTPUtils connection, String gnServiceUrl) throws GNServerException{
		if(LOGGER.isDebugEnabled())
			LOGGER.debug("Getting metadata owners");
		Element request=new Element("request");		
		return gnCall(connection,gnServiceUrl,request,METADATA_OWNERS);
	}
	
	public static String selectMeta (HTTPUtils connection, String gnServiceUrl, List<Long> toSelectIds) throws GNServerException{
		if(LOGGER.isDebugEnabled())
			LOGGER.debug("Massive metadata selection..");
		Element request=buildSelectMetadata(toSelectIds);
		return gnCall(connection,gnServiceUrl,request,METADATA_SELECT);
	}
	
	public static String clearMetaSelection(HTTPUtils connection, String gnServiceUrl) throws GNServerException{
		if(LOGGER.isDebugEnabled())
			LOGGER.debug("Massive metadata selection..");
		Element request=buildClearMetaSelection();
		return gnCall(connection,gnServiceUrl,request,METADATA_SELECT);
	}
	
	public static String assignMassiveOwnership(HTTPUtils connection, String gnServiceUrl,Integer userId, Integer groupId) throws GNServerException{
		if(LOGGER.isDebugEnabled())
			LOGGER.debug("Assign massive ownership..");
		Element request=new Element("request");
		request.addContent(new Element("user").setText(userId+""));
		request.addContent(new Element("group").setText(groupId+""));
		return gnCall(connection,gnServiceUrl,request,ASSIGN_MASSIVE_OWNERSHIP);
	}
	
	
	public static String transferOwnership(HTTPUtils connection, String gnServiceUrl,Integer sourceUserId, Integer sourceGroupId,Integer destUserId, Integer destGroupId) throws GNServerException{
		if(LOGGER.isDebugEnabled())
			LOGGER.debug("Transfering ownership..");
		Element request=new Element("request");
		request.addContent(new Element("sourceUser").setText(sourceUserId+""));
		request.addContent(new Element("sourceGroup").setText(sourceGroupId+""));
		request.addContent(new Element("targetUser").setText(destUserId+""));
		request.addContent(new Element("targetGroup").setText(destGroupId+""));
		return gnCall(connection,gnServiceUrl,request,TRANSFER_OWNSERSHIP);
	}
	
	public static String editUser(HTTPUtils connection,String gnServiceURL,User toAdd, Collection<Integer> groups)throws GNLibException, GNServerException {
		if(LOGGER.isDebugEnabled())
			LOGGER.debug("Adding user to a group..");
		Element request=buildUpdateUserRequest(toAdd.getId(), toAdd.getUsername(), toAdd.getPassword(), toAdd.getProfile(), groups);
		return gnCall(connection,gnServiceURL,request,CREATE_USER_METHOD);
	}

	public static String getUserGroupd(HTTPUtils connection,String gnServiceURL,Integer userId)throws GNLibException, GNServerException {
		if(LOGGER.isDebugEnabled())
			LOGGER.debug("Getting user groups..");
		return gnCall(connection,gnServiceURL,new Element("request").addContent(new Element("id").setText(userId+"")),GET_GROUPS_BY_USER);
	}
	

	public static String getUsers(HTTPUtils connection, String gnServiceURL) throws GNServerException{
		if(LOGGER.isDebugEnabled())
			LOGGER.debug("Requesting users..");
		return gnCall(connection,gnServiceURL,new Element("request"),USER_LIST_METHOD);
	}

	public static String createUser(HTTPUtils connection, String gnServiceURL, String name, String password, Profile profile, Collection<Integer> groups ) throws GNServerException, GNLibException{
		if(LOGGER.isDebugEnabled())
			LOGGER.debug("Requesting users..");
		Element userRequest=buildCreateUserRequest(name, password, profile, groups);
		return gnCall(connection,gnServiceURL,userRequest,CREATE_USER_METHOD);
	}


	public static String createGroup(HTTPUtils connection, String gnServiceURL, String groupName, String groupDescription, String groupMail) throws GNLibException, GNServerException {
		if(LOGGER.isDebugEnabled())
			LOGGER.debug(String.format("Creating group [Name : %s, Description : %s, Mail : %s ",groupName,groupDescription,groupMail));
		Element adminRequest = buildCreateGroupRequest(groupName, groupDescription, groupMail);
		return gnCall(connection, gnServiceURL, adminRequest,CREATE_GROUP_METHOD);
	}


	public static String getGroups(HTTPUtils connection,String gnServiceURL) throws GNServerException{
		if(LOGGER.isDebugEnabled())
			LOGGER.debug("Requesting groups..");
		return gnCall(connection, gnServiceURL, new Element("request"),GROUP_LIST_METHOD);
	}


	private static String gnCall(HTTPUtils connection,String baseURL, final Element gnRequest,String toInvokeMethod)throws GNServerException {

		String serviceURL = baseURL + toInvokeMethod;                
		String result=gnPut(connection, serviceURL, gnRequest);
		if(connection.getLastHttpStatus() != HttpStatus.SC_OK)
			throw new GNServerException("Error setting metadata privileges in GeoNetwork");
		return result;
	}


	/**
	 * 
	 * @see {@link http://geonetwork-opensource.org/latest/developers/xml_services/metadata_xml_services.html#update-operations-allowed-for-a-metadata-metadata-admin }
	 */
	private static Element buildCreateGroupRequest(String groupName,String groupDescription,String groupMail) throws GNLibException {
		if(LOGGER.isDebugEnabled()) 
			LOGGER.debug("Compiling admin request document");

		Element request = new Element("request");
		request.addContent(new Element("name").setText(groupName));
		request.addContent(new Element("description").setText(groupDescription));
		request.addContent(new Element("email").setText(groupMail));

		return request;
	}


	/**
	 * 
	 * @see {@link http://geonetwork-opensource.org/latest/developers/xml_services/metadata_xml_services.html#update-operations-allowed-for-a-metadata-metadata-admin }
	 */
	private static Element buildCreateUserRequest(String name, String password, Profile profile, Collection<Integer> groups) throws GNLibException {
		if(LOGGER.isDebugEnabled()) 
			LOGGER.debug("Compiling admin request document");

		Element request = new Element("request");
		request.addContent(new Element("operation").setText("newuser"));
		request.addContent(new Element("username").setText(name));
		request.addContent(new Element("password").setText(password));
		request.addContent(new Element("profile").setText(profile.name()));
		if(groups!=null){
			for(Integer groupId:groups)request.addContent(new Element("groups").setText(groupId+""));
		}
		return request;
	}


	/**
	 * 
	 * @see {@link http://geonetwork-opensource.org/latest/developers/xml_services/metadata_xml_services.html#update-operations-allowed-for-a-metadata-metadata-admin }
	 */
	private static Element buildUpdateUserRequest(Integer id, String name, String password, Profile profile, Collection<Integer> groups) throws GNLibException {
		if(LOGGER.isDebugEnabled()) 
			LOGGER.debug("Compiling admin request document");

		Element request = new Element("request");
		request.addContent(new Element("operation").setText("editinfo"));
		request.addContent(new Element("id").setText(id+""));
		request.addContent(new Element("username").setText(name));
		request.addContent(new Element("password").setText(password));
		request.addContent(new Element("profile").setText(profile.name()));
		if(groups!=null){
			for(Integer groupId:groups)request.addContent(new Element("groups").setText(groupId+""));
		}
		return request;
	}
	
	
	private static Element buildSelectMetadata(List<Long> toSelectIds){
		if(LOGGER.isDebugEnabled()) 
			LOGGER.debug("building selection request");
		Element request = new Element("request");
		if(toSelectIds!=null){
			for(Long id:toSelectIds) request.addContent(new Element("id").setText(id.toString()));
			request.addContent(new Element("selected").setText("add"));
		}else request.addContent(new Element("selected").setText("add-all"));		
		return request;
	}
	
	private static Element buildClearMetaSelection(){
		if(LOGGER.isDebugEnabled()) 
			LOGGER.debug("building selection request");
		Element request = new Element("request");
		request.addContent(new Element("selected").setText("remove-all"));		
		return request;
	}

	private static String gnPut(HTTPUtils connection, String serviceURL, final Element gnRequest) {


		String s = outputter.outputString(gnRequest);

		connection.setIgnoreResponseContentOnSuccess(false);
		String res = connection.postXml(serviceURL, s);
		//	        if(LOGGER.isInfoEnabled())
		//	            LOGGER.info(serviceURL + " returned --> " + res);
		return res;
	}
}
