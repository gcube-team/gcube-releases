package org.gcube.datacatalogue.catalogue.ws;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datacatalogue.catalogue.utils.CatalogueUtils;
import org.gcube.datacatalogue.catalogue.utils.Constants;
import org.gcube.datacatalogue.metadatadiscovery.bean.jaxb.NamespaceCategory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path(Constants.PROFILES)
/**
 * Items profiles service endpoint.
 * @author Costantino Perciante (ISTI - CNR)
 * @author Luca Frosini (ISTI - CNR)
 */
public class ItemProfile {
	
	private static final Logger logger = LoggerFactory.getLogger(ItemProfile.class);
	
	@SuppressWarnings("unchecked")
	@GET
	@Path(Constants.PROFILES_NAMES_SHOW)
	@Produces(MediaType.APPLICATION_JSON)
	public String showNames() {
		String context = ScopeProvider.instance.get();
		logger.debug("Incoming request for context " + context);
		// get the names as list
		JSONObject json = CatalogueUtils.createJSONObjectMin(true, null);
		try {
			List<String> names = CatalogueUtils.getProfilesNames();
			JSONArray array = new JSONArray();
			for(String elem : names) {
				try {
					array.add(elem);
				} catch(Exception e) {
				}
			}
			json.put(Constants.RESULT_KEY, array);
		} catch(Exception e) {
			json = CatalogueUtils.createJSONObjectMin(false, e.getMessage());
		}
		return json.toJSONString();
	}
	
	@GET
	@Path(Constants.PROFILE_SHOW)
	@Produces({MediaType.APPLICATION_XML, /*MediaType.APPLICATION_JSON*/})
	public String showSource(
			//@DefaultValue(MediaType.APPLICATION_XML) @HeaderParam("Accept") String accept, 
			@QueryParam("name") String profileName) throws Exception {
		String context = ScopeProvider.instance.get();
		logger.debug("Incoming request for context/name " + context + "/" + profileName);
		// TODO Check how this mapping xml-> json works
		/*if(accept.equals(MediaType.APPLICATION_JSON)){
			org.json.JSONObject xmlJSONObj = XML.toJSONObject(content);
			return xmlJSONObj.toString(PRETTY_PRINT_INDENT_FACTOR);
		
		}*/
		return CatalogueUtils.getProfileSource(profileName);
	}
	
	@GET
	@Path(Constants.NAMESPACES_SHOW)
	@Produces(MediaType.APPLICATION_JSON)
	@SuppressWarnings("unchecked")
	public String showNamespaces() throws Exception {
		// get the names as list
		JSONObject json = CatalogueUtils.createJSONObjectMin(true, null);
		JSONArray namespacesJson = new JSONArray();
		try {
			List<NamespaceCategory> namespaces = CatalogueUtils.getNamespaceCategories();
			for(NamespaceCategory namespaceCategory : namespaces) {
				JSONObject obj = new JSONObject();
				obj.put("id", namespaceCategory.getId());
				obj.put("title", namespaceCategory.getTitle());
				obj.put("name", namespaceCategory.getNamespaceCategoryQName());
				obj.put("description", namespaceCategory.getDescription());
				namespacesJson.add(obj);
			}
			json.put(Constants.RESULT_KEY, namespacesJson);
		} catch(Exception e) {
			json = CatalogueUtils.createJSONObjectMin(false, e.getMessage());
		}
		return json.toJSONString();
	}
	
}
