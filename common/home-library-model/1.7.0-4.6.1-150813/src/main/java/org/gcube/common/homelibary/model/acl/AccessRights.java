package org.gcube.common.homelibary.model.acl;

import java.util.ArrayList;
import java.util.List;

public class AccessRights {
	private List<String> granted = new ArrayList<String>();
	private List<String> denied = new ArrayList<String>();

	public List<String> getGranted() {
		return granted;
	}
	public List<String> getDenied() {
		return denied;
	}
}
