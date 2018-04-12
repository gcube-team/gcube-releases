package gr.cite.clustermanager.model.layers;

import java.io.Serializable;
import java.util.List;

public class ZNodeDatum implements Serializable{

	private static final long serialVersionUID = -8218098176365082388L;
	
	private List<ZNodeData> ZNodeDatas;
	private String gosHost;
	private String gosPort;
	private String geoserverEndpoint;
	private String geoserverWorkspace;
	private String datastoreName;
	
	
	public ZNodeDatum() {
	}
	
	public ZNodeDatum(List<ZNodeData> ZNodeDatas, String geoserverEndpoint, String gosHost, String gosPort, String geoserverWorkspace, String datastoreName){
		this.ZNodeDatas = ZNodeDatas;
		this.geoserverEndpoint = geoserverEndpoint;
		this.gosHost = gosHost;
		this.gosPort = gosPort;
		this.geoserverWorkspace = geoserverWorkspace;
		this.datastoreName = datastoreName;
	}
	
	public List<ZNodeData> getZNodeDatas() {
		return ZNodeDatas;
	}
	public void setZNodeDatas(List<ZNodeData> zNodeDatas) {
		ZNodeDatas = zNodeDatas;
	}

	public String getGosHost() {
		return gosHost;
	}

	public void setGosHost(String gosHost) {
		this.gosHost = gosHost;
	}

	public String getGosPort() {
		return gosPort;
	}

	public void setGosPort(String gosPort) {
		this.gosPort = gosPort;
	}

	public String getGeoserverEndpoint() {
		return geoserverEndpoint;
	}
	
	public String getGeoserverWorkspace() {
		return geoserverWorkspace;
	}
	
	public String getDatastoreName() {
		return datastoreName;
	}
	
	
	
}
