package org.gcube.portlets.user.homelibrary.jcr.manager;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

import org.gcube.common.homelibrary.home.Home;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.User;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongItemTypeException;
import org.gcube.common.homelibrary.home.workspace.sharing.WorkspaceMessage;
import org.gcube.common.homelibrary.home.workspace.sharing.WorkspaceMessageManager;
import org.gcube.common.homelibrary.jcr.JCRUser;
import org.gcube.common.homelibrary.jcr.sharing.JCRWorkspaceMessage;
import org.gcube.common.homelibrary.jcr.sharing.JCRWorkspaceMessageManager;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.homelibrary.jcr.workspace.folder.items.JCRExternalFile;
import org.gcube.common.homelibrary.jcr.workspace.folder.items.JCRFile;
import org.gcube.common.scope.api.ScopeProvider;

public class CheckWSConsistence {
	static JCRWorkspace ws = null;

	public static void main(String[] args) throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException, WrongItemTypeException {

		try {
			createLibrary();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void createLibrary() throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException, InterruptedException, WrongItemTypeException {
		//		ScopeProvider.instance.set("/gcube");
		ScopeProvider.instance.set("/d4science.research-infrastructures.eu");

		String user = "julien.barde";
		//		String user = "gianpaolo.coro";

		Home home = HomeLibrary
				.getHomeManagerFactory()
				.getHomeManager()
				.getHome(user);

		ws = (JCRWorkspace) home.getWorkspace();



		//		WorkspaceFolder folder = null;
		//
		//			folder = (WorkspaceFolder) ws.find("AcAA");
		//		if (folder==null)
		//			System.out.println("item not found");
		WorkspaceFolder folder = (WorkspaceFolder) ws.getItemByPath("/Workspace/");

		Writer writer = null;

		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream("julien-check-ws.txt"), "utf-8"));
			
			getItem(folder, writer);
			
		} catch (IOException ex) {
			// report
		} finally {
			try {writer.close();} catch (Exception ex) {ex.printStackTrace();}
		}

		//		int i = 0;
		//		for(WorkspaceItem child: folder.getChildren()){
		//			System.out.println(child.getRemotePath());
		////			System.out.println(child.getPath());
		////			System.out.println(child.getCreationTime().getTime());
		////
		////			if (!child.isFolder()){
		////				//				System.out.println("*** " + child.getRemotePath());
		////				try{
		////				System.out.println(i + ")*** " + child.getPublicLink(false));	
		////				} catch (Exception e) {
		////					System.out.println("*** -> Error");
		////					System.out.println(child.getRemotePath());
		//////												e.printStackTrace();
		////				}
		////				i++;
		////			}

	}




	//		
	//		System.out.println(i);
	//WorkspaceItem item = ws.getItemByPath("/Workspace/MySpecialFolders/d4science.research-infrastructures.eu-gCubeApps-StockAssessment/Notes/BlueASSESSMENT160428.docx");
	//System.out.println(item.getPath());
	//JCRExternalFile file = (JCRExternalFile) item;
	//System.out.println("*** " +file.getPublicLink());
	//System.out.println("*** " + file.getStorageId());

	//System.out.println(item.getStorageID());
	//System.out.println(item.getRemotePath());

	//		JCRWorkspaceMessageManager messageManager = (JCRWorkspaceMessageManager) ws.getWorkspaceMessageManager();
	//		List<WorkspaceMessage> list = messageManager.getReceivedMessages();
	//		
	//		for (WorkspaceMessage msg : list){
	//			System.out.println(msg.getSubject());
	//			System.out.println(msg.getId());
	//			System.out.println(msg.getSender().getPortalLogin());
	//			System.out.println("**");
	//		
	//			JCRWorkspaceMessage mymsg = (JCRWorkspaceMessage) msg;
	//			String requestId = mymsg.getId();
	//			System.out.println("** GET RECEIVED MESSAGE BY ID " + requestId);
	//			WorkspaceMessage message = messageManager.getReceivedMessage(requestId);
	//			System.out.println(message.getSubject());
	//			
	//		}

	//		 [query: /jcr:root/Home/4.facchini/Workspace/MySpecialFolders/d4science.research-infrastructures.eu-gCubeApps-SoBigData.eu//element(*,nthl:workspaceItem) order by @jcr:lastModified descending - lang: xpath - login: 4.facchini - limit: 6]
	//		  javax.jcr.query.InvalidQueryException: Encountered "/" at line 1, column 36.


	//		System.out.println("ROOT " + ws.getRoot().getId());

	//		WorkspaceFolder folder = (WorkspaceFolder) ws.getItemByPath("/Workspace/BBB");

	//		 List<WorkspaceItem> children = ws.getRoot().getChildren();
	//		 for (WorkspaceItem child: children){
	//			 System.out.println(child.getId());
	//		 }

	//		System.out.println(ws.getRoot().getChildren().size());

	//		String url = "http://data-d.d4science.org/Q0IvYW5jaFphUXJHak9iQ0ZuUnZsU0pveHhNYzhYMm5HbWJQNStIS0N6Yz0";
	//		InputStream in = null;
	//
	//		try{
	//			in = new URL(url).openStream();
	//
	//
	////		String name = WorkspaceUtil.getUniqueName("data", ws.getRoot());
	//			
	//			String name = "doc-" + UUID.randomUUID().toString() + ".odt";
	//			Map<String, String> properties = new HashMap<String, String>();
	//			properties.put("key0", "value0");
	//			properties.put("key1", "value1");
	//			properties.put("key2", "value2");
	//			String mimetype = "application/vnd.oasis.opendocument.text";
	//			FolderItem fileItem = WorkspaceUtil.createExternalFile(ws.getRoot(), name, "de", in, properties, mimetype, 18000);
	//			System.out.println("*************** " + fileItem.getPath());
	//			
	////			System.out.println(fileItem.getProperties().getProperties().size());
	//
	//		} catch (Exception e) {
	//							e.printStackTrace();
	//		}finally{
	//			if (in!=null)
	//				in.close();
	//		}







	private static void getItem(WorkspaceItem item, Writer writer) throws InternalErrorException, IOException {
		WorkspaceFolder folder = (WorkspaceFolder) item;
		for(WorkspaceItem child: folder.getChildren()){
			//			System.out.println(child.getRemotePath());
			//			System.out.println(child.getPath());
			//			System.out.println(child.getCreationTime().getTime());

			if (!child.isFolder()){
				//				System.out.println("*** " + child.getRemotePath());
				try{
					System.out.println("*** " + child.getPublicLink(false));	
				} catch (Exception e) {
					System.out.println("*** -> Error");
					System.out.println(child.getRemotePath()  + "\t" + child.getCreationTime().getTime());

					writer.write(child.getRemotePath()  + "\t" + child.getCreationTime().getTime());
					writer.write("\n");
					//												e.printStackTrace();
				}

			}else
				getItem(child, writer);

		}
	}




}
