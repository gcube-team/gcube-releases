package org.gcube.index.entities;

import java.io.Serializable;

public class AutocompleteResponse implements Serializable {
	private static final long serialVersionUID = 1L;
	public String qterm = "";
	public String uri = "";
	public String label;
	public String type = "";
	public Double score;
	public String doc_uri;
	
	//public String prov = "";//"IS of provinence";
	//public Integer pub = 2;
	//public String rholder = "";//"IS of provinence";
	//public String graphid = "";//"http://localhost:8080/dataset/partners/marbound/marbound_imports.owl";

}