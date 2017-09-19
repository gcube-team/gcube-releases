package org.apache.jackrabbit.j2ee.oak;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.jcr.AccessDeniedException;
import javax.jcr.Item;
import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.Workspace;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;

import org.apache.commons.httpclient.HttpException;
import org.apache.jackrabbit.rmi.repository.URLRemoteRepository;
import org.gcube.common.homelibary.model.items.type.NodeProperty;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.model.exceptions.RepositoryException;

public class RemoveInvalidNodes {
		private static final String URL = "http://workspace-repository-prod1.d4science.org/home-library-webapp";

//	private static final String URL = "http://node76.p.d4science.research-infrastructures.eu:8080/home-library-webapp";
	//	private static final String URL = "http://localhost:8080/jackrabbit-webapp-2.14.0";
	//	private static final String USER ="admin";
	//	private static final String PASS = "admin";
	private static final String USER ="workspacerep.imarine";
//		private static final String PASS ="workspacerep.imarine";
	private static final String PASS = "gcube2010*onan";

	static Session session =  null;
	private static Workspace workspace;

	public static void main(String[] args) throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, InterruptedException {

		try {

			try{
				URLRemoteRepository repository = new URLRemoteRepository(URL + "/rmi");
				session = repository.login( 
						new SimpleCredentials(USER, PASS.toCharArray()));

				workspace = session.getWorkspace();


				//				System.out.println(session.getNodeByIdentifier("6ad60ae0-73af-4641-be8b-3b4ba52f18a7").getPath());

				NodeIterator nodes = session.getRootNode().getNodes();
				while (nodes.hasNext()){
					
					Node child = nodes.nextNode();
//					System.out.println(child.getPath());
					if (child.getPath().endsWith("]")){
						System.out.println(child.getPath());
						child.remove();
						
//	
					}
					
				}
					
//				
//				Rimuovi:
//					 
//					/default/Home/leonardo.candela/Workspace/MySpecialFolders/d4science.research-infrastructures.eu-gCubeApps-BiodiversityResearchEnvironment
//					/Home/d4science.research-infrastructures.eu-gCubeApps-BiodiversityResearchEnvironment-Manager

					
					
				session.getNode("/Applications").remove();
				session.getNode("/Groups").remove();
//				session.getNode("/9539f96").remove();
				session.save();
				
				
				session.getNodeByIdentifier("12a3fb8f-1690-472d-8e6b-4567dab09037").remove();
				session.getNodeByIdentifier("97047ff5-3e9a-4a30-8f91-0403c453f20f").remove();
				session.getNodeByIdentifier("da485525-7cde-4724-90fe-b1bb739afeff").remove();
				session.getNodeByIdentifier("012f369b-aaba-4eb3-b606-7cada334eadc").remove();
				session.getNodeByIdentifier("dde57ba5-177b-495b-af50-3e3c6a421595").remove();
				session.getNodeByIdentifier("dad49277-8760-4cd3-90f8-b9c74b457cfa").remove();
				session.getNodeByIdentifier("bc7ba61c-7778-435a-9252-4a9e17c94bd9").remove();
				session.getNodeByIdentifier("85fa022a-b067-4d0f-8dfb-ad760230c5a8").remove();
				session.getNode("/Share/f5841977-3490-4abb-8529-a00243173367").remove();
				session.getNode("/Share/8a0f2b0f-ec7e-43c6-82ff-43802604323e").remove();
				session.getNode("/Share/a1236f36-bb90-407b-9c0f-615fadc99006").remove();
				

				
				
				//inconsistent nodes

				session.getNode("/Share/9ced7320-5571-4989-b30e-a380bb9c2643").remove();
				session.getNode("/Share/2e23fc95-eac6-4d73-bfa9-b46a8b5c0087").remove();
				session.getNode("/Share/fd0f27d8-1579-4290-9f65-5b4aa1a351cc").remove();
				session.getNode("/Share/5e06e84e-584e-4cd4-bc54-7501b276e871").remove();
				session.getNode("/Share/96e8fe2b-5099-4b8c-b896-dd3a926214e4").remove();
				session.getNode("/Share/9286bc6e-0b2d-4569-834d-5b7189e6c2c9").remove();
				session.getNode("/Share/b6e5dae3-9993-413f-8e1f-e8ff1cbba3a2").remove();
				session.getNode("/Share/e8fd1fed-78b1-4a45-8b76-5a751b75da7b").remove();
				session.getNode("/Share/d140df1c-1abb-490e-bd9b-14f5ee80ebab").remove();
				session.getNode("/Share/38ed819e-97ad-4d08-889f-10a7049a28b3").remove();
				session.getNode("/Share/5e27e1c8-ba5c-48d2-9498-7e2ebf663fc5").remove();
				session.getNode("/Share/7c42b9ae-232e-4c5b-82ea-ec47225401ab").remove();
				session.getNode("/Share/cef2bc07-f362-40f5-9fd0-6e772a8a3776").remove();
				session.getNode("/Share/c7f1b7e7-9abe-4b2e-bbf3-c8e0cd690ac6").remove();
				session.getNode("/Share/0a097057-15aa-4b1a-b9a7-cef7877afaf6").remove();
				session.getNode("/Share/7041da88-b0d7-4aa4-a688-488184fca48c").remove();
				session.getNode("/Share/7aca634f-cb73-43d1-acb4-a7f87b1881f5").remove();
				session.getNode("/Share/c497e298-0991-4e42-9fc6-a7916b3871b6").remove();
				session.getNode("/Share/f346ee37-e380-4097-aa27-9a501b263764").remove();
				session.getNode("/Share/dc301101-3d22-45b5-8916-d069755bac0f").remove();
				session.getNode("/Share/eb0fa5b9-411f-44f5-9292-1021b064820c").remove();
				session.getNode("/Share/0097c5d6-6d30-47b1-913f-eea7bde31dda").remove();
				session.getNode("/Share/835b43fc-3ed0-464b-9b7d-32b66aa755a0").remove();
				session.getNode("/Share/353550e7-a44d-4d4c-bf1a-a72833ac23b7").remove();
				session.getNode("/Share/86f53664-3425-4897-bf04-233a872aed6f").remove();
				session.getNode("/Share/d6a1e479-7ebf-4845-a9fa-cf4b3722fdc6").remove();
				session.getNode("/Share/667ffc3a-8010-4e04-a9f9-4322fb821ce5").remove();
				session.getNode("/Share/351586db-a432-4458-b5df-4ef65b06af9a").remove();
				session.getNode("/Share/53fd2ac3-be6f-40e7-8c0b-fcdc216f8c81").remove();
				session.getNode("/Share/e259bac3-19dc-45e3-9401-bf6c2991fbb6").remove();
				session.getNode("/Share/bfa3eff4-2a93-449f-be4b-f8e3874d14d2").remove();
				session.getNode("/Share/564e8008-7c7f-44ae-8e7d-0efa63164b08").remove();
				session.getNode("/Share/3d7fc16b-a12d-4169-b407-d5f1235712ed").remove();
				session.getNode("/Share/e5238d06-c896-4cf5-906a-a786fbbe99fe").remove();
				session.getNode("/Share/da4bcfd5-299b-4e3d-ae8b-076e3c577c89").remove();
				session.getNode("/Share/81821cae-9771-4812-a0f1-467f21ffaa70").remove();

				
				session.save();
				
				//				reader();
			} finally {
				if (session!=null)
					session.logout();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private static void reader() throws ItemNotFoundException, org.gcube.common.homelibrary.model.exceptions.RepositoryException, InternalErrorException, WorkspaceFolderNotFoundException, HomeNotFoundException, UserNotFoundException, IOException, javax.jcr.RepositoryException {

		try {
			Node sharedNode = null;
			//			Node mynode = session.getNode("/Home/franco.zoppi/Workspace/MySpecialFolders/d4science.research-infrastructures.eu-gCubeApps-VesselActivitiesAnalyzer");
			//			String id = mynode.getIdentifier();
			try{
				String id = "";
				//				System.out.println("-> id " + id);
				sharedNode = session.getNodeByIdentifier(id);
				System.out.println(sharedNode.getPath());
				Map<String, String> userMap = getMap(sharedNode);
				Set<String> keys = userMap.keySet();

				for (String user: keys){

					String value = userMap.get(user);

					String[] values = null;
					if (value!=null){
						values = value.split("/");
						if (values.length < 2)
							throw new InternalErrorException("Path node corrupt");
					}

					String parentId = values[0];
					String nodeName = values[1];

					String parentNode = getItemById(parentId);

					clone(sharedNode, parentNode+ "/"+nodeName);
				}

				System.out.println("remove shared folder");

				sharedNode.remove();
				session.save();

			} catch (Exception e) {
				e.printStackTrace();
			}



			//				System.out.println("__________________________________");
			//			}

		} catch (Exception e) {
			e.printStackTrace();
		} 
	}


	private static String getItemById(String parentId) throws MalformedURLException, javax.jcr.RepositoryException {
		Node parent = null ;
		String path = null;
		try{
			parent = session.getNodeByIdentifier(parentId);
			path = parent.getPath();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return path;

	}


	public static Map<String, String> getMap(Node node) throws RepositoryException,
	InternalErrorException, ItemNotFoundException, HttpException, IOException, javax.jcr.RepositoryException {

		try{
			Map<String, String> map = new HashMap<String, String>();

			PropertyIterator users = node.getNode("hl:users").getProperties();
			while(users.hasNext()){

				Property prop = users.nextProperty();

				if (prop.getName().startsWith("jcr:"))
					continue;
				map.put(prop.getName(), prop.getString());

			}
			return map;

		} finally {

		}
	}


	public static void clone(Node node,  String destAbsPath) throws RepositoryException,
	InternalErrorException, ItemNotFoundException, HttpException, IOException, javax.jcr.RepositoryException {

		try{

			String srcAbsPath = node.getPath();
			//			Node srcNode = session.getNode(srcAbsPath);
			//			//			System.out.println("srcNode: "+ srcAbsPath + " - id:" + srcNode.getIdentifier());
			//			System.out.println("Clone from "+srcAbsPath + " to " + destAbsPath);

			//			workspace.clone(workspace.getName(), srcAbsPath, destAbsPath, false);

		} finally {

		}

	}
}
