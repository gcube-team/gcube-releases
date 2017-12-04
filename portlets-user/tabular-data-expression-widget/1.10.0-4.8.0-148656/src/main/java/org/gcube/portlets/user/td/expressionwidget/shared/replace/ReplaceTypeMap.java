package org.gcube.portlets.user.td.expressionwidget.shared.replace;

import org.gcube.portlets.user.td.expressionwidget.shared.exception.ReplaceTypeMapException;
import org.gcube.portlets.user.td.expressionwidget.shared.model.composite.arithmetic.C_Addition;
import org.gcube.portlets.user.td.expressionwidget.shared.model.composite.arithmetic.C_Division;
import org.gcube.portlets.user.td.expressionwidget.shared.model.composite.arithmetic.C_Modulus;
import org.gcube.portlets.user.td.expressionwidget.shared.model.composite.arithmetic.C_Multiplication;
import org.gcube.portlets.user.td.expressionwidget.shared.model.composite.arithmetic.C_Subtraction;
import org.gcube.portlets.user.td.expressionwidget.shared.model.composite.functions.C_Cast;
import org.gcube.portlets.user.td.expressionwidget.shared.model.composite.text.C_Concat;
import org.gcube.portlets.user.td.expressionwidget.shared.model.composite.text.C_Lower;
import org.gcube.portlets.user.td.expressionwidget.shared.model.composite.text.C_MD5;
import org.gcube.portlets.user.td.expressionwidget.shared.model.composite.text.C_SubstringByIndex;
import org.gcube.portlets.user.td.expressionwidget.shared.model.composite.text.C_SubstringByRegex;
import org.gcube.portlets.user.td.expressionwidget.shared.model.composite.text.C_SubstringPosition;
import org.gcube.portlets.user.td.expressionwidget.shared.model.composite.text.C_TextReplaceMatchingRegex;
import org.gcube.portlets.user.td.expressionwidget.shared.model.composite.text.C_Trim;
import org.gcube.portlets.user.td.expressionwidget.shared.model.composite.text.C_Upper;
import org.gcube.portlets.user.td.expressionwidget.shared.model.leaf.C_ColumnReference;
import org.gcube.portlets.user.td.expressionwidget.shared.model.leaf.C_ColumnReferencePlaceholder;
import org.gcube.portlets.user.td.expressionwidget.shared.model.leaf.TD_Value;
import org.gcube.portlets.user.td.widgetcommonevent.shared.expression.C_Expression;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnDataType;

import com.allen_sauer.gwt.log.client.Log;

/**
 * ReplaceTypeMap creates a C_Expression usable client-side
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class ReplaceTypeMap {
	public ReplaceTypeMap() {

	}
	
	public C_Expression map(ColumnData column, ColumnDataType targetType, ReplaceType replaceType,
			String firstArg, String secondArg,boolean template) throws ReplaceTypeMapException {
		return map(column, targetType, replaceType,firstArg,  secondArg, template, null, null, null, null);
	}

	public C_Expression map(ColumnData column, ColumnDataType targetType, ReplaceType replaceType,
			String firstArg, String secondArg, boolean template, C_Expression firstE,
			C_Expression secondE, String readableFirstE, String readableSecondE) throws ReplaceTypeMapException {
		Log.debug("ReplaceTypeMap Map");
		C_Expression exp = null;
		new String();
		if (template) {
			exp = mapPlaceHolder(column, targetType, replaceType, firstArg, secondArg,
					firstE, secondE, readableFirstE,readableSecondE);
		} else {
			exp = mapColumnReference(column, targetType, replaceType, firstArg,
					secondArg, firstE, secondE,readableFirstE,readableSecondE);
		}
		return exp;
	}

	public C_Expression mapPlaceHolder(ColumnData column,ColumnDataType targetType,
			ReplaceType replaceType, String firstArg, String secondArg,
			C_Expression firstE, C_Expression secondE,String readableFirstE, String readableSecondE) throws ReplaceTypeMapException {
		Log.debug("ReplaceTypeMap Map Place Holder");
		C_Expression exp = null;
		
		if(column==null){
			switch (replaceType) {
			case Value:
				checkTypeArgument(targetType,firstArg);
				exp = new TD_Value(targetType, firstArg);
				break;
			case Concat:
				C_Concat concat = new C_Concat(firstE, secondE);
				exp= new C_Cast(concat,targetType);
				break;	
			case Addition:
				C_Addition add = new C_Addition(firstE, secondE);
				exp= new C_Cast(add, targetType);
				break;
			case Subtraction:
				C_Subtraction sub = new C_Subtraction(firstE, secondE);
				exp= new C_Cast(sub,targetType);
				break;
			case Modulus:
				C_Modulus modu = new C_Modulus(firstE, secondE);
				exp=new C_Cast(modu,targetType);
				break;
			case Multiplication:
				C_Multiplication multi = new C_Multiplication(firstE, secondE);
				exp=new C_Cast(multi,targetType);
				
				break;
			case Division:
				C_Division divi = new C_Division(firstE, secondE);
				exp=new C_Cast(divi,targetType);
				break;	
			default:
				throw new ReplaceTypeMapException("No valid column selected"); 
			}
			return exp;
		}
	
		switch (replaceType) {
		case Value:
			checkTypeArgument(targetType,firstArg);
			exp = new TD_Value(targetType, firstArg);
			break;
		case ColumnValue:
			exp = columnValuePlaceHolder(column, targetType);
			break;		
		case Upper:
			exp = upperPlaceHolder(column, targetType);
			break;			
		case Lower:
			exp = lowerPlaceHolder(column, targetType);
			break;				
		case Trim:
			exp = trimPlaceHolder(column, targetType);
			break;			
		case MD5:
			exp = md5PlaceHolder(column, targetType);
			break;				
		case Concat:
			C_Concat concat = new C_Concat(firstE, secondE);
			exp= new C_Cast(concat,targetType);
			break;	
		case Addition:
			C_Addition add = new C_Addition(firstE, secondE);
			exp= new C_Cast(add, targetType);
			break;
		case Subtraction:
			C_Subtraction sub = new C_Subtraction(firstE, secondE);
			exp= new C_Cast(sub,targetType);
			break;
		case Modulus:
			C_Modulus modu = new C_Modulus(firstE, secondE);
			exp=new C_Cast(modu,targetType);
			break;
		case Multiplication:
			C_Multiplication multi = new C_Multiplication(firstE, secondE);
			exp=new C_Cast(multi,targetType);
			
			break;
		case Division:
			C_Division divi = new C_Division(firstE, secondE);
			exp=new C_Cast(divi,targetType);
			break;	
		case SubstringByRegex:
			exp = substringByRegexPlaceHolder(column, targetType, firstArg);
			break;
		case SubstringByIndex:
			exp = substringByIndexPlaceHolder(column, targetType, firstArg,
					secondArg);
			break;
		case SubstringByCharSeq:
			exp = substringByCharSeqPlaceHolder(column, targetType, firstArg,
					secondArg);
			break;
		case TextReplaceMatchingRegex:
			exp = textReplaceMatchingRegexPlaceHolder(column, targetType,
					firstArg, secondArg);
			break;
		default:
			break;
		}
		return exp;
	}

	private C_Expression textReplaceMatchingRegexPlaceHolder(ColumnData column,
			ColumnDataType targetType, String firstArg, String secondArg)
			throws ReplaceTypeMapException {
		C_Expression exp;
		TD_Value fArg;
		TD_Value sArg;
		Log.debug("Column Data Type Name:" + column.getDataTypeName());
		ColumnDataType dataType = ColumnDataType
				.getColumnDataTypeFromId(column
				.getDataTypeName());
		Log.debug("Data Type:" + dataType);
		C_ColumnReferencePlaceholder placeH = new C_ColumnReferencePlaceholder(
				dataType, column.getColumnId(), column.getLabel());
		C_Expression placeHolder;
		if(dataType!=ColumnDataType.Text){
			placeHolder=new C_Cast(placeH, ColumnDataType.Text);
		} else {
			placeHolder=placeH;

		}
		Log.debug("placeHolder:" + placeHolder);
		
		checkTypeArgument(ColumnDataType.Text,firstArg);
		checkTypeArgument(ColumnDataType.Text,secondArg);
		fArg = new TD_Value(ColumnDataType.Text, firstArg);
		sArg = new TD_Value(ColumnDataType.Text, secondArg);
		C_TextReplaceMatchingRegex substring = new C_TextReplaceMatchingRegex(placeHolder, fArg, sArg);
		exp=new C_Cast(substring, targetType);
		return exp;
	}

	private C_Expression substringByCharSeqPlaceHolder(ColumnData column,
			ColumnDataType targetType, String firstArg, String secondArg)
			throws ReplaceTypeMapException {
		C_Expression exp;
		TD_Value fArg;
		//TD_Value sArg;
		C_Expression posFrom;
		C_Expression posTo;
		Log.debug("Column Data Type Name:" + column.getDataTypeName());
		ColumnDataType dataType = ColumnDataType
				.getColumnDataTypeFromId(column
				.getDataTypeName());
		Log.debug("Data Type:" + dataType);
		C_ColumnReferencePlaceholder placeH = new C_ColumnReferencePlaceholder(
				dataType, column.getColumnId(), null);
		C_Expression placeHolder;
		if(dataType!=ColumnDataType.Text){
			placeHolder=new C_Cast(placeH, ColumnDataType.Text);
		} else {
			placeHolder=placeH;

		}
		Log.debug("placeHolder:" + placeHolder);
		
		
		checkTypeArgument(ColumnDataType.Text,firstArg);
		fArg = new TD_Value(ColumnDataType.Text, firstArg);
		posFrom = new C_SubstringPosition(placeHolder, fArg);
		checkTypeArgument(ColumnDataType.Text,secondArg);
		//sArg = new TD_Value(ColumnDataType.Text, secondArg);
		posTo = new C_SubstringPosition(placeHolder, fArg);
		C_SubstringByIndex substring = new C_SubstringByIndex(placeHolder, posFrom, posTo);
		exp=new C_Cast(substring, targetType);
		return exp;
	}

	private C_Expression substringByIndexPlaceHolder(ColumnData column,
			ColumnDataType targetType, String firstArg, String secondArg)
			throws ReplaceTypeMapException {
		C_Expression exp;
		TD_Value fArg;
		TD_Value sArg;
		Log.debug("Column Data Type Name:" + column.getDataTypeName());
		ColumnDataType dataType = ColumnDataType
				.getColumnDataTypeFromId(column
				.getDataTypeName());
		Log.debug("Data Type:" + dataType);
		C_ColumnReferencePlaceholder placeH = new C_ColumnReferencePlaceholder(
				dataType, column.getColumnId(), null);
		C_Expression placeHolder;
		if(dataType!=ColumnDataType.Text){
			placeHolder=new C_Cast(placeH, ColumnDataType.Text);
		} else {
			placeHolder=placeH;

		}
		Log.debug("placeHolder:" + placeHolder);
		
		
		checkTypeArgument(ColumnDataType.Integer,firstArg);
		checkTypeArgument(ColumnDataType.Integer,secondArg);
		fArg = new TD_Value(ColumnDataType.Integer, firstArg);
		sArg = new TD_Value(ColumnDataType.Integer, secondArg);
		C_SubstringByIndex substring = new C_SubstringByIndex(placeHolder, fArg, sArg);
		exp=new C_Cast(substring, targetType);
		return exp;
	}

	private C_Expression substringByRegexPlaceHolder(ColumnData column,
			ColumnDataType targetType, String firstArg)
			throws ReplaceTypeMapException {
		C_Expression exp;
		TD_Value fArg;
		Log.debug("Column Data Type Name:" + column.getDataTypeName());
		ColumnDataType dataType = ColumnDataType
				.getColumnDataTypeFromId(column
				.getDataTypeName());
		Log.debug("Data Type:" + dataType);
		C_ColumnReferencePlaceholder placeH = new C_ColumnReferencePlaceholder(
				dataType, column.getColumnId(), null);
		C_Expression placeHolder;
		if(dataType!=ColumnDataType.Text){
			placeHolder=new C_Cast(placeH, ColumnDataType.Text);
		} else {
			placeHolder=placeH;

		}
		Log.debug("placeHolder:" + placeHolder);
		
		checkTypeArgument(ColumnDataType.Text,firstArg);
		fArg = new TD_Value(ColumnDataType.Text, firstArg);
		C_SubstringByRegex substring = new C_SubstringByRegex(placeHolder, fArg);
		exp=new C_Cast(substring, targetType);
		return exp;
	}

	
	private C_Expression columnValuePlaceHolder(ColumnData column,
			ColumnDataType targetType) {
		C_Expression exp;
		Log.debug("Column Data Type Name:" + column.getDataTypeName());
		ColumnDataType dataType = ColumnDataType
				.getColumnDataTypeFromId(column
				.getDataTypeName());
		Log.debug("Data Type:" + dataType);
		C_ColumnReferencePlaceholder placeH = new C_ColumnReferencePlaceholder(
				dataType, column.getColumnId(), null);
		C_Expression placeHolder;
		if(dataType!=targetType){
			placeHolder=new C_Cast(placeH, targetType);
		} else {
			placeHolder=placeH;

		}
		Log.debug("placeHolder:" + placeHolder);
		
		
		exp = placeHolder;
		return exp;
	}

	private C_Expression upperPlaceHolder(ColumnData column,
			ColumnDataType targetType) throws ReplaceTypeMapException {
		C_Expression exp;
		Log.debug("Column Data Type Name:" + column.getDataTypeName());
		ColumnDataType dataType = ColumnDataType
				.getColumnDataTypeFromId(column
				.getDataTypeName());
		Log.debug("Data Type:" + dataType);
		C_ColumnReferencePlaceholder placeH = new C_ColumnReferencePlaceholder(
				dataType, column.getColumnId(), null);
		C_Expression placeHolder;
		if(dataType!=ColumnDataType.Text){
			placeHolder=new C_Cast(placeH, ColumnDataType.Text);
		} else {
			placeHolder=placeH;

		}
		Log.debug("placeHolder:" + placeHolder);
		
		C_Upper upper = new C_Upper(placeHolder);
		exp=new C_Cast(upper, targetType);
		return exp;
	}
	
	
	private C_Expression lowerPlaceHolder(ColumnData column,
			ColumnDataType targetType) throws ReplaceTypeMapException {
		C_Expression exp;
		Log.debug("Column Data Type Name:" + column.getDataTypeName());
		ColumnDataType dataType = ColumnDataType
				.getColumnDataTypeFromId(column
				.getDataTypeName());
		Log.debug("Data Type:" + dataType);
		C_ColumnReferencePlaceholder placeH = new C_ColumnReferencePlaceholder(
				dataType, column.getColumnId(), null);
		C_Expression placeHolder;
		if(dataType!=ColumnDataType.Text){
			placeHolder=new C_Cast(placeH, ColumnDataType.Text);
		} else {
			placeHolder=placeH;

		}
		Log.debug("placeHolder:" + placeHolder);
		
		C_Lower lower = new C_Lower(placeHolder);
		exp=new C_Cast(lower, targetType);
		return exp;
	}
	
	private C_Expression trimPlaceHolder(ColumnData column,
			ColumnDataType targetType) throws ReplaceTypeMapException {
		C_Expression exp;
		Log.debug("Column Data Type Name:" + column.getDataTypeName());
		ColumnDataType dataType = ColumnDataType
				.getColumnDataTypeFromId(column
				.getDataTypeName());
		Log.debug("Data Type:" + dataType);
		C_ColumnReferencePlaceholder placeH = new C_ColumnReferencePlaceholder(
				dataType, column.getColumnId(), null);
		C_Expression placeHolder;
		if(dataType!=ColumnDataType.Text){
			placeHolder=new C_Cast(placeH, ColumnDataType.Text);
		} else {
			placeHolder=placeH;

		}
		Log.debug("placeHolder:" + placeHolder);
		
		C_Trim trim = new C_Trim(placeHolder);
		exp=new C_Cast(trim, targetType);
		return exp;
	}
	
	
	private C_Expression md5PlaceHolder(ColumnData column,
			ColumnDataType targetType) throws ReplaceTypeMapException {
		C_Expression exp;
		Log.debug("Column Data Type Name:" + column.getDataTypeName());
		ColumnDataType dataType = ColumnDataType
				.getColumnDataTypeFromId(column
				.getDataTypeName());
		Log.debug("Data Type:" + dataType);
		C_ColumnReferencePlaceholder placeH = new C_ColumnReferencePlaceholder(
				dataType, column.getColumnId(), null);
		C_Expression placeHolder;
		if(dataType!=ColumnDataType.Text){
			placeHolder=new C_Cast(placeH, ColumnDataType.Text);
		} else {
			placeHolder=placeH;

		}
		Log.debug("placeHolder:" + placeHolder);
		
		C_MD5 md5 = new C_MD5(placeHolder);
		exp=new C_Cast(md5, targetType);
		return exp;
	}
	
	
	
	
	public C_Expression mapColumnReference(ColumnData column,ColumnDataType targetType,
			ReplaceType replaceType, String firstArg, String secondArg,
			C_Expression firstE, C_Expression secondE, String readableFirstE, String readableSecondE) throws ReplaceTypeMapException {
		Log.debug("ReplaceTypeMap Map Typed Column Reference");
		C_Expression exp = null;
		
		if(column==null){
			switch (replaceType) {
			case Value:
				checkTypeArgument(targetType,firstArg);
				exp = new TD_Value(targetType, firstArg);
				break;
			case Concat:
				C_Concat concat = new C_Concat(firstE, secondE);
				exp= new C_Cast(concat,targetType);
				break;	
			case Addition:
				C_Addition add = new C_Addition(firstE, secondE);
				exp= new C_Cast(add, targetType);
				break;
			case Subtraction:
				C_Subtraction sub = new C_Subtraction(firstE, secondE);
				exp= new C_Cast(sub,targetType);
				break;
			case Modulus:
				C_Modulus modu = new C_Modulus(firstE, secondE);
				exp=new C_Cast(modu,targetType);
				break;
			case Multiplication:
				C_Multiplication multi = new C_Multiplication(firstE, secondE);
				exp=new C_Cast(multi,targetType);
				
				break;
			case Division:
				C_Division divi = new C_Division(firstE, secondE);
				exp=new C_Cast(divi,targetType);
				break;	
			default:
				throw new ReplaceTypeMapException("No valid column selected"); 
			}
			return exp;
		}
		
		
		switch (replaceType) {
		case Value:
			checkTypeArgument(targetType, firstArg);
			exp = new TD_Value(targetType, firstArg);
			break;
		case ColumnValue:
			exp = columnValueReference(column, targetType);
			break;
		case Upper:
			exp = upperReference(column, targetType);
			break;	
		case Lower:
			exp = lowerReference(column, targetType);
			break;	
		case Trim:
			exp = trimReference(column, targetType);
			break;	
		case MD5:
			exp = md5Reference(column, targetType);
			break;		
		case Concat:
			C_Concat concat = new C_Concat(firstE, secondE);
			exp= new C_Cast(concat,targetType);
			break;	
		case Addition:
			C_Addition add = new C_Addition(firstE, secondE);
			exp= new C_Cast(add, targetType);
			break;
		case Subtraction:
			C_Subtraction sub = new C_Subtraction(firstE, secondE);
			exp= new C_Cast(sub,targetType);
			break;
		case Modulus:
			C_Modulus modu = new C_Modulus(firstE, secondE);
			exp=new C_Cast(modu,targetType);
			break;
		case Multiplication:
			C_Multiplication multi = new C_Multiplication(firstE, secondE);
			exp=new C_Cast(multi,targetType);
			
			break;
		case Division:
			C_Division divi = new C_Division(firstE, secondE);
			exp=new C_Cast(divi,targetType);
			break;	
		case SubstringByRegex:
			exp = SubstringByRegexReference(column, targetType, firstArg);
			break;
		case SubstringByIndex:
			exp = substringByIndexReference(column, targetType, firstArg,
					secondArg);
			break;
		case SubstringByCharSeq:
			exp = substringByCharSeq(column, targetType, firstArg, secondArg);
			break;
		case TextReplaceMatchingRegex:
			exp = textReplaceMatchingRegexReference(column, targetType,
					firstArg, secondArg);
			break;
		default:
			break;
		}

		return exp;
	}

	private C_Expression textReplaceMatchingRegexReference(ColumnData column,
			ColumnDataType targetType, String firstArg, String secondArg)
			throws ReplaceTypeMapException {
		C_Expression exp;
		TD_Value fArg;
		TD_Value sArg;
		
		C_ColumnReference columnRef = new C_ColumnReference(
				column);
		ColumnDataType dataType = ColumnDataType
				.getColumnDataTypeFromId(column
				.getDataTypeName());
		
		C_Expression columnReference;
		if(dataType!=ColumnDataType.Text){
			columnReference=new C_Cast(columnRef,ColumnDataType.Text);
		} else {
			columnReference=columnRef;

		}
		Log.debug("Typed Column Reference:" + columnReference);
		
		checkTypeArgument(ColumnDataType.Text, firstArg);
		checkTypeArgument(ColumnDataType.Text, secondArg);
		fArg = new TD_Value(ColumnDataType.Text, firstArg);
		sArg = new TD_Value(ColumnDataType.Text, secondArg);
		C_TextReplaceMatchingRegex substring = new C_TextReplaceMatchingRegex(columnReference, fArg, sArg);
		exp=new C_Cast(substring, targetType);
		return exp;
	}

	private C_Expression substringByCharSeq(ColumnData column,
			ColumnDataType targetType, String firstArg, String secondArg)
			throws ReplaceTypeMapException {
		C_Expression exp;
		TD_Value fArg;
		C_Expression posFrom;
		C_Expression posTo;	
		C_ColumnReference columnRef = new C_ColumnReference(
				column);
		
		ColumnDataType dataType = ColumnDataType
				.getColumnDataTypeFromId(column
				.getDataTypeName());
		C_Expression columnReference;
		if(dataType!=ColumnDataType.Text){
			columnReference=new C_Cast(columnRef,ColumnDataType.Text);
		} else {
			columnReference=columnRef;

		}
		Log.debug("Typed Column Reference:" + columnReference);
		
		checkTypeArgument(ColumnDataType.Text, firstArg);
		fArg = new TD_Value(ColumnDataType.Text, firstArg);
		posFrom = new C_SubstringPosition(columnReference, fArg);
		checkTypeArgument(ColumnDataType.Text, secondArg);
		//sArg = new TD_Value(ColumnDataType.Text, secondArg);
		posTo = new C_SubstringPosition(columnReference, fArg);
		C_SubstringByIndex substring = new C_SubstringByIndex(columnReference, posFrom, posTo);
		exp=new C_Cast(substring, targetType);
		return exp;
	}

	private C_Expression substringByIndexReference(ColumnData column,
			ColumnDataType targetType, String firstArg, String secondArg)
			throws ReplaceTypeMapException {
		C_Expression exp;
		TD_Value fArg;
		TD_Value sArg;
		
		C_ColumnReference columnRef = new C_ColumnReference(
				column);
		
		C_Expression columnReference;
		ColumnDataType dataType = ColumnDataType
				.getColumnDataTypeFromId(column
				.getDataTypeName());
		
		if(dataType!=ColumnDataType.Text){
			columnReference=new C_Cast(columnRef,ColumnDataType.Text);
		} else {
			columnReference=columnRef;

		}
		Log.debug("Typed Column Reference:" + columnReference);
		
		checkTypeArgument(ColumnDataType.Integer, firstArg);
		checkTypeArgument(ColumnDataType.Integer, secondArg);		
		fArg = new TD_Value(ColumnDataType.Integer, firstArg);
		sArg = new TD_Value(ColumnDataType.Integer, secondArg);
		C_SubstringByIndex substring = new C_SubstringByIndex(columnReference, fArg, sArg);
		exp=new C_Cast(substring,targetType);
		return exp;
	}

	private C_Expression SubstringByRegexReference(ColumnData column,
			ColumnDataType targetType, String firstArg)
			throws ReplaceTypeMapException {
		C_Expression exp;
		TD_Value fArg;
		C_ColumnReference columnRef = new C_ColumnReference(
				column);
		
		ColumnDataType dataType = ColumnDataType
				.getColumnDataTypeFromId(column
				.getDataTypeName());
		C_Expression columnReference;
		if(dataType!=ColumnDataType.Text){
			columnReference=new C_Cast(columnRef,ColumnDataType.Text);
		} else {
			columnReference=columnRef;

		}
		Log.debug("Typed Column Reference:" + columnReference);
		
		
		checkTypeArgument(ColumnDataType.Text, firstArg);
		fArg = new TD_Value(ColumnDataType.Text, firstArg);
		C_SubstringByRegex substring= new C_SubstringByRegex(columnReference, fArg);
		exp=new C_Cast(substring, targetType);
		return exp;
	}

	private C_Expression columnValueReference(ColumnData column,
			ColumnDataType targetType) {
		C_Expression exp;
		
		C_ColumnReference columnRef = new C_ColumnReference(
				column);
		
		ColumnDataType dataType = ColumnDataType
				.getColumnDataTypeFromId(column
				.getDataTypeName());
		C_Expression columnReference;
		if(dataType!=targetType){
			columnReference=new C_Cast(columnRef,targetType);
		} else {
			columnReference=columnRef;

		}
		Log.debug("Typed Column Reference:" + columnReference);
		exp = columnReference;
		return exp;
	}

	
	private C_Expression upperReference(ColumnData column,
			ColumnDataType targetType) {
		C_Expression exp;
		
		C_ColumnReference columnRef = new C_ColumnReference(
				column);
		
		ColumnDataType dataType = ColumnDataType
				.getColumnDataTypeFromId(column
				.getDataTypeName());
		
		C_Expression columnReference;
		if(dataType!=ColumnDataType.Text){
			columnReference=new C_Cast(columnRef,ColumnDataType.Text);
		} else {
			columnReference=columnRef;

		}
		Log.debug("Typed Column Reference:" + columnReference);
		
		
		C_Upper upper= new C_Upper(columnReference);
		exp=new C_Cast(upper, targetType);
		return exp;
	}
	
	private C_Expression lowerReference(ColumnData column,
			ColumnDataType targetType) {
		C_Expression exp;
		
		C_ColumnReference columnRef = new C_ColumnReference(
				column);
		
		ColumnDataType dataType = ColumnDataType
				.getColumnDataTypeFromId(column
				.getDataTypeName());
		
		C_Expression columnReference;
		if(dataType!=ColumnDataType.Text){
			columnReference=new C_Cast(columnRef,ColumnDataType.Text);
		} else {
			columnReference=columnRef;

		}
		Log.debug("Typed Column Reference:" + columnReference);
		
		
		C_Lower lower= new C_Lower(columnReference);
		exp=new C_Cast(lower, targetType);
		return exp;
	}
	
	
	private C_Expression trimReference(ColumnData column,
			ColumnDataType targetType) {
		C_Expression exp;
		
		C_ColumnReference columnRef = new C_ColumnReference(
				column);
		
		ColumnDataType dataType = ColumnDataType
				.getColumnDataTypeFromId(column
				.getDataTypeName());
		
		C_Expression columnReference;
		if(dataType!=ColumnDataType.Text){
			columnReference=new C_Cast(columnRef,ColumnDataType.Text);
		} else {
			columnReference=columnRef;

		}
		Log.debug("Typed Column Reference:" + columnReference);
		
		
		C_Trim trim= new C_Trim(columnReference);
		exp=new C_Cast(trim, targetType);
		return exp;
	}
	
	private C_Expression md5Reference(ColumnData column,
			ColumnDataType targetType) {
		C_Expression exp;
		
		C_ColumnReference columnRef = new C_ColumnReference(
				column);
		
		ColumnDataType dataType = ColumnDataType
				.getColumnDataTypeFromId(column
				.getDataTypeName());
		
		C_Expression columnReference;
		if(dataType!=ColumnDataType.Text){
			columnReference=new C_Cast(columnRef,ColumnDataType.Text);
		} else {
			columnReference=columnRef;

		}
		Log.debug("Typed Column Reference:" + columnReference);
		
		
		C_MD5 md5= new C_MD5(columnReference);
		exp=new C_Cast(md5, targetType);
		return exp;
	}
	
	
	
	/*public String getReadableExpression() {
		return readableExpression;
	}*/
	
	protected void checkTypeArgument(ColumnDataType columnDataType, String arg)
			throws ReplaceTypeMapException {
		if (columnDataType==ColumnDataType.Text) {
			if (arg == null) {
				arg = "";
			}
		} else {
			if (columnDataType==ColumnDataType.Boolean) {
				if(arg==null){
					throw new ReplaceTypeMapException(
							"Insert a valid Boolean(ex: true, false)!");
				}
			} else {
				if (columnDataType==ColumnDataType.Date) {

				} else {
					if (columnDataType==ColumnDataType.Geometry) {

					} else {
						if (columnDataType==ColumnDataType.Integer) {
							if(arg==null){
								throw new ReplaceTypeMapException(
										"Insert a valid Integer(ex: -1, 0, 1, 2)!");
							}
						
							try {
								Integer.parseInt(arg);
							} catch (NumberFormatException e) {
								throw new ReplaceTypeMapException(arg
										+ " is not valid Integer(ex: -1, 0, 1, 2)!");
							}
						} else {
							if (columnDataType==ColumnDataType.Numeric) {
								if(arg==null){
									throw new ReplaceTypeMapException(
											"Insert a valid Numeric(ex: -1.2, 0, 1, 2.4)!");
								}
								try {
									Double.parseDouble(arg);
								} catch (NumberFormatException e) {
									throw new ReplaceTypeMapException(arg
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
