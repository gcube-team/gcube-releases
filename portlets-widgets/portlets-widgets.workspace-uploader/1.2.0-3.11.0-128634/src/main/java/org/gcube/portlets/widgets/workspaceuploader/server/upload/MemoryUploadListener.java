/*
 * Copyright 2010 Manuel Carrasco Moñino. (manolo at apache/org)
 * http://code.google.com/p/gwtupload
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.gcube.portlets.widgets.workspaceuploader.server.upload;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This File Upload Listener is used by Apache Commons File Upload to monitor
 * the progress of the uploaded file.
 *
 * This Listener saves itself into a unique map in memory. It doesn't work when
 * the application is deployed in cluster.
 *
 * It is thought to be used in systems where session objects are not updated
 * until the request has finished.
 *
 * @see MemoryUploadEvent
 */
public class MemoryUploadListener extends AbstractUploadProgressListener {

	private static final long serialVersionUID = 2082376357722210169L;

	private static final Map<String, MemoryUploadListener> listeners = new HashMap<String, MemoryUploadListener>();
	private static Logger logger = LoggerFactory
			.getLogger(MemoryUploadListener.class);

	/**
	 * Instantiates a new memory upload listener.
	 *
	 * @param session
	 *            the session
	 * @param clientUploadKey
	 *            the client upload key
	 * @param percentageOffset
	 *            the percentage offset
	 * @param completePercentage
	 *            the complete percentage
	 */
	public MemoryUploadListener(HttpSession session, String clientUploadKey,
			int percentageOffset, double completePercentage) {
		super(session, clientUploadKey, percentageOffset, completePercentage);
	}

	/**
	 * Current.
	 *
	 * @param sessionId
	 *            the session id
	 * @param clientUploadKey
	 *            the client upload key
	 * @return the memory upload listener
	 */
	public static MemoryUploadListener current(String sessionId,
			String clientUploadKey) {
		String sessionKey = getSessionKey(sessionId, clientUploadKey);
		MemoryUploadListener listener = listeners.get(sessionKey);
		logger.debug(sessionKey + " get " + listener);
		return listener;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.gcube.portlets.widgets.workspaceuploader.server.upload.
	 * AbstractUploadProgressListener#remove()
	 */
	public void remove() {
		listeners.remove(sessionKey);
		logger.info(sessionKey + " Remove " + this.toString());
		current(sessionKey);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.gcube.portlets.widgets.workspaceuploader.server.upload.
	 * AbstractUploadProgressListener#save()
	 */
	public void save() {
		listeners.put(sessionKey, this);
		logger.debug(sessionKey + " Saved " + this.toString());
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.widgets.workspaceuploader.server.upload.AbstractUploadProgressListener#toString()
	 */
	public String toString() {
		return super.toString();
	}

}
