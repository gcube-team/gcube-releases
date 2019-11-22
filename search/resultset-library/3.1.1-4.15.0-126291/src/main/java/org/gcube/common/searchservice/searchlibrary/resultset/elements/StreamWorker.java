package org.gcube.common.searchservice.searchlibrary.resultset.elements;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;

import org.apache.log4j.Logger;
import org.gcube.common.searchservice.searchlibrary.resultset.ResultSet;
import org.gcube.common.searchservice.searchlibrary.resultset.helpers.RSFileHelper;

/**
 * Serves result set parts to requesting socket
 * 
 * @author UoA
 */
public class StreamWorker extends Thread {
	/**
	 * The Logger this class uses
	 */
	private static Logger log = Logger.getLogger(StreamWorker.class);
	/**
	 * The socket used
	 */
	private Socket sock;

	/**
	 * Initializes a new instance
	 * 
	 * @param sock the socket to use
	 */
	public StreamWorker(Socket sock){
		this.sock=sock;
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run(){
		InputStream in= null;
		OutputStream out=null;
		DataInputStream din=null;
		DataOutputStream dout=null;
		try{
			in= sock.getInputStream();
			din=new DataInputStream(in);
			int headerSize=din.readInt();
			byte []header=new byte[headerSize];
			din.readFully(header);
			String headerName=new String(header);

			int keySize=din.readInt();
			byte []key=new byte[keySize];
			if (keySize > 0){
				din.readFully(key);
			}
			out=sock.getOutputStream();
			dout=new DataOutputStream(out);
			ResultSet rs= null;
			if (keySize >0){
				byte[] rawkey = new sun.misc.BASE64Decoder().decodeBuffer(new String(key));
				PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(rawkey);
	            KeyFactory factory = KeyFactory.getInstance("RSA");
				RSAPrivateKey pk = (RSAPrivateKey)factory.generatePrivate(spec);
				rs = new ResultSet(headerName,pk);
			}else
				rs= new ResultSet(headerName);
		
			try{

				while(true){
					try{
						dout.writeInt(-1);
						RSFileHelper.copy(rs, rs.getCurrentContentPartName(),dout);
					}catch(Exception e){
						log.error("Could not copy current content part. Continuing",e);
					}
					if(!rs.getNextPart(-1)) break;
				}
			}catch(Exception e){
				log.error("Could not complete streaming of parts. closing",e);
			}
			rs.clear();
			dout.writeInt(-2);
		}catch(Exception e){
			log.error("Could not complete socket transfer. exiting",e);
		}
		try{
			if (din!=null) din.close();
		}catch(Exception e){
			log.error("Could not close data input stream.continuing",e);
		}
		try{
			if (in!=null) in.close();
		}catch(Exception e){
			log.error("Could not close input stream.continuing",e);
		}
		try{
			if(out!=null) out.close();
		}catch(Exception e){
			log.error("Could not close output stream.continuing",e);
		}
		try{
			if(dout!=null) dout.close();
		}catch(Exception e){
			log.error("Could not close data output stream.continuing",e);
		}
		try{
			this.sock.close();
		}catch(Exception e){
			log.error("Could not close socket.continuing",e);
		}
	}
}
