package org.gcube.dataanalysis.dataminer.poolmanager.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

import org.gcube.dataanalysis.dataminer.poolmanager.clients.configuration.Configuration;
import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Algorithm;
import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Dependency;
import org.gcube.dataanalysis.dataminer.poolmanager.util.exception.GenericException;
import org.gcube.dataanalysis.dataminer.poolmanager.util.exception.SVNCommitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.SVNErrorMessage;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.internal.wc.SVNFileUtil;
import org.tmatesoft.svn.core.internal.wc.admin.SVNChecksumInputStream;
import org.tmatesoft.svn.core.io.ISVNEditor;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.diff.SVNDeltaGenerator;

/**
 * Created by ggiammat on 5/9/17.
 */
public abstract class SVNUpdater {

	private SVNRepository svnRepository;
	private Configuration configuration;
	private Logger logger;

	public SVNUpdater(Configuration configuration) throws SVNException {
		this.configuration = configuration;
		this.svnRepository = SVNRepositoryManager.getInstance(configuration).getSvnRepository();
		this.logger = LoggerFactory.getLogger(SVNUpdater.class);
	}

//	public void updateRPRotoDeps(Algorithm algorithm) {
//		this.updateSVN(this.configuration.getSVNRProtoOSDepsList(), algorithm.getOSDependencies());
//		this.updateSVN(this.configuration.getSVNRProtoCRANDepsList(), algorithm.getCranDependencies());
//		this.updateSVN(this.configuration.getSVNRProtoGitHubDepsList(), algorithm.getGitHubDependencies());
//	}

	
	
	public  String getDependencyFile(String language/*, String env*/)
	{
		return getDependencyFile(this.configuration,language);
	}
	
	
	
	
	
	private String getDependencyFile (Configuration configuration, String language)
	{
		this.logger.debug("Getting dependency file for language "+language);
		
		switch (language) 
		{
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
		case "Python3.6":
			return configuration.getSVNPython3_6DepsList();	
		case "Pre-Installed":
			return configuration.getSVNPreInstalledDepsList();
		case "Windows-compiled":
			return configuration.getSVNWCDepsList();
		default:
			return null;
		}
	}


	public boolean  updateSVNAlgorithmList(Algorithm algorithm, String targetVRE, String category, String algorithm_type, String user/*, String env*/)
	{
		return this.updateSVNAlgorithmList(this.configuration.getSVNAlgorithmsList(), algorithm, targetVRE, category, algorithm_type, user);
	}
	

	
	public void updateAlgorithmFiles(File a) throws SVNException, SVNCommitException{
		//this.updateAlgorithmList(this.configuration.getSVNMainAlgoRepo(), a);
		this.updateAlgorithmList(this.configuration.getRepository(), a);
	}

	

	
	
	
	private void updateAlgorithmList(String svnMainAlgoRepo, File algorithmsFile) throws SVNException, SVNCommitException
	{
		this.logger.debug("Adding .jar file: " + algorithmsFile + " to repository " + svnMainAlgoRepo);
		
		try
		{
			
			if (fileExists(svnMainAlgoRepo+File.separator+algorithmsFile.getName(), -1))
			{
				this.updateFile(new FileInputStream(algorithmsFile), svnMainAlgoRepo, algorithmsFile.getName());
				
			}
			else this.putFile(new FileInputStream(algorithmsFile), svnMainAlgoRepo,algorithmsFile.getName());
		} 
		catch (FileNotFoundException e)
		{
			this.logger.error("Temporary algorithm file not found: this exception should not happen",e);
		}
		finally
		{
			this.svnRepository.closeSession();
		}
	}

	
	
	public void putFile(FileInputStream fileInputSreeam, String destinationFolder, String fileName) throws SVNException, SVNCommitException 
	{
		this.logger.debug("Putting new file on the SVN repository");
		final ISVNEditor commitEditor = svnRepository.getCommitEditor("Add algorithm to list", null);
		commitEditor.openRoot(-1);
		commitEditor.openDir(destinationFolder, -1);
		String filePath = destinationFolder + "/" + fileName;
		commitEditor.addFile(filePath, null, -1);
		commitEditor.applyTextDelta(filePath, null);
	    SVNDeltaGenerator deltaGenerator = new SVNDeltaGenerator();
	    String checksum = deltaGenerator.sendDelta(filePath, fileInputSreeam, commitEditor, true);
	    commitEditor.closeFile(filePath, checksum);
	    commitEditor.closeDir();
		commitEditor.closeDir();
		SVNCommitInfo info = commitEditor.closeEdit();
		SVNErrorMessage errorMessage = info.getErrorMessage();
		
		if (errorMessage != null)
		{
			this.logger.error("Operation failed: "+errorMessage.getFullMessage());
			throw new SVNCommitException(errorMessage,fileName);
		}
		
		this.logger.debug("Operation completed");
	}
	
	
	public void updateFile(FileInputStream fileInputStream, String destinationFolder, String fileName) throws SVNException, SVNCommitException  {
		
		this.logger.debug("Updating existing file on the SVN repository");		
		final ISVNEditor commitEditor = svnRepository.getCommitEditor("Updating algorithm", null);
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
	    String checksum = deltaGenerator.sendDelta(filePath, fileInputStream, commitEditor, true);
	    commitEditor.closeFile(filePath, checksum);
	    commitEditor.closeDir();
		commitEditor.closeDir();
		SVNCommitInfo info = commitEditor.closeEdit();
		SVNErrorMessage errorMessage = info.getErrorMessage();
		
		if (errorMessage != null)
		{
			this.logger.error("Operation failed: "+errorMessage.getFullMessage());

			throw new SVNCommitException(errorMessage,fileName+" to be updated");
		}
		
		this.logger.debug("Operation completed");
	}
	
	
	
	
	 public boolean fileExists(String path, long revision) throws SVNException {
		    SVNNodeKind kind = svnRepository.checkPath(path, revision);
		    if (kind == SVNNodeKind.FILE) {
		      return true;
		    }
		    return false;
		}
	
	
	
	
//	public static ByteArrayInputStream reteriveByteArrayInputStream(File file) throws IOException 
//	{
//				
//	    return new ByteArrayInputStream(FileUtils.readFileToByteArray(file));
//	}
	
	
	private boolean updateSVNAlgorithmList(String file, Algorithm algorithm, String targetVRE, String category, String algorithm_type, String user/*, String env*/) 
	{
		boolean response = false;
		
		try {
			this.logger.debug("Updating algorithm list: " + file);
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
			SVNCommitInfo info = commitEditor.closeEdit();
			SVNErrorMessage errorMessage = info.getErrorMessage();
			
			if (errorMessage != null)
			{
				this.logger.error("Operation failed: "+errorMessage.getFullMessage());
				response = false;
			}
			else response = true;
			

		} 
		catch (Exception ex) 
		{
			this.logger.error("Unable to commit algorithm list",ex);
			response = false;
		}

		finally 
		{
			svnRepository.closeSession();
		}
		
		return response;
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
		
		this.logger.info("Algo details: "+sb.toString());
		
		return sb.toString();
	}
	
	
	public Collection<String> getUndefinedDependencies(String file, Collection<Dependency> deps) throws GenericException
	{
		try
		{
//			SendMail sm = new SendMail();
//			NotificationHelper nh = new NotificationHelper();

			List<String> undefined = new LinkedList<String>();
			
			//to fix in next release: if the file is not present for that language in the service.properties then skip and return null list of string
			//just to uncomment the following lines

			if(file.isEmpty()){
			return undefined;
			}
			
			
			this.logger.debug("Checking dependencies list: " + file);
			

			List<String> validDependencies = new LinkedList<String>();

			for (String singlefile: CheckMethod.getFiles(file)){
			
			final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			svnRepository.getFile(singlefile, SVNRepository.INVALID_REVISION, null, byteArrayOutputStream);
			
			for(String l: byteArrayOutputStream.toString().split("\\r?\\n")){
				validDependencies.add(l.trim());
			}}

			this.logger.debug("Valid dependencies are: "+validDependencies);
			for(Dependency d: deps){
				String depName = d.getName();
				if(!validDependencies.contains(depName)){
					undefined.add(depName);
				}
			}
			
			
			return undefined;
//			
//			
//			for (String a : lines) {
//				for (String b : ldep) {
//					if (b.equals(a)) {
//						System.out.println("The following dependency is correctly written: " + b);
//					} else
	//
//				}
//			}
//			
//			boolean check = false;
//			try {
//				System.out.println("Checking dependencies list: " + file);
//				final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//				svnRepository.getFile(file, SVNRepository.INVALID_REVISION, null, byteArrayOutputStream);
//				String lines[] = byteArrayOutputStream.toString().split("\\r?\\n");
	//
//				// if(deps.isEmpty()){
//				// sm.sendNotification(nh.getFailedSubject(), nh.getFailedBody());
//				// Exception e = new Exception("No dependency specified for this
//				// algorithm");
//				// throw e;
//				//
//				// }
	//
//				// else if (!deps.isEmpty()) {
//				List<String> ldep = new LinkedList<>();
//				for (Dependency d : deps) {
//					ldep.add(d.getName());
//				}
//				for (String a : lines) {
//					for (String b : ldep) {
//						if (b.equals(a)) {
//							System.out.println("The following dependency is correctly written: " + b);
//							check = true;
//						} else
//							check = false;
	//
//					}
//				}
//				// }
//			} catch (Exception a) {
//				a.getMessage();
//			}
	//
//			return check;
		} catch (SVNException e)
		{
			throw new GenericException(e);
		}
		


	}

	public boolean checkIfAvaialable(String file, Collection<Dependency> deps) throws SVNException {
		//SendMail sm = new SendMail();
		//NotificationHelper nh = new NotificationHelper();

		boolean check = false;
		try {
			this.logger.info("Checking dependencies list: " + file);
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
		} catch (Exception a) 
		{
			this.logger.error(a.getMessage(),a);
			
		}

		return check;

	}

	public void updateSVN(String file, Collection<Dependency> deps) {
		try {
			this.logger.info("Updating dependencies list: " + file);

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
	    this.logger.info(formatter.format(fromDate));
		return formatter.format(fromDate);
	}
	
	public static void main(String[] args) throws SVNException, ParseException {
		

		
	//	SVNUpdater c = new  SVNUpdater();
		
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
