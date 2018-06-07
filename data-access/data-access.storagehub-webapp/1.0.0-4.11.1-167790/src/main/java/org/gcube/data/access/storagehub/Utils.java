package org.gcube.data.access.storagehub;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;

import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.storagehub.model.Paths;
import org.gcube.common.storagehub.model.items.AbstractFileItem;
import org.gcube.common.storagehub.model.items.FolderItem;
import org.gcube.common.storagehub.model.items.Item;
import org.gcube.common.storagehub.model.types.NodeProperty;
import org.gcube.contentmanager.storageclient.wrapper.AccessType;
import org.gcube.contentmanager.storageclient.wrapper.MemoryType;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;
import org.gcube.data.access.storagehub.accounting.AccountingHandler;
import org.gcube.data.access.storagehub.handlers.ItemHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils {

	public final static String SERVICE_NAME 				= "home-library";	
	public final static String SERVICE_CLASS 				= "org.gcube.portlets.user";
	private static final String FOLDERS_TYPE = "nthl:workspaceItem";
	
	
	private static final List<String> FOLDERS_TO_EXLUDE = Arrays.asList(Constants.VRE_FOLDER_PARENT_NAME, Constants.TRASH_ROOT_FOLDER_NAME);
	
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
	
	public static long getItemCount(Node parent, boolean showHidden) throws Exception{
		NodeIterator iterator = parent.getNodes();
		long count=0;
		while (iterator.hasNext()){
			Node current = iterator.nextNode();
			
			if (isToExclude(current, showHidden))
				continue;
			
			count++;
		}
		return count;
	}
	
	
	
	
	
	public static <T extends Item> List<T> getItemList(Node parent, List<String> excludes, Range range, boolean showHidden) throws Exception{

		List<T> returnList = new ArrayList<T>();
		long start = System.currentTimeMillis();
		NodeIterator iterator = parent.getNodes();
		logger.trace("time to get iterator {}",(System.currentTimeMillis()-start));
		int count =0;
		logger.trace("selected range is {}", range);
		while (iterator.hasNext()){
			Node current = iterator.nextNode();
			
			if (isToExclude(current, showHidden))
				continue;
			
			if (range==null || (count>=range.getStart() && returnList.size()<range.getLimit())) {
				T item = ItemHandler.getItem(current, excludes);
				returnList.add(item);
			} 
			count++;
		}
		return returnList;
	}
	
	private static boolean isToExclude(Node node, boolean showHidden) throws Exception{
		return ((node.getName().startsWith("rep:") || (node.getName().startsWith("hl:"))) || 
			(!showHidden && node.getProperty(NodeProperty.HIDDEN.toString()).getBoolean()) ||
			(node.getPrimaryNodeType().getName().equals(FOLDERS_TYPE) && FOLDERS_TO_EXLUDE.contains(node.getName())));
	}
	
	public static org.gcube.common.storagehub.model.Path getHomePath(){
		return Paths.getPath(String.format("/Home/%s/Workspace",AuthorizationProvider.instance.get().getClient().getId()));
	}

	public static StorageClient getStorageClient(String login){
		return new StorageClient(SERVICE_CLASS, SERVICE_NAME, login, AccessType.SHARED, MemoryType.PERSISTENT);

	}
	
	public static Deque<Item> getAllNodesForZip(FolderItem directory,  Session session, AccountingHandler accountingHandler) throws Exception{
		Deque<Item> queue = new LinkedList<Item>();
		Node currentNode = session.getNodeByIdentifier(directory.getId());
		queue.push(directory);
		Deque<Item> tempQueue = new LinkedList<Item>();
		logger.debug("adding directory {}",directory.getPath());
		for (Item item : Utils.getItemList(currentNode,null, null, false)){
			if (item instanceof FolderItem) 
				tempQueue.addAll(getAllNodesForZip((FolderItem) item, session, accountingHandler));
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
				logger.trace("actualPath is {}",actualPath.toPath());
				String name = Paths.remove(actualPath, originalPath).toPath().replaceFirst("/", "");
				logger.trace("writing dir {}",name);
				zos.putNextEntry(new ZipEntry(name));
				zos.closeEntry();
			} else if (item instanceof AbstractFileItem){
				InputStream streamToWrite = Utils.getStorageClient(login).getClient().get().RFileAsInputStream(((AbstractFileItem)item).getContent().getStorageId());
				if (streamToWrite == null){
					logger.warn("discarding item {} ",item.getName());
					continue;
				}
				try(BufferedInputStream is = new BufferedInputStream(streamToWrite)){
					String name = Paths.remove(actualPath, originalPath).toPath()+item.getName().replaceFirst("/", "");
					logger.trace("writing file {}",name);
					zos.putNextEntry(new ZipEntry(name));
					copyStream(is, zos);

				}catch (Exception e) {
					logger.warn("error writing item {}", item.getName(),e);
				} finally{
					zos.closeEntry();
				}
			}
		}

	}

	private static void copyStream(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[2048];
		int readcount = 0;
		while ((readcount=in.read(buffer))!=-1) {
			out.write(buffer, 0, readcount);
		}
	}
	
}
