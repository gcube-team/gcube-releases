package org.gcube.spatial.data.geonetwork.iso.tpl;

import org.gcube.spatial.data.geonetwork.iso.Protocol;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OnlineResource {

	private Protocol protocol;
	private String uri;
	private String description;
	private String title;
	
	public OnlineResource(String uri,String title){
		protocol=Protocol.getByURI(uri);
		this.uri=uri;
		this.title=title;
		this.description=protocol.getName()+" link to resource.";
	}
	
}
