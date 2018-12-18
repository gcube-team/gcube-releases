package org.gcube.data.simulfishgrowthdata.util;

import java.util.ArrayList;
import java.util.List;

import org.gcube.data.simulfishgrowthdata.model.verify.EntityVerify.VerifyException;

public class UserFriendlyException extends Exception {

	public UserFriendlyException() {
		super();
	}

	public UserFriendlyException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public UserFriendlyException(String message, Throwable cause) {
		super(message, cause);
	}

	public UserFriendlyException(String message) {
		super(message);
	}

	public UserFriendlyException(Throwable cause) {
		super(cause);
	}

	public String getTrace(String sep) {
		String toRet = getMessage();
		if (getCause() != null && getCause() instanceof VerifyException) {
			toRet = toRet + sep + ((VerifyException) getCause()).getTrace(sep);
		}
		return toRet;

	}

	public List<String> getTrace() {
		List<String> toRet = new ArrayList<String>();
		Throwable cause = this;
		while (cause != null) {
			if (cause instanceof UserFriendlyException) {
				toRet.add(cause.getMessage());
			} else {
				toRet.add("(" + cause.getMessage() + ")");
			}
			cause = cause.getCause();
		}

		return toRet;

	}

	static public List<String> getFriendlyTraceFrom(Throwable e) {
		List<String> toRet = new ArrayList<String>();
		Throwable cause = e;
		while (cause != null) {
			if (cause instanceof UserFriendlyException) {
				toRet.add(cause.getMessage());
			} else {
				toRet.add("(" + cause.getMessage() + ")");
			}
			cause = cause.getCause();
		}

		return toRet;

	}

}
