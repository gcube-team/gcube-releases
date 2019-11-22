package org.gcube.portlets.user.workspace;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.gcube.portlets.user.workspace.server.util.MimeTypeUtility;

public class MimeTypeUtilityTest {
	
	static Map<String, String> fileNamesToMimeTypes = new LinkedHashMap<>();
	
	static {
	
		fileNamesToMimeTypes.put("An html page of D4science.html", "text/html");
		fileNamesToMimeTypes.put("No Extension File of kind CSV", "text/csv");
		fileNamesToMimeTypes.put("No Extension File of kind PDF", "application/pdf");
		fileNamesToMimeTypes.put("An .o extension file .take ext", "text/html");
		fileNamesToMimeTypes.put("workspace-6.19.1-4.12.1-169702", "application/x-tika-java-web-archive");
		fileNamesToMimeTypes.put("workspace.o", "application/json");
	}
	
	
	public static void main(String[] args) {

		Map<String, List<String>> mimetype_extension_map = MimeTypeUtility.getMimeTypeToExtensionMap();
		
		System.out.println("Mime types: ");
		for (String mimeType : mimetype_extension_map.keySet()) {
			System.out.println(mimeType + " -> " + mimetype_extension_map.get(mimeType));
		}

		String mimeType = "text/html";
		System.out.println("\n\nGet " + mimeType + " returing list of exstensions: " + mimetype_extension_map.get(mimeType));

		
		for (String keyFileName : fileNamesToMimeTypes.keySet()) {
			try {
				System.out.println("\nResolving file name with extension for name: "+keyFileName);
				String toFileName = MimeTypeUtility.getNameWithExtension(keyFileName, fileNamesToMimeTypes.get(keyFileName));
				System.out.println("Assigned the file name: "+toFileName);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
