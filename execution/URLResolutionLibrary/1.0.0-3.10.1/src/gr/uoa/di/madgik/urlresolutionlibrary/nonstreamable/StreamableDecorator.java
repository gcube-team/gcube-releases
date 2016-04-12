package gr.uoa.di.madgik.urlresolutionlibrary.nonstreamable;

import gr.uoa.di.madgik.urlresolutionlibrary.streamable.Streamable;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author Alex Antoniadis
 * 
 */
public class StreamableDecorator implements Streamable {
	private static Logger logger = Logger.getLogger(StreamableDecorator.class.getName());
	
	private NonStreamable locator;
	private InputStream is;
	private File file;

	public StreamableDecorator(NonStreamable locator) {
		this.locator = locator;
	}

	@Override
	public InputStream getInputStream() throws Exception {
		locator.download();
		file = locator.getFile();

		if (file.isDirectory()) {
			file.delete();
			throw new Exception("Directory can not be streamed");
		}
		is = new FileInputStream(file);
		
		return is;
	}

	@Override
	public void close() {
		try {
			file.delete();
		} catch (Exception e) {
			logger.log(Level.WARNING, "Streamable Decorator file deletion failed", e);
		}
		
		if (locator instanceof BittorrentLocator) {
			try {
				((BittorrentLocator) locator).getTempDir().delete();
			} catch (Exception e) {
				logger.log(Level.WARNING, "Streamable Decorator bittorent temporary directory deletion failed", e);
			}
		}
		try {
			is.close();
		} catch (Exception e) {
			logger.log(Level.WARNING, "Streamable Decorator inputstream close failed", e);
		}
	}
}
