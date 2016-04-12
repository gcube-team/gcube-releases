package it.eng.rdlab.soa3.pm.connector.service.beans;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ListRules 
{
	private List<RuleJaxBean> beanList;
	
	public ListRules() 
	{
		this.beanList = new ArrayList<RuleJaxBean>();

	}

	public List<RuleJaxBean> getBeanList() {
		return beanList;
	}

	public void setBeanList(List<RuleJaxBean> beanList) {
		this.beanList = beanList;
	}
	
	

}
