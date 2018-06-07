package org.gcube.data.analysis.tabulardata.expression.composite.text;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.gcube.data.analysis.tabulardata.expression.Expression;

@XmlAccessorType(XmlAccessType.FIELD)
public abstract class CaseSensitiveTextExpression extends BaseTextExpression {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5757992867331747169L;

	private boolean caseSensitive=true;
	
	protected CaseSensitiveTextExpression(){
		
	}
	
	
	public boolean isCaseSensitive() {		
		return caseSensitive;
	}

	public void setCaseSensitive(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}

	
	protected CaseSensitiveTextExpression(Expression leftArgument,
			Expression rightArgument, boolean caseSensitive) {
		super(leftArgument, rightArgument);
		this.caseSensitive = caseSensitive;
	}

	
	
	protected CaseSensitiveTextExpression(Expression leftArgument,
			Expression rightArgument) {
		super(leftArgument, rightArgument);
	
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (caseSensitive ? 1231 : 1237);
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		CaseSensitiveTextExpression other = (CaseSensitiveTextExpression) obj;
		if (caseSensitive != other.caseSensitive)
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TextExpression [caseSensitive=");
		builder.append(caseSensitive);
		builder.append(", left=");
		builder.append(getLeftArgument());
		builder.append(", right=");
		builder.append(getRightArgument());
		builder.append(", operator=");
		builder.append(getOperator());
		builder.append("]");
		return builder.toString();
	}
	
	
}
