package org.gcube.common.storagehub.model.service;

import java.util.Calendar;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor	
@NoArgsConstructor
public class Version {

	private String id;
	private String name;
	private Calendar created;
	private boolean current;	
	
}
