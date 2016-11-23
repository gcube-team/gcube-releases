package org.gcube.datatransformation.datatransformationlibrary.programs.metadata.util;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.gcube.rest.commons.resourceawareservice.resources.Resource;
import org.gcube.rest.resourcemanager.discovery.InformationCollector;
import org.gcube.rest.resourcemanager.is.discovery.InformationCollectorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Dimitris Katris, NKUA
 * <p>Retrieves an xslt from IS.</p>
 */
public class XSLTRetriever {
	
	private static Logger log = LoggerFactory.getLogger(XSLTRetriever.class);
	
	private static InformationCollector icollector = InformationCollectorFactory.buildInformationCollector(100, 10, TimeUnit.MINUTES);

	public static String getXSLTFromIS(String xsltID, String scope) throws Exception {
		List<Resource> result;
		if (xsltID.startsWith("$")) {
			String xsltName = xsltID.substring(1);
			log.debug("Going to get XSLT from IS with name "+xsltName+" from scope "+scope);
			result = icollector.getGenericResourcesByName(xsltName, scope);
		} else {
			log.debug("Going to get XSLT from IS with ID "+xsltID+" from scope "+scope);
			result = icollector.getGenericResourcesByID(xsltID, scope);
		}
		
		if (result==null || result.size()==0)
			throw new Exception("Generic resource not found.");

		return result.get(0).getBodyAsString();
	}
}
