package org.gcube.datacatalogue.grsf_manage_widget.shared.ex;


/**
 * This is thrown when the Manage product is pushed on a product that has
 * a Record Type field of Source or none.
 * @author Costantino Perciante at ISTI-CNR 
 * (costantino.perciante@isti.cnr.it) 
 */
public class NoGRSFRecordException extends Exception {

	private static final long serialVersionUID = 721315478405659218L;
	private String errorMessage;

	public NoGRSFRecordException() {
		super();
	}

	public NoGRSFRecordException(String errorMessage) {
		super();
		this.errorMessage = errorMessage;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

}
