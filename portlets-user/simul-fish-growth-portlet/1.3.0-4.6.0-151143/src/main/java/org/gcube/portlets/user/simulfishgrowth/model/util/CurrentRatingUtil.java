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

import gr.i2s.fishgrowth.model.CurrentRating;

public class CurrentRatingUtil extends ConnectionUtils {
	static final String ENTITY = "CurrentRating";

	public CurrentRatingUtil(AddGCubeHeaders addGCubeHeaders) {
		super(addGCubeHeaders);
	}

	public List<CurrentRating> getCurrentRatings() throws Exception {
		URI uri = UriBuilder.fromPath(ConnectionUtils.endpoint).path(ENTITY).path(ConnectionUtils.ALL).build();
		String json = MOCK_DATA ? MockData.STARS : getData(uri);
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("retrieved ~%s~", json));
		}
		List<CurrentRating> toRet = new ObjectMapper().readValue(json, new TypeReference<List<CurrentRating>>() {
		});

		return toRet;
	}

	private static Log logger = LogFactoryUtil.getLog(CurrentRatingUtil.class);
}
