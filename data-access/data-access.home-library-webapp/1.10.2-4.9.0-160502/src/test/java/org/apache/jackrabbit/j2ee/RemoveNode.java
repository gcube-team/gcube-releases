package org.apache.jackrabbit.j2ee;
import java.security.MessageDigest;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.lock.Lock;
import javax.jcr.lock.LockManager;

import org.apache.jackrabbit.rmi.repository.URLRemoteRepository;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.api.ScopeProvider;


public class RemoveNode {
	private static final String nameResource 				= "HomeLibraryRepository";
	public static final String HL_NAMESPACE					= "hl:";
	public static final String JCR_NAMESPACE				= "jcr:";
	public static final String REP_NAMESPACE				= "rep:";
	private static final String NT_NAMESPACE 				= "nt:";
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

		//				SecurityTokenProvider.instance.set("8920abf2-54e5-4e35-82ae-abd31dca65c2-98187548");

		//				String rootScope = "/gcube";
		//				String rootScope = ("/d4science.research-infrastructures.eu");
		//		ScopeProvider.instance.set("/gcube/devNext/NextNext");
		//		ScopeProvider.instance.set("/gcube/preprod/preVRE");
		//		ScopeProvider.instance.set("/d4science.research-infrastructures.eu");

		//		SimpleQuery query = queryFor(ServiceEndpoint.class);
		//
		//		query.addCondition("$resource/Profile/Category/text() eq 'Database' and $resource/Profile/Name eq '"+ nameResource + "' ");
		//
		//		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);
		//
		//		List<ServiceEndpoint> resources = client.submit(query);
		//		Session session =null;
		//
		//		try {
		//			ServiceEndpoint resource = resources.get(0);
		//
		//			for (AccessPoint ap:resource.profile().accessPoints()) {
		//
		//				if (ap.name().equals("JCR")) {
		//
		//					String url = ap.address();

		String url = "http://node76.p.d4science.research-infrastructures.eu:8080/home-library-webapp/";
		//		String url = "http://ws-repo-test.d4science.org:8080/home-library-webapp";
		//		String url = "https://workspace-repository-dev.research-infrastructures.eu/home-library-webapp";

		//					String user = ap.username();						
		//					String pass = StringEncrypter.getEncrypter().decrypt(ap.password());
		String user = "workspacerep.imarine";						
		String pass = "gcube2010*onan";

		//		String url = "http://node11.d.d4science.research-infrastructures.eu:8080/home-library-webapp/";
		URLRemoteRepository repository = new URLRemoteRepository(url + "/rmi");
		//		String user = "test.user";
		//		String pass = getSecurePassword(user);
		Session session = repository.login( 
				new SimpleCredentials(user, pass.toCharArray()));

		System.out.println(session.getRootNode().getPath());
		//				Node node = session.getRootNode().getNode("Home/valetina.marioli");
		Node node = session.getNode("/Home/gianpaolo.coro/Workspace/DataMiner-toRemove");
		System.out.println("remove");
		node.remove();
		
//		session.move(node.getPath(), node.getParent().getPath()+ "/DataMiner-toRemove");
		System.out.println("save");
		session.save();
		System.out.println("done");
		
//		System.out.println(node.getParent().getPath());
//		LockManager lm = session.getWorkspace().getLockManager();
//		
//		
//		NodeIterator iterator = node.getNodes();
//		while(iterator.hasNext()){
//			Node child = iterator.nextNode();
//			if (child.getName().startsWith("hl:"))
//				continue;
//			
//			System.out.println(child.getName());
//			lm.addLockToken(lm.getLock(child.getPath()).getLockToken());
//			lm.unlock(child.getPath());
//		
//			System.out.println("remove " + child.getName());
//			child.remove();
//			System.out.println("done");
//			session.save();
//		}
		//		NodeIterator it = node.getNodes();
		//		System.out.println(node.getPath());

		//		while (it.hasNext()){
		//			Node child = it.nextNode();
		////			if (child.getName().equals("massimiliano.assante"))
		////				continue;
		////			System.out.println(child.getName());
		////			System.out.println("Home/"+child.getName()+"/Workspace/DataMiner");
		//			try{
		//			Node dataMiner = session.getNode("/Home/" +child.getName()+"/Workspace/DataMiner");
		//			System.out.println(dataMiner.getPath());
		//			dataMiner.remove();
		//			session.save();
		//			}catch (Exception e){
		////				System.out.println("DataMiner folder is not in " + child.getPath());
		//			}
		////			if (!child.getName().startsWith("hl:")){
		////				LockManager ocm = session.getWorkspace().getLockManager();
		////				ocm.unlock(child.getPath());
		////				//			Lock lock = ocm.lock(child.getPath(), true, true);
		////				//			 ocm.update(child.getPath());
		////				//			ocm.save();
		////				//			ocm.unlock(child.getParent().getPath(),
		////				//			lock.getLockToken()); 
		////			}
		//
		//			//			if (!child.getName().startsWith("hl:")
		//			//					&& !child.getName().equals("LISTTABLES_ID_af8af2f2-9878-46c0-aa04-dc5468acdf27") 	
		//			//					&& !child.getName().equals("LISTDBINFO_ID_102df871-2ce3-4568-904d-ad276f01dcac")
		//			//					&& !child.getName().equals("LISTDBSCHEMA_ID_6f81b10c-6f37-41db-8c2e-84588ac0f79f")
		//			//					&& !child.getName().equals("LISTTABLES_ID_88b8c3e8-2972-4cd7-8922-c1abdfef8012")
		//			//					&& !child.getName().equals("LISTTABLES_ID_1e2e811b-4b8e-431a-a755-2b892ecab02f")
		//			//					)
		//			//				session.removeItem(child.getPath());
		//			//			session.save();
		//		}
		//		Node item = session.getNode("/Home/valentina.marioli/Workspace/Trash/");
		//
		//		String id = "8cf586e5-45e5-430f-980b-38351f5a761b";
		//
		//		System.out.println(item.getPath());
		//		System.out.println(item.getPath());
		//				NodeIterator iterator = root.getNodes();
		//				while(iterator.hasNext()){
		//					Node node = iterator.nextNode();
		//		session.removeItem("8cf586e5-45e5-430f-980b-38351f5a761b");

		//		session.removeItem(node.getPath());

		//		item.remove();
		//		node.remove();
		//		//		session.removeItem(node.getPath());
		//
		session.save();
		//				}


	}
}
