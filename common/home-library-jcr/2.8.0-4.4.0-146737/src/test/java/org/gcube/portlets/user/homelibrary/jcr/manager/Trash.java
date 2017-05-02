package org.gcube.portlets.user.homelibrary.jcr.manager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.Session;

import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSharedFolder;
import org.gcube.common.homelibrary.home.workspace.trash.WorkspaceTrashFolder;
import org.gcube.common.homelibrary.home.workspace.trash.WorkspaceTrashItem;
import org.gcube.common.homelibrary.jcr.repository.JCRRepository;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceSharedFolder;
import org.gcube.common.homelibrary.jcr.workspace.trash.JCRWorkspaceTrashFolder;
import org.gcube.common.scope.api.ScopeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author valentina
 *
 */
public class Trash {


	protected static Logger logger = LoggerFactory.getLogger(Trash.class);

	public static void main(String[] args) {

		try {

			ScopeProvider.instance.set("/gcube/devsec");


			Workspace ws = HomeLibrary
					.getHomeManagerFactory()
					.getHomeManager()
					.getHome("valentina.marioli")
					.getWorkspace();

//			System.out.println(ws.getRoot().getPath());
//			
//			List<WorkspaceItem> children = ws.getRoot().getChildren();
//			for (WorkspaceItem child: children){
//				try{
//					child.remove();
//				}catch (Exception e) {
//					// TODO: handle exception
//				}
//			}
				
		JCRWorkspaceTrashFolder trash = (JCRWorkspaceTrashFolder) ws.getTrash();
		trash.emptyTrash();
//			trash.restoreAll();
			
//			WorkspaceItem item = ws.getItemByPath("/Workspace/a");		
//			item.remove();
			
//			WorkspaceItem item = ws.getItemByPath("/Workspace/cccc");
//			WorkspaceItem destinationFolder = ws.getItemByPath("/Workspace/testMove");
//			List<String> users = new ArrayList<String>();
//			users.add("federico.mameli");
//			ws.shareFolder(users, destinationFolder.getId());
//			ws.moveItem(item.getId(), destinationFolder.getId());
			
//			WorkspaceSharedFolder item = (WorkspaceSharedFolder) ws.getItemByPath("/Workspace/testmyFolder");
//			item.unShare();
//			ws.unshare(item.getId());
//			item.remove();
//			WorkspaceFolder item = (WorkspaceFolder) ws.getItemByPath("/Workspace/test001");		
////			System.out.println(item.getUsers().toString());
//			List<String> users = new ArrayList<String>();
//			users.add("roberto.cirillo");
////			item.share(users);
//			ws.shareFolder(users, item.getId());

//		ws.unshare(item.getId());
//			ws.removeItem(item.getId());
//			item.remove();
//			item.unShare();
			
//
//			WorkspaceItem item =  ws.getItemByPath("/Workspace/testShare00/bookswap.jpg");
//			System.out.println(item.getRemotePath());
			
			
//			Session session = JCRRepository.getSession();

			
//			for ( WorkspaceTrashItem child : trash.listTrashItems()){				
//				System.out.println("* " + child.getName() + " - id: " + child.getId() );
//				if (child.getName().equals("vv")){
//					child.restore();
//			
//				}
//			}

//				System.out.println("getDeletedFrom " + child.getDeletedFrom() + " - id: " +child.getOriginalParentId()) ;
//				System.out.println("getDeletedBy " + child.getDeletedBy());
//				System.out.println("getDeletedTime " + child.getDeletedTime().getTime());
//				System.out.println("isFolder? " + child.isFolder());
				

//				child.deletePermanently();
				
				
//				Node node = session.getNodeByIdentifier(child.getId());				
////				String time = timeStampRename();
//				System.out.println(time + child.);
//			
////				ws.renameItem(node.getIdentifier(), time + "_" + child.getName());
//				
//				System.out.println("child.getOriginalParentId(): " + child.getOriginalParentId());
//				System.out.println("child.getOriginalPath(): " + child.getOriginalPath());
//				System.out.println("-> " + child.getPath() + " - child.getType(): " + child.getType());
//				System.out.println("getId: " + child.getId() + " - child.getName(): " + child.getName());
//				System.out.println("getMimeType: " + child.getMimeType() );				
//				
//				System.out.println("************************* get by id *********************");
//				WorkspaceTrashItem ti = trash.getTrashItemById(child.getId());
//				System.out.println("getDeleteUser: " + ti.getDeleteUser());
//				System.out.println("getMimeType: " + ti.getMimeType());
//				System.out.println("getDescription: " + ti.getDescription());
//				System.out.println("getIdSharedFolder: " + ti.getIdSharedFolder());
//				System.out.println("getOriginalParentId: " + ti.getOriginalParentId());
//				System.out.println("**********************************************");
//				System.out.println("\n");
				
//				child.restore();
				
//			}			

//			System.out.println("end");

		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * Get Time Stamp to rename a file
	 * @return
	 */
	private static String timeStampRename() {
//		"2012_04_05_11400029_MyAwesomeFile.txt"
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HHmmssss_");
		String formattedDate = sdf.format(date);
//		System.out.println(formattedDate);
		return formattedDate;
		
	}

}



