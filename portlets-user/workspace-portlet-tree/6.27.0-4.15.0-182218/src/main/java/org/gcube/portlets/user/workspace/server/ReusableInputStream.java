/**
 *
 */

package org.gcube.portlets.user.workspace.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * The Class ReusableInputStream.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * 
 *         Jul 30, 2019
 */
public class ReusableInputStream extends InputStream {

	private InputStream input;
	private ByteArrayOutputStream output;
	private ByteBuffer buffer;

	/**
	 * Instantiates a new reusable input stream.
	 *
	 * @param input the input
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public ReusableInputStream(InputStream input) throws IOException {

		this.input = input;
		// Note: it's resizable anyway.
		this.output = new ByteArrayOutputStream(input.available());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#read()
	 */
	@Override
	public int read() throws IOException {

		byte[] b = new byte[1];
		read(b, 0, 1);
		return b[0];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#read(byte[])
	 */
	@Override
	public int read(byte[] bytes) throws IOException {

		return read(bytes, 0, bytes.length);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#read(byte[], int, int)
	 */
	@Override
	public int read(byte[] bytes, int offset, int length) throws IOException {

		if (buffer == null) {
			int read = input.read(bytes, offset, length);
			if (read <= 0) {
				input.close();
				input = null;
				buffer = ByteBuffer.wrap(output.toByteArray());
				output = null;
				return -1;
			} else {
				output.write(bytes, offset, read);
				return read;
			}
		} else {
			int read = Math.min(length, buffer.remaining());
			if (read <= 0) {
				buffer.flip();
				return -1;
			} else {
				buffer.get(bytes, offset, read);
				return read;
			}
		}
	}
	// You might want to @Override flush(), close(), etc to delegate to input.
}
