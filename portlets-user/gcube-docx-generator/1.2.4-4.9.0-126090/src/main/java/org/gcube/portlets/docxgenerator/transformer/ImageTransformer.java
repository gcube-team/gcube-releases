package org.gcube.portlets.docxgenerator.transformer;



import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.net.ssl.HttpsURLConnection;

import org.apache.avalon.framework.component.Component;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;
import org.gcube.portlets.d4sreporting.common.shared.BasicComponent;
import org.gcube.portlets.d4sreporting.common.shared.Metadata;
import org.gcube.portlets.docxgenerator.content.Content;
import org.gcube.portlets.docxgenerator.content.PContent;
import org.gcube.portlets.docxgenerator.content.RContent;
import org.gcube.portlets.docxgenerator.utils.IDGenerator;
import org.gcube.portlets.docxgenerator.utils.SSLRelaxer;




/**
 * Transforms an image source InputComponent into a Content object.
 * 
 * @author Luca Santocono
 * 
 */
public class ImageTransformer implements Transformer {

	private static final Log log = LogFactory.getLog(ImageTransformer.class);
	private final String hostName;
	
	public ImageTransformer(String hostName) {
		this.hostName = hostName;
	}

	/**
	 * @see it.cnr.isti.docxgenerator.transformer.Transformer#transform(Component,
	 *      org.docx4j.openpackaging.packages.WordprocessingMLPackage)
	 * 
	 * @param component
	 *            Source InputComponent that is going to be transformed.
	 * @param wmlPack
	 *            WordprocessingMLPackage object, which represents a docx
	 *            archive. Passed to insert intermediate data if needed during
	 *            the transformation.
	 * @return A Content object that can be inserted in the docx archive.
	 * 
	 */
	@Override
	public ArrayList<Content> transform(final BasicComponent component,
			final WordprocessingMLPackage wmlPack) {
		PContent content = null;
		RContent rcontent = new RContent();
		BinaryPartAbstractImage imagePart;
		Inline inline;
		
		try {
			content = new PContent();
		
			String possibleContet = (String)component.getPossibleContent();
			byte[] imgBytes = null;
			if (possibleContet.startsWith("http")){
				Image image = createImageFromUrl((String)component.getPossibleContent());
				imgBytes = transformImageToBytes(image);
			} else {
				String[] contentBase64 = possibleContet.split(",");
				String imgBase64 =  contentBase64[contentBase64.length - 1];
				imgBytes = Base64.decodeBase64(imgBase64.getBytes());
			}
			
			imagePart = BinaryPartAbstractImage.createImagePart(wmlPack,
					imgBytes);
			// inline = imagePart.createImageInline(null, null, IDGenerator
			// .imageIdGenerator(), IDGenerator.imageIdGenerator());
			inline = imagePart.createImageInline(null, null, IDGenerator
					.imageIdGenerator(), IDGenerator.imageIdGenerator(), component.getWidth() * 15, false);
			rcontent.insertImage(inline);
			content.addRun(rcontent);
			
		} catch (Exception e) {
			log.warn("Cannot fetch image at "
					+ component.getPossibleContent());
			e.printStackTrace();
		}
		
		ArrayList<Content> list = new ArrayList<Content>();
		list.add(content);
		
		PContent pMetadata = new PContent();
		pMetadata.setStyle("Attribute");
		RContent r = new RContent();
		List<Metadata> metadataList = component.getMetadata();
		String text = "";
		for (Metadata metadata : metadataList ) {
			if (metadata.getValue() != null) { 
				String attribute = metadata.getAttribute();
				String firstChar = attribute.substring(0,1).toUpperCase();
				text +=  firstChar + attribute.substring(1) + ": " + metadata.getValue() + "  ";
			}
		}
		r.addText(text);
		pMetadata.addContent(r);
		list.add(pMetadata);
		
		return list;
	}

	@SuppressWarnings("unused")
	private byte[] readImageFromFS(String path) throws IOException {
		File file = new File(path);
		java.io.InputStream is = new java.io.FileInputStream(file);
		long length = file.length();
		byte[] bytes = new byte[(int) length];
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length
				&& (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}
		is.close();
		return bytes;
	}

	/**
	 * Transforms an Image object into an array of bytes.
	 * 
	 * @param image
	 *            The image that is transformed
	 * @return The resulting byte array.
	 * @throws IOException
	 *             if there are problems in reading/writing on the filesystem.
	 */
	public byte[] transformImageToBytes(final Image image) throws IOException {
		BufferedImage bu = new BufferedImage(image.getWidth(null), image
				.getHeight(null), BufferedImage.TYPE_INT_RGB);
		Graphics g = bu.getGraphics();
		g.drawImage(image, 0, 0, null);
		g.dispose();
		ByteArrayOutputStream bas = new ByteArrayOutputStream();
		ImageIO.write(bu, "png", bas);
		return bas.toByteArray();
	}

	/**
	 * Returns an Image object from a given URL.
	 * 
	 * @param urlPath
	 *            The URL to the image.
	 * @return an Image object representing the image at the given URL.
	 * @throws IOException
	 *             if there are problems in reading/writing on the filesystem.
	 */
	public Image createImageFromUrl(String urlPath) throws Exception {
		log.warn("*************\nurlPath:" + urlPath);
		
		
		if (urlPath.startsWith("/")) {
			
			urlPath = urlPath.replace("/", File.separator);
			urlPath = hostName + urlPath;
			log.info("Fetching image from FileSystem " + urlPath);
						
			InputStream in = new FileInputStream(urlPath);
			return ImageIO.read(in);
		}
		
		urlPath = urlPath.replace("https", "http");
		URL url = new URL(urlPath);
//		// This is where you'd define the proxy's host name and port.
//		SocketAddress address = new InetSocketAddress(url.getHost(), url
//				.getDefaultPort());
//		
//		
//		
//        SSLContext sslContext = SSLContext.getInstance("SSL");
//
//        // set up a TrustManager that trusts everything
//        sslContext.init(null, new TrustManager[] { new X509TrustManager() {
//        	public X509Certificate[] getAcceptedIssuers() {
//        		//System.out.println("getAcceptedIssuers =============");
//        		
//        		log.debug("################################## getAcceptedIssuers() ####################");
//        		return null;
//        	}
//
//        	public void checkClientTrusted(X509Certificate[] certs,
//        			String authType) {
//        		log.debug("##################################### checkClientTrusted ###################");
//        	}
//
//
//        	@Override
//        	public void checkServerTrusted(
//        			java.security.cert.X509Certificate[] chain,
//        			String authType) throws CertificateException {
//        		log.debug("##################################### checkServerTrusted ####################");
//        		// TODO Auto-generated method stub
//
//        	}
//        } }, new SecureRandom());
//
//        HttpsURLConnection.setDefaultSSLSocketFactory(
//        		sslContext.getSocketFactory());
//
//        HttpsURLConnection
//        .setDefaultHostnameVerifier(new HostnameVerifier() {
//        	public boolean verify(String arg0, SSLSession arg1) {
//        		System.out.println("hostnameVerifier =============");
//        		return true;
//        	}
//        });

		InputStream inStream = null;
		if (url.getProtocol().equalsIgnoreCase("http")) {
			 HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
			 httpURLConnection.setRequestMethod("GET");
			 httpURLConnection.setDoInput(true);
			 httpURLConnection.setDoOutput(true);
			 httpURLConnection.setUseCaches(false);
			 log.debug("################################### Fetching image with HTTP request ####################");
			 inStream = httpURLConnection.getInputStream();
		 }
		 else {
			 HttpsURLConnection httpsURLconnection =  (HttpsURLConnection)url.openConnection();
			 SSLRelaxer.trustAllHostnames(httpsURLconnection);
			 SSLRelaxer.trustAllHttpsCertificates(httpsURLconnection);
			 httpsURLconnection.setDoInput(true);
			 httpsURLconnection.setDoOutput(true);
			 httpsURLconnection.setUseCaches(false);
			 log.debug("################################# Fetching image with HTTPS request ####################");
			 inStream = httpsURLconnection.getInputStream();
		 }

      //  Proxy proxy = new Proxy(Proxy.Type.HTTP, address);
       // URLConnection conn = url.openConnection();

		Image image = ImageIO.read(inStream);
		return image;
	}
	

}
