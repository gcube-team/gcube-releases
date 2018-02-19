package org.gcube.data.analysis.tabulardata.commons.templates.model.actions.finals;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.TabularResource;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.TabularResourceType;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class AddToFlowAction implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5906552996457566623L;
		
	private long tabularResource;

	private DuplicateBehaviour duplicatesBehaviuor = DuplicateBehaviour.Newer;
	
	protected AddToFlowAction(){}
	
	public AddToFlowAction(TabularResource tabularResource, DuplicateBehaviour duplicatesBehaviuor) {
		if (tabularResource.getTabularResourceType()!=TabularResourceType.FLOW)
			throw new IllegalStateException("only tabular resources of type flow can be added");
		this.tabularResource = tabularResource.getId();
		this.duplicatesBehaviuor = duplicatesBehaviuor;
	}
	
	public AddToFlowAction(TabularResource tabularResource) {
		if (tabularResource.getTabularResourceType()!=TabularResourceType.FLOW)
			throw new IllegalStateException("only tabular resources of type flow can be added");
		this.tabularResource = tabularResource.getId();
	}
	
	public long getTabularResource() {
		return tabularResource;
	}

	public DuplicateBehaviour getDuplicatesBehaviuor() {
		return duplicatesBehaviuor;
	}

	@Override
	public String toString() {
		return "AddToFlowAction [tabularResource=" + tabularResource
				+ ", duplicatesBehaviuor=" + duplicatesBehaviuor + "]";
	}
		
}
