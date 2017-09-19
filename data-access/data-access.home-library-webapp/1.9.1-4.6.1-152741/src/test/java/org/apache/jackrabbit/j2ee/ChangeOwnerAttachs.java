package org.apache.jackrabbit.j2ee;
import java.security.MessageDigest;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import org.apache.jackrabbit.rmi.repository.URLRemoteRepository;
import org.gcube.common.scope.api.ScopeProvider;


public class ChangeOwnerAttachs {
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

		//		SecurityTokenProvider.instance.set("97803466-76ff-4cfe-9acc-9d0dbafc3a76-98187548");

		//				String rootScope = "/gcube";
		String rootScope = ("/d4science.research-infrastructures.eu");
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
		//		String url = "http://ws-repo-test.d4science.org/home-library-webapp";
		//		String url = "https://workspace-repository-dev.research-infrastructures.eu/home-library-webapp";

		//					String user = ap.username();						
		//					String pass = StringEncrypter.getEncrypter().decrypt(ap.password());
		String user = "workspacerep.imarine";						
		String pass = "gcube2010*onan";

		//		String url = "http://node11.d.d4science.research-infrastructures.eu:8080/jackrabbit-webapp-2.8.0/";
		URLRemoteRepository repository = new URLRemoteRepository(url + "/rmi");
		//		String user = "test.user";
		//		String pass = getSecurePassword(user);
		Session session = repository.login( 
				new SimpleCredentials(user, pass.toCharArray()));

				NodeIterator homes = session.getRootNode().getNode("Home").getNodes();
				while (homes.hasNext()){
					Node home = homes.nextNode();

//		Node home = session.getRootNode().getNode("Home").getNode("valentina.marioli");
				System.out.println(home.getPath());
		System.out.println("* " + home.getName());
					if (home.getName().startsWith("rep:"))
						continue;
		NodeIterator inbox = home.getNode("InBox").getNodes();
		while (inbox.hasNext()){
			Node msg = inbox.nextNode();
			//				System.out.println(msg.getPath());
			if (msg.hasNode("hl:attachments")){
				NodeIterator attachs = msg.getNode("hl:attachments").getNodes();
				while(attachs.hasNext()){
					Node attach = attachs.nextNode();
					//					System.out.println(attach.getPrimaryNodeType().getName());
									System.out.println("-->" + attach.getPath());
//									
//									PropertyIterator it = attach.getProperties();
//									while(it.hasNext()){
//										Property prop = it.nextProperty();
//										System.out.println(prop.getName() + ": " + prop.getString());
//									}
									
					try{

//						if (attach.hasProperty("hl:portalLogin"))
//							System.out.println("* " + attach.getProperty("hl:portalLogin").getString());
						
//						if (attach.hasNode("hl:owner"))
//							if (attach.getNode("hl:owner").hasProperty("hl:portalLogin"))
//								System.out.println("** " + attach.getNode("hl:owner").getProperty("hl:portalLogin").getString());


						if (!attach.getProperty("hl:portalLogin").getString().equals(home.getName())){
							System.out.println(attach.getProperty("hl:portalLogin").getString()+ " - " + attach.getPath() );

															attach.setProperty("hl:portalLogin",home.getName() );
							//								System.out.println(attach.getProperty("hl:portalLogin").getString());
							System.out.println("--> update owner");
															attach.getSession().save();
						}


					} catch (Exception e) {

//						if (attach.hasNode("hl:owner"))
//							if (attach.getNode("hl:owner").hasProperty("hl:portalLogin"))
//								System.out.println(attach.getNode("hl:owner").getProperty("hl:portalLogin").getString());

						try{
							System.out.println(attach.getNode("hl:owner").getProperty("hl:portalLogin").getString() + " - " + attach.getPath());
							if (!attach.getNode("hl:owner").getProperty("hl:portalLogin").getString().equals(home.getName())){
																	attach.getNode("hl:owner").setProperty("hl:portalLogin", home.getName());
								//									System.out.println(attach.getNode("hl:owner").getProperty("hl:portalLogin").getString());
								System.out.println("--> update owner");
																	attach.getSession().save();
							}
						} catch (Exception e1) {
								System.out.println("NO owner " + " - " + attach.getPath());
								attach.setProperty("hl:portalLogin",home.getName() );
								attach.getSession().save();
						}



					}
				}
			}
						}
		}
	}
}
