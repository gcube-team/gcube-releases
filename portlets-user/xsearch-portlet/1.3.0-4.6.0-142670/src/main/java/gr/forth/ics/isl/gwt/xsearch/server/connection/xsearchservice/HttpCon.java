/*
 * 
 * Copyright 2012 FORTH-ICS-ISL (http://www.ics.forth.gr/isl/) 
 * Foundation for Research and Technology - Hellas (FORTH)
 * Institute of Computer Science (ICS) 
 * Information Systems Laboratory (ISL)
 * 
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent 
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the
 * Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 * 
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 * 
 */
package gr.forth.ics.isl.gwt.xsearch.server.connection.xsearchservice;

import gr.forth.ics.isl.xsearch.configuration.Conf;
import gr.forth.ics.isl.xsearch.configuration.Resources;
import gr.uoa.di.madgik.grs.proxy.GRS2ProxyInvalidArgumentException;
import gr.uoa.di.madgik.grs.proxy.GRS2ProxyInvalidOperationException;
import gr.uoa.di.madgik.grs.record.GenericRecord;
import gr.uoa.di.madgik.grs.utils.Locators;
import gr.uoa.di.madgik.grs.writer.GRS2WriterException;
import gr.uoa.di.madgik.grs.writer.RecordWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;

/**
 * Contains the functionality that is used to send an http Request
 * from portlet to XSearch Service. It passes a set of results and 
 * gets as an answer their semantic analysis (clustering/mining)
 * @author kitsos Ioannis (kitsos@ics.forth.gr)
 */
public class HttpCon {
	private RecordWriter<GenericRecord> writer;
	private String query;
	private int startOffset = 0;
        private boolean connectionEstablished = true;
        private Logger logger=Resources.initializeLogger(this.getClass().getName());

        /**
         * Initialize parameters
         * @param writer a writer which is used to pass the objects
         * @param query query text
         * @param startOffset start Offset of search results
         */
	public HttpCon(RecordWriter<GenericRecord> writer, String query, int startOffset){
		this.writer = writer;
		this.query = query;
		this.startOffset = startOffset;
	}
	
        /**
         * Creates a new HTTP connection with the XSearch Service.
         * @return the created HttpURLConnection
         */
	public HttpURLConnection httpRequestToXSearchServiceForSemanticEnrichment(){
		HttpURLConnection con = null;
		try{	
		
			URI TCPLocator = Locators.localToTCP(writer.getLocator());
					
			this.query=query.trim().replaceAll(" ", "%20");
			
			String encodedLocator = URLEncoder.encode(TCPLocator.toString(), "utf8");
			String xSearchURL = Conf.XSearchServiceURL+"SemanticSearch?query="
					+ query.trim()
					+ "&n="+ Conf.numOfResultsToAnalyze
					+ "&mining="+Conf.enableMining
					+ "&clustering="+Conf.enableClustering 
					+ "&clnum="+Conf.numOfClusters 
					+ "&resultsStartOffset="+startOffset
					+ "&locator="+encodedLocator;
			
			URL url = new URL(xSearchURL);
			con = (HttpURLConnection) url.openConnection();
						
			this.logger.info("=> The url is: "+url.toString());
			
			con.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 5.0; H010818)");
			con.setConnectTimeout(90000);
			con.setReadTimeout(90000);
			con.connect();
		
		}catch (GRS2WriterException | GRS2ProxyInvalidArgumentException | GRS2ProxyInvalidOperationException | IOException ex){
                    connectionEstablished=false;	
                    this.logger.error(ex.toString());
		}
		
		if (con==null){
			this.logger.error("Something went wrong with the HTTP connection to XSearch-Service");
		}
		
		return con;				
	}
	
        /**
         * Establish connection and receive the answer
         * @param con HttpURLConnection
         * @return the JSON string that contains the results of the semantic analysis
         */
	public String httpReceiveReqFromXSearchSeriveWithSemanticEnrichment(HttpURLConnection con){
		String serviceReturn = "";
		try{
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
							
			String input;
			while ((input = in.readLine()) != null) {
				serviceReturn = serviceReturn + input + "\n";
			}
			
			in.close();
		
		}catch(IOException ex){
                    this.logger.error(ex.toString());
                    connectionEstablished=false;
		}
		return serviceReturn;
	}
	
        /**
         * Adds GenericRecord to the TCPLocator
         * @param rec a new Generic Record
         */
	public boolean addRecordToTCPLocator(GenericRecord rec){
            // Put records to the TCPLocator
            try{
                if (!writer.put(rec, 60, TimeUnit.SECONDS)){
                    this.logger.info("TimeOut of writer.put");
                }
            }catch(GRS2WriterException ex) {
                this.logger.error(ex.toString());
            }
            
            return connectionEstablished;
	}
	
    /**
     * 
     * @return true if the connection was established successfully otherwise false
     */
    public boolean isConnectionEstablished() {
        return connectionEstablished;
    }
}
