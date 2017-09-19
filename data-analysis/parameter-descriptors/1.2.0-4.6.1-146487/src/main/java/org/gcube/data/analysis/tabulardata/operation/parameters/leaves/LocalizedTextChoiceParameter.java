package org.gcube.data.analysis.tabulardata.operation.parameters.leaves;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.model.metadata.common.ImmutableLocalizedText;
import org.gcube.data.analysis.tabulardata.model.metadata.common.LocalizedText;
import org.gcube.data.analysis.tabulardata.operation.parameters.Cardinality;
import org.gcube.data.analysis.tabulardata.operation.parameters.LeafParameter;
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class LocalizedTextChoiceParameter extends LeafParameter<LocalizedText> {

	private List<ImmutableLocalizedText> labelChoices;
	
	private boolean caseSensitive=true;
	
	@SuppressWarnings("unused")
	private LocalizedTextChoiceParameter() {}

	public LocalizedTextChoiceParameter(String identifier, String name, String description, Cardinality cardinality,
			List<ImmutableLocalizedText> labelChoices) {
		super(identifier, name, description, cardinality);
		this.labelChoices = labelChoices;
	}

	
	
	public LocalizedTextChoiceParameter(String identifier, String name,
			String description, Cardinality cardinality,
			List<ImmutableLocalizedText> labelChoices, boolean caseSensitive) {
		super(identifier, name, description, cardinality);
		this.labelChoices = labelChoices;
		this.caseSensitive = caseSensitive;
	}

	public List<ImmutableLocalizedText> getLabelChoices() {
		return labelChoices;
	}

	@Override
	public Class<LocalizedText> getParameterType() {
		return LocalizedText.class;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((labelChoices == null) ? 0 : labelChoices.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		LocalizedTextChoiceParameter other = (LocalizedTextChoiceParameter) obj;
		if (labelChoices == null) {
			if (other.labelChoices != null)
				return false;
		} else if (!labelChoices.equals(other.labelChoices))
			return false;
		return true;
	}

	

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LocalizedTextChoiceParameter [labelChoices=");
		builder.append(labelChoices);
		builder.append(", caseSensitive=");
		builder.append(caseSensitive);
		builder.append(", getIdentifier()=");
		builder.append(getIdentifier());
		builder.append(", getName()=");
		builder.append(getName());
		builder.append(", getDescription()=");
		builder.append(getDescription());
		builder.append(", getCardinality()=");
		builder.append(getCardinality());
		builder.append("]");
		return builder.toString();
	}

	@Override
	public void validateValue(Object valueObj) throws Exception {
		super.validateValue(valueObj);
		LocalizedText value=(LocalizedText) valueObj;
		if(caseSensitive){
			if(!labelChoices.contains(value))throw new Exception(String.format("Passed argument %s is not among valid ones %s ",value,getLabelChoices()));
		}else{
			boolean ok=false;
			for(LocalizedText choice:labelChoices)
				if(choice.getValue().equalsIgnoreCase(value.getValue())&&choice.getLocale().equals(value.getLocale())) ok=true;
			if(!ok)throw new Exception(String.format("Passed argument %s is not among valid ones %s ",value,getLabelChoices()));
		}
	}
}
