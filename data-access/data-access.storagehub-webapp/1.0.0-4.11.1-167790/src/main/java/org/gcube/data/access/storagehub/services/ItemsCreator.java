package org.gcube.data.access.storagehub.services;

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
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
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
import javax.ws.rs.core.UriInfo;

import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.Detector;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.authorization.library.provider.CalledMethodProvider;
import org.gcube.common.storagehub.model.items.AbstractFileItem;
import org.gcube.common.storagehub.model.items.FolderItem;
import org.gcube.common.storagehub.model.items.Item;
import org.gcube.common.storagehub.model.service.ItemWrapper;
import org.gcube.common.storagehub.model.types.ItemAction;
import org.gcube.data.access.storagehub.AuthorizationChecker;
import org.gcube.data.access.storagehub.Constants;
import org.gcube.data.access.storagehub.MetaInfo;
import org.gcube.data.access.storagehub.MultipleOutputStream;
import org.gcube.data.access.storagehub.Utils;
import org.gcube.data.access.storagehub.accounting.AccountingHandler;
import org.gcube.data.access.storagehub.handlers.ItemHandler;
import org.gcube.data.access.storagehub.handlers.VersionHandler;
import org.gcube.data.access.storagehub.handlers.content.ContentHandler;
import org.gcube.data.access.storagehub.handlers.content.ContentHandlerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Path("item")
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
	@Path("/{id}/create/{type:(?!FILE)[^/?$]*}")
	public Response createItem(@Context UriInfo uriInfo, @PathParam("id") String id, @PathParam("type") String type,@QueryParam("name") String name, @QueryParam("description") String description){
		CalledMethodProvider.instance.set(String.format("createItem(%s)",type));
		log.info("create generic item called");
		Session ses = null;
		Item destinationItem = null;
		try{
			final String login = AuthorizationProvider.instance.get().getClient().getId();
			long start = System.currentTimeMillis();

			//TODO check if it is possible to change all the ACL on a workspace
			ses = repository.getRepository().login(new SimpleCredentials(context.getInitParameter(Constants.ADMIN_PARAM_NAME),context.getInitParameter(Constants.ADMIN_PARAM_PWD).toCharArray()));

			//validate input parameters for Item Type

			if(!type.equals("FOLDER")) throw new IllegalAccessException("invalid item type");
			
			log.info("time to connect to repo  {}",(System.currentTimeMillis()-start));
			Node destination = ses.getNodeByIdentifier(id);
			destinationItem = ItemHandler.getItem(destination,Arrays.asList("hl:accounting","jcr:content"));
						
			if (!(destinationItem instanceof FolderItem)) throw new Exception("an Item must be created into a directory");

			authChecker.checkWriteAuthorizationControl(ses, destinationItem.getId());
		
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
			item.setHidden(destinationItem.isHidden());
			
			log.debug("item prepared, fulfilling content");

			log.debug("content prepared");
			Node newnode = ItemHandler.createNodeFromItem(ses, destination, item);
			accountingHandler.createFolderAddObj(name, type, null, ses, newnode, false);
			ses.save();
			log.info("item correctly created");
			return Response.ok(new ItemWrapper<>(item)).build();

		}catch(Exception e){
			log.error("error creating item", e);
			throw new WebApplicationException(e);
		} finally{
			if (ses!=null){
				if (destinationItem!=null)
					try {
						ses.getWorkspace().getLockManager().unlock(destinationItem.getPath());
					} catch (Throwable t){
						log.warn("error unlocking {}", destinationItem.getPath(), t);
					}
				ses.logout();
			}
		}
	}



	@POST
	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{id}/create/FILE")
	public Response createFileItem(InputStream stream , @PathParam("id") String id, 
			@QueryParam("name") String name, @QueryParam("description") String description){
		CalledMethodProvider.instance.set(String.format("createItem(FILE)"));
		log.info("create file called");
		Session ses = null;
		Item destinationItem = null;
		try{
			final String login = AuthorizationProvider.instance.get().getClient().getId();
			long start = System.currentTimeMillis();

			//TODO check if it is possible to change all the ACL on a workspace
			ses = repository.getRepository().login(new SimpleCredentials(context.getInitParameter(Constants.ADMIN_PARAM_NAME),context.getInitParameter(Constants.ADMIN_PARAM_PWD).toCharArray()));

			//TODO: validate input parameters for Item Type
			log.info("time to connect to repo  {}",(System.currentTimeMillis()-start));
			Node destination = ses.getNodeByIdentifier(id);
			destinationItem = ItemHandler.getItem(destination,Arrays.asList("hl:accounting","jcr:content"));
			log.debug("destination item path is {}",destinationItem.getPath());
			if (!(destinationItem instanceof FolderItem)) throw new Exception("an Item must be copyed to another directory");
			authChecker.checkWriteAuthorizationControl(ses, destinationItem.getId());
			ses.getWorkspace().getLockManager().lock(destinationItem.getPath(), true, true, 0,login);
			
			ContentHandler handler = getContentHandler(stream , name, destinationItem.getPath());

			AbstractFileItem item =handler.buildItem(name, description, login);
			
			//to inherit hidden property
			item.setHidden(destinationItem.isHidden());
			
			log.debug("item prepared, fulfilling content");

			log.debug("content prepared");
			Node newNode = ItemHandler.createNodeFromItem(ses, destination, item);						
			accountingHandler.createFolderAddObj(name, "FILE", item.getContent().getMimeType(), ses, newNode, false);
			versionHandler.makeVersionableContent(newNode, ses);
			ses.save();
			versionHandler.checkinContentNode(newNode, ses);;
			log.info("item correctly created");
			return Response.ok(new ItemWrapper<>(item)).build();
		}catch(Throwable e){
			log.error("error creating item", e);
			return Response.serverError().build();
		} finally{
			if (ses!=null){
				if (destinationItem!=null)
					try {
						ses.getWorkspace().getLockManager().unlock(destinationItem.getPath());
					} catch (Throwable t){
						log.warn("error unlocking {}", destinationItem.getPath(), t);
					}
				ses.logout();
			}
		}

	}


	private ContentHandler getContentHandler(InputStream stream , String name, String path) throws Exception {

		final MultipleOutputStream mos = new MultipleOutputStream(stream, 2);

		Callable<ContentHandler> mimeTypeDector = new Callable<ContentHandler>() {

			@Override
			public ContentHandler call() throws Exception {
				ContentHandler handler =null;
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
					
				} catch (Throwable e) {
					log.error("error retreiving content",e);
					throw new RuntimeException(e);
				}	
				return handler;
			}

		};

		Callable<MetaInfo> uploader = new Callable<MetaInfo>() {

			@Override
			public MetaInfo call() throws Exception {
				String remotePath= path+"/"+name;
				String storageId = Utils.getStorageClient(AuthorizationProvider.instance.get().getClient().getId()).getClient().put(true).LFile(mos.get()).RFile(remotePath);
				long size = Utils.getStorageClient(AuthorizationProvider.instance.get().getClient().getId()).getClient().getSize().RFileById(storageId);
				MetaInfo info = new MetaInfo();
				info.setSize(size);
				info.setStorageId(storageId);
				return info;
			}
		};

		Future<ContentHandler> detectorF = executor.submit(mimeTypeDector);
		Future<MetaInfo> uploaderF = executor.submit(uploader);
		
		mos.startWriting();
		
		ContentHandler handler = detectorF.get();
		handler.getContent().setData("jcr:content");
		handler.getContent().setStorageId(uploaderF.get().getStorageId());
		handler.getContent().setSize(uploaderF.get().getSize());
				
		return handler;

	}

}
