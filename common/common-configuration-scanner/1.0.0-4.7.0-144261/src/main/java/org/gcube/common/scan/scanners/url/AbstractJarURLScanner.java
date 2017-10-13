package org.gcube.common.scan.scanners.url;

import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.gcube.common.scan.resources.ClasspathResource;
import org.gcube.common.scan.scanners.JarScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractJarURLScanner implements URLScanner {

	private static Logger log = LoggerFactory.getLogger(AbstractJarURLScanner.class);
	
	private final JarScanner scanner = new JarScanner();
	
	@Override
	public Set<URL> additional(URL url) throws Exception {
		
		//adds url entries in jar's manifest class-path attribute 
		//(necessary e.g. for maven testing in forked mode)
		
		
		final Manifest manifest = toFile(url).getManifest();
        if (manifest != null) {
            String classPath = manifest.getMainAttributes().getValue(new Attributes.Name("Class-Path"));
            if (classPath != null) {
            	Set<URL> additionals = new LinkedHashSet<URL>();
                for (String entry : classPath.split(" ")) {
                	
                	try {
                    	if (URI.create(entry).getScheme()!=null)
                			additionals.add(new URL(entry));
                	}
            		catch(Exception e) {
            			log.error("cannot process Class-Path entry "+entry,e);
            		}
                }
                return additionals;
            }
        }

        return Collections.emptySet();
	}
	
	@Override
	public Collection<ClasspathResource> scan(URL url) throws Exception {
		
		return scanner.scan(toFile(url));
	}
	
	protected abstract JarFile toFile(URL url) throws Exception;
}
