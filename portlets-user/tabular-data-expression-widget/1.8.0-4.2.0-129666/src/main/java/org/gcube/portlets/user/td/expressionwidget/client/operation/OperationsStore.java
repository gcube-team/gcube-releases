package org.gcube.portlets.user.td.expressionwidget.client.operation;

import java.util.ArrayList;

import org.gcube.portlets.user.td.expressionwidget.shared.model.C_OperatorType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnDataType;



/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class OperationsStore {
	private ArrayList<Operation> operations;
	
	protected ArrayList<Operation> operationsNumeric = new ArrayList<Operation>() {
		private static final long serialVersionUID = -6559885743626876431L;
	{
	    add(new Operation(1,"EQUALS","Equal to",C_OperatorType.EQUALS));
	    add(new Operation(2,"GREATER","Greater than",C_OperatorType.GREATER));
	    add(new Operation(3,"GREATER_OR_EQUALS","Greater than or equal to",C_OperatorType.GREATER_OR_EQUALS));
	    add(new Operation(4,"LESSER","Less than",C_OperatorType.LESSER));
	    add(new Operation(5,"LESSER_OR_EQUALS","Less than or equal to",C_OperatorType.LESSER_OR_EQUALS));
	    add(new Operation(6,"NOT_EQUALS","Not equal to",C_OperatorType.NOT_EQUALS));
	    add(new Operation(7,"NOT_GREATER","Not greater than",C_OperatorType.NOT_GREATER));
	    add(new Operation(8,"NOT_LESSER","Not less than",C_OperatorType.NOT_LESSER));
	    add(new Operation(9,"IS_NULL","Is null",C_OperatorType.IS_NULL));
	    add(new Operation(10,"IS_NOT_NULL","Is not null",C_OperatorType.IS_NOT_NULL));
	    add(new Operation(11,"BETWEEN","Is between",C_OperatorType.BETWEEN));
	    add(new Operation(12,"NOT_BETWEEN","Is not between",C_OperatorType.NOT_BETWEEN));
	    add(new Operation(13,"IN","Is in",C_OperatorType.IN));   
	    add(new Operation(14,"NOT_IN","Is not in",C_OperatorType.NOT_IN));   
	}};
	
	protected ArrayList<Operation> operationsText = new ArrayList<Operation>() {
		private static final long serialVersionUID = -6559885743626876431L;
	{
		add(new Operation(1,"EQUALS","Equal to",C_OperatorType.EQUALS));
		add(new Operation(2,"BEGIN_WITH","Begin with",C_OperatorType.BEGINS_WITH));
	    add(new Operation(3,"CONTAINS","Contains",C_OperatorType.CONTAINS));
	    add(new Operation(4,"ENDS_WITH","End with",C_OperatorType.ENDS_WITH));
	    add(new Operation(5,"MATCH","Match",C_OperatorType.MATCH_REGEX));
	    add(new Operation(6,"NOT_EQUALS","Not equal to",C_OperatorType.NOT_EQUALS));
	    add(new Operation(7,"NOT_BEGIN_WITH","Not begin with",C_OperatorType.NOT_BEGINS_WITH));
	    add(new Operation(8,"NOT_CONTAINS","Not contains",C_OperatorType.NOT_CONTAINS));
	    add(new Operation(9,"NOT_ENDS_WITH","Not end with",C_OperatorType.NOT_ENDS_WITH));
	    add(new Operation(10,"NOT_MATCH","Not match",C_OperatorType.NOT_MATCH_REGEX));
	    add(new Operation(11,"IS_NULL","Is null",C_OperatorType.IS_NULL));
	    add(new Operation(12,"IS_NOT_NULL","Is not null",C_OperatorType.IS_NOT_NULL));
	    add(new Operation(13,"BETWEEN","Is between",C_OperatorType.BETWEEN));
	    add(new Operation(14,"NOT_BETWEEN","Is not between",C_OperatorType.NOT_BETWEEN));
	    add(new Operation(15,"IN","Is in",C_OperatorType.IN));   
	    add(new Operation(16,"NOT_IN","Is not in",C_OperatorType.NOT_IN)); 
	    add(new Operation(17,"SOUNDEX","Soundex", C_OperatorType.SOUNDEX));
	    add(new Operation(18,"LEVENSHTEIN","Levenshtein", C_OperatorType.LEVENSHTEIN));
	    add(new Operation(19,"SIMILARITY","Similarity", C_OperatorType.SIMILARITY));
	}};
	
	protected ArrayList<Operation> operationsBoolean = new ArrayList<Operation>() {
		
	
		private static final long serialVersionUID = -1095217157799110522L;

	{
		 add(new Operation(1,"EQUALS","Equal to",C_OperatorType.EQUALS));
		 add(new Operation(2,"NOT_EQUALS","Not equal to",C_OperatorType.NOT_EQUALS));
		 add(new Operation(3,"IS_NULL","Is null",C_OperatorType.IS_NULL));
		 add(new Operation(4,"IS_NOT_NULL","Is not null",C_OperatorType.IS_NOT_NULL));
		 
	}};
	
	protected ArrayList<Operation> operationsDate = new ArrayList<Operation>() {
		
		
		private static final long serialVersionUID = -1095217157799110522L;

	{
		add(new Operation(1,"EQUALS","Equal to",C_OperatorType.EQUALS));
		add(new Operation(2,"GREATER","Greater than",C_OperatorType.GREATER));
		add(new Operation(3,"GREATER_OR_EQUALS","Greater than or equal to",C_OperatorType.GREATER_OR_EQUALS));
		add(new Operation(4,"LESSER","Less than",C_OperatorType.LESSER));
		add(new Operation(5,"LESSER_OR_EQUALS","Less than or equal to",C_OperatorType.LESSER_OR_EQUALS));
		add(new Operation(6,"NOT_EQUALS","Not equal to",C_OperatorType.NOT_EQUALS));
		add(new Operation(7,"NOT_GREATER","Not greater than",C_OperatorType.NOT_GREATER));
		add(new Operation(8,"NOT_LESSER","Not less than",C_OperatorType.NOT_LESSER));
		add(new Operation(9,"IS_NULL","Is null",C_OperatorType.IS_NULL));
		add(new Operation(10,"IS_NOT_NULL","Is not null",C_OperatorType.IS_NOT_NULL));   
	 
	}};
	
	protected ArrayList<Operation> operationsGeometry = new ArrayList<Operation>() {
		
		
		private static final long serialVersionUID = -1095217157799110522L;

	{	
		//[ADDITION, DIVISION, MODULUS, MULTIPLICATION, SUBTRACTION, IS_NOT_NULL, IS_NULL, NOT, COUNT, MAX, MIN, ST_EXTENT]
		add(new Operation(1,"IS_NULL","Is null",C_OperatorType.IS_NULL));
		add(new Operation(2,"IS_NOT_NULL","Is not null",C_OperatorType.IS_NOT_NULL));
	 
	}};
	
	
	
	public ArrayList<Operation> getAll(ColumnData column){
		if(column==null){
			operations=new ArrayList<Operation>();
			return operations;
		}
		
		
		ColumnDataType dataType=ColumnDataType.getColumnDataTypeFromId(column.getDataTypeName());
		
		if(dataType==null){
			operations=new ArrayList<Operation>();
			return operations;
		}
		
		switch (dataType) {
		case Boolean:
			operations=operationsBoolean;
			break;
		case Date:
			operations=operationsDate;
			break;
		case Geometry:
			operations=operationsGeometry;
			break;
		case Integer:
			operations=operationsNumeric;
			break;
		case Numeric:
			operations=operationsNumeric;
			break;
		case Text:
			operations=operationsText;
			break;
		default:
			operations=new ArrayList<Operation>();
			break;
		}
		
		return operations;
	}
	
	
	
}
