package org.gcube.data.access.bean;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "RuleList")
public class RuleList {
	
	private List<Rules> rules = new ArrayList<Rules>();
	
	@XmlElement(name = "rule")
	public List<Rules> getRules() {
		return rules;
	}

	public void setRules(List<Rules> rules) {
		this.rules = rules;
	}

}
