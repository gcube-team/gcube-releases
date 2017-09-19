package org.gcube.common.calls;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public abstract class Message {

	private Map<String, String> headers = new HashMap<String, String>();
	
	public void addHeader(String key, String value){
		headers.put(key, value);
	}
	
	public Set<Entry<String, String>> getHeaders(){
		return Collections.unmodifiableSet(headers.entrySet());
	}

	
}
