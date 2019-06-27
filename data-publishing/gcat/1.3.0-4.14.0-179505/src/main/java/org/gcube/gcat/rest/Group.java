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

import org.gcube.gcat.ResourceInitializer;
import org.gcube.gcat.annotation.PATCH;
import org.gcube.gcat.annotation.PURGE;
import org.gcube.gcat.api.GCatConstants;
import org.gcube.gcat.persistence.ckan.CKANGroup;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@Path(Group.GROUPS)
public class Group extends REST<CKANGroup> implements org.gcube.gcat.api.interfaces.Group<Response,Response>{
	
	protected static final String GROUP_ID_PARAMETER = "GROUP_ID";
	
	public Group() {
		super(GROUPS, GROUP_ID_PARAMETER, CKANGroup.class);
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
	@Path("/{" + GROUP_ID_PARAMETER + "}")
	@Produces(ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8)
	@Override
	public String read(@PathParam(GROUP_ID_PARAMETER) String id) {
		return super.read(id);
	}
	
	@PUT
	@Path("/{" + GROUP_ID_PARAMETER + "}")
	@Consumes(ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8)
	@Produces(ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8)
	@Override
	public String update(@PathParam(GROUP_ID_PARAMETER) String id, String json) {
		return super.update(id, json);
	}
	
	@PATCH
	@Path("/{" + GROUP_ID_PARAMETER + "}")
	@Consumes(ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8)
	@Produces(ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8)
	@Override
	public String patch(@PathParam(GROUP_ID_PARAMETER) String id, String json) {
		return super.patch(id, json);
	}
	
	@DELETE
	@Path("/{" + GROUP_ID_PARAMETER + "}")
	@Override
	public Response delete(@PathParam(GROUP_ID_PARAMETER) String id,
			@QueryParam(GCatConstants.PURGE_QUERY_PARAMETER) @DefaultValue("false") Boolean purge) {
		return super.delete(id, purge);
	}
	
	@PURGE
	@Path("/{" + GROUP_ID_PARAMETER + "}")
	@Override
	public Response purge(@PathParam(GROUP_ID_PARAMETER) String id) {
		return delete(id, true);
	}

	@Override
	public Response delete(String name, boolean purge) {
		return delete(name, new Boolean(purge));
	}
	
}
