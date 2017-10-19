package org.gcube.data.simulfishgrowthdata.util;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.resources.discovery.icclient.ICFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccessPointer {
	private static final Logger logger = LoggerFactory.getLogger(AccessPointer.class);
	String mName;
	String mAdditionalCondition;

	public AccessPointer(String name) {
		mName = name;
	}

	public AccessPoint getIt() throws Exception {
		AccessPoint toRet = null;
		try {
			SimpleQuery query = ICFactory.queryFor(ServiceEndpoint.class);
			query.addCondition(String.format("$resource/Profile/Name/text() eq '%s'", mName));
			if (mAdditionalCondition != null && !mAdditionalCondition.isEmpty()) {
				query.addCondition(mAdditionalCondition);
			}
			query.setResult("<accesspoint><id>{$resource/ID/text()}</id>{$resource/Profile/AccessPoint}</accesspoint>");
			DiscoveryClient<AccessPointResult> client = ICFactory.clientFor(AccessPointResult.class);
			List<AccessPointResult> results = client.submit(query);
			if (logger.isTraceEnabled())
				logger.trace(String.format("queried [%s] items", results.size()));
			if (results != null && !results.isEmpty()) {
				toRet = results.get(0).ap;
			}
		} catch (Exception e) {
			throw new Exception(String.format("Error getting access point [%s]", mName), e);
		}
		return toRet;

	}

	public AccessPointer addCondition(String condition) {
		mAdditionalCondition = condition;
		return this;
	}

	@XmlRootElement(name = "accesspoint")
	static class AccessPointResult {

		@XmlElement(name = "id")
		String id;

		@XmlElementRef
		AccessPoint ap;

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("AccessPointResult [").append("id=").append(id).append(", ap=").append(ap).append("]");
			return builder.toString();
		}

	}
}
