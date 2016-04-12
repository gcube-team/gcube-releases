package org.gcube.portlets.user.homelibrary.jcr.repository;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Credentials;
import javax.jcr.LoginException;
import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;

import org.apache.jackrabbit.rmi.repository.URLRemoteRepository;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import com.thoughtworks.xstream.XStream;

public class GetWsRoot {


	private static final String ID 						= "id";

	public static final String CREATED 					= "jcr:created";
	public static final String PORTAL_LOGIN  			= "hl:portalLogin";
	private static final String TITLE 					= "jcr:title";
	private static final String LAST_MODIFIED 			= "jcr:lastModified";
	public static final String MIME_TYPE 				= "jcr:mimeType";
	public static final String SIZE 	 				= "hl:size";
	private static final String TYPE  					= "jcr:primaryType";

	private static final String HOME 					= "/Home/";
	private static final String WORKSPACE 				= "/Workspace/";

	static Session session = null;
	static Repository repository = null;

	final static String repositoryUrl = "http://node11.d.d4science.research-infrastructures.eu:8080/jackrabbit-webapp-patched-2.4.3";

	/**
	 * @param args
	 * @throws InternalErrorException 
	 * @throws RepositoryException 
	 * @throws UserNotFoundException 
	 * @throws HomeNotFoundException 
	 * @throws PathNotFoundException 
	 * @throws WorkspaceFolderNotFoundException 
	 */
	public static void main(String[] args) {

		String user = "valentina.marioli";	

		try {
			httpConnectionTest(user);
		} catch (InternalErrorException e1) {
			e1.printStackTrace();
		}

		try {
			rmiConnectionTest(user);
		} catch (RepositoryException e) {
			e.printStackTrace();
		}

	}


	/**
	 * Test using HTTP Connection
	 * @param user
	 * @throws InternalErrorException
	 */
	private static void httpConnectionTest(String user) throws InternalErrorException {
		long time = System.nanoTime();


		List<String> ids = getChilds(user);
		for(String id:ids){

			try{
				System.out.println(getNodeById(id));
			}catch (Exception e) {
				// TODO: handle exception
			}
		}

		System.out.println("HTTP CONNECTION - Time to retrive "+ ids.size() +" children: "+(System.nanoTime()-time)+" ns");

	}


	/**
	 * Get children using servlet (HTTP CONNECTION)
	 * @param user
	 * @return
	 * @throws InternalErrorException
	 */
	@SuppressWarnings("unchecked")
	public static List<String> getChilds(String user) throws InternalErrorException {
		List<String> users = null;
		GetMethod getMethod = null;
		String path = HOME + user + WORKSPACE;
		try {


			HttpClient httpClient = new HttpClient();            
			//System.out.println(repositoryUrl + "/GetChildren?absPath=" + path);
			getMethod =  new GetMethod(repositoryUrl + "/GetChildren?absPath=" + path);
			httpClient.executeMethod(getMethod);

			XStream xstream = new XStream();
			users= (List<String>) xstream.fromXML(getMethod.getResponseBodyAsString());

		} catch (Exception e) {
			e.getStackTrace();
			throw new InternalErrorException(e);
		} finally {
			if(getMethod != null)
				getMethod.releaseConnection();
		}
		return users;
	}


	/**
	 * Get Node by identifier (HTTP CONNECTION)
	 * @param id
	 * @return
	 * @throws InternalErrorException
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getNodeById(String id) throws InternalErrorException {
		Map<String, Object> properties = null;
		GetMethod getMethod = null;
		try {


			HttpClient httpClient = new HttpClient();            
			//System.out.println(repositoryUrl + "/GetNodeById?id=" + id);
			getMethod =  new GetMethod(repositoryUrl + "/GetNodeById?id=" + id);
			httpClient.executeMethod(getMethod);

			XStream xstream = new XStream();
			properties= (Map<String, Object>) xstream.fromXML(getMethod.getResponseBodyAsString());

		} catch (Exception e) {
			e.getStackTrace();
			throw new InternalErrorException(e);
		} finally {
			if(getMethod != null)
				getMethod.releaseConnection();
		}
		return properties;
	}


	/**
	 * Test using RMI Connection
	 * @param user
	 * @throws InternalErrorException
	 */
	private static void rmiConnectionTest(String user) throws RepositoryException {
		long time = System.nanoTime();
		Session session = getSession();
		String path = HOME + user + WORKSPACE;
		List<String> ids = getChildren(session, path);
		for(String id:ids){

			try{
				System.out.println(getNodeByIdentifier(session, id));
			}catch (Exception e) {
				// TODO: handle exception
			}
		}

		System.out.println("RMI CONNECTION - Time to retrive "+ ids.size() +" children: "+(System.nanoTime()-time)+" ns");

	}


	/**
	 * Get JCR Session using RMI
	 * @return
	 * @throws RepositoryException
	 */
	public static Session getSession() throws RepositoryException{

		Credentials credentials = null;
		try {
			repository = new URLRemoteRepository(repositoryUrl + "/rmi");

			credentials = new SimpleCredentials("workspacerep.imarine", "gcube2010*onan".toCharArray());

			session = repository.login(credentials);

		} catch (LoginException e) {
			e.printStackTrace();
		} catch (NoSuchWorkspaceException e) {
			e.printStackTrace();
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}

		return session;
	}


	/**
	 * Get Children using RMI Connection
	 * @param session
	 * @param absPath
	 * @return
	 * @throws RepositoryException
	 */
	private static List<String> getChildren(Session session, String absPath) throws RepositoryException {

		Node node = session.getNode(absPath);
		NodeIterator childrenIter = node.getNodes();
		List<String> children = new ArrayList<String>();
		while (childrenIter.hasNext()){
			Node child = childrenIter.nextNode();
			children.add(child.getIdentifier());
		}
		return children;

	}


	/**
	 * Get Node by Identifier (RMI Connection)
	 * @param session
	 * @param id
	 * @return
	 * @throws Exception
	 */
	private static Map<String, Object> getNodeByIdentifier(Session session, String id) throws Exception {

		Node node = session.getNodeByIdentifier(id);
		Map<String, Object> map = null;

		try{

			map = new HashMap<String, Object>();

			map.put(ID, id);

			try{
				map.put(TITLE, node.getName());
			}catch (Exception e) {
				System.out.println(TITLE + " cannot be retrieved");
			}

			try{
				map.put(PORTAL_LOGIN, node.getProperty(PORTAL_LOGIN).getString());
			}catch (Exception e) {
				System.out.println(PORTAL_LOGIN + " cannot be retrieved");
			}

			try{
				map.put(LAST_MODIFIED, node.getProperty(LAST_MODIFIED).getString());
			}catch (Exception e) {
				System.out.println(LAST_MODIFIED + " cannot be retrieved");
			}

			try {
				map.put(MIME_TYPE, node.getProperty(MIME_TYPE).getString());
			} catch(Exception e) {
				System.out.println(MIME_TYPE + " cannot be retrieved");
			}

			try{
				map.put(SIZE, node.getProperty(SIZE).getLong());
			}catch (Exception e) {
				System.out.println(SIZE + " cannot be retrieved");
			}

			try{
				map.put(TYPE, node.getPrimaryNodeType().getName());
			}catch (Exception e) {
				System.out.println(TYPE + " cannot be retrieved");
			}
		}catch (Exception e) {
			e.printStackTrace();
		}

		return map;

	}
}
