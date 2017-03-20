package gr.cite.gaap.datatransferobjects;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WfsRequestMessenger {
	private static Logger logger = LoggerFactory.getLogger(WfsRequestMessenger.class);

	String url = null;
	String version = null;
	List<WfsRequestLayer> layersInfo = new ArrayList<WfsRequestLayer>();
	
	
	
	public WfsRequestMessenger() {
		super();
		logger.trace("Initialized default contructor for WfsRequestMessenger");

	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public List<WfsRequestLayer> getLayersInfo() {
		return layersInfo;
	}
	public void setLayersInfo(List<WfsRequestLayer> layersInfo) {
		this.layersInfo = layersInfo;
	}
	
	
	
	
	
}