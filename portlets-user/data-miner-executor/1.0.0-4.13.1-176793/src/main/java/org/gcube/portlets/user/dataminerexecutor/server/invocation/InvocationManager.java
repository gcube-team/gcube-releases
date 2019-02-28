package org.gcube.portlets.user.dataminerexecutor.server.invocation;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.xml.bind.JAXBException;

import org.gcube.data.analysis.dataminermanagercl.shared.exception.ServiceException;
import org.gcube.data.analysis.dminvocation.DataMinerInvocationManager;
import org.gcube.data.analysis.dminvocation.model.DataMinerInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

public class InvocationManager {

	private static Logger logger = LoggerFactory.getLogger(InvocationManager.class);
	private static final String INVOCATION_NAME = "DMInvocation";
	private static final String INVOCATION_XML_EXTENTION = ".xml";
	private DataMinerInvocationManager manager;

	public InvocationManager() throws ServiceException {
		try {
			manager = DataMinerInvocationManager.getInstance();
		} catch (JAXBException | IOException | SAXException e) {
			logger.error("Error in DataMiner Invocation Manager: " + e.getLocalizedMessage(), e);
			throw new ServiceException("Error in DataMiner Invocation Manager: " + e.getLocalizedMessage());
		}

	}

	public DataMinerInvocation readInvocation(String invocationFileUrl) throws ServiceException {
		try {
			
			logger.info("Read DataMiner Invocation Model");
			Path destination = Files.createTempFile(INVOCATION_NAME, INVOCATION_XML_EXTENTION);			
			logger.info("Destination: [destination=" + destination + "]");

			logger.info("Read invocation: [fileUrl=" + invocationFileUrl+"]");
			URL smpFile = new URL(invocationFileUrl);
			URLConnection uc = (URLConnection) smpFile.openConnection();

			DataMinerInvocation dmInvocation=null;
			
			try (InputStream is = uc.getInputStream();) {
				dmInvocation = manager.unmarshalingXML(uc.getInputStream(), true);
			} 
			
			logger.debug("DMInvocation: "+dmInvocation);
			return dmInvocation;
			
		} catch (Throwable e) {
			logger.error("Error reading DataMiner invocation file: " + e.getLocalizedMessage(), e);
			throw new ServiceException("Error reading DataMiner invocation file: "+e.getLocalizedMessage());
		}
	}
//	
//	public void readInvocation(String invocationFileUrl) throws ServiceException {
//		
//		try {
//			Path file=retrieveFile(invocationFileUrl);
//			
//			try (InputStream in = Files.newInputStream(uc.getrn_demo)) {
//	            while ((n = in.read()) != -1) {
//	                System.out.print((char) n);
//	            }
//	        } catch (IOException e) {
//	            System.err.println(e);
//	        }
//			
//			
//
//			
//			List<String> lines = createInfoTxtData();
//			Files.write(tempFile, lines, Charset.defaultCharset(), StandardOpenOption.WRITE);
//			logger.debug(tempFile.toString());
//			return tempFile;
//
//		} catch (IOException e) {
//			logger.error(e.getLocalizedMessage());
//			e.printStackTrace();
//			throw new ServiceException(e.getLocalizedMessage(), e);
//		}
//		
//		
//
//
//	}
}
