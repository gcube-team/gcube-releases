package org.gcube.informationsystem.resourceregistry.api.exceptions.context;

import org.gcube.informationsystem.resourceregistry.api.exceptions.AlreadyPresent;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.EntityAlreadyPresentException;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class ContextAlreadyPresentException extends EntityAlreadyPresentException implements AlreadyPresent {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -3185950257529005913L;

	public ContextAlreadyPresentException(String message) {
		super(message);
	}

	public ContextAlreadyPresentException(Throwable cause) {
		super(cause);
	}

	public ContextAlreadyPresentException(String message, Throwable cause) {
		super(message, cause);
	}

}
