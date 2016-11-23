package org.gcube.portlets.user.tokengenerator.shared;

import java.io.Serializable;

/**
 * A qualified token bean.
 * @author Costantino Perciante at ISTI-CNR 
 * (costantino.perciante@isti.cnr.it)
 */
public class QualifiedToken implements Serializable {

	private static final long serialVersionUID = -1258162433456820234L;
	private String qualifier;
	private String token;

	public QualifiedToken(){
		super();
	}

	/** Create a qualified token
	 * @param qualifier
	 * @param token
	 */
	public QualifiedToken(String qualifier, String token) {
		super();
		this.qualifier = qualifier;
		this.token = token;
	}

	public String getQualifier() {
		return qualifier;
	}

	public void setQualifier(String qualifier) {
		this.qualifier = qualifier;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public String toString() {
		return "QualifiedToken [qualifier=" + qualifier + ", token=" + token.substring(0, 5) + "******************"
				+ "]";
	}
}
