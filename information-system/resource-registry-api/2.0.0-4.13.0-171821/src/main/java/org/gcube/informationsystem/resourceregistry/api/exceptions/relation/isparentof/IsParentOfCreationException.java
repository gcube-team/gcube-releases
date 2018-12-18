package org.gcube.informationsystem.resourceregistry.api.exceptions.relation.isparentof;

/**
 * @author Luca Frosini (ISTI - CNR)
 * 
 */
public class IsParentOfCreationException extends IsParentOfException {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = 3455142144592952399L;
	
	public IsParentOfCreationException(String message) {
		super(message);
	}

	public IsParentOfCreationException(Throwable cause) {
		super(cause);
	}

	public IsParentOfCreationException(String message, Throwable cause) {
		super(message, cause);
	}
}
