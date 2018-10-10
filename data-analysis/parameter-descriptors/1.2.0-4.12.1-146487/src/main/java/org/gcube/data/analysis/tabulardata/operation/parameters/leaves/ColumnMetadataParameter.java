package org.gcube.data.analysis.tabulardata.operation.parameters.leaves;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.model.metadata.column.ColumnMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.column.DataLocaleMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.common.DescriptionsMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.common.NamesMetadata;
import org.gcube.data.analysis.tabulardata.operation.parameters.Cardinality;
import org.gcube.data.analysis.tabulardata.operation.parameters.LeafParameter;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ColumnMetadataParameter extends LeafParameter<ColumnMetadata> {

	private static final List<Class<? extends ColumnMetadata>> DEFAULT_ALLOWED_METADATA_CLASSES=new ArrayList<>();
	
	static{
		DEFAULT_ALLOWED_METADATA_CLASSES.add(DataLocaleMetadata.class);
		DEFAULT_ALLOWED_METADATA_CLASSES.add(DescriptionsMetadata.class);
		DEFAULT_ALLOWED_METADATA_CLASSES.add(NamesMetadata.class);
	}
	
	private List<Class<? extends ColumnMetadata>> allowedClasses=null;
	
	
	@Override
	public Class<ColumnMetadata> getParameterType() {
		return ColumnMetadata.class;
	}

	@SuppressWarnings("unused")
	private ColumnMetadataParameter() {	
	}

	public ColumnMetadataParameter(String identifier, String name,
			String description, Cardinality cardinality,
			List<Class<? extends ColumnMetadata>> allowedClasses) {
		super(identifier, name, description, cardinality);
		this.allowedClasses = allowedClasses;
	}

	public ColumnMetadataParameter(String identifier, String name,
			String description, Cardinality cardinality) {
		super(identifier, name, description, cardinality);
		this.allowedClasses = DEFAULT_ALLOWED_METADATA_CLASSES;
	}

	
	public List<Class<? extends ColumnMetadata>> getAllowedClasses() {
		return allowedClasses;
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ColumnMetadataParameter [allowedClasses=");
		builder.append(allowedClasses);
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
	public void validateValue(Object value) throws Exception {
		super.validateValue(value);
		if(!allowedClasses.contains(value.getClass())) throw new Exception(String.format("Passed argument %s is not among valid ones %s ",value,this.getAllowedClasses()));
	}
}
