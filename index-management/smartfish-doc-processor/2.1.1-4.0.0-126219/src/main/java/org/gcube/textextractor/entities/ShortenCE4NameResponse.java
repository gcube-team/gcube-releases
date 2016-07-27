package org.gcube.textextractor.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ShortenCE4NameResponse implements Serializable {

	private static final long serialVersionUID = 1L;
	public String lang;
	public String label;
	public String uri;

	public ShortenCE4NameResponse(Binding b) {
		this.lang = b.lang.value;
		this.label = b.label_str.value;
		this.uri = b.uri.value;
	}


	public static List<String> getURIFromJSONOld(String json) {
		List<String> uris = new ArrayList<String>();
		
		Gson gson = new Gson();
		List<ShortenCE4NameResponse> snrs = gson.fromJson(json, new TypeToken<List<ShortenCE4NameResponse>>(){}.getType());
		
		for (ShortenCE4NameResponse snr : snrs){
			uris.add(snr.uri);
		}
		
		return uris;
	}
	
	public static List<String> getURIFromJSON(String json) {
		List<String> uris = new ArrayList<String>();
		
		Gson gson = new Gson();
		List<Map<String,String>> snrs = gson.fromJson(json, new TypeToken<List<Map<String, String>>>(){}.getType());
		
		for (Map<String,String> snr : snrs){
			uris.add(snr.get("uri"));
		}
		
		return uris;
	}
}