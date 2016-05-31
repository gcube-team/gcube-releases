package org.gcube.textextractor.entities;

import java.io.Serializable;

public class SpeciesCE4NameResponse implements Serializable {

	private static final long serialVersionUID = 1L;
	public String lang;
	public String label;
	public String uri;

	public SpeciesCE4NameResponse(Binding b) {
		this.lang = b.lang.value;
		this.label = b.label_str.value;
		this.uri = b.uri.value;
	}
	
}