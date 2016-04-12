package org.gcube.portlets.admin.policydefinition.services.restful;

import it.eng.rdlab.soa3.pm.connector.javaapi.beans.RuleBean;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="beanList")
public class PolicyManagementRulesBean {

	private List<RuleBean> beanList;
	
	public PolicyManagementRulesBean() {
		this.beanList = new ArrayList<RuleBean>();
	}

	public List<RuleBean> getBeanList() {
		return beanList;
	}

	public void setBeanList(List<RuleBean> beanList) {
		this.beanList = beanList;
	}

}
