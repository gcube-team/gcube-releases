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

import java.io.Serializable;
import java.util.Date;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a File Upload Listener that is used by Apache Commons File Upload to
 * monitor the progress of the uploaded file.
 *
 * This object and its attributes have to be serializable because Google
 * App-Engine uses dataStore and memCache to store session objects.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it Sep 8, 2015
 *
 */
public class UploadProgressListener extends AbstractUploadProgressListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7757392979344200978L;

	private static Logger logger = LoggerFactory.getLogger(AbstractUploadProgressListener.class);

	private static int noDataTimeout = 20000;

	private static final int WATCHER_INTERVAL = 2000;

	/**
	 * Sets the no data timeout.
	 *
	 * @param i
	 *            the new no data timeout
	 */
	public static void setNoDataTimeout(int i) {
		noDataTimeout = i;
	}

	/**
	 * A class which is executed in a new thread, so its able to detect when an
	 * upload process is frozen and sets an exception in order to be canceled.
	 * This doesn't work in Google application engine
	 *
	 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it Sep 8,
	 *         2015
	 */
	public class TimeoutWatchDog extends Thread implements Serializable {
		/**
	 * 
	 */
		private static final long serialVersionUID = -6958907286385617168L;
		AbstractUploadProgressListener listener;
		private long lastBytesRead = 0L;
		private long lastData = (new Date()).getTime();

		/**
		 * Instantiates a new timeout watch dog.
		 *
		 * @param l
		 *            the l
		 */
		public TimeoutWatchDog(AbstractUploadProgressListener l) {
			listener = l;
		}

		/**
		 * Cancel.
		 */
		public void cancel() {
			listener = null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			try {
				Thread.sleep(WATCHER_INTERVAL);
			} catch (InterruptedException e) {
				if (listener != null) {
					if (listener.getBytesRead() > 0
							&& listener.getPercentage() >= 100
							|| listener.isCanceled()) {
						logger.debug(sessionKey
								+ " TimeoutWatchDog: upload process has finished, stoping watcher");
						listener = null;
					} else {
						// if (isFrozen()) {
						// logger.info( sessionId +
						// " TimeoutWatchDog: the recepcion seems frozen: " +
						// listener.getBytesRead() + "/" +
						// listener.getContentLength() + " bytes ("
						// + listener.getPercentage() + "%) ");
						// exception = new
						// UploadTimeoutException("No new data received after "
						// + noDataTimeout / 1000 + " seconds");
						// } else {
						run();
					}
				}
			}
		}

		/**
		 * Checks if is frozen.
		 *
		 * @return true, if is frozen
		 */
		private boolean isFrozen() {
			long now = (new Date()).getTime();
			if (bytesRead > lastBytesRead) {
				lastData = now;
				lastBytesRead = bytesRead;
			} else if (now - lastData > noDataTimeout) {
				return true;
			}
			return false;
		}
	}

	/**
	 * Current.
	 *
	 * @param request
	 *            the request
	 * @param clientUploadKey
	 *            the client upload key
	 * @return the abstract upload progress listener
	 */
	public static AbstractUploadProgressListener current(HttpSession session,
			String clientUploadKey) {
		String sessionKey = getSessionKey(session.getId(), clientUploadKey);
		return (AbstractUploadProgressListener) session
				.getAttribute(sessionKey);
	}
	
	private TimeoutWatchDog watcher = null;

	/**
	 * Instantiates a new upload listener.
	 *
	 * @param request
	 *            the request
	 * @param clientUploadKey
	 *            the client upload key
	 * @param percentageOffset
	 *            the percentage offset
	 * @param completePercentage
	 *            the complete percentage
	 */
	public UploadProgressListener(HttpSession session, String clientUploadKey,
			int percentageOffset, double completePercentage) {
		super(session, clientUploadKey, percentageOffset, completePercentage);
		startWatcher();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gwtupload.server.AbstractUploadListener#remove()
	 */
	/**
	 * Removes the.
	 */
	public void remove() {
		logger.debug(sessionKey + " removing: " + toString());
		if (getSession() != null) {
			getSession().removeAttribute(sessionKey);
			logger.info("Removed from session: " + toString());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gwtupload.server.AbstractUploadListener#save()
	 */
	/**
	 * Save.
	 */
	public void save() {
		if (getSession() != null) {
			getSession().setAttribute(sessionKey, this);
			logger.info("Added in session: " + toString());
		}
		
		logger.debug(sessionKey + " save listener " + toString());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gwtupload.server.AbstractUploadListener#update(long, long, int)
	 */
	@Override
	public void update(long done, long total, int item) {
		super.update(done, total, item);
		if (getPercentage() >= 100) {
			stopWatcher();
		}
	}

	/**
	 * Start watcher.
	 */
	private void startWatcher() {
		if (watcher == null) {
			try {
				watcher = new TimeoutWatchDog(this);
				watcher.start();
			} catch (Exception e) {
				logger.error(sessionKey + " unable to create watchdog: "
						+ e.getMessage());
			}
		}
	}

	/**
	 * Stop watcher.
	 */
	private void stopWatcher() {
		if (watcher != null) {
			watcher.cancel();
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UploadProgressListener [watcher=");
		builder.append(watcher);
		builder.append(super.toString());
		return builder.toString();
	}
}
