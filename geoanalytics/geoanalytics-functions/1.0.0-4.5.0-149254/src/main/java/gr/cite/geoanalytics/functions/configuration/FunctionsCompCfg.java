package gr.cite.geoanalytics.functions.configuration;

import org.springframework.stereotype.Component;

@Component
public class FunctionsCompCfg {

	private String sparkEndpoint;
	private String sparkExecutorMemory;
	
	
	public String getSparkEndpoint() {
		return sparkEndpoint;
	}
	
	public void setSparkEndpoint(String sparkEndpoint) {
		this.sparkEndpoint = sparkEndpoint;
	}
	
	public String getSparkExecutorMemory() {
		return sparkExecutorMemory;
	}
	
	public void setSparkExecutorMemory(String sparkExecutorMemory) {
		this.sparkExecutorMemory = sparkExecutorMemory;
	}

	
	
}
