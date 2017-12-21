package org.gcube.contentmanagement.blobstorage.transport.backend.util;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.input.ProxyInputStream;
import com.mongodb.MongoClient;

/**
 * 
 * Generates a input stream and close the mongo connection
 * @author Roberto Cirillo (ISTI - CNR)
 *
 */
public class MongoInputStream extends ProxyInputStream{
	
	private MongoClient mongo;
	private boolean closed;


	public MongoInputStream(MongoClient mongo, InputStream proxy) {
		super(proxy);
		this.mongo=mongo;
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void close(){
		if(!isClosed()){
			try {
				super.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (mongo!=null)
				mongo.close();
			setClosed(true);
		}
	}
	
	public boolean isClosed() {
		return closed;
	}

	public void setClosed(boolean closed) {
		this.closed = closed;
	}
	
	 public int read() throws IOException {
        int n = in.read();
        if (n == -1) {
            close();
        }
        return n;
     }
		 	
    /**
     * Reads and returns bytes from the underlying input stream to the given
     * buffer. If the underlying stream returns -1, the {@link #close()} method
     * i called to automatically close and discard the stream.
     *
     * @param b buffer to which bytes from the stream are written
     * @return number of bytes read, or -1 if no more bytes are available
     * @throws IOException if the stream could not be read or closed
     */
    public int read(byte[] b) throws IOException {
        int n = in.read(b);
        if (n == -1) {
            close();
        }
        return n;
    }

    /**
     * Reads and returns bytes from the underlying input stream to the given
     * buffer. If the underlying stream returns -1, the {@link #close()} method
     * i called to automatically close and discard the stream.
     *
     * @param b buffer to which bytes from the stream are written
     * @param off start offset within the buffer
     * @param len maximum number of bytes to read
     * @return number of bytes read, or -1 if no more bytes are available
     * @throws IOException if the stream could not be read or closed
     */
    public int read(byte[] b, int off, int len) throws IOException {
        int n = in.read(b, off, len);
        if (n == -1) {
            close();
       
        }
        return n;
    }

    /**
     * Ensures that the stream is closed before it gets garbage-collected.
     * As mentioned in {@link #close()}, this is a no-op if the stream has
     * already been closed.
     * @throws Throwable if an error occurs
     */
    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }
}
