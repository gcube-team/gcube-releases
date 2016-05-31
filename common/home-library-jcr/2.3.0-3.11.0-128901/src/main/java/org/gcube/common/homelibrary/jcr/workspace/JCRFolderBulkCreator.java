package org.gcube.common.homelibrary.jcr.workspace;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.Session;

import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.folder.FolderBulkCreator;
import org.gcube.common.homelibrary.home.workspace.folder.items.QueryType;
import org.gcube.common.homelibrary.jcr.importing.ImportContentManagerItemRequest;
import org.gcube.common.homelibrary.jcr.importing.ImportQueryRequest;
import org.gcube.common.homelibrary.jcr.importing.ImportRequest;
import org.gcube.common.homelibrary.jcr.importing.ImportUrlRequest;
import org.gcube.common.homelibrary.jcr.importing.JCRWorkspaceFolderItemImporter;
import org.gcube.common.homelibrary.jcr.repository.JCRRepository;
import org.gcube.common.homelibrary.model.exceptions.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JCRFolderBulkCreator implements FolderBulkCreator {
	
	private static final String NT_FOLDER_BULK_CREATOR 	= "nthl:folderBulkCreator";
	private static final String FOLDER_ID 			= "hl:folderId";
	private static final String STATUS 				= "hl:status";
	private static final String FAILURES			= "hl:failures";
	private static final String REQUESTS			= "hl:requests";
	
	private String id;
	private String nodeId;
	private JCRWorkspaceFolder folder;
	private JCRFolderBulkCreatorManager manager;
	private List<ImportRequest> requests = new LinkedList<ImportRequest>();
	
	private boolean commited = false;
	
	private Logger logger;
	private final JCRWorkspace workspace;
	
	
	public JCRFolderBulkCreator(JCRWorkspace workspace, ItemDelegate node, JCRFolderBulkCreatorManager manager) throws RepositoryException, InternalErrorException {
		
		this.logger = LoggerFactory.getLogger(JCRFolderBulkCreator.class);
		this.workspace = workspace;
		this.manager = manager;
		this.commited = true;
		this.id = node.getName();
		this.nodeId = node.getId();
		
		//TO DO
//		try {
//			this.folder = (JCRWorkspaceFolder) workspace.getItem(node.getProperty(FOLDER_ID).getString());
//		} catch (ItemNotFoundException e) {
//			throw new InternalErrorException(e);
//		}
	}
	
	public JCRFolderBulkCreator(String id, JCRWorkspaceFolder folder,
			JCRFolderBulkCreatorManager manager) throws InternalErrorException {
		super();
		
		this.logger = LoggerFactory.getLogger(JCRFolderBulkCreator.class);
		this.workspace = folder.getWorkspace();
		this.commited = false;
		this.id = id;
		this.folder = folder;
		this.manager = manager;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void createMetadata(URI uri) throws InsufficientPrivilegesException,
			InternalErrorException {
		if(commited)
			throw new InternalErrorException("FolderBulkCreator already active");
		
		if (uri == null)
			throw new IllegalArgumentException("Uri are null");
		
//		try {
//			URIs.validate(uri);
//		} catch (Exception e) {
//			throw new IllegalArgumentException("Invalid URI",e);
//		}
		requests.add(ImportContentManagerItemRequest.metadataRequest(uri));
	}

	@Override
	public void createAnnotation(URI uri)
			throws InsufficientPrivilegesException, InternalErrorException {

		if(commited)
			throw new InternalErrorException("FolderBulkCreator already active");
		
		if (uri == null)
			throw new IllegalArgumentException("Uri are null");
		
//		try {
//			URIs.validate(uri);
//		} catch (Exception e) {
//			throw new IllegalArgumentException("Invalid URI",e);
//		}
		requests.add(ImportContentManagerItemRequest.annotationRequest(uri));
	}

	@Override
	public void createDocumentPartItem(URI uri)
			throws InsufficientPrivilegesException, InternalErrorException {
		// TODO Auto-generated method stub

	}

	@Override
	public void createDocumentAlternativeItem(URI uri)
			throws InsufficientPrivilegesException, InternalErrorException {
		
		if(commited)
			throw new InternalErrorException("FolderBulkCreator already active");
		
		if (uri == null)
			throw new IllegalArgumentException("Uri are null");
		
		requests.add(ImportContentManagerItemRequest.alternativeRequest(uri));
	}

	@Override
	public void createDocumentItem(URI uri)
			throws InsufficientPrivilegesException, InternalErrorException {
	
		if(commited)
			throw new InternalErrorException("FolderBulkCreator already active");
		
		if (uri == null)
			throw new IllegalArgumentException("Uri are null");
		
//		try {
//			URIs.validate(uri);
//		} catch (Exception e) {
//			throw new IllegalArgumentException("Invalid URI",e);
//		}
		requests.add(ImportContentManagerItemRequest.documentRequest(uri));
	}

	@Override
	public void createExternalUrl(String url) throws InternalErrorException {
		
		if(commited)
			throw new InternalErrorException("FolderBulkCreator already active");
		
		if (url == null)
			throw new IllegalArgumentException("The url can't be null.");
		
		requests.add(new ImportUrlRequest(url));
	}

	@Override
	public void createQuery(String name, String query, QueryType queryType) throws InternalErrorException {
		
		if(commited)
			throw new InternalErrorException("FolderBulkCreator already active");
		
		if (name == null || query == null || queryType == null)
			throw new IllegalArgumentException("name, query or queryType null");

		requests.add(new ImportQueryRequest(name, query, queryType));
	}

	private void saveFolderBulkCreator() throws InternalErrorException {
		
		//TO DO
		
//		Session session = JCRRepository.getSession();
//		try { 
//			Node rootCreator = workspace.getRepository().
//					getRootFolderBulkCreators(session);
//			Node node = rootCreator.addNode(id, NT_FOLDER_BULK_CREATOR);
//			node.setProperty(FOLDER_ID, folder.getId());
//			node.setProperty(REQUESTS, requests.size());
//			session.save();
//			nodeId = node.getIdentifier();
//		} catch (RepositoryException e) {
//			throw new InternalErrorException(e);
//		} finally {
//			session.logout();
//		}
	
	}
	
	
	@Override
	public void commit() throws InternalErrorException {
		
		logger.debug("Request commited");
		if (commited) {
			throw new IllegalStateException("This FolderBulkCreator has already commited");
		}
		
		commited = true;
		

		if (requests.size() == 0){
			logger.debug("No request was submitted");
			return;
		}

		JCRWorkspaceFolderItemImporter folderItemImporter = new JCRWorkspaceFolderItemImporter(manager, getId(), requests, folder);
		Thread fiiThread = new Thread(folderItemImporter,"FolderItemImporter");
		manager.addFolderBulkCreator(this, fiiThread);
		saveFolderBulkCreator();
		
		logger.debug("Start importer");
		fiiThread.start();

	}
	
	

	@Override
	public WorkspaceFolder getDestinationFolder() {
		return folder;
	}

	@Override
	public int getNumberOfRequests() throws InternalErrorException {
		
		if(nodeId == null)
			return requests.size();
		
		Session session = JCRRepository.getSession();
		try {
			Node node = session.getNodeByIdentifier(nodeId);
			return (int)node.getProperty(REQUESTS).getLong();
		}  catch (Exception e) {
			logger.error("getStatus error ",e);
			throw new InternalErrorException(e);
		} finally {
			session.logout();
		}
	}
	
	@Override
	public float getStatus() throws InternalErrorException{
		
		if (nodeId == null) 
			return 0;
		
		Session session = JCRRepository.getSession();
		try {
			Node node = session.getNodeByIdentifier(nodeId);
			long successes =  node.getProperty(STATUS).getLong();
			long failures = node.getProperty(FAILURES).getLong();
			return (float)(successes + failures)/(float)node.getProperty(REQUESTS).getLong();
		}  catch (Exception e) {
			logger.error("getStatus error ",e);
			throw new InternalErrorException(e);
		} finally {
			session.logout();
		}
	}
	
	@Override
	public int getFailures() throws InternalErrorException {
		
		if (nodeId == null) 
			return 0;
		
		Session session = JCRRepository.getSession();
		try {
			Node node = session.getNodeByIdentifier(nodeId);
			return (int)node.getProperty(FAILURES).getLong();
		}  catch (Exception e) {
			logger.error("getStatus error ",e);
			throw new InternalErrorException(e);
		} finally {
			session.logout();
		}
	}
	
	
	@Override
	public void remove() throws InternalErrorException {
		
		manager.removeThread(id);
		
		if(nodeId == null)
			return;
		
		//TO DO
//		Session session = JCRRepository.getSession();
//		try {
//			Node node = session.getNodeByIdentifier(nodeId);
//			node.remove();
//			session.save();
//		} catch (RepositoryException e) {
//			throw new InternalErrorException(e);
//		} finally {
//			session.logout();
//		}
	}

}
