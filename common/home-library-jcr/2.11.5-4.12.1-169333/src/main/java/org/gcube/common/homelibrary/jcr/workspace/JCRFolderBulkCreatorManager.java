package org.gcube.common.homelibrary.jcr.workspace;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.folder.FolderBulkCreator;
import org.gcube.common.homelibrary.home.workspace.folder.FolderBulkCreatorManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JCRFolderBulkCreatorManager implements FolderBulkCreatorManager {

	Logger logger;
	final JCRWorkspace workspace;
	
	protected Map<String, Thread> threads = new LinkedHashMap<String, Thread>();
	
	public JCRFolderBulkCreatorManager(JCRWorkspace workspace) {
		
		logger = LoggerFactory.getLogger(JCRFolderBulkCreatorManager.class);
		this.workspace = workspace;
	}
	
	public FolderBulkCreator getFolderBulk(JCRWorkspaceFolder destinationFolder) throws InternalErrorException {
		
		String name = UUID.randomUUID().toString();
		return new JCRFolderBulkCreator(name,
					destinationFolder, this);
	}
	
	public void removeThread(String id) throws InternalErrorException {
		threads.remove(id);		
	}
	
	public void addFolderBulkCreator(FolderBulkCreator folderBulk, Thread th) throws InternalErrorException {
		
		threads.put(folderBulk.getId(), th);
	}
	
	@Override
	public List<FolderBulkCreator> getActiveFolderBulkCreators() {
		return null;
		
		//TO DO
		
//		LinkedList<FolderBulkCreator> list = new LinkedList<FolderBulkCreator>();
//		Session session = null;
//		try { 
//			session = JCRRepository.getSession();
//			Node rootCreator = workspace.getRepository().
//					getRootFolderBulkCreators(session);
//			for(NodeIterator iterator = rootCreator.getNodes(); iterator.hasNext();) {
//				Node node = iterator.nextNode();
//				list.add(new JCRFolderBulkCreator(workspace, node, this));
//			}
//			
//		} catch (Exception e) {
//			logger.error("Error ",e);
//		} finally {
//			if(session != null)
//				session.logout();
//		}
//		return list;
	}
	
	@Override
	public FolderBulkCreator getActiveFolderBulkCreator(String id) throws InternalErrorException {
		return null;
		//TO DO
//		Session session = JCRRepository.getSession();
//		try { 
//			Node rootCreator = workspace.getRepository().
//					getRootFolderBulkCreators(session);
//			return new JCRFolderBulkCreator(workspace,rootCreator.getNode(id),this);
//		} catch (RepositoryException e) {
//			logger.error("Error ",e);
//			throw new InternalErrorException(e);
//		} finally {
//			session.logout();
//		}
	}
	

	@Override
	public void waitFolderBulkCreator(String id) throws InterruptedException{
		Thread th = threads.get(id);
		if (th!=null) th.join();
	}

}
