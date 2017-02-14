package org.gcube.data.analysis.tabulardata.expression.composite.condtional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.gcube.data.analysis.tabulardata.model.datatype.BooleanType;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;

@XmlRootElement (name="Case")
@XmlAccessorType(XmlAccessType.FIELD)
public class Case extends CompositeExpression implements ConditionalExpression{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7186075527181316821L;

	private List<WhenConstruct> whens;

	protected Case(){}

	public Case(WhenConstruct ... whens){
		this.whens = Arrays.asList(whens);
	}


	@Override
	public Operator getOperator() {
		return Operator.CASE;
	}

	@Override
	public void validate() throws MalformedExpressionException {
		if (whens==null || whens.isEmpty())
			throw new MalformedExpressionException("empty case");
		Class<? extends DataType> dataTypeClass = null;
		try{
			for (WhenConstruct when : whens){
				
				when.getWhen().validate();
				when.getThen().validate();
				if(!when.getWhen().getReturnedDataType().getClass().equals(BooleanType.class))
					throw new MalformedExpressionException("the when part must return Boolean");
				if (dataTypeClass==null)
					dataTypeClass = when.getThen().getReturnedDataType().getClass();
				else if(!dataTypeClass.equals(when.getThen().getReturnedDataType().getClass()))
					throw new MalformedExpressionException("all then parts must return the same type");
			}
		}catch(NotEvaluableDataTypeException nedt){
			throw new MalformedExpressionException("expression types are not evaluable");
		}


	}

	
	
	public List<WhenConstruct> getWhenConstructs() {
		return whens;
	}

	@Override
	public DataType getReturnedDataType() throws NotEvaluableDataTypeException {

		return null;
	}

	@XmlRootElement
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class WhenConstruct implements Serializable{

		/**
		 * 
		 */
		private static final long serialVersionUID = 7429597146623986788L;
		private Expression when;
		private Expression then;

		protected WhenConstruct(){}
		
		public WhenConstruct(Expression when, Expression then) {
			super();
			this.when = when;
			this.then = then;
		}

		/**
		 * @return the when
		 */
		public Expression getWhen() {
			return when;
		}

		/**
		 * @return the then
		 */
		public Expression getThen() {
			return then;
		}
		
		public void setWhen(Expression when) {
			this.when = when;
		}

		public void setThen(Expression then) {
			this.then = then;
		}

		@Override
		public String toString() {
			return "WhenConstruct [when=" + when + ", then=" + then + "]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((then == null) ? 0 : then.hashCode());
			result = prime * result + ((when == null) ? 0 : when.hashCode());
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
			WhenConstruct other = (WhenConstruct) obj;
			if (then == null) {
				if (other.then != null)
					return false;
			} else if (!then.equals(other.then))
				return false;
			if (when == null) {
				if (other.when != null)
					return false;
			} else if (!when.equals(other.when))
				return false;
			return true;
		}

		
	}
	
	@Override
	public List<Expression> getLeavesByType(Class<? extends LeafExpression> type) {
		List<Expression> toReturn = new ArrayList<>();
		for(WhenConstruct whenConstruct: this.getWhenConstructs()){
			if (type.isInstance(whenConstruct.getWhen()))
				toReturn.add(whenConstruct.getWhen());
			else toReturn.addAll(whenConstruct.getWhen().getLeavesByType(type));
			if (type.isInstance(whenConstruct.getThen()))
				toReturn.add(whenConstruct.getThen());
			else toReturn.addAll(whenConstruct.getThen().getLeavesByType(type));
				
		}
		return toReturn;
	}

	@Override
	public String toString() {
		return "Case [whens=" + whens + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((whens == null) ? 0 : whens.hashCode());
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
		Case other = (Case) obj;
		if (whens == null) {
			if (other.whens != null)
				return false;
		} else if (!whens.equals(other.whens))
			return false;
		return true;
	}
		
}
