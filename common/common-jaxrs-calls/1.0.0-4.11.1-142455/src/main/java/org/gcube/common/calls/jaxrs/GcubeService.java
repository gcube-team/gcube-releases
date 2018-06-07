package org.gcube.common.calls.jaxrs;

import javax.xml.namespace.QName;

import org.gcube.common.calls.Call;
import org.gcube.common.calls.jaxrs.GcubeServiceBuilderDSL.NameClause;

public class GcubeService {

	private final QName name;
	private String path;
	private final Call call = new Call();
	
	/**
	 * Starts the bulding process for a {@link GcubeService}.
	 * @return the service
	 */
	public static NameClause service() {
		return new GcubeServiceBuilder();
	}
	
	public GcubeService(QName name, String path) {
		this.name=name;
		this.path = path;
	}

	public String path() {
		return path;
	}

	public QName name() {
		return name;
	}

	public Call call() {
		return call;
	}
}
