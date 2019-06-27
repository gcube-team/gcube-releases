package org.apache.jackrabbit.j2ee;
import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.lock.Lock;
import javax.jcr.lock.LockManager;

import org.apache.jackrabbit.j2ee.workspacemanager.NodeManager;
import org.apache.jackrabbit.rmi.repository.URLRemoteRepository;
import org.apache.jackrabbit.util.Text;
import org.gcube.common.authorization.client.Constants;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.gcube.common.homelibary.model.items.type.NodeProperty;
import org.gcube.common.homelibrary.home.Home;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.HomeManager;
import org.gcube.common.homelibrary.home.HomeManagerFactory;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.folder.FolderItem;
import org.gcube.common.homelibrary.home.workspace.privilegemanager.PrivilegeManager;
import org.gcube.common.homelibrary.home.workspace.usermanager.UserManager;
import org.gcube.common.homelibrary.util.WorkspaceUtil;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;

import lombok.patcher.Symbols;


public class OffsetTest {
	private static final String nameResource 				= "HomeLibraryRepository";

	private static final String NAME = "ISExporter";
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {



		String rootScope = "/gcube";
		//								String rootScope ="/d4science.research-infrastructures.eu";


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
					//										System.out.println(url);
					//										String	url = "http://node11.d.d4science.research-infrastructures.eu:80/home-library-webapp";
					//					String url = "http://ws-repo-test.d4science.org/home-library-webapp";

					//									String url = "http://workspace-repository-prod1.d4science.org/home-library-webapp";


					//										String admin = ap.username();						
					//										String pass = StringEncrypter.getEncrypter().decrypt(ap.password());
					//
					//					
					//					
					URLRemoteRepository repository = new URLRemoteRepository(url + "/rmi");
					String admin = "workspacerep.imarine";
					String pass = "gcube2010*onan";

					Session session = repository.login( 
							new SimpleCredentials(admin, pass.toCharArray()));


					String id = session.getNode("/Home/valentina.marioli/Workspace/").getIdentifier();
					String login = "valentina.marioli";
					boolean showHidden = false;
					int limit = 0;
					int offset = 0;

					List<ItemDelegate> list = getChildren(session, id, login, showHidden, limit, offset);
					for (ItemDelegate item: list){
						System.out.println(item.getPath());
					}

				}
			}
		}finally{}
	}


	private static List<ItemDelegate> getChildren(Session session, String id, String login, Boolean showHidden, int limit,
			int offset) throws Exception {
		
	
		Node folderNode = session.getNodeByIdentifier(id);
		
		int i = 0;
		int count = 0;
		
		NodeIterator iterator = folderNode.getNodes();
		
		if (limit<1)
			limit = (int) iterator.getSize();
		
		System.out.println(limit);
		List<ItemDelegate> children = new ArrayList<ItemDelegate>();
		while(iterator.hasNext()) {

			Node node = iterator.nextNode();
			
		
			Boolean isHidden = false;

			try {
				if (node.hasProperty(NodeProperty.HIDDEN.toString()))
					isHidden = node.getProperty(NodeProperty.HIDDEN.toString()).getBoolean();

				String path = node.getPath();
				String name = path.substring(path.lastIndexOf('/') + 1);
				//				if ((isHidden && !showHidden) || (name.equals("Trash") || (name.equals("MySpecialFolders") ||(name.startsWith("rep:")) || (name.startsWith("hl:")))))
				if ((isHidden && !showHidden) || (name.startsWith("rep:")) || (name.startsWith("hl:")))
					continue;
				
		
				if (count>limit-1){
					System.out.println(i + ") " + node.getPath() + " STOP");
					break;
				}

				if(i<offset){
					System.out.println(i + ") " + node.getPath() + " skip");
					i++;
					continue;
				}
				
				System.out.println(i + ") " + node.getPath() + " ok");
				
				i++;
				count++;
				
				ItemDelegate item = null;
				NodeManager wrap = new NodeManager(node, login);

				item = wrap.getItemDelegate();
				children.add(item);
			} catch (Exception e) {
				e.printStackTrace();
			}


		}
		return children;
	}






}

