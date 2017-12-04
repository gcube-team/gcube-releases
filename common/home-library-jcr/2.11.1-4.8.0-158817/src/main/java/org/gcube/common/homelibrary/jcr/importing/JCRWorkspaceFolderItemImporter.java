package org.gcube.common.homelibrary.jcr.importing;



import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLConnection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.jcr.Node;
import javax.jcr.Session;

import org.apache.jackrabbit.util.Text;
import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalUrl;
import org.gcube.common.homelibrary.home.workspace.folder.items.Query;
import org.gcube.common.homelibrary.jcr.repository.JCRRepository;
import org.gcube.common.homelibrary.jcr.workspace.JCRFolderBulkCreatorManager;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceFolder;
import org.gcube.common.homelibrary.jcr.workspace.servlet.JCRSession;
import org.gcube.common.homelibrary.util.WorkspaceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JCRWorkspaceFolderItemImporter implements Runnable{
	

	private List<ImportContentManagerItemRequest> contentItemRequests;
	private List<ImportUrlRequest> urlRequests;
	private List<ImportQueryRequest> queryRequests;
	
	
	private final JCRRepository contentManager;
	private final String folderBulkCreatorId;
	private final JCRFolderBulkCreatorManager manager;
	
	private final String STATUS 				= "hl:status";
	private final String FAILURES				= "hl:failures";
	
	private int failures;
	private int status;
	private final int totalRequests;
	
	private final JCRWorkspaceFolder folder;
	private Logger logger;
	

	public JCRWorkspaceFolderItemImporter(JCRFolderBulkCreatorManager manager, String folderBulkCreatorId,
			List<ImportRequest> requests, JCRWorkspaceFolder folder) throws InternalErrorException {
		super();
		
		this.logger = LoggerFactory.getLogger(JCRWorkspaceFolder.class);
	
		this.manager = manager;
		this.folderBulkCreatorId = folderBulkCreatorId;
		this.contentManager = folder.getWorkspace().getRepository();
		this.folder = folder;
		
		this.status = 0;
		this.failures = 0;
		
		this.contentItemRequests = new LinkedList<ImportContentManagerItemRequest>();
		this.urlRequests = new LinkedList<ImportUrlRequest>();
		this.queryRequests = new LinkedList<ImportQueryRequest>();

		this.totalRequests = requests.size();
		for (ImportRequest request:requests){
			switch(request.getType()){
				case CONTENT_MANAGER_ITEM: contentItemRequests.add((ImportContentManagerItemRequest) request);break;
				case URL: urlRequests.add((ImportUrlRequest) request); break;
				case QUERY: queryRequests.add((ImportQueryRequest) request); break;
			}
		}
	}
	
//	private void upgradeStatus() {
//		JCRServlets session = null;
//		try {
//			session = new JCRServlets();
//			ItemDelegate node = contentManager.getRootFolderBulkCreators(session)
//			.getNode(folderBulkCreatorId);
//			node.setProperty(STATUS, ++status);
//			session.save();
//		}  catch (Exception e) {
//			logger.error("Status not set", e);
//		} finally {
//			if(session != null)
//				session.logout();
//		}
//	}
//	
//	private void upgradeFailures()  {
//		JCRServlets session = null;
//		try {
//			session = new JCRServlets();
//			ItemDelegate node = contentManager.getRootFolderBulkCreators(session).
//			getNode(folderBulkCreatorId);
//			node.setProperty(FAILURES, ++failures);
//			session.save();
//		}  catch (Exception e) {
//			logger.error("Failure not set", e);
//		} finally {
//			if (session != null)
//				session.logout();
//		}
//	}
	

	@Override
	public void run() {
	
		
		ExecutorService executorService = Executors.newFixedThreadPool(5);	
		for (ImportContentManagerItemRequest request: contentItemRequests) {
			
			final URI uri = request.getUri();	
			

			if (request.getItemType() == ContentManagerItemType.DOCUMENT){
				executorService.execute(new Runnable() {
					@Override
					public void run() {
						InputStream inputStream = null;
						try {
							URLConnection connection = uri.toURL().openConnection();
							inputStream = connection.getInputStream();
//							importGCubeElement(uri, inputStream);								
						} catch (IOException e) {
//							upgradeFailures();
							logger.error("Content retrieving failed",e);
							return;
						} finally {
							try {
								inputStream.close();
							} catch (Exception e) {
								logger.error("InputStream not closed");
							}
						}
					}
				});
			}
		}
		executorService.shutdown();
		
		for (ImportUrlRequest request:urlRequests) {

			String name = request.getUrl();
//			name = Text.escapeIllegalJcrChars(name);
			try {
				name = folder.getUniqueName(name,false);
				ExternalUrl externalUrl = folder.createExternalUrlItem(name, "", request.getUrl());
				folder.getWorkspace().fireItemImportedEvent(externalUrl);
//				upgradeStatus();
			} catch (Exception e) {
//				upgradeFailures();
				continue;
			}
		}

//		for (ImportQueryRequest request:queryRequests) {
//			
//			String name = request.getName();
//			name = Text.escapeIllegalJcrChars(name);
//			try {
//				name = WorkspaceUtil.getUniqueName(request.getName(), folder);
//				Query query = folder.createQueryItem(name, "", request.getQuery(), request.getQueryType());
//				folder.getWorkspace().fireItemImportedEvent(query);
//				upgradeStatus();
//			} catch (Exception e) {
//				upgradeFailures();
//				continue;
//			}
//						
//		}
		
	}
	
//	private void importGCubeElement(URI uri, InputStream inputStream) {
//		
//		try {
//			importGCubeDocument(uri, inputStream);
//			upgradeStatus();
//		} catch (Exception e) {
//			upgradeFailures();
//			logger.error("Error",e);
//		};
//		
//	}
			
	
	//http://data.d4science.org/tree/FAOFishFinderFactsheeets/SQL?scope=/d4science

	private String getCollectionName(URI uri) throws Exception {

		String uriString = uri.toString();
		String values[] = uriString.split("/tree/");
		String subValues[] = values[1].split("/");
		String collectionID = subValues[0];
		String[] IdAndScope = subValues[1].split("\\?");
		String treeID = IdAndScope[0];
		StringBuilder title = new StringBuilder();
		title.append(treeID);
		title.append("(");
		title.append(collectionID);
		title.append(")");
		return title.toString();
	}
	

}
