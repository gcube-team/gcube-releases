/**
 * 
 */
package org.gcube.portlets.user.statisticalalgorithmsimporter.server.file;

import org.apache.commons.fileupload.ProgressListener;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.file.FileUploadMonitor;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.file.FileUploadState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class FileUploadListener implements ProgressListener {
	
	private Logger logger=LoggerFactory.getLogger(FileUploadListener.class);

	private FileUploadMonitor fileUploadMonitor;
	
	private long num100Ks = 0;
	private long theBytesRead = 0;
	private long theContentLength = -1;
	private int whichItem = 0;
	private int percentDone = 0;
	private boolean contentLengthKnown = false;
	
	
	
	public FileUploadListener(FileUploadMonitor fileUploadMonitor) {
		logger.debug("FileUploadListener created");
		this.fileUploadMonitor=fileUploadMonitor;
		//this.session=session;
		
	}

	/**
	 * {@inheritDoc}
	 */
	public void update(long bytesRead, long contentLength, int items) {
		if (contentLength > -1) {
			contentLengthKnown = true;
		}
		theBytesRead = bytesRead;
		theContentLength = contentLength;
		whichItem = items;

		long nowNum100Ks = bytesRead / 100000;
		// Only run this code once every 100K
		if (nowNum100Ks > num100Ks) {
			num100Ks = nowNum100Ks;
			if (contentLengthKnown) {
				percentDone = (int) Math.round(100.00 * bytesRead / contentLength);
			}
			
		}	
		
		
		fileUploadMonitor.setTotalLenght(contentLength);
		fileUploadMonitor.setElaboratedLenght(bytesRead);
		fileUploadMonitor.setPercentDone(Float.valueOf(percentDone)/100);
		
		fileUploadMonitor.setState(FileUploadState.INPROGRESS);
		//logger.debug("File Upload: "+fileUploadMonitor.toString());
	}

	@Override
	public String toString() {
		return "FileUploadListener [fileUploadMonitor=" + fileUploadMonitor
				+ ", num100Ks=" + num100Ks + ", theBytesRead=" + theBytesRead
				+ ", theContentLength=" + theContentLength + ", whichItem="
				+ whichItem + ", percentDone=" + percentDone
				+ ", contentLengthKnown=" + contentLengthKnown + "]";
	}
	
}
