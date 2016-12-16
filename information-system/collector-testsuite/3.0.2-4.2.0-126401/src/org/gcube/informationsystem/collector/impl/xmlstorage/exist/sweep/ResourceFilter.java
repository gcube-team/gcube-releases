package org.gcube.informationsystem.collector.impl.xmlstorage.exist.sweep;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.gcube.informationsystem.collector.impl.xmlstorage.exist.XQuery;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.informationsystem.collector.impl.resources.GCUBEXMLResource;
import org.gcube.informationsystem.collector.impl.xmlstorage.exist.State;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.modules.XMLResource;


/**
 * 
 * A resource filter for registered WS-Resources. It decides when a resource is expired or not, depending on its publication mode and
 * other criteria.
 *
 * @author Manuele Simi (ISTI-CNR)
 *
 */
class ResourceFilter {

    static protected GCUBELog logger = new GCUBELog(ResourceFilter.class);

    static boolean isExpired(GCUBEXMLResource resource) {
	if (resource.getPublicationMode().compareToIgnoreCase("pull") == 0) {
	    return isPullExpired(resource);
	} else if (resource.getPublicationMode().compareToIgnoreCase("push") == 0) {
	    //return isPushExpired(resource);
	}
	// default
	return false;
    }

   /* private static boolean isPushExpired(GCUBEXMLResource resource) {
	logger.trace("Checking push resource...");
	try {
	    String status = getRIStatus(resource.getSourceRunningInstance());
	    logger.trace("The related running instance is in status " + status);
	    if ((status.compareToIgnoreCase("ready") == 0) || (status.compareToIgnoreCase("started") == 0))
		return false;	    
	    return true;
	} catch (Exception e) {
	    logger.warn("Unable to detect if the resource is expired",e);
	    return false;
	} 
    }*/

    static boolean isPullExpired(GCUBEXMLResource resource) {
	logger.trace("Checking pull resource...");
	Calendar now = new GregorianCalendar();
	now.setTimeZone(TimeZone.getTimeZone("GMT"));
	logger.trace("Now is " + now.getTimeInMillis());
	logger.trace("Resource expires at " + resource.getTerminationTime().getTimeInMillis());
	if (now.getTimeInMillis() > resource.getTerminationTime().getTimeInMillis())
	    return true;
	else
	    return false;
    }
    
    private static String readInputStreamAsString(InputStream in) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(in);
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        int result = bis.read();
        while(result != -1) {
          byte b = (byte)result;
          buf.write(b);
          result = bis.read();
        }        
        return buf.toString();
    }
    
    private static String getRIStatus(String id)throws Exception {
	InputStream statusquery = ResourceFilter.class.getResourceAsStream("XQuery-RIStatus.xml");
	XQuery q = new XQuery(readInputStreamAsString(statusquery).replace("$1",id)); 
	ResourceSet result = State.getQueryManager().executeXQuery(q);
	XMLResource xmlres = (XMLResource) result.getResource((long) 0);
	return ((String) xmlres.getContent()).trim();	
    }

}
