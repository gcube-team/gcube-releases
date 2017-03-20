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

import gr.i2s.fishgrowth.model.ModelerFull;

public class ModelerFullUtil extends ModelerUtil {
	static final String ENTITY = "ModelerFull";

	public ModelerFullUtil(AddGCubeHeaders addGCubeHeaders) {
		super(addGCubeHeaders);
	}

	public ModelerFull getModelerFull(long id) throws Exception {
		URI uri = UriBuilder.fromPath(ConnectionUtils.endpoint).path(ENTITY).path(String.valueOf(id)).build();
		String json = MOCK_DATA ? MockData.MODELER_SINGLE : getData(uri);
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("retrieved ~%s~", json));
		}
		ModelerFull toRet = new ObjectMapper().readValue(json, ModelerFull.class);
		return toRet;
	}

	public List<ModelerFull> getModelerFulls(String ownerId, int start, int end) throws Exception {
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("getModelerFulls ownerId [%s] start [%s] end [%s]", ownerId, start, end));
		}
		URI uri = UriBuilder.fromPath(ConnectionUtils.endpoint).path(ENTITY).path(ConnectionUtils.ALL).path(ownerId)
				.path(String.valueOf(start)).path(String.valueOf(end)).build();
		String json = MOCK_DATA ? MockData.MODELERS_FULL : getData(uri);
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("retrieved ~%s~", json));
		}
		List<ModelerFull> toRet = new ObjectMapper().readValue(json, new TypeReference<List<ModelerFull>>() {
		});

		return toRet;
	}

	public List<ModelerFull> getModelerFulls(String ownerId) throws Exception {
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("getModelerFulls ownerId [%s]", ownerId));
		}
		URI uri = UriBuilder.fromPath(ConnectionUtils.endpoint).path(ENTITY).path(ConnectionUtils.ALL).path(ownerId)
				.build();
		String json = MOCK_DATA ? MockData.MODELERS_FULL : getData(uri);
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("retrieved ~%s~", json));
		}
		List<ModelerFull> toRet = new ObjectMapper().readValue(json, new TypeReference<List<ModelerFull>>() {
		});

		return toRet;
	}

	public int getModelerFullCount(String ownerId) throws Exception {
		URI uri = UriBuilder.fromPath(ConnectionUtils.endpoint).path(ENTITY).path("count").path(ownerId).build();
		String result = MOCK_DATA ? "10" : getData(uri);
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("retrieved ~%s~", result));
		}
		return Integer.parseInt(result);
	}

	private static Log logger = LogFactoryUtil.getLog(ModelerFullUtil.class);
}
