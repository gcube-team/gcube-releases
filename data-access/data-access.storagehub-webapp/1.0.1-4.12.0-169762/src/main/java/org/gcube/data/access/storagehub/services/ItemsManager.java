package org.gcube.data.access.storagehub.services;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.zip.Deflater;
import java.util.zip.ZipOutputStream;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.jcr.version.Version;
import javax.servlet.ServletContext;
import javax.ws.rs.DELETE;
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
import org.gcube.common.storagehub.model.NodeConstants;
import org.gcube.common.storagehub.model.Paths;
import org.gcube.common.storagehub.model.items.AbstractFileItem;
import org.gcube.common.storagehub.model.items.FolderItem;
import org.gcube.common.storagehub.model.items.Item;
import org.gcube.common.storagehub.model.items.SharedFolder;
import org.gcube.common.storagehub.model.items.TrashItem;
import org.gcube.common.storagehub.model.items.VreFolder;
import org.gcube.common.storagehub.model.service.ItemList;
import org.gcube.common.storagehub.model.service.ItemWrapper;
import org.gcube.common.storagehub.model.types.ItemAction;
import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.data.access.storagehub.AuthorizationChecker;
import org.gcube.data.access.storagehub.Constants;
import org.gcube.data.access.storagehub.Range;
import org.gcube.data.access.storagehub.SingleFileStreamingOutput;
import org.gcube.data.access.storagehub.Utils;
import org.gcube.data.access.storagehub.accounting.AccountingHandler;
import org.gcube.data.access.storagehub.handlers.CredentialHandler;
import org.gcube.data.access.storagehub.handlers.ItemHandler;
import org.gcube.data.access.storagehub.handlers.VersionHandler;
import org.gcube.smartgears.utils.InnerMethodName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("items")
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

	@Inject
	VersionHandler versionHandler;
		
	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public ItemWrapper<Item> getById(@QueryParam("exclude") List<String> excludes){
		InnerMethodName.instance.set("getById");
		Session ses = null;
		Item toReturn = null;
		try{
			ses = repository.getRepository().login(CredentialHandler.getAdminCredentials(context));
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
	@Path("{id}/items/{name}")
	@Produces(MediaType.APPLICATION_JSON)
	public ItemList findChildrenByNamePattern(@QueryParam("exclude") List<String> excludes, @PathParam("name") String name){
		InnerMethodName.instance.set("findChildrenByNamePattern");
		Session ses = null;
		List<Item> toReturn = new ArrayList<>();
		try{
			ses = repository.getRepository().login(CredentialHandler.getAdminCredentials(context));
			authChecker.checkReadAuthorizationControl(ses, id);
			NodeIterator it = ses.getNodeByIdentifier(id).getNodes(name);
			while (it.hasNext())
				toReturn.add(ItemHandler.getItem(it.nextNode(), excludes));
		}catch(Throwable e){
			log.error("error reading the node children of {} with name pattern",id,name,e);
			throw new WebApplicationException(e);
		}finally{
			if (ses!=null)
				ses.logout();
		}

		return new ItemList(toReturn);
	}
	

	@GET
	@Path("{id}/children/count")
	@Produces(MediaType.APPLICATION_JSON)
	public Long countById(@QueryParam("showHidden") Boolean showHidden, @QueryParam("exclude") List<String> excludes){
		InnerMethodName.instance.set("countById");
		Session ses = null;
		Long toReturn = null;
		try{
			ses = repository.getRepository().login(CredentialHandler.getAdminCredentials(context));
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
		InnerMethodName.instance.set("listById");
		Session ses = null;
		List<? extends Item> toReturn = null;
		try{
			ses = repository.getRepository().login(CredentialHandler.getAdminCredentials(context));
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
		InnerMethodName.instance.set("listByIdPaged");
		Session ses = null;
		List<? extends Item> toReturn = null;
		try{
			ses = repository.getRepository().login(CredentialHandler.getAdminCredentials(context));
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
		InnerMethodName.instance.set("getPubliclink");
		//TODO: check who can call this method
		Session ses = null;
		try{
			String login = AuthorizationProvider.instance.get().getClient().getId();
			ses = repository.getRepository().login(CredentialHandler.getAdminCredentials(context));
			authChecker.checkReadAuthorizationControl(ses, id);

			Item item = ItemHandler.getItem(ses.getNodeByIdentifier(id), Arrays.asList(NodeConstants.ACCOUNTING_NAME, NodeConstants.METADATA_NAME));

			if (!(item instanceof AbstractFileItem)) throw new Exception("the select item is not a File");

			AbstractFileItem fileItem = (AbstractFileItem) item;

			String url =  Utils.getStorageClient(login).getClient().getHttpsUrl().RFileById(fileItem.getContent().getStorageId());
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
	@Path("{id}/rootSharedFolder")
	@Produces(MediaType.APPLICATION_JSON)
	public ItemWrapper<Item> getRootSharedFolder(@QueryParam("exclude") List<String> excludes){
		InnerMethodName.instance.set("getRootSharedFolder");
		Session ses = null;
		try{
			ses = repository.getRepository().login(CredentialHandler.getAdminCredentials(context));
			authChecker.checkReadAuthorizationControl(ses, id);
			Item currentItem = ItemHandler.getItem(ses.getNodeByIdentifier(id), excludes);
			if (!currentItem.isShared())
				throw new RuntimeException("this item is not shared");
			log.trace("current node is {}",currentItem.getPath());
			while (!(currentItem instanceof SharedFolder )) 
				currentItem = ItemHandler.getItem(ses.getNodeByIdentifier(currentItem.getParentId()), Arrays.asList(NodeConstants.ACCOUNTING_NAME, NodeConstants.METADATA_NAME, NodeConstants.CONTENT_NAME));
			
			return new ItemWrapper<Item>(currentItem);
			
		}catch(Throwable e){
			log.error("error retrieving shared root folder of node with id {}",id,e);
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
		InnerMethodName.instance.set("getAnchestors");
		org.gcube.common.storagehub.model.Path absolutePath = Utils.getHomePath();
		Session ses = null;
		List<Item> toReturn = new LinkedList<>();
		try{
			String login = AuthorizationProvider.instance.get().getClient().getId();
			ses = repository.getRepository().login(CredentialHandler.getAdminCredentials(context));
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
		InnerMethodName.instance.set("downloadById");
		Session ses = null;
		try{
			final String login = AuthorizationProvider.instance.get().getClient().getId();
			ses = repository.getRepository().login(CredentialHandler.getAdminCredentials(context));
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
		InnerMethodName.instance.set("move");
		//TODO: check if identifier is The Workspace root, or the thras folder or the VREFolder root or if the item is thrashed		
		Session ses = null;
		try{
			final String login = AuthorizationProvider.instance.get().getClient().getId();
			//ses = RepositoryInitializer.getRepository().login(new SimpleCredentials(login,Utils.getSecurePassword(login).toCharArray()));
			//TODO check if it is possible to change all the ACL on a workspace
			ses = repository.getRepository().login(CredentialHandler.getAdminCredentials(context));

			authChecker.checkWriteAuthorizationControl(ses, destinationId, true);
			authChecker.checkWriteAuthorizationControl(ses, identifier, false);

			final Node nodeToMove = ses.getNodeByIdentifier(identifier);
			final Node destination = ses.getNodeByIdentifier(destinationId);
			Item destinationItem = ItemHandler.getItem(destination,null);

			final Item item = ItemHandler.getItem(nodeToMove, null);

			if (item instanceof SharedFolder || item.isHidden() || destinationItem.isHidden())
				throw new Exception("shared folder cannot be moved or cannot not move hidden item");

			if (Constants.FOLDERS_TO_EXLUDE.contains(item.getTitle()) || Constants.FOLDERS_TO_EXLUDE.contains(destinationItem.getTitle()))
				throw new Exception("protected folder cannot be moved");


			ses.getWorkspace().getLockManager().lock(destinationItem.getPath(), true, true, 0,login);
			ses.getWorkspace().getLockManager().lock(nodeToMove.getPath(), true, true, 0,login);

			if (item instanceof FolderItem){

				if (Utils.hasSharedChildren((FolderItem) item, ses)) throw new Exception("folder item with shared children cannot be moved");				

				ses.getWorkspace().move(nodeToMove.getPath(), destination.getPath()+"/"+nodeToMove.getName());
			}else 
				ses.getWorkspace().move(nodeToMove.getPath(), destination.getPath()+"/"+nodeToMove.getName());

			//TODO: accounting

			ses.getWorkspace().getLockManager().unlock(nodeToMove.getPath());
			ses.getWorkspace().getLockManager().unlock(destinationItem.getPath());
			ses.save();
		}catch(Exception e){
			log.error("error moving item with id {} in item with id {}",identifier, destinationId,e);
			throw new WebApplicationException(e);
		} finally{
			if (ses!=null) {
				ses.logout();
			}
		}
		return Response.ok().build();
	}

	@DELETE
	@Path("{id}")
	public Response deleteItem(@PathParam("id") String identifier){
		InnerMethodName.instance.set("deleteItem");
		//TODO: check if identifier is The Workspace root, or the trash folder or the VREFolder root	
		//TODO: check also that is not already trashed
		Session ses = null;
		try{

			log.info("removing node with id {}", identifier);
			
			//TODO check if it is possible to change all the ACL on a workspace
			ses = repository.getRepository().login(CredentialHandler.getAdminCredentials(context));

			authChecker.checkWriteAuthorizationControl(ses, identifier, false);

			final Node nodeToDelete = ses.getNodeByIdentifier(identifier);

			Item itemToDelete = ItemHandler.getItem(nodeToDelete, Arrays.asList(NodeConstants.ACCOUNTING_NAME, NodeConstants.METADATA_NAME, NodeConstants.OWNER_NAME));
			
			if (itemToDelete instanceof SharedFolder || itemToDelete instanceof VreFolder || (itemToDelete instanceof FolderItem && Utils.hasSharedChildren((FolderItem) itemToDelete, ses)))
				throw new Exception("SharedFolder, VreFolder or folders with shared children cannot be deleted");
			
			log.debug("item is trashed? {}", itemToDelete.isTrashed());
			
			if (!itemToDelete.isTrashed())
				moveToTrash(ses, nodeToDelete, itemToDelete);
			else 
				removeNode(ses, itemToDelete);

		}catch(Exception e){
			log.error("error removing item with id {} in Thrash",identifier,e);
			throw new WebApplicationException(e);
		} finally{
			if (ses!=null) {
				ses.logout();
			}
		}
		return Response.ok().build();
	}


	private void removeNode(Session ses, Item itemToDelete) throws Exception{
		log.debug("removing node");
		final String login = AuthorizationProvider.instance.get().getClient().getId();
		String parentPath = itemToDelete.getParentPath();
		try {
			ses.getWorkspace().getLockManager().lock(parentPath, true, true, 0,login);
			Set<String> idsToDelete = new HashSet<>();
			getAllContentIds(ses, idsToDelete, itemToDelete);
			ses.removeItem(itemToDelete.getPath());
			new Thread() {
				
				private String user = AuthorizationProvider.instance.get().getClient().getId();
				
				public void run() {
					for (String id: idsToDelete) {
						try {
							IClient client = Utils.getStorageClient(user).getClient();
							client.remove().RFileById(id);
							log.debug("file with id {} correctly removed on storage",id);
						}catch(Throwable t) {
							log.warn("error removing file on storage with id {}",id, t);
						}
					}

				}
			}.start();;
			ses.save();
		}finally {
			ses.getWorkspace().getLockManager().unlock(parentPath);
		}
	}


	private void getAllContentIds(Session ses, Set<String> idsToDelete, Item itemToDelete) throws Exception{
		if (itemToDelete instanceof AbstractFileItem) {
			List<Version> versions = versionHandler.getContentVersionHistory(ses.getNodeByIdentifier(itemToDelete.getId()), ses);
			
			versions.forEach(v -> {
				try {
					String storageId =v.getProperty("hl:storageId").toString();
					idsToDelete.add(storageId);
					log.info("retrieved StorageId {} for version {}", storageId, v.getName());
				} catch (Exception e) {
					log.warn("error retreiving sotrageId",e);
				}
			});
			
			idsToDelete.add(((AbstractFileItem) itemToDelete).getContent().getStorageId());
		}else if (itemToDelete instanceof FolderItem) {
			List<Item> items = Utils.getItemList(ses.getNodeByIdentifier(itemToDelete.getId()), Arrays.asList(NodeConstants.ACCOUNTING_NAME, NodeConstants.METADATA_NAME, NodeConstants.OWNER_NAME) , null, true);
			for (Item item: items) 
				getAllContentIds(ses, idsToDelete, item);

		}

	}


	private void moveToTrash(Session ses, Node nodeToDelete, Item item) throws Exception{
		log.debug("moving node to trash");
		final Node trashFolder = ses.getNode(Paths.append(Utils.getHomePath(),Constants.TRASH_ROOT_FOLDER_NAME).toPath());

		final String login = AuthorizationProvider.instance.get().getClient().getId();

		try {
			ses.getWorkspace().getLockManager().lock(trashFolder.getPath(), true, true, 0,login);
			ses.getWorkspace().getLockManager().lock(nodeToDelete.getPath(), true, true, 0,login);

			log.debug("preparing thrash item");

			TrashItem trashItem = new TrashItem();
			trashItem.setDeletedBy(AuthorizationProvider.instance.get().getClient().getId());
			trashItem.setDeletedFrom(nodeToDelete.getParent().getPath());
			Calendar now = Calendar.getInstance();
			trashItem.setDeletedTime(now);
			trashItem.setHidden(false);
			trashItem.setLastAction(ItemAction.CREATED);
			trashItem.setDescription("trash item of node " + nodeToDelete.getPath());
			trashItem.setParentId(trashFolder.getIdentifier());
			trashItem.setParentPath(trashFolder.getPath());
			String pathUUid= UUID.randomUUID().toString();
			trashItem.setTitle(pathUUid);
			trashItem.setName(pathUUid);
			trashItem.setOriginalParentId(nodeToDelete.getParent().getIdentifier());	
						
			trashItem.setOwner(item.getOwner());
			trashItem.setLastModificationTime(item.getLastModificationTime());
			trashItem.setLastModifiedBy(item.getLastModifiedBy());
			
			trashItem.setLenght(0);
			
			if (item instanceof FolderItem) 
				trashItem.setFolder(true);
			else if (item instanceof AbstractFileItem ) {
				AbstractFileItem file = (AbstractFileItem) item;
				trashItem.setMimeType(file.getContent().getMimeType());
				trashItem.setLenght(file.getContent().getSize());
			}

			log.debug("creating node");
			
			Node newTrashItemNode = ItemHandler.createNodeFromItem(ses, trashFolder, trashItem);
			
			ses.save();
			log.debug("calling move into jcr");
			ses.getWorkspace().move(nodeToDelete.getPath(), Paths.append(Paths.getPath(newTrashItemNode.getPath()),nodeToDelete.getName()).toPath());
			String mimetype = null;
			if (item instanceof AbstractFileItem)
				mimetype = ((AbstractFileItem) item).getContent().getMimeType();
			accountingHandler.createFolderRemoveObj(item.getName(), item.getClass().getSimpleName(), mimetype, ses, ses.getNodeByIdentifier(item.getParentId()), true);
		}finally {
			ses.getWorkspace().getLockManager().unlock(nodeToDelete.getPath());
			ses.getWorkspace().getLockManager().unlock(trashFolder.getPath());
		}

	}

	

}