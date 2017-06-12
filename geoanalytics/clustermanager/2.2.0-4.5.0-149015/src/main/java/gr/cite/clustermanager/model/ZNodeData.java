package gr.cite.clustermanager.model;

import java.io.Serializable;
import java.util.Objects;

public class ZNodeData implements Serializable{

	private static final long serialVersionUID = -1457795393190941898L;

	public enum ZNodeStatus {
		ACTIVE(1), PENDING(2), ERRONEOUS(3);
	    private final int value;
	    private ZNodeStatus(int value) {
	        this.value = value;
	    }
	    public int getValue() {
	        return value;
	    }
	}
	
	private String layerId;
	private ZNodeStatus zNodeStatus;
	
	public ZNodeData(){}
	
	public ZNodeData(String layerId, ZNodeStatus zNodeStatus) {
		this.layerId = layerId;
		this.zNodeStatus = zNodeStatus;
	}
	
	public String getLayerId() {
		return layerId;
	}
	public void setLayerId(String layerId) {
		this.layerId = layerId;
	}
	public ZNodeStatus getzNodeStatus() {
		return zNodeStatus;
	}
	public void setzNodeStatus(ZNodeStatus zNodeStatus) {
		this.zNodeStatus = zNodeStatus;
	}
	
	@Override
	public String toString(){
		return "["+layerId+"->"+zNodeStatus.value+"]";
	}
	
	
	@Override
	public boolean equals(Object other) {
		if (other == this)
			return true;
		if (!(other instanceof ZNodeData))
			return false;
		ZNodeData otherZNodeData = (ZNodeData) other;
		return Objects.equals(layerId, otherZNodeData.layerId);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(layerId);
	}
	
	
	
	
}
