package org.gcube.portlets.user.simulfishgrowth.model.util;

import java.net.URI;
import java.util.List;

import javax.ws.rs.core.UriBuilder;

import org.gcube.portlets.user.simulfishgrowth.util.AddGCubeHeaders;
import org.gcube.portlets.user.simulfishgrowth.util.ConnectionUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import gr.i2s.fishgrowth.model.Modeler;
import gr.i2s.fishgrowth.model.Usage;

public class ModelerUtil extends ConnectionUtils {

	static final String ENTITY = "Modeler";

	public ModelerUtil(AddGCubeHeaders addGCubeHeaders) {
		super(addGCubeHeaders);
	}

	public void add(Modeler modeler) throws Exception {
		URI uri = UriBuilder.fromPath(ConnectionUtils.endpoint).path(ENTITY).build();
		modeler.setId(addData(uri, modeler));
	}

	public void update(Modeler modeler) throws Exception {
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("updating %s", modeler));
		}
		URI uri = UriBuilder.fromPath(ConnectionUtils.endpoint).path(ENTITY).build();
		updateData(uri, modeler);
	}

	public void delete(long id) throws Exception {
		URI uri = UriBuilder.fromPath(ConnectionUtils.endpoint).path(ENTITY).path(String.valueOf(id)).build();
		deleteData(uri);
	}

	public Modeler getModeler(long id) throws Exception {
		URI uri = UriBuilder.fromPath(ConnectionUtils.endpoint).path(ENTITY).path(String.valueOf(id)).build();
		String json = getData(uri);
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("retrieved ~%s~", json));
		}
		Modeler toRet = new ObjectMapper().readValue(json, Modeler.class);
		return toRet;
	}

	public void cleanKPIs(long id) throws Exception {
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("clearing KPIs for model %s", id));
		}
		URI uri = UriBuilder.fromPath(ConnectionUtils.endpoint).path(ENTITY).path("kpi").path(String.valueOf(id))
				.build();
		deleteData(uri);

	}
	
	public List<Usage> getUsage(String ownerId) throws Exception {
		URI uri = UriBuilder.fromPath(ConnectionUtils.endpoint).path(ENTITY).path(USAGE).path(ownerId).build();
		String json = getData(uri);
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("retrieved ~%s~", json));
		}
		List<Usage> toRet = new ObjectMapper().readValue(json, new TypeReference<List<Usage>>() {
		});
		return toRet;
	}

	private static Log logger = LogFactoryUtil.getLog(ModelerUtil.class);
}
