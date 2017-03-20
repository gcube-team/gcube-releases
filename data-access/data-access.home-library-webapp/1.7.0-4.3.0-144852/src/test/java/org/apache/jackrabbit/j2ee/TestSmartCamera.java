package org.apache.jackrabbit.j2ee;
import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.nodetype.NodeType;

import org.apache.jackrabbit.j2ee.workspacemanager.servlets.rest.Utils;
import org.apache.jackrabbit.j2ee.workspacemanager.versioning.JCRVersioning;
import org.apache.jackrabbit.rmi.repository.URLRemoteRepository;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.homelibary.model.items.type.NodeProperty;
import org.gcube.common.homelibary.model.versioning.WorkspaceVersion;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceSharedFolder;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;


public class TestSmartCamera {
	private static final String nameResource 				= "HomeLibraryRepository";
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {



		String rootScope = "/gcube";
		//				String rootScope ="/d4science.research-infrastructures.eu";


		ScopeProvider.instance.set(rootScope);

		SimpleQuery query = queryFor(ServiceEndpoint.class);

		query.addCondition("$resource/Profile/Category/text() eq 'Database' and $resource/Profile/Name eq '"+ nameResource + "' ");

		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);

		List<ServiceEndpoint> resources = client.submit(query);
		//		https://workspace-repository-dev.research-infrastructures.eu

		try {
			ServiceEndpoint resource = resources.get(0);

			for (AccessPoint ap:resource.profile().accessPoints()) {

				if (ap.name().equals("JCR")) {

					String url = ap.address();
					System.out.println(url);
					//							url = "http://node11.d.d4science.research-infrastructures.eu:8080/jackrabbit-webapp-patched-2.4.3";

					String user = ap.username();						
					String pass = StringEncrypter.getEncrypter().decrypt(ap.password());
					//					String user = "admin";						
					//					String pass = "admin";
					//					String url = "http://ws-repo-test.d4science.org/home-library-webapp";

					//		String url = "http://node11.d.d4science.research-infrastructures.eu:8080/jackrabbit-webapp-2.8.0/";
					URLRemoteRepository repository = new URLRemoteRepository(url + "/rmi");
					Session session = repository.login( 
							new SimpleCredentials(user, pass.toCharArray()));

					SecurityTokenProvider.instance.set("aac13fb7-d074-45ae-aa47-61718956a5e6-98187548");
					
					Map<String, Boolean> children = new HashMap<String, Boolean>();
					Workspace workspace = HomeLibrary.getHomeManagerFactory().getHomeManager().getHome().getWorkspace();
					System.out.println("*** Get workspace of " + workspace.getRoot().getPath());
					String absPath = "/Share/042efb85-240b-4a04-93d3-bca3924b7503/camera03/dataset/2016-10-13";
					absPath = Utils.cleanPath(workspace, absPath);
					System.out.println("absPath " +absPath);
					WorkspaceItem item = workspace.getItemByPath(absPath);
					System.out.println("item "+  item.getPath());
					if(item.isFolder()){
						System.out.println("is folder? " + item.isFolder());
						WorkspaceFolder folder = (WorkspaceFolder) item;
						Boolean showHidden = true;
						java.util.List<WorkspaceItem> list = folder.getAllChildren((Boolean) showHidden);
						System.out.println(list.toString());
						for(WorkspaceItem child: list){			
							String name = null;
							if (child.getId().equals(child.getIdSharedFolder())){
								JCRWorkspaceSharedFolder shared = (JCRWorkspaceSharedFolder) child;
								if (shared.isVreFolder())
									name = shared.getDisplayName();
							}
							if (name==null)
								name = child.getName();
							children.put(name, child.isFolder());
						}
					}

				}
			}
		}finally{}
	}


}

