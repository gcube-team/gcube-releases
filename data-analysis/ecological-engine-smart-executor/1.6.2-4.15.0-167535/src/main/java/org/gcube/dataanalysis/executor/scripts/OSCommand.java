package org.gcube.dataanalysis.executor.scripts;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;

import org.slf4j.Logger;

public class OSCommand {

	public static String ExecuteGetLine(String cmd, Logger logger) {

		Process process = null;
		String lastline = "";
		try {
			if (logger == null)
				System.out.println("ExecuteScript-> OSCommand-> Executing Control ->" + cmd);
			else
				logger.debug("ExecuteScript-> OSCommand-> Executing Control ->" + cmd);

			process = Runtime.getRuntime().exec(cmd);

			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = br.readLine();
			if (logger == null)
				System.out.println("ExecuteScript-> OSCommand->  line->" + line);
			else
				logger.debug("ExecuteScript-> OSCommand->  line->" + line);

			while (line != null) {
				try {
					lastline = line;
					if (logger == null)
						System.out.println("ExecuteScript-> OSCommand-> line->" + line);
					else
						logger.debug("ExecuteScript-> OSCommand-> line->" + line);
					line = br.readLine();
				} catch (EOFException e) {
					if (logger == null)
						System.out.println("ExecuteScript-> OSCommand -> Process Finished with EOF");
					else
						logger.debug("ExecuteScript-> OSCommand -> Process Finished with EOF");
					break;
				} catch (Exception e) {
					line = "ERROR";
					break;
				}
			}

			if (logger == null)
				System.out.println("ExecuteScript-> OSCommand -> Process Finished");
			else
				logger.debug("ExecuteScript-> OSCommand -> Process Finished");
		} catch (Throwable e) {
			if (logger == null)
				System.out.println("ExecuteScript-> OSCommand-> error ");
			else
				logger.debug("ExecuteScript-> OSCommand-> error ");
			e.printStackTrace();
			lastline = "ERROR";
		}
		process.destroy();
		if (logger == null)
			System.out.println("ExecuteScript-> OSCommand-> Process destroyed ");
		else
			logger.debug("ExecuteScript-> OSCommand-> Process destroyed ");
		return lastline;
	}

	public static boolean FileCopy(String origin, String destination) {
		try {

			File inputFile = new File(origin);
			System.out.println("ExecuteScript-> OSCommand-> FileCopy-> " + inputFile.length() + " to " + inputFile.canRead());
			int counterrors = 0;
			while ((inputFile.length() == 0) && (counterrors < 10)) {
				Thread.sleep(20);
				counterrors++;
			}

			File outputFile = new File(destination);

			FileReader in = new FileReader(inputFile);
			FileWriter out = new FileWriter(outputFile);
			int c;

			while ((c = in.read()) != -1)
				out.write(c);

			in.close();
			out.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
