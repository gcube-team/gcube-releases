package org.gcube.execution.wol.client;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.axis.message.addressing.AttributedURI;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.contexts.GCUBERemotePortTypeContext;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.scope.GCUBEScope.MalformedScopeExpressionException;
import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.resources.gcore.GCoreEndpoint.Profile.Endpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.execution.workfloworchestrationlayerservice.stubs.JdlResource;
import org.gcube.execution.workfloworchestrationlayerservice.stubs.WOLConfig;
import org.gcube.execution.workfloworchestrationlayerservice.stubs.WOLParams;
import org.gcube.execution.workfloworchestrationlayerservice.stubs.WOLResource;
import org.gcube.execution.workfloworchestrationlayerservice.stubs.WorkflowOrchestrationLayerServicePortType;
import org.gcube.execution.workfloworchestrationlayerservice.stubs.service.WorkflowOrchestrationLayerServiceAddressingLocator;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkflowOrchestrationLayerClient {

	private static Logger logger = LoggerFactory.getLogger(WorkflowOrchestrationLayerClient.class);
	
	private static String readFile( String file ) throws IOException {
	    BufferedReader reader = new BufferedReader( new FileReader (file));
	    String         line = null;
	    StringBuilder  stringBuilder = new StringBuilder();
	    String         ls = System.getProperty("line.separator");

	    while( ( line = reader.readLine() ) != null ) {
	        stringBuilder.append( line );
	        stringBuilder.append( ls );
	    }

	    return stringBuilder.toString();
	}
	
	private static String getJDLDescription(String filename) {
		try {
			// Open the file that is the first
			// command line parameter
			FileInputStream fstream = new FileInputStream(filename);
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			// Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				// Print the content on the console
				String[] splits = strLine.split("\\s#\\s");
				if(splits.length == 2 && splits[0].equalsIgnoreCase("jdl"))
				{
					return readFile(splits[1]);
				}
			}
			// Close the input stream
			in.close();
		} catch (Exception e) {// Catch exception if any
			logger.error("Error",e);
		}
		return null;
	}
	
	private static String getScope(String filename) {
		try {
			// Open the file that is the first
			// command line parameter
			FileInputStream fstream = new FileInputStream(filename);
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			// Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				// Print the content on the console
				String[] splits = strLine.split("\\s#\\s");
				if(splits.length == 2 && splits[0].equalsIgnoreCase("scope"))
				{
					return splits[1];
				}
			}
			// Close the input stream
			in.close();
		} catch (Exception e) {// Catch exception if any
			logger.error("Error",e);
		}
		return null;
	}

	private static WOLConfig getConfig(String filename) {
		WOLConfig wc = new WOLConfig();
		try {
			// Open the file that is the first
			// command line parameter
			FileInputStream fstream = new FileInputStream(filename);
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			// Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				// Print the content on the console
				String[] splits = strLine.split("\\s#\\s");
				if(splits.length == 2)
				{
					if(splits[0].equalsIgnoreCase("chokeProgressEvents"))
						wc.setChokeProgressEvents(Boolean.parseBoolean(splits[1]));
					else if(splits[0].equalsIgnoreCase("chokePerformanceEvents"))
						wc.setChokeProgressEvents(Boolean.parseBoolean(splits[1]));
					else if(splits[0].equalsIgnoreCase("passedBy"))
						wc.setPassedBy(Integer.parseInt(splits[1]));
					else if(splits[0].equalsIgnoreCase("queueSupport"))
						wc.setQueueSupport(Boolean.parseBoolean(splits[1]));
					else if(splits[0].equalsIgnoreCase("utilization"))
						wc.setUtilization(Integer.parseInt(splits[1]));
				}
			}
			// Close the input stream
			in.close();
		} catch (Exception e) {// Catch exception if any
			logger.error("Error: " + e.getMessage());
			return null;
		}
		return wc;
	}

	private static List<WOLResource> getWOLResources(String filename, boolean first) {
		List<WOLResource> wrs = new ArrayList<WOLResource>();
		try {
			// Open the file that is the first
			// command line parameter
			FileInputStream fstream = new FileInputStream(filename);
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			// Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				// Print the content on the console
				String[] splits = strLine.split("\\s#\\s");
				if(splits.length >= 3)
				{
					String key = splits[1];
					String path = splits.length==3 ? splits[2] : splits[3];
					WOLResource wr = new WOLResource();
					wr.setResourceKey(key);
					wr.setInMessageBytePayload(loadFile(new File(path)));
					wrs.add(wr);
					if(splits.length==4 && first)
					{
						wrs.addAll(getWOLResources(path, false));
					}
				}
			}
			// Close the input stream
			in.close();
		} catch (Exception e) {// Catch exception if any
			logger.error("Error: " + e.getMessage());
		}
		return wrs;
	}
	
	private static List<JdlResource> getJdlResources(String filename, boolean first) {
		List<JdlResource> jdlrs = new ArrayList<JdlResource>();
		try {
			// Open the file that is the first
			// command line parameter
			FileInputStream fstream = new FileInputStream(filename);
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			// Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				// Print the content on the console
				String[] splits = strLine.split("\\s#\\s");
				if(splits.length == 4 && first)
				{
					String path = splits[3];
					jdlrs.addAll(getJdlResources(path, false));
				}
				else if(splits.length==2 && splits[0].equalsIgnoreCase("jdl"))
				{
					JdlResource jdlr = new JdlResource();
					jdlr.setResourceKey(splits[1]);
					jdlr.setInMessageStringPayload(readFile(splits[1]));
					jdlrs.add(jdlr);
				}
			}
			// Close the input stream
			in.close();
		} catch (Exception e) {// Catch exception if any
			logger.error("Error: " + e.getMessage());
		}
		return jdlrs;
	}
	
	protected static String getWorkflowOrchestrationLayerEndpoint(String scope) throws Exception
	{
		List<String> endpoints = new ArrayList<String>();
		
		ScopeProvider.instance.set(scope);

		SimpleQuery query = queryFor(GCoreEndpoint.class);

		query.addCondition("$resource/Profile/ServiceClass/text() eq 'Execution'").addCondition(
				"$resource/Profile/ServiceName/text() eq 'WorkflowOrchestrationLayerService'");

		DiscoveryClient<GCoreEndpoint> client = clientFor(GCoreEndpoint.class);

		List<GCoreEndpoint> resources = client.submit(query);

		for (GCoreEndpoint resource : resources) {
			
			for (Endpoint endpoint : resource.profile().endpoints()) {
				endpoints.add(endpoint.uri().toString());
			}
		}
		String endpoint = endpoints.get((new Random()).nextInt(endpoints.size()));
		logger.info("Returning "+endpoint+" from available: "+endpoints.toString());
		return endpoint;
	}
	
	private static byte[] loadFile(File file) throws IOException {
	    InputStream is = new FileInputStream(file);
 
	    long length = file.length();
	    if (length > Integer.MAX_VALUE) {
	        // File is too large
	    }
	    byte[] bytes = new byte[(int)length];
	    
	    int offset = 0;
	    int numRead = 0;
	    while (offset < bytes.length
	           && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
	        offset += numRead;
	    }
 
	    if (offset < bytes.length) {
	        throw new IOException("Could not completely read file "+file.getName());
	    }
	    is.close();
	    return bytes;
	}

	private static void printHelp() {
		StringBuilder buf=new StringBuilder();
		buf.append("Usage:\n");
		buf.append("One argument is needed\n");
		buf.append("1) the path of the resource file. The syntax of the resource file is the following:\n");
		buf.append("\tscope : <the scope to use>\n");
		buf.append("\tjdl : <path to the jdl file>\n");
		buf.append("\tchokeProgressEvents : <true | false> (depending on whether you want to omit progress reporting)\n");
		buf.append("\tchokePerformanceEvents : <true | false> (depending on whether you want to omit performance reporting)\n");
		buf.append("\tqueueSupport : <true | false>\n");
		buf.append("\tutilization: <float>\n");
		buf.append("\tpassedBy : <int>\n");
		buf.append("\t<name of resource as mentioned in jdl> : <local | ss | url depending on where to access the payload from> : <the path / id / url to retrieve the paylaod from>\n");
		buf.append("\t<name of resource as mentioned in jdl> : <local | ss | url depending on where to access the payload from> : <the paath / id / url to retrieve the paylaod from>\n");
		buf.append("\t[...]");
		System.out.println(buf.toString());
	}
	
	public static void main(String[] args) throws MalformedScopeExpressionException, Exception {
		if(args.length!=1)
		{
			printHelp();
			return;
		}
		
		WOLParams wp = new WOLParams();
		String filename = args[0];
		
		String scope = getScope(filename);
		
		wp.setJdlDescription(getJDLDescription(filename));
		wp.setConfig(getConfig(filename));
		List<WOLResource> list = getWOLResources(filename,true);
		wp.setWolResources(list.toArray(new WOLResource[list.size()]));
		List<JdlResource> jdllist = getJdlResources(filename,true);
		wp.setJdlResources(jdllist.toArray(new JdlResource[jdllist.size()]));

		EndpointReferenceType endpoint = new EndpointReferenceType();
		endpoint.setAddress(new AttributedURI(getWorkflowOrchestrationLayerEndpoint(scope)));
		
		WorkflowOrchestrationLayerServicePortType stub = new WorkflowOrchestrationLayerServiceAddressingLocator().getWorkflowOrchestrationLayerServicePortTypePort(endpoint);
		stub = GCUBERemotePortTypeContext.getProxy(stub,GCUBEScope.getScope(scope));
		
		String executionID = stub.adaptWOL(wp);
		
		System.out.println(executionID);
	}

}
