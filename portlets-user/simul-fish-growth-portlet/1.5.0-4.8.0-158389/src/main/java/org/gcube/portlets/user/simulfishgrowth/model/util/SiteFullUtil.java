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

import gr.i2s.fishgrowth.model.SiteFull;

public class SiteFullUtil extends SiteUtil {
	static final String ENTITY = "SiteFull";

	public SiteFullUtil(AddGCubeHeaders addGCubeHeaders) {
		super(addGCubeHeaders);
	}

	public SiteFull getSiteFull(long id) throws Exception {
		URI uri = UriBuilder.fromPath(ConnectionUtils.endpoint).path(ENTITY).path(String.valueOf(id)).build();
		String json = MOCK_DATA ? MockData.SITE_SINGLE : getData(uri);
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("retrieved ~%s~", json));
		}
		SiteFull toRet = new ObjectMapper().readValue(json, SiteFull.class);
		return toRet;
	}

	public List<SiteFull> getSiteFulls(String ownerId, int start, int end) throws Exception {
		URI uri = UriBuilder.fromPath(ConnectionUtils.endpoint).path(ENTITY).path(ConnectionUtils.ALL).path(ownerId)
				.path(String.valueOf(start)).path(String.valueOf(end)).build();
		String json = MOCK_DATA ? MockData.SITES_FULL : getData(uri);
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("retrieved ~%s~", json));
		}
		List<SiteFull> toRet = new ObjectMapper().readValue(json, new TypeReference<List<SiteFull>>() {
		});

		return toRet;
	}

	public List<SiteFull> getSiteFulls(String ownerId) throws Exception {
		URI uri = UriBuilder.fromPath(ConnectionUtils.endpoint).path(ENTITY).path(ConnectionUtils.ALL).path(ownerId)
				.build();
		String json = MOCK_DATA ? MockData.SITES_FULL : getData(uri);
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("retrieved ~%s~", json));
		}
		List<SiteFull> toRet = new ObjectMapper().readValue(json, new TypeReference<List<SiteFull>>() {
		});

		return toRet;
	}

	public int getSiteFullCount(String ownerId) throws Exception {
		URI uri = UriBuilder.fromPath(ConnectionUtils.endpoint).path(ENTITY).path(ConnectionUtils.COUNT).path(ownerId).build();
		String result = MOCK_DATA ? "10" : getData(uri);
		return Integer.parseInt(result);
	}

	private static Log logger = LogFactoryUtil.getLog(SiteFullUtil.class);
}
