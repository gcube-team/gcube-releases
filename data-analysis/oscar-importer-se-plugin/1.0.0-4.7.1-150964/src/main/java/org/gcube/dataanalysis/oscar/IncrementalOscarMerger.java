package org.gcube.dataanalysis.oscar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.apache.commons.io.FileSystemUtils;
import org.apache.commons.io.FileUtils;
import org.gcube.dataanalysis.oscar.util.FTPDownloader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ucar.nc2.Attribute;
import ucar.nc2.FileWriter2;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFileWriter;
import ucar.nc2.util.CancelTaskImpl;

public class IncrementalOscarMerger {

  // the first available year in the OSCAR dataset
  private static final Integer OSCAR_START_YEAR = 1992;
  
  // URL pattern of online yearly files of the OSCAR dataset
  private String yearlyOscarRemoteFilePattern = "ftp://podaac-ftp.jpl.nasa.gov/allData/oscar/preview/L4/resource/LAS/oscar_third_deg_180/oscar_vel${YYYY}_180.nc";

  // pattern for local yearly files
  private String yearlyOscarLocalFilePattern = "oscar_vel${YYYY}_180.nc";

  // local working directory directory
  private static String workdir = "/tmp/oscar-merger";
  
  // the pattern to use for merged files
  private static String mergedFilePattern = "oscar_vel_${FROMYEAR}-${TOYEAR}_180.nc";

  // the pattern to use for merge descriptors
  private static String mergeDescriptorFilePattern = "oscar_vel_${FROMYEAR}-${TOYEAR}_180.xml";

  // the first year to consider in the merge
  private Integer computedStartYear;

  // the last year to consider in the merge
  private Integer computedEndYear;
  
  // how many remote files to download before merging
  private Integer intervalSize = 3;
  
  // if we want to avoid removing files
  private Boolean removeTmpFiles = true;

  // debug mode
  private Boolean debug = false;
  
  // the logger
  private static final Logger logger = LoggerFactory.getLogger(OscarMerger.class);

  public IncrementalOscarMerger() {
  }

  /**
   * Guess the first available year of the dataset, starting from 1990
   * 
   * @return
   */
  private Integer getStartYear() {
    if (this.computedStartYear == null) {
      // start from OSCAR_START_YEAR and look for the first available year
      // in debug mode, start from last year
      int year = debug ? Calendar.getInstance().get(Calendar.YEAR)-1 : OSCAR_START_YEAR;
      for (; year <= Calendar.getInstance().get(Calendar.YEAR); year++) {
        if(checkRemoteYearFileExist(year)) {
          this.computedStartYear = year;
          logger.info("Setting start year of the merge is " + this.computedStartYear);
          break;
        }
      }
    }
    return this.computedStartYear;
  }

  /**
   * Guess the last available year of the dataset, starting from current year
   * and going backward.
   * 
   * @return
   */
  private Integer getEndYear() {
    if (this.computedEndYear == null) {
      // start at current year and go back
      for (int year = Calendar.getInstance().get(Calendar.YEAR); year > OSCAR_START_YEAR; year--) {
        if(checkRemoteYearFileExist(year)) {
          this.computedEndYear = year;
          logger.info("Setting end year of the merge is " + this.computedEndYear);
          break;
        }
      }
    }
    return this.computedEndYear;
  }

  /**
   * Retrieve the most recent merged file in the workspace
   * @return
   */
  private File getLastUsefulMergedFile() {
    int startYear = this.getStartYear();
    int endYear = this.getEndYear();
    for(int i=endYear; i>=startYear; i--) {
      File f = new File(this.getWorkDir(), this.formatMergedFileName(startYear, i));
      // if exists and is stable
      if(f.exists() && !canStillChange(i)) {
        return f;
      }
    }
    return null;
  }
  
  /**
   * Guess if data for the given year can still change. For example, data for
   * the current year surely can; but also data from previous year, if we're
   * running at the first days of January.
   * 
   * @param year
   * @return
   */
  private boolean canStillChange(int year) {
    // a remote OSCAR file might change if:
    // 1) it's about the current year
    // 2) it's about the previous year and no current year is available yet
    int currentYear = Calendar.getInstance().get(Calendar.YEAR);
    if(year==currentYear) {
      return true;
    }
    if(year==currentYear-1 && !checkRemoteYearFileExist(currentYear)) {
      return true;
    }
    return false;
  }
  
  private boolean checkRemoteYearFileExist(int year) {
    return FTPDownloader.checkFtpFileExists(this.getSourceURLForYear(year));
  }
  
  private String getSourceURLForYear(Integer year) {
    return yearlyOscarRemoteFilePattern.replaceAll("\\$\\{YYYY\\}", year+"");
  }
  
  private File getLocalFileForYear(Integer year) {
    return new File(this.getWorkDir(), yearlyOscarLocalFilePattern.replaceAll("\\$\\{YYYY\\}", year + ""));
  }

  private File getMergedFilesForYears(Integer from, Integer to) {
    return new File(this.getWorkDir(), this.formatMergedFileName(from, to));
  }

    
  private File generateDescriptor(List<String> paths, File descriptorFile) {
    String out = "";
    out += "<netcdf xmlns=\"http://www.unidata.ucar.edu/namespaces/netcdf/ncml-2.2\">\n";
    out += "  <attribute name=\"title\" value=\"OSCAR Velocity Dataset\"/>\n";
    out += "  <aggregation type=\"joinExisting\" dimName=\"time\">\n";
    for(String p:paths) {
      out += "    <netcdf location=\"" + p + "\"/>\n";
    }
    out += "  </aggregation>\n";
    out += "</netcdf>";
    try (PrintWriter pw = new PrintWriter(descriptorFile)) {
      pw.println(out);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    return descriptorFile;
  }
        
  /**
   * Return the File associated with the working directory.
   * 
   * @return
   */
  private File getWorkDir() {
    File out = new File(workdir);
    out.mkdir();
    return out;
  }
  
  /**
   * Compute the name of the merged file, by including start and end years.
   * 
   * @param fromYear
   * @param toYear
   * @return
   */
  private String formatMergedFileName(Integer fromYear, Integer toYear) {
    return mergedFilePattern.replaceAll("\\$\\{FROMYEAR\\}",
        fromYear.toString()).replaceAll("\\$\\{TOYEAR\\}", toYear.toString());
  }

  /**
   * Compute the name of the descriptor file, by including start and end years.
   * 
   * @return
   */
  private String formatDescriptorFileName(Integer fromYear, Integer toYear) {
    return mergeDescriptorFilePattern.replaceAll("\\$\\{FROMYEAR\\}",
        fromYear.toString()).replaceAll("\\$\\{TOYEAR\\}", toYear.toString());
  }  
  
  /**
   * Extract the end year from a merged file.
   * 
   * @param f
   * @return
   */
  private Integer extractEndYear(String fileName) {
    // brute-force implementation. Try all years until it matches
    for(int year=this.getStartYear(); year<=this.getEndYear(); year++) {
      String name = this.formatMergedFileName(this.getStartYear(), year);
      if(fileName.equals(name)) {
        return year;
      }
      name = this.formatDescriptorFileName(this.getStartYear(), year);
      if(fileName.equals(name)) {
        return year;
      }
    }
    return null;
  }
  
  /**
   * How many remote files to download before merging?
   * @return
   */
  private int getIntervalSize() {
    return intervalSize;
  }
  
  /**
   * Perform the whole merge by: 1) checking preconditions (e.g. free disk
   * space); 2) downloading dataset files; 3) generating the descriptor and 4)
   * doing the merge itself.
   * 
   * @throws Exception
   */
  public String merge() throws Exception {
    
    // do a preliminary cleanup
    logger.info("Doing some preliminary cleanup...");
    this.cleanup();

    // check there's enough disk space
    this.checkReady();

    /*
     * Get last merged file in working dir (up to 2015; so skip 2017 and 2016 at minimum). Call it Y.
     * 
     * if null
     *   lastMerged = first year in dataset
     * 
     * For each year after Y
     *   download the .nc file
     *   generate a descriptor including the merged and the new year
     *   merge them
     *   if(mergedFile.endYear <= 2015) // so that it can be reused
     *     remove lastMerged // the previous one can be removed
     *   lastMerged = the mergedFile
     */
    
    // Get last merged file in working dir (up to 2015; so skip 2017 and 2016 at minimum). Call it Y.
    File lastMerged = this.getLastUsefulMergedFile();
    
    if(lastMerged!=null) {
      
      logger.info("Found a previously merged file: " + lastMerged.getAbsolutePath());
      
    } else {
      
      // download first-year file
      File currentYearOscarFile = this.getLocalFileForYear(this.getStartYear());
      logger.info("Downloading file to " + currentYearOscarFile.getAbsolutePath());
      this.downloadFile(yearlyOscarRemoteFilePattern.replaceAll("\\$\\{YYYY\\}", this.getStartYear() + ""), currentYearOscarFile, false);

      // make it the lastMerged
      lastMerged = this.getMergedFilesForYears(this.getStartYear(), this.getStartYear());
      logger.info("Renaming it as " + lastMerged.toString());
      FileUtils.moveFile(FileUtils.getFile(currentYearOscarFile),
          FileUtils.getFile(lastMerged));
    }

    logger.info("End year of the last merged file is " + this.extractEndYear(lastMerged.getName()));

    // for each year after endYear
    int year = this.extractEndYear(lastMerged.getName())+1;
    while(year<=this.getEndYear()) {  
      
      // prepare a list of paths to merge
      List<String> paths = new Vector<>();
      paths.add(lastMerged.getAbsolutePath());
      
      // download a set of max N .nc files
      int intervalEnd = year;
      for(int i=0; i<this.getIntervalSize() && year+i<=this.getEndYear(); i++) {
        intervalEnd = year+i;
        File currentYearOscarFile = this.getLocalFileForYear(intervalEnd);
        logger.info("Downloading file for year " + intervalEnd);
        this.downloadFile(this.getSourceURLForYear(intervalEnd), currentYearOscarFile, false);
        paths.add(currentYearOscarFile.getAbsolutePath());
      }

      // generate the descriptor
      logger.info("Generating descriptor...");
      File mergeDescriptor = new File(this.getWorkDir(), this.formatDescriptorFileName(this.getStartYear(), intervalEnd)); 
      this.generateDescriptor(paths, mergeDescriptor);
      logger.info("Descriptor is " + mergeDescriptor.getAbsolutePath());
      
      // merge the files
      File mergedFile = this.getMergedFilesForYears(this.getStartYear(), intervalEnd);
      logger.info("Merging files at " + mergedFile.getAbsolutePath());
      this.mergeDescribedFilesTo(mergeDescriptor, mergedFile);
      logger.info("Merged");
      
      // if the merged file is stable, remove older one
      if(!this.canStillChange(this.extractEndYear(mergedFile.getName()))) {
        logger.info("Removing " + lastMerged.getName() + " since the merged is stable. We'll keep it instead.");
        this.removeFileOrDir(lastMerged);
      } else {
        logger.info("Keeping " + lastMerged.getName() + " since the merged one is not yet stable.");
      }
      
      // remove descriptor
      logger.info("Removing descriptor: " + mergeDescriptor.getAbsolutePath());
      this.removeFileOrDir(mergeDescriptor);
      
      // remove last-year files
      logger.info("Remove yearly files... ");
      for (int i = year; i <= intervalEnd; i++) {
        logger.info("Removing file " + this.getLocalFileForYear(i).getName());
        this.removeFileOrDir(this.getLocalFileForYear(i));
      }
      // update lastMerged
      lastMerged = mergedFile;
      
      // update year
      year = intervalEnd+1;
      
    }
    
    return lastMerged.getAbsolutePath();
     
  }

  /**
   * Download the file at the given URL to the given local File
   * @param source
   * @param destination
   * @param forceDownload if true, downloads the file, even if already there. Default is FALSE
   * @throws IOException
   */
  private void downloadFile(String source, File destination, Boolean forceDownload) throws IOException, Exception {
    if(forceDownload==null) {
      forceDownload = false;
    }
    if(!forceDownload && destination.exists()) {
      logger.info("File already downloaded and completed, skipping.");
      return;
    }
    if (source.startsWith("ftp:")) {

      // give the file being downloaded a temporary name. So that if the
      // download is interrupted, this file will not be considered at next
      // download.
      String tmpFileName = destination.getAbsolutePath() + ".tmp";

      // download file
      FTPDownloader ftpDownloader = new FTPDownloader(source);
      ftpDownloader.downloadFile(source, tmpFileName);
      logger.info("FTP File downloaded successfully");
      ftpDownloader.disconnect();

      // rename to original name
      FileUtils.moveFile(FileUtils.getFile(tmpFileName),
          FileUtils.getFile(destination.getAbsolutePath()));

    } else {
      throw new Exception("Only ftp downloads currently supported");
    }
  }  
  
  /**
   * Perform the merge of the local files using the generated descriptor
   * @throws Exception
   */
  private void mergeDescribedFilesTo(File descriptor, File merged) throws Exception {

    String datasetIn = descriptor.getAbsolutePath();

    // merge into a temporarily-named file
    String datasetOut = merged.getAbsolutePath()+".tmp";

    CancelTaskImpl cancel = new CancelTaskImpl();
    NetcdfFile ncfileIn = ucar.nc2.dataset.NetcdfDataset.openFile(datasetIn, cancel);

    logger.info(String.format("NetcdfDatataset read from %s write to %s ",
        datasetIn, datasetOut));

    // merge
    NetcdfFileWriter.Version version = NetcdfFileWriter.Version.netcdf4;
    FileWriter2 writer = new ucar.nc2.FileWriter2(ncfileIn, datasetOut,
        version, null);
    writer.getNetcdfFileWriter().setLargeFile(true);
    NetcdfFile ncfileOut = writer.write(cancel);

    // print attributes of the merged file
    for (Attribute a : ncfileOut.getGlobalAttributes()) {
      logger.info(a.toString());
    }

    // close stuff
    if (ncfileOut != null) {
      ncfileOut.close();
    }
    ncfileIn.close();
    cancel.setDone(true);
    
    // remove .tmp
    FileUtils.moveFile(FileUtils.getFile(datasetOut), merged);

    logger.info(String.format("%s%n", cancel));
  }

  public void cleanup() {
    this.cleanup(false);
  }
  
  /**
   * Clean the workspace.
   * 
   * @param complete
   *          set to true, remove all files and directory. Otherwise, keep the
   *          directory and files that can be reused only.
   */
  public void cleanup(boolean complete) {
    if(complete) {
      this.removeFileOrDir(this.getWorkDir());
    } else {
      Collection<String> filesToKeep = new Vector<>();
      // remove all files except those that can be reused
      File lastUseful = this.getLastUsefulMergedFile();
      if(lastUseful!=null) {
        filesToKeep.add(lastUseful.getAbsolutePath());
      }
      for(File f:this.getWorkDir().listFiles()) {
        if(filesToKeep.contains(f.getAbsolutePath())) {
          logger.info("Keeping " + f.getAbsolutePath());
        } else {
          this.removeFileOrDir(f);
        }
      }
    }
  }
  
  /**
   * Removes the given file or directory. It only removes things within "/tmp".
   * Furthermore it does not remove files ending with the suffix '.keep'
   * 
   * @param file
   */
  private void removeFileOrDir(File file) {
    logger.info("Removing " + file.getAbsolutePath());
    
    // to avoid erasing my hard disk!
    if (!file.getAbsolutePath().startsWith("/tmp")) {
      logger.info("NOT REMOVING ANYTHING OUTSIDE /tmp");
      return;
    }
    
    if(file.getAbsolutePath().endsWith(".keep")) {
      logger.info("NOT REMOVING " + file.getAbsolutePath() + " since it has to be kept");
      return;
    }

    // remove file, if enabled
    if(removeTmpFiles){
      FileUtils.deleteQuietly(file);
      logger.info("Removed");
    } else {
      logger.info("Removing IS DISABLED");
    }

  }
  
  /**
   * Do all possible checks to ensure the process can safely start (and
   * complete).
   * 
   * @throws Exception
   */
  public void checkReady() throws Exception {
    this.checkEnoughDiskSpace();
  }

  /**
   * Ensure there's enough disk space to download the remote files and create
   * the merged one.
   * 
   * @throws Exception
   */
  private void checkEnoughDiskSpace() throws Exception {
    
    logger.info("Checking needed space...");
    
    // 1.198.005.516 is the size of a yearly OSCAR file
    // 417.518.585 is the average size of a year, in the merged file
    // -1.2 is a constant computed experimentally
    
    // space for one back merged file
    Long backMergedFileSize = 0l;
    File f = this.getLastUsefulMergedFile();
    if(f==null) {
      backMergedFileSize = 417518585l*(long)(this.getEndYear()-intervalSize-this.getStartYear()-1.2);
    }
    logger.info("Size of the last useful merge: " + backMergedFileSize);
    
    // space for the new merged file
    Long newMergedFileSize = 417518585l*(long)(this.getEndYear()-this.getStartYear()-1.2);
    logger.info("Size of the new merge: " + newMergedFileSize);
    
    // space for all the yearly files
    Long yearlyFilesSize = 1198005516l*intervalSize;
    logger.info("Yearly files: " + yearlyFilesSize);

    // needed space is the sum of the three
    Long neededSpace = backMergedFileSize + newMergedFileSize + yearlyFilesSize;

    logger.info("Needed disk space: " + neededSpace + " bytes");

    Long availableSpace = FileSystemUtils.freeSpaceKb(this.getWorkDir()
        .getAbsolutePath()) * 1024;
    
    logger.info("Available disk space: " + availableSpace + " bytes");

    if (neededSpace < availableSpace) {
      logger.info("There's enough disk space to proceed");
      Double percent = neededSpace * 1d / availableSpace * 1d * 100;
      String message = String
          .format(
              "I'm about to use %.0f%% of the available disk space (but I'll release it afterwards, I promise)",
              percent);
      logger.info(message);
    } else {
      Double gb = neededSpace / 1024d / 1024d / 1024d;
      String message = String.format(
          "Not enough disk space. At least %2.2fGB of available disk needed.",
          gb);
      throw new Exception(message);
    }
  }

  public void setIntervalSize(Integer intervalSize) {
    if(intervalSize!=null) {
      // to prevent negative size
      this.intervalSize = Math.max(intervalSize, 1);
    }
  }

  public void setRemoveTmpFiles(Boolean removeTmpFiles) {
    this.removeTmpFiles = removeTmpFiles;
  }

  public void setDebug(Boolean debug) {
    this.debug = debug;
    
    // reset the start year
    this.computedStartYear = null;
  }

}
