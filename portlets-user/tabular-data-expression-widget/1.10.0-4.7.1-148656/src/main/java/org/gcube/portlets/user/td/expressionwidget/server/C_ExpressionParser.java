package org.gcube.portlets.user.td.expressionwidget.server;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.MultivaluedExpression;
import org.gcube.data.analysis.tabulardata.expression.composite.ExternalReferenceExpression;
import org.gcube.data.analysis.tabulardata.expression.composite.aggregation.Avg;
import org.gcube.data.analysis.tabulardata.expression.composite.aggregation.Count;
import org.gcube.data.analysis.tabulardata.expression.composite.aggregation.Max;
import org.gcube.data.analysis.tabulardata.expression.composite.aggregation.Min;
import org.gcube.data.analysis.tabulardata.expression.composite.aggregation.ST_Extent;
import org.gcube.data.analysis.tabulardata.expression.composite.aggregation.Sum;
import org.gcube.data.analysis.tabulardata.expression.composite.arithmetic.Addition;
import org.gcube.data.analysis.tabulardata.expression.composite.arithmetic.Division;
import org.gcube.data.analysis.tabulardata.expression.composite.arithmetic.Exponentiation;
import org.gcube.data.analysis.tabulardata.expression.composite.arithmetic.Modulus;
import org.gcube.data.analysis.tabulardata.expression.composite.arithmetic.Multiplication;
import org.gcube.data.analysis.tabulardata.expression.composite.arithmetic.Subtraction;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.Equals;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.GreaterOrEquals;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.GreaterThan;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.LessOrEquals;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.LessThan;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.NotEquals;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.NotGreater;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.NotLess;
import org.gcube.data.analysis.tabulardata.expression.composite.condtional.Case;
import org.gcube.data.analysis.tabulardata.expression.composite.text.Concat;
import org.gcube.data.analysis.tabulardata.expression.composite.text.Length;
import org.gcube.data.analysis.tabulardata.expression.composite.text.Levenshtein;
import org.gcube.data.analysis.tabulardata.expression.composite.text.Lower;
import org.gcube.data.analysis.tabulardata.expression.composite.text.MD5;
import org.gcube.data.analysis.tabulardata.expression.composite.text.RepeatText;
import org.gcube.data.analysis.tabulardata.expression.composite.text.Similarity;
import org.gcube.data.analysis.tabulardata.expression.composite.text.Soundex;
import org.gcube.data.analysis.tabulardata.expression.composite.text.SubstringByIndex;
import org.gcube.data.analysis.tabulardata.expression.composite.text.SubstringByRegex;
import org.gcube.data.analysis.tabulardata.expression.composite.text.SubstringPosition;
import org.gcube.data.analysis.tabulardata.expression.composite.text.TextBeginsWith;
import org.gcube.data.analysis.tabulardata.expression.composite.text.TextContains;
import org.gcube.data.analysis.tabulardata.expression.composite.text.TextEndsWith;
import org.gcube.data.analysis.tabulardata.expression.composite.text.TextMatchPosixRegexp;
import org.gcube.data.analysis.tabulardata.expression.composite.text.TextMatchSQLRegexp;
import org.gcube.data.analysis.tabulardata.expression.composite.text.TextReplaceMatchingRegex;
import org.gcube.data.analysis.tabulardata.expression.composite.text.Trim;
import org.gcube.data.analysis.tabulardata.expression.composite.text.Upper;
import org.gcube.data.analysis.tabulardata.expression.functions.Cast;
import org.gcube.data.analysis.tabulardata.expression.leaf.ColumnReferencePlaceholder;
import org.gcube.data.analysis.tabulardata.expression.leaf.ConstantList;
import org.gcube.data.analysis.tabulardata.expression.logical.And;
import org.gcube.data.analysis.tabulardata.expression.logical.Between;
import org.gcube.data.analysis.tabulardata.expression.logical.IsNotNull;
import org.gcube.data.analysis.tabulardata.expression.logical.IsNull;
import org.gcube.data.analysis.tabulardata.expression.logical.Not;
import org.gcube.data.analysis.tabulardata.expression.logical.Or;
import org.gcube.data.analysis.tabulardata.expression.logical.ValueIsIn;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.datatype.BooleanType;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.datatype.DateType;
import org.gcube.data.analysis.tabulardata.model.datatype.GeometryType;
import org.gcube.data.analysis.tabulardata.model.datatype.IntegerType;
import org.gcube.data.analysis.tabulardata.model.datatype.NumericType;
import org.gcube.data.analysis.tabulardata.model.datatype.TextType;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDBoolean;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDDate;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDGeometry;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDInteger;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDNumeric;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDText;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDTypeValue;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.portlets.user.td.expressionwidget.shared.exception.ExpressionParserException;
import org.gcube.portlets.user.td.expressionwidget.shared.expression.C_MultivaluedExpression;
import org.gcube.portlets.user.td.expressionwidget.shared.model.composite.aggregation.C_Avg;
import org.gcube.portlets.user.td.expressionwidget.shared.model.composite.aggregation.C_Count;
import org.gcube.portlets.user.td.expressionwidget.shared.model.composite.aggregation.C_Max;
import org.gcube.portlets.user.td.expressionwidget.shared.model.composite.aggregation.C_Min;
import org.gcube.portlets.user.td.expressionwidget.shared.model.composite.aggregation.C_ST_Extent;
import org.gcube.portlets.user.td.expressionwidget.shared.model.composite.aggregation.C_Sum;
import org.gcube.portlets.user.td.expressionwidget.shared.model.composite.arithmetic.C_Addition;
import org.gcube.portlets.user.td.expressionwidget.shared.model.composite.arithmetic.C_Division;
import org.gcube.portlets.user.td.expressionwidget.shared.model.composite.arithmetic.C_Modulus;
import org.gcube.portlets.user.td.expressionwidget.shared.model.composite.arithmetic.C_Multiplication;
import org.gcube.portlets.user.td.expressionwidget.shared.model.composite.arithmetic.C_Subtraction;
import org.gcube.portlets.user.td.expressionwidget.shared.model.composite.comparable.C_Equals;
import org.gcube.portlets.user.td.expressionwidget.shared.model.composite.comparable.C_GreaterOrEquals;
import org.gcube.portlets.user.td.expressionwidget.shared.model.composite.comparable.C_GreaterThan;
import org.gcube.portlets.user.td.expressionwidget.shared.model.composite.comparable.C_LessOrEquals;
import org.gcube.portlets.user.td.expressionwidget.shared.model.composite.comparable.C_LessThan;
import org.gcube.portlets.user.td.expressionwidget.shared.model.composite.comparable.C_NotEquals;
import org.gcube.portlets.user.td.expressionwidget.shared.model.composite.comparable.C_NotGreater;
import org.gcube.portlets.user.td.expressionwidget.shared.model.composite.comparable.C_NotLess;
import org.gcube.portlets.user.td.expressionwidget.shared.model.composite.functions.C_Cast;
import org.gcube.portlets.user.td.expressionwidget.shared.model.composite.text.C_Concat;
import org.gcube.portlets.user.td.expressionwidget.shared.model.composite.text.C_Levenshtein;
import org.gcube.portlets.user.td.expressionwidget.shared.model.composite.text.C_Lower;
import org.gcube.portlets.user.td.expressionwidget.shared.model.composite.text.C_MD5;
import org.gcube.portlets.user.td.expressionwidget.shared.model.composite.text.C_Similarity;
import org.gcube.portlets.user.td.expressionwidget.shared.model.composite.text.C_Soundex;
import org.gcube.portlets.user.td.expressionwidget.shared.model.composite.text.C_SubstringByIndex;
import org.gcube.portlets.user.td.expressionwidget.shared.model.composite.text.C_SubstringByRegex;
import org.gcube.portlets.user.td.expressionwidget.shared.model.composite.text.C_SubstringPosition;
import org.gcube.portlets.user.td.expressionwidget.shared.model.composite.text.C_TextBeginsWith;
import org.gcube.portlets.user.td.expressionwidget.shared.model.composite.text.C_TextContains;
import org.gcube.portlets.user.td.expressionwidget.shared.model.composite.text.C_TextEndsWith;
import org.gcube.portlets.user.td.expressionwidget.shared.model.composite.text.C_TextMatchSQLRegexp;
import org.gcube.portlets.user.td.expressionwidget.shared.model.composite.text.C_TextReplaceMatchingRegex;
import org.gcube.portlets.user.td.expressionwidget.shared.model.composite.text.C_Trim;
import org.gcube.portlets.user.td.expressionwidget.shared.model.composite.text.C_Upper;
import org.gcube.portlets.user.td.expressionwidget.shared.model.leaf.C_ColumnReference;
import org.gcube.portlets.user.td.expressionwidget.shared.model.leaf.C_ColumnReferencePlaceholder;
import org.gcube.portlets.user.td.expressionwidget.shared.model.leaf.C_ConstantList;
import org.gcube.portlets.user.td.expressionwidget.shared.model.leaf.TD_Value;
import org.gcube.portlets.user.td.expressionwidget.shared.model.logical.C_And;
import org.gcube.portlets.user.td.expressionwidget.shared.model.logical.C_Between;
import org.gcube.portlets.user.td.expressionwidget.shared.model.logical.C_IsNotNull;
import org.gcube.portlets.user.td.expressionwidget.shared.model.logical.C_IsNull;
import org.gcube.portlets.user.td.expressionwidget.shared.model.logical.C_Not;
import org.gcube.portlets.user.td.expressionwidget.shared.model.logical.C_Or;
import org.gcube.portlets.user.td.expressionwidget.shared.model.logical.C_ValueIsIn;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.expression.C_Expression;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnDataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class C_ExpressionParser {
	private static Logger logger = LoggerFactory.getLogger(C_ExpressionParser.class);

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * 
	 * @param exp
	 *            Expression
	 * @return Client Expression
	 * @throws ExpressionParserException
	 *             Exception
	 */
	public C_Expression parse(Expression exp) throws ExpressionParserException {
		logger.debug("Parse: " + exp);
		C_Expression ex = null;

		if (exp == null) {
			return ex;
		}

		if (exp instanceof Addition) {
			return getAddition(exp);
		}

		if (exp instanceof Division) {
			return getDivision(exp);
		}

		if (exp instanceof Exponentiation) {
			// TODO
			return ex;
		}

		if (exp instanceof Equals) {
			return getEquals(exp);
		}

		if (exp instanceof GreaterOrEquals) {
			return getGreaterOrEquals(exp);
		}

		if (exp instanceof GreaterThan) {
			return getGreaterThan(exp);
		}

		if (exp instanceof LessOrEquals) {
			return getLessOrEquals(exp);
		}

		if (exp instanceof LessThan) {
			return getLessThan(exp);
		}

		if (exp instanceof Modulus) {
			return getModulus(exp);
		}

		if (exp instanceof Multiplication) {
			return getMultiplication(exp);
		}

		if (exp instanceof NotEquals) {
			return getNotEquals(exp);
		}

		if (exp instanceof NotGreater) {
			return getNotGreater(exp);
		}

		if (exp instanceof NotLess) {
			return getNotLess(exp);
		}

		if (exp instanceof Subtraction) {
			return getSubtraction(exp);
		}

		if (exp instanceof TextBeginsWith) {
			return getTextBeginWith(exp);
		}

		if (exp instanceof TextContains) {
			return getTextContains(exp);
		}

		if (exp instanceof TextEndsWith) {
			return getTextEndWith(exp);
		}

		if (exp instanceof TextMatchSQLRegexp) {
			return getTextMatchSQLRegexp(exp);
		}

		if (exp instanceof TextMatchPosixRegexp) {
			// TODO
			return ex;
		}

		if (exp instanceof TextReplaceMatchingRegex) {
			return getTextReplaceMatchingRegex(exp);
		}

		if (exp instanceof SubstringByRegex) {
			return getSubstringByRegex(exp);
		}

		if (exp instanceof SubstringByIndex) {
			return getSubstringByIndex(exp);
		}

		if (exp instanceof SubstringPosition) {
			return getSubstringPosition(exp);
		}

		if (exp instanceof RepeatText) {
			// TODO
			return ex;
		}

		if (exp instanceof Trim) {
			return getTrim(exp);
		}

		if (exp instanceof Length) {
			// TODO
			return ex;
		}

		if (exp instanceof Upper) {
			return getUpper(exp);
		}

		if (exp instanceof Lower) {
			return getLower(exp);
		}

		if (exp instanceof Concat) {
			return getConcat(exp);
		}

		if (exp instanceof MD5) {
			return getMD5(exp);
		}

		if (exp instanceof Soundex) {
			return getSoundex(exp);
		}

		if (exp instanceof Similarity) {
			return getSimilarity(exp);
		}

		if (exp instanceof Levenshtein) {
			return getLevenshtein(exp);
		}

		if (exp instanceof ValueIsIn) {
			return getValueIsIn(exp);
		}

		if (exp instanceof ExternalReferenceExpression) {
			// TODO
			return ex;
		}

		if (exp instanceof Between) {
			return getBetween(exp);
		}

		// Multiple
		if (exp instanceof And) {
			return getAnd(exp);
		}

		if (exp instanceof Or) {
			return getOr(exp);
		}

		if (exp instanceof Case) {
			// TODO
			return ex;
		}

		// Unary
		if (exp instanceof IsNotNull) {
			return getIsNotNull(exp);
		}

		if (exp instanceof IsNull) {
			return getIsNull(exp);
		}

		if (exp instanceof Not) {
			return getNot(exp);
		}

		// Leaf
		if (exp instanceof ColumnReference) {
			return getColumnReference(exp);
		}

		if (exp instanceof ConstantList) {
			return getConstantList(exp);
		}

		// TypedColumnReference.class,
		if (exp instanceof ColumnReferencePlaceholder) {
			return getColumnReferencePlaceholder(exp);
		}

		// TDTypeValue
		if (exp instanceof TDBoolean) {
			return getExpressionValue((TDBoolean) exp);
		}

		if (exp instanceof TDDate) {
			return getExpressionValue((TDDate) exp);
		}

		if (exp instanceof TDInteger) {
			return getExpressionValue((TDInteger) exp);
		}

		if (exp instanceof TDNumeric) {
			return getExpressionValue((TDNumeric) exp);
		}

		if (exp instanceof TDText) {
			return getExpressionValue((TDText) exp);
		}

		if (exp instanceof TDGeometry) {
			return getExpressionValue((TDGeometry) exp);
		}

		if (exp instanceof Cast) {
			return getCast(exp);
		}

		// Aggregation
		if (exp instanceof Avg) {
			return getAvg(exp);
		}

		if (exp instanceof Count) {
			return getCount(exp);
		}

		if (exp instanceof Max) {
			return getMax(exp);
		}

		if (exp instanceof Min) {
			return getMin(exp);
		}

		if (exp instanceof Sum) {
			return getSum(exp);
		}

		if (exp instanceof ST_Extent) {
			return getSTExtent(exp);
		}

		ex = new C_Expression();

		return ex;

	}

	public C_MultivaluedExpression parseMultivalued(Expression exp) throws ExpressionParserException {

		if (exp instanceof ConstantList) {
			return getConstantList((ConstantList) exp);
		}

		if (exp instanceof ExternalReferenceExpression) {
			return null;
		}

		return null;
	}

	private C_Expression getValueIsIn(Expression exp) throws ExpressionParserException {
		ValueIsIn v = (ValueIsIn) exp;
		C_MultivaluedExpression multivalued = parseMultivalued(v.getRightArgument());
		C_ValueIsIn valueIsIn = new C_ValueIsIn(parse(v.getLeftArgument()), multivalued);
		return valueIsIn;
	}

	private C_Expression getOr(Expression exp) throws ExpressionParserException {
		Or o = (Or) exp;
		List<Expression> listExp = o.getArguments();
		List<C_Expression> listCExp = new ArrayList<C_Expression>();
		C_Expression ex;
		for (Expression serviceExp : listExp) {
			ex = parse(serviceExp);
			listCExp.add(ex);
		}
		C_Or or = new C_Or(listCExp);
		return or;
	}

	private C_Expression getNot(Expression exp) throws ExpressionParserException {
		Not n = (Not) exp;
		C_Not not = new C_Not(parse(n.getArgument()));
		return not;
	}

	private C_Expression getIsNull(Expression exp) throws ExpressionParserException {
		IsNull nullIs = (IsNull) exp;
		C_IsNull isNull = new C_IsNull(parse(nullIs.getArgument()));
		return isNull;
	}

	private C_Expression getIsNotNull(Expression exp) throws ExpressionParserException {
		IsNotNull nullNotIs = (IsNotNull) exp;
		C_IsNotNull isNotNull = new C_IsNotNull(parse(nullNotIs.getArgument()));
		return isNotNull;
	}

	private C_Expression getBetween(Expression exp) throws ExpressionParserException {
		Between bet = (Between) exp;
		C_Between between = new C_Between(parse(bet.getLeftArgument()), parse(bet.getMinRangeArgument()),
				parse(bet.getMaxRangeArgument()));
		return between;
	}

	private C_Expression getAnd(Expression exp) throws ExpressionParserException {
		And o = (And) exp;
		List<Expression> listExp = o.getArguments();
		List<C_Expression> listCExp = new ArrayList<C_Expression>();
		C_Expression ex;
		for (Expression serviceExp : listExp) {
			ex = parse(serviceExp);
			listCExp.add(ex);
		}
		C_And and = new C_And(listCExp);
		return and;
	}

	private C_Expression getTextMatchSQLRegexp(Expression exp) throws ExpressionParserException {
		TextMatchSQLRegexp reg = (TextMatchSQLRegexp) exp;
		C_TextMatchSQLRegexp regExp = new C_TextMatchSQLRegexp(parse(reg.getLeftArgument()),
				parse(reg.getRightArgument()));
		return regExp;

	}

	private C_Expression getTextEndWith(Expression exp) throws ExpressionParserException {
		TextEndsWith textEnd = (TextEndsWith) exp;
		C_TextEndsWith textEndWith = new C_TextEndsWith(parse(textEnd.getLeftArgument()),
				parse(textEnd.getRightArgument()));
		return textEndWith;
	}

	private C_Expression getTextContains(Expression exp) throws ExpressionParserException {
		TextContains textContains = (TextContains) exp;
		C_TextContains textCont = new C_TextContains(parse(textContains.getLeftArgument()),
				parse(textContains.getRightArgument()));
		return textCont;
	}

	private C_Expression getTextBeginWith(Expression exp) throws ExpressionParserException {
		TextBeginsWith textB = (TextBeginsWith) exp;
		C_TextBeginsWith textBegins = new C_TextBeginsWith(parse(textB.getLeftArgument()),
				parse(textB.getRightArgument()));
		return textBegins;
	}

	private C_Expression getNotLess(Expression exp) throws ExpressionParserException {
		NotLess notL = (NotLess) exp;
		C_NotLess notLess = new C_NotLess(parse(notL.getLeftArgument()), parse(notL.getRightArgument()));
		return notLess;
	}

	private C_Expression getNotGreater(Expression exp) throws ExpressionParserException {
		NotGreater notG = (NotGreater) exp;
		C_NotGreater notGreater = new C_NotGreater(parse(notG.getLeftArgument()), parse(notG.getRightArgument()));
		return notGreater;
	}

	private C_Expression getNotEquals(Expression exp) throws ExpressionParserException {
		NotEquals notE = (NotEquals) exp;
		C_NotEquals notEquals = new C_NotEquals(parse(notE.getLeftArgument()), parse(notE.getRightArgument()));
		return notEquals;
	}

	private C_Expression getLessThan(Expression exp) throws ExpressionParserException {
		LessThan lessT = (LessThan) exp;
		C_LessThan lessThan = new C_LessThan(parse(lessT.getLeftArgument()), parse(lessT.getRightArgument()));
		return lessThan;
	}

	private C_Expression getLessOrEquals(Expression exp) throws ExpressionParserException {
		LessOrEquals lessOrE = (LessOrEquals) exp;
		C_LessOrEquals lessOrEquals = new C_LessOrEquals(parse(lessOrE.getLeftArgument()),
				parse(lessOrE.getRightArgument()));
		return lessOrEquals;
	}

	private C_Expression getGreaterThan(Expression exp) throws ExpressionParserException {
		GreaterThan greaterThan = (GreaterThan) exp;
		C_GreaterThan greater = new C_GreaterThan(parse(greaterThan.getLeftArgument()),
				parse(greaterThan.getRightArgument()));
		return greater;

	}

	private C_Expression getGreaterOrEquals(Expression exp) throws ExpressionParserException {
		GreaterOrEquals greaterOrEq = (GreaterOrEquals) exp;
		C_GreaterOrEquals greaterOrEquals = new C_GreaterOrEquals(parse(greaterOrEq.getLeftArgument()),
				parse(greaterOrEq.getRightArgument()));
		return greaterOrEquals;

	}

	private TD_Value getExpressionValue(TDTypeValue value) throws ExpressionParserException {
		try {
			if (value instanceof TDBoolean) {
				TDBoolean tdboolean = (TDBoolean) value;
				return new TD_Value(ColumnDataType.Boolean, String.valueOf(tdboolean.getValue()));
			}

			if (value instanceof TDDate) {
				TDDate tddate = (TDDate) value;
				return new TD_Value(ColumnDataType.Date, sdf.format(tddate.getValue()));
			}

			if (value instanceof TDGeometry) {
				TDGeometry tdgeometry = (TDGeometry) value;
				return new TD_Value(ColumnDataType.Geometry, tdgeometry.getValue());
			}

			if (value instanceof TDInteger) {
				TDInteger tdinteger = (TDInteger) value;
				return new TD_Value(ColumnDataType.Integer, String.valueOf(tdinteger.getValue()));
			}

			if (value instanceof TDNumeric) {
				TDNumeric tdnumeric = (TDNumeric) value;
				return new TD_Value(ColumnDataType.Numeric, String.valueOf(tdnumeric.getValue()));
			}

			if (value instanceof TDText) {
				TDText tdtext = (TDText) value;
				return new TD_Value(ColumnDataType.Text, String.valueOf(tdtext.getValue()));
			}

		} catch (Throwable e) {
			logger.error("type error parsing value " + value + " " + e.getLocalizedMessage());
		}
		return null;
	}

	private ColumnDataType mapColumnDataType(DataType columnDataType) {
		if (columnDataType instanceof IntegerType) {
			return ColumnDataType.Integer;
		} else {
			if (columnDataType instanceof NumericType) {
				return ColumnDataType.Numeric;
			} else {
				if (columnDataType instanceof BooleanType) {
					return ColumnDataType.Boolean;
				} else {
					if (columnDataType instanceof GeometryType) {
						return ColumnDataType.Geometry;
					} else {
						if (columnDataType instanceof TextType) {
							return ColumnDataType.Text;
						} else {
							if (columnDataType instanceof DateType) {
								return ColumnDataType.Date;
							} else {
								return null;
							}
						}
					}
				}
			}
		}
	}

	private C_Expression getConstantList(Expression exp) throws ExpressionParserException {
		List<TD_Value> l = new ArrayList<TD_Value>();
		ConstantList c = (ConstantList) exp;
		List<TDTypeValue> arguments = c.getArguments();
		TD_Value tdv;
		for (TDTypeValue value : arguments) {
			tdv = getExpressionValue(value);
			l.add(tdv);
		}
		C_ConstantList constList = new C_ConstantList(l);
		return constList;
	}

	private C_MultivaluedExpression getConstantList(ConstantList c) throws ExpressionParserException {
		List<TD_Value> l = new ArrayList<TD_Value>();
		List<TDTypeValue> arguments = c.getArguments();
		TD_Value tdv;
		for (TDTypeValue value : arguments) {
			tdv = getExpressionValue(value);
			l.add(tdv);
		}
		C_ConstantList constList = new C_ConstantList(l);
		return constList;
	}

	private C_Expression getColumnReferencePlaceholder(Expression exp) {
		ColumnReferencePlaceholder c = (ColumnReferencePlaceholder) exp;
		C_ColumnReferencePlaceholder col = new C_ColumnReferencePlaceholder(mapColumnDataType(c.getDatatype()),
				c.getId(), c.getLabel());

		return col;
	}

	private C_Expression getColumnReference(Expression exp) {
		ColumnReference c = (ColumnReference) exp;
		String tableId = String.valueOf(c.getTableId().getValue());
		String columnId = c.getColumnId().getValue();
		ColumnDataType columnDataType = mapColumnDataType(c.getType());
		TRId trId = new TRId();
		trId.setTableId(tableId);
		trId.setViewTable(false);
		ColumnData column = new ColumnData();
		column.setId(columnId);
		column.setColumnId(columnId);
		column.setTrId(trId);
		column.setDataTypeName(columnDataType.toString());
		column.setLabel("Unknow");
		C_ColumnReference ref = new C_ColumnReference(column);
		return ref;
	}

	private C_Expression getUpper(Expression exp) throws ExpressionParserException {
		Upper e = (Upper) exp;
		C_Expression arg = parse(e.getArgument());
		C_Upper upper = new C_Upper(arg);
		return upper;
	}

	private C_Expression getLower(Expression exp) throws ExpressionParserException {
		Lower e = (Lower) exp;
		C_Expression arg = parse(e.getArgument());
		C_Lower lower = new C_Lower(arg);
		return lower;
	}

	private C_Expression getTrim(Expression exp) throws ExpressionParserException {
		Trim e = (Trim) exp;
		C_Expression arg = parse(e.getArgument());
		C_Trim trim = new C_Trim(arg);
		return trim;
	}

	private C_Expression getMD5(Expression exp) throws ExpressionParserException {
		MD5 e = (MD5) exp;
		C_Expression arg = parse(e.getArgument());
		C_MD5 md5 = new C_MD5(arg);
		return md5;
	}

	private C_Expression getSoundex(Expression exp) throws ExpressionParserException {
		Soundex e = (Soundex) exp;
		C_Expression arg = parse(e.getArgument());
		C_Soundex soundex = new C_Soundex(arg);
		return soundex;
	}

	private C_Expression getLevenshtein(Expression exp) throws ExpressionParserException {
		Levenshtein e = (Levenshtein) exp;
		C_Expression left = parse(e.getLeftArgument());
		C_Expression right = parse(e.getRightArgument());

		C_Levenshtein levenshtein = new C_Levenshtein(left, right);
		return levenshtein;
	}

	private C_Expression getSimilarity(Expression exp) throws ExpressionParserException {
		Similarity e = (Similarity) exp;
		C_Expression left = parse(e.getLeftArgument());
		C_Expression right = parse(e.getRightArgument());

		C_Similarity similarity = new C_Similarity(left, right);
		return similarity;
	}

	private C_Expression getEquals(Expression exp) throws ExpressionParserException {
		Equals e = (Equals) exp;
		C_Expression left = parse(e.getLeftArgument());
		C_Expression right = parse(e.getRightArgument());
		C_Equals eq = new C_Equals(left, right);
		return eq;
	}

	private C_Expression getConcat(Expression exp) throws ExpressionParserException {
		Concat concat = (Concat) exp;
		C_Concat conc = new C_Concat(parse(concat.getLeftArgument()), parse(concat.getRightArgument()));
		return conc;
	}

	private C_Expression getSubstringByIndex(Expression exp) throws ExpressionParserException {
		SubstringByIndex subByIndex = (SubstringByIndex) exp;
		C_SubstringByIndex sByIndex = new C_SubstringByIndex(parse(subByIndex.getSourceString()),
				parse(subByIndex.getFromIndex()), parse(subByIndex.getToIndex()));
		return sByIndex;
	}

	private C_Expression getSubstringByRegex(Expression exp) throws ExpressionParserException {
		SubstringByRegex subByRegex = (SubstringByRegex) exp;
		C_SubstringByRegex sByRegex = new C_SubstringByRegex(parse(subByRegex.getLeftArgument()),
				parse(subByRegex.getRightArgument()));
		return sByRegex;
	}

	private C_Expression getSubstringPosition(Expression exp) throws ExpressionParserException {
		SubstringPosition subPosition = (SubstringPosition) exp;
		C_SubstringPosition sPosition = new C_SubstringPosition(parse(subPosition.getLeftArgument()),
				parse(subPosition.getRightArgument()));
		return sPosition;
	}

	private C_Expression getTextReplaceMatchingRegex(Expression exp) throws ExpressionParserException {
		TextReplaceMatchingRegex textReplaceMatchingRegex = (TextReplaceMatchingRegex) exp;
		TD_Value replacing = getExpressionValue(textReplaceMatchingRegex.getReplacingValue());
		TD_Value regexp = getExpressionValue(textReplaceMatchingRegex.getRegexp());
		C_Expression toCheckText = parse(textReplaceMatchingRegex.getToCheckText());

		C_TextReplaceMatchingRegex textRepRegex = new C_TextReplaceMatchingRegex(toCheckText, regexp, replacing);
		return textRepRegex;
	}

	private C_Expression getAvg(Expression exp) throws ExpressionParserException {
		Avg avg = (Avg) exp;
		C_Avg av = new C_Avg(parse(avg.getArgument()));
		return av;
	}

	private C_Expression getCount(Expression exp) throws ExpressionParserException {
		Count count = (Count) exp;
		C_Count cnt = new C_Count(parse(count.getArgument()));
		return cnt;
	}

	private C_Expression getMax(Expression exp) throws ExpressionParserException {
		Max max = (Max) exp;
		C_Max ma = new C_Max(parse(max.getArgument()));
		return ma;
	}

	private C_Expression getMin(Expression exp) throws ExpressionParserException {
		Min min = (Min) exp;
		C_Min mi = new C_Min(parse(min.getArgument()));
		return mi;
	}

	private C_Expression getSTExtent(Expression exp) throws ExpressionParserException {
		ST_Extent stExtent = (ST_Extent) exp;
		C_ST_Extent stEx = new C_ST_Extent(parse(stExtent.getArgument()));
		return stEx;
	}

	private C_Expression getSum(Expression exp) throws ExpressionParserException {
		Sum sum = (Sum) exp;
		C_Sum sm = new C_Sum(parse(sum.getArgument()));
		return sm;
	}

	private C_Expression getCast(Expression exp) throws ExpressionParserException {
		Cast castExp = (Cast) exp;
		C_Cast sm = new C_Cast(parse(castExp.getArgument()), mapColumnDataType(castExp.getCastToType()));
		return sm;
	}

	private C_Expression getAddition(Expression exp) throws ExpressionParserException {
		Addition addition = (Addition) exp;
		C_Addition add = new C_Addition(parse(addition.getLeftArgument()), parse(addition.getRightArgument()));
		return add;
	}

	private C_Expression getSubtraction(Expression exp) throws ExpressionParserException {
		Subtraction subtraction = (Subtraction) exp;
		C_Subtraction sub = new C_Subtraction(parse(subtraction.getLeftArgument()),
				parse(subtraction.getRightArgument()));
		return sub;
	}

	private C_Expression getModulus(Expression exp) throws ExpressionParserException {
		Modulus modulus = (Modulus) exp;
		C_Modulus modu = new C_Modulus(parse(modulus.getLeftArgument()), parse(modulus.getRightArgument()));
		return modu;
	}

	private C_Expression getMultiplication(Expression exp) throws ExpressionParserException {
		Multiplication multiplication = (Multiplication) exp;
		C_Multiplication multi = new C_Multiplication(parse(multiplication.getLeftArgument()),
				parse(multiplication.getRightArgument()));
		return multi;
	}

	private C_Expression getDivision(Expression exp) throws ExpressionParserException {
		Division division = (Division) exp;
		C_Division divi = new C_Division(parse(division.getLeftArgument()), parse(division.getRightArgument()));
		return divi;
	}

	/**
	 * 
	 * @param exp
	 *            Client expression
	 * @return Expression 
	 * @throws ExpressionParserException
	 *             Exception
	 */
	public Expression parse(C_Expression exp) throws ExpressionParserException {
		logger.debug("Parse: " + exp);
		Expression ex = null;

		switch (exp.getId()) {
		case "ColumnReferencePlaceholder":
			ex = getColumnReferencePlaceholder(exp);
			break;
		case "ConstantList":
			ex = getConstantlist(exp);
			break;
		case "ColumnReference":
			ex = getColumnReference(exp);
			break;
		case "TD_Value":
			TD_Value value = (TD_Value) exp;
			ex = getExpressionValue(value);
			break;
		case "Equals":
			ex = getEquals(exp);
			break;
		case "GreaterOrEquals":
			ex = getGreaterOrEquals(exp);
			break;
		case "GreaterThan":
			ex = getGreaterThan(exp);
			break;
		case "LessOrEquals":
			ex = getLessOrEquals(exp);
			break;
		case "LessThan":
			ex = getLessThan(exp);
			break;
		case "NotEquals":
			ex = getNotEquals(exp);
			break;
		case "NotGreater":
			ex = getNotGreater(exp);
			break;
		case "NotLess":
			ex = getNotLess(exp);
			break;
		case "TextBeginsWith":
			ex = getTextBeginWith(exp);
			break;
		case "TextContains":
			ex = getTextContains(exp);
			break;
		case "TextEndsWith":
			ex = getTextEndWith(exp);
			break;
		case "TextMatchSQLRegexp":
			ex = getTextMatchSQLRegexp(exp);
			break;
		case "And":
			ex = getAnd(exp);
			break;
		case "Between":
			ex = getBetween(exp);
			break;
		case "IsNotNull":
			ex = getIsNotNull(exp);
			break;
		case "IsNull":
			ex = getIsNull(exp);
			break;
		case "Not":
			ex = getNot(exp);
			break;
		case "Or":
			ex = getOr(exp);
			break;
		case "ValueIsIn":
			ex = getValueIsIn(exp);
			break;
		case "Concat":
			ex = getConcat(exp);
			break;
		case "SubstringByIndex":
			ex = getSubstringByIndex(exp);
			break;
		case "SubstringByRegex":
			ex = getSubstringByRegex(exp);
			break;
		case "SubstringPosition":
			ex = getSubstringPosition(exp);
			break;
		case "TextReplaceMatchingRegex":
			ex = getTextReplaceMatchingRegex(exp);
			break;
		case "Avg":
			ex = getAvg(exp);
			break;
		case "Count":
			ex = getCount(exp);
			break;
		case "Max":
			ex = getMax(exp);
			break;
		case "Min":
			ex = getMin(exp);
			break;
		case "Sum":
			ex = getSum(exp);
			break;
		case "ST_Extent":
			ex = getSTExtent(exp);
			break;
		case "Cast":
			ex = getCast(exp);
			break;
		case "Addition":
			ex = getAddition(exp);
			break;
		case "Subtraction":
			ex = getSubtraction(exp);
			break;
		case "Modulus":
			ex = getModulus(exp);
			break;
		case "Multiplication":
			ex = getMultiplication(exp);
			break;
		case "Division":
			ex = getDivision(exp);
			break;
		case "Upper":
			ex = getUpper(exp);
			break;
		case "Lower":
			ex = getLower(exp);
			break;
		case "Trim":
			ex = getTrim(exp);
			break;
		case "MD5":
			ex = getMD5(exp);
			break;
		case "Soundex":
			ex = getSoundex(exp);
			break;
		case "Levenshtein":
			ex = getLevenshtein(exp);
			break;
		case "Similarity":
			ex = getSimilarity(exp);
			break;
		default:
			break;
		}

		return ex;
	}

	public MultivaluedExpression parseMultivalued(C_MultivaluedExpression exp) throws ExpressionParserException {
		MultivaluedExpression ex = null;
		switch (exp.getIdMulti()) {
		case "ConstantList":
			ex = getConstantlist(exp);
			break;
		case "ExternalReferenceExpression":
			break;
		default:
			break;
		}
		return ex;
	}

	private Expression getValueIsIn(C_Expression exp) throws ExpressionParserException {
		C_ValueIsIn v = (C_ValueIsIn) exp;
		MultivaluedExpression multivalued = parseMultivalued(v.getRightArgument());
		ValueIsIn valueIsIn = new ValueIsIn(parse(v.getLeftArgument()), multivalued);
		return valueIsIn;
	}

	private Expression getOr(C_Expression exp) throws ExpressionParserException {
		C_Or o = (C_Or) exp;
		List<C_Expression> listCExp = o.getArguments();
		List<Expression> listExp = new ArrayList<Expression>();
		Expression ex;
		for (C_Expression cexp : listCExp) {
			ex = parse(cexp);
			listExp.add(ex);
		}
		Or or = new Or(listExp);
		return or;
	}

	private Expression getNot(C_Expression exp) throws ExpressionParserException {
		C_Not n = (C_Not) exp;
		Not not = new Not(parse(n.getArgument()));
		return not;
	}

	private Expression getIsNull(C_Expression exp) throws ExpressionParserException {
		C_IsNull nullIs = (C_IsNull) exp;
		IsNull isNull = new IsNull(parse(nullIs.getArgument()));
		return isNull;
	}

	private Expression getIsNotNull(C_Expression exp) throws ExpressionParserException {
		C_IsNotNull nullNotIs = (C_IsNotNull) exp;
		IsNotNull isNotNull = new IsNotNull(parse(nullNotIs.getArgument()));
		return isNotNull;
	}

	private Expression getBetween(C_Expression exp) throws ExpressionParserException {
		C_Between bet = (C_Between) exp;
		Between between = new Between(parse(bet.getLeftArgument()), parse(bet.getMinRangeArgument()),
				parse(bet.getMaxRangeArgument()));

		return between;
	}

	private Expression getAnd(C_Expression exp) throws ExpressionParserException {
		C_And o = (C_And) exp;
		List<C_Expression> listCExp = o.getArguments();
		List<Expression> listExp = new ArrayList<Expression>();
		Expression ex;
		for (C_Expression cexp : listCExp) {
			ex = parse(cexp);
			listExp.add(ex);
		}
		And and = new And(listExp);
		return and;
	}

	private Expression getTextMatchSQLRegexp(C_Expression exp) throws ExpressionParserException {
		C_TextMatchSQLRegexp reg = (C_TextMatchSQLRegexp) exp;
		TextMatchSQLRegexp regExp = new TextMatchSQLRegexp(parse(reg.getLeftArgument()), parse(reg.getRightArgument()));
		return regExp;

	}

	private Expression getTextEndWith(C_Expression exp) throws ExpressionParserException {
		C_TextEndsWith textEnd = (C_TextEndsWith) exp;
		TextEndsWith textEndWith = new TextEndsWith(parse(textEnd.getLeftArgument()),
				parse(textEnd.getRightArgument()));
		return textEndWith;
	}

	private Expression getTextContains(C_Expression exp) throws ExpressionParserException {
		C_TextContains textContains = (C_TextContains) exp;
		TextContains textCont = new TextContains(parse(textContains.getLeftArgument()),
				parse(textContains.getRightArgument()));
		return textCont;
	}

	private Expression getTextBeginWith(C_Expression exp) throws ExpressionParserException {
		C_TextBeginsWith textB = (C_TextBeginsWith) exp;
		TextBeginsWith textBegins = new TextBeginsWith(parse(textB.getLeftArgument()), parse(textB.getRightArgument()));
		return textBegins;
	}

	private Expression getNotLess(C_Expression exp) throws ExpressionParserException {
		C_NotLess notL = (C_NotLess) exp;
		NotLess notLess = new NotLess(parse(notL.getLeftArgument()), parse(notL.getRightArgument()));
		return notLess;
	}

	private Expression getNotGreater(C_Expression exp) throws ExpressionParserException {
		C_NotGreater notG = (C_NotGreater) exp;
		NotGreater notGreater = new NotGreater(parse(notG.getLeftArgument()), parse(notG.getRightArgument()));
		return notGreater;
	}

	private Expression getNotEquals(C_Expression exp) throws ExpressionParserException {
		C_NotEquals notE = (C_NotEquals) exp;
		NotEquals notEquals = new NotEquals(parse(notE.getLeftArgument()), parse(notE.getRightArgument()));
		return notEquals;
	}

	private Expression getLessThan(C_Expression exp) throws ExpressionParserException {
		C_LessThan lessT = (C_LessThan) exp;
		LessThan lessThan = new LessThan(parse(lessT.getLeftArgument()), parse(lessT.getRightArgument()));
		return lessThan;
	}

	private Expression getLessOrEquals(C_Expression exp) throws ExpressionParserException {
		C_LessOrEquals lessOrE = (C_LessOrEquals) exp;
		LessOrEquals lessOrEquals = new LessOrEquals(parse(lessOrE.getLeftArgument()),
				parse(lessOrE.getRightArgument()));
		return lessOrEquals;
	}

	private Expression getGreaterThan(C_Expression exp) throws ExpressionParserException {
		C_GreaterThan greaterThan = (C_GreaterThan) exp;
		GreaterThan greater = new GreaterThan(parse(greaterThan.getLeftArgument()),
				parse(greaterThan.getRightArgument()));
		return greater;

	}

	private Expression getGreaterOrEquals(C_Expression exp) throws ExpressionParserException {
		C_GreaterOrEquals greaterOrEq = (C_GreaterOrEquals) exp;
		GreaterOrEquals greaterOrEquals = new GreaterOrEquals(parse(greaterOrEq.getLeftArgument()),
				parse(greaterOrEq.getRightArgument()));
		return greaterOrEquals;

	}

	private TDTypeValue getExpressionValue(TD_Value value) throws ExpressionParserException {

		TDTypeValue ex = null;
		try {
			switch (value.getValueType()) {
			case Boolean:
				ex = new TDBoolean(Boolean.valueOf(value.getValue()));
				break;
			case Date:
				Date d = null;
				try {
					d = sdf.parse(value.getValue());
				} catch (ParseException e) {
					logger.error("Unparseable using " + sdf);
					throw new ExpressionParserException(value.getValue() + " is not valid Date type");
				}
				ex = new TDDate(d);
				break;
			case Geometry:
				if (TDGeometry.validateGeometry(value.getValue())) {
					ex = new TDGeometry(value.getValue());
				} else {
					throw new ExpressionParserException(value.getValue() + " is not valid Geometry type");
				}
				break;
			case Integer:
				Integer vInteger;
				try {
					vInteger = Integer.valueOf(value.getValue());
				} catch (NumberFormatException e) {
					throw new ExpressionParserException(value.getValue() + " is not valid Integer type");
				}
				ex = new TDInteger(vInteger);
				break;
			case Numeric:
				Double vNumeric;
				try {
					vNumeric = Double.valueOf(value.getValue());
				} catch (NumberFormatException e) {
					throw new ExpressionParserException(value.getValue() + " is not valid Double type");
				}
				ex = new TDNumeric(vNumeric);
				break;
			case Text:
				if (value.getValue() == null) {
					ex = new TDText("");
				} else {
					ex = new TDText(value.getValue());
				}
				break;
			default:
				break;
			}
		} catch (Throwable e) {
			logger.error("type error parsing value " + value + " " + e.getLocalizedMessage());
		}
		return ex;
	}

	private DataType mapColumnDataType(ColumnDataType columnDataType) {
		if (columnDataType == ColumnDataType.Integer) {
			return new IntegerType();
		} else {
			if (columnDataType == ColumnDataType.Numeric) {
				return new NumericType();
			} else {
				if (columnDataType == ColumnDataType.Boolean) {
					return new BooleanType();
				} else {
					if (columnDataType == ColumnDataType.Geometry) {
						return new GeometryType();
					} else {
						if (columnDataType == ColumnDataType.Text) {
							return new TextType();
						} else {
							if (columnDataType == ColumnDataType.Date) {
								return new DateType();
							} else {
								return null;
							}
						}
					}
				}
			}
		}
	}

	private Expression getConstantlist(C_Expression exp) throws ExpressionParserException {
		List<TDTypeValue> l = new ArrayList<TDTypeValue>();
		C_ConstantList c = (C_ConstantList) exp;
		List<TD_Value> arguments = c.getArguments();
		TDTypeValue tdv;
		for (TD_Value value : arguments) {
			tdv = getExpressionValue(value);
			l.add(tdv);
		}
		ConstantList constList = new ConstantList(l);
		return constList;
	}

	private MultivaluedExpression getConstantlist(C_MultivaluedExpression exp) throws ExpressionParserException {
		List<TDTypeValue> l = new ArrayList<TDTypeValue>();
		C_ConstantList c = (C_ConstantList) exp;
		List<TD_Value> arguments = c.getArguments();
		TDTypeValue tdv;
		for (TD_Value value : arguments) {
			tdv = getExpressionValue(value);
			l.add(tdv);
		}
		ConstantList constList = new ConstantList(l);
		return constList;
	}

	private Expression getColumnReferencePlaceholder(C_Expression exp) {
		C_ColumnReferencePlaceholder c = (C_ColumnReferencePlaceholder) exp;
		ColumnReferencePlaceholder col = new ColumnReferencePlaceholder(mapColumnDataType(c.getDataType()),
				c.getColumnId(), c.getLabel());
		return col;
	}

	private Expression getColumnReference(C_Expression exp) {
		C_ColumnReference c = (C_ColumnReference) exp;
		String tableIdS = null;
		if (c.getColumn().getTrId().isViewTable()) {
			tableIdS = c.getColumn().getTrId().getReferenceTargetTableId();
		} else {
			tableIdS = c.getColumn().getTrId().getTableId();
		}
		TableId tableId = new TableId(Long.valueOf(tableIdS));
		ColumnLocalId columnId = new ColumnLocalId(c.getColumn().getColumnId());

		ColumnReference ref = new ColumnReference(tableId, columnId,
				mapColumnDataType(ColumnDataType.getColumnDataTypeFromId(c.getColumn().getDataTypeName())));
		return ref;
	}

	private Expression getUpper(C_Expression exp) throws ExpressionParserException {
		C_Upper e = (C_Upper) exp;
		Expression arg = parse(e.getArgument());
		Upper upper = new Upper(arg);
		return upper;
	}

	private Expression getLower(C_Expression exp) throws ExpressionParserException {
		C_Lower e = (C_Lower) exp;
		Expression arg = parse(e.getArgument());
		Lower lower = new Lower(arg);
		return lower;
	}

	private Expression getTrim(C_Expression exp) throws ExpressionParserException {
		C_Trim e = (C_Trim) exp;
		Expression arg = parse(e.getArgument());
		Trim trim = new Trim(arg);
		return trim;
	}

	private Expression getMD5(C_Expression exp) throws ExpressionParserException {
		C_MD5 e = (C_MD5) exp;
		Expression arg = parse(e.getArgument());
		MD5 md5 = new MD5(arg);
		return md5;
	}

	private Expression getSoundex(C_Expression exp) throws ExpressionParserException {
		C_Soundex e = (C_Soundex) exp;
		Expression arg = parse(e.getArgument());
		Soundex soundex = new Soundex(arg);
		return soundex;
	}

	private Expression getLevenshtein(C_Expression exp) throws ExpressionParserException {
		C_Levenshtein e = (C_Levenshtein) exp;
		Expression left = parse(e.getLeftArgument());
		Expression right = parse(e.getRightArgument());

		Levenshtein levenshtein = new Levenshtein(left, right);
		return levenshtein;
	}

	private Expression getSimilarity(C_Expression exp) throws ExpressionParserException {
		C_Similarity e = (C_Similarity) exp;
		Expression left = parse(e.getLeftArgument());
		Expression right = parse(e.getRightArgument());

		Similarity similarity = new Similarity(left, right);
		return similarity;
	}

	private Expression getEquals(C_Expression exp) throws ExpressionParserException {
		C_Equals e = (C_Equals) exp;
		Expression left = parse(e.getLeftArgument());
		Expression right = parse(e.getRightArgument());
		Equals eq = new Equals(left, right);
		return eq;
	}

	private Expression getConcat(C_Expression exp) throws ExpressionParserException {
		C_Concat concat = (C_Concat) exp;
		Concat conc = new Concat(parse(concat.getLeftArgument()), parse(concat.getRightArgument()));
		return conc;
	}

	private Expression getSubstringByIndex(C_Expression exp) throws ExpressionParserException {
		C_SubstringByIndex subByIndex = (C_SubstringByIndex) exp;
		SubstringByIndex sByIndex = new SubstringByIndex(parse(subByIndex.getSourceString()),
				parse(subByIndex.getFromIndex()), parse(subByIndex.getToIndex()));
		return sByIndex;
	}

	private Expression getSubstringByRegex(C_Expression exp) throws ExpressionParserException {
		C_SubstringByRegex subByRegex = (C_SubstringByRegex) exp;
		SubstringByRegex sByRegex = new SubstringByRegex(parse(subByRegex.getSourceString()),
				parse(subByRegex.getRegex()));
		return sByRegex;
	}

	private Expression getSubstringPosition(C_Expression exp) throws ExpressionParserException {
		C_SubstringPosition subPosition = (C_SubstringPosition) exp;
		SubstringPosition sPosition = new SubstringPosition(parse(subPosition.getLeftArgument()),
				parse(subPosition.getRightArgument()));
		return sPosition;
	}

	private Expression getTextReplaceMatchingRegex(C_Expression exp) throws ExpressionParserException {
		C_TextReplaceMatchingRegex textReplaceMatchingRegex = (C_TextReplaceMatchingRegex) exp;
		TDText tdRegexp = new TDText(textReplaceMatchingRegex.getRegexp().getValue());

		String replacement = textReplaceMatchingRegex.getReplacing().getValue();
		if (replacement == null) {
			replacement = "";
		}
		TDText tdReplacing = new TDText(replacement);

		TextReplaceMatchingRegex textRepRegex = new TextReplaceMatchingRegex(
				parse(textReplaceMatchingRegex.getToCheckText()), tdRegexp, tdReplacing);
		return textRepRegex;
	}

	private Expression getAvg(C_Expression exp) throws ExpressionParserException {
		C_Avg avg = (C_Avg) exp;
		Avg av = new Avg(parse(avg.getArgument()));
		return av;
	}

	private Expression getCount(C_Expression exp) throws ExpressionParserException {
		C_Count count = (C_Count) exp;
		Count cnt = new Count(parse(count.getArgument()));
		return cnt;
	}

	private Expression getMax(C_Expression exp) throws ExpressionParserException {
		C_Max max = (C_Max) exp;
		Max ma = new Max(parse(max.getArgument()));
		return ma;
	}

	private Expression getMin(C_Expression exp) throws ExpressionParserException {
		C_Min min = (C_Min) exp;
		Min mi = new Min(parse(min.getArgument()));
		return mi;
	}

	private Expression getSTExtent(C_Expression exp) throws ExpressionParserException {
		C_ST_Extent stExtent = (C_ST_Extent) exp;
		ST_Extent stEx = new ST_Extent(parse(stExtent.getArgument()));
		return stEx;
	}

	private Expression getSum(C_Expression exp) throws ExpressionParserException {
		C_Sum sum = (C_Sum) exp;
		Sum sm = new Sum(parse(sum.getArgument()));
		return sm;
	}

	private Expression getCast(C_Expression exp) throws ExpressionParserException {
		C_Cast castExp = (C_Cast) exp;
		Cast sm = new Cast(parse(castExp.getLeftArgument()), mapColumnDataType(castExp.getRightArgument()));
		return sm;
	}

	private Expression getAddition(C_Expression exp) throws ExpressionParserException {
		C_Addition addition = (C_Addition) exp;
		Addition add = new Addition(parse(addition.getLeftArgument()), parse(addition.getRightArgument()));
		return add;
	}

	private Expression getSubtraction(C_Expression exp) throws ExpressionParserException {
		C_Subtraction subtraction = (C_Subtraction) exp;
		Subtraction sub = new Subtraction(parse(subtraction.getLeftArgument()), parse(subtraction.getRightArgument()));
		return sub;
	}

	private Expression getModulus(C_Expression exp) throws ExpressionParserException {
		C_Modulus modulus = (C_Modulus) exp;
		Modulus modu = new Modulus(parse(modulus.getLeftArgument()), parse(modulus.getRightArgument()));
		return modu;
	}

	private Expression getMultiplication(C_Expression exp) throws ExpressionParserException {
		C_Multiplication multiplication = (C_Multiplication) exp;
		Multiplication multi = new Multiplication(parse(multiplication.getLeftArgument()),
				parse(multiplication.getRightArgument()));
		return multi;
	}

	private Expression getDivision(C_Expression exp) throws ExpressionParserException {
		C_Division division = (C_Division) exp;
		Division divi = new Division(parse(division.getLeftArgument()), parse(division.getRightArgument()));
		return divi;
	}

}
