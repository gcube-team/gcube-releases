package org.gcube.portlets.user.speciesdiscovery.server.job;

import java.util.ArrayList;
import java.util.List;

public class KeyStringList {
	
	private List<String> listKeys = new ArrayList<String>();
	
	public List<String> getListKeys() {
		return listKeys;
	}

	public KeyStringList(){
	}
	
	public void addKey(String key){
		listKeys.add(key);
	}

}
