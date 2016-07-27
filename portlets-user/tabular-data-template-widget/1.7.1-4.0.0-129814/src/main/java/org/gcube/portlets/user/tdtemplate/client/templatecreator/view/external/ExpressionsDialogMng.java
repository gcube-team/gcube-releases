/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client.templatecreator.view.external;

import java.util.HashMap;
import java.util.Map;

import org.gcube.portlets.user.tdtemplate.client.TdTemplateController;
import org.gcube.portlets.user.tdtemplate.client.templatecreator.view.ColumnElement;

import com.google.gwt.core.shared.GWT;


/**
 * The Class ExpressionsDialogMng.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Apr 17, 2015
 */
public class ExpressionsDialogMng {
	
	private int columnIndex;
	private Map<String, ExpressionDialogCaller> mapCallers = new HashMap<String, ExpressionDialogCaller>();
	private String columnId;
	private ColumnElement columnElement;
	
	/**
	 * Instantiates a new expressions dialog mng.
	 *
	 * @param columnElement the column id
	 * @param columnIndex the column index
	 */
	public ExpressionsDialogMng(ColumnElement columnElement) {
		this.columnElement = columnElement;
		this.columnId = columnElement.getColumnId();
	}

	/**
	 * Gets the column index.
	 *
	 * @return the column index
	 */
	public int getColumnIndex() {
		return columnIndex;
	}
	
	/**
	 * Update expression caller.
	 *
	 * @param columnType the column type
	 * @param dataType the data type
	 * @param expressionIndex the expression index
	 * @param columnIndex2 the column index2
	 * @return the index of the ExpressionDialogCaller added
	 * @throws Exception the exception
	 */
	public void updateExpressionCaller(String columnType, String dataType, int expressionIndex) throws Exception{
		String key = getKey(expressionIndex);
		mapCallers.put(key, new ExpressionDialogCaller(columnElement.getColumnId(), columnType, dataType, TdTemplateController.getCommonBus(), columnElement.getColumnIndex(), columnElement.getColumnLabel()));
	}
	
	/**
	 * Gets the key.
	 *
	 * @param expressionIndex the expression index
	 * @return the key
	 */
	private String getKey(int expressionIndex){
		return columnId+"Expression"+expressionIndex;
	}
	
	/**
	 * Size.
	 *
	 * @return the int
	 */
	public int size(){
		return mapCallers.size();
	}
	
	/**
	 * Delete expression caller.
	 *
	 * @param expressionIndex the expression index
	 * @return true, if successful
	 */
	public boolean deleteExpressionCaller(int expressionIndex) {
		GWT.log("Deleting expression caller at index "+expressionIndex);
		ExpressionDialogCaller exp = null;
		try {
			validateKey(expressionIndex);
			String key = getKey(expressionIndex);
			exp = mapCallers.remove(key);
		} catch (Exception e) {
			GWT.log("Expression index is out of range, skipping delete expression caller");
			return false;
		}
		
		
		return exp!=null?true:false;
	}
	
	/**
	 * Gets the ED caller.
	 *
	 * @param expressionIndex the expression index
	 * @return the ED caller
	 * @throws Exception the exception
	 */
	public ExpressionDialogCaller getEDCaller(int expressionIndex) throws Exception{
		GWT.log("Get Caller expression at index "+expressionIndex);
		try {
			validateKey(expressionIndex);
			String key = getKey(expressionIndex);
			return 	mapCallers.get(key);
		} catch (Exception e) {
			GWT.log("Expression index is out of range, skipping get expression caller");
			return null;
		}
	}
	
	/**
	 * Validate key.
	 *
	 * @param index the index
	 * @throws Exception the exception
	 */
	private void validateKey(int index) throws Exception{
		String key = getKey(index);
		GWT.log("Validating key: "+key);
		if(mapCallers.get(key)==null)
			throw new Exception("ExpressionDialogCaller with index: "+index +" doesn't exists");
		
		GWT.log("Key is ok, the caller exists");
	}

	/**
	 * Gets the column id.
	 *
	 * @return the column id
	 */
	public String getColumnId() {
		return columnId;
	}

}
