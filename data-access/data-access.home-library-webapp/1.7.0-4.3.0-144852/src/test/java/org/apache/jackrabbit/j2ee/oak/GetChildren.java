package org.apache.jackrabbit.j2ee.oak;
import java.net.MalformedURLException;
import java.net.UnknownHostException;

import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.jackrabbit.j2ee.workspacemanager.session.SessionManager;
import org.apache.jackrabbit.oak.jcr.Jcr;
import org.apache.jackrabbit.oak.plugins.document.DocumentMK;
import org.apache.jackrabbit.oak.plugins.document.DocumentNodeStore;

import com.mongodb.DB;
import com.mongodb.MongoClient;


public class GetChildren {
	private static final String nameResource 				= "HomeLibraryRepository";
	/**
	 * @param args
	 * @throws MalformedURLException 
	 */
	public static void main(String[] args) throws MalformedURLException {
//		System.out.println(SessionManager.getInstance().getSessionIds());
		try {
			oak();
		} catch (UnknownHostException | RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



	}





	public static void oak() throws UnknownHostException, LoginException, RepositoryException {
		DocumentNodeStore ns = null;
		Session session = null;
		try{


			DB db = new MongoClient("ws-repo-mongo-d.d4science.org", 27017).getDB("jackrabbit");
			System.out.println(db.getName());


			ns = new DocumentMK.Builder().
					setMongoDB(db).getNodeStore();
			System.out.println("here");


			Repository repo = new Jcr(new org.apache.jackrabbit.oak.Oak(ns)).createRepository();

			String user = "admin";			
			//					String user = "workspacerep.imarine";						
//								char[] pass = "gcube2010*onan".toCharArray();
			char[] pass = "admin".toCharArray();

			//			String user = "valentina.marioli";
			//			char[] pass = "39c4e6f9fcef359428e15cdbcbfc6df8".toCharArray();
			session = repo.login(
					new SimpleCredentials(user, pass));
			System.out.println(session.getUserID());

			System.out.println(session.getRootNode().getPath());
			//			session.getRootNode().getNode("Share/2a6a4276-7aa5-495f-9d82-064d8edf1111").remove();
			//			session.save();

			//			String absPath = "/Share/332a07c0-664f-4c9f-bb6a-71de64a8b81b/proposal";
			//			System.out.println(session.getNode(absPath).getPath());
			long start = System.currentTimeMillis();
			getChildren(session.getNode("/Home/valentina.marioli/Workspace"));
			long end = System.currentTimeMillis();

			System.out.println("Took : " + ((end - start) / 1000));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			if (session!=null)
				session.logout();
			if (ns!=null)
				ns.dispose();

			System.out.println("Close");
			System.exit(0);
		}
	}



	private static void getChildren(Node node) throws RepositoryException {

		if (!node.getName().contains(":")){
			System.out.println(node.getPath());
			NodeIterator children = node.getNodes();
			while(children.hasNext()){
				Node child = children.nextNode();
				getChildren(child);
			}
		}

	}


}
