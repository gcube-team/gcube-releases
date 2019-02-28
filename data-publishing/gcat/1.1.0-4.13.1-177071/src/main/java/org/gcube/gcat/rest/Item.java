package org.gcube.gcat.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.xml.ws.WebServiceException;

import org.gcube.gcat.ResourceInitializer;
import org.gcube.gcat.annotation.PURGE;
import org.gcube.gcat.api.GCatConstants;
import org.gcube.gcat.persistence.ckan.CKANPackage;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@Path(Item.ITEMS)
public class Item extends REST<CKANPackage> implements org.gcube.gcat.api.interfaces.Item<Response,Response>{
	
	public static final String ITEM_ID_PARAMETER = "ITEM_ID";
	
	public Item() {
		super(ITEMS, ITEM_ID_PARAMETER, CKANPackage.class);
	}
	
	@GET
	@Produces(ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8)
	@Override
	public String list(@QueryParam(GCatConstants.LIMIT_PARAMETER) @DefaultValue("10") int limit,
			@QueryParam(GCatConstants.OFFSET_PARAMETER) @DefaultValue("0") int offset) {
		return super.list(limit, offset);
	}
	
	@POST
	@Consumes(ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8)
	@Produces(ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8)
	@Override
	public Response create(String json) {
		return super.create(json);
	}
	
	@GET
	@Path("/{" + ITEM_ID_PARAMETER + "}")
	@Produces(ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8)
	@Override
	public String read(@PathParam(ITEM_ID_PARAMETER) String id) {
		return super.read(id);
	}
	
	@PUT
	@Path("/{" + ITEM_ID_PARAMETER + "}")
	@Consumes(ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8)
	@Produces(ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8)
	@Override
	public String update(@PathParam(ITEM_ID_PARAMETER) String id, String json) {
		return super.update(id, json);
	}
	
	/*
	@PATCH
	@Path("/{" + ITEM_ID_PARAMETER + "}")
	@Consumes(ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8)
	@Produces(ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8)
	@Override
	public String patch(@PathParam(ITEM_ID_PARAMETER) String id, String json) {
		return super.patch(id, json);
	}
	*/
	
	@DELETE
	@Path("/{" + ITEM_ID_PARAMETER + "}")
	@Override
	public Response delete(@PathParam(ITEM_ID_PARAMETER) String id,
			@QueryParam(GCatConstants.PURGE_QUERY_PARAMETER) @DefaultValue("false") Boolean purge) {
		return super.delete(id, purge);
	}
	
	@PURGE
	@Path("/{" + ITEM_ID_PARAMETER + "}")
	@Override
	public Response purge(@PathParam(ITEM_ID_PARAMETER) String id) {
		return super.purge(id);
	}

	@Override
	public Response delete(String name, boolean purge) throws WebServiceException {
		return delete(name, new Boolean(purge));
	}
	
}
