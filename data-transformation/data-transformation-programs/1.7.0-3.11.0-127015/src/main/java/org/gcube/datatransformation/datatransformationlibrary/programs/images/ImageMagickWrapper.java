package org.gcube.datatransformation.datatransformationlibrary.programs.images;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gcube.datatransformation.datatransformationlibrary.model.ContentType;
import org.gcube.datatransformation.datatransformationlibrary.model.Parameter;
import org.gcube.datatransformation.datatransformationlibrary.programs.File2FileProgram;
import org.gcube.datatransformation.datatransformationlibrary.programs.Program;
import org.gcube.datatransformation.datatransformationlibrary.utils.CLIUtils;
import org.gcube.datatransformation.datatransformationlibrary.utils.FilesUtils;

/**
 * @author Dimitris Katris, NKUA
 * <p>
 * Wraps the functionality of ImageMagick command line program.
 * </p>
 */
public class ImageMagickWrapper extends File2FileProgram {

	private static Map<String, String> mime2ext = new HashMap<String, String>();
	
	static{
		mime2ext.put("image/png", "png");
		mime2ext.put("image/tiff", "tiff");
		mime2ext.put("image/jpeg", "jpeg");
		mime2ext.put("image/bmp", "bmp");
		mime2ext.put("image/gif", "gif");
	}
	private static final String extensionSeperator = ".";
	/**
	 * Tests ImageMagickWrapper program.
	 * 
	 * @param args source and target files.
	 * @throws Exception If conversion could not be performed.
	 */
	public static void main(String[] args) throws Exception {
		ArrayList<Parameter> programParameters = new ArrayList<Parameter>();
		programParameters.add(new Parameter("method", "composite"));
		programParameters.add(new Parameter("dissolve", "15"));
		programParameters.add(new Parameter("tile", "http://dl07.di.uoa.gr:8080/esa_logo.jpg"));
		ContentType targetContentType = new ContentType();
		targetContentType.setMimeType("image/png");
		Parameter param1 = new Parameter("width", "300");
		Parameter param2 = new Parameter("height", "300");
		targetContentType.addContentTypeParameters(param1, param2);
		new ImageMagickWrapper().transformFile(new File(args[0]), programParameters, targetContentType, args[1]);
	}
	
	
	private String method;
	private String tile;
	private String watermarkingLocalPath;
	private int dissolve=-1;
	private int width=-1;
	private int height=-1;
	private boolean parsedParameters=false;

	private String define=null; 
	private boolean keepaspect=false;

	
	private String preSCommand;
	private String preTCommand=" ";

	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.programs.File2FileProgram#transformFile(java.io.File, java.util.List, org.gcube.datatransformation.datatransformationlibrary.model.ContentType, java.lang.String)
	 * @param sourceFile The source file.
	 * @param programParameters The parameters of the {@link Program}.
	 * @param targetContentType The <tt>ContentType</tt> in which the <tt>DataElement</tt> will be transformed.
	 * @param targetContentPath The path for the transformed content.
	 * @return The file with the transformed content.
	 * @throws Exception If the <tt>Program</tt> is not capable to transform <tt>DataElements</tt>.
	 */
	@Override
	public File transformFile(File sourceFile, List<Parameter> programParameters, ContentType targetContentType, String targetContentPath) throws Exception {
		if(!parsedParameters){
			parseCTParameters(targetContentType.getContentTypeParameters());
			parsePParameters(programParameters);
			setUPCommand();
			parsedParameters=true;
		}
		String finalTargetContentPath = targetContentPath+extensionSeperator+mime2ext.get(targetContentType.getMimeType());
		String command = preSCommand+sourceFile.getAbsolutePath()+" "+preTCommand+finalTargetContentPath;
		log.trace("Command to use for IM: "+command);
		int returnCode = CLIUtils.executeCommand(command);
		if(returnCode!=0 && returnCode!=1){
			log.error("Image wasn't transformed by imagemagick library");
			throw new Exception("Image wasn't transformed by imagemagick library");
		}
		File result = new File(finalTargetContentPath);
		if(returnCode==1){
			if(!result.exists() || result.length()==0){
				log.error("Image wasn't transformed by imagemagick library");
				throw new Exception("Image wasn't transformed by imagemagick library");
			}
		}
		return result;
	}
//	/**
//	 * @see org.gcube.datatransformation.datatransformationlibrary.programs.P2PProgram#transformObjectFromItsPath(java.lang.String, java.util.List, org.gcube.datatransformation.datatransformationlibrary.model.ContentType, java.lang.String)
//	 * @param sourceContentPath The location of the source file.
//	 * @param progparameters The parameters of the {@link Program}.
//	 * @param targetContentType The <tt>ContentType</tt> in which the <tt>DataElement</tt> will be transformed.
//	 * @param targetContentPath The path for the transformed content.
//	 * @return The path of the transformed content.
//	 * @throws Exception If the <tt>Program</tt> is not capable to transform <tt>DataElements</tt>.
//	 */
//	@Override
//	public String transformObjectFromItsPath(String sourceContentPath, List<Parameter> progparameters, ContentType targetContentType, String targetContentPath) throws Exception {
//		
//		if(!parsedParameters){
//			parseCTParameters(targetContentType.getContentTypeParameters());
//			parsePParameters(progparameters);
//			setUPCommand();
//			parsedParameters=true;
//		}
//		
//		String finalTargetContentPath = targetContentPath+extensionSeperator+mime2ext.get(targetContentType.getMimeType());
//		String command = preSCommand+sourceContentPath+" "+preTCommand+finalTargetContentPath;
//		if(CLIUtils.executeCommand(command)!=0){
//			log.error("Image wasn't transformed by imagemagick library");
//			throw new Exception("Image wasn't transformed by imagemagick library");
//		}
//		return finalTargetContentPath;
//	}
	
	private Logger log = LoggerFactory.getLogger(ImageMagickWrapper.class);

	private void parseCTParameters(List<Parameter> contentTypeParameters){
		if(contentTypeParameters!=null && contentTypeParameters.size()>0){
			for(Parameter param: contentTypeParameters){
				log.trace("IM Content type Parameter: "+param.getName()+" with value "+param.getValue());
				if(param.getName().equalsIgnoreCase("define")){
					define=param.getValue();
				}
//				This does not play well with our IM version
//				if(param.getName().equalsIgnoreCase("auto-orient") && param.getValue().equalsIgnoreCase("true")){
//					autoorient="auto-orient";
//				}
				if(param.getName().equalsIgnoreCase("keep-aspect") && param.getValue().equalsIgnoreCase("true")){
					keepaspect=true;
				}
//				if(param.getName().equalsIgnoreCase("unsharp")){
//					unsharp=param.getValue();
//				}
//				if(param.getName().equalsIgnoreCase("background")){
//					background=param.getValue();
//				}
//				if(param.getName().equalsIgnoreCase("gravity")){
//					gravity=param.getValue();
//				}
////				This does not play well with our IM version
//				if(param.getName().equalsIgnoreCase("extent")){
//					extent=param.getValue();
//				}
				if(param.getName().equalsIgnoreCase("width")){
					width=Integer.parseInt(param.getValue());
				}
				if(param.getName().equalsIgnoreCase("height")){
					height=Integer.parseInt(param.getValue());
				}
			}
		}
	}
	private void parsePParameters(List<Parameter> parameters) throws Exception {
		if(parameters!=null && parameters.size()>0){
			for(Parameter param: parameters){
				log.trace("IM Parameter: "+param.getName()+" with value "+param.getValue());
				if(param.getName().equalsIgnoreCase("method")){
					method = param.getValue();
				}
				if(param.getName().equalsIgnoreCase("dissolve")){
					dissolve=Integer.parseInt(param.getValue());
				}
				if(param.getName().equalsIgnoreCase("tile")){
					tile=param.getValue();
					watermarkingLocalPath=downloadTile(tile);
				}
			}
		}
		if(method==null){
			throw new Exception("Method for ImageMagick not set");
		}
	}
	private String downloadTile(String tileURL) throws Exception {
		String filename = "/tmp/"+tileURL.hashCode()+extensionSeperator+getFileExtention(tileURL);
		FilesUtils.streamToFile(new URL(tileURL).openStream(), filename);
		return filename;
	}
	
	private static final String extensionSeparator = ".";
	private static String getFileExtention(String fileName){
		int dot = fileName.lastIndexOf(extensionSeparator);
		if(dot==-1){
			return null;
		}
		return fileName.substring(dot + 1);
	}
	
	private void setUPCommand(){
		preSCommand=method+" ";
		if(dissolve>0){
			preSCommand+=("-dissolve "+dissolve+" ");
		}
		if(define!=null){
			preSCommand+=("-define "+define+" ");
		}
		if(watermarkingLocalPath!=null){
			preSCommand+=("-tile "+watermarkingLocalPath+" ");
		}

		if(!method.equals("dissolve")){//!For testing...
			preTCommand ="";
			if(width!=-1 && height!=-1){
				preTCommand+=("-thumbnail "+width+"x"+height);
				if (keepaspect)
					preTCommand+="> ";
				else
					preTCommand+=" ";
			}
//			if(autoorient!= null){
//				preSCommand+=" -auto-orient ";
//			}
//			if(unsharp!= null){
//				preSCommand+=" -unsharp "+unsharp+" ";
//			}
//			if(background!= null){
//				preSCommand+=" -background "+background+ " ";
//			}
//			if(gravity!= null){
//				preSCommand+=" -gravity "+gravity+ " ";
//			}
//			if(extent!= null){
//				preSCommand+=" -size "+extent+ " ";
//			}
		}
	}
}
