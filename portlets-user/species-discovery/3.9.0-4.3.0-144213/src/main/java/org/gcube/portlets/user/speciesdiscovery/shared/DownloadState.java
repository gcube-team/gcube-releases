package org.gcube.portlets.user.speciesdiscovery.shared;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

public enum DownloadState implements IsSerializable, Serializable {
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