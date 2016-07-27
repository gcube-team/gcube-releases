package org.cotrix.gcube.stubs;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import com.google.gson.Gson;

public class SessionToken {

	private static final String ENCODING = "UTF-8";
	private static final Gson converter = new Gson();

	private final String id;
	private final String scope;
	private final String origin;

	public SessionToken(String id, String scope, String origin) {
		this.id = id;
		this.scope = scope;
		this.origin = origin;
	}

	public String id() {
		return id;
	}

	public String scope() {
		return scope;
	}

	public String origin() {
		return origin;
	}

	public String encoded() {

		try {

			return URLEncoder.encode(converter.toJson(this), ENCODING);

		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("cannot encode token", e);
		}
	}

	//factory method
	public static SessionToken valueOf(String encoded) {

		try {

			return converter.fromJson(URLDecoder.decode(encoded, ENCODING), SessionToken.class);

		} catch (UnsupportedEncodingException e) {

			throw new RuntimeException("cannot decode token " + encoded + " (see cause) ", e);

		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Token [sessionId=");
		builder.append(id);
		builder.append(", scope=");
		builder.append(scope);
		builder.append(", portalUrl=");
		builder.append(origin);
		builder.append("]");
		return builder.toString();
	}

}
