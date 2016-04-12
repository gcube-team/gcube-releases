package gr.uoa.di.madgik.urlresolutionlibrary.streamable;

import gr.uoa.di.madgik.urlresolutionlibrary.ILocator;

import java.io.InputStream;

/**
 * 
 * @author Alex Antoniadis
 * 
 */
public interface Streamable extends ILocator {
	public InputStream getInputStream() throws Exception;

	public void close();
}
