package org.gcube.common.homelibrary.jcr.workspace.util;

import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.jcr.repository.external.GCUBEStorage;
import org.gcube.common.homelibrary.util.MimeTypeUtil;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.text.pdf.PdfReader;

import ij.ImagePlus;
import ij.io.FileSaver;
import ij.process.ImageProcessor;


public class WorkspaceItemUtil {


	private static Logger logger = LoggerFactory.getLogger(WorkspaceItemUtil.class);

	private static final int THUMB_MAX_DIM 				= 400;
	//	private static final int THUMB_HEIGHT	 			= 200;
	//	private static final int THUMB_WIDTH 				= 281;
	//	private static final String NO_PREVIEW_IMAGE 		= "/no-thumbnail.png";

	//	public static final String AUTHOR = "Author";
	//	public static final String PRODUCER = "Producer";
	//	public static final String TITLE = "Title";
	//	public static final String VERSION = "hl-version";
	//	public static final String NUMBER_OF_PAGES = "hl-numberOfPages";

	public static final String NUMBER_OF_PAGES 		= "xmpTPg:NPages";
	public static final String PRODUCER 			= "producer";
	public static final String VERSION 				= "version";
	public static final String AUTHOR 				= "Author";
	public static final String TITLE 				= "dc:title";

	public static final Object WIDTH 				= "width";
	public static final Object HEIGHT 				= "height";


	//	public static ImagePlus getImgePlus(InputStream imageData) throws IOException {
	//
	//		//necessary to run without WINDOWS X11
	//		System.setProperty("java.awt.headless","true");
	//
	//		File tmpImgFile = null;
	//		ImagePlus img = null;
	//		try{
	//			tmpImgFile = File.createTempFile("IMG", "TMP");
	//			IOUtils.copy(imageData, new FileOutputStream(tmpImgFile));
	//			img = new ImagePlus(tmpImgFile.getAbsolutePath());		
	//		}catch (Exception e) {
	//			throw new RuntimeException(e);
	//		}finally{
	//			if (tmpImgFile!=null)
	//				tmpImgFile.deleteOnExit();
	//		}
	//
	//		return img;
	//
	//	}

	public static ImagePlus getImgePlus(String pathOrURL) throws IOException {

		//necessary to run without WINDOWS X11
		System.setProperty("java.awt.headless","true");

		ImagePlus img = null;
		try{
			img = new ImagePlus(pathOrURL);		
		}catch (Exception e) {
			throw new RuntimeException(e);
		}
		return img;
	}

	public static int[] getThumbnailDimension(int original_width, int original_height) {
		int new_width = 0;
		int new_height = 0;
		
		if ((original_width < THUMB_MAX_DIM) &&  (original_height< THUMB_MAX_DIM)){
			new_width = original_width;
			new_height = original_height;
		}
		if (original_width > THUMB_MAX_DIM) {
			new_width = THUMB_MAX_DIM;
			new_height = (new_width * original_height) / original_width;
		}

		if (original_width < THUMB_MAX_DIM) {
			new_width = THUMB_MAX_DIM;
			new_height = (new_width * original_height) / original_width;
		}

		if (new_height > THUMB_MAX_DIM) {
			new_height = THUMB_MAX_DIM;
			new_width = (new_height * original_width) / original_height;
		}

		if (new_width > THUMB_MAX_DIM) {
			new_width = THUMB_MAX_DIM;
			new_height = (new_width * original_height) / original_width;
		}

		int[] dimension = {new_width, new_height};

		return dimension;
	}

	public static InputStream getThumbnailAsPng(ImagePlus img, int[] thumbSize) throws IOException {
		return getThumbnailAsPng(img, thumbSize[0], thumbSize[1]); 
	}

	public static InputStream getThumbnailAsPng(ImagePlus img, int thumbWidth, 
			int thumbHeight) throws IOException {

		InputStream stream = null;
		ImageProcessor processor = img.getProcessor();
		try{
			Image thumb = processor.resize(thumbWidth, thumbHeight).createImage();
			thumb = thumb.getScaledInstance(thumbWidth,thumbHeight,Image.SCALE_SMOOTH);

			FileSaver fs = new FileSaver(new ImagePlus("",thumb));
			File tmpThumbFile = File.createTempFile("THUMB", "TMP");
			tmpThumbFile.deleteOnExit();

			fs.saveAsPng(tmpThumbFile.getAbsolutePath());
			stream =  new FileInputStream(tmpThumbFile);

		}catch (Exception e) {
			throw new RuntimeException(e);
		}
		return stream;
	}

	public static HashMap<String, String> getPDFInfo(final InputStream is) {
		final HashMap<String, String> info = new HashMap<String, String>();
		Thread th = new Thread(){
			@Override
			public void run()
			{	
				try {

					PdfReader reader = new PdfReader(is);					
					info.put(NUMBER_OF_PAGES, String.valueOf(reader.getNumberOfPages()));
					info.put(VERSION, String.valueOf(reader.getPdfVersion()));
					info.putAll(reader.getInfo());

				} catch (Exception e) {
					logger.error("PDF info has not been retrieved",e);
				}

			}
		};

		th.start();
		try {
			th.join();
		} catch (InterruptedException e) {
			logger.error("InternalError in getPDFInfo ",e);
		}

		checkPDFInfo(info);
		return info;
	}

	private static void checkPDFInfo(HashMap<String,String> info) {

		if (!info.containsKey(AUTHOR))
			info.put(AUTHOR,"n/a");

		if (!info.containsKey(PRODUCER))
			info.put(PRODUCER,"n/a");

		if (!info.containsKey(TITLE))
			info.put(TITLE,"n/a");

		if (!info.containsKey(VERSION))
			info.put(VERSION,"n/a");

		if (info.containsKey(NUMBER_OF_PAGES)){
			try {
				int pages = Integer.parseInt(info.get(NUMBER_OF_PAGES));
				info.put(NUMBER_OF_PAGES, String.valueOf(pages));
			} catch(NumberFormatException nfe) {

			}
		}else
			info.put(NUMBER_OF_PAGES, "0");
	}


	//	/**
	//	 * Set image info
	//	 * @param stream
	//	 * @return
	//	 */
	//	public static ImageMeta getImageInfo(InputStream stream) {
	//
	//		ImageMeta imageInfo = new ImageMeta();
	//
	//		int[] thumbnailSize = new int[2];
	//		int width = 0;
	//		int height = 0;
	//
	//		try{
	//
	//			SimpleImageInfo info = new SimpleImageInfo(stream);
	//			width =  info.getWidth();
	//			height =  info.getHeight();
	//
	//			thumbnailSize = WorkspaceItemUtil.getThumbnailDimension(width, height);
	//
	//		}catch (Exception e) {
	//			thumbnailSize[0] = THUMB_WIDTH;
	//			thumbnailSize[1] = THUMB_HEIGHT;
	//		}
	//
	//		try {
	//			if (stream!= null)
	//				stream.close();
	//		} catch (IOException e) {
	//			logger.error("tmp file alredy closed");
	//		}
	//
	//		imageInfo.setHeight(height);
	//		imageInfo.setWidth(width);
	//		imageInfo.setThumbnailSize(thumbnailSize);
	//
	//		return imageInfo;
	//
	//	}



	//	private static HashMap<String,String> getMeta(InputStream is) throws IOException, SAXException, TikaException {
	//
	//		long start = System.currentTimeMillis();
	//		HashMap<String,String> map = new HashMap<String, String>();
	//
	//		Parser parser = new AutoDetectParser();
	//		BodyContentHandler handler = new BodyContentHandler(-1);
	//		Metadata metadata = new Metadata();
	//
	//		parser.parse(is, handler, metadata, new ParseContext());
	//		for (String name : metadata.names()) {
	//			logger.info(name + ":\t" + metadata.get(name));
	//			map.put(name, metadata.get(name));
	//		}
	//		logger.info(String.format(
	//				"------------ Processing took %s millis\n\n",
	//				System.currentTimeMillis() - start));
	//
	//		return map;
	//	}



	//	/**
	//	 * Set pdf info
	//	 * @param stream
	//	 * @return
	//	 * @throws IOException 
	//	 */
	//	public static PdfMeta getPdfInfo(InputStream stream) throws IOException{
	//
	//		PdfMeta pdfInfo = new PdfMeta();
	//
	//		Map<String,String> infoPDF = getPDFInfo(stream);
	//		int numberOfPages = Integer.parseInt(infoPDF.get(WorkspaceItemUtil.NUMBER_OF_PAGES));
	//		String version = infoPDF.get(WorkspaceItemUtil.VERSION);
	//		String author = infoPDF.get(WorkspaceItemUtil.AUTHOR);
	//		String title = infoPDF.get(WorkspaceItemUtil.TITLE);
	//		String producer = infoPDF.get(WorkspaceItemUtil.PRODUCER);
	//
	//		pdfInfo.setNumberOfPages(numberOfPages);
	//		pdfInfo.setVersion(version);
	//		pdfInfo.setAuthor(author);
	//		pdfInfo.setTitle(title);
	//		pdfInfo.setProducer(producer);
	//
	//		return pdfInfo;
	//
	//	}

	/**
	 * Get metadata info and save to storage using the same inpustream
	 * @param stream
	 * @param storage
	 * @param name
	 * @param remotePath
	 * @param filenameWithExtension 
	 * @return
	 * @throws InternalErrorException
	 * @throws IOException
	 */
	public static MetaInfo getMetadataInfo(InputStream stream, final GCUBEStorage storage, final String remotePath, final String filenameWithExtension) throws RemoteBackendException, IOException, InternalErrorException{

		final MultipleOutputStream mos = new MultipleOutputStream(stream);
		final MetaInfo metadataInfo = new MetaInfo();

		//save to storage
		Thread t1 = new Thread(){
			public void run(){
				logger.debug("Save file to Storage in remotepath " + remotePath);
				long start = System.currentTimeMillis();

				try(InputStream is = mos.getS1()){
					String url = null;
					int availableSize = is.available();
					try {
						url = storage.putStream(is,remotePath);
						metadataInfo.setStorageId(url);
						logger.info(filenameWithExtension + " saved to " + remotePath + " - GCUBEStorage URL : " + url);				
						logger.debug("Get size from Storage for remotepath " + remotePath);
						long size =	storage.getRemoteFileSize(remotePath);
						if (size < availableSize)
							logger.error("size < available for file " + remotePath);
						else
							metadataInfo.setSize((int) size);
					} catch (RemoteBackendException e) {
						logger.error(remotePath + " remote path not present" + e);
						throw new RemoteBackendException(e.getMessage());
					}

				} catch (IOException e) {
					throw new RemoteBackendException(e.getMessage());
				} 
				logger.info(Thread.currentThread().getName()+"- "+(System.currentTimeMillis()-start));
			}
		};

		//get mimetype utility
		Thread t2 = new Thread(){
			public void run(){
				logger.debug("Mimetype detect for file " + filenameWithExtension);
				long start = System.currentTimeMillis();

				try(InputStream is = mos.getS2()){
					String mimetype = MimeTypeUtil.getMimeType(filenameWithExtension, is);	
					logger.debug(filenameWithExtension + " mimetyepe: " + mimetype);
					//					System.out.println("MIMETYPE " + mimetype);
					metadataInfo.setMimeType(mimetype);
				} catch (IOException e) {
					throw new RemoteBackendException(e.getMessage());
				}

				logger.info(Thread.currentThread().getName()+"- "+(System.currentTimeMillis()-start));
			}
		};

		t1.start();
		t2.start();

		try {
			mos.startWriting();
			t1.join();
			t2.join();
		} catch (Exception e) {
			throw new InternalErrorException(e.getMessage());
		}

		//update mimetype metadata in storage
		if (metadataInfo.getStorageId()!=null && metadataInfo.getMimeType()!=null) {
			logger.info("Update mimetype metadata for remotepath: " + remotePath + " to "+ metadataInfo.getMimeType());
			storage.setMetaInfo("mimetype", metadataInfo.getMimeType(), remotePath);
		}
		return metadataInfo;
	}

}
