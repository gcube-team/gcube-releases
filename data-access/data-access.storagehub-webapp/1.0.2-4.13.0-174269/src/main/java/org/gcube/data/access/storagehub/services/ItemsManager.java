package org.gcube.data.access.storagehub.services;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.zip.Deflater;
import java.util.zip.ZipOutputStream;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.version.Version;
import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.io.FilenameUtils;
import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.gxrest.response.outbound.GXOutboundErrorResponse;
import org.gcube.common.storagehub.model.Excludes;
import org.gcube.common.storagehub.model.NodeConstants;
import org.gcube.common.storagehub.model.Paths;
import org.gcube.common.storagehub.model.exceptions.BackendGenericError;
import org.gcube.common.storagehub.model.exceptions.InvalidCallParameters;
import org.gcube.common.storagehub.model.exceptions.InvalidItemException;
import org.gcube.common.storagehub.model.exceptions.StorageHubException;
import org.gcube.common.storagehub.model.items.AbstractFileItem;
import org.gcube.common.storagehub.model.items.FolderItem;
import org.gcube.common.storagehub.model.items.Item;
import org.gcube.common.storagehub.model.items.SharedFolder;
import org.gcube.common.storagehub.model.items.VreFolder;
import org.gcube.common.storagehub.model.service.ItemList;
import org.gcube.common.storagehub.model.service.ItemWrapper;
import org.gcube.common.storagehub.model.service.VersionList;
import org.gcube.common.storagehub.model.types.ItemAction;
import org.gcube.common.storagehub.model.types.NodeProperty;
import org.gcube.data.access.storagehub.AuthorizationChecker;
import org.gcube.data.access.storagehub.Constants;
import org.gcube.data.access.storagehub.Range;
import org.gcube.data.access.storagehub.SingleFileStreamingOutput;
import org.gcube.data.access.storagehub.Utils;
import org.gcube.data.access.storagehub.accounting.AccountingHandler;
import org.gcube.data.access.storagehub.handlers.ClassHandler;
import org.gcube.data.access.storagehub.handlers.CredentialHandler;
import org.gcube.data.access.storagehub.handlers.Item2NodeConverter;
import org.gcube.data.access.storagehub.handlers.Node2ItemConverter;
import org.gcube.data.access.storagehub.handlers.TrashHandler;
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

	@Inject
	TrashHandler trashHandler;

	@Inject Node2ItemConverter node2Item;
	@Inject Item2NodeConverter item2Node;

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
			toReturn = node2Item.getItem(ses.getNodeByIdentifier(id), excludes);
		}catch(RepositoryException re){
			log.error("jcr error getting item", re);
			GXOutboundErrorResponse.throwException(new BackendGenericError("jcr error searching item", re));
		}catch(StorageHubException she ){
			log.error("error getting item", she);
			GXOutboundErrorResponse.throwException(she);
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
				toReturn.add(node2Item.getItem(it.nextNode(), excludes));
		}catch(RepositoryException re){
			log.error("jcr error searching item", re);
			GXOutboundErrorResponse.throwException(new BackendGenericError("jcr error searching item", re));
		}catch(StorageHubException she ){
			log.error("error searching item", she);
			GXOutboundErrorResponse.throwException(she);		
		}finally{
			if (ses!=null)
				ses.logout();
		}

		return new ItemList(toReturn);
	}


	@GET
	@Path("{id}/children/count")
	@Produces(MediaType.APPLICATION_JSON)
	public Long countById(@QueryParam("showHidden") Boolean showHidden, @QueryParam("exclude") List<String> excludes, @QueryParam("onlyType") String nodeType){
		InnerMethodName.instance.set("countById");
		Session ses = null;
		Long toReturn = null;

		try{

			ses = repository.getRepository().login(CredentialHandler.getAdminCredentials(context));
			authChecker.checkReadAuthorizationControl(ses, id);
			toReturn = Utils.getItemCount(ses.getNodeByIdentifier(id), showHidden==null?false:showHidden, nodeType!=null ? ClassHandler.instance().get(nodeType) : null);
		}catch(RuntimeException | RepositoryException re){
			log.error("jcr error counting item", re);
			GXOutboundErrorResponse.throwException(new BackendGenericError(re));
		}catch(StorageHubException she ){
			log.error("error counting item", she);
			GXOutboundErrorResponse.throwException(she);		
		}finally{
			if (ses!=null)
				ses.logout();
		}
		return toReturn ;
	}

	@GET
	@Path("{id}/children")
	@Produces(MediaType.APPLICATION_JSON)
	public ItemList listById(@QueryParam("showHidden") Boolean showHidden, @QueryParam("exclude") List<String> excludes, @QueryParam("onlyType") String nodeType){
		InnerMethodName.instance.set("listById");
		Session ses = null;
		List<? extends Item> toReturn = null;
		try{
			ses = repository.getRepository().login(CredentialHandler.getAdminCredentials(context));
			authChecker.checkReadAuthorizationControl(ses, id);
			toReturn = Utils.getItemList(ses.getNodeByIdentifier(id), excludes, null, showHidden==null?false:showHidden, nodeType!=null ? ClassHandler.instance().get(nodeType) : null);
		}catch(RepositoryException re){
			log.error("jcr error getting children", re);
			GXOutboundErrorResponse.throwException(new BackendGenericError(re));
		}catch(StorageHubException she ){
			log.error("error getting children", she);
			GXOutboundErrorResponse.throwException(she);		
		}finally{
			if (ses!=null)
				ses.logout();
		}

		return new ItemList(toReturn);
	}

	@GET
	@Path("{id}/children/paged")
	@Produces(MediaType.APPLICATION_JSON)
	public ItemList listByIdPaged(@QueryParam("showHidden") Boolean showHidden, @QueryParam("start") Integer start, @QueryParam("limit") Integer limit, @QueryParam("exclude") List<String> excludes, @QueryParam("onlyType") String nodeType){
		InnerMethodName.instance.set("listByIdPaged");
		Session ses = null;
		List<? extends Item> toReturn = null;
		try{
			ses = repository.getRepository().login(CredentialHandler.getAdminCredentials(context));
			authChecker.checkReadAuthorizationControl(ses, id);
			toReturn = Utils.getItemList(ses.getNodeByIdentifier(id), excludes, new Range(start, limit),showHidden==null?false:showHidden, nodeType!=null ? ClassHandler.instance().get(nodeType) : null);
		}catch(RepositoryException re){
			log.error("jcr error getting paged children", re);
			GXOutboundErrorResponse.throwException(new BackendGenericError(re));
		}catch(StorageHubException she ){
			log.error("error getting paged children", she);
			GXOutboundErrorResponse.throwException(she);		
		}finally{
			if (ses!=null)
				ses.logout();
		}

		return new ItemList(toReturn);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{id}/publiclink")
	public URL getPublicLink(@QueryParam("version") String version) {
		InnerMethodName.instance.set("getPubliclink");
		Session ses = null;
		URL toReturn = null;
		try{
			String login = AuthorizationProvider.instance.get().getClient().getId();
			ses = repository.getRepository().login(CredentialHandler.getAdminCredentials(context));
			authChecker.checkReadAuthorizationControl(ses, id);

			Node selectedNode = ses.getNodeByIdentifier(id);
			
			Item item = node2Item.getItem(selectedNode, Arrays.asList(NodeConstants.ACCOUNTING_NAME, NodeConstants.METADATA_NAME));

			if (!(item instanceof AbstractFileItem)) throw new InvalidCallParameters("the choosen item is not a File");

			
			if (version!=null) {
				boolean versionFound = false;
				VersionList versions = getVersions();
				for (org.gcube.common.storagehub.model.service.Version v: versions.getItemlist() ) 
					if (v.getName().equals(version)) {
						versionFound = true;
						break;
					}
				if (!versionFound) throw new InvalidCallParameters("the selected file has no version "+version); 
			}
			
			ses.getWorkspace().getLockManager().lock(selectedNode.getPath(), false, true, 0,login);
			try {
				selectedNode.setProperty(NodeProperty.IS_PUBLIC.toString(), true);	
				ses.save();
			}finally {
				ses.getWorkspace().getLockManager().unlock(selectedNode.getPath());
			}
			
			String url = createPublicLink(version, id);

			toReturn = new URL(url);

		}catch(RepositoryException | MalformedURLException re ){
			log.error("jcr error getting public link", re);
			GXOutboundErrorResponse.throwException(new BackendGenericError(re));
		}catch(StorageHubException she ){
			log.error("error getting public link", she);
			GXOutboundErrorResponse.throwException(she);		
		}finally{
			if (ses!=null)
				ses.logout();
		}

		return toReturn;
	}

	
	private String createPublicLink(String version, String id) {
		String basepath = context.getInitParameter("resolver-basepath");
		String filePublicUrl = String.format("%s/%s",basepath, id);
		if (version!=null)
			filePublicUrl = String.format("%s/%s", filePublicUrl, version);
		return filePublicUrl;
	}

	@GET
	@Path("{id}/rootSharedFolder")
	@Produces(MediaType.APPLICATION_JSON)
	public ItemWrapper<Item> getRootSharedFolder(@QueryParam("exclude") List<String> excludes){
		InnerMethodName.instance.set("getRootSharedFolder");
		Session ses = null;
		Item sharedParent= null;
		try{
			ses = repository.getRepository().login(CredentialHandler.getAdminCredentials(context));
			authChecker.checkReadAuthorizationControl(ses, id);
			Node currentNode =ses.getNodeByIdentifier(id);
			Item currentItem = node2Item.getItem(currentNode, excludes);
						
			if (!currentItem.isShared())
				throw new InvalidItemException("this item is not shared");
			log.trace("current node is {}",currentNode.getPath());
			
			while (!node2Item.checkNodeType(currentNode, SharedFolder.class)) 
				currentNode = currentNode.getParent();

			sharedParent = node2Item.getItem(currentNode, excludes);

		}catch(RepositoryException re ){
			log.error("jcr error getting rootSharedFolder", re);
			GXOutboundErrorResponse.throwException(new BackendGenericError(re));
		}catch(StorageHubException she ){
			log.error("error getting rootSharedFolder", she);
			GXOutboundErrorResponse.throwException(she);		
		}finally{
			if (ses!=null)
				ses.logout();
		}
		return new ItemWrapper<Item>(sharedParent);
	}

	@GET
	@Path("{id}/versions")
	@Produces(MediaType.APPLICATION_JSON)
	public VersionList getVersions(){
		InnerMethodName.instance.set("getVersions");
		Session ses = null;
		List<org.gcube.common.storagehub.model.service.Version> versions = new ArrayList<>();
		try{
			ses = repository.getRepository().login(CredentialHandler.getAdminCredentials(context));
			authChecker.checkReadAuthorizationControl(ses, id);
			
			Node node = ses.getNodeByIdentifier(id);
			
			Item currentItem = node2Item.getItem(node, Excludes.GET_ONLY_CONTENT);
			if (!(currentItem instanceof AbstractFileItem))
				throw new InvalidItemException("this item is not versioned");

			List<Version> jcrVersions = versionHandler.getContentVersionHistory(node, ses);
			
			for (Version version: jcrVersions) {
				boolean currentVersion = ((AbstractFileItem)currentItem).getContent().getStorageId().equals(version.getFrozenNode().getProperty(NodeProperty.STORAGE_ID.toString()).getString());
				versions.add(new org.gcube.common.storagehub.model.service.Version(version.getIdentifier(), version.getName(), version.getCreated(), currentVersion));
			}
		}catch(RepositoryException re ){
			log.error("jcr error retrieving versions", re);
			GXOutboundErrorResponse.throwException(new BackendGenericError(re));
		}catch(StorageHubException she ){
			log.error("error retrieving versions", she);
			GXOutboundErrorResponse.throwException(she);		
		}finally{
			if (ses!=null)
				ses.logout();
		}
		return new VersionList(versions);
	}

	@GET
	@Path("{id}/versions/{version}/download")
	@Produces(MediaType.APPLICATION_JSON)
	public Response downloadVersion(@PathParam("version") String versionName){
		InnerMethodName.instance.set("downloadSpecificVersion");
		Session ses = null;
		try{
			String login = AuthorizationProvider.instance.get().getClient().getId();
			ses = repository.getRepository().login(CredentialHandler.getAdminCredentials(context));
			authChecker.checkReadAuthorizationControl(ses, id);
			Node node = ses.getNodeByIdentifier(id);
			Item currentItem = node2Item.getItem(node, Excludes.ALL);
			if (!(currentItem instanceof AbstractFileItem))
				throw new InvalidItemException("this item is not a file");

			List<Version> jcrVersions = versionHandler.getContentVersionHistory(ses.getNodeByIdentifier(id), ses);

			for (Version version: jcrVersions) {
				log.debug("retrieved version id {}, name {}", version.getIdentifier(), version.getName());
				if (version.getName().equals(versionName)) {
					long size = version.getFrozenNode().getProperty(NodeProperty.SIZE.toString()).getLong();
					String mimeType = version.getFrozenNode().getProperty(NodeProperty.MIME_TYPE.toString()).getString();
					String storageId = version.getFrozenNode().getProperty(NodeProperty.STORAGE_ID.toString()).getString();
					
					final InputStream streamToWrite = Utils.getStorageClient(login).getClient().get().RFileAsInputStream(storageId);
					
					String oldfilename = FilenameUtils.getBaseName(currentItem.getTitle());
					String ext = FilenameUtils.getExtension(currentItem.getTitle());
					
					String fileName = String.format("%s_v%s.%s", oldfilename, version.getName(), ext);
					
					
					accountingHandler.createReadObj(fileName, ses, node, true);
					
					StreamingOutput so = new SingleFileStreamingOutput(streamToWrite);

					return Response
							.ok(so)
							.header("content-disposition","attachment; filename = "+fileName)
							.header("Content-Length", size)
							.header("Content-Type", mimeType)
							.build();
				}
			}
			
			
		}catch(RepositoryException re ){
			log.error("jcr error downloading version", re);
			GXOutboundErrorResponse.throwException(new BackendGenericError(re));
		}catch(StorageHubException she ){
			log.error("error downloading version", she);
			GXOutboundErrorResponse.throwException(she);		
		}finally{
			if (ses!=null)
				ses.logout();
		}
		return Response.serverError().build();
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
			Node currentNode = ses.getNodeByIdentifier(id);
			Item currentItem = node2Item.getItem(currentNode, excludes);
			log.trace("current node is {}",currentNode.getPath());
			while (!(currentNode.getPath()+"/").equals(absolutePath.toPath())) {
				if (currentItem instanceof SharedFolder){
					Map<String, Object> users =  ((SharedFolder) currentItem).getUsers().getValues();
					String[] user = ((String)users.get(login)).split("/");
					String parentId = user[0];
					currentNode = ses.getNodeByIdentifier(parentId);
					currentItem = node2Item.getItem(currentNode, excludes);

				}else {
					currentNode = currentNode.getParent();
					currentItem = node2Item.getItem(currentNode, excludes);
				}

				log.trace("current node is {}",currentNode.getPath());
				toReturn.add(currentItem);
			}

		}catch(RepositoryException re ){
			log.error("jcr error getting anchestors", re);
			GXOutboundErrorResponse.throwException(new BackendGenericError(re));
		}catch(StorageHubException she ){
			log.error("error getting anchestors", she);
			GXOutboundErrorResponse.throwException(she);		
		}finally{
			if (ses!=null)
				ses.logout();
		}

		log.trace("item list to return is empty ? {}",toReturn.isEmpty());

		return new ItemList(toReturn);
	}




	@GET
	@Path("{id}/download")
	public Response download(@QueryParam("exclude") List<String> excludes){
		InnerMethodName.instance.set("downloadById");
		Session ses = null;
		Response response = null;
		try{
			final String login = AuthorizationProvider.instance.get().getClient().getId();
			ses = repository.getRepository().login(CredentialHandler.getAdminCredentials(context));
			final Node node = ses.getNodeByIdentifier(id);
			authChecker.checkReadAuthorizationControl(ses, id);
			final Item item = node2Item.getItem(node, null);
			if (item instanceof AbstractFileItem){
				AbstractFileItem fileItem =(AbstractFileItem) item;

				final InputStream streamToWrite = Utils.getStorageClient(login).getClient().get().RFileAsInputStream(fileItem.getContent().getStorageId());

				accountingHandler.createReadObj(fileItem.getTitle(), ses, node, true);

				StreamingOutput so = new SingleFileStreamingOutput(streamToWrite);

				response = Response
						.ok(so)
						.header("content-disposition","attachment; filename = "+fileItem.getName())
						.header("Content-Length", fileItem.getContent().getSize())
						.header("Content-Type", fileItem.getContent().getMimeType())
						.build();

			} else if (item instanceof FolderItem){

				try {	
					final Deque<Item> allNodes = Utils.getAllNodesForZip((FolderItem)item, ses, accountingHandler, excludes);
					final org.gcube.common.storagehub.model.Path originalPath = Paths.getPath(item.getParentPath());
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

					response = Response
							.ok(so)
							.header("content-disposition","attachment; filename = "+item.getTitle()+".zip")
							.header("Content-Type", "application/zip")
							.header("Content-Length", -1l)
							.build();
				}finally {
					if (ses!=null) ses.save();
				}
			} else throw new InvalidItemException("item type not supported for download: "+item.getClass());

		}catch(RepositoryException re ){
			log.error("jcr error download", re);
			GXOutboundErrorResponse.throwException(new BackendGenericError(re));
		}catch(StorageHubException she ){
			log.error("error download", she);
			GXOutboundErrorResponse.throwException(she);		
		} finally{
			if (ses!=null) ses.logout();
		}

		return response;
	}


	@PUT
	@Path("{id}/move")
	public String move(@FormParam("destinationId") String destinationId){
		InnerMethodName.instance.set("move");
		//TODO: check if identifier is The Workspace root, or the thras folder or the VREFolder root or if the item is thrashed		
		Session ses = null;
		try{
			final String login = AuthorizationProvider.instance.get().getClient().getId();

			ses = repository.getRepository().login(CredentialHandler.getAdminCredentials(context));

			authChecker.checkWriteAuthorizationControl(ses, destinationId, true);
			authChecker.checkWriteAuthorizationControl(ses, id, false);

			final Node nodeToMove = ses.getNodeByIdentifier(id);
			final Node destination = ses.getNodeByIdentifier(destinationId);
			Node originalParent = nodeToMove.getParent();
			
			Item destinationItem = node2Item.getItem(destination,null);
			final Item item = node2Item.getItem(nodeToMove, null);

			if (item instanceof SharedFolder)
				throw new InvalidItemException("shared folder cannot be moved");

			if (Constants.FOLDERS_TO_EXLUDE.contains(item.getTitle()) || Constants.FOLDERS_TO_EXLUDE.contains(destinationItem.getTitle()))
				throw new InvalidItemException("protected folder cannot be moved");

			if (!(destinationItem instanceof FolderItem))
				throw new InvalidItemException("destination item is not a folder");
			
			ses.getWorkspace().getLockManager().lock(destination.getPath(), false, true, 0,login);
			ses.getWorkspace().getLockManager().lock(nodeToMove.getPath(), true, true, 0,login);
			try {
				String uniqueName =(Utils.checkExistanceAndGetUniqueName(ses, destination, nodeToMove.getName()));
				String newPath = String.format("%s/%s",destination.getPath(), uniqueName);
				if (item instanceof FolderItem && Utils.hasSharedChildren(nodeToMove))
					throw new InvalidItemException("folder item with shared children cannot be moved");				

				ses.getWorkspace().move(nodeToMove.getPath(), newPath);
				Utils.setPropertyOnChangeNode(ses.getNode(newPath), login, ItemAction.MOVED);
				
				String mimeTypeForAccounting = (item instanceof AbstractFileItem)? ((AbstractFileItem) item).getContent().getMimeType(): null; 
				
				accountingHandler.createFolderAddObj(uniqueName, item.getClass().getSimpleName(), mimeTypeForAccounting , ses, destination, false);
				accountingHandler.createFolderRemoveObj(item.getTitle(), item.getClass().getSimpleName(), mimeTypeForAccounting, ses, originalParent, false);
				ses.save();
			}finally {
				ses.getWorkspace().getLockManager().unlock(nodeToMove.getPath());
				ses.getWorkspace().getLockManager().unlock(destination.getPath());
			}
			
		}catch(RepositoryException re ){
			log.error("jcr error moving item", re);
			GXOutboundErrorResponse.throwException(new BackendGenericError(re));
		}catch(StorageHubException she ){
			log.error("error moving item", she);
			GXOutboundErrorResponse.throwException(she);		
		} finally{
			if (ses!=null) {
				ses.logout();
			}
		}
		return id;
	}

	@PUT
	@Path("{id}/copy")
	public String copy(@FormParam("destinationId") String destinationId, @FormParam("fileName") String newFileName){
		InnerMethodName.instance.set("copy");
		//TODO: check if identifier is The Workspace root, or the trash folder or the VREFolder root or if the item is thrashed		
		Session ses = null;
		String newFileIdentifier = null;
		try{
			final String login = AuthorizationProvider.instance.get().getClient().getId();
			//ses = RepositoryInitializer.getRepository().login(new SimpleCredentials(login,Utils.getSecurePassword(login).toCharArray()));
			//TODO check if it is possible to change all the ACL on a workspace
			ses = repository.getRepository().login(CredentialHandler.getAdminCredentials(context));

			authChecker.checkWriteAuthorizationControl(ses, destinationId, true);
			authChecker.checkReadAuthorizationControl(ses, id);

			final Node nodeToCopy = ses.getNodeByIdentifier(id);
			final Node destination = ses.getNodeByIdentifier(destinationId);
			//Item destinationItem = node2Item.getItem(destination,null);

			final Item item = node2Item.getItem(nodeToCopy, Arrays.asList(NodeConstants.ACCOUNTING_NAME, NodeConstants.METADATA_NAME));

			if (item instanceof FolderItem)
				throw new InvalidItemException("folder cannot be copied");


			ses.getWorkspace().getLockManager().lock(destination.getPath(), false, true, 0,login);
			ses.getWorkspace().getLockManager().lock(nodeToCopy.getPath(), true, true, 0,login);
			try {
				String uniqueName = Utils.checkExistanceAndGetUniqueName(ses, destination, newFileName);				
				String newPath= String.format("%s/%s", destination.getPath(), uniqueName);
				ses.getWorkspace().copy(nodeToCopy.getPath(), newPath);
				Node newNode = ses.getNode(newPath);
				newFileIdentifier = newNode.getIdentifier();
				
				if (item instanceof AbstractFileItem) {
					String oldStorageId = ((AbstractFileItem)item).getContent().getStorageId();
					String newStorageID = Utils.getStorageClient(login).getClient().copyFile(true).from(oldStorageId).to(newPath);
					log.info("copying storage Id {} to newPath {} and the id returned by storage is {}", oldStorageId, newPath, newStorageID);					
					((AbstractFileItem) item).getContent().setStorageId(newStorageID);
					((AbstractFileItem) item).getContent().setRemotePath(newPath);
					item2Node.replaceContent(ses, newNode, (AbstractFileItem) item, ItemAction.CLONED);
				} 
					
				Utils.setPropertyOnChangeNode(newNode, login, ItemAction.CLONED);
				newNode.setProperty(NodeProperty.PORTAL_LOGIN.toString(), login);
				newNode.setProperty(NodeProperty.IS_PUBLIC.toString(), false);
				
				String mimeTypeForAccounting = (item instanceof AbstractFileItem)? ((AbstractFileItem) item).getContent().getMimeType(): null; 
				accountingHandler.createFolderAddObj(uniqueName, item.getClass().getSimpleName(), mimeTypeForAccounting, ses, destination, false);
				
				
				ses.save();

			}finally {
				ses.getWorkspace().getLockManager().unlock(nodeToCopy.getPath());
				ses.getWorkspace().getLockManager().unlock(destination.getPath());
			}

		}catch(RepositoryException re ){
			log.error("jcr error moving item", re);
			GXOutboundErrorResponse.throwException(new BackendGenericError(re));
		}catch(StorageHubException she ){
			log.error("error moving item", she);
			GXOutboundErrorResponse.throwException(she);		
		} finally{
			if (ses!=null) {
				ses.logout();
			}
		}
		return newFileIdentifier;
	}

	@PUT
	@Path("{id}/rename")
	public Response rename(@FormParam("newName") String newName){
		InnerMethodName.instance.set("rename");
		//TODO: check if identifier is The Workspace root, or the trash folder or the VREFolder root or if the item is thrashed		
		Session ses = null;

		try{
			final String login = AuthorizationProvider.instance.get().getClient().getId();

			ses = repository.getRepository().login(CredentialHandler.getAdminCredentials(context));

			authChecker.checkWriteAuthorizationControl(ses, id, false);

			final Node nodeToMove = ses.getNodeByIdentifier(id);

			final Item item = node2Item.getItem(nodeToMove, null);

			if (item instanceof SharedFolder)
				throw new InvalidItemException("shared folder");

			if (Constants.FOLDERS_TO_EXLUDE.contains(item.getTitle()))
				throw new InvalidItemException("protected folder cannot be renamed");


			ses.getWorkspace().getLockManager().lock(nodeToMove.getPath(), true, true, 0,login);
			ses.getWorkspace().getLockManager().lock(nodeToMove.getParent().getPath(), false, true, 0,login);
			try {
				String uniqueName = Utils.checkExistanceAndGetUniqueName(ses, nodeToMove.getParent(), newName);

				String newPath = String.format("%s/%s", nodeToMove.getParent().getPath(), uniqueName);
				nodeToMove.setProperty(NodeProperty.TITLE.toString(), uniqueName);
				Utils.setPropertyOnChangeNode(nodeToMove, login, ItemAction.RENAMED);
				ses.move(nodeToMove.getPath(), newPath);
				accountingHandler.createRename(item.getTitle(), uniqueName, ses.getNode(newPath), ses, false);
				ses.save();
			}finally {
				ses.getWorkspace().getLockManager().unlock(nodeToMove.getPath());
				ses.getWorkspace().getLockManager().unlock(nodeToMove.getParent().getPath());
			}
			
		}catch(RepositoryException re ){
			log.error("jcr error moving item", re);
			GXOutboundErrorResponse.throwException(new BackendGenericError(re));
		}catch(StorageHubException she ){
			log.error("error moving item", she);
			GXOutboundErrorResponse.throwException(she);		
		} finally{
			if (ses!=null) {
				ses.logout();
			}

		}
		return Response.ok(id).build();
	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/{id}/metadata")
	public Response setProperties(org.gcube.common.storagehub.model.Metadata metadata){
		InnerMethodName.instance.set("updateMetadata");

		Session ses = null;

		try{
			final String login = AuthorizationProvider.instance.get().getClient().getId();

			ses = repository.getRepository().login(CredentialHandler.getAdminCredentials(context));

			authChecker.checkWriteAuthorizationControl(ses, id, false);

			final Node nodeToUpdate = ses.getNodeByIdentifier(id);

			
			ses.getWorkspace().getLockManager().lock(nodeToUpdate.getPath(), false, true, 0,login);
			try {
				item2Node.updateMetadataNode(ses, nodeToUpdate, metadata.getValues(), login);
				ses.save();
			}finally {
				ses.getWorkspace().getLockManager().unlock(nodeToUpdate.getPath());
			}
			//TODO: UPDATE accounting
			
		}catch(RepositoryException re ){
			log.error("jcr error moving item", re);
			GXOutboundErrorResponse.throwException(new BackendGenericError(re));
		}catch(StorageHubException she ){
			log.error("error moving item", she);
			GXOutboundErrorResponse.throwException(she);		
		} finally{
			if (ses!=null) {
				ses.logout();
			}

		}
		return Response.ok(id).build();
	}

	

	@DELETE
	@Path("{id}")
	public Response deleteItem(){
		InnerMethodName.instance.set("deleteItem");
		//TODO: check if identifier is The Workspace root, or the trash folder or the VREFolder root	
		//TODO: check also that is not already trashed
		Session ses = null;
		try{

			log.info("removing node with id {}", id);

			//TODO check if it is possible to change all the ACL on a workspace
			ses = repository.getRepository().login(CredentialHandler.getAdminCredentials(context));

			authChecker.checkWriteAuthorizationControl(ses, id, false);

			final Node nodeToDelete = ses.getNodeByIdentifier(id);

			Item itemToDelete = node2Item.getItem(nodeToDelete, Excludes.GET_ONLY_CONTENT);

			if (itemToDelete instanceof SharedFolder || itemToDelete instanceof VreFolder || (itemToDelete instanceof FolderItem && Utils.hasSharedChildren(nodeToDelete)))
				throw new InvalidItemException("SharedFolder, VreFolder or folders with shared children cannot be deleted");

			log.debug("item is trashed? {}", itemToDelete.isTrashed());

			if (!itemToDelete.isTrashed())
				trashHandler.moveToTrash(ses, nodeToDelete, itemToDelete);
			else 
				trashHandler.removeNodes(ses, Collections.singletonList(itemToDelete));

		}catch(RepositoryException re ){
			log.error("jcr error moving item", re);
			GXOutboundErrorResponse.throwException(new BackendGenericError(re));
		}catch(StorageHubException she ){
			log.error("error moving item", she);
			GXOutboundErrorResponse.throwException(she);		
		} finally{
			if (ses!=null) {
				ses.logout();
			}
		}
		return Response.ok().build();
	}

	
}