package org.gcube.data.access.storagehub.services;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.jcr.Session;
import javax.jcr.security.AccessControlEntry;
import javax.jcr.security.Privilege;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.jackrabbit.api.security.JackrabbitAccessControlList;
import org.apache.jackrabbit.commons.jackrabbit.authorization.AccessControlUtils;
import org.gcube.common.storagehub.model.acls.ACL;
import org.gcube.common.storagehub.model.acls.AccessType;
import org.gcube.common.storagehub.model.types.ACLList;
import org.gcube.data.access.storagehub.AuthorizationChecker;
import org.gcube.data.access.storagehub.handlers.CredentialHandler;
import org.gcube.smartgears.utils.InnerMethodName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("items")
public class ACLManager {

	private static final Logger log = LoggerFactory.getLogger(ACLManager.class);

	@Inject 
	RepositoryInitializer repository;

	@RequestScoped
	@PathParam("id") 
	String id;

	@Context 
	ServletContext context;

	@Inject
	AuthorizationChecker authChecker;
	
	@Produces(MediaType.APPLICATION_JSON)
	@GET
	@Path("{id}/acls")
	public ACLList getACL() {
		InnerMethodName.instance.set("getACLById");
		Session ses = null;
		List<ACL> acls = new ArrayList<>();
		try{
			ses = repository.getRepository().login(CredentialHandler.getAdminCredentials(context));
			authChecker.checkReadAuthorizationControl(ses, id);
			String path = ses.getNodeByIdentifier(id).getPath();
			log.info("checking acces for path {}",path);
			JackrabbitAccessControlList accessControlList = AccessControlUtils.getAccessControlList(ses, path );
			for (AccessControlEntry aclEntry : accessControlList.getAccessControlEntries()) {
				ACL acl = new ACL();
				acl.setPricipal(aclEntry.getPrincipal().getName());
				List<AccessType> types = new ArrayList<>();
				for (Privilege priv : aclEntry.getPrivileges()) 
					try {
						types.add(AccessType.fromValue(priv.getName()));
					}catch (Exception e) {
						log.warn(priv.getName()+" cannot be mapped to AccessTypes",e);
					}
				acl.setAccessTypes(types);
				acls.add(acl);
			}
			return new ACLList(acls);	
		}catch (Exception e) {
			log.error("error gettign ACL",e);
			throw new WebApplicationException(e);
		}finally{
			if (ses!=null)
				ses.logout();
		}
		
		
	}

}
