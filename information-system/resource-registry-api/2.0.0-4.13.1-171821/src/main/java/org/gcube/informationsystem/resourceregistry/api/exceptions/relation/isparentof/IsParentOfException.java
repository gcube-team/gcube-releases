package org.gcube.informationsystem.resourceregistry.api.exceptions.relation.isparentof;

import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;

public class IsParentOfException extends ResourceRegistryException {
	
	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -202306809126357795L;

	public IsParentOfException(String message) {
		super(message);
	}

	public IsParentOfException(Throwable cause) {
		super(cause);
	}

	public IsParentOfException(String message, Throwable cause) {
		super(message, cause);
	}
}
