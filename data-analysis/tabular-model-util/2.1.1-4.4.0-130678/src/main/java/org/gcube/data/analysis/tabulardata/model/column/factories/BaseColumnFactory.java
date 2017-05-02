package org.gcube.data.analysis.tabulardata.model.column.factories;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.gcube.data.analysis.tabulardata.metadata.NoSuchMetadataException;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.ColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.AnnotationColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.AttributeColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeDescriptionColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeNameColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.DimensionColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.MeasureColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.TimeDimensionColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.ValidationColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDTypeValue;
import org.gcube.data.analysis.tabulardata.model.metadata.Locales;
import org.gcube.data.analysis.tabulardata.model.metadata.column.DataLocaleMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.common.LocalizedText;
import org.gcube.data.analysis.tabulardata.model.metadata.common.NamesMetadata;

public abstract class BaseColumnFactory<T extends ColumnType> implements ColumnFactory<T>{
	
	public abstract T getManagedColumnType();
	
	private TDTypeValue defaultValue;
	
	protected abstract Collection<LocalizedText> getDefaultLabels();
	
	protected DataLocaleMetadata DEFAULT_LOCALE=new DataLocaleMetadata(Locales.getDefaultLocale());
	
	public BaseColumnFactory<T> useDefaultValue(TDTypeValue value){
		this.defaultValue = value;
		return this;
	}
	
	@Override
	public Column createDefault() {		
		return create(getManagedColumnType(),getManagedColumnType().getDefaultDataType(),DEFAULT_LOCALE,getDefaultLabels());
	}
	
	@Override
	public Column create(DataType dataType) {
		return create(getManagedColumnType(),dataType,DEFAULT_LOCALE,getDefaultLabels());
	}
	
	@Override
	public Column create(DataType dataType, Collection<LocalizedText> labels) {
		return create(getManagedColumnType(),dataType,DEFAULT_LOCALE,labels);
	}
	
	@Override
	public Column create(DataType dataType, Collection<LocalizedText> labels,
			String dataLocale) {
		return create(getManagedColumnType(),dataType,new DataLocaleMetadata(dataLocale),labels);
	}
	
	@Override
	public Column create(DataType dataType, Collection<LocalizedText> labels,
			DataLocaleMetadata dataLocale) {
		return create(getManagedColumnType(),dataType,dataLocale,labels);
	}
	
	@Override
	public Column create(LocalizedText label) {
		return create(label,DEFAULT_LOCALE);		
	}
	
	@Override
	public Column create(LocalizedText label, DataLocaleMetadata dataLocale) {
		Collection<LocalizedText> labels=Collections.singletonList(label);
		return create(getManagedColumnType().getDefaultDataType(),labels,dataLocale);
	}
	
	@Override
	public Column create(LocalizedText label, DataType dataType) {
		Collection<LocalizedText> labels=Collections.singletonList(label);
		return create(dataType,labels,DEFAULT_LOCALE);
	}
	
//	protected static Column create(ColumnType columnType, String name, LocalizedText label, DataType dataType){
//		Column result = create(columnType, label, dataType);
//		result.setName(name);
//		return result;
//	}
//	
	protected Column create(ColumnType columnType, DataType dataType, DataLocaleMetadata dataLocale, Collection<LocalizedText> labels){	
		
		if(columnType==null) throw new IllegalArgumentException("Column type cannot be null");
		if(dataType==null) throw new IllegalArgumentException("Column dataType cannot be null");		
		if(this.defaultValue!=null && !this.defaultValue.getReturnedDataType().equals(dataType)) 
			throw new IllegalArgumentException(String.format("Invalid default value type %s for data type %s ",defaultValue.getReturnedDataType(),dataType));
		if(!columnType.isDataTypeAllowed(dataType)) throw new IllegalArgumentException(String.format("Invalid DataType %s for column type %s, Allowed Types are %s ",dataType,columnType,columnType.getAllowedDataTypes()));
		
		Column result = new Column(generateColumnId(),dataType,columnType);
		if (this.defaultValue!=null)
			result.setCreationDefaultValue(this.defaultValue);
			
		NamesMetadata labelsMetadata = null;
		try {
			labelsMetadata = result.getMetadata(NamesMetadata.class);
		} catch (NoSuchMetadataException e) {
			List<LocalizedText> texts = new ArrayList<LocalizedText>();
			for(LocalizedText label:labels)texts.add(label);
			labelsMetadata = new NamesMetadata(texts);
		}
		result.setMetadata(labelsMetadata);
		
		
		if(dataLocale!=null)result.setMetadata(dataLocale);
		
		return result;
	}
	
	private static ColumnLocalId generateColumnId() {
		return new ColumnLocalId(UUID.randomUUID().toString());
	}

	protected static Column create(ColumnType columnType, DataType dataType){
		return new Column(generateColumnId(),dataType,columnType);
	}

	
	public static final ColumnFactory<?> getFactory(ColumnType columnType){
		if(columnType instanceof AnnotationColumnType) return new AnnotationColumnFactory();
		if(columnType instanceof AttributeColumnType) return new AttributeColumnFactory();
		if(columnType instanceof CodeColumnType) return new CodeColumnFactory();
		if(columnType instanceof CodeDescriptionColumnType) return new CodeDescriptionColumnFactory();
		if(columnType instanceof CodeNameColumnType) return new CodeNameColumnFactory();
		if(columnType instanceof DimensionColumnType) return new DimensionColumnFactory();
		if(columnType instanceof IdColumnType) return new IdColumnFactory();
		if(columnType instanceof MeasureColumnType) return new MeasureColumnFactory();
		if(columnType instanceof TimeDimensionColumnType) return new TimeDimensionColumnFactory();
		if(columnType instanceof ValidationColumnType) return new ValidationColumnFactory();
		throw new IllegalArgumentException(String.format("No Factory defined for column type %s",columnType));
	}
}
