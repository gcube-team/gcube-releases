package org.gcube.data.analysis.tabulardata.expression.evaluator.sql;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.composite.BinaryExpression;
import org.gcube.data.analysis.tabulardata.expression.composite.ExternalReferenceExpression;
import org.gcube.data.analysis.tabulardata.expression.composite.MultipleArgumentsExpression;
import org.gcube.data.analysis.tabulardata.expression.composite.UnaryExpression;
import org.gcube.data.analysis.tabulardata.expression.composite.condtional.Case;
import org.gcube.data.analysis.tabulardata.expression.composite.text.SubstringByIndex;
import org.gcube.data.analysis.tabulardata.expression.composite.text.SubstringByRegex;
import org.gcube.data.analysis.tabulardata.expression.composite.text.SubstringPosition;
import org.gcube.data.analysis.tabulardata.expression.composite.text.TextBeginsWith;
import org.gcube.data.analysis.tabulardata.expression.composite.text.TextContains;
import org.gcube.data.analysis.tabulardata.expression.composite.text.TextEndsWith;
import org.gcube.data.analysis.tabulardata.expression.composite.text.TextMatchPosixRegexp;
import org.gcube.data.analysis.tabulardata.expression.composite.text.TextMatchSQLRegexp;
import org.gcube.data.analysis.tabulardata.expression.composite.text.TextReplaceMatchingRegex;
import org.gcube.data.analysis.tabulardata.expression.evaluator.Evaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.EvaluatorFactory;
import org.gcube.data.analysis.tabulardata.expression.evaluator.ReferenceResolver;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.evaluators.CaseExpressionEvaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.evaluators.CastExpressionEvaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.evaluators.SingleArgumentExpressionEvaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.evaluators.bool.BetweenExpressionEvaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.evaluators.bool.BinaryExpressionEvaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.evaluators.bool.ExternalReferenceEvaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.evaluators.bool.MultipleArgumentsExpressionEvaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.evaluators.text.SubstringByIndexEvaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.evaluators.text.SubstringByRegexEvaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.evaluators.text.SubstringPositionEvaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.evaluators.text.TextBeginsEvaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.evaluators.text.TextContainsEvaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.evaluators.text.TextEndsWithEvaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.evaluators.text.TextMatchPosixregexpEvaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.evaluators.text.TextMatchSQLRegexpEvaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.evaluators.text.TextReplaceMatchRegexEvaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.leaf.ColumnReferenceEvaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.leaf.ConstantListEvaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.leaf.PlaceholderEvaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.leaf.TDTypeValueEvaluator;
import org.gcube.data.analysis.tabulardata.expression.functions.Cast;
import org.gcube.data.analysis.tabulardata.expression.leaf.ColumnReferencePlaceholder;
import org.gcube.data.analysis.tabulardata.expression.leaf.ConstantList;
import org.gcube.data.analysis.tabulardata.expression.logical.Between;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDTypeValue;
import org.gcube.data.analysis.tabulardata.model.mapping.PostgreSQLModelMapper;
import org.gcube.data.analysis.tabulardata.model.mapping.SQLModelMapper;

@Singleton
public class SQLExpressionEvaluatorFactory implements EvaluatorFactory<String> {

	private ReferenceResolver evaluatorUtil;

	private SQLModelMapper sqlModelMapper = new PostgreSQLModelMapper();	

	@Inject
	public SQLExpressionEvaluatorFactory(ReferenceResolver evaluatorUtil) {
		this.evaluatorUtil = evaluatorUtil;
	}

	public Evaluator<String> getEvaluator(Expression expression) {
		
		if(expression instanceof Cast) return new CastExpressionEvaluator((Cast) expression, this, sqlModelMapper);
		
		//TEXT
		if(expression instanceof TextReplaceMatchingRegex) return new TextReplaceMatchRegexEvaluator((TextReplaceMatchingRegex) expression, this);
		if(expression instanceof TextBeginsWith) return new TextBeginsEvaluator((TextBeginsWith) expression, this);
		if(expression instanceof TextContains) return new TextContainsEvaluator((TextContains) expression, this);
		if(expression instanceof TextEndsWith) return new TextEndsWithEvaluator((TextEndsWith) expression, this);
		if(expression instanceof TextMatchSQLRegexp) return new TextMatchSQLRegexpEvaluator((TextMatchSQLRegexp) expression, this);
		if(expression instanceof TextMatchPosixRegexp) return new TextMatchPosixregexpEvaluator((TextMatchPosixRegexp) expression, this);
		if(expression instanceof SubstringByRegex) return new SubstringByRegexEvaluator((SubstringByRegex) expression, this);
		if(expression instanceof SubstringByIndex) return new SubstringByIndexEvaluator((SubstringByIndex) expression, this);
		if(expression instanceof SubstringPosition) return new SubstringPositionEvaluator((SubstringPosition) expression, this);
		
		//Composite
		
		if(expression instanceof Between) return new BetweenExpressionEvaluator((Between) expression, this);
		
		if(expression instanceof ExternalReferenceExpression) return new ExternalReferenceEvaluator((ExternalReferenceExpression) expression,this);
		if(expression instanceof MultipleArgumentsExpression) return new MultipleArgumentsExpressionEvaluator(this, (MultipleArgumentsExpression) expression);
		if(expression instanceof BinaryExpression) return new BinaryExpressionEvaluator(this, (BinaryExpression)expression);
		if(expression instanceof UnaryExpression) return new SingleArgumentExpressionEvaluator(this, (UnaryExpression)expression);

		//CONDITIONAL
		if(expression instanceof Case) return new CaseExpressionEvaluator(this, (Case)expression);
	
		
		//Leaf
		if(expression instanceof ColumnReference) return new ColumnReferenceEvaluator((ColumnReference) expression, evaluatorUtil);
		if(expression instanceof TDTypeValue) return new TDTypeValueEvaluator((TDTypeValue) expression,sqlModelMapper);
		if(expression instanceof ColumnReferencePlaceholder) return new PlaceholderEvaluator((ColumnReferencePlaceholder) expression);
		
		if(expression instanceof ConstantList) return new ConstantListEvaluator((ConstantList) expression, this);
		throw new RuntimeException(
				"Unable to provide an evaluator for the given expression. Missing evaluator for expression "
						+ expression);

	}

}
