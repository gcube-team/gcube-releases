package org.gcube.portlets.user.homelibrary.jcr.manager;

import java.io.IOException;

import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceSharedFolder;
import org.gcube.common.scope.api.ScopeProvider;

public class UnshareTest {
	static JCRWorkspace ws = null;

	public static void main(String[] args) throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException {

		try {
			createLibrary();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void createLibrary() throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException, InterruptedException {
		ScopeProvider.instance.set("/gcube");
		//		ScopeProvider.instance.set("/d4science.research-infrastructures.eu");


		String user = "valentina.marioli";

		ws = (JCRWorkspace) HomeLibrary
				.getHomeManagerFactory()
				.getHomeManager()
				.getHome(user).getWorkspace();

//		System.out.println("ROOT " + ws.getRoot().getId());
		
		JCRWorkspaceSharedFolder folder = (JCRWorkspaceSharedFolder) ws.getItemByPath("/Workspace/AAA");
		folder.unShare();
		
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




	}



}
