package org.apache.jackrabbit.j2ee.workspacemanager.trash;

import javax.jcr.AccessDeniedException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.version.VersionException;

import org.apache.jackrabbit.j2ee.workspacemanager.storage.GCUBEStorage;
import org.apache.jackrabbit.j2ee.workspacemanager.versioning.JCRVersioning;
import org.gcube.common.homelibary.model.items.type.NodeProperty;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JCRTrash {

	private Logger logger = LoggerFactory.getLogger(JCRTrash.class);

	private Session session;
	private String login;
	private GCUBEStorage storage;

	private String trashPath;


	public JCRTrash(Session session, String login) {
		this.storage = new GCUBEStorage(login);
		this.session = session;
		this.login = login;
		this.trashPath = "/Home/"+login+"/Workspace/Trash/";

	}


	public void deletePermanently(String id) throws InternalErrorException, InsufficientPrivilegesException {
		logger.info("Delete permanently item ID " + id + " by user " + login);

		try {
			Node node = session.getNodeByIdentifier(id);
			logger.debug("deletePermanently node: " + node.getPath());

			//remove the content of the trash item from storage
			try{
				storage.removeRemoteFolder(node.getPath());
			}catch (RemoteBackendException e) {
				logger.warn("Error removing " + node.getPath() + " from storage", e);
				throw new InternalErrorException(e);
			}

			//remove versions
			visitTree(node);	

			//remove the trash item from jackrabbit
			node.remove();
			session.save();
		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		}
	}


	private void visitTree(Node root) throws RepositoryException, InsufficientPrivilegesException, InternalErrorException {

//		System.out.println("** " + root.getIdentifier() + " - "+root.getPath() );
		try{
			if (root.hasNode(NodeProperty.CONTENT.toString())){

				JCRVersioning versioning = new JCRVersioning(session,login);
				versioning.removeAllVersions(root.getIdentifier());
			}
		} catch (RepositoryException e) {
			logger.error(root.getPath() +" not deleted.");
			//				throw new InternalErrorException(e);
		}

		if (root.hasNodes()){
			NodeIterator iterator = root.getNodes();
			while(iterator.hasNext()){
				Node node = iterator.nextNode();
				visitTree(node);
			}
		}
	}


	public void emptyTrash() throws AccessDeniedException, VersionException, LockException, ConstraintViolationException, RepositoryException {
		logger.info("Empty Trash " + trashPath + " by user " + login);
		new Thread(
				new Runnable() {
					public void run() {
						try {
//							System.out.println("REMOVE REMOTE FOLDER TO STORAGE " + trashPath);
							storage.removeRemoteFolder(trashPath);
							//remove versions

						} catch (RemoteBackendException e) {
							logger.error("Error deleting folder " + trashPath + e);
						}
					}
				}
				).start();


		Node trash = session.getNode(trashPath);
		try {
			visitTree(trash);
		} catch (InsufficientPrivilegesException e) {
			logger.error("Error deleting versions " + trashPath + e);
		} catch (InternalErrorException e) {
			logger.error("Error deleting versions " + trashPath + e);
		}

		session.removeItem(trashPath);	
		session.save();
	}


}
