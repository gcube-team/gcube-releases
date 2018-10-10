package gr.cite.clustermanager.actuators.functions;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.log4j.Logger;

import gr.cite.clustermanager.actuators.ClientBase;
import gr.cite.clustermanager.constants.Paths;
import gr.cite.clustermanager.exceptions.NoExecutionDetailsFound;
import gr.cite.clustermanager.model.functions.ExecutionDetails;

public class ExecutionMonitor extends ClientBase implements Serializable {

	private static final long serialVersionUID = -1352711364473503067L;
	final static Logger log = Logger.getLogger(ExecutionMonitor.class);
	
	private Map<String, ExecutionDetails> allExecutions;
	
	private PathChildrenCache cache = null;
	
	public ExecutionMonitor(String zkConnStr) throws Exception {
		super(zkConnStr);
		allExecutions = new HashMap<String, ExecutionDetails>();
		initializeClient();
	}
	
	private void initializeClient() throws Exception {
		
		try{
		    cache = new PathChildrenCache(getClient(), Paths.FUNCTION_EXECUTION_STATUS, true);
			cache.start();
			log.debug("StartedMonitor");
		} catch (Exception e) {
			log.error("Could not start children client.");
			e.printStackTrace();
		}
		
	    addListener(cache);
	    log.debug("New Execution Monitor instance has been created");
		
	}
	
	
	
	private void addListener(PathChildrenCache cache) {
		
        PathChildrenCacheListener listener = new PathChildrenCacheListener() {
        	
        	@Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
            	
                switch (event.getType()){
                	
                    case CHILD_ADDED:
                    	updateData(event);
                        break;
                    case CHILD_UPDATED: 
                    	updateData(event);
                        break;
                    case CHILD_REMOVED:
                    	updateData(event);
                        break;
                    default:
                    	log.debug("Nothing happened!!!");
						break;
						
                }
            }
        };
        
        cache.getListenable().addListener(listener);
    }
	
	
	private void updateData(PathChildrenCacheEvent event) throws IOException {
		String data = new String(event.getData().getData(), StandardCharsets.UTF_8);
		ExecutionDetails execDetails;
		try {
			execDetails = getObjectMapper().readValue(data, ExecutionDetails.class);
			allExecutions.put(execDetails.getId(), execDetails);
		}
		catch (IOException e) {
			log.error("An error occured during deserialization of zookeeper information of running function executions", e);
			throw e;
		}
		
	}
	
	public Map<String, ExecutionDetails> getAllLatestExecutionDetails(){
		return allExecutions;
	}
	
	
	public ExecutionDetails getLatestExecutionDetailsOf(String executionID) throws NoExecutionDetailsFound {
		ExecutionDetails ed = this.getAllLatestExecutionDetails().get(executionID);
		if(ed==null) throw new NoExecutionDetailsFound("There are no execution details for this execution id");
		return ed;
	}
	
	public Map<String, ExecutionDetails> getLatestExecutionDetailsOf(String[] executionIDs) throws NoExecutionDetailsFound {
		Map<String, ExecutionDetails> execDetails = new HashMap<String, ExecutionDetails>();
		
		for(String executionID : executionIDs) {
			ExecutionDetails ed = allExecutions.get(executionID);
			if(ed==null) throw new NoExecutionDetailsFound("There are no execution details for this execution id");
			
			execDetails.put(executionID, ed);
		}
		
		return execDetails;
	}
}
