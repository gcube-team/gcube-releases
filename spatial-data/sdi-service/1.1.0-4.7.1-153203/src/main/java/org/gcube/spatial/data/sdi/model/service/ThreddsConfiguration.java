package org.gcube.spatial.data.sdi.model.service;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.spatial.data.sdi.model.credentials.Credentials;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@NoArgsConstructor
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ThreddsConfiguration extends GeoService{
	
	
	public ThreddsConfiguration(Version version, String baseEndpoint, List<Credentials> accessibleCredentials) {
		super(version, baseEndpoint, accessibleCredentials);
		// TODO Auto-generated constructor stub
	}

	
	
}
