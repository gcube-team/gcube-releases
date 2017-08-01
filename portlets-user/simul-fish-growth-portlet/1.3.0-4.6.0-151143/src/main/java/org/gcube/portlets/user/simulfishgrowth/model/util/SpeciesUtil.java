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

import gr.i2s.fishgrowth.model.Species;

public class SpeciesUtil  extends ConnectionUtils {
	static final String ENTITY = "Species";

	public SpeciesUtil(AddGCubeHeaders addGCubeHeaders) {
		super(addGCubeHeaders);
	}

	public List<Species> getSpecieses() throws Exception {
		URI uri = UriBuilder.fromPath(ConnectionUtils.endpoint).path(ENTITY).path(ConnectionUtils.ALL).build();
		String json = MOCK_DATA ? MockData.SPECIES : getData(uri);
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("retrieved ~%s~", json));
		}
		List<Species> toRet = new ObjectMapper().readValue(json, new TypeReference<List<Species>>() {
		});

		return toRet;
	}

	private static Log logger = LogFactoryUtil.getLog(SpeciesUtil.class);
}
