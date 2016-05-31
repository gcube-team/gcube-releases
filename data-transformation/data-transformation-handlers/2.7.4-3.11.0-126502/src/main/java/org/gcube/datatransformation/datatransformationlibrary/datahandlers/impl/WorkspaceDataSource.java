package org.gcube.datatransformation.datatransformationlibrary.datahandlers.impl;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jcr.RepositoryException;

import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.HomeManager;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalFile;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceItem;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceSharedFolder;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datatransformation.datatransformationlibrary.DTSCore;
import org.gcube.datatransformation.datatransformationlibrary.DTSScope;
import org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement;
import org.gcube.datatransformation.datatransformationlibrary.dataelements.impl.LocalFileDataElement;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.ContentTypeDataSource;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataBridge;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataHandlerDefinitions;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSource;
import org.gcube.datatransformation.datatransformationlibrary.model.ContentType;
import org.gcube.datatransformation.datatransformationlibrary.model.Parameter;
import org.gcube.datatransformation.datatransformationlibrary.reports.Record.Status;
import org.gcube.datatransformation.datatransformationlibrary.reports.Record.Type;
import org.gcube.datatransformation.datatransformationlibrary.reports.ReportManager;
import org.gcube.datatransformation.datatransformationlibrary.tmpfilemanagement.TempFileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * This {@link DataSource} fetches items from the user workspace.
 * 
 * @author john.gerbesiotis - DI NKUA
 * 
 */
public class WorkspaceDataSource implements DataSource, ContentTypeDataSource {

	private String collectionID = "workspace";
	
	private DataBridge bridge = DTSCore.getDataBridge();

	private static Logger log = LoggerFactory.getLogger(WorkspaceDataSource.class);
	private String tmpDownloadDir;

	private Workspace ws = null;

	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

	private File nullFile;
	
	private List<String> users;

	private List<String> excludeUsers;
	private List<String> excludeMimetypes;
	
	private static final int MAX_FILE_LENGTH_SUPPPORTED = 64*1000*1000; // 64MB
	private int maxFileLength = MAX_FILE_LENGTH_SUPPPORTED;
	
	private Set<String> visited = new HashSet<String>();;
	/**
	 * This constructor for {@link WorkspaceDataSource}
	 * 
	 * @param input
	 *            The input value of the {@link DataSource}. if not provided, all files will be retrieved.
	 *            e.g: [{"user":"john.gerbesiotis","path":"/Workspace/doc.pdf"}]
	 * @param inputParameters
	 *            The input parameters of the <tt>DataSource</tt>. e.g. collectionid="workspace", limitusers="user1,user2"
	 * @throws Exception 
	 */
	public WorkspaceDataSource(final String input, Parameter[] inputParameters) throws Exception {
		if (inputParameters != null) {
			for (Parameter param : inputParameters) {
				if (param.getName().equalsIgnoreCase("collectionid"))
					this.collectionID = param.getValue().trim();
				if (param.getName().equalsIgnoreCase("limitusers"))
					this.users = Arrays.asList(param.getValue().trim().split(" *, *"));
				if (param.getName().equalsIgnoreCase("excludeusers"))
					this.excludeUsers = Arrays.asList(param.getValue().trim().split(" *, *"));
				if (param.getName().equalsIgnoreCase("excludemimetypes"))
					this.excludeMimetypes = Arrays.asList(param.getValue().trim().split(" *, *"));
				if (param.getName().equalsIgnoreCase("maxSize"))
					this.maxFileLength = Integer.parseInt(param.getValue().trim());
			}
		}
		
		String scope = null;
		if ((scope = DTSScope.getScope()) != null) {
			ScopeProvider.instance.set(scope);
		}

		tmpDownloadDir = TempFileManager.genarateTempSubDir();
		log.debug("Managed to create temporary directory to "+tmpDownloadDir);

		new Thread() {
			public void run() {
				this.setName("WorkspaceDataSource Retriever");

				HomeManager homeManager;
				try {
					homeManager = HomeLibrary.getHomeManagerFactory().getHomeManager();
					
					if (input != null && !input.trim().isEmpty()) { // Retrieve specific files
						List<WorkspaceItemRef> refs = new Gson().fromJson(input, new TypeToken<List<WorkspaceItemRef>>(){}.getType());
	
						for (WorkspaceItemRef ref: refs) {
							ws = homeManager.getHome(ref.user).getWorkspace();
							getItems(ws.getItemByPath(ref.path), null);
						}
					} else { // Retrieve all files
						for (String user : getUsers()) {
							log.info("Retrieving stored workspace data from user: " + user);
							Workspace ws = homeManager.getHome(user).getWorkspace();

							getItems(ws.getRoot(), Arrays.asList(user));
							if (bridge.isClosed()) {
								log.error("Bridge was closed unexpectedly. Stop reading any more...");
								break;
							}
						}
					}
						
					
				} catch (Exception e) {
					log.error("Did not manage to fetch content from Workspace", e);
				} finally {
					log.info("Finished retrieving objects from initial source");
					bridge.close();
				}
			}
		}.start();
	}

	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataHandler#close()
	 */
	@Override
	public void close() {
		bridge.close();
	}

	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataHandler#isClosed()
	 * @return true if the reader has been closed. Otherwise false.
	 */
	@Override
	public boolean isClosed() {
		return bridge.isClosed();
	}

	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.ContentTypeDataSource#nextContentType()
	 * @return next DataElement content type.
	 */
	@Override
	public ContentType nextContentType() {
		DataElement de = null;
		try {
			de = bridge.next();
		} catch (Exception e) {
			log.error("Could not manage to fetch next object's content type", e);
		}

		return de == null ? null : de.getContentType();
	}

	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSource#hasNext()
	 * @return true only if TReader has more trees to read. Otherwise false.
	 */
	@Override
	public boolean hasNext() {
		return bridge.hasNext();
	}

	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSource#next()
	 * @return next DataElement
	 * @throws Exception
	 */
	@Override
	public DataElement next() throws Exception {
		return bridge.next();
	}

	private void getItems(WorkspaceItem workspaceItem, List<String> accessList) throws RepositoryException, InternalErrorException {
		List<? extends WorkspaceItem> children;
		if (workspaceItem.isFolder())
			children = workspaceItem.getChildren();
		else
			children = Arrays.asList(workspaceItem);
			
		for (WorkspaceItem child : children) {
			List<String> childAL = accessList;
			if (child instanceof JCRWorkspaceSharedFolder) {
				JCRWorkspaceItem jcrWorkspaceItem = (JCRWorkspaceItem) child;
				if (visited.contains(jcrWorkspaceItem.getId())) {
					log.info("Already visited shared folder: " + jcrWorkspaceItem.getPath() + ". skipping...");
					return;
				}
				visited.add(jcrWorkspaceItem.getId());
				childAL = jcrWorkspaceItem.getUsers();
			}

			retrieveInfo(child, childAL);

			if (child.getChildren() != null && child.getChildren().size() > 0)
				getItems(child, childAL);

			if(bridge.isClosed())
				break;
		}
	}

	private static final String extensionSeparator = ".";
	private static String getFileExtention(String fileName){
		int dot = fileName.lastIndexOf(extensionSeparator);
		if(dot==-1){
			return null;
		}
		return fileName.substring(dot + 1);
	}

	private void retrieveInfo(WorkspaceItem item, List<String> usersACL) {
		String id = null;
		if (item instanceof ExternalFile) {
			LocalFileDataElement object = new LocalFileDataElement();
			
			ExternalFile file = (ExternalFile) item;
			try {
				// get the id
				id = file.getPublicLink(false);
				object.setId(id);
				
				if (excludeMimetypes != null && excludeMimetypes.contains(file.getMimeType())) {
					log.info("File (" + file.getId() + ") has a mimetype (" + file.getMimeType() + ") to exclude from handling. Appending without content.");
					nullFile = new File(TempFileManager.generateTempFileName(null));
					nullFile.createNewFile();
					object.setContent(nullFile);
				} else {
					//Check if filesize is to big
					if (file.getLength() < maxFileLength) {
						// get the data
						String extention = getFileExtention(file.getName());
						String localFileName = TempFileManager.generateTempFileName(tmpDownloadDir);
						if (extention != null) {
							localFileName += (extensionSeparator + extention);
						}
						File localFile = new File(localFileName);
						InputStream is = file.getData();
						Files.copy(is, Paths.get(localFileName));
						is.close();
						
						object.setContent(localFile);
					} else {
						log.info("File (" + file.getId() + ") has too big size (" + file.getLength() + ") to handle. Appending without content.");
						nullFile = new File(TempFileManager.generateTempFileName(null));
						nullFile.createNewFile();
						object.setContent(nullFile);
					}
				}
				
				// get the rest
				object.setAttribute(DataHandlerDefinitions.ATTR_COLLECTION_ID, collectionID);
		
				object.setAttribute(DataHandlerDefinitions.ATTR_CONTENT_OID, id);

				object.setAttribute("title", String.valueOf(file.getName()));

				object.setAttribute("size", String.valueOf(file.getLength()));

				object.setAttribute("creationDate", format.format(file.getCreationTime().getTime()));

				object.setAttribute("modificationDate", format.format(file.getLastModificationTime().getTime()));

				object.setAttribute("owner", String.valueOf(file.getOwner().getPortalLogin()));

				object.setAttribute("mimeType", file.getMimeType());

				object.setAttribute("workspaceItemID", file.getId());

				object.setAttribute("path", file.getPath());

				//get the Access Control List
				if (usersACL == null) {
					if (item.isShared()) {
							String sharedFolderId = item.getIdSharedFolder();
							JCRWorkspaceSharedFolder sharedFolder = (JCRWorkspaceSharedFolder) ws.getItem(sharedFolderId);
							usersACL = sharedFolder.getUsers();
					} else {
						usersACL = Arrays.asList(ws.getOwner().getPortalLogin());
					}
				}
				for (int i = 0; i < usersACL.size(); i++) 
					object.setAttribute("sid" + i, usersACL.get(i));

				object.setContentType(new ContentType(file.getMimeType(), new ArrayList<Parameter>()));

				bridge.append(object);
				
				log.trace("Object with id " + id + " and size " + file.getLength() + " was added for processing from Workspace");
				ReportManager.manageRecord(id, "Object with id " + id + " was added for processing from Workspace", Status.SUCCESSFUL, Type.SOURCE);
			} catch (Exception e) {
				log.warn("Could not manage to fetch the object " + id, e);
				ReportManager.manageRecord(id, "Object with id " + id + " could not be fetched from Workspace", Status.FAILED, Type.SOURCE);
			}
		}
	}

	private List<String> getUsers() throws InternalErrorException, InterruptedException {
		if (users == null) {
			users = HomeLibrary.getHomeManagerFactory().getUserManager().getUsers();
			Collections.shuffle(users);
			
			if (excludeUsers != null) {
				List<String> inters = new ArrayList<String>();
				inters.addAll(users);
				inters.retainAll(excludeUsers);
				log.info("going to remove users: " + inters);
				
				users.removeAll(inters);
			}
		}

		log.info("users: " + users);
		return users;
	}
	
//	public static void main(String[] args) throws Exception {
//		
//		
//		DTSScope.setScope("/gcube/devNext");
////		DTSScope.setScope("/d4science.research-infrastructures.eu");
//
//		JsonObject jo1 = new JsonObject();
//		jo1.add("user", new JsonPrimitive("john.gerbesiotis"));
//		jo1.add("path", new JsonPrimitive("/Workspace/Google.pdf"));
//		
//		JsonObject jo2 = new JsonObject();
//		jo2.add("user", new JsonPrimitive("alex.antoniadi"));
//		jo2.add("path", new JsonPrimitive("/Share/c599fd10-c1b1-40ed-8785-1b349d2b9d2a/sharedTest.txt"));
//		
//		String input = new Gson().toJson(Arrays.asList(jo1));
//System.out.println(input);
//
//input = "[{\"user\":\"john.gerbesiotis\",\"path\":\"/gcube/home/org.gcube.portlets.user/home-library/Home/john.gerbesiotis/Workspace/Trash/8a5653f4-d9fc-4221-8ce6-2a78a6b4107c/delete\"}]";
//WorkspaceDataSource source  = new WorkspaceDataSource(input, new Parameter[]{new Parameter("collectionid", "workspace"), new Parameter("limitusers", "john.gerbesiotis")});
//
//int i = 0;
//		while (source.hasNext()) {
//			System.out.println(i++);
//			DataElement de = source.next();
//			System.out.println(de.getId());
//			System.out.println(de.getContentType().getMimeType());
//			System.out.println(de.getContentType().getContentTypeParameters());
//			System.out.println(de.getAllAttributes().toString().replaceAll(", ", ",\n"));
//		}
//		
//		source.close();
//	}
}

class WorkspaceItemRef {
	public String user;
	public String path; 
}
