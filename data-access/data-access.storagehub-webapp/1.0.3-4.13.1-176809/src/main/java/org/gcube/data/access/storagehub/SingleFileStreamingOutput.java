package org.gcube.data.access.storagehub;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SingleFileStreamingOutput implements StreamingOutput {

	private static final Logger log = LoggerFactory.getLogger(SingleFileStreamingOutput.class);


	InputStream streamToWrite;

	public SingleFileStreamingOutput(InputStream streamToWrite) {
		super();
		this.streamToWrite = streamToWrite;
	}

	/**
	 * Overriding the write method to write request data directly to Jersey outputStream .
	 * @param outputStream
	 * @throws IOException
	 * @throws WebApplicationException
	 */
	@Override
	public void write(OutputStream outputStream) throws IOException, WebApplicationException {
		log.debug("writing StreamOutput");
		copy(streamToWrite, outputStream);
		log.debug("StreamOutput written");
	}

	private void copy(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[2048];
		int readcount = 0;
		while ((readcount=in.read(buffer))!=-1) {
			out.write(buffer, 0, readcount);
		}
	}

}