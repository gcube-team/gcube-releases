/**
 * 
 */
package org.gcube.common.homelibrary.util.zip.zipmodel;

import java.io.File;
import java.io.InputStream;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class ZipFile extends AbstractZipItem {
	
	protected File contentFile;
	protected InputStream contentStream;

	/**
	 * Create a new zip file type.
	 * @param parent the file parent
	 * @param contentFile the content file.
	 * @param name the file name.
	 * @param comment the file comment.
	 * @param extra the file extra field.
	 */
	public ZipFile(ZipFolder parent, File contentFile, String name,String comment, byte[] extra) {
		super(parent, name, ZipItemType.FILE, comment, extra);
		this.contentFile = contentFile;
	}
	
	/**
	 * Create a new zip file type.
	 * @param parent the file parent
	 * @param contentStream the content stream.
	 * @param name the file name.
	 * @param comment the file comment.
	 * @param extra the file extra field.
	 */
	public ZipFile(ZipFolder parent, InputStream contentStream, String name,String comment) {
		super(parent, name, ZipItemType.FILE, comment, new byte[0]);
		this.contentStream = contentStream;
	}
	
	/**
	 * Create a new zip file type.
	 * @param contentStream the content stream.
	 * @param name the file name.
	 * @param comment the file comment.
	 */
	public ZipFile(InputStream contentStream, String name,String comment) {
		super(name, ZipItemType.FILE, comment);
		this.contentStream = contentStream;
	}

	/**
	 * Get content file
	 * @return the contentFile
	 */
	public File getContentFile() {
		return contentFile;
	}
	
	/**
	 * Returns the content stream.
	 * @return the content stream
	 */
	public InputStream getContentStream(){
		return contentStream;
	}

}
