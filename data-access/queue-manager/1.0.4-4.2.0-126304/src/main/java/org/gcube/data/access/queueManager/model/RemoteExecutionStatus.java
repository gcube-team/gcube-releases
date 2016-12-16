package org.gcube.data.access.queueManager.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;


@XStreamAlias("RemoteExecutionStatus")
public enum RemoteExecutionStatus {

	COMPLETED,FAILED,STARTED,
	
}
