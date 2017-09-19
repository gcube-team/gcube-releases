package org.gcube.dataanalysis.statistical_manager_wps_algorithms.output;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.gcube.dataanalysis.statistical_manager_wps_algorithms.utils.SMutils;
import org.n52.wps.io.data.GenericFileData;
import org.n52.wps.io.data.GenericFileDataConstants;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.data.binding.complex.GenericFileDataBinding;
import org.n52.wps.io.data.binding.literal.LiteralStringBinding;

public class OutputBuilderUtil {
	public final UUID SessionUID = UUID.randomUUID();
	public final String wpsLocation = System.getProperty("catalina.base")
			+ "/webapps/wps/";
	protected InetAddress addr;
	private String algorithmId;

	public OutputBuilderUtil(String algorithmId) {
		this.algorithmId = algorithmId;
	}

	public GenericFileDataBinding getXmlFileDataBinding(
			ArrayList<OutputManagement> outputList) {
		GenericFileDataBinding res = null;
		try {

			String xml = getXml(outputList);
			InputStream is = new ByteArrayInputStream(xml.getBytes());

			res = new GenericFileDataBinding(new GenericFileData(is,
					GenericFileDataConstants.MIME_TYPE_PLAIN_TEXT));
					} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.toString());

		}

		return res;
	}

	public String getXml(ArrayList<OutputManagement> outputList) {
		StringBuilder sb = new StringBuilder();
		String result = new String();
		// sb.append("	<algorithmName>").append(algorithmId).append("</algorithmName>\n");
		if (outputList != null) {
			int i=0;
			for (OutputManagement out : outputList) {
				if(i==0)
					sb.append("\n");
				sb.append(out.getFormattedOutput());
				i++;
			}
			

		} else {

			sb.append("computation failed");

		}

		return sb.toString();

	}

	public Map<String, IData> buildFileResults(String filename) {
		String result = "";
		// Get hostname
		String hostname = null;
		try {
			addr = InetAddress.getLocalHost();
			hostname = addr.getHostName();
		} catch (Exception e) {
			throw new RuntimeException("Error retrieving host name "
					+ e.getCause(), e);
		}
		// Define the output URL where the results file will be published
		result = "http://" + hostname + ":8080/" + wpsLocation
				+ SessionUID.toString() + filename;

		HashMap<String, IData> resulthash = new HashMap<String, IData>();
		// Fill WPS results map with the just defined URL
		resulthash.put("result", new LiteralStringBinding(result));
		return resulthash;
	}

	public String getResultPath() throws UnknownHostException {
		addr = InetAddress.getLocalHost();
		String hostname = addr.getHostName();

		return "http://" + hostname + ":8888/" + wpsLocation;

	}

	public File saveFileFromUrl(String url, String path) throws Exception {
		InputStream inputStream = SMutils.getStorageClientInputStream(url);
		byte[] bytes = new byte[3024];
		OutputStream outputStream = null;
		File result = new File(path);
		outputStream = new FileOutputStream(result);
		int read = 0;
		while ((read = inputStream.read(bytes)) != -1) {
			outputStream.write(bytes, 0, read);
		}

		System.out.println("Done!");
		if (inputStream != null)
			inputStream.close();
		if (outputStream != null)
			outputStream.close();

		// OutputStream out = resp.getOutputStream();
		// IOUtils.copy(inputStream, resp.getOutputStream());
		return result;

	}

}
