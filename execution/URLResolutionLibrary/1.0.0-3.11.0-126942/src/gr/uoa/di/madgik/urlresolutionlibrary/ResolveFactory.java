package gr.uoa.di.madgik.urlresolutionlibrary;

import gr.uoa.di.madgik.urlresolutionlibrary.exceptions.LocatorException;
import gr.uoa.di.madgik.urlresolutionlibrary.exceptions.ParseException;
import gr.uoa.di.madgik.urlresolutionlibrary.exceptions.URLResolverException;
import gr.uoa.di.madgik.urlresolutionlibrary.nonstreamable.BittorrentLocator;
import gr.uoa.di.madgik.urlresolutionlibrary.nonstreamable.GridFTPLocator;
import gr.uoa.di.madgik.urlresolutionlibrary.nonstreamable.NonStreamable;
import gr.uoa.di.madgik.urlresolutionlibrary.nonstreamable.StreamableDecorator;
import gr.uoa.di.madgik.urlresolutionlibrary.streamable.FTPLocator;
import gr.uoa.di.madgik.urlresolutionlibrary.streamable.HTTPLocator;
import gr.uoa.di.madgik.urlresolutionlibrary.streamable.LocalFileLocator;
import gr.uoa.di.madgik.urlresolutionlibrary.streamable.SFTPLocator;
import gr.uoa.di.madgik.urlresolutionlibrary.streamable.Streamable;

/**
 * 
 * @author Alex Antoniadis
 * 
 */
public class ResolveFactory {

	private static ILocator getLocator(String url) throws ParseException {
		if (url.toLowerCase().startsWith(FTPLocator.FTP_PROTOCOL)
				|| url.toLowerCase().startsWith(FTPLocator.FTPS_PROTOCOL)) {
			return new FTPLocator(url);
		} else if (url.toLowerCase().startsWith(SFTPLocator.SFTP_PROTOCOL)) {
			return new SFTPLocator(url);
		} else if (url.toLowerCase().startsWith(HTTPLocator.HTTPS_PROTOCOL)) {
			return new HTTPLocator(url);
		} else if (url.toLowerCase().startsWith(GridFTPLocator.GRIDFTP_PROTOCOL)) {
			return new GridFTPLocator(url);
		} else if (url.toLowerCase().startsWith(BittorrentLocator.BITTORRENT_PROTOCOL)) {
			return new BittorrentLocator(url);
		} else if (url.toLowerCase().startsWith(LocalFileLocator.LOCALFILE_PROTOCOL)) {
			return new LocalFileLocator(url);
		}
		return null;

	}

	public static Streamable getStreamableLocator(String url) throws URLResolverException {
		ILocator loc = getLocator(url);
		Streamable sloc = null;
		if (loc instanceof NonStreamable)
			sloc = new StreamableDecorator((NonStreamable) loc);
		else if (loc instanceof Streamable)
			sloc = (Streamable) loc;
		else
			throw new LocatorException("this type of locator " + url + " cannot be converted to streamable");

		return sloc;
	}
}
