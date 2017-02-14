package org.gcube.informationsystem.collector.impl.xmlstorage.exist;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.xml.transform.OutputKeys;


import org.exist.xmldb.XQueryService;

import org.xmldb.api.base.Collection;
import org.xmldb.api.base.CompiledExpression;
import org.xmldb.api.base.ResourceSet;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.informationsystem.collector.impl.xmlstorage.exist.XQuery;


/**
 * XQuery to be executed
 * 
 */
public class XQuery {

	private String query_string = null;

	private static GCUBELog logger = new GCUBELog(XQuery.class.getName());
		
	
	public XQuery(String xquery) {
		this.query_string = xquery;
	}

	public XQuery(FileReader file) throws IOException {
		try {
			this.query_string = this.readFile(file);
		} catch (IOException ioe) {
			logger.error("Unable to read the XQuery file");
			throw new IOException("Unable to read the XQuery file "
					+ ioe.getStackTrace()[0].toString());
		}
	}

	/**
	 * Returns the xquery string
	 */
	public String toString() {
		return this.query_string;
	}

	/**
	 * Executes the query on the given base collection
	 * 
	 * @param col
	 */
	public ResourceSet execute(Collection col) throws Exception {

		logger.info("Executing query on collection " + col.getName());
		
		XQueryService service = (XQueryService) col.getService("XQueryService",	"1.0");

		// set pretty-printing on
		service.setProperty(OutputKeys.INDENT, "yes");
		service.setProperty(OutputKeys.ENCODING, "UTF-8");

		CompiledExpression compiled = service.compile(this.query_string);

		long start = System.currentTimeMillis();

		// execute query and get results in ResourceSet
		ResourceSet result = service.execute(compiled);

		long qtime = System.currentTimeMillis() - start;
		start = System.currentTimeMillis();

		/*Properties outputProperties = new Properties();
		outputProperties.setProperty(OutputKeys.INDENT, "yes");
		SAXSerializer serializer = (SAXSerializer) SerializerPool.getInstance().borrowObject(SAXSerializer.class);
		serializer.setOutput(new OutputStreamWriter(System.out), outputProperties);

		SerializerPool.getInstance().returnObject(serializer); */
		//long rtime = System.currentTimeMillis() - start;
		logger.trace("hits:          " + result.getSize());
		logger.trace("query time:    " + qtime + "ms");
		//logger.info(State.logPrefix + "retrieve time: " + rtime);
		
		return 	result;
	}

	/**
	 * Reads the xquery file and return as string.
	 */
	protected String readFile(FileReader file) throws IOException {
		BufferedReader f = new BufferedReader(file);
		String line;
		StringBuffer xml = new StringBuffer();
		while ((line = f.readLine()) != null)
			xml.append(line + " ");
		f.close();
		return xml.toString();
	}
	
	
}
