package org.gcube.common.scan.resources;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class JarEntryResource implements ClasspathResource {

	private final ZipEntry entry;
	private final JarFile file;
	
	public JarEntryResource(JarFile file,ZipEntry entry) {
		this.file=file;
		this.entry=entry;
	}
	
	@Override
	public String name() {
		String entryName = entry.getName();
		return entryName.substring(entryName.lastIndexOf("/") + 1);
	}
	
	public String path() {
		String path = entry.getName();
		if (path.endsWith(".class"))
			path = path.replace("/",".");
		return path;
	}
	
	@Override
	public InputStream stream() throws Exception {
		return file.getInputStream(entry);
	}
	
	@Override
	public File file() throws Exception {

		System.out.println(this);
		//can only copy into temp file to then create jarfile on
		File file = File.createTempFile("scanned",".jarEntry");
		
		FileOutputStream out = new FileOutputStream(file);
		InputStream stream = stream();
		
		int read = 0; 
		byte[] bytes = new byte[1024];
		while((read=stream.read(bytes))!=-1)
		 out.write(bytes,0,read);
	
		stream.close();
		out.close();
		out.flush();
		return file;			
	
	}
	
	@Override
	public String toString() {
		return "jar-entry:"+path();
	}

}
