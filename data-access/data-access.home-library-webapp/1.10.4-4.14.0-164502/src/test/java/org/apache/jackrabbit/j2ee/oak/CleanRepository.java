package org.apache.jackrabbit.j2ee.oak;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.commons.httpclient.HttpException;
import org.apache.jackrabbit.rmi.repository.URLRemoteRepository;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.model.exceptions.RepositoryException;

public class CleanRepository {
//	private static final String URL = "http://localhost:8080/jackrabbit-webapp-2.14.0";
	private static final String URL = "http://workspace-repository-prod1.d4science.org:8080/home-library-webapp";
//	private static final String URL = "http://workspace-repository-prod.d4science.org:8080/home-library-webapp";
	private static final String USER ="workspacerep.imarine";
	private static final String PASS ="workspacerep.imarine";
//	private static final String PASS = "gcube2010*onan";

	static Session session =  null;

	public static void main(String[] args) throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, InterruptedException {

		try {

			try{
				URLRemoteRepository repository = new URLRemoteRepository(URL + "/rmi");
				System.out.println(repository);
				session = repository.login( 
						new SimpleCredentials(USER, PASS.toCharArray()));

				
				Node node = session.getNode("/922d3c17");
				
				PropertyIterator iterator = node.getProperties();
				while(iterator.hasNext()){
					Property prop = iterator.nextProperty();
					System.out.println(prop.getName() + " " + prop.getString());
				}
				
				NodeIterator nodes = node.getNodes();
				while(nodes.hasNext()){
					Node subNode = nodes.nextNode();
					System.out.println(subNode.getPath());
				}
//				System.out.println(node.getProperty("hl:portalLogin").getString());
//				NodeIterator iterator = session.getRootNode().getNodes();
//				while(iterator.hasNext()){
//					Node node = iterator.nextNode();
//					System.out.println(node.getPath());
//					if (node.getPath().endsWith("Groups")){
//						
//						node.remove();
//						session.save();
//					}
//				}
				
			} finally {
				if (session!=null)
				session.logout();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
