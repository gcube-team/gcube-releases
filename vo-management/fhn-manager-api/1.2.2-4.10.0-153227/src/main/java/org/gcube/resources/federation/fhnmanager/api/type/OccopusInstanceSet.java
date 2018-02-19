package org.gcube.resources.federation.fhnmanager.api.type;

import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class OccopusInstanceSet {

	Map<String, OccopusNode> instances;
	OccopusScalingParams scaling;

	public OccopusInstanceSet() {

	}

	public Map<String, OccopusNode> getInstances() {
		return instances;
	}

	public void setInstances(Map<String, OccopusNode> instances) {
		this.instances = instances;
	}

	public OccopusScalingParams getScaling() {
		return scaling;
	}

	public void setScaling(OccopusScalingParams scaling) {
		this.scaling = scaling;
	}

}
