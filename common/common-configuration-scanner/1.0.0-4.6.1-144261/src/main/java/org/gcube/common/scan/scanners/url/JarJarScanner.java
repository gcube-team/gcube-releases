package org.gcube.common.scan.scanners.url;

import java.net.JarURLConnection;
import java.net.URL;
import java.util.jar.JarFile;

/**
 * Specialises {@link AbstractJarURLScanner} to <code>jar:</code> URLs that refer to JAR files.
 * @author Fabio Simeoni
 *
 */
public class JarJarScanner extends AbstractJarURLScanner {

	@Override
	public boolean handles(URL url) {
		return "jar".equals(url.getProtocol());
	}
	
	@Override
	protected JarFile toFile(URL url) throws Exception {
		JarURLConnection urlConnection = (JarURLConnection) url.openConnection();
        return urlConnection.getJarFile();
        
	}

}
