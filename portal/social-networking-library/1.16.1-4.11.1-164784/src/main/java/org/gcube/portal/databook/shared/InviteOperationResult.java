package org.gcube.portal.databook.shared;

public enum InviteOperationResult {
	SUCCESS,
	FAILED,
	//If I send an invite the same email in the same environment more than once
	ALREADY_INVITED;
}
