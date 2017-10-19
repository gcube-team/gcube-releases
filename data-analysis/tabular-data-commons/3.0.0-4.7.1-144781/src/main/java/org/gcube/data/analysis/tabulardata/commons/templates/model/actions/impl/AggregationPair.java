package org.gcube.data.analysis.tabulardata.commons.templates.model.actions.impl;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.commons.templates.model.Representable;
import org.gcube.data.analysis.tabulardata.commons.templates.model.columns.TemplateColumn;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.datatype.GeometryType;
import org.gcube.data.analysis.tabulardata.model.datatype.IntegerType;
import org.gcube.data.analysis.tabulardata.model.datatype.NumericType;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class AggregationPair implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7119921002012024007L;
	
	@SuppressWarnings("unchecked")
	public enum AggregationFunction implements Representable{
		
		AVG(IntegerType.class, NumericType.class),
		COUNT(),	
		MAX(IntegerType.class, NumericType.class),
		MIN(IntegerType.class, NumericType.class),
		SUM(IntegerType.class, NumericType.class),
		ST_EXTENT(GeometryType.class);
		
		List<Class<? extends DataType>> allowedTypes; 
		
		AggregationFunction(Class<? extends DataType> ... types){
			this.allowedTypes = Arrays.asList(types);
		}

		public List<Class<? extends DataType>> getAllowedTypes() {
			return allowedTypes;
		}

		@Override
		public String getRepresentation() {
			return this.name();
		}
	
	}
	
	private TemplateColumn<?> column;
	private AggregationFunction function;
	
	protected AggregationPair(){}
	
	public AggregationPair(TemplateColumn<?> column,
			AggregationFunction function) {
		if (!function.getAllowedTypes().isEmpty() && !function.getAllowedTypes().contains(column.getValueType()))
			throw new IllegalArgumentException(
					String.format("the aggregation function %s cannot be applyed to DataType %s"
							, function.name(), column.getValueType().getName()));
		this.column = column;
		this.function = function;
	}

	public TemplateColumn<?> getColumn() {
		return column;
	}

	public AggregationFunction getFunction() {
		return function;
	}
		
}
