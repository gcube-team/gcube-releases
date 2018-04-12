package org.gcube.portal.databook.shared;

/**
 * @author Massimiliano Assante ISTI-CNR
 * 
 * @version 1.2 October 2012
 */
public enum FeedType {
	JOIN, SHARE, PUBLISH, TWEET, CONNECTED, 
	/**
	 * Special case used when accounting
	 */
	ACCOUNTING, 
	/**
	 * Special case used when a Feed is removed
	 */
	DISABLED;
}

