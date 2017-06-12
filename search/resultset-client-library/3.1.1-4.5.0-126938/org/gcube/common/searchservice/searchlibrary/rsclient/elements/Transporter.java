package org.gcube.common.searchservice.searchlibrary.rsclient.elements;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.PrivateKey;
import java.util.Calendar;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.apache.log4j.Logger;
import org.gcube.common.searchservice.resultsetservice.stubs.CanStreamResponse;
import org.gcube.common.searchservice.searchlibrary.resultset.helpers.RSConstants;
import org.gcube.common.searchservice.searchlibrary.resultset.helpers.RSFileHelper;
import org.gcube.common.searchservice.searchlibrary.rsreader.RSReader;
import org.gcube.common.searchservice.searchlibrary.rswriter.RSFullWriter;

import sun.misc.BASE64Decoder;

/**
 * Helper class used to move payload part from a remote machine to the local one
 * 
 * @author UoA
 */
public class Transporter {
	/**
	 * The Logger used by this class
	 */
	private static Logger log = Logger.getLogger(Transporter.class);

	/**
	 * Localizes the remote current payload part spliting it to smaller files
	 * and encoding it using Base64 encoding
	 * 
	 * @param reader
	 *            The reader pointing to the
	 *            {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 *            whose current content part must be localized
	 * @return The name of the local file holding the localized payload
	 * @throws Exception
	 *             An unrecoverable for the operation error occured
	 */
	public static String transportEncoded(RSReader reader) throws Exception {
		FileWriter fw = null;
		BufferedWriter bw = null;
		FileReader fr = null;
		BufferedReader br = null;
		FileInputStream in = null;
		FileOutputStream out = null;

		try {
			String[] pages = reader.getLocationIndependent().splitEncoded();
			String[] localPages = new String[pages.length];
			String decodedFile = null;
			for (int i = 0; i < pages.length; i += 1) {
				String pageFile = RSFileHelper.generateName(
						RSConstants.PAGEDCONTENT, null);
				localPages[i] = pageFile;
				fw = new FileWriter(new File(pageFile));
				bw = new BufferedWriter(fw);
				bw.write(reader.getLocationIndependent().getFileContent(
						pages[i]));
				bw.flush();
				bw.close();
				bw = null;
				fw.close();
				fw = null;
			}
			String encodedFile = RSFileHelper.generateName(
					RSConstants.PAGEDCONTENT, null);
			log.trace("Transcoded file: " + encodedFile);
			fw = new FileWriter(encodedFile);
			bw = new BufferedWriter(fw);
			for (int i = 0; i < localPages.length; i += 1) {
				fr = new FileReader(localPages[i]);
				br = new BufferedReader(fr);
				String line = br.readLine();
				while (line != null) {
					bw.write(line);
					line = br.readLine();
				}
				bw.flush();
				br.close();
				br = null;
				fr.close();
				fr = null;
			}
			bw.close();
			bw = null;
			fw.close();
			fw = null;
			decodedFile = RSFileHelper.generateName(RSConstants.CONTENT, null);
			BASE64Decoder decoder = new BASE64Decoder();
			in = new FileInputStream(new File(encodedFile));
			out = new FileOutputStream(new File(decodedFile));
			decoder.decodeBuffer(in, out);
			out.flush();
			in.close();
			in = null;
			out.close();
			out = null;
			// System.out.println("Encoded subparts "+localPages.length);
			for (int i = 0; i < localPages.length; i += 1) {
				try {
					new File(localPages[i]).delete();
				} catch (Exception e) {
					log.error("Could not remove file " + localPages[i]
							+ ". Continuing", e);
				}
			}
			try {
				new File(encodedFile).delete();
			} catch (Exception e) {
				log.error("Could not remove file " + encodedFile
						+ ". Continuing", e);
			}
			return decodedFile;
		} catch (Exception e) {
			if (fw != null)
				fw.close();
			if (bw != null)
				bw.close();
			if (fr != null)
				fr.close();
			if (br != null)
				br.close();
			if (in != null)
				in.close();
			if (out != null)
				out.close();
			log.error("Could not transport encoded. Throwing Exception", e);
			throw new Exception(
					"Could not transport encoded. Throwing Exception");
		}
	}

	/**
	 * Localizes the remote current payload part spliting it to smaller files
	 * 
	 * @param reader
	 *            The reader pointing to the
	 *            {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 *            whose current content part must be localized
	 * @return The name of the local file holding the localized payload
	 * @throws Exception
	 *             An unrecoverable for the operation error occured
	 */
	public static String transportClear(RSReader reader) throws Exception {
		FileWriter fw = null;
		BufferedWriter bw = null;
		FileReader fr = null;
		BufferedReader br = null;
		try {
			String[] pages = reader.getLocationIndependent().splitClear();
			String[] localPages = new String[pages.length];
			for (int i = 0; i < pages.length; i += 1) {
				String pageFile = RSFileHelper.generateName(
						RSConstants.PAGEDCONTENT, null);
				localPages[i] = pageFile;
				fw = new FileWriter(new File(pageFile));
				bw = new BufferedWriter(fw);
				bw.write(reader.getLocationIndependent().getFileContent(
						pages[i]));
				bw.flush();
				bw.close();
				bw = null;
				fw.close();
				fw = null;
			}
			String encodedFile = RSFileHelper.generateName(
					RSConstants.PAGEDCONTENT, null);
			fw = new FileWriter(encodedFile);
			bw = new BufferedWriter(fw);
			for (int i = 0; i < localPages.length; i += 1) {
				fr = new FileReader(localPages[i]);
				br = new BufferedReader(fr);
				String line = br.readLine();
				while (line != null) {
					bw.write(line);
					line = br.readLine();
				}
				bw.flush();
				br.close();
				br = null;
				fr.close();
				fr = null;
			}
			bw.close();
			bw = null;
			fw.close();
			fw = null;
			// System.out.println("Clear subparts "+localPages.length);
			for (int i = 0; i < localPages.length; i += 1) {
				try {
					new File(localPages[i]).delete();
				} catch (Exception e) {
					log.error("Could not remove file " + localPages[i]
							+ ". Continuing", e);
				}
			}
			return encodedFile;
		} catch (Exception e) {
			if (fw != null)
				fw.close();
			if (bw != null)
				bw.close();
			if (fr != null)
				fr.close();
			if (br != null)
				br.close();
			log.error("Could not transport clear. Throwing Exception", e);
			throw new Exception("Could not transport clear. Throwing Exception");
		}
	}

	/**
	 * transports the payload file through SOAP Attachment if supported
	 * 
	 * @param reader
	 *            The reader pointing to the
	 *            {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 *            whose current content part must be localized
	 * @return The name of the local file holding the localized payload
	 * @throws Exception
	 *             An unrecoverable for the operation error occured
	 */
	public static String transportAttached(RSReader reader) throws Exception {
		try {
			return reader.getLocationIndependent().canAttach();
		} catch (Exception e) {
			log.error("Could not transport attached. Throwing Exception", e);
			throw new Exception(
					"Could not transport attached. Throwing Exception");
		}
	}

	/**
	 * transport the full remote result set through stream
	 * 
	 * @param reader
	 *            the reader to read from
	 * @param writer
	 *            the writer to write to
	 * @param portStatic
	 *            the port to use
	 * @param SSLsupport
	 *            SSL support to use
	 * @throws Exception
	 *             An unrecoverable for the operation error occured
	 */
	public static void transportStream(RSReader reader, RSFullWriter writer,
			int portStatic, boolean SSLsupport) throws Exception {
		long startLocal = Calendar.getInstance().getTimeInMillis();
		InputStream in = null;
		OutputStream out = null;
		DataOutputStream dout = null;
		DataInputStream din = null;
		Socket skt = null;
		try {
			long startPort = Calendar.getInstance().getTimeInMillis();
			int port = portStatic;
			boolean SSL = SSLsupport;
			if (port == 0) {
				CanStreamResponse streamInfo = reader.getLocationIndependent()
						.getStreamPort();
				port = streamInfo.getPort();
				SSL = streamInfo.isSSLsupport();
			}
			log.debug("port definition took "
					+ (Calendar.getInstance().getTimeInMillis() - startPort));
			// int port=reader.getLocationIndependent().getStreamPort();
			String ip = null;
			try {
				ip = reader.getLocationIndependent().getHostName();
			} catch (Throwable e) {
				log.debug("could not retrieve host name. trying ip", e);
				ip = reader.getLocationIndependent().getHostIP();
			}
			log.debug("transporting stream from ip:port " + ip + ":" + port
					+ " using SSL: " + SSL);
			if (SSL) {

				SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory
						.getDefault();

				SSLSocket SSLskt = null;
				SSLskt = (SSLSocket) sslsocketfactory.createSocket(ip, port);
				String[] enable = { "TLS_DH_anon_WITH_AES_128_CBC_SHA" };
				SSLskt.setEnabledCipherSuites(enable);
				log.debug("SSL protocols setup");
				skt = SSLskt;
			} else {
				skt = new Socket(ip, port);
			}
			in = skt.getInputStream();
			out = skt.getOutputStream();
			dout = new DataOutputStream(out);
			din = new DataInputStream(in);
			String head = reader.getLocationIndependent().getHeadFileName();
			dout.writeInt(head.getBytes().length);
			dout.write(head.getBytes());
			dout.flush();
			PrivateKey pKey = reader.getRSLocator().getPrivKey();
			if (pKey != null) {
				byte[] pk = new sun.misc.BASE64Encoder().encode(
						pKey.getEncoded()).getBytes();
				dout.writeInt(pk.length);
				dout.write(pk);
			} else {
				dout.writeInt(0);
			}

			int token = din.readInt();
			if (token != -1) {
				writer.endAuthoring();
				log.error("unexpected token " + token + ".");
				throw new Exception("unexpected token " + token
						+ ". protocol error.");
			}
			int count = 0;
			while (true) {
				count += 1;
				try {
					String targetFile = RSFileHelper.generateName(
							RSConstants.CONTENT, null);
					token = readPart(din, targetFile);
					writer.wrapFile(targetFile);
					writer.startNewPart();
				} catch (Exception e) {
					writer.endAuthoring();
					log
							.error(
									"error while reading part content. throwing exception",
									e);
					throw new Exception("error while reading part content");
				}
				if (count == 1)
					log
							.info("first chunk available in "
									+ (Calendar.getInstance().getTimeInMillis() - startLocal)
									+ " millis");
				if (token == -2) {
					writer.endAuthoring();
					break;
				} else if (token != -1) {
					writer.endAuthoring();
					log.error("unexpected token " + token + ".");
					throw new Exception("unexpected token " + token
							+ ". protocol error.");
				}
			}
		} catch (Exception e) {
			log.error("Cannot transport streamed. throwing exception", e);
			throw new Exception("Cannot transport streamed");
		}
		try {
			if (in != null)
				in.close();
		} catch (Exception e) {
			log.error("Could not close input stream.continuing", e);
		}
		try {
			if (out != null)
				out.close();
		} catch (Exception e) {
			log.error("Could not close output stream.continuing", e);
		}
		try {
			if (dout != null)
				dout.close();
		} catch (Exception e) {
			log.error("Could not close data output stream.continuing", e);
		}
		try {
			if (din != null)
				din.close();
		} catch (Exception e) {
			log.error("Could not close data input stream.continuing", e);
		}
		try {
			if (skt != null)
				skt.close();
		} catch (Exception e) {
			log.error("Could not close socket.continuing", e);
		}
	}

	/**
	 * reads a part through the stream
	 * 
	 * @param din
	 *            the stream
	 * @param outFile
	 *            the file to dump the part
	 * @return the finalizing token read
	 * @throws Exception
	 *             an unrecoverable for the operation error occured
	 */
	private static int readPart(DataInputStream din, String outFile)
			throws Exception {
		FileOutputStream fout = new FileOutputStream(new File(outFile));
		int chunkSize = -2;
		try {
			while (true) {
				chunkSize = din.readInt();
				if (chunkSize < 0)
					break;
				byte[] chunk = new byte[chunkSize];
				din.readFully(chunk, 0, chunkSize);
				fout.write(chunk);
			}
			fout.close();
			return chunkSize;
		} catch (Exception e) {
			log
					.error(
							"Could not complete stream reading of part. closing and throwoinfg exception",
							e);
			fout.close();
			throw new Exception("Could not complete stram reading of part.");
		}
	}
}
