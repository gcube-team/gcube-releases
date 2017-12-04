package org.gcube.dataanalysis.dataminer.poolmanager.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.io.FileUtils;
import org.gcube.dataanalysis.dataminer.poolmanager.clients.configuration.Configuration;
import org.gcube.dataanalysis.dataminer.poolmanager.clients.configuration.DMPMClientConfiguratorManager;
import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Algorithm;
import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Dependency;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.wc.SVNFileUtil;
import org.tmatesoft.svn.core.internal.wc.admin.SVNChecksumInputStream;
import org.tmatesoft.svn.core.io.ISVNEditor;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.io.diff.SVNDeltaGenerator;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

/**
 * Created by ggiammat on 5/9/17.
 */
public class SVNUpdater {

	private SVNRepository svnRepository;


	public SVNUpdater() throws SVNException {
		this.svnRepository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(DMPMClientConfiguratorManager.getInstance().getProductionConfiguration().getSVNRepository()));
		ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager();
		this.svnRepository.setAuthenticationManager(authManager);
	}

//	public void updateRPRotoDeps(Algorithm algorithm) {
//		this.updateSVN(this.configuration.getSVNRProtoOSDepsList(), algorithm.getOSDependencies());
//		this.updateSVN(this.configuration.getSVNRProtoCRANDepsList(), algorithm.getCranDependencies());
//		this.updateSVN(this.configuration.getSVNRProtoGitHubDepsList(), algorithm.getGitHubDependencies());
//	}

	
	
	public String getDependencyFile(String language/*, String env*/){
		
		String a = "";
//		if (env.equals("Dev")){
//			a= this.getDevDependencyFile(language);
//		}
//		
//		if (env.equals("Prod")){
//			a= this.getProdDependencyFile(language);
//		}
//
//		if (env.equals("Proto")){
//			a= this.getRProtoDependencyFile(language);
//		}
//		
//		if (env.equals("Preprod")){
//			a= this.getPreProdDependencyFile(language);
//		}
		a = this.getStagingDependencyFile(language);
		return a;
	}
	
	
	
	
public String getDependencyFileProd(String language/*, String env*/){
		
		String a = "";
//		if (env.equals("Dev")){
//			a= this.getDevDependencyFile(language);
//		}
//		
//		if (env.equals("Prod")){
//			a= this.getProdDependencyFile(language);
//		}
//
//		if (env.equals("Proto")){
//			a= this.getRProtoDependencyFile(language);
//		}
//		
//		if (env.equals("Preprod")){
//			a= this.getPreProdDependencyFile(language);
//		}
		a = this.getProdDependencyFile(language);
		return a;
	}

	
	
	public String getStagingDependencyFile(String language) {
		
		return getDependencyFile(DMPMClientConfiguratorManager.getInstance().getStagingConfiguration(),language);
		
//		switch (language) {
//		case "R":
//			return DMPMClientConfigurator.getInstance().getSVNStagingCRANDepsList();
//		case "R-blackbox":
//			return DMPMClientConfigurator.getInstance().getSVNStagingRBDepsList();
//		case "Java":
//			return DMPMClientConfigurator.getInstance().getSVNStagingJavaDepsList();
//		case "Knime-Workflow":
//			return DMPMClientConfigurator.getInstance().getSVNStagingKWDepsList();
//		case "Linux-compiled":
//			return DMPMClientConfigurator.getInstance().getSVNStagingLinuxCompiledDepsList();
//		case "Octave":
//			return DMPMClientConfigurator.getInstance().getSVNStagingOctaveDepsList();
//		case "Python":
//			return DMPMClientConfigurator.getInstance().getSVNStagingPythonDepsList();
//		case "Pre-Installed":
//			return DMPMClientConfigurator.getInstance().getSVNStagingPreInstalledDepsList();
//		case "Windows-compiled":
//			return DMPMClientConfigurator.getInstance().getSVNStagingWCDepsList();
//		default:
//			return null;
//		}
	}
	
	
	public String getProdDependencyFile(String language) {


		return getDependencyFile(DMPMClientConfiguratorManager.getInstance().getProductionConfiguration(),language);
		
//		switch (language) {
//		case "R":
//			return this.configuration.getSVNProdCRANDepsList();
//		case "R-blackbox":
//			return this.configuration.getSVNProdRBDepsList();
//		case "Java":
//			return this.configuration.getSVNProdJavaDepsList();
//		case "Knime-Workflow":
//			return this.configuration.getSVNProdKWDepsList();
//		case "Linux-compiled":
//			return this.configuration.getSVNProdLinuxCompiledDepsList();
//		case "Octave":
//			return this.configuration.getSVNProdOctaveDepsList();
//		case "Python":
//			return this.configuration.getSVNProdPythonDepsList();
//		case "Pre-Installed":
//			return this.configuration.getSVNProdPreInstalledDepsList();
//		case "Windows-compiled":
//			return this.configuration.getSVNProdWCDepsList();
//		default:
//			return null;
//		}
	}
	
	
	private String getDependencyFile (Configuration configuration, String language)
	{
		switch (language) {
		case "R":
			return configuration.getSVNCRANDepsList();
		case "R-blackbox":
			return configuration.getSVNRBDepsList();
		case "Java":
			return configuration.getSVNJavaDepsList();
		case "Knime-Workflow":
			return configuration.getSVNKWDepsList();
		case "Linux-compiled":
			return configuration.getSVNLinuxCompiledDepsList();
		case "Octave":
			return configuration.getSVNOctaveDepsList();
		case "Python":
			return configuration.getSVNPythonDepsList();
		case "Pre-Installed":
			return configuration.getSVNPreInstalledDepsList();
		case "Windows-compiled":
			return configuration.getSVNWCDepsList();
		default:
			return null;
		}
	}


	public void updateSVNStagingAlgorithmList(Algorithm algorithm, String targetVRE, String category, String algorithm_type, String user/*, String env*/) {
	this.updateSVNAlgorithmList(DMPMClientConfiguratorManager.getInstance().getStagingConfiguration().getSVNAlgorithmsList(), algorithm, targetVRE, category, algorithm_type, user);
	}
	
	public void updateSVNProdAlgorithmList(Algorithm algorithm, String targetVRE, String category, String algorithm_type, String user/*, String env*/) {
	this.updateSVNAlgorithmList(DMPMClientConfiguratorManager.getInstance().getProductionConfiguration().getSVNAlgorithmsList(), algorithm, targetVRE, category, algorithm_type, user);
	}
	
	
	public void updateAlgorithmFiles(File a) throws SVNException{
		//this.updateAlgorithmList(this.configuration.getSVNMainAlgoRepo(), a);
		this.updateAlgorithmList(DMPMClientConfiguratorManager.getInstance().getStagingConfiguration().getRepository(), a);
	}

	
	public void updateAlgorithmFilesProd(File a) throws SVNException{
		//this.updateAlgorithmList(this.configuration.getSVNMainAlgoRepo(), a);
		this.updateAlgorithmList(DMPMClientConfiguratorManager.getInstance().getProductionConfiguration().getRepository(), a);
	}
	
	
	
	public void updateAlgorithmList(String svnMainAlgoRepo, File a) throws SVNException {
		try {
			System.out.println("Adding .jar file: " + a + " to repository " + svnMainAlgoRepo);

			if (fileExists(svnMainAlgoRepo+File.separator+a.getName(), -1)){
				this.updateFile(reteriveByteArrayInputStream(a), svnMainAlgoRepo, a.getName());
				
			}
			else this.putFile(reteriveByteArrayInputStream(a), svnMainAlgoRepo,a.getName());
		
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		finally {
			svnRepository.closeSession();
		}
}

	
	
	public void putFile(ByteArrayInputStream byteArrayInputStream, String destinationFolder, String fileName)
		    throws SVNException {
		
		
		final ISVNEditor commitEditor = svnRepository.getCommitEditor("Add algorithm to list", null);
		commitEditor.openRoot(-1);
		commitEditor.openDir(destinationFolder, -1);
		
		
		    String filePath = destinationFolder + "/" + fileName;

		
		    	//commitEditor.openFile(filePath, -1);
		       		      
		  

		    	commitEditor.addFile(filePath, null, -1);
		  
		    commitEditor.applyTextDelta(filePath, null);
		    SVNDeltaGenerator deltaGenerator = new SVNDeltaGenerator();
		    String checksum = deltaGenerator.sendDelta(filePath, byteArrayInputStream, commitEditor, true);
		    commitEditor.closeFile(filePath, checksum);
		    commitEditor.closeDir();
			commitEditor.closeDir();
			commitEditor.closeEdit();
		}
	
	
	public void updateFile(ByteArrayInputStream byteArrayInputStream, String destinationFolder, String fileName)
		    throws SVNException {
		
		
		final ISVNEditor commitEditor = svnRepository.getCommitEditor("Add algorithm to list", null);
		commitEditor.openRoot(-1);
		commitEditor.openDir(destinationFolder, -1);
		
		
		    String filePath = destinationFolder + "/" + fileName;

		   // if (fileExists(filePath, -1)) { // updating existing file
		      
		    	commitEditor.openFile(filePath, -1);
		       		      
		    //} else { // creating new file

		    	//commitEditor.addFile(filePath, null, -1);
		    //}
		    commitEditor.applyTextDelta(filePath, null);
		    SVNDeltaGenerator deltaGenerator = new SVNDeltaGenerator();
		    String checksum = deltaGenerator.sendDelta(filePath, byteArrayInputStream, commitEditor, true);
		    commitEditor.closeFile(filePath, checksum);
		    commitEditor.closeDir();
			commitEditor.closeDir();
			commitEditor.closeEdit();
		}
	
	
	
	
	 public boolean fileExists(String path, long revision) throws SVNException {
		    SVNNodeKind kind = svnRepository.checkPath(path, revision);
		    if (kind == SVNNodeKind.FILE) {
		      return true;
		    }
		    return false;
		}
	
	
	
	
	public static ByteArrayInputStream reteriveByteArrayInputStream(File file) throws IOException {
				
	    return new ByteArrayInputStream(FileUtils.readFileToByteArray(file));
	}
	
	
	public void updateSVNAlgorithmList(String file, Algorithm algorithm, String targetVRE, String category, String algorithm_type, String user/*, String env*/) {
		try {
			System.out.println("Updating algorithm list: " + file);
			final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			svnRepository.getFile(file, SVNRepository.INVALID_REVISION, null, byteArrayOutputStream);
			String lines[] = byteArrayOutputStream.toString().split("\\r?\\n");
			
			List<String> newContent = new LinkedList<>(Arrays.asList(lines));

			// check if the algorithm is already in the list (match the class name) and delete the content 
			for (String l : lines) {
				if (l.contains(algorithm.getClazz())) {
					newContent.remove(l);
					//System.out.println("Not updating algorithm list beacuse already present");
					//return;
				}
			}

			// the algorithm is not in the list or must be overwritten cause some modification. Add it
			newContent.add(this.generateAlgorithmEntry(algorithm, targetVRE, category,algorithm_type/*, env*/));
			// Collections.sort(newContent);

			final SVNDeltaGenerator deltaGenerator = new SVNDeltaGenerator();

			byte[] originalContents = byteArrayOutputStream.toByteArray();

			final ISVNEditor commitEditor = svnRepository.getCommitEditor("update algorithm list", null);
			commitEditor.openRoot(-1);
			commitEditor.openFile(file, -1);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			for (String line : newContent) {				
				baos.write(line.getBytes());
				baos.write("\n".getBytes());
			}
			byte[] bytes = baos.toByteArray();

			commitEditor.applyTextDelta(file, md5(originalContents));

			final String checksum = deltaGenerator.sendDelta(file, new ByteArrayInputStream(originalContents), 0,
					new ByteArrayInputStream(bytes), commitEditor, true);
			commitEditor.closeFile(file, checksum);
			commitEditor.closeEdit();

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		finally {
			svnRepository.closeSession();
		}
	}


	public String generateAlgorithmEntry(Algorithm algorithm, String targetVRE, String category, String algorithm_type/*,String env*/) throws ParseException {
		//Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		//long unixTime = System.currentTimeMillis() / 1000L;

		StringBuffer sb = new StringBuffer("| ");
		sb.append(algorithm.getName() + " | ");
		sb.append(algorithm.getFullname() + " | ");
		sb.append(category + " | ");
		sb.append("DataMinerPoolManager | ");
		sb.append("<notextile>./addAlgorithm.sh " + algorithm.getName() + " " + algorithm.getCategory() + " "
				+ algorithm.getClazz() + " " + targetVRE + " " + algorithm_type + " N "
				+ algorithm.getPackageURL() + " \"" + algorithm.getDescription() + "\" </notextile> | ");
		sb.append("none | ");
		sb.append(this.getTimeZone() + " | ");
		
		System.out.println("Algo details: "+sb.toString());
		
		return sb.toString();
	}
	
	
	public Collection<String> getUndefinedDependencies(String file, Collection<Dependency> deps) throws SVNException {
//		SendMail sm = new SendMail();
//		NotificationHelper nh = new NotificationHelper();

		List<String> undefined = new LinkedList<String>();
		
		//to fix in next release: if the file is not present for that language in the service.properties then skip and return null list of string
		//just to uncomment the following lines

		if(file.isEmpty()){
		return undefined;
		}
		
		
		System.out.println("Checking dependencies list: " + file);
		
		CheckMethod cm = new CheckMethod();
		List<String> validDependencies = new LinkedList<String>();

		for (String singlefile: cm.getFiles(file)){
		
		final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		svnRepository.getFile(singlefile, SVNRepository.INVALID_REVISION, null, byteArrayOutputStream);
		
		for(String l: byteArrayOutputStream.toString().split("\\r?\\n")){
			validDependencies.add(l.trim());
		}}

		System.out.println("Valid dependencies are: "+validDependencies);
		for(Dependency d: deps){
			String depName = d.getName();
			if(!validDependencies.contains(depName)){
				undefined.add(depName);
			}
		}
		
		
		return undefined;
//		
//		
//		for (String a : lines) {
//			for (String b : ldep) {
//				if (b.equals(a)) {
//					System.out.println("The following dependency is correctly written: " + b);
//				} else
//
//			}
//		}
//		
//		boolean check = false;
//		try {
//			System.out.println("Checking dependencies list: " + file);
//			final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//			svnRepository.getFile(file, SVNRepository.INVALID_REVISION, null, byteArrayOutputStream);
//			String lines[] = byteArrayOutputStream.toString().split("\\r?\\n");
//
//			// if(deps.isEmpty()){
//			// sm.sendNotification(nh.getFailedSubject(), nh.getFailedBody());
//			// Exception e = new Exception("No dependency specified for this
//			// algorithm");
//			// throw e;
//			//
//			// }
//
//			// else if (!deps.isEmpty()) {
//			List<String> ldep = new LinkedList<>();
//			for (Dependency d : deps) {
//				ldep.add(d.getName());
//			}
//			for (String a : lines) {
//				for (String b : ldep) {
//					if (b.equals(a)) {
//						System.out.println("The following dependency is correctly written: " + b);
//						check = true;
//					} else
//						check = false;
//
//				}
//			}
//			// }
//		} catch (Exception a) {
//			a.getMessage();
//		}
//
//		return check;

	}

	public boolean checkIfAvaialable(String file, Collection<Dependency> deps) throws SVNException {
		SendMail sm = new SendMail();
		NotificationHelper nh = new NotificationHelper();

		boolean check = false;
		try {
			System.out.println("Checking dependencies list: " + file);
			final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			svnRepository.getFile(file, SVNRepository.INVALID_REVISION, null, byteArrayOutputStream);
			String lines[] = byteArrayOutputStream.toString().split("\\r?\\n");

			// if(deps.isEmpty()){
			// sm.sendNotification(nh.getFailedSubject(), nh.getFailedBody());
			// Exception e = new Exception("No dependency specified for this
			// algorithm");
			// throw e;
			//
			// }

			// else if (!deps.isEmpty()) {
			List<String> ldep = new LinkedList<>();
			for (Dependency d : deps) {
				ldep.add(d.getName());
			}
			for (String a : lines) {
				for (String b : ldep) {
					if (b.equals(a)) {
						System.out.println("The following dependency is correctly written: " + b);
						check = true;
					} else
						check = false;

				}
			}
			// }
		} catch (Exception a) {
			a.getMessage();
		}

		return check;

	}

	public void updateSVN(String file, Collection<Dependency> deps) {
		try {
			System.out.println("Updating dependencies list: " + file);

			final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			svnRepository.getFile(file, SVNRepository.INVALID_REVISION, null, byteArrayOutputStream);
			String lines[] = byteArrayOutputStream.toString().split("\\r?\\n");

			List<String> ldep = new LinkedList<>();
			for (Dependency d : deps) {
				ldep.add(d.getName());
			}
			List<String> aa = this.checkMatch(lines, ldep);
			Collections.sort(aa);

			final SVNDeltaGenerator deltaGenerator = new SVNDeltaGenerator();

			byte[] originalContents = byteArrayOutputStream.toByteArray();

			final ISVNEditor commitEditor = svnRepository.getCommitEditor("update dependencies", null);
			commitEditor.openRoot(-1);
			commitEditor.openFile(file, -1);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			for (String line : aa) {
				baos.write(line.getBytes());
				baos.write("\n".getBytes());

			}
			byte[] bytes = baos.toByteArray();

			commitEditor.applyTextDelta(file, md5(originalContents));

			final String checksum = deltaGenerator.sendDelta(file, new ByteArrayInputStream(originalContents), 0,
					new ByteArrayInputStream(bytes), commitEditor, true);
			
			
			commitEditor.closeFile(file, checksum);
			commitEditor.closeEdit();

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		finally {
			svnRepository.closeSession();
		}
	}

	public static String md5(byte[] contents) {
		final byte[] tmp = new byte[1024];
		final SVNChecksumInputStream checksumStream = new SVNChecksumInputStream(new ByteArrayInputStream(contents),
				"md5");
		try {
			while (checksumStream.read(tmp) > 0) {
				//
			}
			return checksumStream.getDigest();
		} catch (IOException e) {
			// never happens
			e.printStackTrace();
			return null;
		} finally {
			SVNFileUtil.closeFile(checksumStream);
		}
	}
	public List<String> checkMatch(String[] lines, List<String> ls) {
		Set<String> ss = new HashSet<String>(ls);
		ss.addAll(Arrays.asList(lines));
		return new ArrayList<>(ss);
	}

	
	public String getTimeZone() throws ParseException{
		Calendar cal = Calendar.getInstance();
		cal.getTime();				
		DateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
		Date fromDate = formatter.parse(cal.getTime().toString());
	    TimeZone central = TimeZone.getTimeZone("UTC");
	    formatter.setTimeZone(central);
	    System.out.println(formatter.format(fromDate));
		return formatter.format(fromDate);
	}
	
	public static void main(String[] args) throws SVNException, ParseException {
		

		
		SVNUpdater c = new  SVNUpdater();
		
		//File a = new File("/home/ngalante/Desktop/testCiro");
		//File b = new File ("/home/ngalante/Desktop/testB");
		//long unixTime = System.currentTimeMillis() / 1000L;
		//System.out.println(unixTime);
		//c.updateAlgorithmFiles(a);
		//c.updateAlgorithmFiles(b);
		//Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		
		Calendar cal = Calendar.getInstance();
		cal.getTime();				
		DateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
		Date fromDate = formatter.parse(cal.getTime().toString());
	    TimeZone central = TimeZone.getTimeZone("UTC");
	    formatter.setTimeZone(central);
	    System.out.println(formatter.format(fromDate));
		
		
		
	}

	
	
	
}
