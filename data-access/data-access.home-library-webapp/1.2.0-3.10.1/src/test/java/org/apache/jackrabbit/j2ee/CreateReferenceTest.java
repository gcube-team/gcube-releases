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


public class CreateReferenceTest {
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

//					String srcId = "433ae7a9-e25d-4239-b7dd-5e767009a744";
//					String destId = "c1321c58-a86a-4584-8b11-066ee395b54d";
		
					
					String login = "valentina.marioli";
					String srcPath = "/Home/valentina.marioli/Workspace/00000/aaa/";
					String destPath = "/Home/valentina.marioli/Workspace/00000/bbb/";
					ItemDelegate item = createReference(session, srcPath, destPath, login);
					System.out.println("********* " + item.getPath());

				}
			}
		}finally{}
	}
	
	
	
	private static ItemDelegate createReference(Session session, String srcPath, String destPath, String login) {
		
		
		Node reference;
		try {
			reference = session.getNode("/Home/valentina.marioli/Workspace/00000/bbb/aaa");

			System.out.println("refence id: " + reference.getIdentifier());
			
			if (reference.isNodeType(JcrConstants.MIX_REFERENCEABLE)) {
				
				Node original = reference.getProperty(NodeProperty.REFERENCE.toString()).getNode();
				System.out.println("Reference to: " + original.getPath());
				
				} else {
					System.out.println("no referenceable");
				// there is a node with that uuid but the node does not expose it

				}
			

//			NodeIterator children = reference.getNodes();
//			
//			while(children.hasNext()){
//				Node child = children.nextNode();
//				System.out.println(child.getName());
//			}
		} catch (PathNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (RepositoryException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		

		ItemDelegate item = null;
		Node srcNode = null;
		Node destNode = null;
		try{
			srcNode = session.getNode(srcPath);
			System.out.println("src node " + srcNode.getPath());
		destNode = session.getNode(destPath);
			System.out.println("destNode node " + destNode.getPath());
			srcNode.addMixin(JcrConstants.MIX_REFERENCEABLE);
//			
//			session.getWorkspace().clone(session.getWorkspace().getName(), srcNode.getPath(), destNode.getPath() + "/clone", false);
//			session.save();
//			Node link = srcNode.getNode("clone");

//			destNode = session.getNodeByIdentifier(destID);
//
			Node link = destNode.addNode(srcNode.getName(), srcNode.getPrimaryNodeType().getName());
			link.setProperty(NodeProperty.REFERENCE.toString(), srcNode);
			link.setProperty(NodeProperty.TITLE.toString(), srcNode.getProperty(NodeProperty.TITLE.toString()).getString());
			link.setProperty(NodeProperty.LAST_ACTION.toString(), srcNode.getProperty(NodeProperty.LAST_ACTION.toString()).getString());
//			Node content = link.addNode(NodeProperty.CONTENT.toString());
//			content.setProperty(NodeProperty.LAST_ACTION.toString(), srcNode.getProperty(NodeProperty.LAST_ACTION.toString()).getString());
			session.save();

			System.out.println("References to " + srcNode.getPath() + ":");
			for (Property reference1 : JcrUtils.getReferences(srcNode)) {
				System.out.println("- " + reference1.getPath());
			}

			NodeManager wrap = new NodeManager(link, login);
			item = wrap.getItemDelegate();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return item;
	}
}
