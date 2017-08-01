package gr.cite.clustermanager.actuators.layers;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import gr.cite.clustermanager.configuration.Configuration;
import gr.cite.clustermanager.constants.Paths;
import gr.cite.clustermanager.model.layers.GosDefinition;
import gr.cite.clustermanager.model.layers.ZNodeData;
import gr.cite.clustermanager.model.layers.ZNodeDatum;
import gr.cite.clustermanager.model.layers.ZNodeData.ZNodeStatus;

public class DataMonitor implements Serializable{

	private static final long serialVersionUID = -810422468564392420L;

	final static Logger log = Logger.getLogger(DataMonitor.class);

	private static DataMonitor instance = null;
    private static String zkConnStr;
    private static CuratorFramework client = null;
    private static PathChildrenCache cache = null;
    
    private ObjectMapper objectMapper = new ObjectMapper();
    
//    private List<ZNodeData> zNodeDatum = new ArrayList<ZNodeData>();
    
    private Map<String, ZNodeDatum> gosIdentifierToLayerData = new ConcurrentHashMap<String, ZNodeDatum>();
	private Map<String, Set<GosDefinition>> availableLayerToGosDefinitionData = new ConcurrentHashMap<String, Set<GosDefinition>>();
	private Map<String, Set<GosDefinition>> notAvailableLayerToGosDefinitionData = new ConcurrentHashMap<String, Set<GosDefinition>>();
	
//	private DataMonitor() {}

	private static void initializeClient(String zkConnStr) {
		try {
			
			if (!zkConnStr.isEmpty()){
				DataMonitor.setZkConnStr(zkConnStr);
			}else {
				throw new Exception("Zookeeper connection string is not set in spring loading properties. Cannot initiate zookeeper monitoring");
			}
			
			client = CuratorFrameworkFactory.newClient(DataMonitor.zkConnStr, new ExponentialBackoffRetry(1000, 3));
		    client.start();
		    
		    cache = new PathChildrenCache(client, Paths.LAYERS_OF_GEOSERVERS_ZK_PATH, true);
			cache.start();
			
		    addListener(cache);
		    log.debug("New data monitor instance has been created");
		} catch (Exception e) {
			log.error("Could not start children client.");
			e.printStackTrace();
		}
	}
	
	public static synchronized DataMonitor getInstance(String host) {
		
		if (instance == null){
			instance = new DataMonitor();
            initializeClient(host);
		}
		
		return instance;
	}

	private static void addListener(PathChildrenCache cache) {
		
        PathChildrenCacheListener listener = new PathChildrenCacheListener() {
        	
        	@Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
            	
                switch (event.getType()){
                
                    case CHILD_ADDED:
                        log.debug("Znode with name " + event.getData().getPath() + " has been ADDED.");
                        instance.addData(event);
                        log.debug("With raw data -> " + new String(event.getData().getData(), StandardCharsets.UTF_8));
                        break;
                    case CHILD_UPDATED: 
                    	log.debug("Znode with name " + event.getData().getPath() + " has been UPDATED.");
                    	instance.updateData(event);
                        log.debug("With raw data -> " + new String(event.getData().getData(), StandardCharsets.UTF_8));
                        break;
                    case CHILD_REMOVED:
                    	log.debug("Znode with name " + event.getData().getPath() + " has been DELETED.");
                        instance.removeData(event);
                        log.debug("With raw data -> " + new String(event.getData().getData(), StandardCharsets.UTF_8));
                        break;
                    default:
                    	log.debug("Nothing happened!!!");
						break;
                }
            }
        };
        
        cache.getListenable().addListener(listener);
    }
	
	
	private void addData(PathChildrenCacheEvent event) throws IOException {
		final String gosIdentifier = event.getData().getPath().substring(Paths.LAYERS_OF_GEOSERVERS_ZK_PATH.length()+1);
		String data = new String(event.getData().getData(), StandardCharsets.UTF_8);
		ZNodeDatum zNodeDatum;
		try {
			zNodeDatum = objectMapper.readValue(data, ZNodeDatum.class);
			
//			log.debug("BEFORE ADDING: "+zNodeDatum); printStateToConsole();
			
			//update gosIdentifierToLayerData
			getGosIdentifierToLayerData().put(gosIdentifier, zNodeDatum);
			//update availableLayerToGosIdentifierData and notAvailableLayerToGosIdentifierData
			zNodeDatum.getZNodeDatas().forEach(zNodeData -> {
				if(zNodeData.getzNodeStatus().equals(ZNodeStatus.ACTIVE)){
					if(!availableLayerToGosDefinitionData.containsKey(zNodeData.getLayerId()))
						availableLayerToGosDefinitionData.put(zNodeData.getLayerId(), new HashSet<GosDefinition>());
					availableLayerToGosDefinitionData.get(zNodeData.getLayerId()).add(new GosDefinition(gosIdentifier, Configuration.getFullGosEndpoint(zNodeDatum.getGosHost(), zNodeDatum.getGosPort()), zNodeDatum.getGeoserverEndpoint(), zNodeDatum.getDatastoreName(), zNodeDatum.getGeoserverWorkspace()));
				}
				else{
					if(!notAvailableLayerToGosDefinitionData.containsKey(zNodeData.getLayerId()))
						notAvailableLayerToGosDefinitionData.put(zNodeData.getLayerId(), new HashSet<GosDefinition>());
					notAvailableLayerToGosDefinitionData.get(zNodeData.getLayerId()).add(new GosDefinition(gosIdentifier, Configuration.getFullGosEndpoint(zNodeDatum.getGosHost(), zNodeDatum.getGosPort()), zNodeDatum.getGeoserverEndpoint(), zNodeDatum.getDatastoreName(), zNodeDatum.getGeoserverWorkspace()));
				}
			});
			
//			log.debug("AFTER ADDING: "+zNodeDatum); printStateToConsole();
			
			
		} catch (IOException e) {
			log.error("An error occured during deserialization of zookeeper information", e);
			throw e;
		}
		
	}
	
	
	private void updateData(PathChildrenCacheEvent event) throws IOException {
		final String gosIdentifier = event.getData().getPath().substring(Paths.LAYERS_OF_GEOSERVERS_ZK_PATH.length()+1);
		String data = new String(event.getData().getData(), StandardCharsets.UTF_8);
		ZNodeDatum zNodeDatum;
		try {
			zNodeDatum = objectMapper.readValue(data, ZNodeDatum.class);
			
//			log.debug("BEFORE UPDATING: "+zNodeDatum); printStateToConsole();
			
			//update gosIdentifierToLayerData
			getGosIdentifierToLayerData().put(gosIdentifier, zNodeDatum);
			//update availableLayerToGosIdentifierData and notAvailableLayerToGosIdentifierData
			Map<String, ZNodeStatus> layerIdStatus = zNodeDatum.getZNodeDatas().parallelStream().map(zNodeData -> {
				if(zNodeData.getzNodeStatus().equals(ZNodeStatus.ACTIVE)){
					if(!availableLayerToGosDefinitionData.containsKey(zNodeData.getLayerId()))
						availableLayerToGosDefinitionData.put(zNodeData.getLayerId(), new HashSet<GosDefinition>());
					availableLayerToGosDefinitionData.get(zNodeData.getLayerId()).add(new GosDefinition(gosIdentifier, Configuration.getFullGosEndpoint(zNodeDatum.getGosHost(), zNodeDatum.getGosPort()), zNodeDatum.getGeoserverEndpoint(), zNodeDatum.getDatastoreName(), zNodeDatum.getGeoserverWorkspace()));
				}
				else{
					if(!notAvailableLayerToGosDefinitionData.containsKey(zNodeData.getLayerId()))
						notAvailableLayerToGosDefinitionData.put(zNodeData.getLayerId(), new HashSet<GosDefinition>());
					notAvailableLayerToGosDefinitionData.get(zNodeData.getLayerId()).add(new GosDefinition(gosIdentifier, Configuration.getFullGosEndpoint(zNodeDatum.getGosHost(), zNodeDatum.getGosPort()), zNodeDatum.getGeoserverEndpoint(), zNodeDatum.getDatastoreName(), zNodeDatum.getGeoserverWorkspace()));
				}
				return zNodeData;
			})
			.collect(Collectors.toMap(zNodeData -> zNodeData.getLayerId(), zNodeData -> zNodeData.getzNodeStatus()));
			
			
			availableLayerToGosDefinitionData = availableLayerToGosDefinitionData.entrySet().parallelStream().map(entry -> {
				ZNodeStatus status = layerIdStatus.get(entry.getKey());
				if(status == null)
					entry.getValue().remove(new GosDefinition(gosIdentifier, "", "", "", ""));
				else if((status != ZNodeStatus.ACTIVE))
					entry.getValue().remove(new GosDefinition(gosIdentifier, "", "", "", ""));
				return entry;
			}).collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));
			
			notAvailableLayerToGosDefinitionData = notAvailableLayerToGosDefinitionData.entrySet().parallelStream().map(entry -> {
				ZNodeStatus status = layerIdStatus.get(entry.getKey());
				if(status == null)
					entry.getValue().remove(new GosDefinition(gosIdentifier, "", "", "", ""));
				else if((status == ZNodeStatus.ACTIVE))
					entry.getValue().remove(new GosDefinition(gosIdentifier, "", "", "", ""));
				return entry;
			}).collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));
			
//			log.debug("AFTER UPDATING: "+zNodeDatum); printStateToConsole();
			
		} catch (IOException e) {
			log.error("An error occured during deserialization of zookeeper information", e);
			throw e;
		}
		
	}
	
	
	private void removeData(PathChildrenCacheEvent event) throws IOException {
		final String gosIdentifier = event.getData().getPath().substring(Paths.LAYERS_OF_GEOSERVERS_ZK_PATH.length()+1);
		String data = new String(event.getData().getData(), StandardCharsets.UTF_8);
		ZNodeDatum zNodeDatum;
		try {
			zNodeDatum = objectMapper.readValue(data, ZNodeDatum.class);

//			log.debug("BEFORE DELETING: "+zNodeDatum); printStateToConsole();
			
			//remove entry from gosIdentifierToLayerData
			getGosIdentifierToLayerData().remove(gosIdentifier);
			//update availableLayerToGosIdentifierData and notAvailableLayerToGosIdentifierData
			zNodeDatum.getZNodeDatas().forEach(zNodeData -> {
				if(zNodeData.getzNodeStatus().equals(ZNodeStatus.ACTIVE)){
					Set<GosDefinition> gosDefs = availableLayerToGosDefinitionData.get(zNodeData.getLayerId());
					if(gosDefs!=null)
						gosDefs.remove(new GosDefinition(gosIdentifier, Configuration.getFullGosEndpoint(zNodeDatum.getGosHost(), zNodeDatum.getGosPort()), zNodeDatum.getGeoserverEndpoint(), zNodeDatum.getDatastoreName(), zNodeDatum.getGeoserverWorkspace()));
				}
				else{
					Set<GosDefinition> gosDefs = notAvailableLayerToGosDefinitionData.get(zNodeData.getLayerId());
					if(gosDefs!=null)
						gosDefs.remove(new GosDefinition(gosIdentifier, Configuration.getFullGosEndpoint(zNodeDatum.getGosHost(), zNodeDatum.getGosPort()), zNodeDatum.getGeoserverEndpoint(), zNodeDatum.getDatastoreName(), zNodeDatum.getGeoserverWorkspace()));
				}
			});
			
//			log.debug("AFTER DELETING: "+zNodeDatum); printStateToConsole();
			
		} catch (IOException e) {
			log.error("An error occured during deserialization of zookeeper information", e);
			throw e;
		}
		
	}
	
	

	public String getZkConnStr() {
		return zkConnStr;
	}
	private static void setZkConnStr(String zkConnStr) {
		DataMonitor.zkConnStr = zkConnStr;
	}
	public Map<String, ZNodeDatum> getGosIdentifierToLayerData() {
		return gosIdentifierToLayerData;
	}
	public void setGosIdentifierToLayerData (Map<String, ZNodeDatum> gosIdentifierToLayerData) {
		this.gosIdentifierToLayerData = gosIdentifierToLayerData;
	}
	public Map<String, Set<GosDefinition>> getAvailableLayerToGosDefinitionData() {
		return availableLayerToGosDefinitionData;
	}
	public void setAvailableLayerToGosDefinitionData(Map<String, Set<GosDefinition>> availableLayerToGosDefinitionData) {
		this.availableLayerToGosDefinitionData = availableLayerToGosDefinitionData;
	}
	public Map<String, Set<GosDefinition>> getNotAvailableLayerToGosDefinitionData() {
		return notAvailableLayerToGosDefinitionData;
	}
	public void setNotAvailableLayerToGosDefinitionData(Map<String, Set<GosDefinition>> notAvailableLayerToGosDefinitionData) {
		this.notAvailableLayerToGosDefinitionData = notAvailableLayerToGosDefinitionData;
	}
	
	
	// Helper/aggregative functions follow below this point
	
	
	public Set<String> getAllGosIdentifiers(){
		return gosIdentifierToLayerData.keySet();
	}
	
	public Set<GosDefinition> getAllGosEndpoints(){
		return gosIdentifierToLayerData.entrySet().stream().map(entry -> {
			return new GosDefinition(entry.getKey(), Configuration.getFullGosEndpoint(entry.getValue().getGosHost(), entry.getValue().getGosPort()), entry.getValue().getGeoserverEndpoint(), entry.getValue().getDatastoreName(), entry.getValue().getGeoserverWorkspace());
		}).collect(Collectors.toSet());
	}
	
	
	public Set<String> getAvailableLayersOf(String gosIdentifier){
		return gosIdentifierToLayerData.get(gosIdentifier).getZNodeDatas().parallelStream()
				.filter(zNodeData -> { return zNodeData.getzNodeStatus()==ZNodeStatus.ACTIVE;})
				.map(zNodeData -> zNodeData.getLayerId()).collect(Collectors.toSet());
	}
	
	public Set<String> getAllLayersOf(String gosIdentifier){
		return gosIdentifierToLayerData.get(gosIdentifier).getZNodeDatas().parallelStream()
				.map(zNodeData -> zNodeData.getLayerId()).collect(Collectors.toSet());
	}
	
	public Set<GosDefinition> getAvailableGosFor(String layerID){
		Set<GosDefinition> availableGos = availableLayerToGosDefinitionData.get(layerID);
		return (availableGos != null) ? availableGos : new HashSet<GosDefinition>();
	}
	
	public Set<GosDefinition> getNotAvailableGosFor(String layerID){
		Set<GosDefinition> notAvailableGos = notAvailableLayerToGosDefinitionData.get(layerID);
		return (notAvailableGos != null) ? notAvailableGos : new HashSet<GosDefinition>();
	}
	
	public String getGeoserverUrlFor(String gosIdentifier) {
		ZNodeDatum zDatum = gosIdentifierToLayerData.get(gosIdentifier);
		return (zDatum!=null) ? zDatum.getGeoserverEndpoint() : null;
	}
	
	
//	private void printStateToConsole(){
//		System.out.println("gosIdentifierToLayerData: "+gosIdentifierToLayerData);
//		System.out.println("availableLayerToGosDefinitionData: "+availableLayerToGosDefinitionData);
//		System.out.println("notAvailableLayerToGosDefinitionData: "+notAvailableLayerToGosDefinitionData);
//	}
	
}
