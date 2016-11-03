//package org.gcube.application.framework.contentmanagement.cache.factories;
//
//import java.awt.Graphics2D;
//import java.awt.Image;
//import java.awt.Toolkit;
//import java.awt.image.BufferedImage;
//import java.io.ByteArrayOutputStream;
//import java.io.DataInputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.URI;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.Random;
//import java.util.Set;
//import java.util.concurrent.atomic.AtomicInteger;
//
//import javax.imageio.ImageIO;
//import javax.swing.ImageIcon;
//import javax.xml.rpc.ServiceException;
//
//import org.apache.axis.message.addressing.Address;
//import org.apache.axis.message.addressing.EndpointReference;
//import org.apache.axis.message.addressing.EndpointReferenceType;
//import org.apache.axis.types.URI.MalformedURIException;
//import org.apache.xml.security.utils.Base64;
//import org.gcube.application.framework.contentmanagement.content.impl.DigitalObject;
//import org.gcube.application.framework.contentmanagement.util.Pictures;
//import org.gcube.application.framework.contentmanagement.util.ThumbnailConstants;
//import org.gcube.application.framework.contentmanagement.util.ThumbnailUtils;
//import org.gcube.application.framework.core.cache.CachesManager;
//import org.gcube.application.framework.core.cache.RIsManager;
//import org.gcube.application.framework.core.cache.factories.ApplicationCredentials;
//import org.gcube.application.framework.core.security.ServiceContextManager;
//import org.gcube.application.framework.core.util.CacheEntryConstants;
//import org.gcube.application.framework.core.util.QueryString;
//import org.gcube.common.core.scope.GCUBEScope;
//import org.gcube.common.core.scope.GCUBEScope.MalformedScopeExpressionException;
//import org.gcube.common.core.utils.logging.GCUBELog;
//import org.gcube.contentmanagement.contentmanager.stubs.model.protocol.URIs;
//import org.gcube.contentmanagement.gcubedocumentlibrary.io.DocumentReader;
//import org.gcube.contentmanagement.gcubemodellibrary.elements.GCubeAlternative;
//import org.gcube.contentmanagement.gcubemodellibrary.elements.GCubeDocument;
//import org.gcube.contentmanagement.gcubemodellibrary.elements.GCubeElementProperty;
//import org.gcube.datatransformation.datatransformationservice.stubs.ContentType;
//import org.gcube.datatransformation.datatransformationservice.stubs.DataTransformationServicePortType;
//import org.gcube.datatransformation.datatransformationservice.stubs.FindApplicableTransformationUnits;
//import org.gcube.datatransformation.datatransformationservice.stubs.FindApplicableTransformationUnitsResponse;
//import org.gcube.datatransformation.datatransformationservice.stubs.Input;
//import org.gcube.datatransformation.datatransformationservice.stubs.Output;
//import org.gcube.datatransformation.datatransformationservice.stubs.Parameter;
//import org.gcube.datatransformation.datatransformationservice.stubs.TransformDataWithTransformationUnit;
//import org.gcube.informationsystem.cache.SrvType;
////import org.gcube.thumbnailer.stubs.ThumbnailerPortType;
////import org.gcube.thumbnailer.stubs.TransformContent;
////import org.gcube.thumbnailer.stubs.service.ThumbnailServiceAddressingLocator;
//import org.ietf.jgss.GSSCredential;
//import static org.gcube.contentmanagement.gcubedocumentlibrary.projections.Projections.*;
//import com.sun.image.codec.jpeg.JPEGCodec;
//import com.sun.image.codec.jpeg.JPEGImageEncoder;
//
//import net.sf.ehcache.constructs.blocking.CacheEntryFactory;
//
///**
// * @author Rena Tsantouli - Dimitris Katris (NKUA)
// *
// */
//public class ThumbnailCacheEntryFactory implements CacheEntryFactory {
//
//	/** Object logger. */
//	protected final GCUBELog logger = new GCUBELog(this);
//
//	protected static AtomicInteger thumbId = new AtomicInteger(0);
//	
//	protected static final String thumbnailsRootPath = "/icons/";
//	
//	private static ContentType trgContentTypeForSearch = new ContentType();
//	
//	static {
//		trgContentTypeForSearch.setMimeType("image/png");
//		Parameter paramw = new Parameter("width", "*");
//		Parameter paramh = new Parameter("height", "*");
//		Parameter[] ctParameters = {paramw, paramh};
//		trgContentTypeForSearch.setParameters(ctParameters);
//	}
//
//	
//	
//	
//	public Object createEntry(Object key) throws Exception {
//		QueryString query = (QueryString) key;
//		String vre = query.get(CacheEntryConstants.vre);
//		String oid = query.get(CacheEntryConstants.oid);
//		int width = Integer.parseInt(query.get(CacheEntryConstants.width));
//		int height = Integer.parseInt(query.get(CacheEntryConstants.height));
//		String[] thumbOptions =  query.get(CacheEntryConstants.thumbOptions).split(",");
//		
//		logger.debug("Going to get thumbnail oid: " + oid + " width: " + width + " height: " + height + " options: " + thumbOptions[0]);
//		
//		return getThumbnail(oid, width, height, thumbOptions, vre);
//	}
//	
//	
//	public byte[] getThumbnail(String srcObjectID, int width, int height, String[] thumbOptions, String scope) throws Exception {
//		try {
//			String thumbnailOID;
//			if((thumbnailOID=searchForExistingThumbnail(srcObjectID, width, height, thumbOptions, scope))!=null){
//				return getContentFromCMS(thumbnailOID, scope);
//			}
//			TransformationUnit transformationUnit;
//			boolean creation = containOption(thumbOptions, ThumbnailConstants.FORCE_CREATE);
//			if( (creation != false) && ((transformationUnit=findTransformationUnitToCreateThumbnail(getMimeType(srcObjectID, scope), scope))!=null)){
//				createThumbnail(srcObjectID, transformationUnit, width, height, scope);
//			}else{
//				return findStaticImageThumbnail(getMimeType(srcObjectID, scope));
//			}
//			int tries=0;
//			while(true){
//				if((thumbnailOID=searchForExistingThumbnail(srcObjectID, width, height, thumbOptions, scope))!=null){
//					return getContentFromCMS(thumbnailOID, scope);
//				}
//				if(tries==5)break;
//				tries++;
//				try {Thread.sleep(1000);} catch (Exception e) {}
//			}
//		} catch (Exception e) {
//			logger.error("Did not manage to search/create/get Thumbnail for object with id "+srcObjectID+", returning static...", e);
//			logger.error("Exception:", e);
//		}
//		return findStaticImageThumbnail(getMimeType(srcObjectID, scope));
//	}
//	
//	private String constructFileNameFromMT(String srcMimetype){
//		return thumbnailsRootPath+srcMimetype+".png";
//	}
//	
//	private byte[] findStaticImageThumbnail(String srcMimetype) throws Exception {
//		File thumbnail;
//		String srcMimetypeFormat = srcMimetype.replaceAll("/", "_").replaceAll("-", "_").replaceAll("\\.", "_");
//		while(true){
//			thumbnail=new File(constructFileNameFromMT(srcMimetypeFormat));
//			if(thumbnail.exists()){
//				break;
//			}
//			if(srcMimetypeFormat.contains("_")){
//				srcMimetypeFormat = srcMimetypeFormat.substring(0, srcMimetypeFormat.lastIndexOf("_"));
//			}else{
//				//thumbnail = new File(thumbnailsRootPath+"noThumbnail.gif");
//				logger.info("About to get static thumbnail");
//				ImageIcon im = new ImageIcon(DigitalObject.class.getResource(thumbnailsRootPath+"noThumbnail.gif"));
//
//				Image image = im.getImage();
//				int w = image.getWidth(null);
//				int h = image.getHeight(null);
//				logger.info("Width: " + w + " height " + h);
//
//			//	BufferedImage bi = new BufferedImage(w, h,BufferedImage.TYPE_INT_RGB);
//				
//				BufferedImage bi = Pictures.toBufferedImage(image);
//
//				ByteArrayOutputStream baos = new ByteArrayOutputStream();
//				ImageIO.write(bi, "JPEG", baos);
//				byte[] byteArray = baos.toByteArray();
//
//				return byteArray;
//				//break;
//			}
//		}
//	//	logger.trace("Returning static thumbnail file: "+thumbnail.getName());
//		return getBytes(thumbnail);
//	}
//	
//	
//	
//	
//	private byte[] getBytes(File file) throws Exception {
//		try {
//			DataInputStream in = new DataInputStream(new FileInputStream(file));
//			byte[] buf = new byte[(int) file.length()];
//			in.readFully(buf);
//			return buf;
//		} catch (Exception e) {
//		//	logger.error("Could not read contents of file", e);
//			logger.error("Exception:", e);
//			throw new Exception("Could not read contents of file", e);
//		}
//	}
//	
//	private TransformationUnit findTransformationUnitToCreateThumbnail(String srcMimeType, String scope) throws Exception {
//		logger.debug("Going to find transformation unit to create thumbnail for src mimetype: "+srcMimeType);
//		DataTransformationServicePortType dts = getDataTransformationServicePortType(scope);
//		FindApplicableTransformationUnits request = new FindApplicableTransformationUnits();
//		ContentType srcContentTypeForSearch = new ContentType();
//		srcContentTypeForSearch.setMimeType(srcMimeType);
//		
//		request.setSourceContentType(srcContentTypeForSearch);
//		request.setTargetContentType(trgContentTypeForSearch);
//		FindApplicableTransformationUnitsResponse response;
//		try {
//			response = dts.findApplicableTransformationUnits(request);
//		} catch (Exception e) {
//			logger.error("Exception:", e);
//			logger.error("Did not manage to search for applicable transformation units", e);
//			throw new Exception("Did not manage to search for applicable transformation units", e);
//		}
//		
//		if(response==null || response.getTPAndTransformationUnitIDs()==null ||
//				response.getTPAndTransformationUnitIDs().length==0){
//			logger.info("Could not find transformation unit that creates thumbnail for src contenty type "+srcMimeType);
//			return null;
//		}
//		TransformationUnit transformationUnit = new TransformationUnit();
//		transformationUnit.setTransformationProgramID(response.getTPAndTransformationUnitIDs()[0].getTransformationProgramID());
//		transformationUnit.setTransformationUnitID(response.getTPAndTransformationUnitIDs()[0].getTransformationUnitID());
//		if(transformationUnit.getTransformationProgramID()==null || transformationUnit.getTransformationProgramID().trim().length()==0
//				|| transformationUnit.getTransformationUnitID()==null || transformationUnit.getTransformationUnitID().trim().length()==0){
//			logger.warn("Got empty transformation program or unti id");
//			return null;
//		}
//		logger.debug("Transformation unit ("+transformationUnit.getTransformationProgramID()+"/"+transformationUnit.getTransformationUnitID()+")" +
//				" found to create thumbnail for src mimetype: "+srcMimeType);
//		return transformationUnit;
//	}
//	
//	private DataTransformationServicePortType getDataTransformationServicePortType(String scope) throws Exception {
//		return ThumbnailUtils.getDTSPortType(scope);
//	}
//	
//	private GCubeDocument getDocumentDescription(String oid, String scope) throws Exception {
//		return ThumbnailUtils.getDocumentDescription(oid, scope);
//	}
//	
//	private boolean checkAlternativeRepresentation(String alternativeRepresentationOID, int width, int height, String[] thumbOptions, String scope) throws Exception {
//		GCubeDocument documentDescription = getDocumentDescription(alternativeRepresentationOID, scope);
//		if(documentDescription.mimeType()==null || !documentDescription.mimeType().equals("image/png")){
//			return false;
//		}
//		
//		Map<String, GCubeElementProperty> properties = documentDescription.properties();
//		//Searching for content type parameters into the properties of the document...
//		int wThumb=0;
//		int hThumb=0;
//		if(properties!=null && properties.size()>0){
//			Set<String> propertiesNames = properties.keySet();
//			for(String prop: propertiesNames){
//				
//				logger.debug("Property - Name: "+prop +" - Value: "+properties.get(prop).value()+" - Type: "+properties.get(prop).type());
//				if(properties.get(prop).type().equals(ThumbnailConstants.contenttypeParamsPropertyType)){
//					if(prop.equals("width")){
//						wThumb = Integer.parseInt(properties.get(prop).value());
//					}
//					if(prop.equals("height")){
//						hThumb = Integer.parseInt(properties.get(prop).value());
//					}
//				}
//			}
//		}
//		if(wThumb==0 || hThumb==0){return false;}
//		
//		boolean thOption = containOption(thumbOptions, ThumbnailConstants.EQUAL);
//		if(thOption != false) {
//			if(height == hThumb || width == wThumb){
//				return true;
//			}
//		} else if((thOption = containOption(thumbOptions, ThumbnailConstants.FLOOR)) != false) {
//			if(height >= hThumb && width >=wThumb) {
//				return true;
//			}
//		} else if((thOption = containOption(thumbOptions, ThumbnailConstants.CEIL)) != false) {
//			if(height <= hThumb && width <=wThumb) {
//				return true;
//			}	
//		}
//		return false;
//	}
//	
//	
////	private String searchForExistingThumbnail(String srcObjectID, int width, int height, String[] thumbOptions, String scope) throws Exception {
////		CMSPortType1PortType cms = ThumbnailUtils.getCMS(scope);
////		ArrayOfAlternativeRepresentation altRes = cms.getAlternativeRepresentations(srcObjectID);
////		if(altRes!=null && altRes.getDocumentRepresentations()!=null && altRes.getDocumentRepresentations().length>0){
////			AlternativeRepresentationDescription[] docRes = altRes.getDocumentRepresentations();
////			if(docRes != null && docRes.length>0){
////				logger.debug("Found " + docRes.length + " alternative representations!!!");
////				for(AlternativeRepresentationDescription altdesc: docRes) {
////					if(altdesc.getRepresentationRole().equals("thumbnail")){
////						if(checkAlternativeRepresentation(altdesc.getRepresentationID(), width, height, thumbOptions, scope)){
////							return altdesc.getRepresentationID();
////						}
////					}
////				}
////			}
////		}else{
////			logger.debug("Did not manage to find any alternative representations for object with id: "+srcObjectID);
////		}
////		logger.debug("Did not manage to find stored thumbnail "+width+"x"+height+" for object with id: "+srcObjectID);
////		return null;
////	}
//	
//	public String searchForExistingThumbnail(String srcObjectURI, int width, int height, String[] thumbOptions, String scope) throws Exception {
//		URI uri = new URI(srcObjectURI);
//		String collectionId = URIs.collectionID(uri);
//		String documentId = URIs.documentID(uri);
//		
//		// instantiate the DocumentReader
//		try {
//			DocumentReader reader = new DocumentReader(collectionId, GCUBEScope.getScope(scope));
//			GCubeDocument doc = reader.get(documentId, alternative());
//			for (GCubeAlternative alternative : doc.alternatives()) {
//				if (alternative.type().equals("thumbnail")) {
//					if (checkAlternativeRepresentation(alternative.id(), width, height, thumbOptions, scope)) {
//						return alternative.id();
//					}
//				}
//			}
//		} catch (Exception e) {
//			logger.error("Exception:", e);
//		}
//		return null;
//	}
//	
//	private byte[] getContentFromCMS(String oid, String scope) throws Exception {
//		return ThumbnailUtils.getContentFromCMS(oid, scope);
//	}
//	
//	private boolean containOption(String[] thumbOptions, String option) {
//		for(int i=0; i < thumbOptions.length; i++)
//		{
//			if(thumbOptions[i].equals(option))
//				return true;
//		}
//		return false;
//	}
//	
//	private String getMimeType(String oid, String scope) throws Exception {
//		return ThumbnailUtils.getMimeType(oid, scope);
//	}
//	
//	private void createThumbnail(String srcObjectID, TransformationUnit transformationUnit, int width, int height, String scope) throws Exception {
//		DataTransformationServicePortType dts = getDataTransformationServicePortType(scope);
//		TransformDataWithTransformationUnit request = new TransformDataWithTransformationUnit();
//		Input input = new Input();
//		input.setInputType("CObject");
//		input.setInputValue(srcObjectID);
//		Input [] inputs = {input};
//		request.setInputs(inputs);
//		
//		request.setCreateReport(false);
//		request.setFilterSources(false);
//		
//		ContentType targetContentType = new ContentType();
//		targetContentType.setMimeType("image/png");
//		Parameter paramw = new Parameter("width", String.valueOf(width));
//		Parameter paramh = new Parameter("height", String.valueOf(height));
//		Parameter[] ctParameters = {paramw, paramh};
//		targetContentType.setParameters(ctParameters);
//		request.setTargetContentType(targetContentType);
//		
//		Output output = new Output();
//		output.setOutputType("AlternativeRepresentation");
//		Parameter altRepRole = new Parameter("RepresentationRole", "thumbnail");
//		Parameter[] outputParameters = {altRepRole};
//		output.setOutputparameters(outputParameters);
//		request.setOutput(output);
//		
//		request.setTransformationUnitID(transformationUnit.getTransformationUnitID());
//		request.setTPID(transformationUnit.getTransformationProgramID());
//
//		dts.transformDataWithTransformationUnit(request);
//	}
//	
//	
//	class TransformationUnit{
//		private String transformationUnitID;
//		private String transformationProgramID;
//		
//		public String getTransformationUnitID() {
//			return transformationUnitID;
//		}
//		public void setTransformationUnitID(String transformationUnitID) {
//			this.transformationUnitID = transformationUnitID;
//		}
//		public String getTransformationProgramID() {
//			return transformationProgramID;
//		}
//		public void setTransformationProgramID(String transformationProgramID) {
//			this.transformationProgramID = transformationProgramID;
//		}
//	}
//}
