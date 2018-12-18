package org.apache.jackrabbit.j2ee;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.ValueFormatException;

import org.apache.jackrabbit.rmi.repository.URLRemoteRepository;
import org.gcube.common.homelibary.model.items.type.NodeProperty;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongItemTypeException;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.scope.api.ScopeProvider;

import com.thoughtworks.xstream.XStream;

public class STEP1 {
	static JCRWorkspace ws = null;
	public static final String HL_NAMESPACE					= "hl:";
	public static final String JCR_NAMESPACE				= "jcr:";
	public static final String REP_NAMESPACE				= "rep:";
	private static final String NT_NAMESPACE 				= "nt:";

	public static void main(String[] args) throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException, WrongItemTypeException {

		try {
			createLibrary();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	private static void createLibrary() throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException, InterruptedException, WrongItemTypeException {
		//		ScopeProvider.instance.set("/gcube");
		//		ScopeProvider.instance.set("/d4science.research-infrastructures.eu");
		ScopeProvider.instance.set("/gcube/preprod/preVRE");

		String url = "http://ws-repo-test.d4science.org/home-library-webapp";
//		String url = "http://pc-costantino.isti.cnr.it:8080/jackrabbit-webapp-2.14.0";

		Writer writer = null;

		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream("check00.txt"), "utf-8"));

			URLRemoteRepository repository = new URLRemoteRepository(url + "/rmi");
			String user = "workspacerep.imarine";
			String pass = "gcube2010*onan";

			Session session = repository.login( 
					new SimpleCredentials(user, pass.toCharArray()));
			
//			System.out.println(session.getNodeByIdentifier("ae727b1c-9908-4033-a385-0326655deede").getPath());

			System.out.println("Root: " + session.getRootNode().getPath());
			Node folder = session.getNode("/Share/");
			getItem(folder, writer);

		} catch (IOException ex) {
			// report
		} finally {
			try {
				writer.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}


	private static void getItem(Node item, Writer writer) throws InternalErrorException, IOException, RepositoryException {
		NodeIterator nodes = item.getNodes();
		XStream stream = new XStream();
		while(nodes.hasNext()){
			Node node = nodes.nextNode();
			try{
//				writer.write(node.getPath() +"\t" + getOwner(node)+ "\t" + stream.toXML(getUsers(node)));
				writer.write(node.getIdentifier() +"\t" + node.getPath() +"\t" + getOwner(node));
				writer.write("\n");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	private static String getOwner(Node node) throws ValueFormatException, PathNotFoundException, RepositoryException {
		String owner;
		try{
			owner = node.getProperty(NodeProperty.PORTAL_LOGIN.toString()).getString();
		}catch (Exception e) {
			try {
				Node ownerNode = node.getNode(NodeProperty.OWNER.toString());
				owner = ownerNode.getProperty(NodeProperty.PORTAL_LOGIN.toString()).getString();
			} catch (PathNotFoundException e1) {
				throw new RepositoryException(e.getMessage());
			}
		}
		return owner;	

	}

	
	private static Map<String, String> getUsers(Node node) throws RepositoryException {
		System.out.println(node.getPath());
		Map<String, String> list = new HashMap<String, String>();
		try {
			Node usersNode = node.getNode(NodeProperty.USERS.toString());
			for (PropertyIterator iterator = usersNode.getProperties(); iterator.hasNext();) {
				Property property  = iterator.nextProperty();
				String key = property.getName();
				String value = property.getString();
				if (!(key.startsWith(JCR_NAMESPACE)) &&
						!(key.startsWith(HL_NAMESPACE)) &&
						!(key.startsWith(NT_NAMESPACE)))
					list.put(key, value);
			} 
		} catch (RepositoryException e) {
			throw new RepositoryException(e.getMessage());
		} 
		return list;
	}


}
