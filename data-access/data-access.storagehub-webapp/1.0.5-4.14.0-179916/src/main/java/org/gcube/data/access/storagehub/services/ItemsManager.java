package org.gcube.data.access.storagehub.services;

import static org.gcube.common.storagehub.model.Constants.enchriptedPrefix;
import static org.gcube.common.storagehub.model.Constants.versionPrefix;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.zip.Deflater;
import java.util.zip.ZipOutputStream;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.lock.LockException;
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
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.io.FilenameUtils;
import org.gcube.common.authorization.control.annotations.AuthorizationControl;
import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.gxrest.response.outbound.GXOutboundErrorResponse;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.common.scope.impl.ScopeBean.Type;
import org.gcube.common.storagehub.model.Excludes;
import org.gcube.common.storagehub.model.NodeConstants;
import org.gcube.common.storagehub.model.Paths;
import org.gcube.common.storagehub.model.exceptions.BackendGenericError;
import org.gcube.common.storagehub.model.exceptions.IdNotFoundException;
import org.gcube.common.storagehub.model.exceptions.InvalidCallParameters;
import org.gcube.common.storagehub.model.exceptions.InvalidItemException;
import org.gcube.common.storagehub.model.exceptions.ItemLockedException;
import org.gcube.common.storagehub.model.exceptions.StorageHubException;
import org.gcube.common.storagehub.model.exceptions.UserNotAuthorizedException;
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
import org.gcube.data.access.storagehub.exception.MyAuthException;
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
			Node node = ses.getNodeByIdentifier(id);
			authChecker.checkReadAuthorizationControl(ses, id);
			toReturn = node2Item.getItem(node, excludes);
		}catch (ItemNotFoundException e) {
			log.error("id {} not found",id,e);
			GXOutboundErrorResponse.throwException(new IdNotFoundException(id, e), Status.NOT_FOUND);
		}catch(RepositoryException re){
			log.error("jcr error getting item", re);
			GXOutboundErrorResponse.throwException(new BackendGenericError("jcr error searching item", re));
		}catch(StorageHubException she ){
			log.error(she.getErrorMessage(), she);
			GXOutboundErrorResponse.throwException(she, Response.Status.fromStatusCode(she.getStatus()));
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

			//NOT using the internal pattern matching of jcr because of title for shared folder
			NodeIterator it = ses.getNodeByIdentifier(id).getNodes();
			while (it.hasNext()) {
				Node child= it.nextNode();
				String nodeName = child.getName();
				if (!child.hasProperty(NodeProperty.TITLE.toString())) continue;
				String title = child.getProperty(NodeProperty.TITLE.toString()).getString();

				String cleanedName = name;
				if (name.startsWith("*")) cleanedName = name.substring(1);
				if (name.endsWith("*")) cleanedName = name.substring(0, name.length()-1);

				if ((name.startsWith("*") && (nodeName.endsWith(cleanedName) || title.endsWith(cleanedName)))  ||  (name.endsWith("*") && (nodeName.startsWith(cleanedName) || title.startsWith(cleanedName))) 
						|| (nodeName.equals(cleanedName) || title.equals(cleanedName)))
					toReturn.add(node2Item.getItem(child, excludes));
			}
		}catch (ItemNotFoundException e) {
			log.error("id {} not found",id,e);
			GXOutboundErrorResponse.throwException(new IdNotFoundException(id, e), Status.NOT_FOUND);
		}catch(RepositoryException re){
			log.error("jcr error searching item", re);
			GXOutboundErrorResponse.throwException(new BackendGenericError("jcr error searching item", re));
		}catch(StorageHubException she ){
			log.error(she.getErrorMessage(), she);
			GXOutboundErrorResponse.throwException(she, Response.Status.fromStatusCode(she.getStatus()));
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
		}catch (ItemNotFoundException e) {
			log.error("id {} not found",id,e);
			GXOutboundErrorResponse.throwException(new IdNotFoundException(id, e), Status.NOT_FOUND);
		}catch(RuntimeException | RepositoryException re){
			log.error("jcr error counting item", re);
			GXOutboundErrorResponse.throwException(new BackendGenericError(re));
		}catch(StorageHubException she ){
			log.error(she.getErrorMessage(), she);
			GXOutboundErrorResponse.throwException(she, Response.Status.fromStatusCode(she.getStatus()));
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
		}catch (ItemNotFoundException e) {
			log.error("id {} not found",id,e);
			GXOutboundErrorResponse.throwException(new IdNotFoundException(id, e), Status.NOT_FOUND);
		}catch(RepositoryException re){
			log.error("jcr error getting children", re);
			GXOutboundErrorResponse.throwException(new BackendGenericError(re));
		}catch(StorageHubException she ){
			log.error(she.getErrorMessage(), she);
			GXOutboundErrorResponse.throwException(she, Response.Status.fromStatusCode(she.getStatus()));
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
		}catch (ItemNotFoundException e) {
			log.error("id {} not found",id,e);
			GXOutboundErrorResponse.throwException(new IdNotFoundException(id, e), Status.NOT_FOUND);
		}catch(RepositoryException re){
			log.error("jcr error getting paged children", re);
			GXOutboundErrorResponse.throwException(new BackendGenericError(re));
		}catch(StorageHubException she ){
			log.error(she.getErrorMessage(), she);
			GXOutboundErrorResponse.throwException(she, Response.Status.fromStatusCode(she.getStatus()));
		}finally{
			if (ses!=null)
				ses.logout();
		}

		return new ItemList(toReturn);
	}

	@GET
	@Path("publiclink/{id}")
	@AuthorizationControl(allowed={"URIResolver"}, exception=MyAuthException.class)
	public Response resolvePublicLink() {
		InnerMethodName.instance.set("resolvePubliclink");

		log.warn("arrived id is {}",id);
		Session ses = null;
		try{
			String login = AuthorizationProvider.instance.get().getClient().getId();
			ses = repository.getRepository().login(CredentialHandler.getAdminCredentials(context));

			String complexId = id;

			if (id.startsWith(enchriptedPrefix)) {
				String currentScope =  ScopeProvider.instance.get();
				try {
					ScopeBean bean= new ScopeBean(currentScope);
					while (!bean.is(Type.INFRASTRUCTURE)) {
						bean = bean.enclosingScope();
					}

					ScopeProvider.instance.set(bean.toString());
					complexId = StringEncrypter.getEncrypter().decrypt(new String(Base64.getUrlDecoder().decode(id.replace(enchriptedPrefix, ""))));
				}catch(Exception e){
					throw new BackendGenericError("invalid public url",e);
				}finally {
					ScopeProvider.instance.set(currentScope);
				}

			}

			String itemId = complexId;
			String versionName = null;

			if (complexId.contains(versionPrefix)) {
				String[] split = complexId.split(versionPrefix);
				itemId = split[0];
				versionName = split[1];
			}

			log.warn("item id to retrieve is {}",itemId);

			Node selectedNode = ses.getNodeByIdentifier(itemId);

			Item item = node2Item.getItem(selectedNode, Arrays.asList(NodeConstants.ACCOUNTING_NAME, NodeConstants.METADATA_NAME));

			if (!(item instanceof AbstractFileItem)) throw new InvalidCallParameters("the choosen item is not a File");

			if (versionName!=null)
				return downloadVersionInternal(ses, login, itemId, versionName, false);
			else 
				return downloadFileInternal(ses, (AbstractFileItem) item, login, true);


		}catch(RepositoryException re ){
			log.error("jcr error getting public link", re);
			GXOutboundErrorResponse.throwException(new BackendGenericError(re));
		}catch(StorageHubException she ){
			log.error(she.getErrorMessage(), she);
			GXOutboundErrorResponse.throwException(she, Response.Status.fromStatusCode(she.getStatus()));
		}finally{
			if (ses!=null)
				ses.logout();
		}
		return Response.serverError().build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{id}/publiclink")
	public URL getPublicLink(@QueryParam("version") String version) {
		InnerMethodName.instance.set("getPubliclink");
		Session ses = null;
		URL toReturn = null;
		try{
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

			/* NOT SETTING THE PUBLIC ATTRIBUTE FOR SOME STRANGE REASON
			ses.getWorkspace().getLockManager().lock(selectedNode.getPath(), false, true, 0,login);
			try {
				selectedNode.setProperty(NodeProperty.IS_PUBLIC.toString(), true);	
				ses.save();
			}finally {
				ses.getWorkspace().getLockManager().unlock(selectedNode.getPath());
			}*/

			String url = null;
			String currentScope =  ScopeProvider.instance.get();
			try {
				ScopeBean bean= new ScopeBean(currentScope);
				while (!bean.is(Type.INFRASTRUCTURE)) {
					bean = bean.enclosingScope();
				}

				ScopeProvider.instance.set(bean.toString());

				String toEnchript;
				if(version!=null) toEnchript = String.format("%s%s%s",id, versionPrefix, version);
				else toEnchript = id;

				String enchriptedQueryString = StringEncrypter.getEncrypter().encrypt(toEnchript);

				url = createPublicLink(new String(Base64.getUrlEncoder().encode(enchriptedQueryString.getBytes())));
			}catch(Exception e){
				throw new BackendGenericError(e);
			}finally {
				ScopeProvider.instance.set(currentScope);
			}

			toReturn = new URL(url);

		}catch(RepositoryException | MalformedURLException re ){
			log.error("jcr error getting public link", re);
			GXOutboundErrorResponse.throwException(new BackendGenericError(re));
		}catch(StorageHubException she ){
			log.error(she.getErrorMessage(), she);
			GXOutboundErrorResponse.throwException(she, Response.Status.fromStatusCode(she.getStatus()));
		}finally{
			if (ses!=null)
				ses.logout();
		}

		return toReturn;
	}


	private String createPublicLink(String enchriptedString) {
		String basepath = context.getInitParameter("resolver-basepath");
		String filePublicUrl = String.format("%s/%s%s",basepath, enchriptedPrefix, enchriptedString);
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
			log.trace("current node is {}",currentNode.getPath());

			Node sharedParentNode = getSharedParentNode(currentNode);

			if (sharedParentNode==null)
				throw new InvalidCallParameters("item is not shared");

			sharedParent = node2Item.getItem(sharedParentNode, excludes);

		}catch(RepositoryException re ){
			log.error("jcr error getting rootSharedFolder", re);
			GXOutboundErrorResponse.throwException(new BackendGenericError(re));
		}catch(StorageHubException she ){
			log.error(she.getErrorMessage(), she);
			GXOutboundErrorResponse.throwException(she, Response.Status.fromStatusCode(she.getStatus()));
		}finally{
			if (ses!=null)
				ses.logout();
		}
		return new ItemWrapper<Item>(sharedParent);
	}

	private Node getSharedParentNode(Node node) throws RepositoryException, BackendGenericError{
		Item currentItem = node2Item.getItem(node, Excludes.ALL);
		if (!currentItem.isShared())
			return null;
		Node currentNode = node;
		while (!node2Item.checkNodeType(currentNode, SharedFolder.class)) 
			currentNode = currentNode.getParent();
		return currentNode;
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
			log.error(she.getErrorMessage(), she);
			GXOutboundErrorResponse.throwException(she, Response.Status.fromStatusCode(she.getStatus()));
		}finally{
			if (ses!=null)
				ses.logout();
		}
		return new VersionList(versions);
	}

	@GET
	@Path("{id}/versions/{version}/download")
	public Response downloadVersion(@PathParam("version") String versionName){
		InnerMethodName.instance.set("downloadSpecificVersion");
		Session ses = null;
		try{
			String login = AuthorizationProvider.instance.get().getClient().getId();
			ses = repository.getRepository().login(CredentialHandler.getAdminCredentials(context));
			authChecker.checkReadAuthorizationControl(ses, id);

			return downloadVersionInternal(ses, login,  id, versionName, true);

		}catch(RepositoryException re ){
			log.error("jcr error downloading version", re);
			GXOutboundErrorResponse.throwException(new BackendGenericError(re));
		}catch(StorageHubException she ){
			log.error(she.getErrorMessage(), she);
			GXOutboundErrorResponse.throwException(she, Response.Status.fromStatusCode(she.getStatus()));
		}finally{
			if (ses!=null)
				ses.logout();
		}
		return Response.serverError().build();
	}

	private Response downloadVersionInternal(Session ses, String login, String id, String versionName, boolean withAccounting) throws RepositoryException, StorageHubException{
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

				if (withAccounting)
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
		throw new InvalidItemException("the version is not valid");
	}

	@GET
	@Path("{id}/anchestors")
	@Produces(MediaType.APPLICATION_JSON)
	public ItemList getAnchestors(@QueryParam("exclude") List<String> excludes){
		InnerMethodName.instance.set("getAnchestors");
		org.gcube.common.storagehub.model.Path absolutePath = Utils.getWorkspacePath();
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
					NodeIterator sharedSetIterator = currentNode.getSharedSet();
					while (sharedSetIterator.hasNext()) {
						Node sharedNode = sharedSetIterator.nextNode();
						if (sharedNode.getPath().startsWith(Utils.getWorkspacePath(login).toPath())) {
							currentNode = sharedNode.getParent();
							break;
						}
					}
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
			log.error(she.getErrorMessage(), she);
			GXOutboundErrorResponse.throwException(she, Response.Status.fromStatusCode(she.getStatus()));
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
				return downloadFileInternal(ses, (AbstractFileItem) item, login, true);
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

					accountingHandler.createReadObj(item.getTitle(), ses, ses.getNodeByIdentifier(item.getId()), false);
				}finally {
					if (ses!=null) ses.save();
				}
			} else throw new InvalidItemException("item type not supported for download: "+item.getClass());

		}catch(RepositoryException re ){
			log.error("jcr error download", re);
			GXOutboundErrorResponse.throwException(new BackendGenericError(re));
		}catch(StorageHubException she ){
			log.error(she.getErrorMessage(), she);
			GXOutboundErrorResponse.throwException(she, Response.Status.fromStatusCode(she.getStatus()));
		} finally{
			if (ses!=null) ses.logout();
		}

		return response;
	}

	private Response downloadFileInternal(Session ses, AbstractFileItem fileItem, String login, boolean withAccounting) throws RepositoryException {

		final InputStream streamToWrite = Utils.getStorageClient(login).getClient().get().RFileAsInputStream(fileItem.getContent().getStorageId());

		if (withAccounting)
			accountingHandler.createReadObj(fileItem.getTitle(), ses, ses.getNodeByIdentifier(fileItem.getId()), true);

		StreamingOutput so = new SingleFileStreamingOutput(streamToWrite);

		return Response
				.ok(so)
				.header("content-disposition","attachment; filename = "+fileItem.getName())
				.header("Content-Length", fileItem.getContent().getSize())
				.header("Content-Type", fileItem.getContent().getMimeType())
				.build();

	}


	@PUT
	@Path("{id}/move")
	public String move(@FormParam("destinationId") String destinationId){
		InnerMethodName.instance.set("move");

		Session ses = null;
		try{
			final String login = AuthorizationProvider.instance.get().getClient().getId();

			ses = repository.getRepository().login(CredentialHandler.getAdminCredentials(context));

			authChecker.checkMoveOpsForProtectedFolders(ses, id);	
			authChecker.checkWriteAuthorizationControl(ses, destinationId, true);
			authChecker.checkWriteAuthorizationControl(ses, id, false);

			final Node nodeToMove = ses.getNodeByIdentifier(id);
			final Node destination = ses.getNodeByIdentifier(destinationId);
			Node originalParent = nodeToMove.getParent();

			Item destinationItem = node2Item.getItem(destination,null);
			final Item item = node2Item.getItem(nodeToMove, null);

			if (item instanceof SharedFolder)
				throw new InvalidItemException("shared folder cannot be moved");

			if (item instanceof FolderItem && Utils.hasSharedChildren(nodeToMove))
				throw new InvalidItemException("folder item with shared children cannot be moved");	

			if (Constants.FOLDERS_TO_EXLUDE.contains(item.getTitle()) || Constants.FOLDERS_TO_EXLUDE.contains(destinationItem.getTitle()))
				throw new InvalidItemException("protected folder cannot be moved");

			if (!(destinationItem instanceof FolderItem))
				throw new InvalidItemException("destination item is not a folder");

			if (item.isShared() && (!destinationItem.isShared() || !getSharedParentNode(nodeToMove).getIdentifier().equals(getSharedParentNode(destination).getIdentifier())))
				throw new InvalidCallParameters("shared Item cannot be moved in a different shared folder or in a private folder");

			try {
				ses.getWorkspace().getLockManager().lock(destination.getPath(), false, true, 0,login);
				ses.getWorkspace().getLockManager().lock(nodeToMove.getPath(), true, true, 0,login);
			}catch (LockException e) {
				throw new ItemLockedException(e);
			}
			try {
				String uniqueName =(Utils.checkExistanceAndGetUniqueName(ses, destination, nodeToMove.getName()));
				String newPath = String.format("%s/%s",destination.getPath(), uniqueName);

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
			log.error(she.getErrorMessage(), she);
			GXOutboundErrorResponse.throwException(she, Response.Status.fromStatusCode(she.getStatus()));
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

			try {
				ses.getWorkspace().getLockManager().lock(destination.getPath(), false, true, 0,login);
				ses.getWorkspace().getLockManager().lock(nodeToCopy.getPath(), true, true, 0,login);
			}catch (LockException e) {
				throw new ItemLockedException(e);
			}
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
					item2Node.replaceContent(newNode, (AbstractFileItem) item, ItemAction.CLONED);
				} 

				Utils.setPropertyOnChangeNode(newNode, login, ItemAction.CLONED);
				newNode.setProperty(NodeProperty.PORTAL_LOGIN.toString(), login);
				newNode.setProperty(NodeProperty.IS_PUBLIC.toString(), false);
				newNode.setProperty(NodeProperty.TITLE.toString(), uniqueName);

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
			log.error(she.getErrorMessage(), she);
			GXOutboundErrorResponse.throwException(she, Response.Status.fromStatusCode(she.getStatus()));
		}finally{
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
		Session ses = null;

		try{
			final String login = AuthorizationProvider.instance.get().getClient().getId();

			ses = repository.getRepository().login(CredentialHandler.getAdminCredentials(context));

			authChecker.checkMoveOpsForProtectedFolders(ses, id);	
			authChecker.checkWriteAuthorizationControl(ses, id, false);

			final Node nodeToMove = ses.getNodeByIdentifier(id);

			final Item item = node2Item.getItem(nodeToMove, null);

			if (item instanceof SharedFolder)
				throw new InvalidItemException("shared folder");

			if (Constants.FOLDERS_TO_EXLUDE.contains(item.getTitle()))
				throw new InvalidItemException("protected folder cannot be renamed");


			try {
				ses.getWorkspace().getLockManager().lock(nodeToMove.getPath(), true, true, 0,login);
				ses.getWorkspace().getLockManager().lock(nodeToMove.getParent().getPath(), false, true, 0,login);
			}catch (LockException e) {
				throw new ItemLockedException(e);
			}

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
			log.error(she.getErrorMessage(), she);
			GXOutboundErrorResponse.throwException(she, Response.Status.fromStatusCode(she.getStatus()));
		}finally{
			if (ses!=null) {
				ses.logout();
			}

		}
		return Response.ok(id).build();
	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/{id}/metadata")
	public Response setMetadata(org.gcube.common.storagehub.model.Metadata metadata){
		InnerMethodName.instance.set("updateMetadata");

		Session ses = null;

		try{
			final String login = AuthorizationProvider.instance.get().getClient().getId();

			ses = repository.getRepository().login(CredentialHandler.getAdminCredentials(context));

			authChecker.checkWriteAuthorizationControl(ses, id, false);

			final Node nodeToUpdate = ses.getNodeByIdentifier(id);

			try {
				ses.getWorkspace().getLockManager().lock(nodeToUpdate.getPath(), false, true, 0,login);
			}catch (LockException e) {
				throw new ItemLockedException(e);
			}
			try {
				item2Node.updateMetadataNode(nodeToUpdate, metadata.getMap(), login);
				ses.save();
			}finally {
				ses.getWorkspace().getLockManager().unlock(nodeToUpdate.getPath());
			}
			//TODO: UPDATE accounting

		}catch(RepositoryException re ){
			log.error("jcr error moving item", re);
			GXOutboundErrorResponse.throwException(new BackendGenericError(re));
		}catch(StorageHubException she ){
			log.error(she.getErrorMessage(), she);
			GXOutboundErrorResponse.throwException(she, Response.Status.fromStatusCode(she.getStatus()));
		}finally{
			if (ses!=null) {
				ses.logout();
			}

		}
		return Response.ok(id).build();
	}



	@DELETE
	@Path("{id}")
	public Response deleteItem(@QueryParam("force") boolean force){
		InnerMethodName.instance.set("deleteItem("+force+")");
		
		Session ses = null;
		try{

			log.info("removing node with id {}", id);

			//TODO check if it is possible to change all the ACL on a workspace
			ses = repository.getRepository().login(CredentialHandler.getAdminCredentials(context));
			authChecker.checkMoveOpsForProtectedFolders(ses, id);		
			authChecker.checkWriteAuthorizationControl(ses, id, false);

			final Node nodeToDelete = ses.getNodeByIdentifier(id);

			Item itemToDelete = node2Item.getItem(nodeToDelete, Excludes.GET_ONLY_CONTENT);

			if (itemToDelete instanceof SharedFolder || itemToDelete instanceof VreFolder || (itemToDelete instanceof FolderItem && Utils.hasSharedChildren(nodeToDelete)))
				throw new InvalidItemException("SharedFolder, VreFolder or folders with shared children cannot be deleted");

			log.debug("item is trashed? {}", itemToDelete.isTrashed());

			if (!itemToDelete.isTrashed() && !force)
				trashHandler.moveToTrash(ses, nodeToDelete, itemToDelete);
			else 
				trashHandler.removeNodes(ses, Collections.singletonList(itemToDelete));

		}catch (LockException e) {
			
		}catch(RepositoryException re ){
			log.error("jcr error moving item", re);
			GXOutboundErrorResponse.throwException(new BackendGenericError(re));
		}catch(StorageHubException she ){
			log.error(she.getErrorMessage(), she);
			GXOutboundErrorResponse.throwException(she, Response.Status.fromStatusCode(she.getStatus()));
		}finally{
			if (ses!=null) {
				ses.logout();
			}
		}
		return Response.ok().build();
	}


}