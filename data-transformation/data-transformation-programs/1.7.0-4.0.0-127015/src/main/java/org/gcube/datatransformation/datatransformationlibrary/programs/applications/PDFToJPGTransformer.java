package org.gcube.datatransformation.datatransformationlibrary.programs.applications;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import javax.imageio.IIOException;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement;
import org.gcube.datatransformation.datatransformationlibrary.model.ContentType;
import org.gcube.datatransformation.datatransformationlibrary.model.Parameter;
import org.gcube.datatransformation.datatransformationlibrary.programs.Program;
import org.gcube.datatransformation.datatransformationlibrary.programs.Stream2StreamProgram;
import org.gcube.datatransformation.datatransformationlibrary.statistics.Metric;
import org.gcube.datatransformation.datatransformationlibrary.statistics.StatisticsManager;
import org.gcube.datatransformation.datatransformationlibrary.statistics.StatisticsManager.MetricType;
import org.gcube.datatransformation.datatransformationlibrary.tmpfilemanagement.TempFileManager;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.pdmodel.PDPage;

/**
 * @author Dimitris Katris, NKUA
 * <p>
 * Program transforming the first page of a pdf to jpeg.
 * </p>
 */
public class PDFToJPGTransformer extends Stream2StreamProgram {

	private static Logger log = LoggerFactory.getLogger(PDFToJPGTransformer.class);
	
	private static Metric pdfToJPGTransformerMetric = StatisticsManager.createMetric("PDFToJPGTransformerMetric", "Time to transform a pdf to one jpeg with "+PDFToJPGTransformer.class, MetricType.TRANSFORMER);
	
	private static String PAGENUMPARAM = "PAGENUM";
	
	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.programs.Stream2StreamProgram#transformStream(java.io.InputStream, org.gcube.datatransformation.datatransformationlibrary.model.ContentType, java.util.List, org.gcube.datatransformation.datatransformationlibrary.model.ContentType)
	 * @param content The content of the source {@link DataElement}.
	 * @param sourceContentType The <tt>ContentType</tt> of the source {@link DataElement}. 
	 * @param programParameters The parameters of the {@link Program}.
	 * @param targetContentType The <tt>ContentType</tt> in which the <tt>DataElement</tt> will be transformed.
	 * @return The transformed content
	 * @throws Exception If the <tt>Program</tt> is not capable to transform <tt>DataElements</tt>.
	 */
	@Override
	public InputStream transformStream(InputStream content, ContentType sourceContentType, List<Parameter> programParameters, ContentType targetContentType) throws Exception {
		int pageNum=0;
		if(programParameters==null || programParameters.size()==0){
			log.warn("Page number not set as programParameter");
			for(Parameter param: programParameters){
				if(param!=null && param.getName()!=null && param.getName().toUpperCase().equals(PAGENUMPARAM)){
					try {
						pageNum = Integer.parseInt(param.getValue());
					} catch (Exception e) {}
				}
			}
		}
		
		PDDocument document = PDDocument.load(content);
		try {
			long startTime = System.currentTimeMillis();
			String path = pdfToOneImage(TempFileManager.generateTempFileName(null), document, pageNum);
			pdfToJPGTransformerMetric.addMeasure(System.currentTimeMillis() - startTime);
			return new FileInputStream(path);
		} catch (Exception e) {
			log.error("Did not manage to create the JPEG of the "+pageNum+" page ",e);
			throw new Exception("Did not manage to create the JPEG of the "+pageNum+" page");
		} finally {
			if (document != null) {
				document.close();
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected static String pdfToOneImage(String outFileName, PDDocument document, int pageNum) throws Exception {
		String imageType = "jpg";
		outFileName = outFileName+"."+imageType;
		List pages = document.getDocumentCatalog().getAllPages();
		ImageOutputStream output = null; 
		ImageWriter imageWriter = null;
		try {
			PDPage page = (PDPage)pages.get( pageNum );
			BufferedImage image = page.convertToImage();
			output = ImageIO.createImageOutputStream( new File( outFileName ) );
			boolean foundWriter = false;
			Iterator writerIter = ImageIO.getImageWritersByFormatName( imageType );
			while( writerIter.hasNext() && !foundWriter ) {
				try {
					imageWriter = (ImageWriter)writerIter.next();
					ImageWriteParam writerParams = imageWriter.getDefaultWriteParam();
					if(writerParams.canWriteCompressed() ) {
						writerParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
						writerParams.setCompressionQuality(1.0f);
					}

					imageWriter.setOutput( output );
					imageWriter.write( null, new IIOImage( image, null, null), writerParams );
					foundWriter = true;
				} catch( IIOException io ) {
				}
				finally {
					if( imageWriter != null ) {
						imageWriter.dispose();
					}
				}
			}
			if( !foundWriter ) {
				throw new RuntimeException( "Error: no writer found for image type '" + imageType + "'" );
			}
		} finally {
			if( output != null ) {
				output.flush();
				output.close();
			}
		}
		return outFileName;
	}
	
	@SuppressWarnings("unchecked")
	protected static void pdfToAllImages(String outdir, PDDocument document) throws Exception {
		String imageType = "jpg";
		List pages = document.getDocumentCatalog().getAllPages();
		for( int i=0; i<pages.size(); i++ ) {
			ImageOutputStream output = null; 
			ImageWriter imageWriter = null;
			try {
				PDPage page = (PDPage)pages.get( i );
				BufferedImage image = page.convertToImage();
				String fileName = outdir+"\\pdfimg" + (i+1) + "." + imageType;
				output = ImageIO.createImageOutputStream( new File( fileName ) );

				boolean foundWriter = false;
				Iterator writerIter = ImageIO.getImageWritersByFormatName( imageType );
				while( writerIter.hasNext() && !foundWriter ) {
					try {
						imageWriter = (ImageWriter)writerIter.next();
						ImageWriteParam writerParams = imageWriter.getDefaultWriteParam();
						if(writerParams.canWriteCompressed() ) {
							writerParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
							writerParams.setCompressionQuality(1.0f);
						}

						imageWriter.setOutput( output );
						imageWriter.write( null, new IIOImage( image, null, null), writerParams );
						foundWriter = true;
					} catch( IIOException io ) {
					}
					finally {
						if( imageWriter != null ) {
							imageWriter.dispose();
						}
					}
				}
				if( !foundWriter ) {
					throw new RuntimeException( "Error: no writer found for image type '" + imageType + "'" );
				}
			} finally {
				if( output != null ) {
					output.flush();
					output.close();
				}
			}
		}
	}
}
