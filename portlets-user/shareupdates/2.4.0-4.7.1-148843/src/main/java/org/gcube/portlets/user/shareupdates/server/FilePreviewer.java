package org.gcube.portlets.user.shareupdates.server;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import net.coobird.thumbnailator.Thumbnails;

import org.apache.commons.fileupload.util.Streams;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.Detector;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.gcube.applicationsupportlayer.social.storage.FTPManager;
import org.gcube.common.portal.PortalContext;
import org.gcube.portal.databook.shared.ImageType;
import org.gcube.portlets.user.shareupdates.shared.LinkPreview;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;
import com.sun.pdfview.PDFParseException;
/**
 * 
 * @author Massimiliano Assante, ISTI-CNR
 * 
 * Parse files and returns an image preview plus description
 *
 */
public class FilePreviewer {
	private static Logger _log = LoggerFactory.getLogger(FilePreviewer.class);

	private static final String PDF_DEFAULT_IMAGE = "default/pdf.png";
	private static final String GENERICFILE_DEFAULT_IMAGE = "default/default_generic.png";
	private static final String UPLOAD_LOCATION_LOCAL = System.getProperty("java.io.tmpdir");

	/**
	 * these are the extension for which I have an icon image preview
	 */
	private static final String[] handledextensionImages = {"css", "csv", "doc", "docx", "java", "mdb", "mp3", "pdf", "ppt", "pptx", "psd", "rar", "tex", "txt", "xls", "xlsx", "zip"};

	private static FTPManager getFTPManager(String context) {
		return FTPManager.getInstance(context);
	}

	/**
	 * 
	 * @param fileNameLabel thename of the file
	 * @param path2Pdf the path of the pdf file
	 * @param httpUrl the http url where the file is reachable at
	 * @param context the current infrastructure context (scope)
	 * @return
	 * @throws Exception
	 */
	protected static LinkPreview getPdfPreview(String fileName, String path2Pdf, String httpUrl, String mimeType, String context) throws Exception {		
		ArrayList<String> imagesUrl = new ArrayList<String>();
		//description
		String desc = null;
		try {
			desc = getPDFDescription(path2Pdf);
		}
		catch (Exception ex) {
			_log.warn("PDF Parse exception, returning no description");
			desc = "";
		}
		//thumbnail preview
		File pdfFile = new File(path2Pdf);

		RandomAccessFile raf = new RandomAccessFile(pdfFile, "r");
		FileChannel channel = raf.getChannel();
		ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
		PDFFile pdf = null;
		try {
			pdf = new PDFFile(buf);		
		} catch (PDFParseException ex) {
			raf.close();
			_log.error("PDF Parse exception, returning default pdf image");

			imagesUrl.add(getFTPManager(context).getBaseURL()+PDF_DEFAULT_IMAGE);
			return new LinkPreview(fileName, desc, httpUrl, mimeType, imagesUrl);
		}
		PDFPage page = pdf.getPage(0);

		int width = (int) page.getBBox().getWidth();
		int height = (int) page.getBBox().getHeight();

		int scaledWidth = width/8;
		int scaledHeight = height/8;

		// create the image
		Rectangle rect = new Rectangle(0, 0, width, height);

		BufferedImage bufferedImage = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_RGB);

		Image image = page.getImage(scaledWidth, scaledHeight, rect, null, true, true);                    

		Graphics2D bufImageGraphics = bufferedImage.createGraphics();
		bufImageGraphics.drawImage(image, 0, 0, scaledWidth, scaledHeight, null);

		//thumbnail previes are very small in this case we can use in-memory streams
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		boolean result = ImageIO.write(bufferedImage, "JPG", out);
		raf.close();

		if (result) {
			String httpLink = getFTPManager(context).uploadImageOnFTPServer(new ByteArrayInputStream(out.toByteArray()), ImageType.JPG);
			_log.debug("PDF thumbnail available at: " + httpLink);
			imagesUrl.add(httpLink);
			return new LinkPreview(fileName, desc, httpUrl, mimeType, imagesUrl);
		}
		else
			throw new IOException("Could not process pdf file");
	}
	/**
	 * 
	 * @param fileNameLabel thename of the file
	 * @param path2Image the path of the image file
	 * @param httpUrl the http url where the file is reachable at
	 * @return
	 * @throws Exception
	 */
	protected static LinkPreview getImagePreview(String fileName, String path2Image, String httpUrl, String mimeType, String context) {
		ArrayList<String> imagesUrl = new ArrayList<String>();

		Dimension dim;
		ByteArrayOutputStream out = null;
		String desc = "";
		try {
			dim = extractDimension(path2Image);

			//description
			desc = ((int) dim.getWidth()) + "x" + ((int) dim.getHeight()) + " pixels";

			out = new ByteArrayOutputStream();

			// get the buffered image
			BufferedImage bufferedImage = Thumbnails.of(path2Image).scale(1).asBufferedImage();

			bufferedImage = changeBackgroundColorToWhite(bufferedImage);
			
			// write
			Thumbnails.fromImages(Arrays.asList(bufferedImage))
			.width(80)
			.outputFormat("jpg")
			.toOutputStream(out);

		}
		catch (IOException e) {
			_log.warn("Thumbnail extraction failed for this reason: " + e.getMessage());
		}
		String httpLink = getFTPManager(context).uploadImageOnFTPServer(new ByteArrayInputStream(out.toByteArray()), ImageType.JPG);
		_log.debug("\nFlushed, Image thumbnail available at: " + httpLink);
		imagesUrl.add(httpLink);
		return new LinkPreview(fileName, desc, httpUrl, mimeType, imagesUrl);
	}

	private static BufferedImage changeBackgroundColorToWhite(BufferedImage bufferedImage){

		BufferedImage changedImage = null;
		try{
			// if the image has a transparent background color, it will be changed to white
			if(bufferedImage.getColorModel().getTransparency() != Transparency.OPAQUE) {
				_log.debug("Background color is going to be changed to white for the thumbnail");
				int w = bufferedImage.getWidth();
				int h = bufferedImage.getHeight();
				changedImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
				Graphics2D g = changedImage.createGraphics();
				g.setColor(Color.WHITE);
				g.fillRect(0,0,w,h);
				g.drawRenderedImage(bufferedImage, null);
				g.dispose(); 
			}
		}catch(Exception e){
			_log.warn("Failed to change the transparent background to white for image");
		}
		return changedImage == null ? bufferedImage : changedImage;
	}

	/**
	 * 
	 * @param fileNameLabel thename of the file
	 * @param path2Pdf the path of the pdf file
	 * @param httpUrl the http url where the file is reachable at
	 * @return
	 * @throws Exception
	 */
	protected static LinkPreview getUnhandledTypePreview(String fileName, String path2Pdf, String httpUrl, String mimeType, String context) throws Exception {		

		ArrayList<String> imagesUrl = new ArrayList<String>();
		String extension = getExtension(fileName);
		//no description
		String desc = "";
		if (extension == null)
			imagesUrl.add(getFTPManager(context).getBaseURL()+GENERICFILE_DEFAULT_IMAGE);
		else {
			int foundIndex = Arrays.binarySearch(handledextensionImages, extension);
			if (foundIndex < 0)
				imagesUrl.add(getFTPManager(context).getBaseURL()+GENERICFILE_DEFAULT_IMAGE);
			else
				imagesUrl.add(getFTPManager(context).getBaseURL()+"default/"+extension+".png");
		}
		return new LinkPreview(fileName, desc, httpUrl, mimeType, imagesUrl);
	}

	private static String getExtension(String fileName) {
		int lastDot = fileName.lastIndexOf(".");
		String extension = fileName.substring(lastDot+1);
		_log.debug("EXTENSION FOUND =  " + extension);
		return extension;
	}
	/**
	 * 
	 * @param path2File
	 * @return
	 * @throws Exception
	 */
	private static String getPDFDescription(String path2File) throws Exception {
		PDDocument doc = PDDocument.load(path2File);
		PDFTextStripper stripper = new PDFTextStripper();
		//only first page text
		stripper.setStartPage(1);
		stripper.setEndPage(1);
		String text = stripper.getText(doc);
		String toReturn = (text.length() > 300) ? text.substring(0, 295) + " ... " : text;
		doc.close();
		return toReturn;
	}
	/**
	 * extract the dimension in pixels without reading the whole file 
	 * @param path2Image
	 * @return
	 * @throws IOException
	 */
	private static Dimension extractDimension(String path2Image) throws IOException {
		ImageInputStream in = ImageIO.createImageInputStream(new File(path2Image));
		try {
			final Iterator<ImageReader> readers = ImageIO.getImageReaders(in);
			if (readers.hasNext()) {
				ImageReader reader = readers.next();
				try {
					reader.setInput(in);
					return new Dimension(reader.getWidth(0), reader.getHeight(0));
				} finally {
					reader.dispose();
				}
			}
		} finally {
			if (in != null) in.close();
		}
		return null;
	}

	/**
	 * 
	 * @param file
	 * @return
	 * @throws IOException 
	 * @throws MagicParseException
	 * @throws MagicMatchNotFoundException
	 * @throws MagicException
	 */
	protected static String getMimeType(File file, String filenameWithExtension) throws IOException {
		TikaConfig config = TikaConfig.getDefaultConfig();
		Detector detector = config.getDetector();
		TikaInputStream stream = TikaInputStream.get(file);
		Metadata metadata = new Metadata();
		metadata.add(Metadata.RESOURCE_NAME_KEY, filenameWithExtension);
		MediaType mediaType = detector.detect(stream, metadata);
		return mediaType.getBaseType().toString();
	}

	/**
	 * Check if a resource at a given url exists or not. If it exists, create a file in the temp dir
	 * of the tomcat
	 * @param urlThumbnail
	 * @return
	 */
	public static File storeAndGetFile(String urlThumbnail){
		try {
			HttpURLConnection.setFollowRedirects(true);
			HttpURLConnection con =
					(HttpURLConnection) new URL(urlThumbnail).openConnection();
			con.setRequestMethod("HEAD"); // body is not needed yet
			if(con.getResponseCode() == HttpURLConnection.HTTP_OK){

				//generate the random dir 
				File theRandomDir = new File(UPLOAD_LOCATION_LOCAL + File.separator + UUID.randomUUID().toString());
				theRandomDir.mkdir();
				theRandomDir.deleteOnExit();
				_log.debug("Created temp upload directory in: " + theRandomDir);

				// generate a random file name and create it under the randomDir
				File file = new File(theRandomDir, UUID.randomUUID().toString().substring(0, 10)); 
				file.deleteOnExit();

				//Get the inputstream and copy there
				URL url = new URL(urlThumbnail);
				Streams.copy(url.openStream(), new FileOutputStream(file), true);
				_log.debug("File is at " + file.getAbsolutePath());

				return file;
			}else
				return null;
		}
		catch (Exception e) {
			_log.error("The resource at url " + urlThumbnail + " doesn't exist");
			return null;
		}
	}

	/**
	 * When a link preview needs to be saved, the method save the thumbnail on the storage no longer using the external url
	 * @param urlThumbnail
	 * @return the url of the thumbnail saved on the storage or null in case of error
	 */
	public static String saveThumbnailOnFTPAndGetUrl(String urlThumbnail, String context) {
		File localFile;
		if((localFile = storeAndGetFile(urlThumbnail)) != null){
			String mimeType = null;
			if (ShareUpdateServiceImpl.isWithinPortal()) {
				try {
					mimeType = FilePreviewer.getMimeType(localFile, localFile.getName());
					String thumbnailUrlFTP = null;
					switch(mimeType){
					case "image/png":
					case "image/gif":
					case "image/tiff":
					case "image/jpg":
					case "image/jpeg":
					case "image/bmp":
						thumbnailUrlFTP = FilePreviewer.getImagePreview(localFile.getName(), localFile.getAbsolutePath(), null, mimeType, context).getImageUrls().get(0);
						break;
					default: break;
					}
					return thumbnailUrlFTP;
				} catch (IOException e) {
					_log.error("Error while saving thumbnail on ftp", e);
				}
			}
		}else
			_log.warn("the file at url " + urlThumbnail + " doesn't exist");

		return null;
	}
}
