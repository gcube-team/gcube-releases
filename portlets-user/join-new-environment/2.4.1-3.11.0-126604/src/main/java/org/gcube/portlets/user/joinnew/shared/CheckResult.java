package org.gcube.portlets.user.joinnew.shared;

import java.io.Serializable;
/**
 * passed check for the infrastructure
 * @author Massimiliano Assante ISTI-CNR
 * 
 * @version 1.0 Jan 12th 2012
 */
@SuppressWarnings("serial")
public class CheckResult implements Serializable {
	/**
	 * 
	 */
	private CheckType type;
	/**
	 * 
	 */
	private boolean passed;
	/**
	 * 
	 */
	public CheckResult() {
		super();
	}
	/**
	 * 
	 * @param type .
	 * @param passed .
	 */
	public CheckResult(CheckType type, boolean passed) {
		super();
		this.type = type;
		this.passed = passed;
	}
	/**
	 * @return type of check performed
	 */
	public CheckType getType() {
		return type;
	}
	/**
	 * 
	 * @param type .
	 */
	public void setType(CheckType type) {
		this.type = type;
	}
	/**
	 * 
	 * @return if passed
	 */
	public boolean isPassed() {
		return passed;
	}
	/**
	 * 
	 * @param result .
	 */
	public void setPassed(boolean result) {
		this.passed = result;
	}	
	
	public String toString() {
		return "[" + type + ", " + passed + "]";
	}
}
