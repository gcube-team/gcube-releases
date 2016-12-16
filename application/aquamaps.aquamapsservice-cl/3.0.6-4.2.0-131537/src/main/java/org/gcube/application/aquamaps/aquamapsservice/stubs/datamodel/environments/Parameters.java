package org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.environments;

import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.DataModel;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("Parameters")
public class Parameters extends DataModel{
	private int max_partitions;
	private int min_partitions;
	private int default_partitions;
	
	
	public Parameters(int min, int max, int def){
		min_partitions=min;
		max_partitions=max;
		default_partitions=def;
	}
	
	public int getMax_partitions() {
		return max_partitions;
	}
	public void setMax_partitions(int max_partitions) {
		this.max_partitions = max_partitions;
	}
	public int getMin_partitions() {
		return min_partitions;
	}
	public void setMin_partitions(int min_partitions) {
		this.min_partitions = min_partitions;
	}
	public int getDefault_partitions() {
		return default_partitions;
	}
	public void setDefault_partitions(int default_partitions) {
		this.default_partitions = default_partitions;
	}
	
}
