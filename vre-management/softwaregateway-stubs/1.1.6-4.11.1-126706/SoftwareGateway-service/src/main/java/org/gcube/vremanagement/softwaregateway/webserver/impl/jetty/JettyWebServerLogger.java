package org.gcube.vremanagement.softwaregateway.webserver.impl.jetty;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.mortbay.log.Logger;

/**
 * @author Luca Frosini (ISTI-CNR)
 */
public class JettyWebServerLogger implements Logger {
	/** 
	 * Class logger. 
	 */
	protected static final GCUBELog logger = new GCUBELog(JettyWebServerLogger.class);
	
	/**
	 * {@inheritDoc}
	 */
	public void debug(String arg0, Throwable arg1) {
		logger.debug(arg0,arg1);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void debug(String arg0, Object arg1, Object arg2) {
		logger.debug(arg0);
		logger.debug(arg1);
		logger.debug(arg2);
	}

	/**
	 * {@inheritDoc}
	 */
	public Logger getLogger(String arg0) {
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	public void info(String arg0, Object arg1, Object arg2) {
		logger.info(arg0);
		logger.info(arg1);
		logger.info(arg2);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isDebugEnabled() {
		return logger.isDebugEnabled();
	}

	/**
	 * {@inheritDoc}
	 */
	public void setDebugEnabled(boolean arg0) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void warn(String arg0, Throwable arg1) {
		logger.warn(arg0, arg1);
	}

	/**
	 * {@inheritDoc}
	 */
	public void warn(String arg0, Object arg1, Object arg2) {
		logger.warn(arg0);
		logger.warn(arg1);
		logger.warn(arg2);
	}

}
