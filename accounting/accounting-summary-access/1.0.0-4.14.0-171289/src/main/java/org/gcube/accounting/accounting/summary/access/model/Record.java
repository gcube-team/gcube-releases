package org.gcube.accounting.accounting.summary.access.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import lombok.AllArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Record implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2789039942507048174L;
	private String x;
	private Long y; 
	
}
