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

import gr.i2s.fishgrowth.model.Site;
import gr.i2s.fishgrowth.model.Usage;

public class SiteUtil  extends ConnectionUtils {
	static final String ENTITY = "Site";

	public SiteUtil(AddGCubeHeaders addGCubeHeaders) {
		super(addGCubeHeaders);
	}

	public void add(Site site) throws Exception {
		URI uri = UriBuilder.fromPath(ConnectionUtils.endpoint).path(ENTITY).build();
		site.setId(addData(uri, site));
	}

	public void update(Site site) throws Exception {
		URI uri = UriBuilder.fromPath(ConnectionUtils.endpoint).path(ENTITY).build();
		updateData(uri, site);
	}

	public void delete(long id) throws Exception {
		URI uri = UriBuilder.fromPath(ConnectionUtils.endpoint).path(ENTITY).path(String.valueOf(id)).build();
		deleteData(uri);
	}

	public Site getSite(long id) throws Exception {
		URI uri = UriBuilder.fromPath(ConnectionUtils.endpoint).path(ENTITY).path(String.valueOf(id)).build();
		String json = getData(uri);
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("retrieved ~%s~", json));
		}
		Site toRet = new ObjectMapper().readValue(json, Site.class);
		return toRet;
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
	

	private static Log logger = LogFactoryUtil.getLog(SiteUtil.class);
}
