package gr.cite.gaap.datatransferobjects;

import java.util.HashMap;
import java.util.Map;

public class GeoSearchSelection {
	public enum SearchType {
		MAP, PROJECTS
	}

	private SearchType type = null;
	private Map<String, String> attributes = new HashMap<String, String>();
	private String term = null;

	public SearchType getType() {
		return type;
	}

	public void setType(SearchType type) {
		this.type = type;
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}
}
