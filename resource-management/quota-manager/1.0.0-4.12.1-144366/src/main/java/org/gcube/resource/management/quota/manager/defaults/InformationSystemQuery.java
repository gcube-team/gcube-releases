package org.gcube.resource.management.quota.manager.defaults;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;

import org.gcube.common.resources.gcore.GenericResource;
import org.gcube.resource.management.quota.library.quotedefault.QuotaDefault;
import org.gcube.resource.management.quota.library.quotedefault.QuotaDefaultList;
import org.gcube.resource.management.quota.manager.util.Constants;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.resources.discovery.icclient.ICFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * InformationSystemQuery
 *  
 * @author Alessandro Pieve (alessandro.pieve@isti.cnr.it)
 *
 */
public class InformationSystemQuery {

	private static Logger log = LoggerFactory
			.getLogger(InformationSystemQuery.class);

	private List<QuotaDefault> listQuotaDefault=new ArrayList<QuotaDefault>();


	/**
	 * Query at information System for retrive a list quote default
	 */
	public InformationSystemQuery(){
		SimpleQuery query = ICFactory.queryFor(GenericResource.class);
		query.addCondition(
				"$resource/Profile/SecondaryType/text() eq '"
						+ Constants.QUOTA_CATEGORY + "'")
						.addCondition(
								"$resource/Profile/Name/text() eq '"
										+ Constants.QUOTA_NAME + "'")
										.setResult("$resource");

		DiscoveryClient<GenericResource> client = ICFactory
				.clientFor(GenericResource.class);
		List<GenericResource> quotaResources = client.submit(query);
		QuotaDefaultList quotalist=null;
		if (quotaResources.size()>0){
			String resource=quotaResources.get(0).profile().bodyAsString();
			try {
				JAXBContext jaxbContext = JAXBContext.newInstance(QuotaDefaultList.class);
				quotalist =  (QuotaDefaultList) jaxbContext.createUnmarshaller().unmarshal(new StringReader(resource.toString()));
				listQuotaDefault=quotalist.getQuotaDefaultList();				
				log.debug("From IS found a quota default:{}",quotalist.toString());

			}catch (Exception e) {
				log.error("--:{}",e.getLocalizedMessage());
				e.printStackTrace();
			}

		}
	}

	public List<QuotaDefault> getListQuotaDefault() {	
		return listQuotaDefault;
	}
}
