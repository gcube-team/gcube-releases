/**
 *
 */
package org.gcube.portlets.user.performfishanalytics.server.util;

import java.io.InputStream;
import java.util.List;

import org.gcube.portlets.user.performfishanalytics.server.util.dataminer.DMServiceResponse;
import org.gcube.portlets.user.performfishanalytics.server.util.dataminer.DataMinerOutputData;
import org.gcube.portlets.user.performfishanalytics.server.util.xml.WPSParserUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * The Class DataMinerUtil.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 23, 2019
 */
public class DataMinerUtil {

	public static String XML_RESULT_ROOT_EXCEPTION = "wps:ExceptionReport";

	protected static Logger log = LoggerFactory.getLogger(DataMinerUtil.class);


	public static DMServiceResponse parseResult(String httpRequestURL, String xml) throws Exception {

		InputStream wpsResponseStream = WPSParserUtil.stringToInputStream(xml);
		Document doc = WPSParserUtil.inputStreamToW3CDocument(wpsResponseStream);
		Element root = doc.getDocumentElement();
		String rootName = root.getNodeName();
		log.info("Root name: "+rootName);
		if(root.getNodeName().compareToIgnoreCase(XML_RESULT_ROOT_EXCEPTION) == 0){
			return new DMServiceResponse(true, httpRequestURL, xml, null);
		}else {
			List<DataMinerOutputData> listResponse = WPSParserUtil.getListDataMinerOutputDataFromWPSResponse(doc);
			return new DMServiceResponse(false, httpRequestURL, xml, listResponse);
		}

	}

}
