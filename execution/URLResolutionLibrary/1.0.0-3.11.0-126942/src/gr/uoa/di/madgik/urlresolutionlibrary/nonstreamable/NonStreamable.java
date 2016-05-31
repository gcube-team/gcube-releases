package gr.uoa.di.madgik.urlresolutionlibrary.nonstreamable;

import gr.uoa.di.madgik.urlresolutionlibrary.ILocator;

import java.io.File;

/**
 * 
 * @author Alex Antoniadis
 * 
 */
public interface NonStreamable extends ILocator {
	public void download() throws Exception;

	public File getFile();
}
