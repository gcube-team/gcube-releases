package org.apache.jackrabbit.j2ee;
import java.security.MessageDigest;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
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

				SecurityTokenProvider.instance.set("8920abf2-54e5-4e35-82ae-abd31dca65c2-98187548");

		//				String rootScope = "/gcube";
//		String rootScope = ("/d4science.research-infrastructures.eu");
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

//		String url = "http://node76.p.d4science.research-infrastructures.eu:8080/home-library-webapp/";
//				String url = "http://ws-repo-test.d4science.org/home-library-webapp";
		//		String url = "https://workspace-repository-dev.research-infrastructures.eu/home-library-webapp";

		//					String user = ap.username();						
		//					String pass = StringEncrypter.getEncrypter().decrypt(ap.password());
		String user = "workspacerep.imarine";						
		String pass = "gcube2010*onan";

				String url = "http://node11.d.d4science.research-infrastructures.eu:8080/home-library-webapp/";
		URLRemoteRepository repository = new URLRemoteRepository(url + "/rmi");
		//		String user = "test.user";
		//		String pass = getSecurePassword(user);
		Session session = repository.login( 
				new SimpleCredentials(user, pass.toCharArray()));

				System.out.println(session.getRootNode().getPath());
//				Node node = session.getRootNode().getNode("Home/valetina.marioli");
				Node node = session.getRootNode().getNode("Home/gianpaolo.coro/Workspace/DataMiner/Computations");
//				NodeIterator iterator = root.getNodes();
//				while(iterator.hasNext()){
//					Node node = iterator.nextNode();
					System.out.println(node.getPath());
					node.remove();
					session.save();
//				}
				
	
	}
}
