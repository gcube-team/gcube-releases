package org.gcube.portlets.admin.software_upload_wizard.server.aslmanagers;

import org.gcube.application.framework.core.session.ASLSession;

public interface ASLSessionManager {

	public static final String D4SCIENCE_INFRASTRUCTURE = "d4science.research-infrastructures.eu";
	public static final String GCUBE_INFRASTRUCTURE = "gcube";

	public abstract ASLSession getASLSession();

}