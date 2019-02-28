package org.gcube.data.analysis.tabulardata.expression.composite.text;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.MalformedExpressionException;
import org.gcube.data.analysis.tabulardata.expression.Operator;
import org.gcube.data.analysis.tabulardata.expression.composite.CompositeExpression;
import org.gcube.data.analysis.tabulardata.expression.leaf.LeafExpression;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.datatype.TextType;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDText;
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TextReplaceMatchingRegex extends CompositeExpression implements TextExpression{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8463250380703209101L;

	private Expression toCheckText;
	private TDText regexp;
	private TDText replacingValue;
	private boolean caseSensitive=false;
	
	
	@Override
	public Operator getOperator() {
		return Operator.REPLACE_REGEX;
	}

	@Override
	public DataType getReturnedDataType(){
		return new TextType();
	}

	
	@SuppressWarnings("unused")
	private TextReplaceMatchingRegex() {		
	}

	public void setCaseSensitive(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}
	
	public boolean isCaseSensitive() {
		return caseSensitive;
	}
	
	
	/**
	 * The regexp_replace function provides substitution of new text for substrings that match POSIX regular expression patterns. 
	 * The toCheckText String is returned unchanged if there is no match to the regexp. 
	 * If there is a match, the toCheckText String is returned with the replacing string substituted for the matching substrings.
	 * 
	 * 
	 * @param toCheckText
	 * @param regexp
	 * @param replacing
	 */
	public TextReplaceMatchingRegex(Expression toCheckText, TDText regexp,
			TDText replacing) {
		super();
		this.toCheckText = toCheckText;
		this.regexp = regexp;
		this.replacingValue = replacing;
	}
	
	public TextReplaceMatchingRegex(Expression toCheckText, TDText regexp,
			TDText replacing,boolean caseSensitive) {
		super();
		this.toCheckText = toCheckText;
		this.regexp = regexp;
		this.replacingValue = replacing;
		this.caseSensitive=caseSensitive;
	}
	
	@Override
	public void validate() throws MalformedExpressionException {
		if(toCheckText==null) throw new MalformedExpressionException("To check text cannot be null. "+this);
		if(regexp==null) throw new MalformedExpressionException("Regexp value cannot be null."+this);
		if(replacingValue==null) throw new MalformedExpressionException("To set value cannot be null."+this);
		toCheckText.validate();
		regexp.validate();
		replacingValue.validate();		
	}

	/**
	 * @return the toCheckText
	 */
	public Expression getToCheckText() {
		return toCheckText;
	}

	/**
	 * @param toCheckText the toCheckText to set
	 */
	public void setToCheckText(Expression toCheckText) {
		this.toCheckText = toCheckText;
	}

	/**
	 * @return the regexp
	 */
	public TDText getRegexp() {
		return regexp;
	}

	/**
	 * @param regexp the regexp to set
	 */
	public void setRegexp(TDText regexp) {
		this.regexp = regexp;
	}

	/**
	 * @return the replacingValue
	 */
	public TDText getReplacingValue() {
		return replacingValue;
	}

	/**
	 * @param replacingValue the replacingValue to set
	 */
	public void setReplacingValue(TDText replacingValue) {
		this.replacingValue = replacingValue;
	}

	@Override
	public List<Expression> getLeavesByType(Class<? extends LeafExpression> type) {
		if (type.isInstance(toCheckText))
				return Collections.singletonList(toCheckText);
		else return toCheckText.getLeavesByType(type);

	}

	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TextReplaceMatchingRegex [toCheckText=");
		builder.append(toCheckText);
		builder.append(", regexp=");
		builder.append(regexp);
		builder.append(", replacingValue=");
		builder.append(replacingValue);
		builder.append(", caseSensitive=");
		builder.append(caseSensitive);
		builder.append("]");
		return builder.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (caseSensitive ? 1231 : 1237);
		result = prime * result + ((regexp == null) ? 0 : regexp.hashCode());
		result = prime * result
				+ ((replacingValue == null) ? 0 : replacingValue.hashCode());
		result = prime * result
				+ ((toCheckText == null) ? 0 : toCheckText.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TextReplaceMatchingRegex other = (TextReplaceMatchingRegex) obj;
		if (caseSensitive != other.caseSensitive)
			return false;
		if (regexp == null) {
			if (other.regexp != null)
				return false;
		} else if (!regexp.equals(other.regexp))
			return false;
		if (replacingValue == null) {
			if (other.replacingValue != null)
				return false;
		} else if (!replacingValue.equals(other.replacingValue))
			return false;
		if (toCheckText == null) {
			if (other.toCheckText != null)
				return false;
		} else if (!toCheckText.equals(other.toCheckText))
			return false;
		return true;
	}

	
	
	
}
