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

import gr.i2s.fishgrowth.model.Sfr;

public class SfrUtil extends ConnectionUtils {
	static final String ENTITY = "Sfr";

	public SfrUtil(AddGCubeHeaders addGCubeHeaders) {
		super(addGCubeHeaders);
	}

	public List<Sfr> getSfrs(long modelerId) throws Exception {
		URI uri = UriBuilder.fromPath(ConnectionUtils.endpoint).path(ENTITY).path(ConnectionUtils.ALL)
				.path(String.valueOf(modelerId)).build();
		String json = getData(uri);
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("retrieved ~%s~", json));
		}
		List<Sfr> toRet = new ObjectMapper().readValue(json, new TypeReference<List<Sfr>>() {
		});

		return toRet;
	}

	public int deleteAll(long id, int idx) throws Exception {
		URI uri = UriBuilder.fromPath(ConnectionUtils.endpoint).path(ENTITY).path(ConnectionUtils.ALL)
				.path(String.valueOf(id)).build();
		deleteData(uri);
		return 0;
	}

	private static Log logger = LogFactoryUtil.getLog(SfrUtil.class);
}
