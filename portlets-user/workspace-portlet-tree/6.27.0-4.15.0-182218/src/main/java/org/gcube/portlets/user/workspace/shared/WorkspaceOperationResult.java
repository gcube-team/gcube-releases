/**
 *
 */
package org.gcube.portlets.user.workspace.shared;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;



/**
 * The Class WorkspaceOperationResult.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 * Oct 8, 2018
 */
public class WorkspaceOperationResult implements Serializable, IsSerializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 4252367709147458665L;
	String operationName;
	boolean operationCompleted;
	String error;


	/**
	 * Instantiates a new workspace operation result.
	 */
	public WorkspaceOperationResult() {
	}


	/**
	 * Instantiates a new workspace operation result.
	 *
	 * @param operationName the operation name
	 * @param operationCompleted the operation completed
	 * @param error the error
	 */
	public WorkspaceOperationResult(
		String operationName, boolean operationCompleted, String error) {

		super();
		this.operationName = operationName;
		this.operationCompleted = operationCompleted;
		this.error = error;
	}



	/**
	 * Gets the operation name.
	 *
	 * @return the operationName
	 */
	public String getOperationName() {

		return operationName;
	}



	/**
	 * Checks if is operation completed.
	 *
	 * @return the operationCompleted
	 */
	public boolean isOperationCompleted() {

		return operationCompleted;
	}



	/**
	 * Gets the error.
	 *
	 * @return the error
	 */
	public String getError() {

		return error;
	}



	/**
	 * Sets the operation name.
	 *
	 * @param operationName the operationName to set
	 */
	public void setOperationName(String operationName) {

		this.operationName = operationName;
	}



	/**
	 * Sets the operation completed.
	 *
	 * @param operationCompleted the operationCompleted to set
	 */
	public void setOperationCompleted(boolean operationCompleted) {

		this.operationCompleted = operationCompleted;
	}



	/**
	 * Sets the error.
	 *
	 * @param error the error to set
	 */
	public void setError(String error) {

		this.error = error;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("WorkpaceOperationResult [operationName=");
		builder.append(operationName);
		builder.append(", operationCompleted=");
		builder.append(operationCompleted);
		builder.append(", error=");
		builder.append(error);
		builder.append("]");
		return builder.toString();
	}



}
