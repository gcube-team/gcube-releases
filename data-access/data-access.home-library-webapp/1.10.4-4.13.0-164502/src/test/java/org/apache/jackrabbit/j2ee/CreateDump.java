package org.apache.jackrabbit.j2ee;
import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.io.File;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import org.apache.jackrabbit.rmi.repository.URLRemoteRepository;
import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;


public class CreateDump {
	static Session session;
	static String repositorybasexpath = "/Home/";
	private static final String nameResource 				= "HomeLibraryRepository";
	/**
	 * @param args
	 * @throws MalformedURLException 
	 */
	public static void main(String[] args) throws MalformedURLException {



		String rootScope = "/gcube";


		ScopeProvider.instance.set(rootScope);

		SimpleQuery query = queryFor(ServiceEndpoint.class);

		query.addCondition("$resource/Profile/Category/text() eq 'Database' and $resource/Profile/Name eq '"+ nameResource + "' ");

		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);

		List<ServiceEndpoint> resources = client.submit(query);


		try {
			ServiceEndpoint resource = resources.get(0);

			for (AccessPoint ap:resource.profile().accessPoints()) {

				if (ap.name().equals("JCR")) {

					//					String url = ap.address();
					//							url = "http://node11.d.d4science.research-infrastructures.eu:8080/jackrabbit-webapp-patched-2.4.3";
					String user = ap.username();						
					String pass = StringEncrypter.getEncrypter().decrypt(ap.password());
					String url = "http://ws-repo-test.d4science.org/home-library-webapp";
					//		String url = "http://node11.d.d4science.research-infrastructures.eu:8080/jackrabbit-webapp-2.8.0/";


					String jackrabbitconfig = "jackrabbit/repository.xml";
					String jackrabbithome = "jackrabbit/repository/";


//					TransientRepository repository = new TransientRepository(jackrabbitconfig, jackrabbithome);
					
//					Repository repository = JcrUtils.getRepository();
					
									URLRemoteRepository repository = new URLRemoteRepository(url + "/rmi");
					session = repository.login( 
							new SimpleCredentials(user, pass.toCharArray()));

					System.out.println("Root: " + session.getRootNode().getPath());
					



					//					String type = "nthl:timeSeriesItem";
					//					
					//					String query1 = "/jcr:root/Home" +
					//							"//element(*,nthl:timeSeriesItem)";
					//					QueryManager queryManager = session.getWorkspace().getQueryManager();
					//					root
					//					javax.jcr.query.Query q =  queryManager.createQuery(query1, javax.jcr.query.Query.XPATH);
					//					
					//					QueryResult result = q.execute();
					//
					//					NodeIterator iterator = result.getNodes();
					//					while (iterator.hasNext()) {
					//
					//						Node node = iterator.nextNode();
					//
					//						System.out.println(node.getPath());
					//						node.remove();
					//						session.save();
					//
					//					}


										 NodeIterator iterator1 = session.getRootNode().getNodes();
								            while(iterator1.hasNext()){
								            	Node node = iterator1.nextNode();
								            	 System.out.println("child: " + node.getPath());
								            }

								            doExport("test-homea.xml");
					//					File targetDir;
					//					Object target = RepositoryConfig.install(targetDir);
					//
					//
					//					System.out.println("Creating a repository copy in " + targetDir);
					//
					//					RepositoryImpl source;
					//					RepositoryCopier copier = new RepositoryCopier(source, target);
					//					copier.copy();
					//
					//					System.out.println("The repository has been successfully copied.");


				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}




	}


	public static void doExport(String filepath) throws Exception {
		File f = new File(filepath);
		if (f.exists()) {
			throw new IllegalArgumentException("Export file "+filepath+" is existing, can not export");
		}
		try {
			FileOutputStream os = new FileOutputStream(f);
			//export all including binary, recursive
			session.exportSystemView(repositorybasexpath, os, false, false);
			os.close();
		} catch(Throwable t) {
			throw new Exception("Failed to export repository at "+ repositorybasexpath +" to file "+filepath+"\n"+t.toString(), t);
		}
		System.out.println("Exported the repository to "+f);
	}

	public static void doExportDocument(String filepath) throws Exception {
		File f = new File(filepath);
		if (f.exists()) {
			throw new IllegalArgumentException("Export file "+filepath+" is existing, can not export");
		}
		try {
			FileOutputStream os = new FileOutputStream(f);
			//export all including binary, recursive
			session.exportDocumentView(repositorybasexpath, os, false, false);
			os.close();
		} catch(Throwable t) {
			throw new Exception("Failed to export repository at " +
					repositorybasexpath + " to file "+filepath+"\n"+t.toString(), t);
		}
		System.out.println("Exported the repository to "+f);
	}



}
