package gr.cite.clustermanager.actuators.layers;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import gr.cite.clustermanager.constants.Paths;
import gr.cite.clustermanager.model.layers.ZNodeData;
import gr.cite.clustermanager.model.layers.ZNodeDatum;
import gr.cite.clustermanager.model.layers.ZNodeData.ZNodeStatus;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.ZKPaths;
import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;

import com.fasterxml.jackson.databind.ObjectMapper;

public class DataCreatorGeoanalytics implements Serializable{

	private static final long serialVersionUID = -8390406829555102503L;

	final static Logger log = Logger.getLogger(DataCreatorGeoanalytics.class);

	private static DataCreatorGeoanalytics instance = null;
    private static String zkConnStr;
    
    private static CuratorFramework client = null;
    
    private ObjectMapper objectMapper = new ObjectMapper();
    

    private DataCreatorGeoanalytics() {}
    
    
    public void addLayer(String layerId, ZNodeStatus status, String gosIdentifier) throws Exception{
    	log.debug("Adding layer "+layerId+" on "+gosIdentifier);
    	ZNodeDatum zNodeDatum = getZNodeDatum(gosIdentifier);
    	zNodeDatum.getZNodeDatas().add(new ZNodeData(layerId, status));
    	setZNodeDatum(zNodeDatum, gosIdentifier);
    	log.debug("Added layer "+layerId+" on "+gosIdentifier);
    }
    
    
    public void updateLayerState(String layerId, ZNodeStatus status, String gosIdentifier) throws Exception {
		ZNodeDatum zNodeDatum = getZNodeDatum(gosIdentifier);
		List<Boolean> foundInAny = zNodeDatum.getZNodeDatas().parallelStream().map(znode -> { 
			if(znode.getLayerId().equals(layerId)){
				znode.setzNodeStatus(status);
				return true;
			}
			return false;
		})
		.filter(res -> res==true)
		.collect(Collectors.toList());
		
		if(foundInAny.isEmpty()) //means that it did not exist (this should happen on extreme cases, i.e. power failure on a node), so fix it
			zNodeDatum.getZNodeDatas().add(new ZNodeData(layerId, status));
			
		setZNodeDatum(zNodeDatum, gosIdentifier);
    }
    
    public void deleteLayer(String layerId, String gosIdentifier) throws Exception {
    	ZNodeDatum zNodeDatum = getZNodeDatum(gosIdentifier);
    	zNodeDatum.getZNodeDatas().remove(new ZNodeData(layerId, ZNodeStatus.ACTIVE)); //znodestatus can by anything, since only layerId matters in underlying comparisons
    	setZNodeDatum(zNodeDatum, gosIdentifier);
    }
    
    
    
    private ZNodeDatum getZNodeDatum(String gosIdentifier) throws Exception{
    	String znodePath = ZKPaths.makePath(Paths.LAYERS_OF_GEOSERVERS_ZK_PATH, gosIdentifier);
    	byte[] rawData = client.getData().forPath(znodePath);
    	String dataStr = new String(rawData, StandardCharsets.UTF_8);
		ZNodeDatum zNodeDatum;
		try {
			zNodeDatum = objectMapper.readValue(dataStr, ZNodeDatum.class);
		} catch (IOException e) {
			e.printStackTrace();
			log.error("An error occured during deserialization", e);
			throw e;
		}
		return zNodeDatum;
    }
    
    private void setZNodeDatum(ZNodeDatum zNodeDatum, String gosIdentifier) throws Exception{
    	String znodePath = ZKPaths.makePath(Paths.LAYERS_OF_GEOSERVERS_ZK_PATH, gosIdentifier);
    	byte[] layersNamesByte = objectMapper.writeValueAsString(zNodeDatum).getBytes();
    	try {
            client.setData().forPath(znodePath, layersNamesByte);
        }catch ( KeeperException.NoNodeException e ) {
        	e.printStackTrace();
            client.create().creatingParentContainersIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(znodePath, layersNamesByte);
        }
    }
    
    /**
     * this initializer should be used by the geoanalytics
     */
    public static synchronized DataCreatorGeoanalytics getInstance(String zkConnStr){
    	if (instance == null){
			instance = new DataCreatorGeoanalytics();
			
            try {
            	
            	if (!zkConnStr.isEmpty()){
            		DataCreatorGeoanalytics.setZkConnStr(zkConnStr);
            	}else {
            		throw new Exception("Geoserver host parameter is not set on properties file.");
            	}
            	
            	client = CuratorFrameworkFactory.newClient(zkConnStr, new ExponentialBackoffRetry(1000, Integer.MAX_VALUE));
                client.start();
                
                log.debug("New data monitor instance has been created");
                
			} catch (Exception e) {
				log.error("Could not start children client.");
				e.printStackTrace();
			}
		}
		
		return instance;
    }
    

	public static String getZkConnStr() {
		return zkConnStr;
	}

	private static void setZkConnStr(String zkConnStr) {
		DataCreatorGeoanalytics.zkConnStr = zkConnStr;
	}

	
}
