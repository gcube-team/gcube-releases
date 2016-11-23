package org.gcube.application.aquamaps.ecomodelling.generators.connectors;

import java.util.List;

import org.gcube.application.aquamaps.ecomodelling.generators.connectors.livemonitor.Resources;
import org.gcube.application.aquamaps.ecomodelling.generators.connectors.livemonitor.SingleResource;

public class RemoteHspecOutputObject {
	public String id;
	public String status;
	public String completion;
	public Metric metrics;
	public String error;
	
	
	
//	"load":[{"resId":"W1","value":51.5},{"resId":"W2","value":23.4}],"throughput":[1307977348021,16490000]}

	public class Metric{
		public long timestamp;
		public double activityvalue;
		public int processedspecies;
		public Resources resources;
		public List<SingleResource> load;
		public List<Long> throughput;
		
		public Metric(){
			resources = new Resources();
		}
		public String toString(){
			return timestamp+""+activityvalue+""+resources;
		}
		
		
	}
	
	public String toString(){
		return id+";"+status+";"+completion+";"+metrics+";"+error+";";
	}
}
