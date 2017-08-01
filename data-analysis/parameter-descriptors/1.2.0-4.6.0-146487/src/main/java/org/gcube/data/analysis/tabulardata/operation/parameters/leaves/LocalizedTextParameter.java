package org.gcube.data.analysis.tabulardata.operation.parameters.leaves;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.model.metadata.common.LocalizedText;
import org.gcube.data.analysis.tabulardata.operation.parameters.Cardinality;
import org.gcube.data.analysis.tabulardata.operation.parameters.LeafParameter;
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class LocalizedTextParameter extends LeafParameter<LocalizedText> {
	
	@SuppressWarnings("unused")
	private LocalizedTextParameter() {}

	public LocalizedTextParameter(String identifier, String name, String description, Cardinality cardinality) {
		super(identifier, name, description, cardinality);
	}

	@Override
	public Class<LocalizedText> getParameterType() {
		return LocalizedText.class;
	}

	@Override
	public String toString() {
		return "LocalizedTextParameter [getIdentifier()=" + getIdentifier()
				+ ", getName()=" + getName() + ", getDescription()="
				+ getDescription() + ", getCardinality()=" + getCardinality()
				+ "]";
	}
	
}
