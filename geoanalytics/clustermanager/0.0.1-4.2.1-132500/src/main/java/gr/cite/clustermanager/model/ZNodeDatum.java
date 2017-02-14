package gr.cite.clustermanager.model;

import java.util.List;

public class ZNodeDatum {
	
	private List<ZNodeData> ZNodeDatas;
	private String geoserverName;
	
	public ZNodeDatum() {
	}
	
	public ZNodeDatum(List<ZNodeData> ZNodeDatas, String geoserverName){
		this.ZNodeDatas = ZNodeDatas;
		this.geoserverName = geoserverName;
	}
	
	public List<ZNodeData> getZNodeDatas() {
		return ZNodeDatas;
	}
	public void setZNodeDatas(List<ZNodeData> zNodeDatas) {
		ZNodeDatas = zNodeDatas;
	}
	public String getGeoserverName() {
		return geoserverName;
	}
	public void setGeoserverName(String geoserverName) {
		this.geoserverName = geoserverName;
	}
}
