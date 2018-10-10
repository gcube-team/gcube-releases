package org.gcube.portlets.widgets.wsexplorer.server.stohub;

import org.gcube.vomanagement.usermanagement.model.GCubeUser;

public class AuthorizedUser {
	private GCubeUser user;
	private String token;
	private String context;
	public AuthorizedUser(GCubeUser user, String token, String context) {
		super();
		this.user = user;
		this.token = token;
		this.context = context;
	}
	public GCubeUser getUser() {
		return user;
	}
	public void setUser(GCubeUser user) {
		this.user = user;
	}
	public String getSecurityToken() {
		return token;
	}
	public void setSecurityToken(String token) {
		this.token = token;
	}
	public String getContext() {
		return context;
	}
	public void setContext(String context) {
		this.context = context;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AuthorizedUser [user=");
		builder.append(user);
		builder.append(", token=");
		builder.append(token);
		builder.append(", context=");
		builder.append(context);
		builder.append("]");
		return builder.toString();
	}	
}
