package org.gcube.common.homelibrary.jcr.workspace.util;

import ij.ImagePlus;
import ij.io.FileSaver;
import ij.process.ImageProcessor;

import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.text.pdf.PdfReader;


public class WorkspaceItemUtil {


	private static Logger logger = LoggerFactory.getLogger(WorkspaceItemUtil.class);

	private static final int THUMB_MAX_DIM = 400;

	public static final String AUTHOR = "Author";
	public static final String PRODUCER = "Producer";
	public static final String TITLE = "Title";
	public static final String VERSION = "hl-version";
	public static final String NUMBER_OF_PAGES = "hl-numberOfPages";

	public static ImagePlus getImgePlus(InputStream imageData) throws IOException {

		//necessary to run without WINDOWS X11
		System.setProperty("java.awt.headless","true");

		File tmpImgFile = File.createTempFile("IMG", "TMP");
		tmpImgFile.deleteOnExit();
		IOUtils.copy(imageData, new FileOutputStream(tmpImgFile));

		ImagePlus img = new ImagePlus(tmpImgFile.getAbsolutePath());		
		return img;

	}

	public static ImagePlus getImgePlus(File tmpImgFile) throws IOException {

		//necessary to run without WINDOWS X11
		System.setProperty("java.awt.headless","true");

		ImagePlus img = new ImagePlus(tmpImgFile.getAbsolutePath());		

		return img;

	}
	public static int[] getThumbnailDimension(int imgWidth, int imgHeight) {

		int thumbHeight = THUMB_MAX_DIM;
		int thumbWidth = THUMB_MAX_DIM;			
		if(imgHeight/thumbHeight > imgWidth/thumbWidth)
			thumbWidth = (imgHeight == 0) ? 0 : imgWidth*thumbHeight/imgHeight; 
		else
			thumbHeight = (imgWidth == 0) ? 0 : imgHeight*thumbWidth/imgWidth;
		int[] dimension = {thumbWidth, thumbHeight};

		//		int[] dimension = {imgWidth / 2, imgHeight / 2};
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

		info.put(NUMBER_OF_PAGES, "0");
		if (info.containsKey(NUMBER_OF_PAGES)){
			try {
				int pages = Integer.parseInt(info.get(NUMBER_OF_PAGES));
				info.put(NUMBER_OF_PAGES, String.valueOf(pages));
			} catch(NumberFormatException nfe) {

			}
		}
	}

	

}
