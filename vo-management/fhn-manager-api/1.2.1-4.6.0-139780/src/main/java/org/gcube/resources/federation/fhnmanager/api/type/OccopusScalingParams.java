package org.gcube.resources.federation.fhnmanager.api.type;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement

public class OccopusScalingParams {

	int actual;

	int max;

	int min;

	int target;

	public OccopusScalingParams() {

	}

	public int getActual() {
		return actual;
	}

	public void setActual(int actual) {
		this.actual = actual;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public int getTarget() {
		return target;
	}

	public void setTarget(int target) {
		this.target = target;
	}

	@Override
	public String toString() {
		return "OccopusScaling [actual=" + actual + ", max=" + max + ", min=" + min + ", target=" + target + "]";
	}

}
