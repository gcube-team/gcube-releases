package org.gcube.data.access.storagehub.services;

import static org.gcube.common.storagehub.model.NodeConstants.ACCOUNTING_NAME;
import static org.gcube.common.storagehub.model.NodeConstants.CONTENT_NAME;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Session;
import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.Detector;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.storagehub.model.items.AbstractFileItem;
import org.gcube.common.storagehub.model.items.FolderItem;
import org.gcube.common.storagehub.model.items.Item;
import org.gcube.common.storagehub.model.types.ItemAction;
import org.gcube.data.access.storagehub.AuthorizationChecker;
import org.gcube.data.access.storagehub.MetaInfo;
import org.gcube.data.access.storagehub.MultipleOutputStream;
import org.gcube.data.access.storagehub.Utils;
import org.gcube.data.access.storagehub.accounting.AccountingHandler;
import org.gcube.data.access.storagehub.handlers.CredentialHandler;
import org.gcube.data.access.storagehub.handlers.ItemHandler;
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


	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	//@Path("/{id}/create/{type:(?!FILE)[^/?$]*}")
	@Path("/{id}/create/FOLDER")
	public Response createItem(@PathParam("id") String id, @PathParam("type") String type,@QueryParam("name") String name, @QueryParam("description") String description){
		InnerMethodName.instance.set(String.format("createItem(%s)",type));
		log.info("create generic item called");
		Session ses = null;
		Item destinationItem = null;
		try{
			final String login = AuthorizationProvider.instance.get().getClient().getId();
			long start = System.currentTimeMillis();

			//TODO check if it is possible to change all the ACL on a workspace
			ses = repository.getRepository().login(CredentialHandler.getAdminCredentials(context));

			//validate input parameters for Item Type

			//if(!type.equals("FOLDER")) throw new IllegalAccessException("invalid item type");

			log.info("time to connect to repo  {}",(System.currentTimeMillis()-start));
			Node destination = ses.getNodeByIdentifier(id);
			destinationItem = ItemHandler.getItem(destination,Arrays.asList(ACCOUNTING_NAME,CONTENT_NAME));

			if (!(destinationItem instanceof FolderItem)) throw new Exception("an Item must be created into a directory");

			authChecker.checkWriteAuthorizationControl(ses, destinationItem.getId(), true);

			ses.getWorkspace().getLockManager().lock(destinationItem.getPath(), true, true, 0,login);

			FolderItem item = new FolderItem();
			Calendar now = Calendar.getInstance();
			item.setName(name);
			item.setTitle(name);
			item.setDescription(description);
			//item.setCreationTime(now);
			item.setHidden(false);
			item.setLastAction(ItemAction.CREATED);
			item.setLastModificationTime(now);
			item.setLastModifiedBy(login);
			item.setOwner(login);

			//to inherit hidden property
			//item.setHidden(destinationItem.isHidden());

			log.debug("item prepared, fulfilling content");

			log.debug("content prepared");
			Node newNode = ItemHandler.createNodeFromItem(ses, destination, item);
			accountingHandler.createFolderAddObj(name, item.getClass().getSimpleName(), null, ses, newNode, false);
			ses.save();
			log.info("item with id {} correctly created",newNode.getIdentifier());
			return Response.ok(newNode.getIdentifier()).build();
		}catch(Exception e){
			log.error("error creating item", e);
			throw new WebApplicationException(e);
		} finally{
			if (ses!=null){
				if (destinationItem!=null)
					try {
						if (ses.getWorkspace().getLockManager().isLocked(destinationItem.getPath()))
							ses.getWorkspace().getLockManager().unlock(destinationItem.getPath());
					} catch (Throwable t){
						log.warn("error unlocking {}", destinationItem.getPath(), t);
					}
				ses.logout();
			}
		}
	}



	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{id}/create/FILE")
	public Response createFileItem(@PathParam("id") String id, @FormDataParam("name") String name,
			@FormDataParam("description") String description,
			@FormDataParam("file") InputStream stream,
			@FormDataParam("file") FormDataContentDisposition fileDetail){
		InnerMethodName.instance.set("createItem(FILE)");
		
		Session ses = null;
		Item destinationItem = null;
		try{
			if (name==null || name.trim().isEmpty() || description ==null) throw new Exception("name or description are null");
			final String login = AuthorizationProvider.instance.get().getClient().getId();

			//TODO check if it is possible to change all the ACL on a workspace
			ses = repository.getRepository().login(CredentialHandler.getAdminCredentials(context));

			//TODO: validate input parameters for Item Type
			Node destination = ses.getNodeByIdentifier(id);

			log.info("create file called with filename {} in dir {} ", name, destination.getPath() );

			destinationItem = ItemHandler.getItem(destination,Arrays.asList(ACCOUNTING_NAME,CONTENT_NAME));
			if (!(destinationItem instanceof FolderItem)) throw new Exception("an Item must be copyed to another directory");

			ses.getWorkspace().getLockManager().lock(destinationItem.getPath(), true, true, 0,login);

			ContentHandler handler = getContentHandler(stream , name, destinationItem.getPath());

			AbstractFileItem item =handler.buildItem(name, description, login);

			//to inherit hidden property
			//item.setHidden(destinationItem.isHidden());

			log.debug("item prepared, fulfilling content");
			log.debug("content prepared");

			Node newNode;
			try {
				newNode = ses.getNode(org.gcube.common.storagehub.model.Paths.append(org.gcube.common.storagehub.model.Paths.getPath(destinationItem.getPath()), name).toPath());
				authChecker.checkWriteAuthorizationControl(ses, newNode.getIdentifier(), false);
				versionHandler.checkoutContentNode(newNode, ses);
				log.trace("replacing content of class {}",item.getContent().getClass());
				ItemHandler.replaceContent(ses, newNode,item);
			}catch(PathNotFoundException pnf) {
				log.info("creating new node");
				authChecker.checkWriteAuthorizationControl(ses, destinationItem.getId(), true);
				newNode = ItemHandler.createNodeFromItem(ses, destination, item);	
				versionHandler.makeVersionableContent(newNode, ses);
			}

			accountingHandler.createFolderAddObj(name, item.getClass().getSimpleName(), item.getContent().getMimeType(), ses, newNode, false);

			ses.save();
			versionHandler.checkinContentNode(newNode, ses);
			log.info("file with id {} correctly created",newNode.getIdentifier());
			return Response.ok(newNode.getIdentifier()).build();
		}catch(Throwable e){
			log.error("error creating item", e);
			return Response.serverError().build();
		} finally{
			if (ses!=null){
				if (destinationItem!=null)
					try {
						if (ses.getWorkspace().getLockManager().isLocked(destinationItem.getPath()))
							ses.getWorkspace().getLockManager().unlock(destinationItem.getPath());
					} catch (Throwable t){
						log.warn("error unlocking {}", destinationItem.getPath(), t);
					}
				ses.logout();
			}
		}

	}

	
	/*
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{id}/create/ARCHIVE")
	public Response uploadArchive(@PathParam("id") String id, @FormDataParam("folderName") String folderName,
			@FormDataParam("file") InputStream stream,
			@FormDataParam("file") FormDataContentDisposition fileDetail){
		InnerMethodName.instance.set("createItem(FILE)");
		
		Session ses = null;
		Item destinationItem = null;
		try{
			if (folderName==null) throw new Exception("new folder name is null");
			final String login = AuthorizationProvider.instance.get().getClient().getId();

			//TODO check if it is possible to change all the ACL on a workspace
			ses = repository.getRepository().login(CredentialHandler.getAdminCredentials(context));

			//TODO: validate input parameters for Item Type
			Node destination = ses.getNodeByIdentifier(id);

			destinationItem = ItemHandler.getItem(destination,Arrays.asList(ACCOUNTING_NAME,CONTENT_NAME));
			if (!(destinationItem instanceof FolderItem)) throw new Exception("destination item is not a folder");

			ses.getWorkspace().getLockManager().lock(destinationItem.getPath(), true, true, 0,login);

			ContentHandler handler = getContentHandler(stream , name, destinationItem.getPath());

			AbstractFileItem item =handler.buildItem(name, description, login);

			//to inherit hidden property
			//item.setHidden(destinationItem.isHidden());

			log.debug("item prepared, fulfilling content");
			log.debug("content prepared");

			Node newNode;
			try {
				newNode = ses.getNode(org.gcube.common.storagehub.model.Paths.append(org.gcube.common.storagehub.model.Paths.getPath(destinationItem.getPath()), name).toPath());
				authChecker.checkWriteAuthorizationControl(ses, newNode.getIdentifier(), false);
				versionHandler.checkoutContentNode(newNode, ses);
				log.trace("replacing content of class {}",item.getContent().getClass());
				ItemHandler.replaceContent(ses, newNode,item);
			}catch(PathNotFoundException pnf) {
				log.info("creating new node");
				authChecker.checkWriteAuthorizationControl(ses, destinationItem.getId(), true);
				newNode = ItemHandler.createNodeFromItem(ses, destination, item);	
				versionHandler.makeVersionableContent(newNode, ses);
			}

			accountingHandler.createFolderAddObj(name, item.getClass().getSimpleName(), item.getContent().getMimeType(), ses, newNode, false);

			ses.save();
			versionHandler.checkinContentNode(newNode, ses);
			log.info("file with id {} correctly created",newNode.getIdentifier());
			return Response.ok(newNode.getIdentifier()).build();
		}catch(Throwable e){
			log.error("error creating item", e);
			return Response.serverError().build();
		} finally{
			if (ses!=null){
				if (destinationItem!=null)
					try {
						if (ses.getWorkspace().getLockManager().isLocked(destinationItem.getPath()))
							ses.getWorkspace().getLockManager().unlock(destinationItem.getPath());
					} catch (Throwable t){
						log.warn("error unlocking {}", destinationItem.getPath(), t);
					}
				ses.logout();
			}
		}

	}
	*/

	private ContentHandler getContentHandler(InputStream stream , String name, String path) throws Exception {

		final MultipleOutputStream mos = new MultipleOutputStream(stream, 2);

		Callable<ContentHandler> mimeTypeDector = new Callable<ContentHandler>() {

			@Override
			public ContentHandler call() throws Exception {
				ContentHandler handler =null;
				long start = System.currentTimeMillis();
				log.debug("TIMING: reading the mimetype - start");
				try(BufferedInputStream is1 = new BufferedInputStream(mos.get(), 2048)){
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
					handler.initiliseSpecificContent(is1);
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
				try {
					long start = System.currentTimeMillis();
					log.debug("TIMING: sending the content to Storage - start");
					String remotePath= path+"/"+name;
					String storageId = Utils.getStorageClient(AuthorizationProvider.instance.get().getClient().getId()).getClient().put(true).LFile(mos.get()).RFile(remotePath);
					long size = Utils.getStorageClient(AuthorizationProvider.instance.get().getClient().getId()).getClient().getSize().RFileById(storageId);
					MetaInfo info = new MetaInfo();
					info.setSize(size);
					info.setStorageId(storageId);
					info.setRemotePath(remotePath);
					log.debug("TIMING: sending the content to Storage - finished in {}",System.currentTimeMillis()-start);
					return info;
				}catch (Throwable e) {
					log.error("error writing content");
					throw e;
				}

			}
		};

		Future<ContentHandler> detectorF = executor.submit(mimeTypeDector);
		Future<MetaInfo> uploaderF = executor.submit(uploader);
		
		long start = System.currentTimeMillis();
		log.debug("TIMING: writing the strem - start");	
		mos.startWriting();
		log.debug("TIMING: writing the stream - finished in {}",System.currentTimeMillis()-start);
		
		ContentHandler handler = detectorF.get();
		MetaInfo info = uploaderF.get();
		handler.getContent().setData("jcr:content");
		handler.getContent().setStorageId(info.getStorageId());
		handler.getContent().setSize(info.getSize());
		handler.getContent().setRemotePath(info.getRemotePath());		
		return handler;

	}

}
