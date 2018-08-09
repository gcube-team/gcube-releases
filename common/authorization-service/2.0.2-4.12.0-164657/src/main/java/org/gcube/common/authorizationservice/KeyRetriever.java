package org.gcube.common.authorizationservice;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;

import lombok.extern.slf4j.Slf4j;

import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.common.authorization.library.provider.ContainerInfo;
import org.gcube.common.authorizationservice.filters.AuthorizedCallFilter;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.common.scope.impl.ScopeBean.Type;

@Path("symmKey")
@Slf4j
public class KeyRetriever {

	@GET
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getKey(@Context HttpServletRequest req) throws Exception {
		final AuthorizationEntry info = (AuthorizationEntry)req.getAttribute(AuthorizedCallFilter.AUTH_ATTRIBUTE);
		if (info.getClientInfo() instanceof ContainerInfo){
			final String keyFileName =getKeyFileName(info.getContext());
			StreamingOutput so = new StreamingOutput() {
				public void write(OutputStream output) throws IOException, WebApplicationException {
					try{
						
						try(InputStream is =KeyRetriever.class.getResourceAsStream("/"+keyFileName)){
							byte[] buffer = new byte[8192]; 
							int read = -1;
							while ((read = is.read(buffer))!=-1){
								output.write(buffer, 0, read);
							}
						}
					}catch(Exception e){
						log.error("error retrieving symm key", e);
						throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
					}
				}
			};
			return Response.ok(so).header("content-disposition","attachment; filename = "+keyFileName)
					.header("resource-name", keyFileName).build();
		} else return Response.status(Status.UNAUTHORIZED).build();
	}

	protected static String getKeyFileName(String context) throws InvalidKeyException{
		String keyFile=null;
		if(context!=null){
			ScopeBean bean = new ScopeBean(context);
			if(bean.is(Type.VRE)) 
				bean = bean.enclosingScope(); 
			String name = bean.name();
			//build keyfile name with name
			keyFile=name+".gcubekey";
		}else{
			throw new InvalidKeyException("invalid key for context: "+context);
		}
		return keyFile;
	}

}
