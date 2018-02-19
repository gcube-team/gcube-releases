package org.gcube.usecases.ws.thredds;

public class Constants {

	public static final String LOCK_FILE="~WS-LOCK.lock";
	public static final String GCUBE_TOKEN_HEADER="gcube-token";
	
	public static final String LAST_UPDATE_TIME="WS-SYNCH.LAST_UPDATE";

	public static final String THREDDS_PERSISTENCE="thredds";
	public static final String THREDDS_DATA_TRANSFER_BASE_URL="data-transfer-service/gcube/service/REST/"+THREDDS_PERSISTENCE+"/";
	
	
	
	public static class WorkspaceProperties{
		public static final String TBS="WS-SYNCH.TO-BE-SYNCHRONIZED";
		public static final String REMOTE_PATH="WS-SYNCH.REMOTE-PATH";
		public static final String REMOTE_PERSISTENCE="WS-SYNCH.REMOTE-PERSISTENCE";
		public static final String SYNCH_FILTER="WS-SYNCH.SYNCHRONIZATION-FILTER";
		public static final String TARGET_TOKEN="WS-SYNCH.TARGET-TOKEN";
	}
	
}
