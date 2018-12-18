package org.apache.jackrabbit.j2ee;
import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;

import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.lock.Lock;
import javax.jcr.lock.LockException;
import javax.jcr.lock.LockManager;

import org.apache.jackrabbit.rmi.repository.URLRemoteRepository;
import org.gcube.common.authorization.client.Constants;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.encryption.StringEncrypter;
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


public class Unlock {
	private static final String nameResource 				= "HomeLibraryRepository";

	private static final String NAME = "ISExporter";
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {



		//		String rootScope = "/gcube";
		String rootScope ="/d4science.research-infrastructures.eu";


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
					//					System.out.println(url);
					//					String	url = "http://node11.d.d4science.research-infrastructures.eu:80/home-library-webapp";
					//					String url = "http://ws-repo-test.d4science.org/home-library-webapp";
					//					String admin = ap.username();						
					//					String pass = StringEncrypter.getEncrypter().decrypt(ap.password());
					//
					//					
					//					
					URLRemoteRepository repository = new URLRemoteRepository(url + "/rmi");
					String admin = "workspacerep.imarine";
					String pass = "gcube2010*onan";

					//										String url = "http://node76.p.d4science.research-infrastructures.eu:8080/home-library-webapp";
					Session session = repository.login( 
							new SimpleCredentials(admin, pass.toCharArray()));

					//					Node item = session.getNode("/Home/gianpaolo.coro/Workspace/MySpecialFolders/d4science.research-infrastructures.eu-gCubeApps-KnowledgeBridging/CNR - 20h Course (5 Moduled) @ PhD Pisa - Biodiversity Conservation/BiodivConservation_Module 4 - ecological niche modelling.pptx");
//										Node node = session.getNodeByIdentifier("b89817af-24f9-433c-ad4d-592f41580226");
//										Node node = session.getNodeByIdentifier("e47db4b3-3d01-47cc-a995-8bafea14d23a");

//					Node node = session.getNodeByIdentifier("1e451d56-b206-41c4-a0eb-277e1a67a4a9");
										Node node = session.getNodeByIdentifier("b038931b-be8b-4f4c-bafe-61eb78acf841");
					//					

					System.out.println(node.getPath());

//					System.out.println(node.isLocked());
//
//					LockManager lockManager = session.getWorkspace().getLockManager();
//					Lock lock = null;
//					try {
//						lock = lockManager.getLock(node.getPath());
//					} catch (LockException ex) {                    
//					}
//					if (lock != null) {
//						lockManager.addLockToken(lock.getLockToken());
//						lockManager.unlock(node.getPath());
//					} 


					//					LockManager lm = session.getWorkspace().getLockManager();
					//					Lock lock =	lm.getLock(item.getPath());
					//					System.out.println(lock.getLockToken());
					////					Lock lock =	item.getLock();
					//					  lm.addLockToken(lock.getLockToken()); 
					////					  session.addLockToken(lock.getLockToken());
					////					  item.unlock();
					//					  lm.unlock(item.getPath()); 
					//					Lock lock = item.getLock();


					//					 LockManager lockManager = null;
					//				        try {
					//				        	
					//				                lockManager = session.getWorkspace().getLockManager();
					//				                lockManager.addLockToken(lock.getLockToken());
					//				            }
					//				            
					//				            

					//					
					//				
					//					
					//					session.addLockToken(lock.getLockToken());
					//					
					//					try {
					//					item.unlock();
					//					}catch (Exception e) {
					//					    System.out.println(e);
					//					}
					//					lm.unlock(item.getPath());

				}


			}
		}finally{}
	}



}

