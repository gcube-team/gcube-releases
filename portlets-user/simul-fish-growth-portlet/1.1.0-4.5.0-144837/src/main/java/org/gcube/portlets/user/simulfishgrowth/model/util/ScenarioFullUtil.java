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

import gr.i2s.fishgrowth.model.ScenarioFull;

public class ScenarioFullUtil  extends ScenarioUtil {
	static final String ENTITY = "ScenarioFull";

	public ScenarioFullUtil(AddGCubeHeaders addGCubeHeaders) {
		super(addGCubeHeaders);
	}

	public ScenarioFull getScenarioFull(long id) throws Exception {
		URI uri = UriBuilder.fromPath(ConnectionUtils.endpoint).path(ENTITY).path(String.valueOf(id)).build();
		String json = MOCK_DATA ? MockData.ANALYSIS_SINGLE : getData(uri);
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("retrieved ~%s~", json));
		}
		ScenarioFull toRet = new ObjectMapper().readValue(json, ScenarioFull.class);
		return toRet;
	}

	public List<ScenarioFull> getScenarioFulls(String ownerId, int start, int end) throws Exception {
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("getScenarioFulls ownerId [%s] start [%s] end [%s]", ownerId, start, end));
		}
		URI uri = UriBuilder.fromPath(ConnectionUtils.endpoint).path(ENTITY).path(ConnectionUtils.ALL).path(ownerId).path(String.valueOf(start)).path(String.valueOf(end)).build();
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("getScenarioFulls uri [%s]", uri));
		}
		String json = MOCK_DATA ? MockData.ANALYSIS_FULL: getData(uri);
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("retrieved ~%s~", json));
		}
		List<ScenarioFull> toRet = new ObjectMapper().readValue(json, new TypeReference<List<ScenarioFull>>() {
		});

		return toRet;
	}

	public List<ScenarioFull> getScenarioFulls(String ownerId) throws Exception {
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("getScenarioFulls ownerId [%s]", ownerId));
		}
		URI uri = UriBuilder.fromPath(ConnectionUtils.endpoint).path(ENTITY).path(ConnectionUtils.ALL).path(ownerId).build();
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("getScenarioFulls uri [%s]", uri));
		}
		String json = MOCK_DATA ? MockData.ANALYSIS_FULL: getData(uri);
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("retrieved ~%s~", json));
		}
		List<ScenarioFull> toRet = new ObjectMapper().readValue(json, new TypeReference<List<ScenarioFull>>() {
		});

		return toRet;
	}

	public int getScenarioFullCount(String ownerId) throws Exception {
		URI uri = UriBuilder.fromPath(ConnectionUtils.endpoint).path(ENTITY).path("count").path(ownerId).build();
		String result = MOCK_DATA ? "10": getData(uri);
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("retrieved ~%s~", result));
		}
		return Integer.parseInt(result);
	}

	private static Log logger = LogFactoryUtil.getLog(ScenarioFullUtil.class);
}
