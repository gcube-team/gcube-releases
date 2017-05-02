package org.gcube.data.analysis.tabulardata.expression.evaluator.description;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.composite.BinaryExpression;
import org.gcube.data.analysis.tabulardata.expression.composite.ExternalReferenceExpression;
import org.gcube.data.analysis.tabulardata.expression.composite.MultipleArgumentsExpression;
import org.gcube.data.analysis.tabulardata.expression.composite.UnaryExpression;
import org.gcube.data.analysis.tabulardata.expression.composite.text.SubstringByIndex;
import org.gcube.data.analysis.tabulardata.expression.composite.text.SubstringByRegex;
import org.gcube.data.analysis.tabulardata.expression.composite.text.SubstringPosition;
import org.gcube.data.analysis.tabulardata.expression.composite.text.TextReplaceMatchingRegex;
import org.gcube.data.analysis.tabulardata.expression.evaluator.Evaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.EvaluatorFactory;
import org.gcube.data.analysis.tabulardata.expression.evaluator.description.composite.BinaryExpressionEvaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.description.composite.CastExpressionEvaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.description.composite.ExternalReferenceExpressionEvaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.description.composite.MultipleArgumentExpressionEvaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.description.composite.SingleArgumentEvaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.description.composite.SubstringByIndexEvaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.description.composite.SubstringByRegexEvaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.description.composite.SubstringPositionEvaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.description.composite.TextReplaceMatchingRegexEvaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.description.leaf.ColumnReferenceEvaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.description.leaf.ConstantListEvaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.description.leaf.PlaceholderEvaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.description.leaf.TDTypeValueEvaluator;
import org.gcube.data.analysis.tabulardata.expression.functions.Cast;
import org.gcube.data.analysis.tabulardata.expression.leaf.ColumnReferencePlaceholder;
import org.gcube.data.analysis.tabulardata.expression.leaf.ConstantList;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDTypeValue;

@Singleton
public class DescriptionExpressionEvaluatorFactory implements EvaluatorFactory<String> {
	
	private NamesRetriever namesRetriever;
	
	@Inject
	public DescriptionExpressionEvaluatorFactory(NamesRetriever namesRetriever) {
		this.namesRetriever = namesRetriever;
	}


	
	public Evaluator<String> getEvaluator(Expression expression) {
		
		if(expression instanceof Cast) return new CastExpressionEvaluator((Cast) expression, this);
		
		if(expression instanceof ExternalReferenceExpression) return new ExternalReferenceExpressionEvaluator((ExternalReferenceExpression) expression, this);
		if(expression instanceof TextReplaceMatchingRegex) return new TextReplaceMatchingRegexEvaluator((TextReplaceMatchingRegex) expression, this);
		if(expression instanceof SubstringPosition) return new SubstringPositionEvaluator((SubstringPosition) expression, this);
		if(expression instanceof SubstringByRegex) return new SubstringByRegexEvaluator((SubstringByRegex) expression, this);
		if(expression instanceof SubstringByIndex) return new SubstringByIndexEvaluator((SubstringByIndex) expression, this);
		
		
		//Composite
		if(expression instanceof MultipleArgumentsExpression) return new MultipleArgumentExpressionEvaluator(this, (MultipleArgumentsExpression) expression);
		if(expression instanceof BinaryExpression) return new BinaryExpressionEvaluator(this, (BinaryExpression)expression);
		if(expression instanceof UnaryExpression) return new SingleArgumentEvaluator(this, (UnaryExpression)expression);
		
		//Leaf
		if(expression instanceof ColumnReference) return new ColumnReferenceEvaluator((ColumnReference) expression, namesRetriever);
		if(expression instanceof TDTypeValue) return new TDTypeValueEvaluator((TDTypeValue) expression);
		if(expression instanceof ColumnReferencePlaceholder) return new PlaceholderEvaluator((ColumnReferencePlaceholder) expression);
		if(expression instanceof ConstantList) return new ConstantListEvaluator((ConstantList) expression, this);
		throw new RuntimeException("Unable to provide an evaluator for the given expression : "+expression.getClass());
	}
	
	
}
