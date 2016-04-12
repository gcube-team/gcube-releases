package org.apache.jackrabbit.j2ee;
import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.net.MalformedURLException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.nodetype.NodeType;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.jackrabbit.core.SessionImpl;
import org.apache.jackrabbit.j2ee.workspacemanager.NodeManager;
import org.apache.jackrabbit.j2ee.workspacemanager.post.CreateReference;
import org.apache.jackrabbit.rmi.repository.URLRemoteRepository;
import org.apache.jackrabbit.util.ISO9075;
import org.apache.jackrabbit.util.Text;
import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.gcube.common.homelibary.model.items.type.NodeProperty;
import org.gcube.common.homelibary.model.items.type.PrimaryNodeType;
import org.gcube.common.homelibrary.model.exceptions.InternalErrorException;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.globus.wsrf.client.GetProperties;

import com.thoughtworks.xstream.XStream;


public class RemoveRobertosFiles {
	private static final String nameResource 				= "HomeLibraryRepository";
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

		//		ScopeProvider.instance.set("/d4science.research-infrastructures.eu");
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

					String url = ap.address();
					//							url = "http://node11.d.d4science.research-infrastructures.eu:8080/jackrabbit-webapp-patched-2.4.3";

					String user = ap.username();						
					String pass = StringEncrypter.getEncrypter().decrypt(ap.password());


					//		String url = "http://node11.d.d4science.research-infrastructures.eu:8080/jackrabbit-webapp-2.8.0/";
					URLRemoteRepository repository = new URLRemoteRepository(url + "/rmi");
					Session session = repository.login( 
							new SimpleCredentials(user, pass.toCharArray()));
					//					Node node = session.getNode("/Home/valentina.marioli/Workspace/MySpecialFolders");
					//					System.out.println(node.getPath());
					//					node.remove();
					//					session.save();

					NodeIterator iterator = session.getNode("/Share").getNodes();
					while (iterator.hasNext()){
						Node node = iterator.nextNode();
											
						if (node.hasProperty("hl:portalLogin")){
							Property createdBy = node.getProperty("hl:portalLogin");
//							System.out.println(node.getPath() + " - " + createdBy.getString());
							if (createdBy.getString().equals("valentina.marioli")){

								System.out.println(node.getPath());
								try{
								node.remove();
								session.save();
								}catch (Exception e){
									e.printStackTrace();
								}
							}

						}
						//						PropertyIterator properties = node.getProperties();
						//						while (properties.hasNext()){
						//							Property property = properties.nextProperty();
						//							System.out.println(property.getName());
						//						}
						//						iterator.nextNode().remove();
						//						session.save();
					}
					//					session.getNode("/Home/valentina.marioli/OutBox/1b09177f-43d4-4918-bb3f-6bc22a7d928d").remove();
					//					session.save();
					//					NodeIterator children = session.getNode("/Home/valentina.marioli/Workspace/MySpecialFolders").getNodes();
					//
					//					while (children.hasNext()){
					//						Node node = children.nextNode();
					////						System.out.println(node.getPrimaryNodeType().getName());
					//						if (node.getPrimaryNodeType().getName().equals("nthl:workspaceSharedItem")){
					////							if (node.getProperty("jcr:title").getString().startsWith("gcube-devsec-Tes")){
					////								System.out.println(node.getPath());
					//								node.remove();
					//								session.save();
					////							}
					//						}
					//					}


				}
			}
		}finally{}
	}


}
