/****************************************************************************
 *  This software is part of the gCube Project.
 *  Site: http://www.gcube-system.org/
 ****************************************************************************
 * The gCube/gCore software is licensed as Free Open Source software
 * conveying to the EUPL (http://ec.europa.eu/idabc/eupl).
 * The software and documentation is provided by its authors/distributors
 * "as is" and no expressed or
 * implied warranty is given for its use, quality or fitness for a
 * particular case.
 ****************************************************************************
 * Filename: CurrentStatus.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.resourcemanagement.support.client.utils;

import java.io.Serializable;

import org.gcube.resourcemanagement.support.client.Resource_support;
import org.gcube.resourcemanagement.support.client.events.SetScopeEvent;
import org.gcube.resourcemanagement.support.shared.types.RunningMode;
import org.gcube.resourcemanagement.support.shared.types.UserGroup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;

/**
 * @author Daniele Strollo (ISTI-CNR)
 */
public final class CurrentStatus implements Serializable {
	private static final long serialVersionUID = 6487678609485417222L;
	private String currentScope = "/gcube/devsec";
	private String currentResourceType = null;
	private String currentResourceSubType = "";
	private String currentUser = "anonymous";
	private UserGroup credentials = UserGroup.DEBUG;
	private RunningMode runningMode = RunningMode.STANDALONE;
	private boolean useCache = false;
	private boolean loadGHNatStartup = true;

	public CurrentStatus() {
	}

	public boolean useCache() {
		return this.useCache;
	}

	public void setUseCache(final boolean useCache) {
		this.useCache = useCache;
	}

	public void setCurrentScope(final String scope) {
		if (scope != null) {
			currentScope = scope.trim();
		}
		if (Resource_support.get() != null) {
			Resource_support.get().getEventBus().fireEvent(new SetScopeEvent(scope));
		}
		//Commands.setStatusScope(scope);
	}

	public String getCurrentScope() {
		return currentScope;
	}

	public void setCurrentResourceType(String resType) {
		if (resType != null) {
			currentResourceType = resType.trim();
		} else {
			currentResourceType = null;
		}
	}

	public String getCurrentResourceType() {
		return currentResourceType;
	}

	public String getCurrentResourceSubType() {
		return currentResourceSubType;
	}

	public void setCurrentResourceSubType(final String currentResourceSubType) {
		if (currentResourceSubType != null) {
			this.currentResourceSubType = currentResourceSubType.trim();
		} else {
			this.currentResourceSubType = null;
		}
	}

	public UserGroup getCredentials() {
		return credentials;
	}

	public void setCredentials(final UserGroup credentials) {
		this.credentials = credentials;
	}

	public void setRunningMode(final RunningMode runningMode) {
		this.runningMode = runningMode;
	}

	public RunningMode getRunningMode() {
		return runningMode;
	}

	public void setCurrentUser(final String currentUser) {
		if (currentUser != null) {
			this.currentUser = currentUser.trim();
		} else {
			this.currentUser = null;
		}
	}

	public String getCurrentUser() {
		return currentUser;
	}

	public boolean isLoadGHNatStartup() {
		return loadGHNatStartup;
	}

	public void setLoadGHNatStartup(boolean loadGHNatStartup) {
		this.loadGHNatStartup = loadGHNatStartup;
	}
}
