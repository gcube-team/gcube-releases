package org.gcube.spatial.data.geonetwork.extension;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.List;

import org.apache.commons.httpclient.HttpStatus;
import org.gcube.spatial.data.geonetwork.extension.ServerAccess.Version;
import org.gcube.spatial.data.geonetwork.model.User;
import org.gcube.spatial.data.geonetwork.model.User.Profile;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.json.JSONArray;
import org.json.JSONObject;

import it.geosolutions.geonetwork.exception.GNLibException;
import it.geosolutions.geonetwork.exception.GNServerException;
import it.geosolutions.geonetwork.util.HTTPUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GNMetadataAdminExtension {

	private final static XMLOutputter outputter = new XMLOutputter(Format.getCompactFormat());

	private final static String USER_3="/srv/api/0.1/users";
	private final static String GROUPS_3="/srv/api/0.1/groups";


	private final static String CREATE_GROUP_METHOD_2="/srv/en/group.update";

	private final static String GROUP_LIST_METHOD="/srv/en/xml.group.list";
	private final static String USER_LIST_METHOD_2="/srv/en/xml.user.list";

	private final static String CREATE_USER_METHOD="/srv/en/user.update";
	private final static String GET_GROUPS_BY_USER="/srv/en/xml.usergroups.list";
	private final static String METADATA_SELECT="/srv/en/metadata.select";
	private final static String ASSIGN_MASSIVE_OWNERSHIP="/srv/en/metadata.massive.newowner";
	private final static String AVAILABLE_OWNERSHIP="/srv/en/xml.ownership.groups";
	private final static String METADATA_OWNERS="/srv/en/xml.ownership.editors";
	private final static String TRANSFER_OWNSERSHIP="/srv/en/xml.ownership.transfer";



	public static String allowedOwnershipTransfer(HTTPUtils connection, ServerAccess access, Integer userId) throws GNServerException, GNLibException{
		log.debug("Getting available ownership transfer for user "+userId);
		Element request=new Element("request");
		request.addContent(new Element("id").setText(userId+""));
		return gnCall(connection,access,request,AVAILABLE_OWNERSHIP);
	}

	public static String metadataOwners(HTTPUtils connection, ServerAccess access) throws GNServerException, GNLibException{
		log.debug("Getting metadata owners");
		Element request=new Element("request");		
		return gnCall(connection,access,request,METADATA_OWNERS);
	}

	public static String selectMeta (HTTPUtils connection, ServerAccess access, List<Long> toSelectIds) throws GNServerException, GNLibException{
		log.debug("Massive metadata selection..");
		Element request=buildSelectMetadata(toSelectIds);
		return gnCall(connection,access,request,METADATA_SELECT);
	}

	public static String clearMetaSelection(HTTPUtils connection, ServerAccess access) throws GNServerException, GNLibException{
		log.debug("Massive metadata selection..");
		Element request=buildClearMetaSelection();
		return gnCall(connection,access,request,METADATA_SELECT);
	}

	public static String assignMassiveOwnership(HTTPUtils connection, ServerAccess access,Integer userId, Integer groupId) throws GNServerException, GNLibException{
		log.debug("Assign massive ownership to u:{},g:{} ",userId,groupId);
		Element request=new Element("request");
		request.addContent(new Element("user").setText(userId+""));
		request.addContent(new Element("group").setText(groupId+""));
		return gnCall(connection,access,request,ASSIGN_MASSIVE_OWNERSHIP);
	}


	public static String transferOwnership(HTTPUtils connection, ServerAccess access,Integer sourceUserId, Integer sourceGroupId,Integer destUserId, Integer destGroupId) throws GNServerException, GNLibException{
		log.debug("Transfering ownership from u:{},g:{} to u:{},g:{}",sourceUserId,sourceGroupId,destUserId,destGroupId);
		Element request=new Element("request");
		request.addContent(new Element("sourceUser").setText(sourceUserId+""));
		request.addContent(new Element("sourceGroup").setText(sourceGroupId+""));
		request.addContent(new Element("targetUser").setText(destUserId+""));
		request.addContent(new Element("targetGroup").setText(destGroupId+""));
		return gnCall(connection,access,request,TRANSFER_OWNSERSHIP);
	}

	public static String editUser(HTTPUtils connection,ServerAccess access,User toAdd, Collection<Integer> groups)throws GNLibException, GNServerException {
		log.debug("Coupling user {} to groups {} ",toAdd,groups);

		Object request=null;
		String method=null;
		if(access.getVersion().equals(Version.DUE)){
			Element requestEl = new Element("request");
			requestEl.addContent(new Element("operation").setText("editinfo"));
			requestEl.addContent(new Element("id").setText(toAdd.getId()+""));
			requestEl.addContent(new Element("username").setText(toAdd.getUsername()));
			requestEl.addContent(new Element("password").setText(toAdd.getPassword()));
			requestEl.addContent(new Element("profile").setText(toAdd.getProfile().name()));
			if(groups!=null){
				for(Integer groupId:groups)requestEl.addContent(new Element("groups").setText(groupId+""));
			}
			request=requestEl;
			method=CREATE_USER_METHOD;
		}else{
			
			try{
				JSONObject object=new JSONObject();
				object.put("username", toAdd.getUsername());
				object.put("password", toAdd.getPassword());				
				object.put("profile",toAdd.getProfile().toString());
				object.put("enabled", true);
				if(groups!=null){
					JSONArray array=new JSONArray();
					for(Integer groupId:groups) array.put(groupId+"");
					object.put("groupsReviewer", array);
				}
				request= object;
				method=USER_3+"/"+toAdd.getId();	
			}catch(Exception e){
				throw new GNLibException("Unabel to create JSON request for group creation ", e);
			}
			//			request=buildUpdateUserRequest(toAdd.getId(), toAdd.getUsername(), toAdd.getPassword(), toAdd.getProfile(), groups);
		}

		return gnCall(connection,access,request,method);
	}

	public static String getUserGroupd(HTTPUtils connection,ServerAccess access,Integer userId)throws GNLibException, GNServerException {
		log.debug("Getting user groups..");
		return gnCall(connection,access,new Element("request").addContent(new Element("id").setText(userId+"")),GET_GROUPS_BY_USER);
	}


	public static String getUsers(HTTPUtils connection, ServerAccess access) throws GNServerException, GNLibException{
		log.debug("Requesting users..");


		if(access.getVersion().equals(Version.DUE)){
			return gnCall(connection,access,new Element("request"),USER_LIST_METHOD_2); 
		}else {
			String toReturn=gnCall(connection,access,null,USER_3);
			return toReturn;
		}
	}

	public static String createUser(HTTPUtils connection, ServerAccess access, String name, String password, Profile profile, Collection<Integer> groups ) throws GNServerException, GNLibException{

		log.debug("Requesting users..");
		log.debug("Compiling admin request document");

		Object userRequest=null;
		String method=null;

		if(access.getVersion().equals(Version.DUE)){
			Element request = new Element("request");
			request.addContent(new Element("operation").setText("newuser"));
			request.addContent(new Element("username").setText(name));
			request.addContent(new Element("password").setText(password));
			request.addContent(new Element("profile").setText(profile.name()));
			if(groups!=null){
				for(Integer groupId:groups)request.addContent(new Element("groups").setText(groupId+""));
			}
			userRequest=request;
			method=CREATE_USER_METHOD;
		}else{
			try{
				JSONObject object=new JSONObject();
				object.put("username", name);
				object.put("password", password);				
				object.put("profile",profile);
				object.put("enabled", true);
				if(groups!=null){
					JSONArray array=new JSONArray();
					for(Integer groupId:groups) array.put(groupId+"");
					object.put("groupsReviewer", array);
				}
				userRequest= object;
				method=USER_3;	
			}catch(Exception e){
				throw new GNLibException("Unabel to create JSON request for group creation ", e);
			}
		}



		return gnCall(connection,access,userRequest,method);
	}


	public static String createGroup(HTTPUtils connection, ServerAccess access, String groupName, String groupDescription, String groupMail, Integer groupId) throws GNLibException, GNServerException {
		log.debug(String.format("Creating group [Name : %s, Description : %s, Mail : %s ",groupName,groupDescription,groupMail));

		Object adminRequest=null;
		String method=null;
		if(access.getVersion().equals(Version.DUE)){
			Element request = new Element("request");
			request.addContent(new Element("name").setText(groupName));
			request.addContent(new Element("description").setText(groupDescription));
			request.addContent(new Element("email").setText(groupMail));

			adminRequest= request;
			method=CREATE_GROUP_METHOD_2;
		}	else {
			try{
				JSONObject object=new JSONObject();
				object.put("name", groupName);
				object.put("description", groupDescription);
				object.put("email", groupMail);
				object.put("id",groupId);
				adminRequest= object;
				method=GROUPS_3;	
			}catch(Exception e){
				throw new GNLibException("Unabel to create JSON request for group creation ", e);
			}
		}




		return gnCall(connection, access, adminRequest,method);
	}


	public static String getGroups(HTTPUtils connection,ServerAccess access) throws GNServerException, GNLibException{
		log.debug("Requesting groups..");
		Object request=null;
		String method=null;
		if(access.getVersion().equals(Version.DUE)){
			request=new Element("request");
			method=GROUP_LIST_METHOD;
		}else{
			method=GROUPS_3;
		}
		return gnCall(connection, access, request,method);
	}


	private static String gnCall(HTTPUtils connection,ServerAccess access, final Object gnRequest,String toInvokeMethod)throws GNServerException, GNLibException {

		String serviceURL = access.getGnServiceURL() + toInvokeMethod;                
		try{
			String result=gnRequest==null?gnGET(connection,serviceURL):gnPut(connection, serviceURL, gnRequest);
			int httpStatus=connection.getLastHttpStatus();
			if(httpStatus<200 ||httpStatus>=300)
				throw new GNServerException("Error executing call, received "+httpStatus+". Result is "+result);
			return result;
		}catch(MalformedURLException e){
			throw new GNServerException("Unable to send request ",e);
		}catch(UnsupportedEncodingException e){
			throw new GNServerException("Unable to send request ", e);
		}catch(GNLibException e){
			throw e;
		}
	}










	private static Element buildSelectMetadata(List<Long> toSelectIds){
		log.debug("building selection request");
		Element request = new Element("request");
		if(toSelectIds!=null){
			for(Long id:toSelectIds) request.addContent(new Element("id").setText(id.toString()));
			request.addContent(new Element("selected").setText("add"));
		}else request.addContent(new Element("selected").setText("add-all"));		
		return request;
	}

	private static Element buildClearMetaSelection(){
		log.debug("building selection request");
		Element request = new Element("request");
		request.addContent(new Element("selected").setText("remove-all"));		
		return request;
	}

	private static String gnPut(HTTPUtils connection, String serviceURL, final Object gnRequest) throws UnsupportedEncodingException, GNLibException, GNServerException {

		if(gnRequest instanceof Element){

			String s = outputter.outputString((Element)gnRequest);

			connection.setIgnoreResponseContentOnSuccess(false);
			String res = connection.postXml(serviceURL, s);

			return res;
		} else if (gnRequest instanceof JSONObject){
			String s=((JSONObject) gnRequest).toString();
			connection.setIgnoreResponseContentOnSuccess(false);
			return ((HttpUtilsExtensions)connection).putJSON(serviceURL, s);
		} else throw new GNLibException("Unable to manage request element "+gnRequest);
	}


	private static String gnGET(HTTPUtils connection, String serviceURL) throws MalformedURLException, GNServerException {

		connection.setIgnoreResponseContentOnSuccess(false);
		String res = ((HttpUtilsExtensions)connection).getJSON(serviceURL);

		return res;
	}
}
