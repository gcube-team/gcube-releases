package org.gcube.portlets.user.homelibrary.jcr.repository;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

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
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.search.SearchFolderItem;
import org.gcube.common.homelibrary.jcr.repository.JCRRepository;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;

import com.thoughtworks.xstream.XStream;

public class GetChildrenServlet {

	static String relPath = "/Home/valentina.marioli/Workspace/";

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
	public static void main(String[] args) throws InternalErrorException, WorkspaceFolderNotFoundException, PathNotFoundException, HomeNotFoundException, UserNotFoundException, RepositoryException {
		long time = System.nanoTime();
		List<String> childs = null;

		try{
			childs = getChilds(relPath, true);
		}catch (Exception e) {
			// TODO: handle exception
		}

			System.out.println("Time to retrive children: "+(System.nanoTime()-time)+" ns");

		oldHL();
	}
	private static void oldHL() throws WorkspaceFolderNotFoundException, InternalErrorException, HomeNotFoundException, UserNotFoundException, PathNotFoundException, RepositoryException {

		long time = System.nanoTime();

		Session session = connectByRmi();

		NodeIterator childs = session.getNode(relPath).getNodes();

		List<String> list = new ArrayList<String>();
		while (childs.hasNext()){
			list.add(childs.nextNode().getIdentifier());

		}
		System.out.println(list);

		System.out.println("Time to retrive ids in Old HL: "+(System.nanoTime()-time)+" ns");
		System.out.println(list.size());
	}

	public static Session connectByRmi() throws RepositoryException{


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

	@SuppressWarnings("unchecked")
	public static List<String> getChilds(String parent, Boolean all) throws InternalErrorException {
		List<String> users = null;
		GetMethod getMethod = null;
		try {

			HttpClient httpClient = new HttpClient();            

			String url = "http://node11.d.d4science.research-infrastructures.eu:8080/jackrabbit-webapp-patched-2.4.3";
			getMethod =  new GetMethod(url + "/GetChildren?absPath=" + parent);
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


	void getTimes() throws InternalErrorException, WorkspaceFolderNotFoundException, PathNotFoundException, HomeNotFoundException, UserNotFoundException, RepositoryException{
		long time = System.nanoTime();

		System.out.println(getChilds(relPath, true));

		System.out.println("Time to retrive children: "+(System.nanoTime()-time)+" ns");

		oldHL();
	}
}
