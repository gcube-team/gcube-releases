package org.gcube.portlets.user.simulfishgrowth.model.util;

import org.gcube.portlets.user.simulfishgrowth.util.AddGCubeHeaders;
import org.gcube.portlets.user.simulfishgrowth.util.ConnectionUtils;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class SimilarSiteUtil extends ConnectionUtils {
	static final String ENTITY = "SimilarSite";

	public SimilarSiteUtil(AddGCubeHeaders addGCubeHeaders) {
		super(addGCubeHeaders);
	}

	private static Log logger = LogFactoryUtil.getLog(SimilarSiteUtil.class);
}
