package org.gcube.data.access.storagehub.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;
import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.gxrest.response.outbound.GXOutboundErrorResponse;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.common.scope.impl.ScopeBean.Type;
import org.gcube.common.storagehub.model.Excludes;
import org.gcube.common.storagehub.model.Paths;
import org.gcube.common.storagehub.model.exceptions.BackendGenericError;
import org.gcube.common.storagehub.model.exceptions.InvalidItemException;
import org.gcube.common.storagehub.model.exceptions.StorageHubException;
import org.gcube.common.storagehub.model.expressions.Expression;
import org.gcube.common.storagehub.model.expressions.logical.And;
import org.gcube.common.storagehub.model.expressions.logical.ISDescendant;
import org.gcube.common.storagehub.model.items.Item;
import org.gcube.common.storagehub.model.items.TrashItem;
import org.gcube.common.storagehub.model.service.ItemList;
import org.gcube.common.storagehub.model.service.ItemWrapper;
import org.gcube.data.access.storagehub.AuthorizationChecker;
import org.gcube.data.access.storagehub.Constants;
import org.gcube.data.access.storagehub.Range;
import org.gcube.data.access.storagehub.Utils;
import org.gcube.data.access.storagehub.handlers.CredentialHandler;
import org.gcube.data.access.storagehub.handlers.Item2NodeConverter;
import org.gcube.data.access.storagehub.handlers.Node2ItemConverter;
import org.gcube.data.access.storagehub.handlers.TrashHandler;
import org.gcube.data.access.storagehub.handlers.VRE;
import org.gcube.data.access.storagehub.handlers.VREManager;
import org.gcube.data.access.storagehub.query.sql2.evaluators.Evaluators;
import org.gcube.smartgears.utils.InnerMethodName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;


@Path("")
public class WorkspaceManager {

	private static final Logger log = LoggerFactory.getLogger(WorkspaceManager.class);

	@Inject 
	RepositoryInitializer repository;

	@Inject
	Evaluators evaluator;

	@Inject
	AuthorizationChecker authChecker;

	
	@Inject
	ServletContext context;
	
	@Inject
	VREManager vreManager;
	
	@Inject
	TrashHandler trashHandler;
	
	@RequestScoped
	@QueryParam("exclude") 
	private List<String> excludes = Collections.emptyList();
	
	@Inject Node2ItemConverter node2Item;
	@Inject Item2NodeConverter item2Node;

	@Path("")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ItemWrapper<Item> getWorkspace(@QueryParam("relPath") String relPath){
		InnerMethodName.instance.set("getWorkspace");
		Session ses = null;
		org.gcube.common.storagehub.model.Path absolutePath;
		if (relPath==null)
			absolutePath = Utils.getWorkspacePath();
		else 	absolutePath = Paths.append(Utils.getWorkspacePath(), relPath);

		Item toReturn = null;
		try{
			long start = System.currentTimeMillis();
			ses = repository.getRepository().login(CredentialHandler.getAdminCredentials(context));
			
			//TODO: remove it when users will be created via storageHub
			String user = AuthorizationProvider.instance.get().getClient().getId();
			org.gcube.common.storagehub.model.Path trashPath = Paths.append(Utils.getWorkspacePath(), Constants.TRASH_ROOT_FOLDER_NAME);
			if (!ses.nodeExists(trashPath.toPath())) {
				Utils.createFolderInternally(ses, ses.getNode(Utils.getWorkspacePath().toPath()) , Constants.TRASH_ROOT_FOLDER_NAME, "trash of "+user, false, user, null);
			}
			
			log.trace("time to connect to repo  {}",(System.currentTimeMillis()-start));
			Node node = ses.getNode(absolutePath.toPath());
			authChecker.checkReadAuthorizationControl(ses, node.getIdentifier());
			toReturn = node2Item.getItem(node, excludes);
		}catch(RepositoryException re ){
			log.error("jcr error getting workspace item", re);
			GXOutboundErrorResponse.throwException(new BackendGenericError(re));
		}catch(StorageHubException she ){
			log.error("error getting workspace item", she);
			GXOutboundErrorResponse.throwException(she);		
		}finally{
			if (ses!=null)
				ses.logout();
		}

		return new ItemWrapper<Item>(toReturn);
	}

	private synchronized VRE getVreFolderItem(Session ses) throws RepositoryException, BackendGenericError{
		org.gcube.common.storagehub.model.Path vrePath = Paths.append(Utils.getWorkspacePath(), Constants.VRE_FOLDER_PARENT_NAME);
		ScopeBean bean = new ScopeBean(ScopeProvider.instance.get());
		if (!bean.is(Type.VRE)) throw new BackendGenericError("the current scope is not a VRE");
		String entireScopeName= bean.toString().replaceAll("^/(.*)/?$", "$1").replaceAll("/", "-");
		VRE vre = vreManager.getVRE(entireScopeName);
		if (vre!=null) return vre;
		else {
			String query = String.format("SELECT * FROM [nthl:workspaceItem] As node WHERE node.[jcr:title] like '%s' AND ISDESCENDANTNODE('%s')",entireScopeName, vrePath.toPath());
			Query jcrQuery = ses.getWorkspace().getQueryManager().createQuery(query, Constants.QUERY_LANGUAGE);
			NodeIterator it =  jcrQuery.execute().getNodes();

			if (!it.hasNext()) throw new BackendGenericError("vre folder not found for context "+entireScopeName);

			Node folder = it.nextNode();
			Item vreFolder = node2Item.getItem(folder, excludes);
			return vreManager.putVRE(vreFolder);
		}	

	}


	@Path("vrefolder")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ItemWrapper<Item> getVreRootFolder(){
		InnerMethodName.instance.set("getVreRootFolder");
		Session ses = null;
		Item vreItem = null;
		try {
			ses = repository.getRepository().login(CredentialHandler.getAdminCredentials(context));
			vreItem = getVreFolderItem(ses).getVreFolder();
		}catch(RepositoryException re ){
			log.error("jcr error getting vrefolder", re);
			GXOutboundErrorResponse.throwException(new BackendGenericError(re));
		}catch(StorageHubException she ){
			log.error("error getting vrefolder", she);
			GXOutboundErrorResponse.throwException(she);		
		}finally{
			if (ses!=null)
				ses.logout();
		}
		return new ItemWrapper<Item>(vreItem);
	}

	@Path("vrefolder/recents")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ItemList getVreFolderRecentsDocument(){
		InnerMethodName.instance.set("getVreFolderRecents");
		Session ses = null;
		List<Item> recentItems = Collections.emptyList();
		try{
			String login = AuthorizationProvider.instance.get().getClient().getId();
			ses = repository.getRepository().login(CredentialHandler.getAdminCredentials(context));
			
			VRE vre = getVreFolderItem(ses);
			log.trace("VRE retrieved {}",vre.getVreFolder().getTitle());
			recentItems = vre.getRecents();
			log.trace("recents retrieved {}",vre.getVreFolder().getTitle());
			return new ItemList(recentItems);
		}catch(RepositoryException re ){
			log.error("jcr error getting recents", re);
			GXOutboundErrorResponse.throwException(new BackendGenericError(re));
		}catch(StorageHubException she ){
			log.error("error getting recents", she);
			GXOutboundErrorResponse.throwException(she);		
		}finally{
			if (ses!=null)
				ses.logout();
		}

		return new ItemList(recentItems);

		
	}


	@Path("trash")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ItemWrapper<Item> getTrashRootFolder(){
		InnerMethodName.instance.set("getTrashRootFolder");
		Session ses = null;
		String user = AuthorizationProvider.instance.get().getClient().getId();
		org.gcube.common.storagehub.model.Path trashPath = Paths.append(Utils.getWorkspacePath(), Constants.TRASH_ROOT_FOLDER_NAME);
		Item item = null;
		try{
			long start = System.currentTimeMillis();
			ses = repository.getRepository().login(CredentialHandler.getAdminCredentials(context));
			log.info("time to connect to repo  {}",(System.currentTimeMillis()-start));
			
			Node folder = ses.getNode(trashPath.toPath());
			item = node2Item.getItem(folder, excludes);
		}catch(RepositoryException re ){
			log.error("jcr error getting trash", re);
			GXOutboundErrorResponse.throwException(new BackendGenericError(re));
		}catch(StorageHubException she ){
			log.error("error  getting trash", she);
			GXOutboundErrorResponse.throwException(she);		
		}finally{
			if (ses!=null)
				ses.logout();
		}
		
		return new ItemWrapper<Item>(item);

	}
	
	@Path("trash/empty")
	@DELETE
	public String emptyTrash(){
		InnerMethodName.instance.set("emptyTrash");
		Session ses = null;
		org.gcube.common.storagehub.model.Path trashPath = Paths.append(Utils.getWorkspacePath(), Constants.TRASH_ROOT_FOLDER_NAME);
		String toReturn = null;
		try{
			ses = repository.getRepository().login(CredentialHandler.getAdminCredentials(context));
			Node trashNode = ses.getNode(trashPath.toPath());
			List<Item> itemsToDelete = Utils.getItemList(trashNode, Excludes.ALL, null, true, null);
			trashHandler.removeNodes(ses, itemsToDelete);
			toReturn = trashNode.getIdentifier();
		}catch(RepositoryException re ){
			log.error("jcr error emptying trash", re);
			GXOutboundErrorResponse.throwException(new BackendGenericError(re));
		}catch(StorageHubException she ){
			log.error("error  emptying trash", she);
			GXOutboundErrorResponse.throwException(she);		
		}finally{
			if (ses!=null)
				ses.logout();
		}
		
		return toReturn;
	}
	
	@PUT
	@Consumes(MediaType.TEXT_PLAIN)
	@Path("trash/restore")
	public String restoreItem(String identifier){
		InnerMethodName.instance.set("restoreItem");
		Session ses = null;
		String toReturn = null;
		try{

			log.info("restoring node with id {}", identifier);
			
			//TODO check if it is possible to change all the ACL on a workspace
			ses = repository.getRepository().login(CredentialHandler.getAdminCredentials(context));

			authChecker.checkWriteAuthorizationControl(ses, identifier, false);

			final Node nodeToRestore = ses.getNodeByIdentifier(identifier);

			Item itemToRestore = node2Item.getItem(nodeToRestore, Excludes.ALL);
			
			if (!(itemToRestore instanceof TrashItem))
				throw new InvalidItemException("Only trash items can be restored");
						
			toReturn =  trashHandler.restoreItem(ses, (TrashItem)itemToRestore);

		}catch(RepositoryException re ){
			log.error("error restoring item with id {}",identifier, re);
			GXOutboundErrorResponse.throwException(new BackendGenericError(re));
		}catch(StorageHubException she ){
			log.error("error restoring item with id {}",identifier, she);
			GXOutboundErrorResponse.throwException(she);		
		} finally{
			if (ses!=null) {
				ses.logout();
			}
		}
		
		return toReturn;
	}
	
	
	@Path("vrefolders")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ItemList getVreFolders(){
		InnerMethodName.instance.set("getVreFolders");
		Session ses = null;

		org.gcube.common.storagehub.model.Path vrePath = Paths.append(Utils.getWorkspacePath(), Constants.VRE_FOLDER_PARENT_NAME);
		List<? extends Item> toReturn = null;
		try{
			log.info("vres folder path is {}",vrePath.toPath());
			ses = repository.getRepository().login(CredentialHandler.getAdminCredentials(context));
			toReturn = Utils.getItemList(ses.getNode(vrePath.toPath()) , excludes, null, false, null);
		}catch(RepositoryException re ){
			log.error("error reading the node children of {}",vrePath, re);
			GXOutboundErrorResponse.throwException(new BackendGenericError(re));
		}catch(StorageHubException she ){
			log.error("error reading the node children of {}",vrePath, she);
			GXOutboundErrorResponse.throwException(she);		
		}finally{
			if (ses!=null)
				ses.logout();
		}

		return new ItemList(toReturn);
	}

	@Path("vrefolders/paged")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ItemList getVreFoldersPaged(@QueryParam("start") Integer start, @QueryParam("limit") Integer limit){
		InnerMethodName.instance.set("getVreFoldersPaged");
		Session ses = null;
		org.gcube.common.storagehub.model.Path vrePath = Paths.append(Utils.getWorkspacePath(), Constants.VRE_FOLDER_PARENT_NAME);
		List<? extends Item> toReturn = null;
		try{
			ses = repository.getRepository().login(CredentialHandler.getAdminCredentials(context));
			toReturn = Utils.getItemList(ses.getNode(vrePath.toPath()) , excludes, new Range(start, limit), false, null);
		}catch(RepositoryException re ){
			log.error("(paged) error reading the node children of {}",vrePath, re);
			GXOutboundErrorResponse.throwException(new BackendGenericError(re));
		}catch(StorageHubException she ){
			log.error("(paged) error reading the node children of {}",vrePath, she);
			GXOutboundErrorResponse.throwException(she);		
		}finally{
			if (ses!=null)
				ses.logout();
		}

		return new ItemList(toReturn);
	}



	@Path("query")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ItemList searchItems(@QueryParam("n") String node, @QueryParam("e") String jsonExpr, @QueryParam("o") List<String> orderField, @QueryParam("l") Integer limit, @QueryParam("f") Integer offset){
		InnerMethodName.instance.set("searchItems");
		Session ses = null;
		List<? extends Item> toReturn = new ArrayList<>();

		try{

			ObjectMapper mapper = new ObjectMapper();
			Expression<Boolean> expression = mapper.readValue(jsonExpr, Expression.class);
			String stringExpression = evaluator.evaluate(new And(new ISDescendant(Utils.getWorkspacePath()), expression));

			String orderBy = "";
			if (orderField!=null && orderField.size()>0)
				orderBy= String.format("ORDER BY %s", orderField.stream().collect(Collectors.joining(",")).toString());

			String sql2Query = String.format("SELECT * FROM [%s] AS node WHERE %s %s ",node, stringExpression,orderBy);

			log.info("query sent is {}",sql2Query);


			
			ses = repository.getRepository().login(CredentialHandler.getAdminCredentials(context));
			Query jcrQuery = ses.getWorkspace().getQueryManager().createQuery(sql2Query, Constants.QUERY_LANGUAGE);

			if (limit!=null && limit!=-1 )
				jcrQuery.setLimit(limit);

			if (offset!=null && offset!=-1 )
				jcrQuery.setOffset(offset);

			QueryResult result = jcrQuery.execute();

			NodeIterator it = result.getNodes();

			while (it.hasNext())
				toReturn.add(node2Item.getItem(it.nextNode(), null));			
		}catch(RepositoryException | IOException re ){
			log.error("error executing the query", re);
			GXOutboundErrorResponse.throwException(new BackendGenericError(re));
		}catch(StorageHubException she ){
			log.error("error executing the query", she);
			GXOutboundErrorResponse.throwException(she);		
		}finally{
			if (ses!=null)
				ses.logout();
		}

		return new ItemList(toReturn);
	}

}
