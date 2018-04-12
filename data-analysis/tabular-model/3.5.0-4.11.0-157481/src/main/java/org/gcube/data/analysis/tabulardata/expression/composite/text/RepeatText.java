package org.gcube.data.analysis.tabulardata.expression.composite.text;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.MalformedExpressionException;
import org.gcube.data.analysis.tabulardata.expression.NotEvaluableDataTypeException;
import org.gcube.data.analysis.tabulardata.expression.Operator;
import org.gcube.data.analysis.tabulardata.expression.composite.CompositeExpression;
import org.gcube.data.analysis.tabulardata.expression.leaf.LeafExpression;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.datatype.IntegerType;
import org.gcube.data.analysis.tabulardata.model.datatype.TextType;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class RepeatText  extends CompositeExpression implements TextExpression{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2210765202980244382L;

	Expression times;
	Expression value;
	
	
	protected RepeatText(){}
	
	public RepeatText(Expression value, Expression repeatTimes) {
		super();
		this.times = repeatTimes;
		this.value = value;
	}

	@Override
	public Operator getOperator() {
		return Operator.REPEAT_TEXT;
	}
	
	

	public Expression getTimes() {
		return times;
	}

	public Expression getValue() {
		return value;
	}

	@Override
	public void validate() throws MalformedExpressionException {
		if(value==null) throw new MalformedExpressionException("value cannot be null. "+this);
		if(times==null) throw new MalformedExpressionException("repeat times cannot be null. "+this);
		value.validate();
		times.validate();
		try{
			DataType sourceType=value.getReturnedDataType();
			if(!(sourceType instanceof TextType)) throw new MalformedExpressionException("value expression must return string type. Returned Type is "+sourceType.getName()+"."+this);
		}catch(NotEvaluableDataTypeException e){/* not evaluable*/}
		try{
			DataType fromType=times.getReturnedDataType();
			if(!(fromType instanceof IntegerType)) throw new MalformedExpressionException("RepeatTimes expression must return integer type. Returned Type is "+fromType.getName()+"."+this);
		}catch(NotEvaluableDataTypeException e){/* not evaluable*/}
	}

	@Override
	public DataType getReturnedDataType() throws NotEvaluableDataTypeException {
		return new TextType();
	}
	
	@Override
	public List<Expression> getLeavesByType(Class<? extends LeafExpression> type) {
		List<Expression> toReturn = new ArrayList<>();
		if (type.isInstance(times))
				toReturn.add(times);
		else toReturn.addAll(times.getLeavesByType(type));
		if (type.isInstance(value))
			toReturn.add(value);
		else toReturn.addAll(value.getLeavesByType(type));
		return toReturn;
	}

	@Override
	public String toString() {
		return "RepeatText [times=" + times + ", value=" + value + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((times == null) ? 0 : times.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RepeatText other = (RepeatText) obj;
		if (times == null) {
			if (other.times != null)
				return false;
		} else if (!times.equals(other.times))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	
	
}
