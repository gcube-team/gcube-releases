package org.gcube.accounting.accounting.summary.access.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class ReportElement implements Serializable {

		
	private static final long serialVersionUID = 4067222241647217177L;
	
	/**
	 * 						LABEL
	 * 			|
	 *			|		_
	 *	yaxis	|	 _ | |
	 * 			|	| || |_
	 * 			|	| || | |	
	 * 			---------------------------------------------------
	 * 				
	 * 
	 * 					*Series_label1     *Series_label2    *Series_label3
	 * 
	 * 								xAxis
	 * 
	 */
	
	
	
	/**
	 * Series[] {
	 * 		Series : LABEL, dataRow [] 
	 * 		Series : LABEL, dataRow []
	 * 		Series : LABEL, dataRow []
	 * 		Series : LABEL, dataRow [] 
	 * 	}
	 */
	
	
		
	private String label;
	private String category;
	private String xAxis;
	private String yAxis;
	
	
	private Series[] serieses; 
		
	
	@Override
	public String toString() {
		StringBuilder builder=new StringBuilder("REPORT "+label);
		builder.append(" Y : "+yAxis+" X : "+xAxis+"\n");
		for(Series s:serieses){
			builder.append(s.toString()+"\n");
		}
		return builder.toString();
	}
}
