package org.gcube.portlets.user.tokengenerator.shared;

import java.io.Serializable;

/**
 * This bean contains the user's username, user's token and the current context information.
 * It also contains the label for a qualified token or the identifier for application token.
 * The type is specified by the enumerators in {@link org.gcube.portlets.user.tokengenerator.shared.TokenType}
 * @author Costantino Perciante costantino.perciante@isti.cnr.it
 */
@SuppressWarnings("serial")
public class TokenBean implements Serializable {

	private TokenType type;
	private String username;
	private String token;
	private String context; // e.g., /gcube/devsec/devVRE
	private String contextName; // devVRE
	private String qualifier;

	public TokenBean() {
		super();
	}

	/**
	 * Build a token bean
	 * @param type (standard, qualfied, per application)
	 * @param username the owner
	 * @param token the token
	 * @param context the context in which it is valid
	 * @param qualifier the label for a qualified token or the identifier for application token
	 */
	public TokenBean(TokenType type, String username, String token,
			String context, String contextName, String qualifier) {
		super();
		this.type = type;
		this.username = username;
		this.token = token;
		this.context = context;
		this.qualifier = qualifier;
		this.contextName = contextName;
	}

	public TokenType getType() {
		return type;
	}

	public void setType(TokenType type) {
		this.type = type;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public String getQualifier() {
		return qualifier;
	}

	public void setQualifier(String qualifier) {
		this.qualifier = qualifier;
	}

	public String getContextName() {
		return contextName;
	}

	public void setContextName(String contextName) {
		this.contextName = contextName;
	}

	@Override
	public String toString() {
		return "TokenBean [type=" + type + ", username=" + username
				+ ", token=" + token + ", context=" + context
				+ ", contextName=" + contextName + ", qualifier=" + qualifier
				+ "]";
	}
}
