package org.apache.jackrabbit.j2ee;
import java.net.MalformedURLException;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import org.apache.jackrabbit.rmi.repository.URLRemoteRepository;


public class LocalRepository {

	/**
	 * @param args
	 * @throws MalformedURLException 
	 */
	public static void main(String[] args) throws MalformedURLException {
		String url = "http://localhost:8080/jackrabbit-webapp";
		String user= "admin";

		try{


			URLRemoteRepository repository = new URLRemoteRepository(url + "/rmi");
			Session session = repository.login( 
					new SimpleCredentials(user, user.toCharArray()));

			System.out.println(session.getRootNode().getPath());
			
			NodeIterator children = session.getRootNode().getNodes();
			while(children.hasNext()){
				Node child = children.nextNode();
				System.out.println(child.getPath());
				try{
				child.remove();
				session.save();
				}catch (Exception e){
					e.printStackTrace();
				}
	
			}
			
			
	
//			System.out.println(session.getNodeByIdentifier("b2ad0a71-a2c3-4fa9-9b88-b6d3a436cf0e").getPath());


		}catch (Exception e) {
			e.printStackTrace();
		}


	}







}
