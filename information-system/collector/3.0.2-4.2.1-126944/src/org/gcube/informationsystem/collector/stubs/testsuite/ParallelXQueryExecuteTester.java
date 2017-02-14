package org.gcube.informationsystem.collector.stubs.testsuite;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gcube.common.core.contexts.GCUBERemotePortTypeContext;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBEClientLog;
import org.gcube.informationsystem.collector.stubs.XQueryAccessPortType;
import org.gcube.informationsystem.collector.stubs.XQueryExecuteRequest;
import org.gcube.informationsystem.collector.stubs.XQueryExecuteResponse;
import org.gcube.informationsystem.collector.stubs.XQueryFaultType;
import org.gcube.informationsystem.collector.stubs.service.XQueryAccessServiceLocator;

/**
 * Tester for <em>XQueryExecute</em> operation of the <em>gcube/informationsystem/collector/XQueryAccess</em> portType
 * 
 * @author Manuele Simi (ISTI-CNR)
 * 
 */
public class ParallelXQueryExecuteTester {    

    
    private static GCUBEClientLog logger = new GCUBEClientLog(ParallelXQueryExecuteTester.class);
    /**
     * @param args 
     *  <ol>
     *   <li> IC host
     *   <li> IC port
     *   <li> Caller Scope
     *   <li> File including the XQuery to submit
     *  </ol>
     */
    public static void main(String[] args) {

	if (args.length != 5) {
	    logger.fatal("Usage: XQueryExecuteTester <host> <port> <Scope> <XQueryExpressionFile> <parallelism>" );
	    return;
	}
	String portTypeURI = "http://"+args[0]+":"+ args[1]+"/wsrf/services/gcube/informationsystem/collector/XQueryAccess";
	
	XQueryAccessPortType tempPort = null;
	try {
	    tempPort = new XQueryAccessServiceLocator().getXQueryAccessPortTypePort(new URL(portTypeURI));
	    tempPort = GCUBERemotePortTypeContext.getProxy(tempPort, GCUBEScope.getScope(args[2]));
	} catch (Exception e) {
	    logger.error("",e);
	}
	final XQueryAccessPortType port = tempPort;
	final String queryFile = args[3]; 
	final String scopeName = args[2];
	for (int i=1; i <= Integer.valueOf(args[4]); i++) {
	    new Thread() {

		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
		    XQueryExecuteRequest request = new XQueryExecuteRequest();
	        	request.setXQueryExpression(readQuery(queryFile));
	        	try {
	        	    logger.info(this.getId() + ": Submitting query in scope " + GCUBEScope.getScope(scopeName).getName() + "....");
	        	    XQueryExecuteResponse response = port.XQueryExecute(request);
	        	    logger.info(this.getId() + ": Number of returned records: " + response.getSize());
	        	    logger.info(this.getId() + ": Received dataset: \n" + response.getDataset());
	        
	        	    Pattern p = Pattern.compile("<Record>(.*?)</Record>", Pattern.DOTALL);
	        	    Matcher m = p.matcher(response.getDataset());
	        	    while (m.find()) {
	        		logger.debug(m.group(1));		
	        	    }
	        	    
	        	} catch (XQueryFaultType e) {
	        	    logger.error("XQuery Fault Error received", e);	    	    
	        	} catch (RemoteException e) {
	        	    logger.error(e);	    
	        	}
		}
		
	    }.start();
        	
	}
    }
    
    private static String readQuery(final String filename) {
	String queryString = null;
	try {
	    BufferedReader input =  new BufferedReader(new FileReader(filename));
	    StringBuilder contents = new StringBuilder();
	    String line;
	    while (( line = input.readLine()) != null){
	          contents.append(line);
	          contents.append(System.getProperty("line.separator"));
	    }
	    input.close();
	    queryString = contents.toString();
	} catch (FileNotFoundException e1) {
	    logger.fatal("invalid file: " + filename);
	} catch (IOException e) {
	    logger.fatal("an error occurred when reading " + filename);
	}
	return queryString;

    }

}
