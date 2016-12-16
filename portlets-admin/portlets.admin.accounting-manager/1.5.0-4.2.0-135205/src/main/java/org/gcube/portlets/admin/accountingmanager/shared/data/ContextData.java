package org.gcube.portlets.admin.accountingmanager.shared.data;

import java.io.Serializable;

/**
 * 
 * @author Giancarlo Panichi
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class ContextData implements Serializable {

	private static final long serialVersionUID = -6337819958414362393L;
	private String contextData;

	public ContextData() {
		super();
	}

	public ContextData(String context) {
		super();
		this.contextData = context;
	}

	public String getContextData() {
		return contextData;
	}

	public void setContextData(String contextData) {
		this.contextData = contextData;
	}

	public String getLabel() {
		return contextData;
	}

	public void setLabel(String contextData) {
		this.contextData = contextData;
	}

	@Override
	public String toString() {
		return "ContextData [contextData=" + contextData + "]";
	}

}
