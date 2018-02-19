package org.gcube.data.analysis.tabulardata.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.MalformedExpressionException;
import org.gcube.data.analysis.tabulardata.expression.PlaceholderReplacer;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Converter implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4502221521951722442L;

	public static Converter converter(Expression expression){
		return new Converter(expression);
	}

	private Expression expression;

	protected Converter(Expression expression) {
		super();
		this.expression = expression;
	}

	public Expression getExpression(ColumnReference cr) throws MalformedExpressionException{
		PlaceholderReplacer pr = new PlaceholderReplacer(expression);
		pr.replaceAll(cr);
		return pr.getExpression();
	}

}
