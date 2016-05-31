package org.gcube.datatransformation.datatransformationlibrary.programs.video;

import java.io.File;
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
 * @author Dimitris Katris, NKUA
 *
 */
public class MEncoderWrapper extends File2FileProgram{

	/**
	 * Tests MEncoderWrapper program.
	 * 
	 * @param args source and target files.
	 * @throws Exception If conversion could not be performed.
	 */
	public static void main(String[] args) throws Exception {
		long startedAt = System.currentTimeMillis();
		new MEncoderWrapper().transformFile(new File(args[0]), null, null, args[1]);
		System.out.println("The transformation took "+((System.currentTimeMillis()-startedAt)/1000)+" secs.");
	}
	private static final String extensionSeperator = ".";
	private static Map<String, String> mime2ext = new HashMap<String, String>();
	static{
		mime2ext.put("video/x-flv", "flv");
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
		String finalTargetContentPath = targetContentPath+extensionSeperator+mime2ext.get(targetContentType.getMimeType());
		String command = preSCommand+sourceFile.getAbsolutePath()+" "+preTCommand+finalTargetContentPath+" "+afterTCommand;
		int returnCode = CLIUtils.executeCommand(command);
		if(returnCode!=0 && returnCode!=1){
			log.error("Did not manage to transform video, mencoder returned "+returnCode);
			throw new Exception("Did not manage to transform video, mencoder returned "+returnCode);
		}
		File result = new File(finalTargetContentPath);
		if(returnCode==1){
			if(!result.exists() || result.length()==0){
				log.error("Did not manage to transform video");
				throw new Exception("Did not manage to transform video");
			}
		}
		return result;
	}
	
	private Logger log = LoggerFactory.getLogger(MEncoderWrapper.class);
	
	private static final String preSCommand = "mencoder ";
	private static final String preTCommand = "-o ";
//	private static final String afterTCommand = "-ofps 15 -of lavf -oac mp3lame -lameopts abr:br=64 -srate 22050 -ovc lavc -lavfopts i_certify_that_my_video_stream_does_not_use_b_frames -lavcopts vcodec=flv:keyint=25:vbitrate=300:mbd=2:mv0 :trell:v4mv:cbp:last_pred=3 -vf harddup,expand=:::::4/3,scale=320:240 -msglevel all=1 -mc 0/10";
	private static final String afterTCommand = "-ofps 15 -of lavf -oac mp3lame -lameopts abr:br=64 -srate 22050 -ovc lavc -lavcopts vcodec=flv:keyint=25:vbitrate=300:mbd=2:mv0 :trell:v4mv:cbp:last_pred=3 -vf harddup,expand=:::::4/3,scale=320:240 -msglevel all=1 -mc 0/10";
	
}
