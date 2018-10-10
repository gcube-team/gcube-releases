package org.gcube.data.transfer.plugin.model;

import org.gcube.smartgears.context.application.ApplicationContext;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DataTransferContext {

	private ApplicationContext ctx;
	
}
