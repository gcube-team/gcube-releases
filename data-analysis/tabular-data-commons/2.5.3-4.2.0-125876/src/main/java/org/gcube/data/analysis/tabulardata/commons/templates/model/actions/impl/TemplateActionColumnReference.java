package org.gcube.data.analysis.tabulardata.commons.templates.model.actions.impl;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TemplateActionColumnReference implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5704982439068969636L;
	
	public String columnId;
	
	@SuppressWarnings("unused")
	private TemplateActionColumnReference(){}
	
	protected TemplateActionColumnReference(String columnId){
		this.columnId = columnId;
	}

	public String getColumnId() {
		return columnId;
	}
		
}
