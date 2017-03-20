package org.gcube.datatransformation.datatransformationlibrary.programs.graphs;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.datatransformation.datatransformationlibrary.model.ContentType;
import org.gcube.datatransformation.datatransformationlibrary.model.Parameter;
import org.gcube.datatransformation.datatransformationlibrary.programs.File2FileProgram;
import org.gcube.datatransformation.datatransformationlibrary.programs.Program;
import org.gcube.datatransformation.datatransformationlibrary.utils.CLIUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wraps the functionality of Gnuplot command line program
 * 
 * @author john.gerbesiotis - DI NKUA
 *
 */
public class GnuplotWrapper extends File2FileProgram {

	private static Map<String, String> mime2ext = new HashMap<String, String>();
	private static Map<String, String> mime2term = new HashMap<String, String>();
	
	static{
		mime2ext.put("image/svg+xml", "svg");
		mime2ext.put("image/png", "png");
		mime2ext.put("image/gif", "gif");
		mime2ext.put("image/jpeg", "jpg");
		mime2ext.put("application/postscript", "eps");

		mime2term.put("image/svg+xml", "svg");
		mime2term.put("image/png", "pngcairo");
		mime2term.put("image/gif", "gif");
		mime2term.put("image/jpeg", "jpeg");
		mime2term.put("application/postscript", "postscript eps");
	}

	private Logger log = LoggerFactory.getLogger(GnuplotWrapper.class.getName());

	private String method = "gnuplot";
	private String plot = "";
	private String dataFile;
	private boolean parsedParameters = false;
	private static final String extensionSeparator = ".";

//	public static void main(String[] args) throws Exception {
//		ArrayList<Parameter> programParameters = new ArrayList<Parameter>();
//
//		String script = new String();
//		script += "set term post eps enh color solid \"Helvetica\" 12";
//		script += "\n";
//		script += "set title \"US immigration from Europe by decade\"";
//		script += "\n";
//		script += "set datafile missing \"-\"";
//		script += "\n";
//		script += "set xtics nomirror rotate by -45";
//		script += "\n";
//		script += "set key noenhanced";
//		script += "\n";
//		script += "set style data linespoints";
//		script += "\n";
//		script += "plot 'moufa.dat' using 2:xtic(1) title columnheader(2), \\\nfor [i=3:22] '' using i title columnheader(i)";
//
//		System.out.println(script);
//		System.out.println();
//		programParameters.add(new Parameter("script", script));
//		
//		GnuplotWrapper gp = new GnuplotWrapper();
//		gp.dataFile = "dataFile.dat";
//		gp.parsePParameters(programParameters);
//		System.out.println(gp.plot);
////		ContentType targetContentType = new ContentType();
////		targetContentType.setMimeType("application/postscript");
////		new GnuplotWrapper().transformFile(new File("/home/jgerbe/testArea/gnuplot/immigration.dat"), programParameters, targetContentType, "/home/jgerbe/testArea/gnuplot/output");
//	}
	
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
		String finalTargetContentPath = targetContentPath+extensionSeparator+mime2ext.get(targetContentType.getMimeType());
		dataFile = sourceFile.getAbsolutePath();
		if(!parsedParameters){
			parseCT(targetContentType);
			plot += "set output \"" + finalTargetContentPath + "\"\n"; 
			parsePParameters(programParameters);
			parsedParameters=true;
		}
		
		log.trace("Plot to use for GP: "+plot);

		String command = method;
		int returnCode = CLIUtils.executeCommand(command, new ByteArrayInputStream(plot.getBytes()));
		if(returnCode!=0 && returnCode!=1){
			log.error("Gnuplot failed to execute");
			throw new Exception("Gnuplot failed to execute");
		}
		File result = new File(finalTargetContentPath);
		if(returnCode==1){
			if(!result.exists() || result.length()==0){
				log.error("Gnuplot failed to execute");
				throw new Exception("Gnuplot failed to execute");
			}
		}
		return result;
	}

	private void parseCT(ContentType contentType){
		List<Parameter> contentTypeParameters = contentType.getContentTypeParameters();
		
		if (mime2term.containsKey(contentType.getMimeType()))
			plot += "set term " + mime2term.get(contentType.getMimeType()) + "\n";
		
		if(contentTypeParameters !=null && contentTypeParameters.size()>0){
			for(Parameter param: contentTypeParameters){
				log.trace("Content type Parameter: "+param.getName()+"="+param.getValue());
				if(param.getName().equalsIgnoreCase("define")){
//					define=param.getValue();
				}
			}
		}
	}

	private void parsePParameters(List<Parameter> parameters) {
		if (parameters != null && parameters.size() > 0) {
			for (Parameter param : parameters) {
				String paramValue = param.getValue().replaceAll("\\\\\\s*\n", " ");
				for (String line : paramValue.split(System.getProperty("line.separator"))) {
					if (line.contains("!")) {
						log.info("Parameter " + line + " with value " + line + " will be removed");
						continue;
					}
	
					if (line.toLowerCase().matches(".*set.*output.*")) {
						log.info("Parameter " + line + " with value " + line + " will be removed");
						continue;
					}
					
					if (line.toLowerCase().startsWith("plot")) {
						String cont = line;
						// replace input file with sourceFile
						String regex, repl;
	
						//remove multiple white chars and backslash
						cont = cont.replace("\\", " ");
						cont = cont.replaceAll("\\s+", " ");
						cont = " " + cont;
						
						//replace all strings between '' except titles
						regex = "((?<!\\stitle\\s)(?<!\\st\\s))(')(\\S*)(')";
						repl = "$2" + dataFile + "$4";
						cont = cont.replaceAll(regex, repl);
	
						//replace all strings between "" except titles
						regex = "((?<!\\stitle\\s)(?<!\\st\\s))(\")(\\S*)(\")";
						repl = "$2" + dataFile + "$4";
						cont = cont.replaceAll(regex, repl);
	
						cont = cont.trim();
						
						plot += cont + "\n";
						
						continue;
					}
					
					log.trace("GM Parameter: " + line + "=" + line);
					plot += line + "\n";
				}
			}
		}
	}
}
