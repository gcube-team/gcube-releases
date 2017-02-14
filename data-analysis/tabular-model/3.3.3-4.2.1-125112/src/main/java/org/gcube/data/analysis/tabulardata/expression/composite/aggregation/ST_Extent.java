package org.gcube.data.analysis.tabulardata.expression.composite.aggregation;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.NotEvaluableDataTypeException;
import org.gcube.data.analysis.tabulardata.expression.Operator;
import org.gcube.data.analysis.tabulardata.expression.composite.UnaryExpression;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.datatype.GeometryType;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ST_Extent extends UnaryExpression implements AggregationExpression{

	private static final List<Class<? extends DataType>> ACCEPTED_DATA_TYPES=new ArrayList<>();
	
	static {
		ACCEPTED_DATA_TYPES.add(GeometryType.class);		
	}
	
	@Override
	public List<Class<? extends DataType>> allowedDataTypes() {
		return ACCEPTED_DATA_TYPES;
	}
	
	
	@SuppressWarnings("unused")
	private ST_Extent() {	
	}
	
	
	
	public ST_Extent(Expression argument) {
		super(argument);	
	}



	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public Operator getOperator() {
		return Operator.ST_EXTENT;
	}

	@Override
	public DataType getReturnedDataType() throws NotEvaluableDataTypeException {
		return getArgument().getReturnedDataType();
	}

	
}
