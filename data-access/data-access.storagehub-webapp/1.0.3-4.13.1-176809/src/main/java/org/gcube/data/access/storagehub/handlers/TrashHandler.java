package org.gcube.data.access.storagehub.handlers;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.storagehub.model.Excludes;
import org.gcube.common.storagehub.model.Paths;
import org.gcube.common.storagehub.model.exceptions.BackendGenericError;
import org.gcube.common.storagehub.model.exceptions.UserNotAuthorizedException;
import org.gcube.common.storagehub.model.items.AbstractFileItem;
import org.gcube.common.storagehub.model.items.FolderItem;
import org.gcube.common.storagehub.model.items.Item;
import org.gcube.common.storagehub.model.items.TrashItem;
import org.gcube.common.storagehub.model.types.ItemAction;
import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.data.access.storagehub.AuthorizationChecker;
import org.gcube.data.access.storagehub.Constants;
import org.gcube.data.access.storagehub.Utils;
import org.gcube.data.access.storagehub.accounting.AccountingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class TrashHandler {

	private static Logger log = LoggerFactory.getLogger(TrashHandler.class);

	@Inject
	VersionHandler versionHandler;

	@Inject 
	AccountingHandler accountingHandler;

	@Inject
	AuthorizationChecker authChecker;
	
	@Inject
	Item2NodeConverter item2Node;

	public void removeNodes(Session ses, List<Item> itemsToDelete) throws RepositoryException{
		log.debug("defnitively removing nodes with ids {}",itemsToDelete);
		final String login = AuthorizationProvider.instance.get().getClient().getId();
		final Node trashFolder = ses.getNode(Paths.append(Utils.getWorkspacePath(),Constants.TRASH_ROOT_FOLDER_NAME).toPath());
		//String parentPath = itemToDelete.getParentPath();
		try {
			ses.getWorkspace().getLockManager().lock(trashFolder.getPath(), true, true, 0,login);
			Set<String> contentIdsToDelete = new HashSet<>();

			for (Item trashItem:  itemsToDelete) {
				try {
					Node trashItemNode = ses.getNodeByIdentifier(trashItem.getId());
					List<Item> trashChildren = Utils.getItemList(trashItemNode, Excludes.GET_ONLY_CONTENT, null, true, null);
					for (Item itemContentToRetrieve: trashChildren)
						Utils.getAllContentIds(ses, contentIdsToDelete, itemContentToRetrieve, versionHandler);
					trashItemNode.remove();
				}catch (Exception e) {
					log.warn("error removing item with id {}",trashItem.getId(), e);
				}
			}
			log.debug("content ids to remove are {}",contentIdsToDelete);
			
			
			//TODO: make it as an authorizableTask
			String user = AuthorizationProvider.instance.get().getClient().getId();
			new Thread() {

				public void run() {
					for (String id: contentIdsToDelete) {
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
			ses.getWorkspace().getLockManager().unlock(trashFolder.getPath());
		}
	}


	public void moveToTrash(Session ses, Node nodeToDelete, Item item) throws RepositoryException, BackendGenericError{
		log.debug("moving node {} to trash ",item.getId());
		final Node trashFolder = ses.getNode(Paths.append(Utils.getWorkspacePath(),Constants.TRASH_ROOT_FOLDER_NAME).toPath());

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
			//String pathUUid= UUID.randomUUID().toString();
			trashItem.setTitle(item.getTitle());
			trashItem.setName(item.getId());
			trashItem.setOriginalParentId(nodeToDelete.getParent().getIdentifier());	

			trashItem.setOwner(item.getOwner());
			trashItem.setLastModificationTime(item.getLastModificationTime());
			trashItem.setLastModifiedBy(item.getLastModifiedBy());

			trashItem.setLenght(0);

			if (item instanceof FolderItem) { 
				trashItem.setFolder(true);
			}else if (item instanceof AbstractFileItem ) {
				AbstractFileItem file = (AbstractFileItem) item;
				if (file.getContent()!=null) {
					trashItem.setMimeType(file.getContent().getMimeType());
					trashItem.setLenght(file.getContent().getSize());
				}
				trashItem.setFolder(false);
			}

			log.debug("creating node");

			Node newTrashItemNode = item2Node.getNode(ses, trashFolder, trashItem);

			ses.save();
			log.debug("calling jcr move");
			ses.getWorkspace().move(nodeToDelete.getPath(), Paths.append(Paths.getPath(newTrashItemNode.getPath()),nodeToDelete.getName()).toPath());
			String mimetype = null;
			if (item instanceof AbstractFileItem) {
				if (((AbstractFileItem)item).getContent()!=null)
					mimetype = ((AbstractFileItem) item).getContent().getMimeType();
				 else log.warn("the AbstractFileItem with id {} has no content (check it!!)", item.getId());
			}
			accountingHandler.createFolderRemoveObj(item.getName(), item.getClass().getSimpleName(), mimetype, ses, ses.getNodeByIdentifier(item.getParentId()), true);
		}catch(Throwable t) {
			throw new BackendGenericError(t);
		}finally {
			ses.getWorkspace().getLockManager().unlock(nodeToDelete.getPath());
			ses.getWorkspace().getLockManager().unlock(trashFolder.getPath());
		}

	}

	public String restoreItem(Session ses, TrashItem item) throws RepositoryException, BackendGenericError, UserNotAuthorizedException{
		log.debug("restoring node from trash");
		final String login = AuthorizationProvider.instance.get().getClient().getId();
		//final Node trashFolder = ses.getNode(Paths.append(Utils.getHomePath(),Constants.TRASH_ROOT_FOLDER_NAME).toPath());
		Node originalParent = ses.getNodeByIdentifier(item.getOriginalParentId());
		authChecker.checkWriteAuthorizationControl(ses, originalParent.getIdentifier(), false );		

		ses.getWorkspace().getLockManager().lock(originalParent.getPath(), true, true, 0,login);
		List<Item> items = Utils.getItemList(ses.getNodeByIdentifier(item.getId()), Excludes.ALL, null, false, null);
		if (items.size()!=1) {
			log.warn("a problem occurred restoring item from trash");
			throw new BackendGenericError("An error occurred on trash item");
		}
		Item itemToMove = items.get(0);
		String newNodePath = Paths.append(Paths.getPath(originalParent.getPath()), itemToMove.getName()).toPath();
		ses.move(itemToMove.getPath(), newNodePath);
		Utils.setPropertyOnChangeNode(ses.getNode(newNodePath), login, ItemAction.MOVED);
		ses.removeItem(item.getPath());
		ses.save();
		return ses.getNode(newNodePath).getIdentifier();			

	}

}
