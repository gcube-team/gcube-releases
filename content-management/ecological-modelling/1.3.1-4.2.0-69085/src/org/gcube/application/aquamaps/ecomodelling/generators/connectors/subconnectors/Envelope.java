package org.gcube.application.aquamaps.ecomodelling.generators.connectors.subconnectors;

import org.gcube.application.aquamaps.ecomodelling.generators.connectors.EnvelopeName;

public class Envelope {

	private EnvelopeName name;
	private String min;
	private String prefmin;
	private String prefmax;
	private String max;
	
	public Envelope(){
		
	}
	public Envelope(String min,String prefMin,String prefMax,String max){
		this.min = min;
		this.prefmin = prefMin;
		this.prefmax = prefMax;
		this.max = max;
	}

	public void setMin(String min) {
		this.min = min;
	}
	public String getMin() {
		return min;
	}
	public void setPrefmin(String prefmin) {
		this.prefmin = prefmin;
	}
	public String getPrefmin() {
		return prefmin;
	}
	public void setPrefmax(String prefmax) {
		this.prefmax = prefmax;
	}
	public String getPrefmax() {
		return prefmax;
	}
	public void setMax(String max) {
		this.max = max;
	}
	public String getMax() {
		return max;
	}
	public void setName(EnvelopeName name) {
		this.name = name;
	}
	public EnvelopeName getName() {
		return name;
	}
	
	
	
	
}
