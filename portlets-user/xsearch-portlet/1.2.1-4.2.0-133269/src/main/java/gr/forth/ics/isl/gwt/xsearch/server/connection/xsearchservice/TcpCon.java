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
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * A Class that has a set of function to establish a TCP connection
 * between XSearch portlet and service in order to send the hits
 * and get back their semantic analysis
 * @author kitsos Ioannis (kitsos@ics.forth.gr)
 */
public class TcpCon {
    private Logger logger=Resources.initializeLogger(this.getClass().getName());

    public TcpCon() {
    }

    /**
     * Request to XSearchService. Which returns a Json string with the results of mining and clustering.
     *
     * @param writer an instance of RecordWriter in order to take a locator
     * @param query the query that submitted
     * @return Json String that contains the answer of XSearchService
     */
    public Socket tcpRequestToXSearchService(ObjectOutputStream Soutput, RecordWriter<GenericRecord> writer, String query, int startOffset) {
        Socket socket = null;

        try {
            URI TCPLocator = Locators.localToTCP(writer.getLocator());
            this.logger.info("# TCPLocator = " + TCPLocator);

            // we use "localhost" as host name, the server is on the same machine
            // 	but you can put the "real" server name or IP address	
            socket = new Socket(Conf.XSearchServiceURL, Conf.XSearchServicePort);

            this.logger.info("Connection accepted "+socket.getInetAddress()+":"+socket.getPort());

            /*
             * Creating both Data Stream
             */
            Soutput = new ObjectOutputStream(socket.getOutputStream());
            this.logger.info("Client sending \"" + TCPLocator.toString() + "\" to server");

            // Initialize to a map all the variables that you want to pass from socket
            Map<String, String> map = new HashMap<>();
            map.put("query", query);
            map.put("resultsStartOffset", Integer.toString(startOffset));
            map.put("mining", Conf.enableMining + "");
            map.put("clustering", Conf.enableClustering + "");
            map.put("clnum", Conf.numOfClusters + "");
            map.put("OnlySnippets", Conf.OnlySnippets + "");
            map.put("locator", TCPLocator.toString());

            Soutput.writeObject(map);
            Soutput.flush();

        }catch(GRS2ProxyInvalidArgumentException | GRS2ProxyInvalidOperationException | GRS2WriterException | IOException ex){
            this.logger.error("An error occured with the socket request\n"+ex.toString());
        }

        return socket;
    }

    /**
     * Receive the answer of XSearch Service request
     * @param Soutput ObjectOutputStream
     * @param socket Socket
     * @return a JSON string with the results of semantic analysis
     */
    public String tcpReceiveReqFromXSearchSerive(ObjectOutputStream Soutput, Socket socket) {
        String response = "";

        ObjectInputStream Sinput = null;

        /*
         * Creating both Data Stream
         */
        try {
            Sinput = new ObjectInputStream(socket.getInputStream());
        }catch(IOException ex){
            this.logger.error("Exception when creating new Input/output Streams: \n"+ex.toString());
        }

        try{
            response = (String) Sinput.readObject();
            this.logger.info("Read back from server: " + response);
        }catch(ClassNotFoundException | IOException ex){
            this.logger.error("Problem reading back from server:\n"+ ex.toString());
        }

        try{
            Sinput.close();
            Soutput.close();
        }catch(IOException ex){
            this.logger.error("Problem closing the stout kai stin streams\n"+ex);
        }

        //System.out.println("# JSON = \n" + response);

        return response;
    }
}
