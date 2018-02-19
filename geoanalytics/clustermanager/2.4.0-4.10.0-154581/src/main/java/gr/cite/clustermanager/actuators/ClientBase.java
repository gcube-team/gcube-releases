package gr.cite.clustermanager.actuators;

import java.io.Serializable;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;


public class ClientBase implements Serializable {

	private static final long serialVersionUID = 8587740627633188071L;
	final static Logger logger = Logger.getLogger(ClientBase.class);

    private CuratorFramework client = null; //SHOULD REMAIN STATIC ! 
    private ObjectMapper objectMapper;
	
	public ClientBase (String zkConnStr) throws Exception{
		
		if ((client == null) && (zkConnStr!=null) && (!zkConnStr.isEmpty())){
			client = CuratorFrameworkFactory.newClient(zkConnStr, new ExponentialBackoffRetry(1000, 3));
		    client.start();
		    objectMapper = new ObjectMapper();
		    logger.debug("Initiated ClientBase");
		}
		else {
			throw new Exception("Zookeeper connection string is not set in spring loading properties (ClientBase). Cannot initiate zookeeper monitoring");
		}
		
	}
    
    public CuratorFramework getClient(){
    	return client;
    }
    
    public ObjectMapper getObjectMapper(){
    	return objectMapper;
    }
    
	
}
