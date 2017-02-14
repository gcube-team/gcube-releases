package gr.cite.geoanalytics.layerimport.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.ws.rs.core.MultivaluedMap;

import com.google.common.io.Files;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class TestZip {
	
	public static void main(String[] args) throws Exception {
		
		Map<String, InputStream> map = new HashMap<String, InputStream>();
		
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("service", "wfs");
		parameters.put("version", "1.0.0");
		parameters.put("request", "GetFeature");
		parameters.put("typeName", "topp:tasmania_roads");
		parameters.put("outputFormat", "SHAPE-ZIP");
		
		InputStream inputStream = (InputStream)doTheRequest("http://dl007.madgik.di.uoa.gr:9999/geoserver/topp/ows", parameters, true);
		
		
	//		File f = File.createTempFile(featureType, ".zip", new File("C:/"));
		
			
		File f = new File(Files.createTempDir(), "topp:tasmania_roads.zip");
		
		FileOutputStream fos = new FileOutputStream(f);
		int length;
		byte[] bytes = new byte[1024];
		while ((length = inputStream.read(bytes)) >= 0) {
			fos.write(bytes, 0, length);
		}
		fos.close();
		
		ZipFile zipFile = new ZipFile(f);
		
		Enumeration<?> enu = zipFile.entries();
		while (enu.hasMoreElements()) {
			ZipEntry zipEntry = (ZipEntry) enu.nextElement();
			
			String name = zipEntry.getName();
			long size = zipEntry.getSize();
			long compressedSize = zipEntry.getCompressedSize();
			System.out.printf("name: %-20s | size: %6d | compressed size: %6d\n", 
					name, size, compressedSize);
			
			
			
			InputStream is = zipFile.getInputStream(zipEntry);
			map.put(name, is);
		}
		
	}
	
	public static Object doTheRequest(String url, Map<String, String> parameters, boolean inputStream) throws Exception {
		Client client = Client.create();
		WebResource webResource = null;
		
		MultivaluedMap nameValuePairs = new MultivaluedMapImpl();
		for (Map.Entry<String, String> params: parameters.entrySet()) {
			if (params.getKey()!=null && !params.getKey().isEmpty() && 
				params.getValue()!=null && !params.getValue().isEmpty())
				nameValuePairs.add(params.getKey(), params.getValue());
		}
		webResource = client.resource(url).queryParams(nameValuePairs);
		ClientResponse response = webResource.get(ClientResponse.class);

		if (response.getStatus()==201 || response.getStatus()==200) {
			try {
				if (inputStream)
					return response.getEntity(InputStream.class);
				return response.getEntity(String.class);
						
			} catch (Exception e) {
				System.err.println("Exception occured!");
			}
		}
		return null;
	}
}