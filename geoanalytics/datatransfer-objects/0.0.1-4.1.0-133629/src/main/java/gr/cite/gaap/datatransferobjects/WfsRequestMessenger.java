package gr.cite.gaap.datatransferobjects;

import java.util.ArrayList;
import java.util.List;

public class WfsRequestMessenger {

	String url = null;
	String version = null;
	List<String> featureTypes = new ArrayList<String>();
	
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
	public List<String> getFeatureTypes() {
		return featureTypes;
	}
	public void setFeatureTypes(List<String> featureTypes) {
		this.featureTypes = featureTypes;
	}
	
}