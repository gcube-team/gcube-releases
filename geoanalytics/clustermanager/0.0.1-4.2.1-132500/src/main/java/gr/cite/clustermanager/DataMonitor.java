package gr.cite.clustermanager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import gr.cite.clustermanager.model.ZNodeData;
import gr.cite.clustermanager.model.ZNodeData.ZNodeStatus;
import gr.cite.clustermanager.model.ZNodeDatum;

public class DataMonitor {
	
	final static Logger log = Logger.getLogger(DataMonitor.class);

	private static String path = "/";
	private static DataMonitor instance = null;
    private static String host;
    private static CuratorFramework client = null;
    private static PathChildrenCache cache = null;
    
    private ObjectMapper objectMapper = new ObjectMapper();
    
    private List<ZNodeData> zNodeDatum = new ArrayList<ZNodeData>();
    private Map<String, String> serverToGeoserverData = new ConcurrentHashMap<String, String>();
    private Map<String, Set<String>> serverKeyData = new ConcurrentHashMap<String, Set<String>>();
	private Map<String, Set<String>> layerKeyData = new ConcurrentHashMap<String, Set<String>>();
	
	private DataMonitor() {}

	private static void initializeClient(String host, String path) {
		try {
			
			if (!host.isEmpty() && !path.isEmpty()){
				DataMonitor.setHost(host);
				DataMonitor.setPath(path);
			}else {
				throw new Exception("Host or Path is not ser.");
			}
			
			client = CuratorFrameworkFactory.newClient(DataMonitor.host, new ExponentialBackoffRetry(1000, 3));
		    client.start();
		    
		    cache = new PathChildrenCache(client, path, true);
			cache.start();
			
		    addListener(cache);
		    log.debug("New data monitor instance has been created");
		} catch (Exception e) {
			log.error("Could not start children client.");
			e.printStackTrace();
		}
	}
	
	public static synchronized DataMonitor getInstance(String host, String path) {
		
		if (instance == null){
			instance = new DataMonitor();
            initializeClient(host, path);
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
                    	instance.addData(event);
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
	
	protected void removeData(PathChildrenCacheEvent event) {
		
		this.serverKeyData.remove(event.getData().getPath());
		this.removeFromLayerKeyData(event.getData().getPath());
	}

	private void removeFromLayerKeyData(String path) {
		for (Map.Entry<String,Set<String>> entry : this.layerKeyData.entrySet()) {
		    entry.getValue().removeIf(l -> l.equals(path));
		}
	}

	private void addData(PathChildrenCacheEvent event) throws IOException {
		
		addLayerKeyData(event);
		addServerKeyData(event);
		addServerToGeoserverData(event);
	}

	private void addServerToGeoserverData(PathChildrenCacheEvent event) throws IOException {
		
		String path = event.getData().getPath();
		byte[] rawData = event.getData().getData();
		String data = new String(rawData, StandardCharsets.UTF_8);
		
		ZNodeDatum zNodeDatum;
		
		try {
			zNodeDatum = objectMapper.readValue(data, ZNodeDatum.class);
		} catch (IOException e) {
			e.printStackTrace();
			log.error("An error occured during deserialization", e);
			throw e;
		}
		
		String geoserverIp = zNodeDatum.getGeoserverName();
		
		getServerToGeoserverData().put(path, geoserverIp);
	}

	private void addServerKeyData(PathChildrenCacheEvent event) throws IOException {
		
		String path = event.getData().getPath();
		byte[] rawData = event.getData().getData();
		String data = new String(rawData, StandardCharsets.UTF_8);
		
		ZNodeDatum zNodeDatum;
		
		try {
			zNodeDatum = objectMapper.readValue(data, ZNodeDatum.class);
		} catch (IOException e) {
			e.printStackTrace();
			log.error("An error occured during deserialization", e);
			throw e;
		}
		
		List<ZNodeData> zNodeData = zNodeDatum.getZNodeDatas();
		
		Set<String> formedData = zNodeData.stream().filter(z -> z.getzNodeStatus() == ZNodeStatus.ACTIVE.statusCode()).map(z -> z.getLayerName().trim()).collect(Collectors.toSet());
		getServerKeyData().put(path, formedData);
	}

	private void addLayerKeyData(PathChildrenCacheEvent event) throws IOException {
		
		String path = event.getData().getPath();
		byte[] rawData = event.getData().getData();
		String data = new String(rawData, StandardCharsets.UTF_8);
		
		ZNodeDatum zNodeDatum;
		
		try {
			zNodeDatum = objectMapper.readValue(data, ZNodeDatum.class);
		} catch (IOException e) {
			e.printStackTrace();
			log.error("An error occured during deserialization", e);
			throw e;
		}
		
		List<ZNodeData> zNodeData = zNodeDatum.getZNodeDatas();
		
		Set<String> formedData = zNodeData.stream().filter(z -> z.getzNodeStatus() == ZNodeStatus.ACTIVE.statusCode()).map(z -> z.getLayerName().trim()).collect(Collectors.toSet());
		
		for (String layer : formedData){
			if (getLayerKeyData().containsKey(layer)){
				Set<String> geoservers = this.layerKeyData.get(layer);
				geoservers.add(path);
				getLayerKeyData().put(layer, geoservers);
			}else{
				Set<String> geoservers = new HashSet<String>();
				geoservers.add(path);
				getLayerKeyData().put(layer , geoservers);
			}
		}
		
	}

	public String getHost() {
		return host;
	}
	
	public static String getPath() {
		return path;
	}
	
	private static void setPath(String path) {
		DataMonitor.path = path;
	}

	private static void setHost(String host) {
		DataMonitor.host = host;
	}

	public Map<String, Set<String>> getServerKeyData() {
		return serverKeyData;
	}

	public void setServerKeyData(Map<String, Set<String>> serverKeyData) {
		this.serverKeyData = serverKeyData;
	}

	public Map<String, Set<String>> getLayerKeyData() {
		return layerKeyData;
	}

	public void setLayerKeyData(Map<String, Set<String>> layerKeyData) {
		this.layerKeyData = layerKeyData;
	}

	public List<ZNodeData> getzNodeDatum() {
		return zNodeDatum;
	}

	public void setzNodeDatum(List<ZNodeData> zNodeDatum) {
		this.zNodeDatum = zNodeDatum;
	}

	public Map<String, String> getServerToGeoserverData() {
		return serverToGeoserverData;
	}

	public void setServerToGeoserverData(Map<String, String> serverToGeoserverData) {
		this.serverToGeoserverData = serverToGeoserverData;
	}
}
