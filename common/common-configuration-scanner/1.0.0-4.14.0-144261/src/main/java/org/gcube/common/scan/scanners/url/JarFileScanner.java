package org.gcube.common.scan.scanners.url;

import java.io.File;
import java.net.URL;
import java.util.jar.JarFile;

/**
 * Specialises {@link AbstractJarURLScanner} to <code>file:</code> URLs that refer to JAR files.
 * @author Fabio Simeoni
 *
 */
public class JarFileScanner extends AbstractJarURLScanner {

	@Override
	public boolean handles(URL url) {
		return "file".equals(url.getProtocol()) && url.toExternalForm().contains(".jar");
	}
	
	@Override
	protected JarFile toFile(URL url) throws Exception {
		
		File file = new File(url.toURI().getSchemeSpecificPart());
		
		if (file==null || !file.exists() || !file.canRead())
			throw new IllegalArgumentException(file+" does not exist or is not readable");
		
		return new JarFile(file);
	}

}
