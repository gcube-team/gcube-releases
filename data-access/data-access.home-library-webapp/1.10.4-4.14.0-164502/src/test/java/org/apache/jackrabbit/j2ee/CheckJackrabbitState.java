package org.apache.jackrabbit.j2ee;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.jackrabbit.j2ee.workspacemanager.storage.GCUBEStorage;
import org.apache.jackrabbit.rmi.repository.URLRemoteRepository;
import org.gcube.common.encryption.StringEncrypter;

import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;

public class CheckJackrabbitState {

	private static final String nameResource 				= "HomeLibraryRepository";
	static int i = 0;
	static int j = 0;

	public static void main(String[] args) throws Exception {


				ScopeProvider.instance.set("/d4science.research-infrastructures.eu");
//		String rootScope = "/gcube";
//		ScopeProvider.instance.set("/gcube");

		SimpleQuery query = queryFor(ServiceEndpoint.class);

		query.addCondition("$resource/Profile/Category/text() eq 'Database' and $resource/Profile/Name eq '"+ nameResource + "' ");

		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);

		List<ServiceEndpoint> resources = client.submit(query);
		Writer writer = null;

		try {
			ServiceEndpoint resource = resources.get(0);

			for (AccessPoint ap:resource.profile().accessPoints()) {

				if (ap.name().equals("JCR")) {

					String url = ap.address();
					//							url = "http://node11.d.d4science.research-infrastructures.eu:8080/jackrabbit-webapp-patched-2.4.3";

					String user = ap.username();						
					String pass = StringEncrypter.getEncrypter().decrypt(ap.password());


					//		String url = "http://node11.d.d4science.research-infrastructures.eu:8080/jackrabbit-webapp-2.8.0/";
					URLRemoteRepository repository = new URLRemoteRepository(url + "/rmi");
					Session session = repository.login( 
							new SimpleCredentials(user, pass.toCharArray()));

					System.out.println(session.getUserID());

					int i = 0;
					writer = new BufferedWriter(new OutputStreamWriter(
							new FileOutputStream("check.txt"), "utf-8"));


					check(session.getRootNode().getNode("Share"), writer, i);

				}
			}
		}finally{
			System.out.println(i);
			try {
				if (writer!=null)
					writer.close();
			} catch (Exception ex) {ex.printStackTrace();}
		}
	}

	//	ScopeProvider.instance.set("/gcube");
	////		ScopeProvider.instance.set("/d4science.research-infrastructures.eu");
	//		UserManager um = HomeLibrary.getHomeManagerFactory().getUserManager();
	//
	//		List<String> users = um.getUsers();
	//		//		String user = "valentina.marioli";
	//
	//		Writer writer = null;
	//
	//		try {
	//			writer = new BufferedWriter(new OutputStreamWriter(
	//					new FileOutputStream("check-jackrabbit-state02.txt"), "utf-8"));
	//			
	//		for(String user:users){
	//
	//			System.out.println(user);
	//			JCRWorkspace ws = (JCRWorkspace) getWorkspace(user);
	//			getFiles(ws.getRoot(), user, writer);
	////			ws.getTrash().emptyTrash();
	//		}
	//
	//		} catch (IOException ex) {
	//			// report
	//		} finally {
	//			try {writer.close();} catch (Exception ex) {ex.printStackTrace();}
	//		}
	//		System.out.println("found " + j + "/" + i + " different");
	//	}



	private static void check(Node node, Writer writer, int i) throws RepositoryException {

		NodeIterator nodes = node.getNodes();
		while (nodes.hasNext()){
			Node child = nodes.nextNode();
			if (!child.getName().startsWith("hl:") && (!child.getName().startsWith("jcr:"))){

				if (child.hasProperty("hl:portalLogin")){
//					System.out.println(child.getProperty("hl:portalLogin").getString());
					if(child.getProperty("hl:portalLogin").getString().equals("francesco.mangiacrapa")){
						if(!child.getPrimaryNodeType().getName().equals("nthl:workspaceSharedItem")){
						
						
						if (child.hasNode("jcr:content")){
							Node content = child.getNode("jcr:content");
							System.out.println(child.getPath() + " : " +child.getPrimaryNodeType().getName());
							if (content.hasProperty("hl:remotePath")){
								String remotePath = content.getProperty("hl:remotePath").getString();
								System.out.println(remotePath);
								
								GCUBEStorage storage = new GCUBEStorage("francesco.mangiacrapa");
								System.out.println(storage.getPublicLink(remotePath));
								
							}
//							 PropertyIterator it = content.getProperties();
//							while(it.hasNext()){
//								System.out.println(it.nextProperty().getName());
//							}
//							System.out.println(child.getProperty("hl:remotePath").getString());
						}
						
					
//						Workspace ws = HomeLibrary
//						.getHomeManagerFactory().getHomeManager().getHome("francesco.mangiacrapa").getWorkspace();
//				WorkspaceItem item = ws.getItem(child.getIdentifier());
//				System.out.println(item.getPath());
						i++;
						}

					}
				}
				check(child, writer, i);
			}

			//			try{
			//				String link = item.getPublicLink(false);
			//				System.out.println(link);
			//
			//			}catch (Exception e){
			//
			//				//						item.remove();
			//				//						System.out.println(" file not found in storage " + item.getPath());
			//				System.out.println("found " + j++ + "/" + i + " files not in storage");
			//
			//				writer.write(remotePath  + "\t" + user);
			//				writer.write("\n");
			//			}

		}
	}



}
