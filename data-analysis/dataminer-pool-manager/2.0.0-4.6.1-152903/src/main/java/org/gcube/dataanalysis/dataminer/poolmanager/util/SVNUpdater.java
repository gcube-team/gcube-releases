package org.gcube.dataanalysis.dataminer.poolmanager.util;

import org.apache.commons.io.FileUtils;
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.ws.rs.core.NewCookie;

/**
 * Created by ggiammat on 5/9/17.
 */
public class SVNUpdater {

	private SVNRepository svnRepository;
	private ServiceConfiguration configuration;

	public SVNUpdater(ServiceConfiguration configuration) throws SVNException {
		this.svnRepository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(configuration.getSVNRepository()));
		ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager();
		this.svnRepository.setAuthenticationManager(authManager);
		this.configuration = configuration;
	}

//	public void updateRPRotoDeps(Algorithm algorithm) {
//		this.updateSVN(this.configuration.getSVNRProtoOSDepsList(), algorithm.getOSDependencies());
//		this.updateSVN(this.configuration.getSVNRProtoCRANDepsList(), algorithm.getCranDependencies());
//		this.updateSVN(this.configuration.getSVNRProtoGitHubDepsList(), algorithm.getGitHubDependencies());
//	}

	
	
	public String getDependencyFile(String language, String env){
		
		String a = "";
		if (env.equals("Dev")){
			a= this.getDevDependencyFile(language);
		}
		
		if (env.equals("Prod")){
			a= this.getProdDependencyFile(language);
		}

		if (env.equals("Proto")){
			a= this.getRProtoDependencyFile(language);
		}
		return a;
	}
	

	
	public String getRProtoDependencyFile(String language) {
		switch (language) {
		case "R":
			return this.configuration.getSVNRProtoCRANDepsList();
		case "R-blackbox":
			return this.configuration.getSVNRProtoRBDepsList();
		case "Java":
			return this.configuration.getSVNRProtoJavaDepsList();
		case "Knime-Workflow":
			return this.configuration.getSVNRProtoKWDepsList();
		case "Linux-compiled":
			return this.configuration.getSVNRProtoLinuxCompiledDepsList();
		case "Octave":
			return this.configuration.getSVNRProtoOctaveDepsList();
		case "Python":
			return this.configuration.getSVNRProtoPythonDepsList();
		case "Pre-Installed":
			return this.configuration.getSVNRProtoPreInstalledDepsList();
		case "Windows-compiled":
			return this.configuration.getSVNRProtoWCDepsList();
		default:
			return null;
		}
	}
	
	
	
	public String getProdDependencyFile(String language) {
		switch (language) {
		case "R":
			return this.configuration.getSVNRProdCRANDepsList();
		case "R-blackbox":
			return this.configuration.getSVNRProdRBDepsList();
		case "Java":
			return this.configuration.getSVNRProdJavaDepsList();
		case "Knime-Workflow":
			return this.configuration.getSVNRProdKWDepsList();
		case "Linux-compiled":
			return this.configuration.getSVNRProdLinuxCompiledDepsList();
		case "Octave":
			return this.configuration.getSVNRProdOctaveDepsList();
		case "Python":
			return this.configuration.getSVNRProdPythonDepsList();
		case "Pre-Installed":
			return this.configuration.getSVNRProdPreInstalledDepsList();
		case "Windows-compiled":
			return this.configuration.getSVNRProdWCDepsList();
		default:
			return null;
		}
	}

	
	public String getDevDependencyFile(String language) {
		switch (language) {
		case "R":
			return this.configuration.getSVNRDevCRANDepsList();
		case "R-blackbox":
			return this.configuration.getSVNRDevRBDepsList();
		case "Java":
			return this.configuration.getSVNRDevJavaDepsList();
		case "Knime-Workflow":
			return this.configuration.getSVNRDevKWDepsList();
		case "Linux-compiled":
			return this.configuration.getSVNRDevLinuxCompiledDepsList();
		case "Octave":
			return this.configuration.getSVNRDevOctaveDepsList();
		case "Python":
			return this.configuration.getSVNRDevPythonDepsList();
		case "Pre-Installed":
			return this.configuration.getSVNRDevPreInstalledDepsList();
		case "Windows-compiled":
			return this.configuration.getSVNRDevWCDepsList();
		default:
			return null;
		}
	}
	
	
	
	
	

	public void readRPRotoDeps(Algorithm algorithm) throws SVNException {
		if (algorithm.getLanguage().equals("R")) {
			this.checkIfAvaialable(this.configuration.getSVNRProtoCRANDepsList(), algorithm.getDependencies());
		}
		if (algorithm.getLanguage().equals("R-blackbox")) {
			this.checkIfAvaialable("", algorithm.getDependencies());
		}
		if (algorithm.getLanguage().equals("Java")) {
			this.checkIfAvaialable("", algorithm.getDependencies());
		}
		if (algorithm.getLanguage().equals("Knime-Workflow")) {
			this.checkIfAvaialable(this.configuration.getSVNRProtoCRANDepsList(), algorithm.getDependencies());
		}
		if (algorithm.getLanguage().equals("Linux-compiled")) {
			this.checkIfAvaialable("", algorithm.getDependencies());
		}
		if (algorithm.getLanguage().equals("Octave")) {
			this.checkIfAvaialable("", algorithm.getDependencies());
		}
		if (algorithm.getLanguage().equals("Python")) {
			this.checkIfAvaialable("", algorithm.getDependencies());
		}
		if (algorithm.getLanguage().equals("Windows-compiled")) {
			this.checkIfAvaialable("", algorithm.getDependencies());
		}
		if (algorithm.getLanguage().equals("Pre-Installed")) {
			this.checkIfAvaialable("", algorithm.getDependencies());
		}
	}

	public void readProdDeps(Algorithm algorithm) throws SVNException {
		if (algorithm.getLanguage().equals("R")) {
			this.checkIfAvaialable(this.configuration.getSVNRProdCRANDepsList(), algorithm.getDependencies());
		}
		if (algorithm.getLanguage().equals("R-blackbox")) {
			this.checkIfAvaialable("", algorithm.getDependencies());
		}
		if (algorithm.getLanguage().equals("Java")) {
			this.checkIfAvaialable("", algorithm.getDependencies());
		}
		if (algorithm.getLanguage().equals("Knime-Workflow")) {
			this.checkIfAvaialable("", algorithm.getDependencies());
		}
		if (algorithm.getLanguage().equals("Linux-compiled")) {
			this.checkIfAvaialable("", algorithm.getDependencies());
		}
		if (algorithm.getLanguage().equals("Octave")) {
			this.checkIfAvaialable("", algorithm.getDependencies());
		}
		if (algorithm.getLanguage().equals("Python")) {
			this.checkIfAvaialable("", algorithm.getDependencies());
		}
		if (algorithm.getLanguage().equals("Windows-compiled")) {
			this.checkIfAvaialable("", algorithm.getDependencies());
		}
		if (algorithm.getLanguage().equals("Pre-Installed")) {
			this.checkIfAvaialable("", algorithm.getDependencies());
		}
	}
	
	
	
	public void readRDevDeps(Algorithm algorithm) throws SVNException {
		if (algorithm.getLanguage().equals("R")) {
			this.checkIfAvaialable(this.configuration.getSVNRProtoCRANDepsList(), algorithm.getDependencies());
		}
		if (algorithm.getLanguage().equals("R-blackbox")) {
			this.checkIfAvaialable("", algorithm.getDependencies());
		}
		if (algorithm.getLanguage().equals("Java")) {
			this.checkIfAvaialable("", algorithm.getDependencies());
		}
		if (algorithm.getLanguage().equals("Knime-Workflow")) {
			this.checkIfAvaialable(this.configuration.getSVNRProtoCRANDepsList(), algorithm.getDependencies());
		}
		if (algorithm.getLanguage().equals("Linux-compiled")) {
			this.checkIfAvaialable("", algorithm.getDependencies());
		}
		if (algorithm.getLanguage().equals("Octave")) {
			this.checkIfAvaialable("", algorithm.getDependencies());
		}
		if (algorithm.getLanguage().equals("Python")) {
			this.checkIfAvaialable("", algorithm.getDependencies());
		}
		if (algorithm.getLanguage().equals("Windows-compiled")) {
			this.checkIfAvaialable("", algorithm.getDependencies());
		}
		if (algorithm.getLanguage().equals("Pre-Installed")) {
			this.checkIfAvaialable("", algorithm.getDependencies());
		}
	}

	
	
	
	

//	public void updateProdDeps(Algorithm algorithm) {
//		this.updateSVN(this.configuration.getSVNProdOSDepsList(), algorithm.getOSDependencies());
//		this.updateSVN(this.configuration.getSVNRProdCRANDepsList(), algorithm.getCranDependencies());
//		this.updateSVN(this.configuration.getSVNRProdGitHubDepsList(), algorithm.getGitHubDependencies());
//	}

	public void updateSVNAlgorithmList(Algorithm algorithm, String targetVRE, String category, String algorithm_type, String user, String env) {
		
		if (env.equals("Dev")){
		this.updateSVNAlgorithmList(this.configuration.getSVNDevAlgorithmsList(), algorithm, targetVRE, category,algorithm_type, user, env);
		}
		
		if (env.equals("Prod")){
		this.updateSVNAlgorithmList(this.configuration.getSVNProdAlgorithmsList(), algorithm, targetVRE, category, algorithm_type, user, env);
		}
		
		if (env.equals("Proto")){
		this.updateSVNAlgorithmList(this.configuration.getSVNRProtoAlgorithmsList(), algorithm, targetVRE, category, algorithm_type, user, env);
		}
	}

	public void updateSVNProdAlgorithmList(Algorithm algorithm, String targetVRE, String category, String algorithm_type, String user, String env) {
		this.updateSVNAlgorithmList(this.configuration.getSVNProdAlgorithmsList(), algorithm, targetVRE, category, algorithm_type, user, env);
	}

	
	public void updateAlgorithmFiles(File a) throws SVNException{
		this.updateAlgorithmList(this.configuration.getSVNMainAlgoRepo(), a);
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
	
	
	public void updateSVNAlgorithmList(String file, Algorithm algorithm, String targetVRE, String category, String algorithm_type, String user, String env) {
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
			newContent.add(this.generateAlgorithmEntry(algorithm, targetVRE, category,algorithm_type, env));
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

	public String generateAlgorithmEntry(Algorithm algorithm, String targetVRE, String category, String algorithm_type,String env) throws ParseException {
		//Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		//long unixTime = System.currentTimeMillis() / 1000L;

		StringBuffer sb = new StringBuffer("| ");
		sb.append(algorithm.getName() + " | ");
		sb.append(algorithm.getFullname() + " | ");
		sb.append(category + " | ");
		sb.append(env + " | ");
		sb.append("<notextile>./addAlgorithm.sh " + algorithm.getName() + " " + algorithm.getCategory() + " "
				+ algorithm.getClazz() + " " + targetVRE + " " + algorithm_type + " N "
				+ algorithm.getPackageURL() + " \"" + algorithm.getDescription() + "\" </notextile> | ");
		sb.append("none | ");
		sb.append(this.getTimeZone() + " | ");
		return sb.toString();
	}
	
	
	public Collection<String> getUndefinedDependencies(String file, Collection<Dependency> deps) throws SVNException {
//		SendMail sm = new SendMail();
//		NotificationHelper nh = new NotificationHelper();

		
		System.out.println("Checking dependencies list: " + file);
		final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		svnRepository.getFile(file, SVNRepository.INVALID_REVISION, null, byteArrayOutputStream);
		
		List<String> validDependencies = new LinkedList<String>();
		for(String l: byteArrayOutputStream.toString().split("\\r?\\n")){
			validDependencies.add(l.trim());
		}
				
		List<String> undefined = new LinkedList<String>();
		
		
		
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
		Date fromDate = (Date)formatter.parse(cal.getTime().toString());
	    TimeZone central = TimeZone.getTimeZone("UTC");
	    formatter.setTimeZone(central);
	    System.out.println(formatter.format(fromDate));
		return formatter.format(fromDate);
	}
	
	public static void main(String[] args) throws SVNException, ParseException {
		
		ServiceConfiguration sc = new ServiceConfiguration("/home/ngalante/workspace/dataminer-pool-manager/src/main/resources/service.properties");
		
		SVNUpdater c = new  SVNUpdater(sc);
		
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
		Date fromDate = (Date)formatter.parse(cal.getTime().toString());
	    TimeZone central = TimeZone.getTimeZone("UTC");
	    formatter.setTimeZone(central);
	    System.out.println(formatter.format(fromDate));
		
		
		
	}

	
	
	
}
