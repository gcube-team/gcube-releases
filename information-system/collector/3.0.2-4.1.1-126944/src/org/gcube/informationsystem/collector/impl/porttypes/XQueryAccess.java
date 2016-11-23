package org.gcube.informationsystem.collector.impl.porttypes;

import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.modules.XMLResource;

import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.porttypes.GCUBEPortType;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.informationsystem.collector.impl.contexts.ICServiceContext;
import org.gcube.informationsystem.collector.impl.xmlstorage.exist.State;
import org.gcube.informationsystem.collector.impl.xmlstorage.exist.XQuery;
import org.gcube.informationsystem.collector.impl.xmlstorage.exist.XMLStorage.XMLStorageNotAvailableException;
import org.gcube.informationsystem.collector.stubs.XQueryExecuteRequest;
import org.gcube.informationsystem.collector.stubs.XQueryExecuteResponse;
import org.gcube.informationsystem.collector.stubs.XQueryFaultType;

/**
 * <em>XQueryAccess</em> portType implementation
 * 
 * @author Manuele Simi (ISTI-CNR)
 * 
 */
public class XQueryAccess extends GCUBEPortType {

    private final GCUBELog logger = new GCUBELog(XQueryAccess.class);

    /** {@inheritDoc} */
    @Override
    protected GCUBEServiceContext getServiceContext() {
	return ICServiceContext.getContext();
    }

    /**
     * Executes a XQuery expression in the XML storage
     * 
     * @param XQueryExpression
     *            the XQuery expression
     * @return a formatted XML document
     * @throws XQueryFaultType
     *             if the execution fails
     */
    public XQueryExecuteResponse XQueryExecute(final XQueryExecuteRequest request) throws XQueryFaultType {
	XQueryExecuteResponse response = new XQueryExecuteResponse();
	try {
	    logger.debug("executing XQuery: " + request.getXQueryExpression());

	    XQuery q = new XQuery(request.getXQueryExpression());
	    ResourceSet result = State.getQueryManager().executeXQuery(q);
	    response.setSize(result.getSize());
	    logger.debug("number of returned documents: " + result.getSize());

	    response.setDataset(buildDataSet(result));

	} catch (XMLStorageNotAvailableException e) {
	    XQueryFaultType fault = new XQueryFaultType();
	    fault.addFaultDetailString("XMLStorage is not currently available for XQuery execution");	    
	    throw fault;
	} catch (Exception e) {
	    XQueryFaultType fault = new XQueryFaultType();
	    fault.addFaultDetailString("Exception when executing the requested XQuery");	    
	    throw fault;
	}
	return response;
    }

    /**
     * Builds the output dataset for XQueryExecute operation
     * @param result the query results
     * @return the dataset to return
     * @throws XMLDBException
     */
    private String buildDataSet(final ResourceSet result) throws Exception {
	final String rootElement = "Resultset";
	final String recordElement = "Record";
	StringBuilder dataset = new StringBuilder();
	dataset.append("<" + rootElement + ">\n");
	for (int i = 0; i < (int) result.getSize(); i++) {
	    XMLResource xmlres = (XMLResource) result.getResource((long) i);
	    dataset.append("<"+recordElement+">\n" + xmlres.getContent() + "\n</"+recordElement+">\n");
	}
	dataset.append("</"+rootElement+">");
	return dataset.toString();
    }

}
