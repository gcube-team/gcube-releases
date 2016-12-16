package org.gcube.data.analysis.tabulardata.model.datatype.value;

import org.gcube.data.analysis.tabulardata.expression.leaf.LeafExpression;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;

//@XmlSeeAlso({TDBoolean.class, TDDate.class, TDInteger.class, TDNumeric.class, TDText.class})
public abstract class TDTypeValue extends LeafExpression implements Comparable<TDTypeValue>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8476935790575319860L;

	
	@Override
	public DataType getReturnedDataType(){return null;}
}
