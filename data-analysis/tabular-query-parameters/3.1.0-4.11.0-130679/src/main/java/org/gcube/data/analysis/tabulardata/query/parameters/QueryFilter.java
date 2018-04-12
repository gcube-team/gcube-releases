package org.gcube.data.analysis.tabulardata.query.parameters;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.Equals;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.GreaterOrEquals;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.GreaterThan;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.LessOrEquals;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.LessThan;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.NotEquals;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.NotGreater;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.NotLess;
import org.gcube.data.analysis.tabulardata.expression.composite.text.TextBeginsWith;
import org.gcube.data.analysis.tabulardata.expression.composite.text.TextContains;
import org.gcube.data.analysis.tabulardata.expression.composite.text.TextEndsWith;
import org.gcube.data.analysis.tabulardata.expression.composite.text.TextMatchSQLRegexp;
import org.gcube.data.analysis.tabulardata.expression.logical.And;
import org.gcube.data.analysis.tabulardata.expression.logical.Between;
import org.gcube.data.analysis.tabulardata.expression.logical.IsNotNull;
import org.gcube.data.analysis.tabulardata.expression.logical.IsNull;
import org.gcube.data.analysis.tabulardata.expression.logical.Not;
import org.gcube.data.analysis.tabulardata.expression.logical.Or;
import org.gcube.data.analysis.tabulardata.expression.logical.ValueIsIn;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class QueryFilter {

	@XmlElementRefs({ 
		@XmlElementRef(type = And.class), 
		@XmlElementRef(type = Or.class),
		@XmlElementRef(type = IsNull.class),
		@XmlElementRef(type = IsNotNull.class),
		@XmlElementRef(type = ValueIsIn.class),
		@XmlElementRef(type = Equals.class),
		@XmlElementRef(type = GreaterThan.class),
		@XmlElementRef(type = LessThan.class),
		@XmlElementRef(type = NotEquals.class),
		@XmlElementRef(type = TextContains.class),
		@XmlElementRef(type = TextMatchSQLRegexp.class),
		@XmlElementRef(type = ColumnReference.class),
		@XmlElementRef(type = Not.class),
		@XmlElementRef(type = Between.class),
		@XmlElementRef(type = GreaterOrEquals.class),
		@XmlElementRef(type = GreaterThan.class),
		@XmlElementRef(type = LessOrEquals.class),
		@XmlElementRef(type = NotGreater.class),
		@XmlElementRef(type = NotLess.class),
		@XmlElementRef(type = TextBeginsWith.class),
		@XmlElementRef(type = TextEndsWith.class),
		@XmlElementRef(type = ColumnReference.class)
	})
	private Expression expression;

	@SuppressWarnings("unused")
	private QueryFilter() {
	}

	public QueryFilter(Expression expression) {
		super();
		this.expression = expression;
	}

	public Expression getFilterExpression() {
		return expression;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((expression == null) ? 0 : expression.hashCode());
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
		QueryFilter other = (QueryFilter) obj;
		if (expression == null) {
			if (other.expression != null)
				return false;
		} else if (!expression.equals(other.expression))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("QueryFilter [expression=");
		builder.append(expression);
		builder.append("]");
		return builder.toString();
	}

}
