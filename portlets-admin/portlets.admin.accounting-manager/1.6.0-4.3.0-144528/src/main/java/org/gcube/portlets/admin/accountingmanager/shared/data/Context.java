package org.gcube.portlets.admin.accountingmanager.shared.data;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class Context implements Serializable {

	private static final long serialVersionUID = -2453517525713556421L;
	private ArrayList<String> contexts;

	public Context() {
		super();
	}

	public Context(ArrayList<String> contexts) {
		super();
		this.contexts = contexts;
	}

	public ArrayList<String> getContexts() {
		return contexts;
	}

	public void setContexts(ArrayList<String> contexts) {
		this.contexts = contexts;
	}

	@Override
	public String toString() {
		return "Context [contexts=" + contexts + "]";
	}

}
