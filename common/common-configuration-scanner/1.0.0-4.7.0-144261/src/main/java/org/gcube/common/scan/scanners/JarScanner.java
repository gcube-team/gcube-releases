package org.gcube.common.scan.scanners;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.gcube.common.scan.resources.ClasspathResource;
import org.gcube.common.scan.resources.JarEntryResource;


public class JarScanner {

	
	public Collection<ClasspathResource> scan(JarFile file) throws Exception {
		
		Collection<ClasspathResource> resources = new ArrayList<ClasspathResource>();
		
		Enumeration<? extends ZipEntry> entries = file.entries();
		
		while (entries.hasMoreElements()) {
			ZipEntry entry = entries.nextElement();
			if (!entry.isDirectory())
                resources.add(new JarEntryResource(file,entry));
        }
		
		return resources;
	}

}
