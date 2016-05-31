package org.gcube.dataanalysis.ewe.util;

import java.io.File;
import java.util.Scanner;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;

public class ExecUtils {

	public static String exec(String command) {
		return exec(command, null);
	}
	
	/**
	 * Executes the given 'command' in the given 'dir' as current directory.
	 * @param command
	 * @param dir
	 * @return
	 */
	public static String exec(final String command, File dir) {
		AnalysisLogger.getLogger().debug("Executing command '" + command + (dir!=null?"' in dir '" + dir + "'" : ""));
		ProcessBuilder builder = new ProcessBuilder(command.split(" "));
    if (dir != null) {
      builder.directory(dir.getAbsoluteFile());
    }
		builder.redirectErrorStream(true);
		StringBuilder text = new StringBuilder();
		try {
			Process process =  builder.start();
			
			Scanner s = new Scanner(process.getInputStream());
			while (s.hasNextLine()) {
			  text.append(s.nextLine());
			  text.append("\n");
			}
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return text.toString();
	}

}
