package org.gcube.data.access.storagehub.services;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.inject.Inject;
import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.Detector;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.gcube.common.authorization.library.AuthorizedTasks;
import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.gxrest.response.outbound.GXOutboundErrorResponse;
import org.gcube.common.storagehub.model.NodeConstants;
import org.gcube.common.storagehub.model.exceptions.BackendGenericError;
import org.gcube.common.storagehub.model.exceptions.IdNotFoundException;
import org.gcube.common.storagehub.model.exceptions.InvalidCallParameters;
import org.gcube.common.storagehub.model.exceptions.InvalidItemException;
import org.gcube.common.storagehub.model.exceptions.StorageHubException;
import org.gcube.common.storagehub.model.exceptions.UserNotAuthorizedException;
import org.gcube.common.storagehub.model.items.AbstractFileItem;
import org.gcube.common.storagehub.model.items.FolderItem;
import org.gcube.common.storagehub.model.items.GCubeItem;
import org.gcube.common.storagehub.model.types.ItemAction;
import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.data.access.storagehub.AuthorizationChecker;
import org.gcube.data.access.storagehub.MetaInfo;
import org.gcube.data.access.storagehub.MultipleOutputStream;
import org.gcube.data.access.storagehub.Utils;
import org.gcube.data.access.storagehub.accounting.AccountingHandler;
import org.gcube.data.access.storagehub.handlers.CredentialHandler;
import org.gcube.data.access.storagehub.handlers.Item2NodeConverter;
import org.gcube.data.access.storagehub.handlers.Node2ItemConverter;
import org.gcube.data.access.storagehub.handlers.VersionHandler;
import org.gcube.data.access.storagehub.handlers.content.ContentHandler;
import org.gcube.data.access.storagehub.handlers.content.ContentHandlerFactory;
import org.gcube.smartgears.utils.InnerMethodName;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Path("items")
public class ItemsCreator {

	private static final Logger log = LoggerFactory.getLogger(ItemsCreator.class);

	private static ExecutorService executor = Executors.newFixedThreadPool(100);

	@Context ServletContext context;

	@Inject 
	RepositoryInitializer repository;

	@Inject
	ContentHandlerFactory contenthandlerFactory;

	@Inject
	VersionHandler versionHandler;

	@Inject
	AuthorizationChecker authChecker;

	@Inject 
	AccountingHandler accountingHandler;

	@Inject Node2ItemConverter node2Item;
	@Inject Item2NodeConverter item2Node;
	

	//@Path("/{id}/create/{type:(?!FILE)[^/?$]*}")
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Path("/{id}/create/FOLDER")
	public String createFolder(@PathParam("id") String id, @FormParam("name") String name, @FormParam("description") String description, @FormParam("hidden") boolean hidden) {
		InnerMethodName.instance.set("createItem(FOLDER)");
		log.info("create folder item called");
		Session ses = null;
		String toReturn = null;
		try{
			final String login = AuthorizationProvider.instance.get().getClient().getId();
			long start = System.currentTimeMillis();

			ses = repository.getRepository().login(CredentialHandler.getAdminCredentials(context));

			log.info("time to connect to repo  {}",(System.currentTimeMillis()-start));
			
			Node destination;
			try {
				 destination = ses.getNodeByIdentifier(id);
			}catch(ItemNotFoundException inf) {
				throw new IdNotFoundException(id);
			}
				
			if (!node2Item.checkNodeType(destination, FolderItem.class)) 
				throw new InvalidItemException("the destination item is not a folder");
			
			authChecker.checkWriteAuthorizationControl(ses, destination.getIdentifier(), true);

			
			ses.getWorkspace().getLockManager().lock(destination.getPath(), false, true, 0,login);
			Node newNode;
			try {
				newNode = Utils.createFolderInternally(ses, destination, name, description, hidden,  login, accountingHandler);
				ses.save();
			} finally {
				ses.getWorkspace().getLockManager().unlock(destination.getPath());
			}
						
			log.info("item with id {} correctly created",newNode.getIdentifier());
			toReturn =  newNode.getIdentifier();
		}catch(StorageHubException she ){
			log.error("error creating item", she);
			GXOutboundErrorResponse.throwException(she);
		}catch(RepositoryException re ){
			log.error("jcr error creating item", re);
			GXOutboundErrorResponse.throwException(new BackendGenericError("jcr error creating item", re));
		}finally{
			if (ses!=null)
				ses.logout();
			
		}
		return toReturn;
	}

	@POST
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/{id}/create/GCUBEITEM")
	public String createGcubeItem(@PathParam("id") String id, GCubeItem item) {
		InnerMethodName.instance.set("createItem(GCUBEITEM)");
		log.info("create Gcube item called");
		Session ses = null;
		String toReturn = null;
		
		try{
			final String login = AuthorizationProvider.instance.get().getClient().getId();
			ses = repository.getRepository().login(CredentialHandler.getAdminCredentials(context));
			
			Node destination;
			try {
				 destination = ses.getNodeByIdentifier(id);
			}catch(ItemNotFoundException inf) {
				throw new IdNotFoundException(id);
			}
				
			if (!node2Item.checkNodeType(destination, FolderItem.class)) 
				throw new InvalidItemException("the destination item is not a folder");

			authChecker.checkWriteAuthorizationControl(ses, destination.getIdentifier(), true);

			ses.getWorkspace().getLockManager().lock(destination.getPath(), false, true, 0,login);
			
			Node newNode;
			try {
				newNode = Utils.createGcubeItemInternally(ses, destination, item.getName(), item.getDescription(), login, item, accountingHandler);
				ses.save();
			} finally {
				ses.getWorkspace().getLockManager().unlock(destination.getPath());
			}

			log.info("item with id {} correctly created",newNode.getIdentifier());
			toReturn =  newNode.getIdentifier();
		}catch(StorageHubException she ){
			log.error("error creating item", she);
			GXOutboundErrorResponse.throwException(she);
		}catch(RepositoryException re ){
			log.error("jcr error creating item", re);
			GXOutboundErrorResponse.throwException(new BackendGenericError("jcr error creating item", re));
		}finally{
			if (ses!=null)
				ses.logout();
		}
		return toReturn;
	}
	

	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/{id}/create/FILE")
	public String createFileItem(@PathParam("id") String id, @FormDataParam("name") String name,
			@FormDataParam("description") String description,
			@FormDataParam("file") InputStream stream,
			@FormDataParam("file") FormDataContentDisposition fileDetail){
		InnerMethodName.instance.set("createItem(FILE)");

		Session ses = null;
		String toReturn = null;
		try{
			if (name==null || name.trim().isEmpty() || description ==null) throw new InvalidCallParameters("name or description are null");
			final String login = AuthorizationProvider.instance.get().getClient().getId();

			ses = repository.getRepository().login(CredentialHandler.getAdminCredentials(context));

			Node destination = ses.getNodeByIdentifier(id);

			log.info("create file called with filename {} in dir {} ", name, destination.getPath() );

			if (!node2Item.checkNodeType(destination, FolderItem.class)) 
				throw new InvalidItemException("the destination item is not a folder");

			ses.getWorkspace().getLockManager().lock(destination.getPath(), false, true, 0,login);
			
			Node newNode;
			try {
				newNode = createFileItemInternally(ses, destination,  stream, name, description, login);
				ses.save();
			} finally {
				ses.getWorkspace().getLockManager().unlock(destination.getPath());
			}
			
			versionHandler.checkinContentNode(newNode, ses);
			log.info("file with id {} correctly created",newNode.getIdentifier());
			toReturn = newNode.getIdentifier(); 			
		}catch(RepositoryException re ){
			log.error("jcr error creating file item", re);
			GXOutboundErrorResponse.throwException(new BackendGenericError("jcr error creating file item", re));
		}catch(StorageHubException she ){
			log.error("error creating file item", she);
			GXOutboundErrorResponse.throwException(she);
		} finally{
			if (ses!=null)
				ses.logout();
		}
		return toReturn;
		
	}



	private Node createFileItemInternally(Session ses, Node destinationNode,  InputStream stream, String name, String description, String login) throws RepositoryException, UserNotAuthorizedException, BackendGenericError{

		ContentHandler handler = getContentHandler(stream , name, destinationNode.getPath(), login);

		AbstractFileItem item =handler.buildItem(name, description, login);

		//to inherit hidden property
		//item.setHidden(destinationItem.isHidden());

		log.debug("item prepared, fulfilling content");
		log.debug("content prepared");

		Node newNode;
		try {
			newNode = ses.getNode(org.gcube.common.storagehub.model.Paths.append(org.gcube.common.storagehub.model.Paths.getPath(destinationNode.getPath()), name).toPath());
			authChecker.checkWriteAuthorizationControl(ses, newNode.getIdentifier(), false);
			versionHandler.checkoutContentNode(newNode, ses);
			log.trace("replacing content of class {}",item.getContent().getClass());
			item2Node.replaceContent(ses, newNode,item, ItemAction.UPDATED);
		}catch(PathNotFoundException pnf) {
			log.info("creating new node");
			authChecker.checkWriteAuthorizationControl(ses, destinationNode.getIdentifier(), true);
			newNode = item2Node.getNode(ses, destinationNode, item);	
			versionHandler.makeVersionableContent(newNode, ses);
		}

		accountingHandler.createFolderAddObj(name, item.getClass().getSimpleName(), item.getContent().getMimeType(), ses, newNode, false);
		return newNode;
	}

	

	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{id}/create/ARCHIVE")
	public String uploadArchive(@PathParam("id") String id, @FormDataParam("parentFolderName") String parentFolderName,
			@FormDataParam("file") InputStream stream,
			@FormDataParam("file") FormDataContentDisposition fileDetail){
		InnerMethodName.instance.set("createItem(ARCHIVE)");

		Session ses = null;
		String toReturn = null;
		try{
			if (parentFolderName==null) throw new InvalidCallParameters("new folder name is null");

			final String login = AuthorizationProvider.instance.get().getClient().getId();

			ses = repository.getRepository().login(CredentialHandler.getAdminCredentials(context));

			Node destination = ses.getNodeByIdentifier(id);
			
			if (!node2Item.checkNodeType(destination, FolderItem.class)) 
				throw new InvalidItemException("the destination item is not a folder");
			
			authChecker.checkWriteAuthorizationControl(ses, destination.getIdentifier() , true);

			ses.getWorkspace().getLockManager().lock(destination.getPath(), false, true, 0,login);
			Node parentDirectoryNode = null;

			try {
				parentDirectoryNode = Utils.createFolderInternally(ses, destination, parentFolderName, "", false, login, accountingHandler);

				Set<Node> fileNodes = new HashSet<>();


				HashMap<String, Node> directoryNodeMap = new HashMap<>();			
							
				try (ArchiveInputStream input = new ArchiveStreamFactory()
					    .createArchiveInputStream(new BufferedInputStream(stream, 1024*64))){
					ArchiveEntry entry;
					while ((entry = input.getNextEntry()) != null) {
						if (entry.isDirectory()) {
							String entirePath = entry.getName();
							String name = entirePath.replaceAll("(.*/)*(.*)/", "$2");
							String parentPath = entirePath.replaceAll("(.*/)*(.*)/", "$1");
							log.debug("creating directory with entire path {}, name {}, parentPath {} ", entirePath, name, parentPath);
							Node createdNode;
							if (parentPath.isEmpty()) {
								createdNode = Utils.createFolderInternally(ses,  parentDirectoryNode, name, "", false, login, accountingHandler);
							}else { 
								Node parentNode = directoryNodeMap.get(parentPath);
								createdNode = Utils.createFolderInternally(ses, parentNode, name, "", false, login, accountingHandler);	
							}
							directoryNodeMap.put(entirePath, createdNode);
							continue;
						} else {
							try {
								String entirePath = entry.getName();
								String name = entirePath.replaceAll("(.*/)*(.*)", "$2");
								String parentPath = entirePath.replaceAll("(.*/)*(.*)", "$1");
								log.debug("creating file with entire path {}, name {}, parentPath {} ", entirePath, name, parentPath);
								Node fileNode = null;
								if (parentPath.isEmpty())
									fileNode = createFileItemInternally(ses, parentDirectoryNode, input, name, "", login);
								else { 
									Node parentNode = directoryNodeMap.get(parentPath);
									fileNode = createFileItemInternally(ses, parentNode, input, name, "", login);
								}
								fileNodes.add(fileNode);
							}catch(Exception e) {
								log.warn("error getting file {}",entry.getName(),e);
							}
						}
					}

				}

				ses.save();
				for (Node node : fileNodes)
					versionHandler.checkinContentNode(node, ses);
				toReturn = parentDirectoryNode.getIdentifier();

			} finally {
				try {
					if (ses.getWorkspace().getLockManager().isLocked(destination.getPath()))
						ses.getWorkspace().getLockManager().unlock(destination.getPath());
				} catch (Throwable t){
					log.warn("error unlocking {}", destination.getPath(), t);
				}
			}

		}catch(RepositoryException | ArchiveException | IOException re){
			log.error("jcr error extracting archive", re);
			GXOutboundErrorResponse.throwException(new BackendGenericError("jcr error extracting archive", re));
		}catch(StorageHubException she ){
			log.error("error creating file item", she);
			GXOutboundErrorResponse.throwException(she);
		} finally{
			if (ses!=null)
				ses.logout();

		}
		return toReturn;
	}


	private ContentHandler getContentHandler(InputStream stream , String name, String path, String login) throws BackendGenericError {

		
		final MultipleOutputStream mos;
		try{
			mos = new MultipleOutputStream(stream, 2);
		}catch (IOException e) {
			throw new BackendGenericError(e);
		}

		Callable<ContentHandler> mimeTypeDector = new Callable<ContentHandler>() {

			@Override
			public ContentHandler call() throws Exception {
				ContentHandler handler =null;
				long start = System.currentTimeMillis();
				log.debug("TIMING: reading the mimetype - start");
				try(InputStream is1 = new BufferedInputStream(mos.get(), 1024*64)){
					org.apache.tika.mime.MediaType mediaType = null;
					TikaConfig config = TikaConfig.getDefaultConfig();
					Detector detector = config.getDetector();
					TikaInputStream stream = TikaInputStream.get(is1);
					Metadata metadata = new Metadata();
					metadata.add(Metadata.RESOURCE_NAME_KEY, name);
					mediaType = detector.detect(stream, metadata);			 
					String mimeType = mediaType.getBaseType().toString();

					handler = contenthandlerFactory.create(mimeType);

					is1.reset();
					handler.initiliseSpecificContent(is1, name);
					handler.getContent().setMimeType(mimeType);
					log.trace("TIMING: reading the mimetype - finished in {}",System.currentTimeMillis()-start);
				} catch (Throwable e) {
					log.error("error retrieving mimeType",e);
					throw new RuntimeException(e);
				}	
				return handler;
			}

		};

		Callable<MetaInfo> uploader = new Callable<MetaInfo>() {

			@Override
			public MetaInfo call() throws Exception {
				try(InputStream is1 = mos.get()){
					String uid = UUID.randomUUID().toString();
					String remotePath= String.format("%s/%s-%s",path,uid,name);
					long start = System.currentTimeMillis();
					log.debug("TIMING: sending the content to Storage - start");
					IClient storageClient = Utils.getStorageClient(login).getClient();
					log.debug("TIMING: getting the client took {} ",System.currentTimeMillis()-start);
					String storageId =storageClient.put(true).LFile(is1).RFile(remotePath);
					log.debug("returned storage Id is {} for remotepath {}",storageId, remotePath);
					log.debug("TIMING: sending the file took {} ",System.currentTimeMillis()-start);
					long size = storageClient.getSize().RFileById(storageId);
					log.debug("TIMING: sending the content to Storage - finished in {}",System.currentTimeMillis()-start);
					MetaInfo info = new MetaInfo();
					info.setSize(size);
					info.setStorageId(storageId);
					info.setRemotePath(remotePath);
					return info;
				}catch (Throwable e) {
					log.error("error writing content");
					throw e;
				}

			}
		};

		Future<ContentHandler> detectorF = executor.submit(AuthorizedTasks.bind(mimeTypeDector));
		Future<MetaInfo> uploaderF = executor.submit(AuthorizedTasks.bind(uploader));

		long start = System.currentTimeMillis();
		log.debug("TIMING: writing the stream - start");	
		try {
			mos.startWriting();
			log.debug("TIMING: writing the stream - finished in {}",System.currentTimeMillis()-start);
			
			ContentHandler handler = detectorF.get();
			MetaInfo info = uploaderF.get();
			handler.getContent().setData(NodeConstants.CONTENT_NAME);
			handler.getContent().setStorageId(info.getStorageId());
			handler.getContent().setSize(info.getSize());
			handler.getContent().setRemotePath(info.getRemotePath());		
			return handler;
		}catch (Exception e) {
			throw new BackendGenericError(e);
		}

	}

}
