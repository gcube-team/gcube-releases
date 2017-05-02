package org.gcube.contentmanagement.blobstorage.transport.backend.util;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.commons.io.output.ProxyOutputStream;
import com.mongodb.MongoClient;

public class MongoOutputStream extends ProxyOutputStream {
   
	          
	private MongoClient mongo;
	private boolean closed;


	public MongoOutputStream(MongoClient mongo, OutputStream proxy) {
		super(proxy);
		this.mongo=mongo;
		// TODO Auto-generated constructor stub
	}
	
      
    /**
     * Invokes the delegate's <code>write(int)</code> method.
     * @param idx the byte to write
     * @throws IOException if an I/O error occurs
     */
    public void write(int idx) throws IOException {
        out.write(idx);
    }
  
    /**
     * Invokes the delegate's <code>write(byte[])</code> method.
     * @param bts the bytes to write
     * @throws IOException if an I/O error occurs
     */
    public void write(byte[] bts) throws IOException {
        out.write(bts);
    }
  
    /**
     * Invokes the delegate's <code>write(byte[])</code> method.
     * @param bts the bytes to write
     * @param st The start offset
     * @param end The number of bytes to write
     * @throws IOException if an I/O error occurs
     */
    public void write(byte[] bts, int st, int end) throws IOException {
        out.write(bts, st, end);
    }
       /**
     * Invokes the delegate's <code>flush()</code> method.
     * @throws IOException if an I/O error occurs
     */
    public void flush() throws IOException {
        out.flush();
    }
    /**
     * Invokes the delegate's <code>close()</code> method.
     * @throws IOException if an I/O error occurs
     */
    public void close() throws IOException {
    	if(!isClosed()){
			try {
				super.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mongo.close();
			setClosed(true);
		}
    }
    
    public void setClosed(boolean closed) {
		this.closed = closed;
	}
    
    public boolean isClosed() {
		return closed;
	}
  
}
