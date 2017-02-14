package gr.cite.gaap.datatransferobjects;

import java.util.HashMap;
import java.util.Map;

public class AnalyzeResponse {
	private boolean status = false;
	private Map<String, String> attrs = new HashMap<String, String>();
	private String token = null;

	public AnalyzeResponse() {
	}

	public AnalyzeResponse(boolean status, Map<String, String> attrs, String token) {
		this.status = status;
		this.attrs = attrs;
		this.token = token;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public Map<String, String> getAttrs() {
		return attrs;
	}

	public void setAttrs(Map<String, String> attrs) {
		this.attrs = attrs;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
