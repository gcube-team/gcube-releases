package org.gcube.portlets.user.dataminermanager.server.smservice.wps.computationsvalue;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.gcube.portlets.user.dataminermanager.shared.data.computations.ComputationValue;
import org.gcube.portlets.user.dataminermanager.shared.data.computations.ComputationValueFile;
import org.gcube.portlets.user.dataminermanager.shared.data.computations.ComputationValueFileList;
import org.gcube.portlets.user.dataminermanager.shared.data.computations.ComputationValueImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComputationValueBuilder {
	private static Logger logger = LoggerFactory
			.getLogger(ComputationValueBuilder.class);

	private LinkedHashMap<String, String> valueParameters;
	private LinkedHashMap<String, ComputationValue> computationsValueParameters;

	public ComputationValueBuilder(LinkedHashMap<String, String> valueParameters) {
		this.valueParameters = valueParameters;
	}

	/**
	 * 
	 */
	public LinkedHashMap<String, ComputationValue> create() {
		computationsValueParameters = new LinkedHashMap<String, ComputationValue>();
		if (valueParameters != null && !valueParameters.isEmpty()) {
			for (String key : valueParameters.keySet()) {
				String value = valueParameters.get(key);
				if (value != null && !value.isEmpty()
						&& value.startsWith("http")) {
					if (value.contains("|")) {
						ComputationValue valueFileList = createComputationValueFileList(value);
						computationsValueParameters.put(key, valueFileList);
					} else {
						ComputationValue computationValue = retrieveFileName(value);
						computationsValueParameters.put(key, computationValue);
					}
				} else {
					ComputationValue valueString = new ComputationValue(value);
					computationsValueParameters.put(key, valueString);
				}
			}
		}
		logger.debug("CompuatationsValues: " + computationsValueParameters);
		return computationsValueParameters;

	}

	
	private ComputationValue createComputationValueFileList(String value) {
		ArrayList<ComputationValue> fileList=new ArrayList<>();
		int indexSeparator;
		String file;
		ComputationValue computationValue;
		while((indexSeparator=value.indexOf("|"))!=-1){
			file=value.substring(0, indexSeparator);
			value=value.substring(indexSeparator+1);
			computationValue=retrieveFileName(file);
			fileList.add(computationValue);
		}
		computationValue=retrieveFileName(value);
		fileList.add(computationValue);	
		ComputationValueFileList computationValueFileList=new ComputationValueFileList(fileList, "|");
		return computationValueFileList;
		
	}

	private ComputationValue retrieveFileName(final String value) {
		URLConnection conn = null;
		logger.debug("Retrieve File Header from Storage for value: "+value);
		try {
			//Use URL connection because in internal network is more fast
			URL url = new URL(value);
			conn =  url.openConnection();
			return extractFileName(conn, value);

		} catch (Throwable e) {
			logger.error("Retrieve File Name: " + e.getLocalizedMessage());
			e.printStackTrace();
			return new ComputationValueFile(value, null, null);
		}
	}
	
	/*
	int attempts = 0;
	

	do {
		logger.debug("Attempts: "+attempts);
		
		conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(30000);
		conn.setReadTimeout(30000);
		conn.setRequestMethod("HEAD");
		conn.setAllowUserInteraction(false);
		conn.setDoInput(false);
		conn.setDoOutput(false);
		
		conn.connect();
	
		if (conn.getResponseCode() != 200) {
			logger.error("Retrieve File Name Response Code: "
					+ conn.getResponseCode());
			try {
				logger.debug("Sleep time: 5000");
				Thread.sleep(5000);
			} catch (InterruptedException e) {

			}
		}
		attempts++;
		
	} while (attempts <= 5 || conn.getResponseCode() != 200);

	if (conn.getResponseCode() == 200) {
		logger.debug("Retrived Header");
		return extractFileName(conn, value);
	} else {
		logger.debug("Header not retrieved use default");
		return new ComputationValueFile(value, null, null);
	}
	*/

	private ComputationValue extractFileName(URLConnection conn,
			String value) {
		ComputationValue computationValue = null;
		String fileName = null;
		String mimeType = null;

		logger.debug("Connection-Header: " + conn.getHeaderFields());

		String contentDisposition = conn.getHeaderField("Content-Disposition");
		if (contentDisposition == null) {
			Map<String, List<String>> headerFields = conn.getHeaderFields();
			boolean found = false;
			for (String key : headerFields.keySet()) {
				List<String> headerField = headerFields.get(key);
				for (String fieldValue : headerField) {
					if (fieldValue.toLowerCase().contains("filename=")) {
						contentDisposition = fieldValue;
						found = true;
						break;
					}
				}
				if (found) {
					break;
				}
			}
		}

		logger.debug("Content-Disposition: " + contentDisposition);
		// Content-Disposition="attachment; filename=abc.png"
		if (contentDisposition != null && contentDisposition.indexOf("=") != -1) {
			fileName = contentDisposition.split("=")[1]; // getting value
															// after '='
			if (fileName != null && !fileName.isEmpty()) {
				if (fileName.startsWith("\"")) {
					fileName = fileName.substring(1);
				}
				if (fileName.endsWith("\"")) {
					fileName = fileName.substring(0, fileName.length() - 1);
				}
			}
		} else { // fall back to random generated file name? }
		}
		mimeType = conn.getContentType();
		if ((mimeType != null && mimeType.compareToIgnoreCase("image/png") == 0)
				|| fileName != null && fileName.endsWith(".png")) {
			computationValue = new ComputationValueImage(value, fileName,
					mimeType);
		} else {
			computationValue = new ComputationValueFile(value, fileName,
					mimeType);
		}

		return computationValue;
	}
}
