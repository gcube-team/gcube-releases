package gr.cite.clustermanager.actuators.functions;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.CreateMode;

import gr.cite.clustermanager.actuators.ClientBase;
import gr.cite.clustermanager.constants.Paths;
import gr.cite.clustermanager.model.functions.ExecutionDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecutionNotifier extends ClientBase implements Serializable {

	private static final long serialVersionUID = -3409922355958327265L;
	
	final static Logger log = LoggerFactory.getLogger(ExecutionNotifier.class);
	
	private PathChildrenCache cache = null;
	
	public ExecutionNotifier(String zkConnStr) throws Exception {
		super(zkConnStr);
	}
	
	public void notifyAbout(ExecutionDetails executionDetails) throws Exception {
    	
    	byte[] executionDetailsBytes = getObjectMapper().writeValueAsString(executionDetails).getBytes();
    	
    	String objectPathZK = ZKPaths.makePath(Paths.FUNCTION_EXECUTION_STATUS, executionDetails.getId());
        
    	
        try {
        	if(getClient().checkExists().forPath(objectPathZK)==null)
        		getClient().create().creatingParentContainersIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(objectPathZK, executionDetailsBytes);
        	getClient().setData().forPath(objectPathZK, executionDetailsBytes);
        }
        catch (Exception e1) {
        	e1.printStackTrace();
        	boolean ok = false;
        	while(!ok){
        		try{
        			if(getClient().checkExists().forPath(objectPathZK)==null)
        				getClient().create().creatingParentContainersIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(objectPathZK, executionDetailsBytes);
                	getClient().setData().forPath(objectPathZK, executionDetailsBytes);
                	ok = true;
        		}
        		catch(Exception e2){
        			log.warn("Could not upload current execution information. Will try again in a while...");
        			Thread.sleep(3000);
        		}
        	}
        	
        }
    }
	
	
}
