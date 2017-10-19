package org.apache.jackrabbit.j2ee.oak;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.jackrabbit.rmi.repository.URLRemoteRepository;


public class RemoveData {
	private static final String nameResource 				= "HomeLibraryRepository";


	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

		Session session = null;

		//		String url = "http://node11.d.d4science.research-infrastructures.eu:8080/home-library-webapp/";
		String url = "http://node76.p.d4science.research-infrastructures.eu:8080/home-library-webapp";
		//		String url ="https://workspace-repository-prod1.d4science.org/home-library-webapp";
		String user ="workspacerep.imarine";
		String pass ="gcube2010*onan";

		try{
			URLRemoteRepository repository = new URLRemoteRepository(url + "/rmi");
			session = repository.login( 
					new SimpleCredentials(user, pass.toCharArray()));
			System.out.println(session.getRootNode().getPath());

			NodeIterator homes = session.getRootNode().getNode("Home").getNodes();

			ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);

			int count = 0;
			while(homes.hasNext()){
			
				Node userNode = homes.nextNode();
				
				count ++;
				System.out.println(count);
				if (count < 1034 || count >= 1407){
					System.out.println("continue");
					continue;
				}
				
				String login = userNode.getName();
				if (login.startsWith("rep:"))
					continue;

				CleanerTask task = new CleanerTask(login, userNode.getIdentifier());
				System.out.println("A new task has been added : " + task.getName());
				executor.execute(task);
				System.out.println();

			}

			System.out.println("Maximum threads inside pool " + executor.getMaximumPoolSize());
			executor.shutdown();

			int max = (int) executor.getTaskCount(); // its gonna be somewhere around 600-700 I think

			// as long as the excutor isnt dead yet.
			while (!executor.awaitTermination(2, TimeUnit.SECONDS))
			{
				int done = (int) executor.getCompletedTaskCount();
				System.out.println("Current status: "+done+"/"+max+"   "+(int) ((double) done / max * 100)+"%");
			}

			//			while (!executor.isTerminated()) {}  

			System.out.println("Finished all threads");  

		}finally{
			if (session!=null)
				session.logout();
		}
	}


}




