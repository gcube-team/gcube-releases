package org.gcube.common.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.http.conn.ConnectTimeoutException;
import org.gcube.common.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctc.wstx.exc.WstxUnexpectedCharException;

/**
 * @author David Uvalle, david.uvalle@gmail.com
 * @version 0.1
 * 
 */
@SuppressWarnings("all")
public class RecordIterator implements Iterator<Record>  {

	private static Logger log = LoggerFactory.getLogger(RecordIterator.class);

	private int completeListSize;
	private String percentage;
	private String resumptionToken;
	private String from;
	private String until;
	private String set;
	private String metadataPrefix;
	private String baseUrl;
	private boolean hasResumptionToken;
	//	private boolean deleted = false;
	private Iterator<OMElement> getRecords;
	private OMElement documentElement = null;
	private OMElement elementType = null;
	Record record = null;

	public RecordIterator(String resumptionToken,String from,String until,String set, String metadataPrefix, String baseUrl) throws Exception {	
		//				System.out.println("RecordIterator " );
		//		this.listRecords = listRecords;

		this.resumptionToken = resumptionToken;
		this.set = set;
		this.from = from;
		this.until = until;

		if (metadataPrefix == null)
			this.metadataPrefix = "oai_dc";
		else
			this.metadataPrefix = metadataPrefix;

		this.baseUrl = baseUrl;
		// set number


		OMElement documentElement = null;

		this.hasResumptionToken = false;

		getUrl();

	}


	private void getUrl() throws Exception {

		try {

			if(resumptionToken.isEmpty()) {	

				if(from==null && until == null && set == null)
					documentElement = Utils.getReaderFromHttpGet(baseUrl, "ListRecords&metadataPrefix="+metadataPrefix);
				else if(from!=null && until == null && set == null)
					documentElement = Utils.getReaderFromHttpGet(baseUrl, "ListRecords&metadataPrefix="+metadataPrefix+"&from="+from);
				else if(from==null && until != null && set == null)
					documentElement = Utils.getReaderFromHttpGet(baseUrl, "ListRecords&metadataPrefix="+metadataPrefix+"&until="+from);
				else if(from==null && until == null && set != null)
					documentElement = Utils.getReaderFromHttpGet(baseUrl, "ListRecords&metadataPrefix="+metadataPrefix+"&set="+set);
				else if(from!=null && until != null && set == null)
					documentElement = Utils.getReaderFromHttpGet(baseUrl, "ListRecords&metadataPrefix="+metadataPrefix+"&from="+from+"&until="+until);
				else if(from!=null && until != null && set != null)
					documentElement = Utils.getReaderFromHttpGet(baseUrl, "ListRecords&metadataPrefix="+metadataPrefix+"&from="+from+"&until="+until+"&set="+set);
				else if(from==null && until != null && set != null)
					documentElement = Utils.getReaderFromHttpGet(baseUrl, "ListRecords&metadataPrefix="+metadataPrefix+"&until="+until+"&set="+set);
				else if(from!=null && until == null && set != null)
					documentElement = Utils.getReaderFromHttpGet(baseUrl, "ListRecords&metadataPrefix="+metadataPrefix+"&from="+from+"&set="+set);				
			}
			else {

				documentElement = Utils.getReaderFromHttpGet(baseUrl, "ListRecords&resumptionToken="+ URLEncoder.encode(resumptionToken, "UTF-8"));
			}	

		}catch(ConnectTimeoutException e){
			log.error("Connection timed out, retry to connect", e);
			getUrl();
		}

		catch(Exception e) {
			log.error("Opening http url " , e);
			//			throw new Exception(e.getMessage());
		}


		//		Iterator<OMElement> getRecordError = documentElement.getChildrenWithName(new QName("http://www.openarchives.org/OAI/2.0/","error"));
		//		if(getRecordError.hasNext()) {
		//			Utils.sendException(getRecordError.next());
		//
		//		}

		Iterator<OMElement> getListRecords = documentElement.getChildrenWithName(new QName("http://www.openarchives.org/OAI/2.0/","ListRecords"));	

		if (getListRecords.hasNext()){
			getRecords = getListRecords.next().getChildElements();
		}


	}


	public boolean hasNext() {

		if (getRecords!=null){
			if(getRecords.hasNext()){

				//check if the next element is an empty resumptionToken
				elementType = getRecords.next();

				if(elementType.getLocalName().equals("resumptionToken")) {
					hasResumptionToken = true;
					this.resumptionToken = elementType.getText();	

					//					System.out.println("-----> resumptionToken: " + this.resumptionToken);
					int cursor = 0;
					completeListSize = 0;

					Iterator<OMAttribute> metadataPrefix = elementType.getAllAttributes();
					while(metadataPrefix.hasNext()) {
						OMAttribute prefixElement = metadataPrefix.next();
						if(prefixElement.getLocalName().equals("cursor")) {
							cursor = Integer.parseInt(prefixElement.getAttributeValue());
						} else if(prefixElement.getLocalName().equals("completeListSize")) {
							completeListSize = Integer.parseInt(prefixElement.getAttributeValue());
						}
					}

					if ((cursor!=0) && (completeListSize!=0)){
						percentage = getPercentage(cursor, completeListSize);
//						System.out.println("Delivered " + percentage + "% records for " + baseUrl);
						log.trace("Delivered " + percentage + "% records for " + baseUrl);
					}else
						log.trace("Already delivered " + cursor + "/" + completeListSize + " records for " + baseUrl);

//					System.out.println("Already delivered " + cursor + "/" + completeListSize + " records for " + baseUrl);
					if (resumptionToken.length()>0)			
						return true;
					else 
						return false;
				}		
				return true;
			}
		}
		return false;
	}


	/**
	 * Get percentage of records delivered
	 * @param cursor
	 * @param completeListSize2
	 * @return
	 */
	private String getPercentage(double cursor, double completeListSize2) {
		double percentuage = ((cursor * 100) / completeListSize2); 
		NumberFormat nf = NumberFormat.getInstance(); // get instance
		nf.setMaximumFractionDigits(2); // set decimal places
		String s = nf.format(percentuage);
		return s;
	}


	public Record next() {
		try{
			//			elementType = getRecords.next();
			if(elementType.getLocalName().equals("record")) {
				//				System.out.println("record");
				record = Utils.getRecord(elementType);
				record.setMetadataPrefix(metadataPrefix);

			} 

			else if(elementType.getLocalName().equals("resumptionToken")) {
				//				record = null;
				//				completeListSize = getCompleteListSize(elementType);				
				//				hasResumptionToken = true;
				//				this.resumptionToken = elementType.getText();		
				//				if (resumptionToken.length()>0){					
				iterator();
				if (hasNext())
					next();
				//				}
			}
		}
		catch(OMException e) {
			log.error("Error getting next record " , e);
		}

		return record;

	}



	private RecordIterator iterator() {
		try {
			getUrl();
		} catch (Exception e) {
			log.error("Error opening new url " , e);
		}
		return this;
	}

	//	//check if the record is valid
	//	public Boolean isDeleted() {
	//		return deleted;
	//	}

	public void remove() {
	}


	public int getCompleteListSize() {
		return completeListSize;
	}
	
	public String getPercentage() {
		return percentage;
	}

}