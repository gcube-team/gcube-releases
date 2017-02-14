package gr.cite.clustermanager;

import java.util.List;
import java.util.stream.Collectors;

import gr.cite.clustermanager.model.ZNodeData;
import gr.cite.clustermanager.model.ZNodeData.ZNodeStatus;
import gr.cite.clustermanager.model.ZNodeDatum;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.GeoServerBridge;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.ZKPaths;
import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;

public class DataCreator {

	final static Logger log = Logger.getLogger(DataCreator.class);

	private static String path;
	private static DataCreator instance = null;
    private static String host;
    private static String gosName;
    private static String geoserverName;
    private static CuratorFramework client = null;
    
    private ObjectMapper objectMapper = new ObjectMapper();
    
    @Autowired
    private GeoServerBridge geoServerBridge;

    private DataCreator() {}
    
    public void create() throws Exception {
    	List<ZNodeData> zNodeData = this.geoServerBridge.getLayers().stream().map(x -> new ZNodeData(x.getName(), ZNodeStatus.ACTIVE)).collect(Collectors.toList());
    	ZNodeDatum ZNodeDatum = new ZNodeDatum(zNodeData, DataCreator.geoserverName);
    	String zNodeDataAsString = objectMapper.writeValueAsString(ZNodeDatum);
    	byte[] layersNamesByte = zNodeDataAsString.getBytes();
    	
    	String znodePath = ZKPaths.makePath(path, gosName);
        
        try {
            client.setData().forPath(znodePath, layersNamesByte);
        }catch ( KeeperException.NoNodeException e ) {
            client.create().creatingParentContainersIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(znodePath, layersNamesByte);
        }
    }
    
	public static synchronized DataCreator getInstance(String host, String path, String gosName, String geoserverName) {
		if (instance == null){
			instance = new DataCreator();
            try {
            	
            	if (!host.isEmpty() && !path.isEmpty() && !geoserverName.isEmpty()){
            		DataCreator.setHost(host);
            		DataCreator.setPath(path);
            		DataCreator.setGosName(gosName);
            		DataCreator.setGeoserverName(geoserverName);
            	}else {
            		throw new Exception("Host or Path is not set.");
            	}
            	
            	client = CuratorFrameworkFactory.newClient(host, new ExponentialBackoffRetry(1000, 3));
                client.start();
                
                log.debug("New data monitor instance has been created");
                
			} catch (Exception e) {
				log.error("Could not start children client.");
				e.printStackTrace();
			}
		}
		
		return instance;
	}

	public static String getHost() {
		return host;
	}

	private static void setHost(String host) {
		DataCreator.host = host;
	}

	public static String getPath() {
		return path;
	}

	private static void setPath(String path) {
		DataCreator.path = path;
	}

	public void setGeoServerBridge(GeoServerBridge geoServerBridge) {
		this.geoServerBridge = geoServerBridge;
	}

	public static String getGosName() {
		return gosName;
	}

	public static void setGosName(String geoserverName) {
		DataCreator.gosName = geoserverName;
	}

	public static String getGeoserverName() {
		return geoserverName;
	}

	public static void setGeoserverName(String geoserverName) {
		DataCreator.geoserverName = geoserverName;
	}
}
