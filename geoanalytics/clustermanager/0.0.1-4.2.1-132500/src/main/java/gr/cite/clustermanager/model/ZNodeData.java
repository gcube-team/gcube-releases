package gr.cite.clustermanager.model;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import gr.cite.geoanalytics.dataaccess.entities.workflow.Workflow.WorkflowStatus;

public class ZNodeData {

	public enum ZNodeStatus {
		ACTIVE((short)0), PENDING((short)1);
		
		private final short statusCode;
		
		private static final Map<Short,WorkflowStatus> lookup  = new HashMap<Short,WorkflowStatus>();
		 
		static {
		      for(WorkflowStatus s : EnumSet.allOf(WorkflowStatus.class))
		           lookup.put(s.statusCode(), s);
		 }
		
		ZNodeStatus(short statusCode) {
			this.statusCode = statusCode;
		}
		
		public short statusCode() { return statusCode; }
	
		public static WorkflowStatus fromStatusCode(short statusCode) {
			return lookup.get(statusCode);
		}
	};
	
	private String layerName;
	private short zNodeStatus;
	
	public ZNodeData(){}
	
	public ZNodeData(String layerName, ZNodeStatus zNodeStatus) {
		this.layerName = layerName;
		this.zNodeStatus = zNodeStatus.statusCode;
	}
	
	public String getLayerName() {
		return layerName;
	}
	public void setLayerName(String layerName) {
		this.layerName = layerName;
	}
	public short getzNodeStatus() {
		return zNodeStatus;
	}
	public void setzNodeStatus(short zNodeStatus) {
		this.zNodeStatus = zNodeStatus;
	}
}
