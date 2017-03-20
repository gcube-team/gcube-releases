package org.gcube.portlets.user.simulfishgrowth.model.verify;

import org.apache.commons.lang.StringUtils;
import org.gcube.portlets.user.simulfishgrowth.util.UserFriendlyException;

import gr.i2s.fishgrowth.model.EntityWithId;

public abstract class EntityVerify<T extends EntityWithId> {
	protected T entity;

	public EntityVerify(T entity) {
		this.entity = entity;
	}

	public EntityVerify<T> normalise() {
		entity.setDesignation(StringUtils.trimToEmpty(entity.getDesignation()));
		return this;
	}

	public void verify() throws VerifyException {
		VerifyException toThrow = null;
		if (StringUtils.isBlank(entity.getDesignation())) {
			toThrow = new VerifyException("Empty designation", toThrow);
		}
		if (toThrow != null)
			throw toThrow;
	}

	static public class VerifyException extends UserFriendlyException {

		public VerifyException() {
			super();
		}

		public VerifyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
			super(message, cause, enableSuppression, writableStackTrace);
		}

		public VerifyException(String message, Throwable cause) {
			super(message, cause);
		}

		public VerifyException(String message) {
			super(message);
		}

		public VerifyException(Throwable cause) {
			super(cause);
		}

	}
}
