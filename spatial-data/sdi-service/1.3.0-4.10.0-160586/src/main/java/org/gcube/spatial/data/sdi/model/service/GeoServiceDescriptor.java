package org.gcube.spatial.data.sdi.model.service;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.spatial.data.sdi.model.credentials.Credentials;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class GeoServiceDescriptor {

	public GeoServiceDescriptor(Version version, String baseEndpoint, List<Credentials> accessibleCredentials) {
		super();
		this.version = version;
		this.baseEndpoint = baseEndpoint;
		this.accessibleCredentials = accessibleCredentials;
	}
	@NonNull
	private Version version;
	@NonNull
	private String baseEndpoint;
	@NonNull
	private List<Credentials> accessibleCredentials=new ArrayList<Credentials>();
	
}
