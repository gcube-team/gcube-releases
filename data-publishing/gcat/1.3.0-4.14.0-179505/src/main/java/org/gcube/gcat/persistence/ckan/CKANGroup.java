package org.gcube.gcat.persistence.ckan;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.WebApplicationException;

import org.gcube.datacatalogue.ckanutillibrary.server.utils.CatalogueUtilMethods;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class CKANGroup extends CKAN {
	
	// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.get.group_list
	public static final String GROUP_LIST = CKAN.CKAN_API_PATH + "group_list";
	// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.create.group_create
	public static final String GROUP_CREATE = CKAN.CKAN_API_PATH + "group_create";
	// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.get.group_show
	public static final String GROUP_SHOW = CKAN.CKAN_API_PATH + "group_show";
	// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.update.group_update
	public static final String GROUP_UPDATE = CKAN.CKAN_API_PATH + "group_update";
	// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.patch.group_patch
	public static final String GROUP_PATCH = CKAN.CKAN_API_PATH + "group_patch";
	// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.delete.group_delete
	public static final String GROUP_DELETE = CKAN.CKAN_API_PATH + "group_delete";
	// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.delete.group_purge
	public static final String GROUP_PURGE = CKAN.CKAN_API_PATH + "group_purge";
	
	public CKANGroup() {
		super();
		LIST = GROUP_LIST;
		CREATE = GROUP_CREATE;
		READ = GROUP_SHOW;
		UPDATE = GROUP_UPDATE;
		PATCH = GROUP_PATCH;
		DELETE = GROUP_DELETE;
		PURGE = GROUP_PURGE;
	}
	
	public static String getGroupName(String name) {
		return CatalogueUtilMethods.fromGroupTitleToName(name);
		// return name.trim().toLowerCase().replaceAll(" ", "_");
	}
	
	public String create() throws WebApplicationException {
		try {
			ObjectNode objectNode = mapper.createObjectNode();
			objectNode.put(NAME_KEY, CKANGroup.getGroupName(name));
			objectNode.put("title", name);
			objectNode.put("display_name", name);
			objectNode.put("description", "");
			return super.create(mapper.writeValueAsString(objectNode));
		}catch (WebApplicationException e) {
			throw e;
		}catch (Exception e) {
			throw new InternalServerErrorException(e);
		}
	}
}
