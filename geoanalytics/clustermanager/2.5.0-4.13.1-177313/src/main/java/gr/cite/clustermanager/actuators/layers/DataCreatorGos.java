package gr.cite.clustermanager.actuators.layers;

import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

import gr.cite.clustermanager.constants.Paths;
import gr.cite.clustermanager.model.layers.ZNodeData;
import gr.cite.clustermanager.model.layers.ZNodeDatum;
import gr.cite.clustermanager.model.layers.ZNodeData.ZNodeStatus;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.ZKPaths;
import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DataCreatorGos {

	final static Logger log = Logger.getLogger(DataCreatorGos.class);

	private static DataCreatorGos instance = null;
    private static String zkConnStr;
    private static String gosIdentifier;
    private static String geoserverUrl;
	private static String gosHost;
	private static String gosPort;
	private static String geoserverWorkspace;
	private static String datastoreName;
	
    
    private static CuratorFramework curatorClient = null;
    private Client jerseyClient = ClientBuilder.newClient();
    
    private ObjectMapper objectMapper = new ObjectMapper();
    
    private static List<String> lastCreatedLayerIDs = null;  //do not change from null
    
    private DataCreatorGos() {}
    
	public void create(List<String> layerIDs) throws Exception {
    	List<ZNodeData> zNodeData = layerIDs.stream().map(layerID -> new ZNodeData(layerID, ZNodeStatus.ACTIVE)).collect(Collectors.toList());
    	ZNodeDatum ZNodeDatum = new ZNodeDatum(zNodeData, geoserverUrl, gosHost, gosPort, geoserverWorkspace, datastoreName);
    	String zNodeDataAsString = objectMapper.writeValueAsString(ZNodeDatum);
    	byte[] layersNamesByte = zNodeDataAsString.getBytes();
    	
    	String znodePath = ZKPaths.makePath(Paths.LAYERS_OF_GEOSERVERS_ZK_PATH, gosIdentifier);
        
    	//try to delete first
    	try{curatorClient.delete().forPath(znodePath);} catch(Exception ex){/*do nothing*/}
    	
        try {
        	curatorClient.create().creatingParentContainersIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(znodePath, layersNamesByte);
        	curatorClient.setData().forPath(znodePath, layersNamesByte);
        }
        catch (Exception e1) {
        	e1.printStackTrace();
        	boolean ok = false;
        	while(!ok){
        		try{
        			curatorClient.create().creatingParentContainersIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(znodePath, layersNamesByte);
                	curatorClient.setData().forPath(znodePath, layersNamesByte);
                	ok = true;
        		}
        		catch(Exception e2){
        			log.warn("Could not upload current gos information. Will try again in a while...");
        			Thread.sleep(3000);
        		}
        	}
        }
        lastCreatedLayerIDs = layerIDs; //this is to be used in case of reconnection on the zookeeper cluster (to prevent data loss, due to EPHEMERAL nature).
    }
    	

	public static synchronized DataCreatorGos getInstance(String setZkConnStr, String gosIdentifier, String geoserverUrl, String gosHost, String gosPort, String geoserverWorkspace, String datastoreName) {
		if (instance == null){
			instance = new DataCreatorGos();
			
            try {
            	
            	if (!setZkConnStr.isEmpty()){
            		DataCreatorGos.setZkConnStr(setZkConnStr);
            		DataCreatorGos.setGosHost(gosHost);
            		DataCreatorGos.setGosPort(gosPort);
            		DataCreatorGos.setGosIdentifier(gosIdentifier);
            		DataCreatorGos.setGeoserverUrl(geoserverUrl);
            		DataCreatorGos.setDatastoreName(datastoreName);
            		DataCreatorGos.setGeoserverWorkspace(geoserverWorkspace);
            	}else {
            		throw new Exception("Geoserver host parameter is not set on properties file.");
            	}
            	
            	curatorClient = CuratorFrameworkFactory.newClient(setZkConnStr, new ExponentialBackoffRetry(1000, Integer.MAX_VALUE));
            	curatorClient.start();
                
                log.debug("New data monitor instance has been created");
                
			} catch (Exception e) {
				log.error("Could not start children client.");
				e.printStackTrace();
			}
            
            //also set up a listener for republishing last state on connection loss...
            curatorClient.getConnectionStateListenable().addListener(new ConnectionStateListener() {
				@Override
				public void stateChanged(CuratorFramework client, ConnectionState newState) {
					log.debug("Connection state with zookeeper changed to: "+newState);
					if((newState == ConnectionState.CONNECTED) || (newState == ConnectionState.RECONNECTED) ){
						if(lastCreatedLayerIDs != null){
							try {
								log.info("Republishing gos layers on cluster, due to cluster connection state change: "+newState);
								log.debug("Republishing layers: "+lastCreatedLayerIDs);
								instance.create(lastCreatedLayerIDs);
							} catch (Exception e) {
								log.info("Could not republish the layers");
							}
						}
					}
				}
			});
            
            
		}
		
		return instance;
	}
	
	public static String getZkConnStr() {
		return zkConnStr;
	}

	private static void setZkConnStr(String zkConnStr) {
		DataCreatorGos.zkConnStr = zkConnStr;
	}

	public static String getGosIdentifier() {
		return gosIdentifier;
	}

	public static void setGosIdentifier(String gosIdentifier) {
		DataCreatorGos.gosIdentifier = gosIdentifier;
	}

	public static String getGeoserverUrl() {
		return geoserverUrl;
	}

	public static void setGeoserverUrl(String geoserverUrl) {
		DataCreatorGos.geoserverUrl = geoserverUrl;
	}
	
	public static String getGosHost() {
		return gosHost;
	}

	public static void setGosHost(String gosHost) {
		DataCreatorGos.gosHost = gosHost;
	}

	public static String getGosPort() {
		return gosPort;
	}

	public static void setGosPort(String gosPort) {
		DataCreatorGos.gosPort = gosPort;
	}
	
	public static void setGeoserverWorkspace(String geoserverWorkspace) {
		DataCreatorGos.geoserverWorkspace = geoserverWorkspace;
	}
	
	public static void setDatastoreName(String datastoreName) {
		DataCreatorGos.datastoreName = datastoreName;
	}
	
}
