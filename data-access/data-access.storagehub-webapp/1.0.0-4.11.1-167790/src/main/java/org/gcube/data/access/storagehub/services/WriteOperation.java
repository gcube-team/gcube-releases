package org.gcube.data.access.storagehub.services;

import java.util.List;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.storagehub.model.Paths;
import org.gcube.common.storagehub.model.items.AbstractFileItem;
import org.gcube.common.storagehub.model.items.FolderItem;
import org.gcube.common.storagehub.model.items.Item;
import org.gcube.common.storagehub.model.items.SharedFolder;
import org.gcube.common.storagehub.model.types.PrimaryNodeType;
import org.gcube.data.access.storagehub.StorageFactory;
import org.gcube.data.access.storagehub.Utils;
import org.gcube.data.access.storagehub.handlers.ItemHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("modify")
public class WriteOperation {

	private static final Logger log = LoggerFactory.getLogger(WriteOperation.class);
	
	@Inject 
	RepositoryInitializer repository;
/*	
	@PUT
	@Path("copy")
	public String copy(@QueryParam("destinationPath") String destinationPath,  @QueryParam("sourcePath") String origin){
		Session ses = null;
		try{
			String login = AuthorizationProvider.instance.get().getClient().getId();
			ses = repository.getRepository().login(new SimpleCredentials(login,Utils.getSecurePassword(login).toCharArray()));
			Node originNode = ses.getNode(origin);
			Item originItem = ItemHandler.getItem(originNode, null);
			Node destinationNode = ses.getNode(destinationPath);
			Item destinationItem = ItemHandler.getItem(destinationNode, null);
			
			if (!(destinationItem instanceof FolderItem)) throw new Exception("an Item must be copyed to another directory");
			
			if (originItem instanceof SharedFolder) throw new Exception("trying to copy a sharedFolder into a normal folder");
			
			ses.getWorkspace().getLockManager().lock(origin, true, true, 0,login);
			ses.getWorkspace().getLockManager().lock(destinationPath, true, true, 0,login);
			
			
			if (!destinationItem.getPrimaryType().equals(PrimaryNodeType.NT_WORKSPACE_FOLDER) || 
					!destinationItem.getPrimaryType().equals(PrimaryNodeType.NT_WORKSPACE_SHARED_FOLDER))
					throw new Exception("origin is not a folder");
			
			ses.getWorkspace().getLockManager().lock(origin, true, true, Long.MAX_VALUE, login);
			ses.getWorkspace().getLockManager().lock(destinationPath, false, true, Long.MAX_VALUE, login);
			
			ses.getWorkspace().copy(origin, destinationPath);
			
			org.gcube.common.storagehub.model.Path newNodePath = Paths.append(Paths.getPath(destinationPath), Paths.getPath(origin).getLastDirName());
			
			if (originItem instanceof FolderItem ){
				//copying a folder
				
				StorageFactory.getGcubeStorage().copyDir().from(origin).to(destinationPath);
				
				
				List<Item> items= Utils.getItemList(originNode,null, null);
				for (Item item: items){
					if (item instanceof FolderItem){
						//TODO iterate on it recursively
					} else if (item instanceof AbstractFileItem) {
						String storageId = ((AbstractFileItem) item).getContent().getStorageId();
						//String newStorageId = StorageFactory.getGcubeStorage().copyById(storageId);
						//TODO set the new storageId into the item
					} //else nothing to do
					
				}
			} else {
				//copying item that is not a folder
				
			}
		
			ItemHandler handler = new ItemHandler();
			
			//itera su i nodi e modifica solo quelli che non sono di tipo folder facendo la copy del content (con le nuovi api dello storage)
			//e setta il nuovo id del content
			
			//copy also the content of the directory
			ses.save();
			return destinationPath;
		}catch(Exception e){
			log.error("error copying {} to {}", origin, destinationPath);
			return null;
		} finally {
			if (ses!=null){
				try {
					ses.getWorkspace().getLockManager().unlock(destinationPath);
				} catch (Throwable t){
					log.warn("error unlocking {}", destinationPath);
				}
				try {
					ses.getWorkspace().getLockManager().unlock(origin);
				} catch (Throwable t){
					log.warn("error unlocking {}", origin);
				}
				ses.logout();
			}
		}
	}
	
	*/
	
	
}
