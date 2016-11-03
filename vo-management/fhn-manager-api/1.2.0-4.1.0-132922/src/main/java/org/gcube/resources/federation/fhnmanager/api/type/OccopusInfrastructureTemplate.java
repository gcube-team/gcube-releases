package org.gcube.resources.federation.fhnmanager.api.type;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by ggiammat on 9/6/16.
 */
@XmlRootElement
public class OccopusInfrastructureTemplate extends FHNResource {

	private String occopusDescription;

	public String getOccopusDescription() {
		return occopusDescription;
	}

	public void setOccopusDescription(String occopusDescription) {
		this.occopusDescription = occopusDescription;
	}

}
