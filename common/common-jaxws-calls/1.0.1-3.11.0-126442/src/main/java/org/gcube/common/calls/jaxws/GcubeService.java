package org.gcube.common.calls.jaxws;

import javax.xml.namespace.QName;

import org.gcube.common.calls.Call;
import org.gcube.common.calls.jaxws.GcubeServiceBuilderDSL.NameClause;

public class GcubeService<T> {

	private final QName name;
	private final Class<T> type;
	private final Call call = new Call();
	
	/**
	 * Starts the bulding process for a {@link GcubeService}.
	 * @return the service
	 */
	public static NameClause service() {
		return new GcubeServiceBuilder();
	}
	
	public GcubeService(QName name, Class<T> type) {
		this.name=name;
		this.type=type;
	}

	public QName name() {
		return name;
	}

	public Class<T> type() {
		return type;
	}

	public Call call() {
		return call;
	}
}
