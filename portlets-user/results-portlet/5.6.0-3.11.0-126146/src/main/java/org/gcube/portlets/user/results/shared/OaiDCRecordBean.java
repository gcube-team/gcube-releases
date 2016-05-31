package org.gcube.portlets.user.results.shared;

import java.util.HashMap;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class OaiDCRecordBean extends GenericTreeRecordBean implements IsSerializable {
	
	private HashMap<String, List<String>> contentURLs = new HashMap<String, List<String>>(); 

	public OaiDCRecordBean(){super();};
	
	public OaiDCRecordBean(ObjectType type, String payload) {
		super(type, payload);
	}

	public HashMap<String, List<String>> getContent() {
		return contentURLs;
	}

	public void setContent(HashMap<String, List<String>> content) {
		this.contentURLs = content;
	}

	
}
