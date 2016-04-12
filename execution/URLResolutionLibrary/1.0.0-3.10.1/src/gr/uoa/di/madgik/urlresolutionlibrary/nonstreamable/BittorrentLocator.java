package gr.uoa.di.madgik.urlresolutionlibrary.nonstreamable;

import gr.uoa.di.madgik.urlresolutionlibrary.ResolveFactory;
import gr.uoa.di.madgik.urlresolutionlibrary.exceptions.ParseException;
import gr.uoa.di.madgik.urlresolutionlibrary.streamable.Streamable;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bitlet.wetorrent.Metafile;
import org.bitlet.wetorrent.Torrent;
import org.bitlet.wetorrent.disk.PlainFileSystemTorrentDisk;
import org.bitlet.wetorrent.disk.TorrentDisk;
import org.bitlet.wetorrent.peer.IncomingPeerListener;

/**
 * 
 * @author Alex Antoniadis
 * 
 */
public class BittorrentLocator implements NonStreamable {
	private static Logger logger = Logger.getLogger(BittorrentLocator.class.getName());

	public static final String BITTORRENT_PROTOCOL = "bittorrent";
	private static final int DEFAULT_BITTORENT_PORT = 6881;

	private int port = DEFAULT_BITTORENT_PORT;
	private String torrentUrl;
	private Torrent torrent;
	private IncomingPeerListener peerListener;
	private String filename;
	private String tempDir;

	public BittorrentLocator(String url) throws ParseException {
		parseURI(url);
	}

	/**
	 * Parses the url
	 * 
	 * @param url
	 *            The url in format bittorrent://path[filename]
	 * @throws ParseException
	 */
	private void parseURI(String url) throws ParseException {
		String tmpUri = url.trim();

		if (!tmpUri.startsWith(BITTORRENT_PROTOCOL + "://"))
			throw new ParseException("url is not in bittorrent format");

		tmpUri = tmpUri.substring((BITTORRENT_PROTOCOL + "://").length());

		int listIdx = tmpUri.lastIndexOf('[');
		if (listIdx > 0) {
			this.torrentUrl = tmpUri.substring(0, listIdx);
			this.filename = tmpUri.substring(listIdx + 1, tmpUri.length() - 1);
		} else {
			this.torrentUrl = tmpUri;
		}

		logger.log(Level.INFO, "TorrentUrl : " + torrentUrl);
		logger.log(Level.INFO, "File : " + filename);
	}

	public void init() throws Exception {
		Streamable locator = ResolveFactory.getStreamableLocator(torrentUrl);
		Metafile metafile = new Metafile(new BufferedInputStream(((Streamable) locator).getInputStream()));

		locator.close();

		/*
		 * Create the torrent disk, this is the destination where the torrent
		 * file/s will be saved
		 */

		File tempDir = File.createTempFile("torrent-", "-downloads");
		
		if(!(tempDir.delete()))
	        throw new IOException("Could not delete temp file: " + tempDir.getAbsolutePath());
		if(!(tempDir.mkdir()))
	        throw new IOException("Could not create temp directory: " + tempDir.getAbsolutePath());
		
		TorrentDisk tdisk = new PlainFileSystemTorrentDisk(metafile, tempDir);
		tdisk.init();

		peerListener = new IncomingPeerListener(this.port);
		peerListener.start();

		torrent = new Torrent(metafile, tdisk, peerListener);

		
		if (filename == null)
			this.filename = tempDir.getAbsolutePath() + "/" + metafile.getName();
		else
			this.filename = tempDir.getAbsolutePath() + "/" + metafile.getName() + "/" + filename;
		
		this.tempDir = tempDir.getAbsolutePath();
	}

	@Override
	public void download() throws Exception {
		this.init();

		torrent.startDownload();
		while (!torrent.isCompleted()) {

			try {
				Thread.sleep(1000);
			} catch (InterruptedException ie) {
				break;
			}

			torrent.tick();
			logger.log(Level.INFO, "Got " + torrent.getPeersManager().getActivePeersNumber() + " peers, completed " + torrent.getTorrentDisk().getCompleted() + " bytes");
		}

		logger.log(Level.INFO, "Download finished");
		torrent.interrupt();
		peerListener.interrupt();
	}

	@Override
	public File getFile() {
		return new File(this.filename);
	}
	
	public File getTempDir() {
		return new File(this.tempDir);
	}
}
