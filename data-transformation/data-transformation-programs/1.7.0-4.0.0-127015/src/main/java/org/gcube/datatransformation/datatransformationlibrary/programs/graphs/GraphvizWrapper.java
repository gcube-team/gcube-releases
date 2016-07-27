package org.gcube.datatransformation.datatransformationlibrary.programs.graphs;

import java.io.File;
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

/**
 * Wraps the functionality of Graphviz command line program
 * 
 * @author john.gerbesiotis - DI NKUA
 *
 */
public class GraphvizWrapper extends File2FileProgram {

	private Logger log = LoggerFactory.getLogger(GraphvizWrapper.class.getName());
	
	private static Map<String, String> mime2ext = new HashMap<String, String>();
	
	static{
		mime2ext.put("image/svg+xml", "svg");
		mime2ext.put("image/png", "png");
		mime2ext.put("image/gif", "gif");
		mime2ext.put("application/postscript", "ps");
	}

	private static final String extensionSeparator = ".";
	
	private String method;
	private String Tlang;
	private String goverlap="true";
	private boolean parsedParameters=false;

	private String preSCommand;
	private String preTCommand=" ";

	/**
	 * Tests ImageMagickWrapper program.
	 * 
	 * @param args source and target files.
	 * @throws Exception If conversion could not be performed.
	 */
	public static void main(String[] args) throws Exception {
		ArrayList<Parameter> programParameters = new ArrayList<Parameter>();
		programParameters.add(new Parameter("method", "neato"));
		ContentType targetContentType = new ContentType();
		targetContentType.setMimeType("application/postscript");
//		Parameter param1 = new Parameter("width", "300");
//		Parameter param2 = new Parameter("height", "300");
//		targetContentType.addContentTypeParameters(param1, param2);
		new GraphvizWrapper().transformFile(new File("/home/jgerbe/testArea/graphviz/traffic_lights.gv.txt"), programParameters, targetContentType, "/home/jgerbe/testArea/graphviz/output");
	}
	
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
			Tlang = "-T" + mime2ext.get(targetContentType.getMimeType());
			parseCTParameters(targetContentType.getContentTypeParameters());
			parsePParameters(programParameters);
			setUPCommand();
			parsedParameters=true;
		}
		String finalTargetContentPath = targetContentPath+extensionSeparator+mime2ext.get(targetContentType.getMimeType());
		String command = preSCommand+sourceFile.getAbsolutePath()+" "+preTCommand+ "-o " + finalTargetContentPath;
		log.trace("Command to use for IM: "+command);
		int returnCode = CLIUtils.executeCommand(command);
		if(returnCode!=0 && returnCode!=1){
			log.error("Graph wasn't constructed by graphviz library");
			throw new Exception("Graph wasn't constructed by graphviz library");
		}
		File result = new File(finalTargetContentPath);
		if(returnCode==1){
			if(!result.exists() || result.length()==0){
				log.error("Graph wasn't constructed by graphviz library");
				throw new Exception("Graph wasn't constructed by graphviz library");
			}
		}
		return result;
	}

	private void parseCTParameters(List<Parameter> contentTypeParameters){
		if(contentTypeParameters!=null && contentTypeParameters.size()>0){
			for(Parameter param: contentTypeParameters){
				log.trace("GV Content type Parameter: "+param.getName()+" with value "+param.getValue());
				
			}
		}
	}

	private void parsePParameters(List<Parameter> parameters) throws Exception {
		if(parameters!=null && parameters.size()>0){
			for(Parameter param: parameters){
				log.trace("GV Parameter: "+param.getName()+" with value "+param.getValue());
				if(param.getName().equalsIgnoreCase("method")){
					method = param.getValue();
					
					if (method.compareTo("dot") == 0){
					} else if (method.compareTo("neato") == 0) {
					} else if (method.compareTo("sfdp") == 0) {
					} else if (method.compareTo("fdp") == 0) {
					} else if (method.compareTo("twopi") == 0) {
					} else if (method.compareTo("circo") == 0) {
					} else if (method.compareTo("dotty") == 0) {
					} else if (method.compareTo("lefty") == 0) {
					} else {
						log.warn("method was not accepted");
						method = null;
					}
					
					continue;
				}
				
				preTCommand += "-" + param.getName() + "=" + param.getValue();
			}
		}
		if(method==null){
			throw new Exception("Method for Graphviz not set");
		}
	}
	
	private void setUPCommand(){
		preSCommand=method+" " + Tlang + " ";
		if (goverlap.compareTo("true") != 0)
			preSCommand+=("-Goverlap="+goverlap+" ");
	}
}
