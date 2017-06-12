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
import org.gcube.data.analysis.tabulardata.model.datatype.BooleanType;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.datatype.DateType;
import org.gcube.data.analysis.tabulardata.model.datatype.GeometryType;
import org.gcube.data.analysis.tabulardata.model.datatype.IntegerType;
import org.gcube.data.analysis.tabulardata.model.datatype.NumericType;
import org.gcube.data.analysis.tabulardata.model.datatype.TextType;
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Count extends UnaryExpression implements AggregationExpression {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5043963811568398617L;

	private static final List<Class<? extends DataType>> DEFAULT_ACCEPTED_TYPES=new ArrayList<>();
	
	static {
		DEFAULT_ACCEPTED_TYPES.add(TextType.class);
		DEFAULT_ACCEPTED_TYPES.add(BooleanType.class);
		DEFAULT_ACCEPTED_TYPES.add(DateType.class);		
		DEFAULT_ACCEPTED_TYPES.add(IntegerType.class);
		DEFAULT_ACCEPTED_TYPES.add(NumericType.class);
		DEFAULT_ACCEPTED_TYPES.add(GeometryType.class);
	}

	@Override
	public List<Class<? extends DataType>> allowedDataTypes(){
		return DEFAULT_ACCEPTED_TYPES;
	}
	
	@SuppressWarnings("unused")
	private Count() {
	}
	
	
	
	public Count(Expression argument) {
		super(argument);
	}



	@Override
	public Operator getOperator() {
		return Operator.COUNT;
	}

	@Override
	public DataType getReturnedDataType() throws NotEvaluableDataTypeException {
		return new IntegerType();
	}

}
