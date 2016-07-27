package org.gcube.data.analysis.tabulardata.service.template;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TemplateId {

	private long value;
	
	@SuppressWarnings("unused")
	private TemplateId(){}
	
	public TemplateId(long value) {
		super();
		this.value = value;
	}

	public long getValue(){
		return value;
	}

	@Override
	public String toString() {
		return "TemplateId [value=" + value + "]";
	}
	
}
