package org.gcube.gcat.rest;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.gcube.datacatalogue.metadatadiscovery.bean.jaxb.NamespaceCategory;
import org.gcube.gcat.ResourceInitializer;
import org.gcube.gcat.profile.MetadataUtility;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@Path(Namespace.NAMESPACES)
public class Namespace extends BaseREST implements org.gcube.gcat.api.interfaces.Namespace {
	
	@GET
	@Produces(ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8)
	public String list() {
		setCalledMethod("GET /" + NAMESPACES);
		
		ObjectMapper mapper = new ObjectMapper();
		ArrayNode arrayNode = mapper.createArrayNode();
		
		try {
			List<NamespaceCategory> namespaces = MetadataUtility.getInstance().getNamespaceCategories();
			for(NamespaceCategory namespaceCategory : namespaces) {
				ObjectNode namespace = mapper.createObjectNode();
				namespace.put("id", namespaceCategory.getId());
				namespace.put("title", namespaceCategory.getTitle());
				namespace.put("name", namespaceCategory.getNamespaceCategoryQName());
				namespace.put("description", namespaceCategory.getDescription());
				arrayNode.add(namespace);
			}
			return mapper.writeValueAsString(arrayNode);
		} catch(Exception e) {
			throw new InternalServerErrorException(e.getMessage());
		}
	}
	
}
