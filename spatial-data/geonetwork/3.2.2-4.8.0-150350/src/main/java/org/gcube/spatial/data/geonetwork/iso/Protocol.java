package org.gcube.spatial.data.geonetwork.iso;

public enum Protocol {

	HTTP("HTTP","WWW:LINK-1.0-http--link","1.0.0"),
	WFS("WFS","OGC:WFS-1.0.0-http-get-feature","1.0.0"),
	WCS("WCS","OGC:WCS-1.0.0-http-get-coverage","1.0.0"),
	WMS("WMS","OGC:WMS-1.3.0-http-get-map","1.3.0");
	
	private String name;
	private String declaration;
	private String version;
	
	private Protocol(String name,String declaration,String version){
		this.name=name;
		this.declaration=declaration;
		this.version=version;
	}
	public String getDeclaration() {
		return declaration;
	}
	public String getName() {
		return name;
	}
	public String getVersion() {
		return version;
	}
	
	
	public static final Protocol getByURI(String uri){
		uri=uri.toLowerCase();
		if(uri.contains(("service=wms"))) return Protocol.WMS;
		else if(uri.contains("service=wfs")) return Protocol.WFS;
		else if(uri.contains("service=wcs")) return Protocol.WCS;
		else return Protocol.HTTP;
	}
}
