package org.gcube.portal.databook.shared.ex;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@SuppressWarnings("serial")
public class FeedIDNotFoundException extends Exception {
	private static final Logger _log = LoggerFactory.getLogger(FeedIDNotFoundException.class);
	public FeedIDNotFoundException(String message, String postId) {
		_log.info("The Post having id: " + postId + " is not present in the database: " + message);
	}
}