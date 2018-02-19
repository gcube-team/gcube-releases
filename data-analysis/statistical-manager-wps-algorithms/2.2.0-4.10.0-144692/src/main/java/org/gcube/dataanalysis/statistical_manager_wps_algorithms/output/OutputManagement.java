package org.gcube.dataanalysis.statistical_manager_wps_algorithms.output;


public abstract  class OutputManagement<T> {

	protected String key;
	protected T value;
	public OutputManagement()
	{
		
	}
	public void addInput(String key,T value)
	{
		this.key= key;
		this.value=value;
	}
	
	
	public abstract String getFormattedOutput();
		
}
