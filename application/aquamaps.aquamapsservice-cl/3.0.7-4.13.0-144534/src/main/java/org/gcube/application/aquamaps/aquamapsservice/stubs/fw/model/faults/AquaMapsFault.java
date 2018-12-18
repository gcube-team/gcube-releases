package org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.faults;

import javax.xml.ws.WebFault;

@WebFault(name="GCUBEFault",targetNamespace="http://gcube-system.org/namespaces/common/core/faults")
public class AquaMapsFault extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 467980686528541159L;

	public AquaMapsFault() {
		super();
		// TODO Auto-generated constructor stub
	}

	public AquaMapsFault(String arg0, Throwable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	public AquaMapsFault(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public AquaMapsFault(Throwable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	
}
