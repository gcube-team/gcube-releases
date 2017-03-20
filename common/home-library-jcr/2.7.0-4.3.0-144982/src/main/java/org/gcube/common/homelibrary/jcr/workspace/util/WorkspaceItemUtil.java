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
	public static MetaInfo getMetadataInfo(InputStream stream, GCUBEStorage storage, String remotePath, String filenameWithExtension, String mimeType, long size) throws RemoteBackendException, IOException, InternalErrorException{

		if (mimeType!=null && !mimeType.isEmpty()){
			
			final MetaInfo metadataInfo = new MetaInfo(); 
			metadataInfo.setMimeType(mimeType);
			metadataInfo.setSize((int) size);
			logger.debug("Save file to Storage in remotepath " + remotePath);
//			System.out.println("***** REMOTE PATH " + remotePath);

			saveToStorage(stream, storage, remotePath, metadataInfo, filenameWithExtension);
			
			logger.debug("Set Mimetype for file " + filenameWithExtension + " to " + mimeType);	

			updateMimeTypeIntoStorage(storage, metadataInfo, remotePath, filenameWithExtension);

			return metadataInfo;
		}else
			return getMetadataInfo(stream, storage, remotePath, filenameWithExtension);
	}

	/**
	 * Update mimetype into storage
	 * @param storage
	 * @param metadataInfo
	 * @param remotePath
	 * @param filenameWithExtension
	 */
	private static void updateMimeTypeIntoStorage(GCUBEStorage storage, MetaInfo metadataInfo, String remotePath, String filenameWithExtension) {
		long start = System.currentTimeMillis();
		//update mimetype metadata in storage
		if (metadataInfo.getStorageId()!=null && metadataInfo.getMimeType()!=null) {
			logger.debug("Update mimetype metadata for remotepath: " + remotePath + " to "+ metadataInfo.getMimeType());
			storage.setMetaInfo("mimetype", metadataInfo.getMimeType(), remotePath);
		}
//		System.out.println("**** UPDATE Mimetype into storage for file " + filenameWithExtension + " in milliseconds: "+ (System.currentTimeMillis()-start));
		logger.debug("UPDATE Mimetype into storage for file " + filenameWithExtension + " in milliseconds: "+ (System.currentTimeMillis()-start));

	}

	/**
	 * Save to Storage, get size and set mimetype
	 * @param is
	 * @param storage
	 * @param remotePath
	 * @param metadataInfo
	 * @param filenameWithExtension
	 * @throws IOException
	 */
	private static void saveToStorage(InputStream is, GCUBEStorage storage, String remotePath, MetaInfo metadataInfo, String filenameWithExtension) throws IOException {
		long start = System.currentTimeMillis();
		String url = null;
		int availableSize = is.available();
		try {
			url = storage.putStream(is,remotePath);
			metadataInfo.setStorageId(url);
			metadataInfo.setRemotePath(remotePath);
			logger.debug(filenameWithExtension + " saved to " + remotePath + " - GCUBEStorage URL : " + url);
//			System.out.println("**** A - File " + remotePath + " saved into Storage in milliseconds: "+ (System.currentTimeMillis()-start));
			
			long start00 = System.currentTimeMillis();
			if (metadataInfo.getSize()<=0){
				long size = storage.getRemoteFileSize(remotePath);
				
//				System.out.println("SIZE " +  size);
				if (size < availableSize)
					logger.error("size < available for file " + remotePath);
				else
					metadataInfo.setSize((int) size);			
			}
//			System.out.println("**** SIZE - Getting size from Storage for file " + remotePath + " in milliseconds: "+ (System.currentTimeMillis()-start00));

		} catch (RemoteBackendException e) {
			logger.error(remotePath + " remote path not present " + e);
			throw new RemoteBackendException(e.getMessage());
		}

	}

	/**
	 * Save inpustream into storage and get metadata
	 * @param stream
	 * @param storage
	 * @param remotePath
	 * @param filenameWithExtension
	 * @return
	 * @throws RemoteBackendException
	 * @throws IOException
	 * @throws InternalErrorException
	 */
	public static MetaInfo getMetadataInfo(final InputStream stream, final GCUBEStorage storage, final String remotePath, final String filenameWithExtension) throws RemoteBackendException, IOException, InternalErrorException{
//	System.out.println("/n");
//		System.out.println("***** REMOTE PATH " + remotePath);
	
		final MultipleOutputStream mos = new MultipleOutputStream(stream);
		final MetaInfo metadataInfo = new MetaInfo();

		//save to storage
		Thread t1 = new Thread(){
			public void run(){
//				long start = System.currentTimeMillis();
//							System.out.println("*** START STEP A - Save file to Storage in remotepath " + remotePath);
				logger.debug("Save file to Storage in remotepath " + remotePath);

				try(InputStream is = mos.getS1()){
					saveToStorage(is, storage, remotePath, metadataInfo, filenameWithExtension);
				} catch (IOException e) {
					throw new RemoteBackendException(e.getMessage());
				} 
//				System.out.println("**** STORAGE - Save file to Storage in remotepath " + remotePath + " in milliseconds: "+ (System.currentTimeMillis()-start));
			}
		};

		//get mimetype utility
		Thread t2 = new Thread(){
			public void run(){
//									System.out.println("*** START STEP 1B - Detect Mimetype for file " + filenameWithExtension);
				logger.debug("Mimetype detect for file " + filenameWithExtension);
				long start = System.currentTimeMillis();

				try(InputStream is = mos.getS2()){
					String mimetype = MimeTypeUtil.getMimeType(filenameWithExtension, is);	
					logger.debug(filenameWithExtension + " mimetyepe: " + mimetype);
//										System.out.println("MIMETYPE " + mimetype);
					metadataInfo.setMimeType(mimetype);
				} catch (IOException e) {
					throw new RemoteBackendException(e.getMessage());
				}
//				System.out.println("**** MIMETYPE - detected for file " + filenameWithExtension + " in milliseconds: "+ (System.currentTimeMillis()-start));

				logger.debug("Mimetype detected for file " + filenameWithExtension + " in milliseconds: "+ (System.currentTimeMillis()-start));
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

		updateMimeTypeIntoStorage(storage, metadataInfo, remotePath, filenameWithExtension);

		return metadataInfo;
	}


}
