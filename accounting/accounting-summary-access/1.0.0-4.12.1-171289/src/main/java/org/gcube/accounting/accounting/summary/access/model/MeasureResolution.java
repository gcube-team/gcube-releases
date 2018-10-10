package org.gcube.accounting.accounting.summary.access.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter

public enum MeasureResolution implements Serializable{

	MONTHLY("monthly_measure");

	private String tableName;
	
	
	
}
