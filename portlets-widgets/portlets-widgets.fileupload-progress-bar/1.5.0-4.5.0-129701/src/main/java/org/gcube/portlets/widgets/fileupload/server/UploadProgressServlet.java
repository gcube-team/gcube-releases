package org.gcube.portlets.widgets.fileupload.server;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.gcube.portlets.widgets.fileupload.client.UploadProgressService;
import org.gcube.portlets.widgets.fileupload.shared.event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public final class UploadProgressServlet extends RemoteServiceServlet implements UploadProgressService {

	private static final int EVENT_WAIT = 30 * 1000;
	private static final Logger _log = LoggerFactory.getLogger(UploadProgressServlet.class);

	@Override
	public void initialise() {
		getThreadLocalRequest().getSession(true);
	}

	@Override
	public List<Event> getEvents() {

		HttpSession session = getThreadLocalRequest().getSession();
		UploadProgress uploadProgress = UploadProgress.getUploadProgress(session);

		List<Event> events = null;
		if (null != uploadProgress) {
			if (uploadProgress.isEmpty()) {
				try {
					synchronized (uploadProgress) {
						_log.debug("waiting...");
						uploadProgress.wait(EVENT_WAIT);
					}
				} catch (final InterruptedException ie) {
					_log.debug("interrupted...");
				}
			}

			synchronized (uploadProgress) {
				events = uploadProgress.getEvents();
				uploadProgress.clear();
			}
		}
		return events;
	}
}
