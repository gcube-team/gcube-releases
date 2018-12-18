package org.gcube.dataanalysis.oscar;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;

import org.apache.commons.io.FileSystemUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.gcube.dataanalysis.oscar.util.FTPDownloader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ucar.nc2.Attribute;
import ucar.nc2.FileWriter2;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFileWriter;
import ucar.nc2.util.CancelTaskImpl;

public class OscarMerger {

  // URL pattern of the yearly files of the OSCAR dataset
  // private static String yearlyOscarFilePattern =
  // "https://thredds.jpl.nasa.gov/thredds/dodsC/OSCAR_L4_OC_third-deg/oscar_vel${YYYY}.nc.gz";
  // private static String yearlyOscarFilePattern =
  // "https://podaac-opendap.jpl.nasa.gov/opendap/hyrax/allData/oscar/preview/L4/resource/LAS/oscar_third_deg_180/oscar_vel${YYYY}_180.nc.gz";
  // private String yearlyOscarFilePattern =
  // "https://podaac-opendap.jpl.nasa.gov/opendap/hyrax/OceanCirculation/oscar/preview/L4/oscar_third_deg/oscar_vel1992.nc.gz";

  private String yearlyOscarRemoteFilePattern = "ftp://podaac-ftp.jpl.nasa.gov/allData/oscar/preview/L4/resource/LAS/oscar_third_deg_180/oscar_vel${YYYY}_180.nc";

  private String yearlyOscarLocalFilePattern = "oscar_vel${YYYY}_180.nc";

  // private static String yearlyOscarFilePattern =
  // "http://mdst-macroes.ird.fr:8080/thredds/catalog/macroes/world/current/global/180/catalog.html?dataset=testDatasetScan/world/current/global/180/oscar_vel${YYYY}_180.nc";
  // private static String yearlyOscarFilePattern =
  // "ftp://podaac-ftp.jpl.nasa.gov/allData/oscar/preview/L4/resource/LAS/oscar_third_deg_180/oscar_vel2016_180.nc";

  // URL pattern to be checked for availability of yearly files
  private static String yearlyOscarHTMLPagePattern = "https://thredds.jpl.nasa.gov/thredds/dodsC/OSCAR_L4_OC_third-deg/oscar_vel${YYYY}.nc.gz.html";

  // local working directory directory
  private static String workdir = "/tmp/oscar-merger";

  // the first year to consider in the merge
  private Integer startYear;

  // the last year to consider in the merge
  private Integer endYear;

  private Boolean test = true;

  private static final Logger logger = LoggerFactory
      .getLogger(IncrementalOscarMerger.class);

  public OscarMerger() {
    // do a preliminary cleanup
//    this.cleanup();
  }

  /**
   * Guess the first available year of the dataset, starting from 1990
   * 
   * @return
   */
  private Integer getStartYear() {
    if (this.startYear == null) {
      // start from 1990 and look for the first available year
      for (int year = 1990; year < Calendar.getInstance().get(Calendar.YEAR); year++) {
        if (checkRemoteOscarFile(year)) {
          this.startYear = year;
          break;
        }
      }
    }
    return this.startYear;
  }

  /**
   * Guess the last available year of the dataset, starting from current year
   * and going backward.
   * 
   * @return
   */
  private Integer getEndYear() {
    if (test) {
      this.startYear = 1992;
      this.endYear = 1993;
    }
    if (this.endYear == null) {
      // start at current year and go back
      for (int year = Calendar.getInstance().get(Calendar.YEAR); year > 1990; year--) {
        if (checkRemoteOscarFile(year)) {
          this.endYear = year;
          break;
        }
      }
    }
    return this.endYear;
  }

  /**
   * Generate the descriptor file, needed by the merger, in the workspace
   * directory.
   */
  public void generateDescriptorFile() {
    this.generateDescriptorFile(new File(workdir), this.getDescriptorFileName());
  }

  /**
   * Generate the descriptor file needed by the merger with the given name, in
   * the given directory.
   * 
   * @param destinationDir
   *          the destination directory
   * @param fileName
   *          the filename of the descriptor
   */
  private void generateDescriptorFile(File destinationDir, String fileName) {
    String out = "";
    out += "<netcdf xmlns=\"http://www.unidata.ucar.edu/namespaces/netcdf/ncml-2.2\">\n";
    out += "  <attribute name=\"title\" value=\"OSCAR Velocity Dataset\"/>\n";
    out += "  <aggregation type=\"joinExisting\" dimName=\"time\">\n";
    // iterate years here
    final int startYear = this.getStartYear();
    final int endYear = this.getEndYear();
    for (int year = startYear; year <= endYear; year++) {
      // check remote file exists
      // boolean remoteFileExists = checkRemoteOscarFile(year);
      boolean localFileExists = true;
      String url = yearlyOscarLocalFilePattern.replaceAll("\\$\\{YYYY\\}", year
          + "");
      File f = new File(destinationDir, url);
      if (localFileExists) {
        out += "    <netcdf location=\"" + f.getAbsolutePath() + "\"/>\n";
      } else {
        logger.info("can't find remote file " + url);
      }
    }
    out += "  </aggregation>\n";
    out += "</netcdf>";
    try (PrintWriter pw = new PrintWriter(this.getDescriptorFile())) {
      pw.println(out);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  /**
   * Return the File object associated with the descriptor
   * 
   * @return
   */
  private File getDescriptorFile() {
    return new File(this.getWorkDir(), this.getDescriptorFileName());
  }

  /**
   * Compute the name of the descriptor file, by including start and end years.
   * 
   * @return
   */
  private String getDescriptorFileName() {
    if (this.startYear == this.endYear) {
      return "oscar-vel-" + getStartYear() + ".xml";
    } else {
      return "oscar-vel-" + getStartYear() + "-" + getEndYear() + ".xml";
    }
  }

  /**
   * Connects remotely to check if the remote file, for the given year, exist.
   * 
   * @param year
   * @return
   */
  private boolean checkRemoteOscarFile(int year) {
    String url = yearlyOscarHTMLPagePattern.replaceAll("\\$\\{YYYY\\}", year + "");
    try {
      HttpURLConnection.setFollowRedirects(false);
      HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
      con.setRequestMethod("HEAD");
      return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
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
   * Compute the name of the merged dataset file, including start and end years.
   * 
   * @return
   */
  private String getMergedFileName() {
    if (this.startYear == this.endYear) {
      return "oscar-vel-" + getStartYear() + ".nc";
    } else {
      return "oscar-vel-" + getStartYear() + "-" + getEndYear() + ".nc";
    }
  }

  /**
   * Return the File associated to the merged datatset
   * @return
   */
  private File getMergedFile() {
    return new File(this.getWorkDir(), this.getMergedFileName());
  }

  /**
   * Perform the whole merge by: 1) checking preconditions (e.g. free disk
   * space); 2) downloading dataset files; 3) generating the descriptor and 4)
   * doing the merge itself.
   * 
   * @throws Exception
   */
  public void merge() throws Exception {
    this.checkReady();
    this.downloadRemoteFiles();
    this.generateDescriptorFile();
    this.createOscarMergedFile();
  }

  /**
   * Download dataset files to the working directory.
   */
  private void downloadRemoteFiles() {
    this.downloadRemoteFiles(this.getWorkDir());
  }

  /**
   * Download dataset files to the given directory.
   * @param destinationDir
   */
  private void downloadRemoteFiles(File destinationDir) {
    System.out.println("downloading remote files");
    final int startYear = this.getStartYear();
    final int endYear = this.getEndYear();
    for (int year = startYear; year <= endYear; year++) {
      try {
//        URL url = new URL(yearlyOscarRemoteFilePattern.replaceAll(
//            "\\$\\{YYYY\\}", year + ""));
//        System.out.println("downloading from " + url.toString());
//        URLConnection conn = url.openConnection();
//        InputStream inputStream = conn.getInputStream();
        File localFile = new File(this.getWorkDir(),
            yearlyOscarLocalFilePattern.replaceAll("\\$\\{YYYY\\}", year + ""));
        System.out.println("writing to " + localFile.toString());
        this.downloadFile(yearlyOscarRemoteFilePattern.replaceAll(
            "\\$\\{YYYY\\}", year + ""), localFile);
        /*
        FileOutputStream outputStream = new FileOutputStream(localFile);
        byte[] buffer = new byte[4096];
        int bytesRead = -1;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
          System.out.println(bytesRead);
          outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.close();
        inputStream.close();
        */
        System.out.println("File downloaded");
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
  }
  
  /**
   * Download the file at the given URL to the given local File
   * @param source
   * @param destination
   * @throws IOException
   */
  private void downloadFile(String source, File destination) throws IOException, Exception {
    if(source.startsWith("ftp:")) {
      // extracting host and file
      String tmp = source.substring(6);
      String host = tmp.split("/")[0];
      String file = tmp.split("/", 2)[1];

      // extracting remote file
//      System.out.println(host);
//      System.out.println(file);
      
      FTPDownloader ftpDownloader = new FTPDownloader(host, "anonymous", null);
      ftpDownloader.downloadFile(file, destination.getAbsolutePath());
      System.out.println("FTP File downloaded successfully");
      ftpDownloader.disconnect();
    }
    else {
      throw new Exception("only ftp downloads currently supported");
    }
  }  
  
  /**
   * Perform the merge of the local files using the generated descriptor
   * @throws Exception
   */
  private void createOscarMergedFile() throws Exception {

    String datasetIn = this.getDescriptorFile().getAbsolutePath(); // "data/oscar-descriptor.xml";
    String datasetOut = this.getMergedFile().getAbsolutePath();

    CancelTaskImpl cancel = new CancelTaskImpl();
    NetcdfFile ncfileIn = ucar.nc2.dataset.NetcdfDataset.openFile(datasetIn,
        cancel);

    logger.info(String.format("NetcdfDatataset read from %s write to %s ",
        datasetIn, datasetOut));

    NetcdfFileWriter.Version version = NetcdfFileWriter.Version.netcdf4;
    FileWriter2 writer = new ucar.nc2.FileWriter2(ncfileIn, datasetOut,
        version, null);
    writer.getNetcdfFileWriter().setLargeFile(true);
    NetcdfFile ncfileOut = writer.write(cancel);

    for (Attribute a : ncfileOut.getGlobalAttributes()) {
      System.out.println(a);
    }

    if (ncfileOut != null) {
      ncfileOut.close();
    }
    ncfileIn.close();
    cancel.setDone(true);

    logger.info(String.format("%s%n", cancel));
  }

  /**
   * Clean the workspace.
   */
  public void cleanup() {
    // to avoid erasing my hard disk!
    if (!this.getWorkDir().getAbsolutePath().startsWith("/tmp")) {
      logger.info("NOT REMOVING WORKING DIR AS IT'S NOT IN /TMP");
    } else {
      // removing working directory
      logger.info("Removing working directory "
          + this.getWorkDir().getAbsolutePath() + "...");
      try {
        FileUtils.deleteDirectory(this.getWorkDir());
        logger.info("Removed.");
      } catch (IOException e) {
        e.printStackTrace();
      }
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
    
    // this is a guess for the merged file (~370MB/year)
    Long neededSpace = 370l * 1024 * 1024 * (this.getEndYear() - this.getStartYear() + 1);
    
    // adding the space to download the dataset (~1.2GB/year)
    neededSpace += 1169928 * 1024 * (this.getEndYear()-this.getStartYear()+1);
    
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

}
