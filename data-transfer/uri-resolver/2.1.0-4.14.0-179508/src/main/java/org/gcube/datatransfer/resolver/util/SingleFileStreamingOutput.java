package org.gcube.datatransfer.resolver.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * The Class SingleFileStreamingOutput.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Nov 15, 2018
 */
public class SingleFileStreamingOutput implements StreamingOutput {

	private static final Logger log = LoggerFactory.getLogger(SingleFileStreamingOutput.class);


	InputStream streamToWrite;

	/**
	 * Instantiates a new single file streaming output.
	 *
	 * @param streamToWrite the stream to write
	 */
	public SingleFileStreamingOutput(InputStream streamToWrite) {
		super();
		this.streamToWrite = streamToWrite;
	}

	/**
	 * Overriding the write method to write request data directly to Jersey outputStream .
	 *
	 * @param outputStream the output stream
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws WebApplicationException the web application exception
	 */
	@Override
	public void write(OutputStream outputStream) throws IOException, WebApplicationException {
		log.debug("writing StreamOutput");
		copy(streamToWrite, outputStream);
		log.debug("StreamOutput written");
	}

	/**
	 * Copy.
	 *
	 * @param in the in
	 * @param out the out
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void copy(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[2048];
		int readcount = 0;
		while ((readcount=in.read(buffer))!=-1) {
			out.write(buffer, 0, readcount);
		}
	}

}