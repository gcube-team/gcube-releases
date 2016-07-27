package org.gcube.portlets.user.speciesdiscovery.shared;

import java.io.Serializable;

public enum DownloadState implements Serializable {
	PENDING, 
	ONGOING, 
	ONGOINGWITHFAILURES, 
	FAILED, 
	COMPLETED, 
	COMPLETEDWITHFAILURES, 
	SAVING,
	SAVED,
	SERVICE_UNKNOWN
}