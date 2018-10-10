package org.gcube.data.analysis.tabulardata.model.column.factories;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.type.ValidationColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDTypeValue;
import org.gcube.data.analysis.tabulardata.model.metadata.column.DataValidationMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.common.DescriptionsMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ImmutableLocalizedText;
import org.gcube.data.analysis.tabulardata.model.metadata.common.LocalizedText;

public class ValidationColumnFactory extends BaseColumnFactory<ValidationColumnType> {

	private static final ArrayList<LocalizedText> DEFAULT_LABELS=new ArrayList<LocalizedText>(); 
	
	static{
		DEFAULT_LABELS.add(new ImmutableLocalizedText("Validation"));
	}
	
	@Override
	protected Collection<LocalizedText> getDefaultLabels() {
		return DEFAULT_LABELS; 
	}
	
	
	
	@Override
	public ValidationColumnFactory useDefaultValue(
			TDTypeValue value) {
		return (ValidationColumnFactory)super.useDefaultValue(value);
	}



	public Column create(List<LocalizedText> names, List<LocalizedText> descriptions,
			DataValidationMetadata validationMetadata) {
		if (names.size() < 1)
			throw new IllegalArgumentException("Must provide at least one label");
		Column column=create(getManagedColumnType().getDefaultDataType(),names);
		if (descriptions.size() > 0)
			column.setMetadata(new DescriptionsMetadata(names));
		column.setMetadata(validationMetadata);
		return column;
	}

	public Column create(LocalizedText name, DataValidationMetadata validationMetadata) {
		Column col=create(name);
		col.setMetadata(validationMetadata);
		return col;
	}
	
	@Override
	public ValidationColumnType getManagedColumnType() {
		return new ValidationColumnType();
	}
	
}
