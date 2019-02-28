package org.gcube.data.access.storagehub;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.version.Version;

import org.apache.commons.io.FilenameUtils;
import org.apache.jackrabbit.util.Text;
import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.storagehub.model.Excludes;
import org.gcube.common.storagehub.model.Paths;
import org.gcube.common.storagehub.model.exceptions.BackendGenericError;
import org.gcube.common.storagehub.model.items.AbstractFileItem;
import org.gcube.common.storagehub.model.items.FolderItem;
import org.gcube.common.storagehub.model.items.GCubeItem;
import org.gcube.common.storagehub.model.items.Item;
import org.gcube.common.storagehub.model.items.SharedFolder;
import org.gcube.common.storagehub.model.types.ItemAction;
import org.gcube.common.storagehub.model.types.NodeProperty;
import org.gcube.contentmanager.storageclient.wrapper.AccessType;
import org.gcube.contentmanager.storageclient.wrapper.MemoryType;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;
import org.gcube.data.access.storagehub.accounting.AccountingHandler;
import org.gcube.data.access.storagehub.handlers.Item2NodeConverter;
import org.gcube.data.access.storagehub.handlers.Node2ItemConverter;
import org.gcube.data.access.storagehub.handlers.VersionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils {

	public final static String SERVICE_NAME 				= "home-library";	
	public final static String SERVICE_CLASS 				= "org.gcube.portlets.user";
	private static final String FOLDERS_TYPE = "nthl:workspaceItem";

	private static final Logger logger = LoggerFactory.getLogger(Utils.class);

	public static String getSecurePassword(String user) throws Exception {
		String digest = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] hash = md.digest(user.getBytes("UTF-8"));

			//converting byte array to Hexadecimal String
			StringBuilder sb = new StringBuilder(2*hash.length);
			for(byte b : hash){
				sb.append(String.format("%02x", b&0xff));
			}
			digest = sb.toString();

		} catch (Exception e) {
			logger.error("error getting secure password",e);
		} 
		return digest;
	}

	public static long getItemCount(Node parent, boolean showHidden, Class<? extends Item> nodeType) throws RepositoryException, BackendGenericError{
		return getItemList(parent, Excludes.ALL, null, showHidden, nodeType).size();
	}





	public static <T extends Item> List<T> getItemList(Node parent, List<String> excludes, Range range, boolean showHidden, Class<? extends Item> nodeTypeToInclude) throws RepositoryException, BackendGenericError{

		logger.debug("getting children of node {}", parent.getIdentifier());
		
		List<T> returnList = new ArrayList<T>();
		long start = System.currentTimeMillis();
		NodeIterator iterator = parent.getNodes();
		logger.trace("time to get iterator {}",(System.currentTimeMillis()-start));
		logger.trace("nodeType is {}",nodeTypeToInclude);
		int count =0;
		logger.trace("selected range is {}", range);
		Node2ItemConverter node2Item= new Node2ItemConverter();
		while (iterator.hasNext()){
			Node current = iterator.nextNode();

			logger.debug("current node "+current.getName());
			
			if (isToExclude(current, showHidden))
				continue;
			
			logger.debug("current node not excluded "+current.getName());
			
			if (range==null || (count>=range.getStart() && returnList.size()<range.getLimit())) {
				T item = node2Item.getFilteredItem(current, excludes, nodeTypeToInclude);
				if (item==null) continue;
				returnList.add(item);
			} 
			count++;

		}
		return returnList;
	}

	private static boolean isToExclude(Node node, boolean showHidden) throws RepositoryException{
		return ((node.getName().startsWith("rep:") || (node.getName().startsWith("hl:"))) || 
				(!showHidden && node.hasProperty(NodeProperty.HIDDEN.toString()) && node.getProperty(NodeProperty.HIDDEN.toString()).getBoolean()) ||
				(node.getPrimaryNodeType().getName().equals(FOLDERS_TYPE) && Constants.FOLDERS_TO_EXLUDE.contains(node.getName())));
	}

	public static org.gcube.common.storagehub.model.Path getWorkspacePath(){
		return Paths.getPath(String.format("/Home/%s/Workspace",AuthorizationProvider.instance.get().getClient().getId()));
	}

	public static org.gcube.common.storagehub.model.Path getWorkspacePath(String login){
		return Paths.getPath(String.format("/Home/%s/Workspace",login));
	}

	public static org.gcube.common.storagehub.model.Path getHome(String login){
		return Paths.getPath(String.format("/Home/%s",login));
	}
	
	public static StorageClient getStorageClient(String login){
		return new StorageClient(SERVICE_CLASS, SERVICE_NAME, login, AccessType.SHARED, MemoryType.PERSISTENT);

	}

	public static Deque<Item> getAllNodesForZip(FolderItem directory,  Session session, AccountingHandler accountingHandler, List<String> excludes) throws RepositoryException, BackendGenericError{
		Deque<Item> queue = new LinkedList<Item>();
		Node currentNode = session.getNodeByIdentifier(directory.getId());
		queue.push(directory);
		Deque<Item> tempQueue = new LinkedList<Item>();
		logger.debug("adding directory {}",currentNode.getPath());
		for (Item item : Utils.getItemList(currentNode,Excludes.GET_ONLY_CONTENT, null, false, null)){
			if (excludes.contains(item.getId())) continue;
			if (item instanceof FolderItem) 
				tempQueue.addAll(getAllNodesForZip((FolderItem) item, session, accountingHandler, excludes));
			else if (item instanceof AbstractFileItem){
				logger.debug("adding file {}",item.getPath());
				AbstractFileItem fileItem = (AbstractFileItem) item;
				accountingHandler.createReadObj(fileItem.getTitle(), session, session.getNodeByIdentifier(item.getId()), false);
				queue.addLast(item);
			}
		}
		queue.addAll(tempQueue);
		return queue;
	}


	public static void zipNode(ZipOutputStream zos, Deque<Item> queue, String login, org.gcube.common.storagehub.model.Path originalPath) throws Exception{
		logger.trace("originalPath is {}",originalPath.toPath());
		org.gcube.common.storagehub.model.Path actualPath = Paths.getPath("");
		while (!queue.isEmpty()) {
			Item item = queue.pop();
			if (item instanceof FolderItem) {
				actualPath = Paths.getPath(item.getPath());
				logger.debug("actualPath is {}",actualPath.toPath());
				String name = Paths.remove(actualPath, originalPath).toPath().replaceFirst("/", "");
				logger.debug("writing dir {}",name);
				if (name.isEmpty()) continue;
				try {
					zos.putNextEntry(new ZipEntry(name));
				}finally {
					zos.closeEntry();
				}
			} else if (item instanceof AbstractFileItem){
				try {
					InputStream streamToWrite = Utils.getStorageClient(login).getClient().get().RFileAsInputStream(((AbstractFileItem)item).getContent().getStorageId());
					if (streamToWrite == null){
						logger.warn("discarding item {} ",item.getName());
						continue;
					}
					try(BufferedInputStream is = new BufferedInputStream(streamToWrite)){
						String name = (Paths.remove(actualPath, originalPath).toPath()+item.getName()).replaceFirst("/", "");
						logger.debug("writing file {}",name);
						zos.putNextEntry(new ZipEntry(name));
						copyStream(is, zos);
					}catch (Exception e) {
						logger.warn("error writing item {}", item.getName(),e);
					} finally{
						zos.closeEntry();
					}
					zos.flush();
				}catch (Throwable e) {
					logger.warn("error reading content for item {}", item.getPath(),e);
				}
			}
		}
		zos.close();
	}

	private static void copyStream(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[2048];
		int readcount = 0;
		while ((readcount=in.read(buffer))!=-1) {
			out.write(buffer, 0, readcount);
		}
	}


	public static boolean hasSharedChildren(Node node) throws RepositoryException, BackendGenericError{
		Node2ItemConverter node2Item = new Node2ItemConverter();
		NodeIterator children = node.getNodes();
		
		while (children.hasNext()) {
			Node child= children.nextNode();
			if (node2Item.checkNodeType(child, SharedFolder.class)) return true;
			if (node2Item.checkNodeType(child, FolderItem.class) && hasSharedChildren(child)) return true;
		}
		return false;
	}

	public static void getAllContentIds(Session ses, Set<String> idsToDelete, Item itemToDelete, VersionHandler versionHandler) throws Exception{
		if (itemToDelete instanceof AbstractFileItem) {
			List<Version> versions = versionHandler.getContentVersionHistory(ses.getNodeByIdentifier(itemToDelete.getId()), ses);

			versions.forEach(v -> {
				try {
					String storageId =v.getFrozenNode().getProperty(NodeProperty.STORAGE_ID.toString()).getString();
					idsToDelete.add(storageId);
					logger.info("retrieved StorageId {} for version {}", storageId, v.getName());
				} catch (Exception e) {
					logger.warn("error retreiving sotrageId version for item with id {}",itemToDelete.getId(),e);
				}
			});

			idsToDelete.add(((AbstractFileItem) itemToDelete).getContent().getStorageId());
		}else if (itemToDelete instanceof FolderItem) {
			List<Item> items = Utils.getItemList(ses.getNodeByIdentifier(itemToDelete.getId()), Excludes.GET_ONLY_CONTENT , null, true, null);
			for (Item item: items) 
				getAllContentIds(ses, idsToDelete, item, versionHandler);

		}

	}
	
	public static String checkExistanceAndGetUniqueName(Session ses, Node destination, String name) throws BackendGenericError{
		try {
			destination.getNode(name);
		}catch(PathNotFoundException pnf) {
			return  Text.escapeIllegalJcrChars(name);
		} catch (Exception e) {
			throw new BackendGenericError(e);
		}

		try {
			String filename = FilenameUtils.getBaseName(name);
			String ext = FilenameUtils.getExtension(name);

			String nameTocheck = ext.isEmpty()? String.format("%s(*)",filename): String.format("%s(*).%s",filename, ext);

			logger.debug("filename is {}, extension is {} , and name to check is {}", filename, ext, nameTocheck);

			NodeIterator ni = destination.getNodes(nameTocheck);
			int maxval = 0;
			while (ni.hasNext()) {
				Node n = ni.nextNode();
				int actual = Integer.parseInt(n.getName().replaceAll(String.format("%s\\((\\d*)\\).*", filename), "$1"));
				if (actual>maxval)
					maxval = actual;
			}


			String newName = ext.isEmpty()? String.format("%s(%d)", filename,maxval+1) : String.format("%s(%d).%s", filename,maxval+1, ext) ;
			return  Text.escapeIllegalJcrChars(newName);
		} catch (Exception e) {
			throw new BackendGenericError(e);
		}
	}
	
	public static Node createFolderInternally(Session ses, Node destinationNode, String name, String description, boolean hidden, String login, AccountingHandler accountingHandler) throws BackendGenericError {
		
		String uniqueName = Utils.checkExistanceAndGetUniqueName(ses, destinationNode, name);
		
		FolderItem item = new FolderItem();
		Calendar now = Calendar.getInstance();
		item.setName(uniqueName);
		item.setTitle(uniqueName);
		item.setDescription(description);
		//item.setCreationTime(now);
		item.setHidden(hidden);
		item.setLastAction(ItemAction.CREATED);
		item.setLastModificationTime(now);
		item.setLastModifiedBy(login);
		item.setOwner(login);
		item.setPublicItem(false);
		
		//to inherit hidden property
		//item.setHidden(destinationItem.isHidden());
		
		Node newNode = new Item2NodeConverter().getNode(ses, destinationNode, item);
		if (accountingHandler!=null)
			accountingHandler.createFolderAddObj(name, item.getClass().getSimpleName(), null, ses, newNode, false);
		return newNode;
	}
	
	public static Node createGcubeItemInternally(Session ses, Node destinationNode, String name, String description, String login, GCubeItem gcubeItem, AccountingHandler accountingHandler) throws BackendGenericError {
						
		Calendar now = Calendar.getInstance();
		gcubeItem.setName(name);
		gcubeItem.setTitle(name);
		gcubeItem.setDescription(description);
		//item.setCreationTime(now);
		gcubeItem.setHidden(false);
		gcubeItem.setLastAction(ItemAction.CREATED);
		gcubeItem.setLastModificationTime(now);
		gcubeItem.setLastModifiedBy(login);
		gcubeItem.setOwner(login);
		//to inherit hidden property
		//item.setHidden(destinationItem.isHidden());

		Node newNode = new Item2NodeConverter().getNode(ses, destinationNode, gcubeItem);
		//TODO: accounting for GCUBEITEM
		//accountingHandler.createFolderAddObj(name, item.getClass().getSimpleName(), null, ses, newNode, false);
		return newNode;
	}
	
	public static void setPropertyOnChangeNode(Node node, String login, ItemAction action) throws RepositoryException {
		node.setProperty(NodeProperty.LAST_MODIFIED.toString(), Calendar.getInstance());
		node.setProperty(NodeProperty.LAST_MODIFIED_BY.toString(), login);
		node.setProperty(NodeProperty.LAST_ACTION.toString(), action.name());
	}
}
