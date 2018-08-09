package org.gcube.portlets.user.td.gwtservice.shared.tr.batch;

import java.io.Serializable;

/**
 * 
 * @author Giancarlo Panichi 
 * 
 *
 */
public class Occurrences implements Serializable {

	private static final long serialVersionUID = -5521905443482827219L;
	
	protected String value;
	protected int number;
	
	//For view column, 
	//contains the value of the associated dimension column
	//for example: [value=atricaudus, number=1, rowId=10]
	protected String rowId;
	
	
	
	public Occurrences(){
		
	}
	
	public Occurrences(String value, int number){
		this.value=value;
		this.number=number;
		this.rowId=null;
	}
	
	public Occurrences(String value, String rowId,int number){
		this.value=value;
		this.number=number;
		this.rowId=rowId;
	}
	
	
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}
	
	public String getRowId() {
		return rowId;
	}

	public void setRowId(String rowId) {
		this.rowId = rowId;
	}

	@Override
	public String toString() {
		return "Occurences [value=" + value + ", number=" + number + ", rowId="
				+ rowId + "]";
	}

	
	
	
}
