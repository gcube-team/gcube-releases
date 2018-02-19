package org.gcube.portlets.user.td.expressionwidget.shared.condition;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.td.expressionwidget.client.multicolumn.ArgType;
import org.gcube.portlets.user.td.expressionwidget.client.threshold.Threshold;
import org.gcube.portlets.user.td.expressionwidget.client.threshold.ThresholdStore;
import org.gcube.portlets.user.td.expressionwidget.shared.exception.ConditionTypeMapException;
import org.gcube.portlets.user.td.expressionwidget.shared.model.C_OperatorType;
import org.gcube.portlets.user.td.expressionwidget.shared.model.composite.comparable.C_Equals;
import org.gcube.portlets.user.td.expressionwidget.shared.model.composite.comparable.C_GreaterOrEquals;
import org.gcube.portlets.user.td.expressionwidget.shared.model.composite.comparable.C_GreaterThan;
import org.gcube.portlets.user.td.expressionwidget.shared.model.composite.comparable.C_LessOrEquals;
import org.gcube.portlets.user.td.expressionwidget.shared.model.composite.comparable.C_LessThan;
import org.gcube.portlets.user.td.expressionwidget.shared.model.composite.comparable.C_NotEquals;
import org.gcube.portlets.user.td.expressionwidget.shared.model.composite.comparable.C_NotGreater;
import org.gcube.portlets.user.td.expressionwidget.shared.model.composite.comparable.C_NotLess;
import org.gcube.portlets.user.td.expressionwidget.shared.model.composite.text.C_Levenshtein;
import org.gcube.portlets.user.td.expressionwidget.shared.model.composite.text.C_Similarity;
import org.gcube.portlets.user.td.expressionwidget.shared.model.composite.text.C_Soundex;
import org.gcube.portlets.user.td.expressionwidget.shared.model.composite.text.C_TextBeginsWith;
import org.gcube.portlets.user.td.expressionwidget.shared.model.composite.text.C_TextContains;
import org.gcube.portlets.user.td.expressionwidget.shared.model.composite.text.C_TextEndsWith;
import org.gcube.portlets.user.td.expressionwidget.shared.model.composite.text.C_TextMatchSQLRegexp;
import org.gcube.portlets.user.td.expressionwidget.shared.model.leaf.C_ColumnReference;
import org.gcube.portlets.user.td.expressionwidget.shared.model.leaf.C_ColumnReferencePlaceholder;
import org.gcube.portlets.user.td.expressionwidget.shared.model.leaf.C_ConstantList;
import org.gcube.portlets.user.td.expressionwidget.shared.model.leaf.C_Leaf;
import org.gcube.portlets.user.td.expressionwidget.shared.model.leaf.TD_Value;
import org.gcube.portlets.user.td.expressionwidget.shared.model.logical.C_And;
import org.gcube.portlets.user.td.expressionwidget.shared.model.logical.C_Between;
import org.gcube.portlets.user.td.expressionwidget.shared.model.logical.C_IsNotNull;
import org.gcube.portlets.user.td.expressionwidget.shared.model.logical.C_IsNull;
import org.gcube.portlets.user.td.expressionwidget.shared.model.logical.C_Not;
import org.gcube.portlets.user.td.expressionwidget.shared.model.logical.C_Or;
import org.gcube.portlets.user.td.expressionwidget.shared.model.logical.C_ValueIsIn;
import org.gcube.portlets.user.td.widgetcommonevent.shared.expression.C_Expression;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnDataType;

import com.allen_sauer.gwt.log.client.Log;

/**
 * ConditionTypeMap creates a C_Expression usable client-side
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class ConditionOnMultiColumnTypeMap {

	public ConditionOnMultiColumnTypeMap() {

	}

	public C_Expression map(ColumnData column, C_OperatorType operatorType,
			ArgType firstArgType, ColumnData firstArgColumn, String firstArg,
			ArgType secondArgType, ColumnData secondArgColumn,
			String secondArg, Threshold threshold)
			throws ConditionTypeMapException {
		Log.debug("ConditionOnMultiColumnTypeMap: [" + column + ", "
				+ operatorType + ", " + firstArgType + ", " + firstArgColumn
				+ ", "+firstArg+" "+ secondArgType + ", " + secondArgColumn
				+ ", "+secondArg+", "+threshold+"]");
		C_Expression exp = null;
		if (column.getTrId() == null) {
			exp = mapPlaceHolder(column, operatorType, firstArgType,
					firstArgColumn, firstArg, secondArgType, secondArgColumn,
					secondArg, threshold);
		} else {
			exp = mapTypedColumnReference(column, operatorType, firstArgType,
					firstArgColumn, firstArg, secondArgType, secondArgColumn,
					secondArg, threshold);
		}
		return exp;
	}

	public C_Expression mapPlaceHolder(ColumnData column,
			C_OperatorType operatorType, ArgType firstArgType,
			ColumnData firstArgColumn, String firstArg, ArgType secondArgType,
			ColumnData secondArgColumn, String secondArg, Threshold threshold)
			throws ConditionTypeMapException {
		Log.debug("ConditionOnMultiColumnTypeMap Place Holder");
		C_Expression exp = null;
		Log.debug("Column Data Type Name:" + column.getDataTypeName());
		ColumnDataType dataType = ColumnDataType.getColumnDataTypeFromId(column
				.getDataTypeName());
		Log.debug("Data Type:" + dataType);
		C_ColumnReferencePlaceholder placeHolder = new C_ColumnReferencePlaceholder(
				dataType, column.getColumnId(), column.getLabel());
		Log.debug("placeHolder:" + placeHolder);

		// TD_Value fArg;
		// TD_Value sArg;
		// C_Range range;

		// List<TD_Value> arguments = new ArrayList<TD_Value>();
		// int separator;

		switch (operatorType) {
		case ADDITION:
			break;
		case ALL:
			break;
		case AND:
			break;
		case ANY:
			break;
		case BEGINS_WITH:
			exp = placeHolderBeginsWith(column, firstArgType, firstArgColumn,
					firstArg, dataType, placeHolder);
			break;
		case BETWEEN:
			exp = placeHolderBetween(column, firstArgType, firstArgColumn,
					firstArg, secondArgType, secondArgColumn, secondArg,
					dataType, placeHolder);
			break;
		case CONTAINS:
			exp = placeHolderContains(column, firstArgType, firstArgColumn,
					firstArg, dataType, placeHolder);
			break;
		case DIVISION:
			break;
		case ENDS_WITH:
			exp = placeHolderEndsWith(column, firstArgType, firstArgColumn,
					firstArg, dataType, placeHolder);
			break;
		case EQUALS:
			exp = placeHolderEquals(column, firstArgType, firstArgColumn,
					firstArg, dataType, placeHolder);
			break;
		case EXISTS:
			// exp=new C_Exi(placeHolder,new C_ConstantList(arguments));
			break;
		case GREATER:
			exp = placeHolderGreater(column, firstArgType, firstArgColumn,
					firstArg, dataType, placeHolder);
			break;
		case GREATER_OR_EQUALS:
			exp = placeHolderGreaterOrEquals(column, firstArgType,
					firstArgColumn, firstArg, dataType, placeHolder);
			break;
		case IN:
			exp = placeHolderIn(column, firstArgType, firstArgColumn, firstArg,
					dataType, placeHolder);
			break;
		case IS_NOT_NULL:
			exp = new C_IsNotNull(placeHolder);
			break;
		case IS_NULL:
			exp = new C_IsNull(placeHolder);
			break;
		case LESSER:
			exp = placeHolderLesser(column, firstArgType, firstArgColumn,
					firstArg, dataType, placeHolder);
			break;
		case LESSER_OR_EQUALS:
			exp = placeHolderLesserOrEquals(column, firstArgType,
					firstArgColumn, firstArg, dataType, placeHolder);
			break;
		case LIKE:
			break;
		case MATCH_REGEX:
			exp = placeHolderMatchRegex(column, firstArgType, firstArgColumn,
					firstArg, dataType, placeHolder);
			break;
		case MODULUS:
			break;
		case MULTIPLICATION:
			break;
		case NOT:
			break;
		case NOT_BEGINS_WITH:
			exp = placeHolderBeginsWith(column, firstArgType, firstArgColumn,
					firstArg, dataType, placeHolder);
			exp = new C_Not(exp);
			break;
		case NOT_BETWEEN:
			exp = placeHolderBetween(column, firstArgType, firstArgColumn,
					firstArg, secondArgType, secondArgColumn, secondArg,
					dataType, placeHolder);
			exp = new C_Not(exp);
			break;
		case NOT_CONTAINS:
			exp = placeHolderContains(column, firstArgType, firstArgColumn,
					firstArg, dataType, placeHolder);
			exp = new C_Not(exp);
			break;
		case NOT_ENDS_WITH:
			exp = placeHolderEndsWith(column, firstArgType, firstArgColumn,
					firstArg, dataType, placeHolder);
			exp = new C_Not(exp);
			break;
		case NOT_EQUALS:
			exp = placeHolderNotEquals(column, firstArgType, firstArgColumn,
					firstArg, dataType, placeHolder);
			break;
		case NOT_GREATER:
			exp = placeHolderNotGreater(column, firstArgType, firstArgColumn,
					firstArg, dataType, placeHolder);
			break;
		case NOT_IN:
			exp = placeHolderIn(column, firstArgType, firstArgColumn, firstArg,
					dataType, placeHolder);
			exp = new C_Not(exp);
			break;
		case NOT_LESSER:
			exp = placeHolderNotLess(column, firstArgType, firstArgColumn,
					firstArg, dataType, placeHolder);
			break;
		case NOT_LIKE:
			break;
		case NOT_MATCH_REGEX:
			exp = placeHolderMatchRegex(column, firstArgType, firstArgColumn,
					firstArg, dataType, placeHolder);
			exp = new C_Not(exp);
			break;
		case OR:
			break;
		case SELECT_IN:
			break;
		case SUBTRACTION:
			break;
		case UNIQUE:
			break;
		case SOUNDEX:
			exp = placeHolderSoundex(column, firstArg, dataType, placeHolder);
			break;
		case LEVENSHTEIN:
			exp = placeHolderLevenshtein(column, firstArg, threshold, dataType,
					placeHolder);
			break;
		case SIMILARITY:
			exp = placeHolderSimilarity(column, firstArg, threshold, dataType,
					placeHolder);
			break;

		default:
			break;
		}
		
		Log.debug("CExpression: "+exp);
		return exp;
	}

	protected C_Expression placeHolderSimilarity(ColumnData column,
			String firstArg, Threshold threshold, ColumnDataType dataType,
			C_ColumnReferencePlaceholder placeHolder)
			throws ConditionTypeMapException {
		C_Expression exp;
		checkTypeArgument(column, firstArg);
		C_Similarity similarity = new C_Similarity(placeHolder, new TD_Value(
				dataType, firstArg));
		Threshold thresholdSim;

		if (threshold == null) {
			thresholdSim = ThresholdStore.defaultThresholdSimilarity();
		} else {
			thresholdSim = threshold;
		}
		exp = new C_GreaterOrEquals(similarity, new TD_Value(
				ColumnDataType.Numeric, thresholdSim.getLabel()));
		return exp;
	}

	protected C_Expression placeHolderLevenshtein(ColumnData column,
			String firstArg, Threshold threshold, ColumnDataType dataType,
			C_ColumnReferencePlaceholder placeHolder)
			throws ConditionTypeMapException {
		C_Expression exp;
		checkTypeArgument(column, firstArg);
		C_Levenshtein levenshstein = new C_Levenshtein(placeHolder,
				new TD_Value(dataType, firstArg));
		Threshold thresholdLev;

		if (threshold == null) {
			thresholdLev = ThresholdStore.defaultThresholdLevenshtein();
		} else {
			thresholdLev = threshold;
		}
		exp = new C_LessOrEquals(levenshstein, new TD_Value(
				ColumnDataType.Integer, thresholdLev.getIntegerValue()
						.toString()));
		return exp;
	}

	protected C_Expression placeHolderSoundex(ColumnData column,
			String firstArg, ColumnDataType dataType,
			C_ColumnReferencePlaceholder placeHolder)
			throws ConditionTypeMapException {
		C_Expression exp;
		checkTypeArgument(column, firstArg);
		C_Soundex soundexPlaceHolder = new C_Soundex(placeHolder);
		C_Soundex soundexValue = new C_Soundex(new TD_Value(dataType, firstArg));
		exp = new C_Equals(soundexPlaceHolder, soundexValue);
		return exp;
	}

	protected C_Expression placeHolderNotLess(ColumnData column,
			ArgType firstArgType, ColumnData firstArgColumn, String firstArg,
			ColumnDataType dataType, C_ColumnReferencePlaceholder placeHolder)
			throws ConditionTypeMapException {
		C_Expression exp = null;
		switch (firstArgType) {
		case COLUMN:
			ColumnDataType firstArgDataType = ColumnDataType
					.getColumnDataTypeFromId(firstArgColumn.getDataTypeName());
			C_ColumnReferencePlaceholder firstArgPlaceHolder = new C_ColumnReferencePlaceholder(
					firstArgDataType, firstArgColumn.getColumnId(), firstArgColumn.getLabel());
			exp = new C_NotLess(placeHolder, firstArgPlaceHolder);
			break;
		case VALUE:
			checkTypeArgument(column, firstArg);
			TD_Value fArg = new TD_Value(dataType, firstArg);
			exp = new C_NotLess(placeHolder, fArg);
			break;
		default:
			throw new ConditionTypeMapException(
					"No valid first argument type selected!");

		}

		return exp;
	}

	protected C_Expression placeHolderNotGreater(ColumnData column,
			ArgType firstArgType, ColumnData firstArgColumn, String firstArg,
			ColumnDataType dataType, C_ColumnReferencePlaceholder placeHolder)
			throws ConditionTypeMapException {
		C_Expression exp = null;
		switch (firstArgType) {
		case COLUMN:
			ColumnDataType firstArgDataType = ColumnDataType
					.getColumnDataTypeFromId(firstArgColumn.getDataTypeName());
			C_ColumnReferencePlaceholder firstArgPlaceHolder = new C_ColumnReferencePlaceholder(
					firstArgDataType, firstArgColumn.getColumnId(), firstArgColumn.getLabel());
			exp = new C_NotGreater(placeHolder, firstArgPlaceHolder);
			break;
		case VALUE:
			checkTypeArgument(column, firstArg);
			TD_Value fArg = new TD_Value(dataType, firstArg);
			exp = new C_NotGreater(placeHolder, fArg);
			break;
		default:
			throw new ConditionTypeMapException(
					"No valid first argument type selected!");

		}
		return exp;
	}

	protected C_Expression placeHolderNotEquals(ColumnData column,
			ArgType firstArgType, ColumnData firstArgColumn, String firstArg,
			ColumnDataType dataType, C_ColumnReferencePlaceholder placeHolder)
			throws ConditionTypeMapException {
		C_Expression exp = null;
		switch (firstArgType) {
		case COLUMN:
			ColumnDataType firstArgDataType = ColumnDataType
					.getColumnDataTypeFromId(firstArgColumn.getDataTypeName());
			C_ColumnReferencePlaceholder firstArgPlaceHolder = new C_ColumnReferencePlaceholder(
					firstArgDataType, firstArgColumn.getColumnId(), firstArgColumn.getLabel());
			exp = new C_NotEquals(placeHolder, firstArgPlaceHolder);
			break;
		case VALUE:
			checkTypeArgument(column, firstArg);
			TD_Value fArg = new TD_Value(dataType, firstArg);
			exp = new C_NotEquals(placeHolder, fArg);
			break;
		default:
			throw new ConditionTypeMapException(
					"No valid first argument type selected!");

		}

		return exp;
	}

	protected C_Expression placeHolderMatchRegex(ColumnData column,
			ArgType firstArgType, ColumnData firstArgColumn, String firstArg,
			ColumnDataType dataType, C_ColumnReferencePlaceholder placeHolder)
			throws ConditionTypeMapException {
		C_Expression exp = null;
		switch (firstArgType) {
		case COLUMN:
			ColumnDataType firstArgDataType = ColumnDataType
					.getColumnDataTypeFromId(firstArgColumn.getDataTypeName());
			C_ColumnReferencePlaceholder firstArgPlaceHolder = new C_ColumnReferencePlaceholder(
					firstArgDataType, firstArgColumn.getColumnId(), firstArgColumn.getLabel());
			exp = new C_TextMatchSQLRegexp(placeHolder, firstArgPlaceHolder);
			break;
		case VALUE:
			checkTypeArgument(column, firstArg);
			TD_Value fArg = new TD_Value(dataType, firstArg);
			exp = new C_TextMatchSQLRegexp(placeHolder, fArg);
			break;
		default:
			throw new ConditionTypeMapException(
					"No valid first argument type selected!");

		}

		return exp;
	}

	protected C_Expression placeHolderLesserOrEquals(ColumnData column,
			ArgType firstArgType, ColumnData firstArgColumn, String firstArg,
			ColumnDataType dataType, C_ColumnReferencePlaceholder placeHolder)
			throws ConditionTypeMapException {
		C_Expression exp = null;
		switch (firstArgType) {
		case COLUMN:
			ColumnDataType firstArgDataType = ColumnDataType
					.getColumnDataTypeFromId(firstArgColumn.getDataTypeName());
			C_ColumnReferencePlaceholder firstArgPlaceHolder = new C_ColumnReferencePlaceholder(
					firstArgDataType, firstArgColumn.getColumnId(), firstArgColumn.getLabel());
			exp = new C_LessOrEquals(placeHolder, firstArgPlaceHolder);
			break;
		case VALUE:
			checkTypeArgument(column, firstArg);
			TD_Value fArg = new TD_Value(dataType, firstArg);
			exp = new C_LessOrEquals(placeHolder, fArg);
			break;
		default:
			throw new ConditionTypeMapException(
					"No valid first argument type selected!");

		}

		return exp;
	}

	protected C_Expression placeHolderLesser(ColumnData column,
			ArgType firstArgType, ColumnData firstArgColumn, String firstArg,
			ColumnDataType dataType, C_ColumnReferencePlaceholder placeHolder)
			throws ConditionTypeMapException {
		C_Expression exp = null;
		switch (firstArgType) {
		case COLUMN:
			ColumnDataType firstArgDataType = ColumnDataType
					.getColumnDataTypeFromId(firstArgColumn.getDataTypeName());
			C_ColumnReferencePlaceholder firstArgPlaceHolder = new C_ColumnReferencePlaceholder(
					firstArgDataType, firstArgColumn.getColumnId(), firstArgColumn.getLabel());
			exp = new C_LessThan(placeHolder, firstArgPlaceHolder);
			break;
		case VALUE:
			checkTypeArgument(column, firstArg);
			TD_Value fArg = new TD_Value(dataType, firstArg);
			exp = new C_LessThan(placeHolder, fArg);
			break;
		default:
			throw new ConditionTypeMapException(
					"No valid first argument type selected!");

		}

		return exp;
	}

	protected C_Expression placeHolderIn(ColumnData column,
			ArgType firstArgType, ColumnData firstArgColumn, String firstArg,
			ColumnDataType dataType, C_ColumnReferencePlaceholder placeHolder)
			throws ConditionTypeMapException {
		ArrayList<TD_Value> arguments = new ArrayList<TD_Value>();
		int separator;

		C_Expression exp = null;
		switch (firstArgType) {
		case COLUMN:
			throw new ConditionTypeMapException(
					"Only on value is supported for Is In!");
			/*
			 * ColumnDataType firstArgDataType = ColumnDataType
			 * .getColumnDataTypeFromId(firstArgColumn.getDataTypeName());
			 * C_ColumnReferencePlaceholder firstArgPlaceHolder = new
			 * C_ColumnReferencePlaceholder( firstArgDataType,
			 * firstArgColumn.getColumnId(), firstArgColumn.getLabel()); exp = new
			 * C_ValueIsIn(placeHolder,firstArgPlaceHolder);
			 */
			
		case VALUE:
			separator = firstArg.indexOf(",");
			if (separator == -1 || firstArg.length() == 1) {
				checkTypeArgument(column, firstArg);
				arguments.add(new TD_Value(dataType, firstArg));
			} else {
				String arg;
				boolean end = false;
				while (!end) {
					arg = firstArg.substring(0, separator);
					checkTypeArgument(column, arg);
					arguments.add(new TD_Value(dataType, arg));
					separator++;
					if (separator < firstArg.length()) {
						firstArg = firstArg.substring(separator,
								firstArg.length());
						separator = firstArg.indexOf(",");
						if (separator == -1) {
							checkTypeArgument(column, firstArg);
							arguments.add(new TD_Value(dataType, firstArg));
							end = true;
						}
					} else {
						end = true;
					}
				}
			}
			exp = new C_ValueIsIn(placeHolder, new C_ConstantList(arguments));
			break;
		default:
			throw new ConditionTypeMapException(
					"No valid first argument type selected!");

		}

		return exp;
	}

	protected C_Expression placeHolderGreaterOrEquals(ColumnData column,
			ArgType firstArgType, ColumnData firstArgColumn, String firstArg,
			ColumnDataType dataType, C_ColumnReferencePlaceholder placeHolder)
			throws ConditionTypeMapException {
		C_Expression exp = null;
		switch (firstArgType) {
		case COLUMN:
			ColumnDataType firstArgDataType = ColumnDataType
					.getColumnDataTypeFromId(firstArgColumn.getDataTypeName());
			C_ColumnReferencePlaceholder firstArgPlaceHolder = new C_ColumnReferencePlaceholder(
					firstArgDataType, firstArgColumn.getColumnId(), firstArgColumn.getLabel());
			exp = new C_GreaterOrEquals(placeHolder, firstArgPlaceHolder);
			break;
		case VALUE:
			checkTypeArgument(column, firstArg);
			TD_Value fArg = new TD_Value(dataType, firstArg);
			exp = new C_GreaterOrEquals(placeHolder, fArg);
			break;
		default:
			throw new ConditionTypeMapException(
					"No valid first argument type selected!");

		}

		return exp;
	}

	protected C_Expression placeHolderGreater(ColumnData column,
			ArgType firstArgType, ColumnData firstArgColumn, String firstArg,
			ColumnDataType dataType, C_ColumnReferencePlaceholder placeHolder)
			throws ConditionTypeMapException {
		C_Expression exp = null;
		switch (firstArgType) {
		case COLUMN:
			ColumnDataType firstArgDataType = ColumnDataType
					.getColumnDataTypeFromId(firstArgColumn.getDataTypeName());
			C_ColumnReferencePlaceholder firstArgPlaceHolder = new C_ColumnReferencePlaceholder(
					firstArgDataType, firstArgColumn.getColumnId(), firstArgColumn.getLabel());
			exp = new C_GreaterThan(placeHolder, firstArgPlaceHolder);
			break;
		case VALUE:
			checkTypeArgument(column, firstArg);
			TD_Value fArg = new TD_Value(dataType, firstArg);
			exp = new C_GreaterThan(placeHolder, fArg);
			break;
		default:
			throw new ConditionTypeMapException(
					"No valid first argument type selected!");

		}

		return exp;
	}

	protected C_Expression placeHolderEquals(ColumnData column,
			ArgType firstArgType, ColumnData firstArgColumn, String firstArg,
			ColumnDataType dataType, C_ColumnReferencePlaceholder placeHolder)
			throws ConditionTypeMapException {
		C_Expression exp = null;
		switch (firstArgType) {
		case COLUMN:
			ColumnDataType firstArgDataType = ColumnDataType
					.getColumnDataTypeFromId(firstArgColumn.getDataTypeName());
			C_ColumnReferencePlaceholder firstArgPlaceHolder = new C_ColumnReferencePlaceholder(
					firstArgDataType, firstArgColumn.getColumnId(), firstArgColumn.getLabel());
			exp = new C_Equals(placeHolder, firstArgPlaceHolder);
			break;
		case VALUE:
			checkTypeArgument(column, firstArg);
			TD_Value fArg = new TD_Value(dataType, firstArg);
			exp = new C_Equals(placeHolder, fArg);
			break;
		default:
			throw new ConditionTypeMapException(
					"No valid first argument type selected!");

		}

		return exp;
	}

	protected C_Expression placeHolderEndsWith(ColumnData column,
			ArgType firstArgType, ColumnData firstArgColumn, String firstArg,
			ColumnDataType dataType, C_ColumnReferencePlaceholder placeHolder)
			throws ConditionTypeMapException {
		C_Expression exp = null;

		switch (firstArgType) {
		case COLUMN:
			ColumnDataType firstArgDataType = ColumnDataType
					.getColumnDataTypeFromId(firstArgColumn.getDataTypeName());
			C_ColumnReferencePlaceholder firstArgPlaceHolder = new C_ColumnReferencePlaceholder(
					firstArgDataType, firstArgColumn.getColumnId(), firstArgColumn.getLabel());
			exp = new C_TextEndsWith(placeHolder, firstArgPlaceHolder);
			break;
		case VALUE:
			checkTypeArgument(column, firstArg);
			TD_Value fArg = new TD_Value(dataType, firstArg);
			exp = new C_TextEndsWith(placeHolder, fArg);
			break;
		default:
			throw new ConditionTypeMapException(
					"No valid first argument type selected!");

		}

		return exp;
	}

	protected C_Expression placeHolderContains(ColumnData column,
			ArgType firstArgType, ColumnData firstArgColumn, String firstArg,
			ColumnDataType dataType, C_ColumnReferencePlaceholder placeHolder)
			throws ConditionTypeMapException {
		C_Expression exp = null;

		switch (firstArgType) {
		case COLUMN:
			ColumnDataType firstArgDataType = ColumnDataType
					.getColumnDataTypeFromId(firstArgColumn.getDataTypeName());
			C_ColumnReferencePlaceholder firstArgPlaceHolder = new C_ColumnReferencePlaceholder(
					firstArgDataType, firstArgColumn.getColumnId(), firstArgColumn.getLabel());
			exp = new C_TextContains(placeHolder, firstArgPlaceHolder);
			break;
		case VALUE:
			checkTypeArgument(column, firstArg);
			TD_Value fArg = new TD_Value(dataType, firstArg);
			exp = new C_TextContains(placeHolder, fArg);

			break;
		default:
			throw new ConditionTypeMapException(
					"No valid first argument type selected!");

		}

		return exp;
	}

	protected C_Expression placeHolderBetween(ColumnData column,
			ArgType firstArgType, ColumnData firstArgColumn, String firstArg,
			ArgType secondArgType, ColumnData secondArgColumn,
			String secondArg, ColumnDataType dataType,
			C_ColumnReferencePlaceholder placeHolder)
			throws ConditionTypeMapException {
		C_Expression exp = null;
		C_Leaf fArg;
		C_Leaf sArg;

		switch (firstArgType) {
		case COLUMN:
			ColumnDataType firstArgDataType = ColumnDataType
					.getColumnDataTypeFromId(firstArgColumn.getDataTypeName());
			fArg = new C_ColumnReferencePlaceholder(firstArgDataType,
					firstArgColumn.getColumnId(), firstArgColumn.getLabel());
			break;
		case VALUE:
			if(firstArg==null|| firstArg.isEmpty()){
				throw new ConditionTypeMapException(
						"Fill all arguments!");
			}
			checkTypeArgument(column, firstArg);
			fArg = new TD_Value(dataType, firstArg);
			break;
		default:
			throw new ConditionTypeMapException(
					"No valid first argument type selected!");
		}
	

		switch (secondArgType) {
		case COLUMN:
			ColumnDataType secondArgDataType = ColumnDataType
					.getColumnDataTypeFromId(secondArgColumn.getDataTypeName());
			Log.debug("Second Arg Data Type:" + secondArgDataType);
			sArg = new C_ColumnReferencePlaceholder(secondArgDataType,
					secondArgColumn.getColumnId(), secondArgColumn.getLabel());
			break;
		case VALUE:
			if(secondArg==null|| secondArg.isEmpty()){
				throw new ConditionTypeMapException(
						"Fill all arguments!");
			}
			checkTypeArgument(column, secondArg);
			sArg = new TD_Value(dataType, secondArg);
			break;
		default:
			throw new ConditionTypeMapException(
					"No valid second argument type selected!");
		}

	
		exp = new C_Between(placeHolder, fArg, sArg);
		return exp;
	}

	protected C_Expression placeHolderBeginsWith(ColumnData column,
			ArgType firstArgType, ColumnData firstArgColumn, String firstArg,
			ColumnDataType dataType, C_ColumnReferencePlaceholder placeHolder)
			throws ConditionTypeMapException {
		C_Expression exp = null;

		switch (firstArgType) {
		case COLUMN:
			ColumnDataType firstArgDataType = ColumnDataType
					.getColumnDataTypeFromId(firstArgColumn.getDataTypeName());
			Log.debug("First Arg Data Type:" + firstArgDataType);
			C_ColumnReferencePlaceholder firstArgColumnPlaceHolder = new C_ColumnReferencePlaceholder(
					firstArgDataType, firstArgColumn.getColumnId(), firstArgColumn.getLabel());
			Log.debug("FirstArgColumnPlaceHolder:" + firstArgColumnPlaceHolder);
			exp = new C_TextBeginsWith(placeHolder, firstArgColumnPlaceHolder);
			break;
		case VALUE:
			checkTypeArgument(column, firstArg);
			TD_Value fArg = new TD_Value(dataType, firstArg);
			exp = new C_TextBeginsWith(placeHolder, fArg);
			break;
		default:
			throw new ConditionTypeMapException(
					"No valid first argument type selected!");
		}

		return exp;
	}

	public C_Expression mapTypedColumnReference(ColumnData column,
			C_OperatorType operatorType, ArgType firstArgType,
			ColumnData firstArgColumn, String firstArg, ArgType secondArgType,
			ColumnData secondArgColumn, String secondArg, Threshold threshold)
			throws ConditionTypeMapException {
		Log.debug("ConditionTypeMap Map Typed Column Reference");
		C_Expression exp = null;

		Log.debug("Column Data Type Name:" + column.getDataTypeName());
		ColumnDataType dataType = ColumnDataType.getColumnDataTypeFromId(column
				.getDataTypeName());
		Log.debug("Data Type:" + dataType);
		C_ColumnReference columnReference = new C_ColumnReference(
				column);
		Log.debug("Typed Column Reference:" + columnReference);

		switch (operatorType) {
		case ADDITION:
			break;
		case ALL:
			break;
		case AND:
			break;
		case ANY:
			break;
		case BEGINS_WITH:
			exp = columnReferenceBeginsWith(column, firstArgType,
					firstArgColumn, firstArg, dataType, columnReference);
			break;
		case BETWEEN:
			exp = columnReferenceBetween(column, firstArgType, firstArgColumn,
					firstArg, secondArgType, secondArgColumn, secondArg,
					dataType, columnReference);
			break;
		case CONTAINS:
			exp = columnReferenceContains(column, firstArgType, firstArgColumn,
					firstArg, dataType, columnReference);
			break;
		case DIVISION:
			break;
		case ENDS_WITH:
			exp = columnReferenceEndsWith(column, firstArgType, firstArgColumn,
					firstArg, dataType, columnReference);
			break;
		case EQUALS:
			exp = columnReferenceEquals(column, firstArgType, firstArgColumn,
					firstArg, dataType, columnReference);
			break;
		case EXISTS:
			// exp=new C_Exi(placeHolder,new C_ConstantList(arguments));
			break;
		case GREATER:
			exp = columnReferenceGreater(column, firstArgType, firstArgColumn,
					firstArg, dataType, columnReference);
			break;
		case GREATER_OR_EQUALS:
			exp = columnReferenceGreaterOrEquals(column, firstArgType,
					firstArgColumn, firstArg, dataType, columnReference);
			break;
		case IN:
			exp = columnReferenceIn(column, firstArgType, firstArgColumn,
					firstArg, dataType, columnReference);
			break;
		case IS_NOT_NULL:
			exp = new C_IsNotNull(columnReference);
			break;
		case IS_NULL:
			exp = new C_IsNull(columnReference);
			break;
		case LESSER:
			exp = columnReferenceLesser(column, firstArgType, firstArgColumn,
					firstArg, dataType, columnReference);
			break;
		case LESSER_OR_EQUALS:
			exp = columnReferenceLesserOrEquals(column, firstArgType,
					firstArgColumn, firstArg, dataType, columnReference);
			break;
		case LIKE:
			break;
		case MATCH_REGEX:
			exp = columnReferenceMatchRegex(column, firstArgType,
					firstArgColumn, firstArg, dataType, columnReference);
			break;
		case MODULUS:
			break;
		case MULTIPLICATION:
			break;
		case NOT:
			break;
		case NOT_BEGINS_WITH:
			exp = columnReferenceBeginsWith(column, firstArgType,
					firstArgColumn, firstArg, dataType, columnReference);
			exp = new C_Not(exp);
			break;
		case NOT_BETWEEN:
			exp = columnReferenceBetween(column, firstArgType, firstArgColumn,
					firstArg, secondArgType, secondArgColumn, secondArg,
					dataType, columnReference);
			exp = new C_Not(exp);
			break;
		case NOT_CONTAINS:
			exp = columnReferenceContains(column, firstArgType, firstArgColumn,
					firstArg, dataType, columnReference);
			exp = new C_Not(exp);
			break;
		case NOT_ENDS_WITH:
			exp = columnReferenceEndsWith(column, firstArgType, firstArgColumn,
					firstArg, dataType, columnReference);
			exp = new C_Not(exp);
			break;
		case NOT_EQUALS:
			exp = columnReferenceNotEquals(column, firstArgType,
					firstArgColumn, firstArg, dataType, columnReference);
			break;
		case NOT_GREATER:
			exp = columnReferenceNotGreater(column, firstArgType,
					firstArgColumn, firstArg, dataType, columnReference);
			break;
		case NOT_IN:
			exp = columnReferenceIn(column, firstArgType, firstArgColumn,
					firstArg, dataType, columnReference);
			exp = new C_Not(exp);
			break;
		case NOT_LESSER:
			exp = columnReferenceNotLesser(column, firstArgType,
					firstArgColumn, firstArg, dataType, columnReference);
			break;
		case NOT_LIKE:
			break;
		case NOT_MATCH_REGEX:
			exp = columnReferenceMatchRegex(column, firstArgType,
					firstArgColumn, firstArg, dataType, columnReference);
			exp = new C_Not(exp);
			break;
		case OR:
			break;
		case SELECT_IN:
			break;
		case SUBTRACTION:
			break;
		case UNIQUE:
			break;
		case SOUNDEX:
			exp = columnReferenceSoundex(column, firstArg, dataType,
					columnReference);
			break;
		case LEVENSHTEIN:
			exp = columnReferenceLevenshtein(column, firstArg, threshold,
					dataType, columnReference);
			break;
		case SIMILARITY:
			exp = columnReferenceSimilarity(column, firstArg, threshold,
					dataType, columnReference);
			break;
		default:
			break;
		}
		
		
		Log.debug("CExpression: "+exp);
		return exp;
	}

	protected C_Expression columnReferenceSimilarity(ColumnData column,
			String firstArg, Threshold threshold, ColumnDataType dataType,
			C_ColumnReference columnReference) throws ConditionTypeMapException {
		C_Expression exp;
		checkTypeArgument(column, firstArg);
		C_Similarity similarity = new C_Similarity(columnReference,
				new TD_Value(dataType, firstArg));
		Threshold thresholdSim;

		if (threshold == null) {
			thresholdSim = ThresholdStore.defaultThresholdSimilarity();
		} else {
			thresholdSim = threshold;
		}
		exp = new C_GreaterOrEquals(similarity, new TD_Value(
				ColumnDataType.Numeric, thresholdSim.getLabel()));
		return exp;
	}

	protected C_Expression columnReferenceLevenshtein(ColumnData column,
			String firstArg, Threshold threshold, ColumnDataType dataType,
			C_ColumnReference columnReference) throws ConditionTypeMapException {
		C_Expression exp;
		checkTypeArgument(column, firstArg);
		C_Levenshtein levenshstein = new C_Levenshtein(columnReference,
				new TD_Value(dataType, firstArg));
		Threshold thresholdLev;

		if (threshold == null) {
			thresholdLev = ThresholdStore.defaultThresholdLevenshtein();
		} else {
			thresholdLev = threshold;
		}
		exp = new C_LessOrEquals(levenshstein, new TD_Value(
				ColumnDataType.Integer, thresholdLev.getIntegerValue()
						.toString()));
		return exp;
	}

	protected C_Expression columnReferenceSoundex(ColumnData column,
			String firstArg, ColumnDataType dataType,
			C_ColumnReference columnReference) throws ConditionTypeMapException {
		C_Expression exp;
		checkTypeArgument(column, firstArg);
		C_Soundex soundexPlaceHolder = new C_Soundex(columnReference);
		C_Soundex soundexValue = new C_Soundex(new TD_Value(dataType, firstArg));
		exp = new C_Equals(soundexPlaceHolder, soundexValue);
		return exp;
	}

	protected C_Expression columnReferenceNotLesser(ColumnData column,
			ArgType firstArgType, ColumnData firstArgColumn, String firstArg,
			ColumnDataType dataType, C_ColumnReference columnReference)
			throws ConditionTypeMapException {
		C_Expression exp = null;

		switch (firstArgType) {
		case COLUMN:
			C_ColumnReference firstArgColumnReference = new C_ColumnReference(
					firstArgColumn);
			exp = new C_NotLess(columnReference, firstArgColumnReference);
			break;
		case VALUE:
			checkTypeArgument(column, firstArg);
			TD_Value fArg = new TD_Value(dataType, firstArg);
			exp = new C_NotLess(columnReference, fArg);

			break;
		default:
			throw new ConditionTypeMapException(
					"No valid first argument type selected!");
		}

		return exp;
	}

	protected C_Expression columnReferenceNotGreater(ColumnData column,
			ArgType firstArgType, ColumnData firstArgColumn, String firstArg,
			ColumnDataType dataType, C_ColumnReference columnReference)
			throws ConditionTypeMapException {
		C_Expression exp = null;

		switch (firstArgType) {
		case COLUMN:
			C_ColumnReference firstArgColumnReference = new C_ColumnReference(
					firstArgColumn);
			exp = new C_NotGreater(columnReference, firstArgColumnReference);
			break;
		case VALUE:
			checkTypeArgument(column, firstArg);
			TD_Value fArg = new TD_Value(dataType, firstArg);
			exp = new C_NotGreater(columnReference, fArg);

			break;
		default:
			throw new ConditionTypeMapException(
					"No valid first argument type selected!");
		}

		return exp;
	}

	protected C_Expression columnReferenceNotEquals(ColumnData column,
			ArgType firstArgType, ColumnData firstArgColumn, String firstArg,
			ColumnDataType dataType, C_ColumnReference columnReference)
			throws ConditionTypeMapException {
		C_Expression exp = null;

		switch (firstArgType) {
		case COLUMN:
			C_ColumnReference firstArgColumnReference = new C_ColumnReference(
					firstArgColumn);
			exp = new C_NotEquals(columnReference, firstArgColumnReference);
			break;
		case VALUE:
			checkTypeArgument(column, firstArg);
			TD_Value fArg = new TD_Value(dataType, firstArg);
			exp = new C_NotEquals(columnReference, fArg);

			break;
		default:
			throw new ConditionTypeMapException(
					"No valid first argument type selected!");
		}

		return exp;
	}

	protected C_Expression columnReferenceMatchRegex(ColumnData column,
			ArgType firstArgType, ColumnData firstArgColumn, String firstArg,
			ColumnDataType dataType, C_ColumnReference columnReference)
			throws ConditionTypeMapException {
		C_Expression exp = null;

		switch (firstArgType) {
		case COLUMN:
			C_ColumnReference firstArgColumnReference = new C_ColumnReference(
					firstArgColumn);
			exp = new C_TextMatchSQLRegexp(columnReference,
					firstArgColumnReference);
			break;
		case VALUE:
			checkTypeArgument(column, firstArg);
			TD_Value fArg = new TD_Value(dataType, firstArg);
			exp = new C_TextMatchSQLRegexp(columnReference, fArg);

			break;
		default:
			throw new ConditionTypeMapException(
					"No valid first argument type selected!");
		}

		return exp;
	}

	protected C_Expression columnReferenceLesserOrEquals(ColumnData column,
			ArgType firstArgType, ColumnData firstArgColumn, String firstArg,
			ColumnDataType dataType, C_ColumnReference columnReference)
			throws ConditionTypeMapException {
		C_Expression exp = null;

		switch (firstArgType) {
		case COLUMN:
			C_ColumnReference firstArgColumnReference = new C_ColumnReference(
					firstArgColumn);
			exp = new C_LessOrEquals(columnReference, firstArgColumnReference);
			break;
		case VALUE:
			checkTypeArgument(column, firstArg);
			TD_Value fArg = new TD_Value(dataType, firstArg);
			exp = new C_LessOrEquals(columnReference, fArg);

			break;
		default:
			throw new ConditionTypeMapException(
					"No valid first argument type selected!");
		}

		return exp;
	}

	protected C_Expression columnReferenceLesser(ColumnData column,
			ArgType firstArgType, ColumnData firstArgColumn, String firstArg,
			ColumnDataType dataType, C_ColumnReference columnReference)
			throws ConditionTypeMapException {
		C_Expression exp = null;

		switch (firstArgType) {
		case COLUMN:
			C_ColumnReference firstArgColumnReference = new C_ColumnReference(
					firstArgColumn);
			exp = new C_LessThan(columnReference, firstArgColumnReference);
			break;
		case VALUE:
			checkTypeArgument(column, firstArg);
			TD_Value fArg = new TD_Value(dataType, firstArg);
			exp = new C_LessThan(columnReference, fArg);

			break;
		default:
			throw new ConditionTypeMapException(
					"No valid first argument type selected!");
		}

		return exp;
	}

	protected C_Expression columnReferenceIn(ColumnData column,
			ArgType firstArgType, ColumnData firstArgColumn, String firstArg,
			ColumnDataType dataType, C_ColumnReference columnReference)
			throws ConditionTypeMapException {
		ArrayList<TD_Value> arguments = new ArrayList<TD_Value>();
		int separator;

		C_Expression exp = null;
		switch (firstArgType) {
		case COLUMN:
			throw new ConditionTypeMapException(
					"Only on value is supported for Is In!");
			/*
			 * ColumnDataType firstArgDataType = ColumnDataType
			 * .getColumnDataTypeFromId(firstArgColumn.getDataTypeName());
			 * C_ColumnReference firstArgColumnReference = new
			 * C_ColumnReference( firstArgColumn.getTrId(), firstArgDataType,
			 * firstArgColumn.getColumnId()); exp = new
			 * C_ValueIsIn(columnReference, firstArgColumnReference);
			 */
		case VALUE:
			separator = firstArg.indexOf(",");
			if (separator == -1 || firstArg.length() == 1) {
				checkTypeArgument(column, firstArg);
				arguments.add(new TD_Value(dataType, firstArg));
			} else {
				String arg;
				boolean end = false;
				while (!end) {
					arg = firstArg.substring(0, separator);
					checkTypeArgument(column, arg);
					arguments.add(new TD_Value(dataType, arg));
					separator++;
					if (separator < firstArg.length()) {
						firstArg = firstArg.substring(separator,
								firstArg.length());
						separator = firstArg.indexOf(",");
						if (separator == -1) {
							checkTypeArgument(column, firstArg);
							arguments.add(new TD_Value(dataType, firstArg));
							end = true;
						}
					} else {
						end = true;
					}
				}
			}

			exp = new C_ValueIsIn(columnReference,
					new C_ConstantList(arguments));

			break;
		default:
			throw new ConditionTypeMapException(
					"No valid first argument type selected!");

		}
		return exp;
	}

	protected C_Expression columnReferenceGreaterOrEquals(ColumnData column,
			ArgType firstArgType, ColumnData firstArgColumn, String firstArg,
			ColumnDataType dataType, C_ColumnReference columnReference)
			throws ConditionTypeMapException {
		C_Expression exp = null;

		switch (firstArgType) {
		case COLUMN:
			C_ColumnReference firstArgColumnReference = new C_ColumnReference(
					firstArgColumn);
			exp = new C_GreaterOrEquals(columnReference,
					firstArgColumnReference);
			break;
		case VALUE:
			checkTypeArgument(column, firstArg);
			TD_Value fArg = new TD_Value(dataType, firstArg);
			exp = new C_GreaterOrEquals(columnReference, fArg);

			break;
		default:
			throw new ConditionTypeMapException(
					"No valid first argument type selected!");
		}

		return exp;
	}

	protected C_Expression columnReferenceGreater(ColumnData column,
			ArgType firstArgType, ColumnData firstArgColumn, String firstArg,
			ColumnDataType dataType, C_ColumnReference columnReference)
			throws ConditionTypeMapException {
		C_Expression exp = null;

		switch (firstArgType) {
		case COLUMN:
			C_ColumnReference firstArgColumnReference = new C_ColumnReference(
					firstArgColumn);
			exp = new C_GreaterThan(columnReference, firstArgColumnReference);
			break;
		case VALUE:
			checkTypeArgument(column, firstArg);
			TD_Value fArg = new TD_Value(dataType, firstArg);
			exp = new C_GreaterThan(columnReference, fArg);

			break;
		default:
			throw new ConditionTypeMapException(
					"No valid first argument type selected!");
		}

		return exp;
	}

	protected C_Expression columnReferenceEquals(ColumnData column,
			ArgType firstArgType, ColumnData firstArgColumn, String firstArg,
			ColumnDataType dataType, C_ColumnReference columnReference)
			throws ConditionTypeMapException {
		C_Expression exp = null;

		switch (firstArgType) {
		case COLUMN:
			C_ColumnReference firstArgColumnReference = new C_ColumnReference(
					firstArgColumn);
			exp = new C_Equals(columnReference, firstArgColumnReference);
			break;
		case VALUE:
			checkTypeArgument(column, firstArg);
			TD_Value fArg = new TD_Value(dataType, firstArg);
			exp = new C_Equals(columnReference, fArg);

			break;
		default:
			throw new ConditionTypeMapException(
					"No valid first argument type selected!");
		}

		return exp;
	}

	protected C_Expression columnReferenceEndsWith(ColumnData column,
			ArgType firstArgType, ColumnData firstArgColumn, String firstArg,
			ColumnDataType dataType, C_ColumnReference columnReference)
			throws ConditionTypeMapException {
		C_Expression exp = null;

		switch (firstArgType) {
		case COLUMN:
			C_ColumnReference firstArgColumnReference = new C_ColumnReference(
					firstArgColumn);
			exp = new C_TextEndsWith(columnReference, firstArgColumnReference);
			break;
		case VALUE:
			checkTypeArgument(column, firstArg);
			TD_Value fArg = new TD_Value(dataType, firstArg);
			exp = new C_TextEndsWith(columnReference, fArg);

			break;
		default:
			throw new ConditionTypeMapException(
					"No valid first argument type selected!");
		}

		return exp;
	}

	protected C_Expression columnReferenceContains(ColumnData column,
			ArgType firstArgType, ColumnData firstArgColumn, String firstArg,
			ColumnDataType dataType, C_ColumnReference columnReference)
			throws ConditionTypeMapException {
		C_Expression exp = null;

		switch (firstArgType) {
		case COLUMN:
			C_ColumnReference firstArgColumnReference = new C_ColumnReference(
					firstArgColumn);
			exp = new C_TextContains(columnReference, firstArgColumnReference);
			break;
		case VALUE:
			checkTypeArgument(column, firstArg);
			TD_Value fArg = new TD_Value(dataType, firstArg);
			exp = new C_TextContains(columnReference, fArg);

			break;
		default:
			throw new ConditionTypeMapException(
					"No valid first argument type selected!");
		}

		return exp;
	}

	protected C_Expression columnReferenceBetween(ColumnData column,
			ArgType firstArgType, ColumnData firstArgColumn, String firstArg,
			ArgType secondArgType, ColumnData secondArgColumn,
			String secondArg, ColumnDataType dataType,
			C_ColumnReference columnReference) throws ConditionTypeMapException {
		C_Expression exp = null;
		C_Leaf fArg;
		C_Leaf sArg;
		
		switch (firstArgType) {
		case COLUMN:
			fArg = new C_ColumnReference(firstArgColumn);
			break;
		case VALUE:
			if(firstArg==null|| firstArg.isEmpty()){
				throw new ConditionTypeMapException(
						"Fill all arguments!");
			}
			checkTypeArgument(column, firstArg);
			fArg = new TD_Value(dataType, firstArg);
			break;
		default:
			throw new ConditionTypeMapException(
					"No valid first argument type selected!");
		}

		switch (secondArgType) {
		case COLUMN:
			sArg = new C_ColumnReference(secondArgColumn);
			break;
		case VALUE:
			if(secondArg==null|| secondArg.isEmpty()){
				throw new ConditionTypeMapException(
						"Fill all arguments!");
			}
			checkTypeArgument(column, secondArg);
			sArg = new TD_Value(dataType, secondArg);
			break;
		default:
			throw new ConditionTypeMapException(
					"No valid second argument type selected!");
		}

		exp = new C_Between(columnReference, fArg, sArg);
		return exp;
	}

	protected C_Expression columnReferenceBeginsWith(ColumnData column,
			ArgType firstArgType, ColumnData firstArgColumn, String firstArg,
			ColumnDataType dataType, C_ColumnReference columnReference)
			throws ConditionTypeMapException {
		C_Expression exp = null;

		switch (firstArgType) {
		case COLUMN:
			C_ColumnReference firstArgColumnReference = new C_ColumnReference(
					firstArgColumn);
			exp = new C_TextBeginsWith(columnReference, firstArgColumnReference);
			break;
		case VALUE:
			checkTypeArgument(column, firstArg);
			TD_Value fArg = new TD_Value(dataType, firstArg);
			exp = new C_TextBeginsWith(columnReference, fArg);

			break;
		default:
			throw new ConditionTypeMapException(
					"No valid first argument type selected!");
		}

		return exp;
	}

	public C_Expression createC_Or(List<C_Expression> arguments) {
		C_Or or = new C_Or(arguments);
		return or;
	}

	public C_Expression createC_And(List<C_Expression> arguments) {
		C_And or = new C_And(arguments);
		return or;
	}

	protected void checkTypeArgument(ColumnData column, String arg)
			throws ConditionTypeMapException {
		if (column.getDataTypeName().compareTo(ColumnDataType.Text.toString()) == 0) {
			if (arg == null) {
				arg = "";
			}
		} else {
			if (column.getDataTypeName().compareTo(
					ColumnDataType.Boolean.toString()) == 0) {
				if (arg == null) {
					throw new ConditionTypeMapException(
							"Insert a valid Boolean(ex: true, false)!");
				}
			} else {
				if (column.getDataTypeName().compareTo(
						ColumnDataType.Date.toString()) == 0) {

				} else {
					if (column.getDataTypeName().compareTo(
							ColumnDataType.Geometry.toString()) == 0) {

					} else {
						if (column.getDataTypeName().compareTo(
								ColumnDataType.Integer.toString()) == 0) {
							if (arg == null) {
								throw new ConditionTypeMapException(
										"Insert a valid Integer(ex: -1, 0, 1, 2)!");
							}

							try {
								Integer.parseInt(arg);
							} catch (NumberFormatException e) {
								throw new ConditionTypeMapException(
										arg
												+ " is not valid Integer(ex: -1, 0, 1, 2)!");
							}
						} else {
							if (column.getDataTypeName().compareTo(
									ColumnDataType.Numeric.toString()) == 0) {
								if (arg == null) {
									throw new ConditionTypeMapException(
											"Insert a valid Numeric(ex: -1.2, 0, 1, 2.4)!");
								}
								try {
									Double.parseDouble(arg);
								} catch (NumberFormatException e) {
									throw new ConditionTypeMapException(
											arg
													+ " is not valid Numeric(ex: -1.2, 0, 1, 2.4)!");
								}
							} else {

							}
						}
					}
				}
			}

		}
	}

}
