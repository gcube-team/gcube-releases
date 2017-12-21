package org.gcube.portlets.user.td.monitorwidget.client.details.tree;

import java.util.ArrayList;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class MonitorTaskSDto extends MonitorFolderDto {

	private static final long serialVersionUID = 4644048540524701598L;

	public MonitorTaskSDto() {
		super();
	}

	public MonitorTaskSDto(String type, String id, String description,
			String state, String humanReadableStatus, float progress,
			ArrayList<MonitorBaseDto> childrens) {
		super(type, id, description, state, humanReadableStatus, progress,
				childrens);

	}

	

}
