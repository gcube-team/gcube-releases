package org.gcube.portlets.user.homelibrary.jcr.performance;

import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.jcr.repository.JCRRepository;
import org.gcube.common.homelibrary.jcr.workspace.usermanager.JCRGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

public class TestWebApp {
	public static String url;
	
	
	/**
	 * @param args
	 * @throws InternalErrorException 
	 * @throws RepositoryException 
	 */
	public static void main(String[] args) throws InternalErrorException, RepositoryException {
		
		url = "http://node11.d.d4science.research-infrastructures.eu:8080/jackrabbit-webapp-patched-2.4.3";
		
		List<Node> children = getChildren("Home/valentina.marioli/");
		for (Node child: children){
			System.out.println(child.getPath());
		}

	}

	
	@SuppressWarnings("unchecked")
	public static List<Node> getChildren(String path) throws InternalErrorException {

		GetMethod getMethod = null;
		List<Node> children = null;
		try {

			HttpClient httpClient = new HttpClient();            
			getMethod =  new GetMethod(url + "/HomeManager?path=" + path);
			httpClient.executeMethod(getMethod);

//			System.out.println("Response " + getMethod.getResponseBodyAsString());


			XStream xstream = new XStream();
			children = (List<Node>) xstream.fromXML(getMethod.getResponseBodyAsString());


		} catch (Exception e) {
			System.out.println("Error retrieving Users in UserManager");
			throw new InternalErrorException(e);
		} finally {
			if(getMethod != null)
				getMethod.releaseConnection();
		}

		return children;	

	}
}
