package org.gcube.portlets.admin.software_upload_wizard.shared;

import java.io.Serializable;
import java.util.ArrayList;

public class Url implements Serializable{
	
	public static final ArrayList<String> DEFAULT_DESCRIPTIONS = new ArrayList<String>();
	public static final String DOCUMENTATION = "Documentation";
	public static final String WIKI = "Wiki";
	public static final String SOURCE_CODE = "Source code";
	public static final String BINARIES = "Binaries";
	public static final String TEST_CASE_DOCUMENTATION = "Test case documentation";
	
	
	static  { 
		DEFAULT_DESCRIPTIONS.add(DOCUMENTATION);
		DEFAULT_DESCRIPTIONS.add(WIKI);
		DEFAULT_DESCRIPTIONS.add(SOURCE_CODE);
		DEFAULT_DESCRIPTIONS.add(BINARIES);
		DEFAULT_DESCRIPTIONS.add(TEST_CASE_DOCUMENTATION);
	}

	private String url = "";
	private String urlDescription = "";
	
	public Url() {

	}
	
	public Url(String url, String urlDescription) {
		super();
		this.url = url;
		this.urlDescription = urlDescription;
	}

	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getUrlDescription() {
		return urlDescription;
	}
	public void setUrlDescription(String urlDescription) {
		this.urlDescription = urlDescription;
	}
	
}
