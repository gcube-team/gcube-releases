package org.gcube.vremanagement.vremodeler.utils.reports;

import java.io.Serializable;

public enum Status implements Serializable {
	Running,
	Failed,
	Finished,
	Skipped,
	Pending,
	Waiting, 
	Expired,
	Deployed, 
	Disposed,
	Incomplete
}
