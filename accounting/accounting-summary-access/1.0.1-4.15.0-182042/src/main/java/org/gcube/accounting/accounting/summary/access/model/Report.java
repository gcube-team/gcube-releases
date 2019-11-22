package org.gcube.accounting.accounting.summary.access.model;

import java.io.Serializable;
import java.util.LinkedList;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Report implements Serializable {

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4621957934766560486L;
	private LinkedList<ReportElement> elements;
	@Override
	public String toString() {
		StringBuilder builder=new StringBuilder();
		for(ReportElement element:elements){
			builder.append(element.toString()+"\n");
		}
		return builder.toString();
	}
	
	
}
