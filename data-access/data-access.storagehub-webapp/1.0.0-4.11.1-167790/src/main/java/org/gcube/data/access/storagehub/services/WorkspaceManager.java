package org.gcube.data.access.storagehub.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.xml.ws.WebServiceException;

import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.authorization.library.provider.CalledMethodProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.common.scope.impl.ScopeBean.Type;
import org.gcube.common.storagehub.model.Paths;
import org.gcube.common.storagehub.model.expressions.Expression;
import org.gcube.common.storagehub.model.expressions.logical.And;
import org.gcube.common.storagehub.model.expressions.logical.ISDescendant;
import org.gcube.common.storagehub.model.items.Item;
import org.gcube.common.storagehub.model.service.ItemList;
import org.gcube.common.storagehub.model.service.ItemWrapper;
import org.gcube.data.access.storagehub.Constants;
import org.gcube.data.access.storagehub.Range;
import org.gcube.data.access.storagehub.Utils;
import org.gcube.data.access.storagehub.handlers.ItemHandler;
import org.gcube.data.access.storagehub.query.sql2.evaluators.Evaluators;
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
	
	@RequestScoped
	@QueryParam("exclude") 
	private List<String> excludes = Collections.emptyList();

	@Path("")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ItemWrapper<Item> getWorkspace(){
		CalledMethodProvider.instance.set("getWorkspace");
		Session ses = null;
		org.gcube.common.storagehub.model.Path absolutePath = Utils.getHomePath();
		Item toReturn = null;
		try{
			String login = AuthorizationProvider.instance.get().getClient().getId();
			long start = System.currentTimeMillis();
			ses = repository.getRepository().login(new SimpleCredentials(login,Utils.getSecurePassword(login).toCharArray()));
			log.info("time to connect to repo  {}",(System.currentTimeMillis()-start));
			toReturn = ItemHandler.getItem(ses.getNode(absolutePath.toPath()), excludes);
		}catch(Throwable e){
			log.error("error reading the node children of {}",absolutePath,e);
		}finally{
			if (ses!=null)
				ses.logout();
		}
		
		return new ItemWrapper<Item>(toReturn);
	}
	
	
	@Path("vrefolder")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ItemWrapper<Item> getVreRootFolder(){
		CalledMethodProvider.instance.set("getVreRootFolder");
		Session ses = null;
		
		org.gcube.common.storagehub.model.Path vrePath = Paths.append(Utils.getHomePath(), Constants.VRE_FOLDER_PARENT_NAME);
				
		try{
			String login = AuthorizationProvider.instance.get().getClient().getId();
			ScopeBean bean = new ScopeBean(ScopeProvider.instance.get());
			if (!bean.is(Type.VRE)) throw new Exception("the current scope is not a VRE");
			long start = System.currentTimeMillis();
			ses = repository.getRepository().login(new SimpleCredentials(login,Utils.getSecurePassword(login).toCharArray()));
			log.info("time to connect to repo  {}",(System.currentTimeMillis()-start));
			
			String entireScopename= bean.toString().replaceAll("^/(.*)/?$", "$1").replaceAll("/", "-");
			
			String query = String.format("SELECT * FROM [nthl:workspaceItem] As node WHERE node.[jcr:title] like '%s'", entireScopename);
			Query jcrQuery = ses.getWorkspace().getQueryManager().createQuery(query, Constants.QUERY_LANGUAGE);
			NodeIterator it =  jcrQuery.execute().getNodes();
			
			if (!it.hasNext()) throw new Exception("vre folder not found for context "+bean.toString());
			
			Node folder = it.nextNode();
			Item item = ItemHandler.getItem(folder, excludes);
			
			return new ItemWrapper<Item>(item);
		}catch(Throwable e){
			log.error("error reading node {}",vrePath,e);
			throw new WebApplicationException("error retrieving vre folder",e);
		}finally{
			if (ses!=null)
				ses.logout();
		}
		
	}
	
	@Path("trash")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ItemWrapper<Item> getTrashRootFolder(){
		CalledMethodProvider.instance.set("getTrashRootFolder");
		Session ses = null;
		
		org.gcube.common.storagehub.model.Path trashPath = Paths.append(Utils.getHomePath(), Constants.TRASH_ROOT_FOLDER_NAME);
				
		try{
			String login = AuthorizationProvider.instance.get().getClient().getId();
			long start = System.currentTimeMillis();
			ses = repository.getRepository().login(new SimpleCredentials(login,Utils.getSecurePassword(login).toCharArray()));
			log.info("time to connect to repo  {}",(System.currentTimeMillis()-start));
			
			
			
			Node folder = ses.getNode(trashPath.toPath());
			Item item = ItemHandler.getItem(folder, excludes);
			
			return new ItemWrapper<Item>(item);
		}catch(Throwable e){
			log.error("error reading the node {}",trashPath,e);
			throw new WebApplicationException("error retrieving trash folder",e);
		}finally{
			if (ses!=null)
				ses.logout();
		}
		
	}
	
	@Path("vrefolders")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ItemList getVreFolders(){
		CalledMethodProvider.instance.set("getVreFolders");
		Session ses = null;
		
		org.gcube.common.storagehub.model.Path vrePath = Paths.append(Utils.getHomePath(), Constants.VRE_FOLDER_PARENT_NAME);
		List<? extends Item> toReturn = null;
		try{
			String login = AuthorizationProvider.instance.get().getClient().getId();
			ses = repository.getRepository().login(new SimpleCredentials(login,Utils.getSecurePassword(login).toCharArray()));
			toReturn = Utils.getItemList(ses.getNode(vrePath.toPath()) , excludes, null, false);
		}catch(Throwable e){
			log.error("error reading the node children of {}",vrePath,e);
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
		CalledMethodProvider.instance.set("getVreFoldersPaged");
		Session ses = null;
		
		org.gcube.common.storagehub.model.Path vrePath = Paths.append(Utils.getHomePath(), Constants.VRE_FOLDER_PARENT_NAME);
		List<? extends Item> toReturn = null;
		try{
			String login = AuthorizationProvider.instance.get().getClient().getId();
			ses = repository.getRepository().login(new SimpleCredentials(login,Utils.getSecurePassword(login).toCharArray()));
			toReturn = Utils.getItemList(ses.getNode(vrePath.toPath()) , excludes, new Range(start, limit), false);
		}catch(Throwable e){
			log.error("error reading the node children of {}",vrePath,e);
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
		CalledMethodProvider.instance.set("searchItems");
		Session ses = null;
		List<? extends Item> toReturn = new ArrayList<>();
		
		try{
			
			ObjectMapper mapper = new ObjectMapper();
			Expression<Boolean> expression = mapper.readValue(jsonExpr, Expression.class);
			String stringExpression = evaluator.evaluate(new And(new ISDescendant(Utils.getHomePath()), expression));
			//ADD ALSO LIMIT AND OFFSET
			
			String orderBy = "";
			if (orderField!=null && orderField.size()>0)
				 orderBy= String.format("ORDER BY %s", orderField.stream().collect(Collectors.joining(",")).toString());
			
			
			String sql2Query = String.format("SELECT * FROM [%s] AS node WHERE %s %s ",node, stringExpression,orderBy);
			
			log.info("query sent is {}",sql2Query);
			
			
			String login = AuthorizationProvider.instance.get().getClient().getId();
			ses = repository.getRepository().login(new SimpleCredentials(login,Utils.getSecurePassword(login).toCharArray()));
			Query jcrQuery = ses.getWorkspace().getQueryManager().createQuery(sql2Query, Constants.QUERY_LANGUAGE);
			
			if (limit!=null && limit!=-1 )
				jcrQuery.setLimit(limit);
			
			if (offset!=null && offset!=-1 )
				jcrQuery.setOffset(offset);
			
			QueryResult result = jcrQuery.execute();
			
			NodeIterator it = result.getNodes();
									
			while (it.hasNext())
				toReturn.add(ItemHandler.getItem(it.nextNode(), excludes));			
			
		}catch(Throwable e){
			log.error("error executing the query",e);
			throw new WebServiceException("error executing the query", e);
		}finally{
			if (ses!=null)
				ses.logout();
		}
		
		return new ItemList(toReturn);
	}
	
	
/*
	@POST
	@Path("create")
	@Consumes({MediaType.MULTIPART_FORM_DATA})
	public Response create(@FormDataParam("item") ItemWrapper itemWrapper, @FormDataParam("file") InputStream stream , @FormDataParam("file") FormDataBodyPart fileDetail, @QueryParam("path") String path){
		Session ses = null;
		log.debug("method create called");
		org.gcube.common.storagehub.model.Path absolutePath = Paths.append(Utils.getHomePath(), Paths.getPath(path));
		try{
			final String login = AuthorizationProvider.instance.get().getClient().getId();
			//long start = System.currentTimeMillis();
			ses = repository.getRepository().login(new SimpleCredentials("workspacerep.imarine","gcube2010*onan".toCharArray()));
			log.debug("session retrieved");
			ItemHandler handler = new ItemHandler();
			Node parentNode = ses.getNode(absolutePath.toPath());
			Item item = itemWrapper.getItem();
			
			if (item instanceof AbstractFileItem){
				if (stream==null)
			 		throw new Exception("invalid item: file without an input stream is not accepted");
				fulfillContent((AbstractFileItem)item, stream, fileDetail, absolutePath.toPath());
			}
			
			
			Calendar now = Calendar.getInstance();
			item.setCreationTime(now);
			item.setHidden(false);
			item.setLastAction(ItemAction.CREATED);
			item.setLastModificationTime(now);
			item.setLastModifiedBy(login);
			item.setOwner(login);
			
			handler.createNodeFromItem(ses, parentNode, item, stream);
			ses.save();
		}catch(Throwable e){
			log.error("error creating file",e);
			return Response.serverError().build();
		}  finally{
			if (ses!=null) ses.logout();
		}
		return Response.ok().build();
	}

	private void fulfillContent(AbstractFileItem item, InputStream stream , FormDataBodyPart fileDetail, String path) {
		if (item instanceof GenericFileItem){
			Content content = new Content();
			String remotePath= path+"/"+fileDetail.getContentDisposition().getFileName();
			content.setData("jcr:content");
			content.setRemotePath(remotePath);
			content.setSize(fileDetail.getContentDisposition().getSize());
			content.setMimeType(fileDetail.getMediaType().toString());
			String storageId = Utils.getStorageClient(AuthorizationProvider.instance.get().getClient().getId()).getClient().put(true).LFile(stream).RFile(remotePath);
			content.setStorageId(storageId);
			((GenericFileItem) item).setContent(content);
		} else throw new RuntimeException("type file error");
	}

	@PUT
	@Path("{id}/move")
	public Response move(@QueryParam("path") String path, @PathParam("id") String identifier){
		Session ses = null;
		try{
			final String login = AuthorizationProvider.instance.get().getClient().getId();
			long start = System.currentTimeMillis();
			//ses = RepositoryInitializer.getRepository().login(new SimpleCredentials(login,Utils.getSecurePassword(login).toCharArray()));
			//TODO check if it is possible to change all the ACL on a workspace
			ses = repository.getRepository().login(new SimpleCredentials("workspacerep.imarine","gcube2010*onan".toCharArray()));

			log.info("time to connect to repo  {}",(System.currentTimeMillis()-start));
			final Node nodeToMove = ses.getNodeByIdentifier(identifier);
			final Node destination = ses.getNode(path);
			Item destinationItem = ItemHandler.getItem(destination,null);
			//TODO for now only owner of the destination folder can move file
			if (!destinationItem.getOwner().equals(login)){
				/*AccessControlManager accessControlManager = ses.getAccessControlManager();
				boolean canWrite = accessControlManager.hasPrivileges(path, new Privilege[] {
						accessControlManager.privilegeFromName(Privilege.JCR_ADD_CHILD_NODES)});*/
				//if (!canWrite) 
	/*
				throw new IllegalAccessException("Insufficent Provileges to write in "+path);
			}
			final Item item = ItemHandler.getItem(nodeToMove, null);
			if (item instanceof SharedFolder){
				throw new Exception("shared item cannot be moved");				
			}else if (item instanceof FolderItem){
				if (hasSharedChildren((FolderItem) item, ses)) throw new Exception("folder item with shared children cannot be moved");				
				ses.getWorkspace().move(nodeToMove.getPath(), destination.getPath()+"/"+nodeToMove.getName());
			}else {
				item.setParentId(destinationItem.getId());
				ses.getWorkspace().move(nodeToMove.getPath(), destination.getPath()+"/"+nodeToMove.getName());
			}
			ses.save();
		}catch(Exception e){
			log.error("error moving item with id {} in path {}",identifier, path,e);
			return Response.serverError().build();
		} finally{
			if (ses!=null) ses.logout();
		}
		return Response.ok().build();
	}

	private boolean hasSharedChildren(FolderItem folder, Session session) throws Exception{
		Node currentNode = session.getNodeByIdentifier(folder.getId());
		for (Item item : Utils.getItemList(currentNode,null)){
			if (item instanceof FolderItem) 
				return (item instanceof SharedFolder) || hasSharedChildren((FolderItem)item, session);
		}
		return false;

	}

	@PUT
	@Path("{id}/rename")
	public Response rename(@QueryParam("newname") String newName, @PathParam("id") String identifier){
		Session ses = null;
		try{
			final String login = AuthorizationProvider.instance.get().getClient().getId();
			long start = System.currentTimeMillis();
			ses = repository.getRepository().login(new SimpleCredentials(login,Utils.getSecurePassword(login).toCharArray()));
			log.info("time to connect to repo  {}",(System.currentTimeMillis()-start));
		}catch(Exception e){

		}
		return Response.ok().build();
	}
*/
	

	
}
