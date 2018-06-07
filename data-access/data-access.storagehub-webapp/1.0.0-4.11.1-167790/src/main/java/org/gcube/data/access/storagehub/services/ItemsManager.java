package org.gcube.data.access.storagehub.services;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.zip.Deflater;
import java.util.zip.ZipOutputStream;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.authorization.library.provider.CalledMethodProvider;
import org.gcube.common.storagehub.model.Paths;
import org.gcube.common.storagehub.model.items.AbstractFileItem;
import org.gcube.common.storagehub.model.items.FolderItem;
import org.gcube.common.storagehub.model.items.Item;
import org.gcube.common.storagehub.model.items.SharedFolder;
import org.gcube.common.storagehub.model.service.ItemList;
import org.gcube.common.storagehub.model.service.ItemWrapper;
import org.gcube.data.access.storagehub.AuthorizationChecker;
import org.gcube.data.access.storagehub.Constants;
import org.gcube.data.access.storagehub.Range;
import org.gcube.data.access.storagehub.SingleFileStreamingOutput;
import org.gcube.data.access.storagehub.Utils;
import org.gcube.data.access.storagehub.accounting.AccountingHandler;
import org.gcube.data.access.storagehub.handlers.ItemHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("item")
public class ItemsManager {

	private static final Logger log = LoggerFactory.getLogger(ItemsManager.class);

	@Inject 
	RepositoryInitializer repository;

	@Inject 
	AccountingHandler accountingHandler;

	@RequestScoped
	@PathParam("id") 
	String id;

	@Context 
	ServletContext context;

	@Inject
	AuthorizationChecker authChecker;
	
	
	

	@GET()
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public ItemWrapper<Item> getById(@QueryParam("exclude") List<String> excludes){
		CalledMethodProvider.instance.set("getById");
		Session ses = null;
		Item toReturn = null;
		try{
			String login = AuthorizationProvider.instance.get().getClient().getId();
			ses = repository.getRepository().login(new SimpleCredentials(login,Utils.getSecurePassword(login).toCharArray()));
			authChecker.checkReadAuthorizationControl(ses, id);
			toReturn = ItemHandler.getItem(ses.getNodeByIdentifier(id), excludes);
		}catch(Throwable e){
			log.error("error reading the node children of {}",id,e);
			throw new WebApplicationException(e);
		}finally{
			if (ses!=null)
				ses.logout();
		}

		return new ItemWrapper<Item>(toReturn);
	}


	@GET
	@Path("{id}/children/count")
	@Produces(MediaType.APPLICATION_JSON)
	public Long countById(@QueryParam("showHidden") Boolean showHidden, @QueryParam("exclude") List<String> excludes){
		CalledMethodProvider.instance.set("countById");
		Session ses = null;
		Long toReturn = null;
		try{
			String login = AuthorizationProvider.instance.get().getClient().getId();
			ses = repository.getRepository().login(new SimpleCredentials(login,Utils.getSecurePassword(login).toCharArray()));
			authChecker.checkReadAuthorizationControl(ses, id);
			toReturn = Utils.getItemCount(ses.getNodeByIdentifier(id), showHidden==null?false:showHidden);
		}catch(Throwable e){
			log.error("error reading the node children of {}",id,e);
			throw new WebApplicationException(e);
		}finally{
			if (ses!=null)
				ses.logout();
		}
		return toReturn ;
	}

	@GET
	@Path("{id}/children")
	@Produces(MediaType.APPLICATION_JSON)
	public ItemList listById(@QueryParam("showHidden") Boolean showHidden, @QueryParam("exclude") List<String> excludes){
		CalledMethodProvider.instance.set("listById");
		Session ses = null;
		List<? extends Item> toReturn = null;
		try{
			String login = AuthorizationProvider.instance.get().getClient().getId();
			ses = repository.getRepository().login(new SimpleCredentials(login,Utils.getSecurePassword(login).toCharArray()));
			authChecker.checkReadAuthorizationControl(ses, id);
			toReturn = Utils.getItemList(ses.getNodeByIdentifier(id), excludes, null, showHidden==null?false:showHidden);
		}catch(Throwable e){
			log.error("error reading the node children of {}",id,e);
			throw new WebApplicationException(e);
		}finally{
			if (ses!=null)
				ses.logout();
		}

		return new ItemList(toReturn);
	}

	@GET
	@Path("{id}/children/paged")
	@Produces(MediaType.APPLICATION_JSON)
	public ItemList listByIdPaged(@QueryParam("showHidden") Boolean showHidden, @QueryParam("start") Integer start, @QueryParam("limit") Integer limit, @QueryParam("exclude") List<String> excludes){
		CalledMethodProvider.instance.set("listByIdPaged");
		Session ses = null;
		List<? extends Item> toReturn = null;
		try{
			String login = AuthorizationProvider.instance.get().getClient().getId();
			ses = repository.getRepository().login(new SimpleCredentials(login,Utils.getSecurePassword(login).toCharArray()));
			authChecker.checkReadAuthorizationControl(ses, id);
			toReturn = Utils.getItemList(ses.getNodeByIdentifier(id), excludes, new Range(start, limit),showHidden==null?false:showHidden);
		}catch(Throwable e){
			log.error("error reading the node children of {}",id,e);
			throw new WebApplicationException(e);
		}finally{
			if (ses!=null)
				ses.logout();
		}

		return new ItemList(toReturn);
	}

	@GET
	@Path("{id}/publiclink")
	public URL getPubliclink() {
		CalledMethodProvider.instance.set("getPubliclink");
		//TODO: check who can call this method
		Session ses = null;
		try{
			String login = AuthorizationProvider.instance.get().getClient().getId();
			ses = repository.getRepository().login(new SimpleCredentials(login,Utils.getSecurePassword(login).toCharArray()));
			authChecker.checkReadAuthorizationControl(ses, id);
			String url =  Utils.getStorageClient(login).getClient().getHttpsUrl().RFileById(id);
			return new URL(url);
		}catch(Throwable e){
			log.error("error reading the node children of {}",id,e);
			throw new WebApplicationException(e);
		}finally{
			if (ses!=null)
				ses.logout();
		}

	}

	@GET
	@Path("{id}/anchestors")
	@Produces(MediaType.APPLICATION_JSON)
	public ItemList getAnchestors(@QueryParam("exclude") List<String> excludes){
		CalledMethodProvider.instance.set("getAnchestors");
		org.gcube.common.storagehub.model.Path absolutePath = Utils.getHomePath();
		Session ses = null;
		List<Item> toReturn = new LinkedList<>();
		try{
			String login = AuthorizationProvider.instance.get().getClient().getId();
			ses = repository.getRepository().login(new SimpleCredentials(login,Utils.getSecurePassword(login).toCharArray()));
			authChecker.checkReadAuthorizationControl(ses, id);
			Item currentItem = ItemHandler.getItem(ses.getNodeByIdentifier(id), excludes);
			log.trace("current node is {}",currentItem.getPath());
			while (!(currentItem.getPath()+"/").equals(absolutePath.toPath())) {
				if (currentItem instanceof SharedFolder){
					Map<String, Object> users =  ((SharedFolder) currentItem).getUsers().getValues();
					String[] user = ((String)users.get(login)).split("/");
					String parentId = user[0];
					currentItem = ItemHandler.getItem(ses.getNodeByIdentifier(parentId), excludes);

				}else
					currentItem = ItemHandler.getItem(ses.getNodeByIdentifier(currentItem.getParentId()), excludes);
				
				
				log.trace("current node is {}",currentItem.getPath());
				toReturn.add(currentItem);
			}
			
		}catch(Throwable e){
			log.error("error retrieving parents of node with id {}",id,e);
			throw new WebApplicationException(e);
		}finally{
			if (ses!=null)
				ses.logout();
		}

		log.trace("item list to return is empty ? {}",toReturn.isEmpty());
		
		return new ItemList(toReturn);
	}
	

	@GET
	@Path("{id}/download")
	public Response download(){
		CalledMethodProvider.instance.set("downloadById");
		Session ses = null;
		try{
			final String login = AuthorizationProvider.instance.get().getClient().getId();
			ses = repository.getRepository().login(new SimpleCredentials(context.getInitParameter(Constants.ADMIN_PARAM_NAME),context.getInitParameter(Constants.ADMIN_PARAM_PWD).toCharArray()));
			final Node node = ses.getNodeByIdentifier(id);
			authChecker.checkReadAuthorizationControl(ses, id);
			final Item item = ItemHandler.getItem(node, null);
			if (item instanceof AbstractFileItem){
				AbstractFileItem fileItem =(AbstractFileItem) item;

				final InputStream streamToWrite = Utils.getStorageClient(login).getClient().get().RFileAsInputStream(fileItem.getContent().getStorageId());

				accountingHandler.createReadObj(fileItem.getTitle(), ses, node, true);

				StreamingOutput so = new SingleFileStreamingOutput(streamToWrite);

				return Response
						.ok(so)
						.header("content-disposition","attachment; filename = "+fileItem.getName())
						.header("Content-Length", fileItem.getContent().getSize())
						.build();

			} else if (item instanceof FolderItem){

				try {	
					final Deque<Item> allNodes = Utils.getAllNodesForZip((FolderItem)item, ses, accountingHandler);
					final org.gcube.common.storagehub.model.Path originalPath = Paths.getPath(item.getPath());
					StreamingOutput so = new StreamingOutput() {

						@Override
						public void write(OutputStream os) {

							try(ZipOutputStream zos = new ZipOutputStream(os)){
								long start = System.currentTimeMillis();
								zos.setLevel(Deflater.BEST_COMPRESSION);
								log.debug("writing StreamOutput");
								Utils.zipNode(zos, allNodes, login, originalPath);	
								log.debug("StreamOutput written in {}",(System.currentTimeMillis()-start));
							} catch (Exception e) {
								log.error("error writing stream",e);
							}

						}
					};

					return Response
							.ok(so)
							.header("content-disposition","attachment; filename = directory.zip")
							.header("Content-Length", -1l)
							.build();
				}finally {
					if (ses!=null) ses.save();
				}
			} else throw new Exception("item type not supported for download: "+item.getClass());

		}catch(Exception e ){
			log.error("error downloading item content",e);
			throw new WebApplicationException(e);
		} finally{
			if (ses!=null) ses.logout();
		}
	}

	@PUT
	@Path("{id}/move")
	public Response move(@QueryParam("destinationId") String destinationId, @PathParam("id") String identifier){
		CalledMethodProvider.instance.set("move");
		Session ses = null;
		try{
			final String login = AuthorizationProvider.instance.get().getClient().getId();
			//ses = RepositoryInitializer.getRepository().login(new SimpleCredentials(login,Utils.getSecurePassword(login).toCharArray()));
			//TODO check if it is possible to change all the ACL on a workspace
			ses = repository.getRepository().login(new SimpleCredentials(context.getInitParameter(Constants.ADMIN_PARAM_NAME),context.getInitParameter(Constants.ADMIN_PARAM_PWD).toCharArray()));
			
			authChecker.checkWriteAuthorizationControl(ses, destinationId);
			authChecker.checkReadAuthorizationControl(ses, identifier);
			
			final Node nodeToMove = ses.getNodeByIdentifier(identifier);
			final Node destination = ses.getNodeByIdentifier(destinationId);
			Item destinationItem = ItemHandler.getItem(destination,null);
			
			
			
			
			ses.getWorkspace().getLockManager().lock(destinationItem.getPath(), true, true, 0,login);
			ses.getWorkspace().getLockManager().lock(destinationItem.getPath(), true, true, 0,login);
			
			final Item item = ItemHandler.getItem(nodeToMove, null);
			if (item instanceof SharedFolder){
				throw new Exception("shared folder cannot be moved");				
			}else if (item instanceof FolderItem){
				if (hasSharedChildren((FolderItem) item, ses)) throw new Exception("folder item with shared children cannot be moved");				
				ses.getWorkspace().move(nodeToMove.getPath(), destination.getPath()+"/"+nodeToMove.getName());
			}else {
				item.setParentId(destinationItem.getId());
				ses.getWorkspace().move(nodeToMove.getPath(), destination.getPath()+"/"+nodeToMove.getName());
			}
			ses.save();
		}catch(Exception e){
			log.error("error moving item with id {} in item with id {}",identifier, destinationId,e);
			throw new WebApplicationException(e);
		} finally{
			if (ses!=null) ses.logout();
		}
		return Response.ok().build();
	}



	private boolean hasSharedChildren(FolderItem item, Session session) throws Exception{
		Node currentNode = session.getNodeByIdentifier(item.getId());
		for (Item children : Utils.getItemList(currentNode,Arrays.asList("hl:accounting","jcr:content"), null, false)){
			if (children instanceof FolderItem) 
				return (children instanceof SharedFolder) || hasSharedChildren((FolderItem)item, session);
		}
		return false;

	}

}