// Generated from /home/lucio/workspace/imarine/SPQLParser.BRANCH/src/main/resources/SPQL.g by ANTLR 4.6

package org.gcube.dataaccess.spql;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link SPQLParser}.
 */
public interface SPQLListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link SPQLParser#query}.
	 * @param ctx the parse tree
	 */
	void enterQuery(SPQLParser.QueryContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPQLParser#query}.
	 * @param ctx the parse tree
	 */
	void exitQuery(SPQLParser.QueryContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPQLParser#termsRule}.
	 * @param ctx the parse tree
	 */
	void enterTermsRule(SPQLParser.TermsRuleContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPQLParser#termsRule}.
	 * @param ctx the parse tree
	 */
	void exitTermsRule(SPQLParser.TermsRuleContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPQLParser#termRule}.
	 * @param ctx the parse tree
	 */
	void enterTermRule(SPQLParser.TermRuleContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPQLParser#termRule}.
	 * @param ctx the parse tree
	 */
	void exitTermRule(SPQLParser.TermRuleContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPQLParser#scientificTerms}.
	 * @param ctx the parse tree
	 */
	void enterScientificTerms(SPQLParser.ScientificTermsContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPQLParser#scientificTerms}.
	 * @param ctx the parse tree
	 */
	void exitScientificTerms(SPQLParser.ScientificTermsContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPQLParser#commonNameTerms}.
	 * @param ctx the parse tree
	 */
	void enterCommonNameTerms(SPQLParser.CommonNameTermsContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPQLParser#commonNameTerms}.
	 * @param ctx the parse tree
	 */
	void exitCommonNameTerms(SPQLParser.CommonNameTermsContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPQLParser#wordsRule}.
	 * @param ctx the parse tree
	 */
	void enterWordsRule(SPQLParser.WordsRuleContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPQLParser#wordsRule}.
	 * @param ctx the parse tree
	 */
	void exitWordsRule(SPQLParser.WordsRuleContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPQLParser#wordRule}.
	 * @param ctx the parse tree
	 */
	void enterWordRule(SPQLParser.WordRuleContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPQLParser#wordRule}.
	 * @param ctx the parse tree
	 */
	void exitWordRule(SPQLParser.WordRuleContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPQLParser#unfoldClause}.
	 * @param ctx the parse tree
	 */
	void enterUnfoldClause(SPQLParser.UnfoldClauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPQLParser#unfoldClause}.
	 * @param ctx the parse tree
	 */
	void exitUnfoldClause(SPQLParser.UnfoldClauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPQLParser#expandClause}.
	 * @param ctx the parse tree
	 */
	void enterExpandClause(SPQLParser.ExpandClauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPQLParser#expandClause}.
	 * @param ctx the parse tree
	 */
	void exitExpandClause(SPQLParser.ExpandClauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPQLParser#resolveClause}.
	 * @param ctx the parse tree
	 */
	void enterResolveClause(SPQLParser.ResolveClauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPQLParser#resolveClause}.
	 * @param ctx the parse tree
	 */
	void exitResolveClause(SPQLParser.ResolveClauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPQLParser#datasourcesRule}.
	 * @param ctx the parse tree
	 */
	void enterDatasourcesRule(SPQLParser.DatasourcesRuleContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPQLParser#datasourcesRule}.
	 * @param ctx the parse tree
	 */
	void exitDatasourcesRule(SPQLParser.DatasourcesRuleContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPQLParser#datasourceRule}.
	 * @param ctx the parse tree
	 */
	void enterDatasourceRule(SPQLParser.DatasourceRuleContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPQLParser#datasourceRule}.
	 * @param ctx the parse tree
	 */
	void exitDatasourceRule(SPQLParser.DatasourceRuleContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPQLParser#rankRule}.
	 * @param ctx the parse tree
	 */
	void enterRankRule(SPQLParser.RankRuleContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPQLParser#rankRule}.
	 * @param ctx the parse tree
	 */
	void exitRankRule(SPQLParser.RankRuleContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPQLParser#whereExpression}.
	 * @param ctx the parse tree
	 */
	void enterWhereExpression(SPQLParser.WhereExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPQLParser#whereExpression}.
	 * @param ctx the parse tree
	 */
	void exitWhereExpression(SPQLParser.WhereExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPQLParser#wexpression}.
	 * @param ctx the parse tree
	 */
	void enterWexpression(SPQLParser.WexpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPQLParser#wexpression}.
	 * @param ctx the parse tree
	 */
	void exitWexpression(SPQLParser.WexpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPQLParser#coordinateCondition}.
	 * @param ctx the parse tree
	 */
	void enterCoordinateCondition(SPQLParser.CoordinateConditionContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPQLParser#coordinateCondition}.
	 * @param ctx the parse tree
	 */
	void exitCoordinateCondition(SPQLParser.CoordinateConditionContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPQLParser#eventDateCondition}.
	 * @param ctx the parse tree
	 */
	void enterEventDateCondition(SPQLParser.EventDateConditionContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPQLParser#eventDateCondition}.
	 * @param ctx the parse tree
	 */
	void exitEventDateCondition(SPQLParser.EventDateConditionContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPQLParser#relationalOperator}.
	 * @param ctx the parse tree
	 */
	void enterRelationalOperator(SPQLParser.RelationalOperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPQLParser#relationalOperator}.
	 * @param ctx the parse tree
	 */
	void exitRelationalOperator(SPQLParser.RelationalOperatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPQLParser#dateRule}.
	 * @param ctx the parse tree
	 */
	void enterDateRule(SPQLParser.DateRuleContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPQLParser#dateRule}.
	 * @param ctx the parse tree
	 */
	void exitDateRule(SPQLParser.DateRuleContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPQLParser#coordinateRule}.
	 * @param ctx the parse tree
	 */
	void enterCoordinateRule(SPQLParser.CoordinateRuleContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPQLParser#coordinateRule}.
	 * @param ctx the parse tree
	 */
	void exitCoordinateRule(SPQLParser.CoordinateRuleContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPQLParser#returnExpression}.
	 * @param ctx the parse tree
	 */
	void enterReturnExpression(SPQLParser.ReturnExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPQLParser#returnExpression}.
	 * @param ctx the parse tree
	 */
	void exitReturnExpression(SPQLParser.ReturnExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPQLParser#havingExpression}.
	 * @param ctx the parse tree
	 */
	void enterHavingExpression(SPQLParser.HavingExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPQLParser#havingExpression}.
	 * @param ctx the parse tree
	 */
	void exitHavingExpression(SPQLParser.HavingExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPQLParser#number}.
	 * @param ctx the parse tree
	 */
	void enterNumber(SPQLParser.NumberContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPQLParser#number}.
	 * @param ctx the parse tree
	 */
	void exitNumber(SPQLParser.NumberContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPQLParser#unary_operator}.
	 * @param ctx the parse tree
	 */
	void enterUnary_operator(SPQLParser.Unary_operatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPQLParser#unary_operator}.
	 * @param ctx the parse tree
	 */
	void exitUnary_operator(SPQLParser.Unary_operatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPQLParser#unsigned_number}.
	 * @param ctx the parse tree
	 */
	void enterUnsigned_number(SPQLParser.Unsigned_numberContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPQLParser#unsigned_number}.
	 * @param ctx the parse tree
	 */
	void exitUnsigned_number(SPQLParser.Unsigned_numberContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPQLParser#expressionRule}.
	 * @param ctx the parse tree
	 */
	void enterExpressionRule(SPQLParser.ExpressionRuleContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPQLParser#expressionRule}.
	 * @param ctx the parse tree
	 */
	void exitExpressionRule(SPQLParser.ExpressionRuleContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPQLParser#parExpression}.
	 * @param ctx the parse tree
	 */
	void enterParExpression(SPQLParser.ParExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPQLParser#parExpression}.
	 * @param ctx the parse tree
	 */
	void exitParExpression(SPQLParser.ParExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPQLParser#conditionalAndExpression}.
	 * @param ctx the parse tree
	 */
	void enterConditionalAndExpression(SPQLParser.ConditionalAndExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPQLParser#conditionalAndExpression}.
	 * @param ctx the parse tree
	 */
	void exitConditionalAndExpression(SPQLParser.ConditionalAndExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPQLParser#equalityExpression}.
	 * @param ctx the parse tree
	 */
	void enterEqualityExpression(SPQLParser.EqualityExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPQLParser#equalityExpression}.
	 * @param ctx the parse tree
	 */
	void exitEqualityExpression(SPQLParser.EqualityExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPQLParser#relationalExpression}.
	 * @param ctx the parse tree
	 */
	void enterRelationalExpression(SPQLParser.RelationalExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPQLParser#relationalExpression}.
	 * @param ctx the parse tree
	 */
	void exitRelationalExpression(SPQLParser.RelationalExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPQLParser#relationalOp}.
	 * @param ctx the parse tree
	 */
	void enterRelationalOp(SPQLParser.RelationalOpContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPQLParser#relationalOp}.
	 * @param ctx the parse tree
	 */
	void exitRelationalOp(SPQLParser.RelationalOpContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPQLParser#additiveExpression}.
	 * @param ctx the parse tree
	 */
	void enterAdditiveExpression(SPQLParser.AdditiveExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPQLParser#additiveExpression}.
	 * @param ctx the parse tree
	 */
	void exitAdditiveExpression(SPQLParser.AdditiveExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPQLParser#multiplicativeExpression}.
	 * @param ctx the parse tree
	 */
	void enterMultiplicativeExpression(SPQLParser.MultiplicativeExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPQLParser#multiplicativeExpression}.
	 * @param ctx the parse tree
	 */
	void exitMultiplicativeExpression(SPQLParser.MultiplicativeExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPQLParser#unaryExpression}.
	 * @param ctx the parse tree
	 */
	void enterUnaryExpression(SPQLParser.UnaryExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPQLParser#unaryExpression}.
	 * @param ctx the parse tree
	 */
	void exitUnaryExpression(SPQLParser.UnaryExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPQLParser#unaryExpressionNotPlusMinus}.
	 * @param ctx the parse tree
	 */
	void enterUnaryExpressionNotPlusMinus(SPQLParser.UnaryExpressionNotPlusMinusContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPQLParser#unaryExpressionNotPlusMinus}.
	 * @param ctx the parse tree
	 */
	void exitUnaryExpressionNotPlusMinus(SPQLParser.UnaryExpressionNotPlusMinusContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPQLParser#primary}.
	 * @param ctx the parse tree
	 */
	void enterPrimary(SPQLParser.PrimaryContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPQLParser#primary}.
	 * @param ctx the parse tree
	 */
	void exitPrimary(SPQLParser.PrimaryContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPQLParser#calls}.
	 * @param ctx the parse tree
	 */
	void enterCalls(SPQLParser.CallsContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPQLParser#calls}.
	 * @param ctx the parse tree
	 */
	void exitCalls(SPQLParser.CallsContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPQLParser#xpath_function}.
	 * @param ctx the parse tree
	 */
	void enterXpath_function(SPQLParser.Xpath_functionContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPQLParser#xpath_function}.
	 * @param ctx the parse tree
	 */
	void exitXpath_function(SPQLParser.Xpath_functionContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPQLParser#exl_function}.
	 * @param ctx the parse tree
	 */
	void enterExl_function(SPQLParser.Exl_functionContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPQLParser#exl_function}.
	 * @param ctx the parse tree
	 */
	void exitExl_function(SPQLParser.Exl_functionContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPQLParser#lucio_function}.
	 * @param ctx the parse tree
	 */
	void enterLucio_function(SPQLParser.Lucio_functionContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPQLParser#lucio_function}.
	 * @param ctx the parse tree
	 */
	void exitLucio_function(SPQLParser.Lucio_functionContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPQLParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterLiteral(SPQLParser.LiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPQLParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitLiteral(SPQLParser.LiteralContext ctx);
}