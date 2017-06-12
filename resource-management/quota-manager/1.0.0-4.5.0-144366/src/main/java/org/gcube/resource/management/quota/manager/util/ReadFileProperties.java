package org.gcube.resource.management.quota.manager.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;

import org.gcube.resource.management.quota.library.quotedefault.QuotaDefault;
import org.gcube.resource.management.quota.library.quotedefault.QuotaDefaultList;
import org.gcube.resource.management.quota.manager.check.QuotaCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReadFileProperties {
	
	private static Logger log = LoggerFactory.getLogger(QuotaCheck.class);	   
	
	private InputStream input =null ;
	private String fileProperties=null;
	private List<QuotaDefault> listQuotaDefault=new ArrayList<QuotaDefault>();
	
	public ReadFileProperties(String fileProperties){
		this.fileProperties = fileProperties;
	}
	public String getFileProperties() {
		return fileProperties;
	}
	public List<QuotaDefault> getListQuotaDefault() {
		try {
			input = new FileInputStream(fileProperties);
			JAXBContext jaxbContext = JAXBContext.newInstance(QuotaDefaultList.class);
			QuotaDefaultList quotalist =  (QuotaDefaultList) jaxbContext.createUnmarshaller().unmarshal(input);
			listQuotaDefault=quotalist.getQuotaDefaultList();		
	
		} catch (Exception ex) {
			log.error("--:{}",ex.getLocalizedMessage());
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return listQuotaDefault;
	}
}
