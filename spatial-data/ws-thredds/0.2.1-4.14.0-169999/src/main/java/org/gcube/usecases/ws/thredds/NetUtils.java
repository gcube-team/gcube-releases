package org.gcube.usecases.ws.thredds;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.IOUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NetUtils {

	public static final File toFile(InputStream is) {
		FileOutputStream outputStream =null;
		try {
			File toReturn=File.createTempFile("tempFile", ".tmp");
			outputStream=new FileOutputStream(toReturn);

			int read = 0;
			byte[] bytes = new byte[1024];

			while ((read = is.read(bytes)) != -1) {
				outputStream.write(bytes, 0, read);
			}
			return toReturn;
		}catch(Throwable t) {
			throw new RuntimeException(t);
		}finally {
			if(outputStream!=null) {
				IOUtils.closeQuietly(outputStream);
			}
		}
	}

	
	public static String resolveRedirects(String url) throws IOException{
		log.debug("Resolving redirect for url {} ",url);
		URL urlObj=new URL(url);
		HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
		int status=connection.getResponseCode();
		if(status>=300&&status<400){
			String newUrl=connection.getHeaderField("Location");
			log.debug("Following redirect from {} to {} ",url,newUrl);
			return resolveRedirects(newUrl);
		}else return url;
	}
	
	
	public static File download(String url) throws MalformedURLException, IOException {
		return toFile(new URL(resolveRedirects(url)).openStream());
	}
}
