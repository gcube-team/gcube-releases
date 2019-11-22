package org.gcube.data.transfer.library.model;

import java.net.HttpURLConnection;
import java.net.URL;

import org.gcube.data.transfer.library.faults.InvalidSourceException;

public class URLSource extends Source<URL> {


	private URL theURL=null;


	public URLSource(URL theURL) throws InvalidSourceException {
		super();
		if(theURL==null) throw new InvalidSourceException("URL Source cannot be null");
		this.theURL = theURL;
	}

	@Override
	public boolean validate() throws InvalidSourceException {
		try{
			HttpURLConnection conn=(HttpURLConnection) theURL.openConnection();
			conn.setConnectTimeout(3000);
			conn.setRequestMethod("HEAD");
			int responseCode = conn.getResponseCode();
			return (200 <= responseCode && responseCode <= 399);
		}catch(Exception e){
			throw new InvalidSourceException("Unable to contact URL "+theURL,e);
		}
	}

	@Override
	public void prepare() {
		// nothing to do

	}

	@Override
	public void clean() {
		// nothing to do
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("URLSource [theURL=");
		builder.append(theURL);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public URL getTheSource() {
		return theURL;
	}

	
}
