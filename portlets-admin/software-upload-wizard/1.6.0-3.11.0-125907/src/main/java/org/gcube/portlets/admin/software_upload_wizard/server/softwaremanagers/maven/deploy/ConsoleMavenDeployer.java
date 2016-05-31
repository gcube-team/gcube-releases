package org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.maven.deploy;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.maven.IMavenRepositoryInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsoleMavenDeployer implements IMavenDeployer {

	private static final Logger log = LoggerFactory.getLogger(ConsoleMavenDeployer.class);

	public void deploy(IMavenRepositoryInfo repositoryInfo, File archiveFile, File pomFile) throws Exception {
		this.deploy(repositoryInfo, archiveFile, pomFile, true);
	}

	@Override
	public void deploy(IMavenRepositoryInfo repositoryInfo, File archiveFile, File pomFile, boolean generatePom)
			throws Exception {
		deploy(repositoryInfo, archiveFile, pomFile, generatePom, null);

	}

	@Override
	public void deploy(IMavenRepositoryInfo repositoryInfo, File archiveFile, File pomFile, boolean generatePom,
			String classifier) throws Exception {
		log.debug("Deploying artifact on maven repository.");
		List<String> args = new LinkedList<String>();
		args.add("mvn");

		// Execute mvn command in batch mode
		args.add("-B");

		args.add("deploy:deploy-file");

		args.add("-Durl=" + repositoryInfo.getUrl());

		args.add("-DrepositoryId=" + repositoryInfo.getId());

		args.add("-Dfile=" + archiveFile.getAbsolutePath());

		args.add("-DpomFile=" + pomFile.getAbsolutePath());

		if (!generatePom)
			args.add("-DgeneratePom=false");

		if (classifier != null && !classifier.isEmpty())
			args.add("-Dclassifier=" + classifier);

		log.debug("Executing maven command with args: " + args);

		ProcessBuilder processBuilder = new ProcessBuilder(args);

		processBuilder.redirectErrorStream(true);

		Process process = processBuilder.start();

		String commandOutput = "";

		InputStream inputStream = process.getInputStream();
		InputStreamReader isr = new InputStreamReader(inputStream);
		BufferedReader br = new BufferedReader(isr);
		String line;
		while ((line = br.readLine()) != null) {
			commandOutput += line + "\n";
			log.trace(line);
		}

		int code = process.waitFor();

		log.debug("Maven command return code: " + code);

		if (code != 0) {
			log.error("An error occurred while deploying an artifact on a maven repository, mvn command output follows:\n"
					+ commandOutput);
			throw new Exception("Maven deploy error.");
		}
		log.debug("Maven artifact uploaded succesfully");
	}

	@Override
	public void deploy(IMavenRepositoryInfo repositoryInfo, File archiveFile, File pomFile,
			PrimaryArtifactAttachment... attachments) throws Exception {
		log.debug("Deploying artifact on maven repository.");
		List<String> args = new LinkedList<String>();
		args.add("mvn");

		// Execute mvn command in batch mode
		args.add("-B");

		args.add("deploy:deploy-file");

		args.add("-Durl=" + repositoryInfo.getUrl());

		args.add("-DrepositoryId=" + repositoryInfo.getId());

		args.add("-Dfile=" + archiveFile.getAbsolutePath());

		args.add("-DpomFile=" + pomFile.getAbsolutePath());

		args.add("-DgeneratePom=false");

		if (attachments != null && attachments.length > 0) {
			String files = "";
			String classifiers = "";
			String types = "";

			for (int i = 0; i < attachments.length; i++) {
				files += attachments[i].getFile().getAbsolutePath();
				classifiers += attachments[i].getClassifier();
				types += attachments[i].getType();
				if (i < (attachments.length - 1)) {
					files += ",";
					classifiers += ",";
					types += ",";
				}
			}
			args.add("-Dfiles=" + files);
			args.add("-Dclassifiers=" + classifiers);
			args.add("-Dtypes=" + types);
		}

		String cmd = "";
		for (String s : args) {
			cmd += s + " ";
		}
		log.debug("Executing maven command: " + cmd);

		ProcessBuilder processBuilder = new ProcessBuilder(args);

		processBuilder.redirectErrorStream(true);

		Process process = processBuilder.start();

		String commandOutput = "";

		InputStream inputStream = process.getInputStream();
		InputStreamReader isr = new InputStreamReader(inputStream);
		BufferedReader br = new BufferedReader(isr);
		String line;
		while ((line = br.readLine()) != null) {
			commandOutput += line + "\n";
			log.trace(line);
		}

		int code = process.waitFor();

		log.debug("Maven command return code: " + code);

		if (code != 0) {
			log.error("An error occurred while deploying an artifact on a maven repository, mvn command output follows:\n"
					+ commandOutput);
			throw new Exception("Maven deploy error.");
		}
		log.debug("Maven artifact uploaded succesfully");

	}

}
