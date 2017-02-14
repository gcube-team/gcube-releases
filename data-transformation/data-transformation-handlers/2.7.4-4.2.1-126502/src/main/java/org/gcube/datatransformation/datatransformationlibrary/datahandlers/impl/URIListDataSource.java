package org.gcube.datatransformation.datatransformationlibrary.datahandlers.impl;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gcube.datatransformation.datatransformationlibrary.dataelements.impl.URLDataElement;
import org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.ContentTypeDataSource;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataHandlerDefinitions;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSource;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DistributableDataSource;
import org.gcube.datatransformation.datatransformationlibrary.model.ContentType;
import org.gcube.datatransformation.datatransformationlibrary.model.Parameter;
import java.io.File;


/**
 * @author Dimitris Katris, NKUA
 *
 * <p>
 * This <tt>DataSource</tt> fetches <tt>DataElements</tt> from a file containing urls.
 * </p>
 */
public class URIListDataSource implements DataSource, DistributableDataSource, ContentTypeDataSource {

	/**
	 * Tests URIListDataSource.
	 * @param args nothing
	 * @throws Exception If data source could not be initialized.
	 */
	public static void main(String[] args) throws Exception {
//		URIListDataSource dataSource = new URIListDataSource("http://dl07.di.uoa.gr:8080/myURIList-ext.txt", null);
		URIListDataSource dataSource = new URIListDataSource("http://node1.d.efg.research-infrastructures.eu/urls/list.txt", null);
		int cnt=0;
		while(dataSource.hasNext()){
			DataElement elm = dataSource.next();
			cnt++;
			if(elm!=null){
				System.out.println("---"+elm.getId());
			}
		}
		System.out.println("Got "+cnt+" data elements");
	}
	
	private BufferedReader br;
	private File temp ;
	

	
	
	/**
	 * @param input The input value of the <tt>DataSource</tt>.
	 * @param inputParameters The input parameters of the <tt>DataSource</tt>.
	 * @throws Exception If the <tt>DataSource</tt> could not be initialized.
	 */
	public URIListDataSource(String input, Parameter[] inputParameters) throws Exception {
		//Read the doc and put it in a tmp file maybe...
		temp = File.createTempFile("list",".URIlist");
		FileOutputStream fileoutputstream = new FileOutputStream(temp);
		DataOutputStream dataoutputstream = new DataOutputStream(fileoutputstream);

		
		URL uriListLocation = new URL(input);
		DataInputStream in = new DataInputStream(uriListLocation.openStream());
        br = new BufferedReader(new InputStreamReader(in));

	    String inputLine;
	    
    	//grab the contents at the URL
    	while ((inputLine = br.readLine()) != null){
    		dataoutputstream.writeBytes(inputLine+"\n");
    	}
    	//	write it locally
		dataoutputstream.flush();
		dataoutputstream.close();

		//Setup the reading stream
		FileInputStream fileinputstream = new FileInputStream(temp);
		br = new BufferedReader(new InputStreamReader(fileinputstream));
		
		br.close();
	}


	private boolean hasNext=true;
	
	private static Logger log = LoggerFactory.getLogger(URIListDataSource.class);
	
	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSource#hasNext()
	 * @return true if the <tt>DataSource</tt> has more elements.
	 */
	public boolean hasNext() {
		return hasNext;
	}

	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSource#next()
	 * @return the next element of the <tt>DataSource</tt>.
	 */
	public DataElement next() {
		try {
			String nextElement;
			if((nextElement = br.readLine()) != null){
				log.trace("Returning element to get from url "+nextElement);
				String name = null;
				String URL = nextElement;
				
				if (nextElement.startsWith("\"")){
					int URLstart, URLend, nameStart, nameEnd;
					URLstart = nextElement.indexOf('"')+1;
					URLend = nextElement.indexOf('"', URLstart+1);
					nameStart = nextElement.indexOf('"',URLend+1)+1;
					nameEnd = nextElement.lastIndexOf('"');
					URL = nextElement.substring(URLstart, URLend);
					name = nextElement.substring(nameStart, nameEnd);
//					System.out.println("URL: "+URL+ " output filename: "+name);
					log.trace("URL: "+URL+ " output filename: "+name);
				}

				URLDataElement next = new URLDataElement(URL);
				if (name!=null)
					next.setAttribute(DataHandlerDefinitions.ATTR_DOCUMENT_NAME, name);
				return next;
			}else{
				log.trace("No more elements");
				hasNext=false;
				return null;
			}
		} catch (Exception e) {
			log.trace("URLList got exception: ",e);
			hasNext=false;
			return null;
		}
	}

	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataHandler#close()
	 */
	public void close() {
		try {
			if(!isClosed){
				isClosed=true;
				temp.delete();
				br.close();
			}
		} catch (Exception e) {
			log.error("Did not manage to clear rs reader", e);
		}
	}

	private boolean isClosed=false;
	
	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataHandler#isClosed()
	 * @return true if the <tt>DataHandler</tt> has been closed.
	 */
	public boolean isClosed() {
		return isClosed;
	}
	
	/* DISTRIBUTABLE METHODS */
	
	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DistributableDataSource#getDataElement(java.lang.String)
	 * @param dataElementID The id of the {@link DataElement}.
	 * @return The {@link DataElement} instance.
	 * @throws Exception If an error occurred in getting the {@link DataElement}.
	 */
	public DataElement getDataElement(String dataElementID) throws Exception {
		return new URLDataElement(dataElementID);
	}

	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DistributableDataSource#getNextDataElementID()
	 * @return The next data element id. 
	 * @throws Exception If the <tt>DistributableDataSource</tt> did not manage to get another data element id.
	 */
	public String getNextDataElementID() throws Exception {
		try {
			String nextElement;
			if((nextElement = br.readLine()) != null){
				log.trace("Returning element with url "+nextElement);
				return nextElement;
			}else{
				hasNext=false;
				return null;
			}
		} catch (Exception e) {
			hasNext=false;
			return null;
		}
	}

	/**
	 * 
	 */
	public URIListDataSource(){}
	
	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DistributableDataSource#initializeDistributableDataSource(java.lang.String, org.gcube.datatransformation.datatransformationlibrary.model.Parameter[])
	 * @param input The input of the <tt>DistributableDataSource</tt>
	 * @param inputParameters Any input parameters required by the <tt>DistributableDataSource</tt>.
	 * @throws Exception If the <tt>DistributableDataSource</tt> could not be initialized.
	 */
	public void initializeDistributableDataSource(String input,
			Parameter[] inputParameters) throws Exception {
	}
	
	public ContentType nextContentType() {
		DataElement de = next();
		
		return de == null? null : de.getContentType();
	}
}
