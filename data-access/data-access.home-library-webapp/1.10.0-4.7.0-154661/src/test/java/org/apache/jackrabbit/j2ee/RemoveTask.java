package org.apache.jackrabbit.j2ee;

import java.net.MalformedURLException;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.jackrabbit.rmi.repository.URLRemoteRepository;

public class RemoveTask implements Runnable {

	String url = "http://node11.d.d4science.research-infrastructures.eu:8080/home-library-webapp";
	String user ="workspacerep.imarine";
	String pass ="gcube2010*onan";
	
	private String id;

	public RemoveTask(String id) {
		this.id = id;
	}
	
	public String getName() throws RepositoryException {
		return id;
	}
	
	@Override
	public void run() {
		URLRemoteRepository repository = null;
		Session session =null;
		try {
			repository = new URLRemoteRepository(url + "/rmi");
			session = repository.login( 
					new SimpleCredentials(user, pass.toCharArray()));
			
			session.getNodeByIdentifier(id).remove();
			session.save();

		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}finally{
			if (session!=null)
				session.logout();
		}
		
	}  

}
