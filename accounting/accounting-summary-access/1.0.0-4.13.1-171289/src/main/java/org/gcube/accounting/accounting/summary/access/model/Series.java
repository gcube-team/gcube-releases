package org.gcube.accounting.accounting.summary.access.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Series implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6980358300936282238L;
	
	private String label;
	private Record[] dataRow;	
	
	
	@Override
	public String toString() {
		StringBuilder values=new StringBuilder();
		StringBuilder xCoord=new StringBuilder();
		for(Record record: dataRow){
			values.append(record.getY()+"\t");
			xCoord.append(record.getX()+"\t");
		}
		return label+"\n"+values+"\n"+xCoord+"\n";
	}
}
