package org.gcube.gcat.persistence.ckan;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.WebApplicationException;

import org.gcube.gcat.utils.RandomString;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class CKANUser extends CKAN {
	
	/* User Paths */
	// see https://docs.ckan.org/en/latest/api/#ckan.logic.action.get.user_list
	public static final String USER_LIST = CKAN.CKAN_API_PATH + "user_list";
	// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.create.user_create
	public static final String USER_CREATE = CKAN.CKAN_API_PATH + "user_create";
	// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.get.user_show
	public static final String USER_SHOW = CKAN.CKAN_API_PATH + "user_show";
	// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.update.user_update
	public static final String USER_UPDATE = CKAN.CKAN_API_PATH + "user_update";
	// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.delete.user_delete
	public static final String USER_DELETE = CKAN.CKAN_API_PATH + "user_delete";
	
	public static final String ADD_USER_TO_GROUP = CKAN.CKAN_API_PATH + "member_create";
	
	public CKANUser() {
		super();
		LIST = USER_LIST; 
		CREATE = USER_CREATE;
		READ = USER_SHOW;
		UPDATE = USER_UPDATE;
		PATCH = null;
		DELETE = USER_DELETE;
		PURGE =  null;
	}
	
	public static final String NAME = "name";
	public static final String EMAIL = "email";
	public static final String PASSWORD = "password";
	
	public String create() {
		RandomString randomString = new RandomString(12);
		ObjectNode objectNode = mapper.createObjectNode();
		objectNode.put(NAME, name);
		objectNode.put(EMAIL, name+"@gcube.ckan.org");
		objectNode.put(PASSWORD, randomString.nextString());
		return create(getAsString(objectNode));
	}
	
	@Override
	public void delete(boolean purge) {
		this.delete();
	}
	
	
	public void addToGroup(String groupName) throws WebApplicationException {
		try {
			ObjectNode objectNode = mapper.createObjectNode();
			objectNode.put(ID_KEY, CKANGroup.getGroupName(groupName));
			objectNode.put("object", name);
			objectNode.put("object_type", "user");
			objectNode.put("capacity", "member");
			sendPostRequest(ADD_USER_TO_GROUP, getAsString(objectNode));
		}catch (WebApplicationException e) {
			throw e;
		}catch (Exception e) {
			throw new InternalServerErrorException(e);
		}
	}
}