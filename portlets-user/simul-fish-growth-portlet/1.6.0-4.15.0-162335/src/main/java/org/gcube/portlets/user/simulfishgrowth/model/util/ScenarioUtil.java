package org.gcube.portlets.user.simulfishgrowth.model.util;

import java.net.URI;
import java.text.SimpleDateFormat;

import javax.ws.rs.core.UriBuilder;

import org.gcube.portlets.user.simulfishgrowth.util.AddGCubeHeaders;
import org.gcube.portlets.user.simulfishgrowth.util.ConnectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import gr.i2s.fishgrowth.model.Scenario;

public class ScenarioUtil extends ConnectionUtils {
	static final String ENTITY = "Scenario";

	public ScenarioUtil(AddGCubeHeaders addGCubeHeaders) {
		super(addGCubeHeaders);
	}

	public void add(Scenario scenario) throws Exception {
		URI uri = UriBuilder.fromPath(ConnectionUtils.endpoint).path(ENTITY).build();
		scenario.setId(addData(uri, scenario));
	}

	public void update(Scenario scenario) throws Exception {
		URI uri = UriBuilder.fromPath(ConnectionUtils.endpoint).path(ENTITY).build();
		updateData(uri, scenario);
	}

	public void delete(long id) throws Exception {
		URI uri = UriBuilder.fromPath(ConnectionUtils.endpoint).path(ENTITY).path(String.valueOf(id)).build();
		deleteData(uri);
	}

	public Scenario getScenario(long id) throws Exception {
		URI uri = UriBuilder.fromPath(ConnectionUtils.endpoint).path(ENTITY).path(String.valueOf(id)).build();
		String json = getData(uri);
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("retrieved ~%s~", json));
		}
		Scenario toRet = new ObjectMapper().readValue(json, Scenario.class);
		return toRet;
	}

	public Scenario executeScenario(long id) throws Exception {
		URI uri = UriBuilder.fromPath(ConnectionUtils.endpoint).path(ENTITY).path("execute").path(String.valueOf(id))
				.build();
		String json = getData(uri);
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("retrieved ~%s~", json));
		}
		Scenario toRet = new ObjectMapper().readValue(json, Scenario.class);
		return toRet;
	}

	public String executeConsumption(Scenario base) throws Exception {
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("executeConsumption"));
		}

		SimpleDateFormat df = new SimpleDateFormat("yyMMdd");
		URI uri = UriBuilder.fromPath(ConnectionUtils.endpoint).path("Scenario").path("execute").path("consumption")
				.path(df.format(base.getStartDate())).path(df.format(base.getTargetDate()))
				.path(String.valueOf((int)(base.getWeight() * 100))).path(String.valueOf(base.getFishNo()))
				.path(String.valueOf(base.getModelerId())).build();
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("invoking %s", uri));
		}
		String json = getData(uri);
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("retrieved ~%s~", json));
		}
		return json;
	}

	private static Log logger = LogFactoryUtil.getLog(ScenarioUtil.class);
}
