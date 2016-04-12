package org.gcube.opensearch.opensearchoperator.client;

import gr.uoa.di.madgik.environment.hint.EnvHintCollection;
import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
import gr.uoa.di.madgik.grs.reader.ForwardReader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.gcube.opensearch.opensearchlibrary.OpenSearchConstants;
import org.gcube.opensearch.opensearchoperator.OpenSearchOp;
import org.gcube.opensearch.opensearchoperator.OpenSearchOpConfig;
import org.gcube.opensearch.opensearchoperator.record.OpenSearchRecord;
import org.gcube.opensearch.opensearchoperator.resource.LocalOpenSearchResource;
import org.gcube.opensearch.opensearchoperator.resource.OpenSearchResource;

/**
 * A client that can be used to test the OpenSearch operator locally
 * 
 * @author gerasimos.farantatos
 *
 */
public class OpenSearchOpClient {
	
	/**
	 * The Logger used by this class
	 */
	private static Logger logger = LoggerFactory.getLogger(OpenSearchOpClient.class.getName());
	
	private OpenSearchResource resource;
	private BufferedWriter fwriter;
	private String query;
	
	/**
	 * Creates a new {@link OpenSearchOpClient} in order to test {@link OpenSearhOp}
	 * 
	 * @param resource The OpenSearch resource that will be used by the operator in order to obtain search results
	 * @param query The query to be sent to the OpenSearch operator
	 * @param outStream The output stream to send output to
	 */
	OpenSearchOpClient(OpenSearchResource resource, String query, OutputStream outStream) throws IOException  {

	    fwriter = new BufferedWriter(new OutputStreamWriter(outStream));
	    
	    this.resource = resource;
	    this.query = query;
	}
	
	/**
	 * Executes an OpenSearch operator, passing to it the OpenSearchResource the search query
	 * and dumping the results to an OutputStream
	 * 
	 * @throws Exception
	 */
	void compute() throws Exception {
		int rc = 0;
		URI outLocator;
		
		try {
			OpenSearchOp osOp = new OpenSearchOp(resource, new String[]{}, new OpenSearchOpConfig(20, false, true, null), new EnvHintCollection());
			outLocator = osOp.query(query);
			
			ForwardReader<OpenSearchRecord> reader = new ForwardReader<OpenSearchRecord>(outLocator);
		
			while(true) {
				if(reader.getStatus() == Status.Dispose || (reader.getStatus() == Status.Close && reader.availableRecords() == 0))
					break;
				OpenSearchRecord rec = reader.get(60, TimeUnit.SECONDS);
				if(rec == null)
					break;
					
				rc+=1;
	
				fwriter.write(rec.getPayload());
				fwriter.newLine();
			}
			reader.close();
			fwriter.flush();
			fwriter.close();
			System.out.println("Total " + rc + " records");
			
		}catch(Exception e) {
			logger.error("Client error", e);
			throw new Exception("Client error");
		}
		
	}
	
	/**
	 * Prints the client's usage and exits the program
	 */
	static void printUsageAndExit() {
		System.out.println("Usage:");
		System.out.println("OpenSearchOpClient OpenSearchResourcePath [query] [outFileName] ");
		System.exit(0);
	}
	
	public static void main(String[] args) throws Exception {
		String query = OpenSearchConstants.searchTermsQName + "=\"earth\" config:numOfResults=\"40\"";
		OutputStream oos = null;
		if(args.length == 0)
			printUsageAndExit();
		String resourcePath = args[0];
		System.out.println(args[0]);
		if(args.length > 1)
			query = args[1];
		System.out.println(query);
		if(args.length > 2)
			oos = new BufferedOutputStream(new FileOutputStream(args[2]));
		else
			oos = new BufferedOutputStream(System.out);
		
		OpenSearchResource resource = new LocalOpenSearchResource(new File(resourcePath), null);
		
		OpenSearchOpClient cl = new OpenSearchOpClient(resource, query, oos);
		cl.compute();
	}

}
