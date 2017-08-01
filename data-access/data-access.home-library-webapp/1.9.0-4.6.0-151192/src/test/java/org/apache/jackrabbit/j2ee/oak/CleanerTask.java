package org.apache.jackrabbit.j2ee.oak;

import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;

import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import org.apache.jackrabbit.rmi.repository.URLRemoteRepository;
import org.gcube.common.homelibary.model.items.type.NodeProperty;

public class CleanerTask implements Runnable {  

	private static final String CONTENT = "jcr:content";
	String url ="https://workspace-repository-prod1.d4science.org/home-library-webapp";
	String user ="workspacerep.imarine";
	String pass ="gcube2010*onan";


	private static long free;
	private String login;
	private String id;


	public CleanerTask(String login,String id) {
		this.login = login;
		this.id = id;
	}

	public String getName() throws RepositoryException {
		return login;
	}

	@Override
	public void run() {

//		System.out.println("Fetching data of " + login + " - count:" + i + " tot " + size);

		URLRemoteRepository repository = null;
		Session session =null;
		try {
			repository = new URLRemoteRepository(url + "/rmi");
			session = repository.login( 
					new SimpleCredentials(user, pass.toCharArray()));
			removeData(session.getNodeByIdentifier(id));

//			float percentage = (float) i/size * 100;

//			System.out.println("Done " + login+ " - percentage " + percentage + "%");

		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}finally{
			if (session!=null)
				session.logout();
		}
	}



	private static void removeData(Node node) throws RepositoryException {


		Session session = node.getSession();
		if ( node.hasNode(CONTENT)){

			Node content =  node.getNode(CONTENT);

			if (content.hasProperty(NodeProperty.DATA.toString())){
				try{
//					long size = content.getProperty(NodeProperty.DATA.toString()).getBinary().getSize();
//
//					if (size > 0){
//
//						free = size + free;
//						System.out.println(node.getPath() + " -  get Free: " + free);

						byte[] source = new byte[0];
						ByteArrayInputStream bis = new ByteArrayInputStream(source);
						Binary binary = session.getValueFactory().createBinary(bis);

						content.setProperty(NodeProperty.DATA.toString(), binary);
						session.save();
//					}
				}catch (Exception e) {			
					System.out.println("error setting propery " + NodeProperty.DATA);
					e.printStackTrace();
				}
			}
		}

		NodeIterator iterator = node.getNodes();
		while(iterator.hasNext()){
			Node child = iterator.nextNode();
			if (child.getPrimaryNodeType().getName().equals("nthl:workspaceSharedItem"))
				continue;
			removeData(child);
		}

	}


}
