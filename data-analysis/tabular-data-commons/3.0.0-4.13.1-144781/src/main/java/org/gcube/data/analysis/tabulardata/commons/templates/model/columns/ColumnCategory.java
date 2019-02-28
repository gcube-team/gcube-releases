package org.gcube.data.analysis.tabulardata.commons.templates.model.columns;

import java.util.Arrays;
import java.util.List;

import org.gcube.data.analysis.tabulardata.commons.utils.DimensionReference;
import org.gcube.data.analysis.tabulardata.commons.utils.FormatReference;
import org.gcube.data.analysis.tabulardata.commons.utils.LocaleReference;
import org.gcube.data.analysis.tabulardata.commons.utils.TimeDimensionReference;
import org.gcube.data.analysis.tabulardata.model.column.ColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.AnnotationColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.AttributeColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeDescriptionColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeNameColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.DimensionColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.MeasureColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.TimeDimensionColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.datatype.DateType;
import org.gcube.data.analysis.tabulardata.model.datatype.GeometryType;
import org.gcube.data.analysis.tabulardata.model.datatype.IntegerType;
import org.gcube.data.analysis.tabulardata.model.datatype.NumericType;
import org.gcube.data.analysis.tabulardata.model.datatype.TextType;

@SuppressWarnings("unchecked")
public enum ColumnCategory {
		
	ATTRIBUTE(new AttributeColumnType(), FormatReference.class, IntegerType.class, NumericType.class, TextType.class, DateType.class, GeometryType.class),
	DIMENSION(new DimensionColumnType(), DimensionReference.class, IntegerType.class), 
	TIMEDIMENSION(new TimeDimensionColumnType(), TimeDimensionReference.class, IntegerType.class), 
	MEASURE(new MeasureColumnType(), FormatReference.class, IntegerType.class, NumericType.class),
	CODENAME(new CodeNameColumnType(), LocaleReference.class,TextType.class),
	CODEDESCRIPTION(new CodeDescriptionColumnType(), null, TextType.class),
	ANNOTATION(new AnnotationColumnType(),null, TextType.class),
	CODE(new CodeColumnType(), null, TextType.class);
	
	private List<Class<? extends DataType>> classes;
	
	private boolean referenceRequired;
	
	private Class<?> referenceClass;
	
	private ColumnType modelType;
	
	private ColumnCategory(ColumnType modelType, Class<?> referenceClass, Class<? extends DataType> ... classes) {
		this.classes = Arrays.asList(classes);
		this.referenceRequired = referenceClass!=null;
		this.referenceClass = referenceClass;
		this.modelType = modelType;
	}

	/**
	 * @return the classes
	 */
	public List<Class<? extends DataType>> getAllowedClasses() {
		return classes;
	}

	/**
	 * @return the referenceRequired
	 */
	public boolean isReferenceRequired() {
		return referenceRequired;
	}

	public Class<?> getReferenceClass(){
		return referenceClass;
	}

	public ColumnType getModelType() {
		return modelType;
	}
		
}
