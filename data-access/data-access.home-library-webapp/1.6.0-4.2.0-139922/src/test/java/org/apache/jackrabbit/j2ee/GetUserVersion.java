package org.apache.jackrabbit.j2ee;
import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.net.MalformedURLException;
import java.util.List;
import javax.jcr.RepositoryException;
import javax.jcr.SimpleCredentials;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;

import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.jackrabbit.core.SessionImpl;
import org.apache.jackrabbit.rmi.repository.URLRemoteRepository;
import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;


public class GetUserVersion {
	private static final String nameResource 				= "HomeLibraryRepository";
	
	public static final String VERSION_LABEL					= "hl:version";
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
		String version = null;

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
					SessionImpl session = (SessionImpl) repository.login( 
							new SimpleCredentials(user, pass.toCharArray()));

					final UserManager userManager = session.getUserManager();

					String login = "test.adsfdsdfsd";
					Authorizable authorizable = userManager.getAuthorizable(login);

					if (authorizable.isGroup()) {			
						Group group = (Group) authorizable;
						Value[] versionValue = group.getProperty(VERSION_LABEL);
						version = getVersion(versionValue);

					}
					else{
						User user1 = (User) authorizable;
						Value[] versionValue = user1.getProperty(VERSION_LABEL);
						version = getVersion(versionValue);
					}
					
					System.out.println("version: " + version);



				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}


	}
	
	private static String getVersion(Value[] versionValue) throws ValueFormatException, IllegalStateException, RepositoryException {

		String version = null;
		int size = versionValue.length;
		for (int i=0; i< size; i++){
			version = versionValue[i].getString();	
		}
		return version;

	}

}
