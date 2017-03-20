package org.gcube.portlets.user.homelibrary.jcr.manager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.gcube.common.homelibary.model.items.type.WorkspaceItemType;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.User;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSharedFolder;
import org.gcube.common.homelibrary.home.workspace.accessmanager.ACLType;
import org.gcube.common.homelibrary.home.workspace.accounting.AccountingEntry;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.home.workspace.folder.items.GCubeItem;
import org.gcube.common.homelibrary.home.workspace.folder.items.ReportTemplate;
import org.gcube.common.homelibrary.home.workspace.trash.WorkspaceTrashFolder;
import org.gcube.common.homelibrary.jcr.repository.JCRRepository;
import org.gcube.common.homelibrary.jcr.repository.external.GCUBEStorage;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceItem;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceSharedFolder;
import org.gcube.common.scope.api.ScopeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CheckStream {


	static JCRWorkspace ws = null;
	public static final String REMOTE_STORAGE_PATH 		=  	"hl:remotePath";
	public static final String DATA 	  				= 	"jcr:data";

	static BufferedWriter bw = null;
	static List<String> lista =null;
	static Logger logger = LoggerFactory.getLogger(CheckStream.class);

	public static void main(String[] args) throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException {

		ScopeProvider.instance.set("/gcube/devsec");


		String portalLogin = "gianpaolo.coro";

		ws = (JCRWorkspace) HomeLibrary
				.getHomeManagerFactory()
				.getHomeManager()
				.getHome(portalLogin).getWorkspace();


		//		String id = "e3047a69-645a-46d0-8f80-ec4869a0b06e";
		//		WorkspaceItem csv = ws.getItem(id);
		//		System.out.println(csv.getPath());
		//		System.out.println(csv.getRemotePath());
		//				System.out.println(csv.getCreationTime().getTime());

		//		List<AccountingEntry> list = csv.getAccounting();
		//		for(AccountingEntry entry: list){
		//			System.out.println(entry.getEntryType() + " - " + entry.getDate().getTime()) ;
		//		}
		//		System.out.println(csv.getRemotePath());


		//		checkRoot(portalLogin);
		//		checkRoot(ws.getRoot(), portalLogin);
		//		write("test");
//		lista = read();

		checkRoot(ws.getRoot(), portalLogin);
	}


	private static void checkRoot(WorkspaceItem item, String portalLogin) throws InternalErrorException, InsufficientPrivilegesException, IOException {

		try{
			bw = new BufferedWriter(new FileWriter("/home/valentina/missing.csv", true));
			System.out.println(item.getPath());
			if (item.isFolder()){
				System.out.println("is folder or gcubeITem -> skip");

				List<? extends WorkspaceItem> children = item.getChildren();
				for (WorkspaceItem child: children){
					checkRoot(child, portalLogin);
				}
				//				System.out.println("is folder or gcubeITem -> skip");

			}else{

								InputStream stream = null;
				try{
					//										getData(item, portalLogin);
					//					if (lista.contains(item.getPath())){
					//						System.out.println("IN LISTA!!!");
					////						write(item.getPath() + "\t" + item.getCreationTime().getTime().toString() + "\t" + "In lista");
					//						getData(item, portalLogin);
					//					}
					stream = getData(item, portalLogin);
									System.out.println(stream.available());



//										write(item.getPath() + "\t" + item.getCreationTime().getTime().toString() + "\t" + "OK");
				} catch (Exception e) {
										System.out.println("CREATED: " + item.getCreationTime().getTime());
					//				child.remove();
									write(item.getPath() + "\t" + item.getCreationTime().getTime().toString() + "\t" + "MISSING");
					e.printStackTrace();
				}finally{
										if (stream!=null)
											stream.close(); 
				}
			}
		}finally {                       // always close the file
			if (bw != null) try {
				bw.close();
			} catch (IOException ioe2) {
				// just ignore it
			}
		} 



	}



	private static void write(String line) throws IOException {


		try {
			// APPEND MODE SET HERE

			bw.write(line);
			bw.newLine();
			bw.flush();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} 


	}



	//	private static void write(String line) throws IOException {
	//		try {
	//			writer = new BufferedWriter(new OutputStreamWriter(
	//					new FileOutputStream("/home/valentina/filename.txt"), "utf-8"));
	//		
	//		writer.write(line);
	//		} catch (IOException ex) {
	//			// report
	//		} finally {
	//			try {
	//				writer.close();
	//			} catch (Exception ex) {
	//				/*ignore*/
	//			}
	//		}
	//
	//	}

	//
	//	private static void checkRoot(String portalLogin) throws InternalErrorException, InsufficientPrivilegesException, IOException {
	//		List<WorkspaceItem> children = ws.getRoot().getChildren();
	//		for (WorkspaceItem child: children){
	//			System.out.println(child.getName());
	//			if (child.isFolder() || (child instanceof GCubeItem)){
	//				System.out.println("is folder or gcubeITem -> skip");
	//				continue;
	//			}
	//
	//
	//			try{
	//				getData(child, portalLogin);
	//		
	//			} catch (Exception e) {
	//				System.out.println("CREATED: " + child.getCreationTime().getTime());
	//				//				child.remove();
	//				e.printStackTrace();
	//			}finally{
	////				if (stream!=null)
	////					stream.close(); 
	//			}
	//		}
	//
	//	}


	private static InputStream getData(WorkspaceItem node, String portalLogin) throws InternalErrorException {
		logger.trace("Content retrieved from remote storage...");
		InputStream stream = null;

		try{
			String remotePath = node.getRemotePath();
			System.out.println("REMOTE PATH: " + remotePath);
//			stream = workspace.getStorage().getRemoteFile(remotePath, portalLogin);				
		}catch (Exception e) {
			logger.error("no payload for " +node.getName());
		}	
		return stream;
	}

	//
	//	public static void getData(WorkspaceItem item, String portalLogin) throws InternalErrorException {
	////		String remotePath = null;
	//		Session session = JCRRepository.getSession();
	//		try {
	//			Node node = session.getNodeByIdentifier(item.getId()).getNode("jcr:content");
	//
	////			try {
	////				remotePath = node.getProperty(REMOTE_STORAGE_PATH).getString();
	////				System.out.println("----> " + remotePath);
	////			} catch (PathNotFoundException e) {
	////				logger.trace("Old retrieve content method");
	////			}
	////
	////			// The remote data is stored on GCUBE storage.
	////			if (remotePath != null) {
	////				logger.trace("Content retrieved from remote storage...");
	////				InputStream stream = null;
	////
	////				try{
	////					stream = GCUBEStorage.getRemoteFile(remotePath, portalLogin);				
	////				}catch (Exception e) {
	////					logger.error("no payload for " +node.getPath());
	////				}	
	////				return stream;
	////			} else {
	//
	//				// Move binary content on remote gcube-storage
	//				Binary data =null;
	//			
	//				try {
	//					 data = node.getProperty(DATA).getBinary();	
	//					//					logger.trace("Content moved to remote storage");
	//					//					//					remotePath = Text.getRelativeParent(node.getPath(), 2)
	//					//					//							+ UUID.randomUUID().toString();
	//					//
	//					//					//					remotePath = Text.getRelativeParent(node.getPath(), 2)
	//					//					//							+ node.getIdentifier().toString();
	//					//
	//					//					remotePath = Text.unescapeIllegalJcrChars(node.getPath());
	//					//
	//					//					String url = GCUBEStorage.putStream(data.getStream(), remotePath, portalLogin);					
	//					//
	//					//					logger.trace("New gcube storage url : " + url);
	//					//
	//					//					// Convert url to byte stream in jcr:data binary property
	//					//					// mandatory for nt:resource node
	//					//					ByteArrayInputStream  binaryUrl = new ByteArrayInputStream(url.getBytes());
	//					//					Binary binary = node.getSession().getValueFactory().createBinary(binaryUrl);
	//
	//					// Store URI and remote storage path 
	//					//					node.setProperty(DATA,binary);
	//					//					node.setProperty(REMOTE_STORAGE_PATH, remotePath);
	//					//					session.save();
	//					 
	//					 System.out.println(data.getSize());
	//					if (data!=null){
	//					ItemDelegate itemDelegate = JCRRepository.getServlets().getItemById(item.getId());
	//					String remotePath = itemDelegate.getPath();
	//					
	//					System.out.println(remotePath +" - payload in jackrabbit");
	//					GCUBEStorage.putStream(data.getStream(), remotePath, portalLogin);
	//					JCRWorkspaceItem myItem = (JCRWorkspaceItem) item;
	//					myItem.setRemotePath(remotePath);
	//					}
	//					
	//				} catch (Exception e) {
	//					logger.error("The item doesn't contain " 
	//							+ REMOTE_STORAGE_PATH + " property");
	//					throw new InternalErrorException(e);
	//				}
	////				return data.getStream();
	//		
	//
	//		} catch (Exception e) {
	//			throw new InternalErrorException(e);
	//		} finally {
	//			session.logout();
	//		}
	//	}


	public static  List<String> read() throws InternalErrorException, ItemNotFoundException {

		List<String> list = new ArrayList<String>();
		String csvFile = "/home/valentina/ws-gp.csv";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = "\t";

		try {

			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {

				// use comma as separator
				String[] column = line.split(cvsSplitBy);

				System.out.println(column[0]);
				list.add(column[0]);
				//					String path = "/Home/gianpaolo.coro"+column[0];
				//					WorkspaceItem item = ws.getItemByPath(path);
				//					System.out.println(item.getName());


			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		System.out.println("Done");
		return list;
	}
}
