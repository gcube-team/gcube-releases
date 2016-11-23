package org.gcube.portlets.user.td.expressionwidget.shared.condition;

import java.util.ArrayList;
import java.util.List;

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
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class ConditionTypeMap {
	public ConditionTypeMap() {

	}

	public C_Expression map(ColumnData column, C_OperatorType operatorType,
			String firstArg, String secondArg, Threshold threshold) throws ConditionTypeMapException {
		Log.debug("ConditionTypeMap Map");
		C_Expression exp = null;
		new String();
		if (column.getTrId() == null) {
			exp = mapPlaceHolder(column, operatorType, firstArg, secondArg, threshold);
		} else {
			exp = mapTypedColumnReference(column, operatorType, firstArg,
					secondArg, threshold);
		}
		return exp;
	}

	public C_Expression mapPlaceHolder(ColumnData column,
			C_OperatorType operatorType, String firstArg, String secondArg, Threshold threshold)
			throws ConditionTypeMapException {
		Log.debug("ConditionTypeMap Map Place Holder");
		C_Expression exp = null;
		Log.debug("Column Data Type Name:" + column.getDataTypeName());
		ColumnDataType dataType = ColumnDataType
				.getColumnDataTypeFromId(column
				.getDataTypeName());
		Log.debug("Data Type:" + dataType);
		C_ColumnReferencePlaceholder placeHolder = new C_ColumnReferencePlaceholder(
				dataType, column.getColumnId(), column.getLabel());
		Log.debug("placeHolder:" + placeHolder);

		TD_Value fArg;
		TD_Value sArg;

		List<TD_Value> arguments = new ArrayList<TD_Value>();
		int separator;

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
			checkTypeArgument(column, firstArg);
			exp = new C_TextBeginsWith(placeHolder, new TD_Value(dataType,
					firstArg));
			break;
		case BETWEEN:
			checkTypeArgument(column, firstArg);
			checkTypeArgument(column, secondArg);
			fArg = new TD_Value(dataType, firstArg);
			sArg = new TD_Value(dataType, secondArg);
			exp = new C_Between(placeHolder, fArg, sArg);
			break;
		case CONTAINS:
			checkTypeArgument(column, firstArg);
			fArg = new TD_Value(dataType, firstArg);
			exp = new C_TextContains(placeHolder, fArg);
			break;
		case DIVISION:
			break;
		case ENDS_WITH:
			checkTypeArgument(column, firstArg);
			exp = new C_TextEndsWith(placeHolder, new TD_Value(dataType,
					firstArg));
			break;
		case EQUALS:
			checkTypeArgument(column, firstArg);
			exp = new C_Equals(placeHolder, new TD_Value(dataType, firstArg));
			break;
		case EXISTS:
			// exp=new C_Exi(placeHolder,new C_ConstantList(arguments));
			break;
		case GREATER:
			checkTypeArgument(column, firstArg);
			exp = new C_GreaterThan(placeHolder, new TD_Value(dataType,
					firstArg));
			break;
		case GREATER_OR_EQUALS:
			checkTypeArgument(column, firstArg);
			exp = new C_GreaterOrEquals(placeHolder, new TD_Value(dataType,
					firstArg));
			break;
		case IN:
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
		case IS_NOT_NULL:
			exp = new C_IsNotNull(placeHolder);
			break;
		case IS_NULL:
			exp = new C_IsNull(placeHolder);
			break;
		case LESSER:
			checkTypeArgument(column, firstArg);
			exp = new C_LessThan(placeHolder, new TD_Value(dataType, firstArg));
			break;
		case LESSER_OR_EQUALS:
			checkTypeArgument(column, firstArg);
			exp = new C_LessOrEquals(placeHolder, new TD_Value(dataType,
					firstArg));
			break;
		case LIKE:
			break;
		case MATCH_REGEX:
			checkTypeArgument(column, firstArg);
			exp = new C_TextMatchSQLRegexp(placeHolder, new TD_Value(dataType,
					firstArg));
			break;
		case MODULUS:
			break;
		case MULTIPLICATION:
			break;
		case NOT:
			break;
		case NOT_BEGINS_WITH:
			checkTypeArgument(column, firstArg);
			exp = new C_TextBeginsWith(placeHolder, new TD_Value(dataType,
					firstArg));
			exp = new C_Not(exp);
			break;
		case NOT_BETWEEN:
			checkTypeArgument(column, firstArg);
			checkTypeArgument(column, secondArg);
			
			fArg = new TD_Value(dataType, firstArg);
			sArg = new TD_Value(dataType, secondArg);
			exp = new C_Between(placeHolder, fArg, sArg);
			exp = new C_Not(exp);
			break;
		case NOT_CONTAINS:
			checkTypeArgument(column, firstArg);
			fArg = new TD_Value(dataType, firstArg);
			exp = new C_TextContains(placeHolder, fArg);
			exp = new C_Not(exp);
			break;
		case NOT_ENDS_WITH:
			checkTypeArgument(column, firstArg);
			exp = new C_TextEndsWith(placeHolder, new TD_Value(dataType,
					firstArg));
			exp = new C_Not(exp);
			break;
		case NOT_EQUALS:
			checkTypeArgument(column, firstArg);
			exp = new C_NotEquals(placeHolder, new TD_Value(dataType, firstArg));
			break;
		case NOT_GREATER:
			checkTypeArgument(column, firstArg);
			exp = new C_NotGreater(placeHolder,
					new TD_Value(dataType, firstArg));
			break;
		case NOT_IN:
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
			exp = new C_Not(exp);
			break;
		case NOT_LESSER:
			checkTypeArgument(column, firstArg);
			exp = new C_NotLess(placeHolder, new TD_Value(dataType, firstArg));
			break;
		case NOT_LIKE:
			break;
		case NOT_MATCH_REGEX:
			checkTypeArgument(column, firstArg);
			exp = new C_TextMatchSQLRegexp(placeHolder, new TD_Value(dataType,
					firstArg));
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
			checkTypeArgument(column, firstArg);
			C_Soundex soundexPlaceHolder=new C_Soundex(placeHolder);
			C_Soundex soundexValue=new C_Soundex(new TD_Value(dataType, firstArg));	
			exp = new C_Equals(soundexPlaceHolder, soundexValue);
			break;	
		case LEVENSHTEIN:
			checkTypeArgument(column, firstArg);
			C_Levenshtein levenshstein=new C_Levenshtein(placeHolder,new TD_Value(dataType, firstArg));
			Threshold thresholdLev;
	
			if(threshold==null){
				thresholdLev=ThresholdStore.defaultThresholdLevenshtein();
			} else {
				thresholdLev=threshold;
			}
			exp= new C_LessOrEquals(levenshstein,new TD_Value(ColumnDataType.Integer,
					thresholdLev.getIntegerValue().toString()));
			break;
		case SIMILARITY:
			checkTypeArgument(column, firstArg);
			C_Similarity similarity=new C_Similarity(placeHolder,new TD_Value(dataType, firstArg));
			Threshold thresholdSim;
	
			if(threshold==null){
				thresholdSim=ThresholdStore.defaultThresholdSimilarity();
			} else {
				thresholdSim=threshold;
			}
			exp= new C_GreaterOrEquals(similarity,new TD_Value(ColumnDataType.Numeric,
					thresholdSim.getLabel()));
			break;
			
		default:
			break;
		}

		return exp;
	}

	public C_Expression mapTypedColumnReference(ColumnData column,
			C_OperatorType operatorType, String firstArg, String secondArg, Threshold threshold)
			throws ConditionTypeMapException {
		Log.debug("ConditionTypeMap Map Typed Column Reference");
		C_Expression exp = null;

		Log.debug("Column Data Type Name:" + column.getDataTypeName());
		ColumnDataType dataType = ColumnDataType
				.getColumnDataTypeFromId(column
				.getDataTypeName());
		Log.debug("Data Type:" + dataType);
		C_ColumnReference columnReference = new C_ColumnReference(
				column);
		Log.debug("Typed Column Reference:" + columnReference);
		
		TD_Value fArg;
		TD_Value sArg;

		List<TD_Value> arguments = new ArrayList<TD_Value>();
		int separator;

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
			checkTypeArgument(column, firstArg);
			exp = new C_TextBeginsWith(columnReference, new TD_Value(dataType,
					firstArg));
			break;
		case BETWEEN:
			checkTypeArgument(column, firstArg);
			checkTypeArgument(column, secondArg);
			fArg = new TD_Value(dataType, firstArg);
			sArg = new TD_Value(dataType, secondArg);
			exp = new C_Between(columnReference, fArg, sArg);
			break;
		case CONTAINS:
			checkTypeArgument(column, firstArg);
			fArg = new TD_Value(dataType, firstArg);
			exp = new C_TextContains(columnReference, fArg);
			break;
		case DIVISION:
			break;
		case ENDS_WITH:
			checkTypeArgument(column, firstArg);
			exp = new C_TextEndsWith(columnReference, new TD_Value(dataType,
					firstArg));
			break;
		case EQUALS:
			checkTypeArgument(column, firstArg);
			exp = new C_Equals(columnReference,
					new TD_Value(dataType, firstArg));
			break;
		case EXISTS:
			// exp=new C_Exi(placeHolder,new C_ConstantList(arguments));
			break;
		case GREATER:
			checkTypeArgument(column, firstArg);
			exp = new C_GreaterThan(columnReference, new TD_Value(dataType,
					firstArg));
			break;
		case GREATER_OR_EQUALS:
			checkTypeArgument(column, firstArg);
			exp = new C_GreaterOrEquals(columnReference, new TD_Value(dataType,
					firstArg));
			break;
		case IN:
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
		case IS_NOT_NULL:
			exp = new C_IsNotNull(columnReference);
			break;
		case IS_NULL:
			exp = new C_IsNull(columnReference);
			break;
		case LESSER:
			checkTypeArgument(column, firstArg);
			exp = new C_LessThan(columnReference, new TD_Value(dataType,
					firstArg));
			break;
		case LESSER_OR_EQUALS:
			checkTypeArgument(column, firstArg);
			exp = new C_LessOrEquals(columnReference, new TD_Value(dataType,
					firstArg));
			break;
		case LIKE:
			break;
		case MATCH_REGEX:
			checkTypeArgument(column, firstArg);
			exp = new C_TextMatchSQLRegexp(columnReference, new TD_Value(
					dataType, firstArg));
			break;
		case MODULUS:
			break;
		case MULTIPLICATION:
			break;
		case NOT:
			break;
		case NOT_BEGINS_WITH:
			checkTypeArgument(column, firstArg);
			exp = new C_TextBeginsWith(columnReference, new TD_Value(dataType,
					firstArg));
			exp = new C_Not(exp);
			break;
		case NOT_BETWEEN:
			checkTypeArgument(column, firstArg);
			checkTypeArgument(column, secondArg);
			fArg = new TD_Value(dataType, firstArg);
			sArg = new TD_Value(dataType, secondArg);
			exp = new C_Between(columnReference, fArg, sArg);
			exp = new C_Not(exp);
			break;
		case NOT_CONTAINS:
			checkTypeArgument(column, firstArg);
			fArg = new TD_Value(dataType, firstArg);
			exp = new C_TextContains(columnReference, fArg);
			exp = new C_Not(exp);
			break;
		case NOT_ENDS_WITH:
			checkTypeArgument(column, firstArg);
			exp = new C_TextEndsWith(columnReference, new TD_Value(dataType,
					firstArg));
			exp = new C_Not(exp);
			break;
		case NOT_EQUALS:
			checkTypeArgument(column, firstArg);
			exp = new C_NotEquals(columnReference, new TD_Value(dataType,
					firstArg));
			break;
		case NOT_GREATER:
			checkTypeArgument(column, firstArg);
			exp = new C_NotGreater(columnReference, new TD_Value(dataType,
					firstArg));
			break;
		case NOT_IN:
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
			exp = new C_Not(exp);
			break;
		case NOT_LESSER:
			checkTypeArgument(column, firstArg);
			exp = new C_NotLess(columnReference, new TD_Value(dataType,
					firstArg));
			break;
		case NOT_LIKE:
			break;
		case NOT_MATCH_REGEX:
			checkTypeArgument(column, firstArg);
			exp = new C_TextMatchSQLRegexp(columnReference, new TD_Value(
					dataType, firstArg));
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
			checkTypeArgument(column, firstArg);
			C_Soundex soundexPlaceHolder=new C_Soundex(columnReference);
			C_Soundex soundexValue=new C_Soundex(new TD_Value(dataType, firstArg));	
			exp = new C_Equals(soundexPlaceHolder, soundexValue);
			break;	
		case LEVENSHTEIN:
			checkTypeArgument(column, firstArg);
			C_Levenshtein levenshstein=new C_Levenshtein(columnReference,new TD_Value(dataType, firstArg));
			Threshold thresholdLev;
	
			if(threshold==null){
				thresholdLev=ThresholdStore.defaultThresholdLevenshtein();
			} else {
				thresholdLev=threshold;
			}
			exp= new C_LessOrEquals(levenshstein,new TD_Value(ColumnDataType.Integer,
					thresholdLev.getIntegerValue().toString()));
			break;
		case SIMILARITY:
			checkTypeArgument(column, firstArg);
			C_Similarity similarity=new C_Similarity(columnReference,new TD_Value(dataType, firstArg));
			Threshold thresholdSim;
	
			if(threshold==null){
				thresholdSim=ThresholdStore.defaultThresholdSimilarity();
			} else {
				thresholdSim=threshold;
			}
			exp= new C_GreaterOrEquals(similarity,new TD_Value(ColumnDataType.Numeric,
					thresholdSim.getLabel()));	
			break;
		default:
			break;
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
				if(arg==null){
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
							if(arg==null){
								throw new ConditionTypeMapException(
										"Insert a valid Integer(ex: -1, 0, 1, 2)!");
							}
						
							try {
								Integer.parseInt(arg);
							} catch (NumberFormatException e) {
								throw new ConditionTypeMapException(arg
										+ " is not valid Integer(ex: -1, 0, 1, 2)!");
							}
						} else {
							if (column.getDataTypeName().compareTo(
									ColumnDataType.Numeric.toString()) == 0) {
								if(arg==null){
									throw new ConditionTypeMapException(
											"Insert a valid Numeric(ex: -1.2, 0, 1, 2.4)!");
								}
								try {
									Double.parseDouble(arg);
								} catch (NumberFormatException e) {
									throw new ConditionTypeMapException(arg
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
