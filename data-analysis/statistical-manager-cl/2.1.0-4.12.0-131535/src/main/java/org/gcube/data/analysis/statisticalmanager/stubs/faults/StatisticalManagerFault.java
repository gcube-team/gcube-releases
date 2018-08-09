package org.gcube.data.analysis.statisticalmanager.stubs.faults;

import javax.xml.ws.WebFault;

@WebFault(name="GCUBEFault",targetNamespace="http://gcube-system.org/namespaces/common/core/faults")
public class StatisticalManagerFault extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 978514657530057984L;

	/**
	 * 
	 */

	
	private StatisticalManagerFaultBean faultInfo;
	
	public StatisticalManagerFault() {
		super();
		// TODO Auto-generated constructor stub
	}

	public StatisticalManagerFault(String arg0, Throwable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	public StatisticalManagerFault(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public StatisticalManagerFault(Throwable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public StatisticalManagerFaultBean getFaultInfo() {
		return faultInfo;
	}

	public StatisticalManagerFault(String message,
			StatisticalManagerFaultBean faultInfo) {
		super(message);
		this.faultInfo = faultInfo;
	}

	public StatisticalManagerFault(Throwable cause,
			StatisticalManagerFaultBean faultInfo) {
		super(cause);
		this.faultInfo = faultInfo;
	}

	public StatisticalManagerFault(String message, Throwable cause,
			StatisticalManagerFaultBean faultInfo) {
		super(message, cause);
		this.faultInfo = faultInfo;
	}
	
	
}
