package org.gcube.dataanalysis.oscar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
//import org.gcube.data.transfer.library.DataTransferClient;
//import org.gcube.data.transfer.model.Destination;
//import org.gcube.data.transfer.model.DestinationClashPolicy;
import org.gcube.dataanalysis.oscar.util.ISClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class in is charge of uploading the generated merged oscar file to the
 * thredds server
 */
public class ThreddsUploader {

  // the remote persistency area to use
  private static final String DT_PERSISTENCEID = "thredds";

  // the relative subfoloder to upload data
  private static final String DT_SUBFOLDER = "public/netcdf/Oscar";
  
  // how to upload to thredds
  private boolean uploadWithCurl = false;
  
  private static final Logger logger = LoggerFactory.getLogger(ThreddsUploader.class);

  public boolean publishOnThredds(String fileAbsolutePath) throws Exception {
    
    // 1. locate the Thredds service in the current scope
    List<URL> threddsAddress = new ISClient().getThreddsServicesIDs();
    if (threddsAddress.size() == 0)
      throw new Exception("Thredds resources is not available in current scope");
    logger.info("Found " + threddsAddress.size() + " thredds services.");
    for(URL u:threddsAddress) {
      logger.info(u.toString());
    }
    URL thredds = threddsAddress.get(0);
    String threddsEndpoint = String.format("%s://%s:%s", thredds.getProtocol(), thredds.getHost(), thredds.getPort());
    logger.info("Picking the first one: " + threddsEndpoint);
    
    // 2 create a client for the DT on thredds
    // does not work locally since the client.localFile tries to open a connection to mongo-d-vol.d4science.org:27017
    // furthermore, the port 27017 seems to be closed outside cnr
    if(this.uploadWithCurl) {
      this.uploadUsingCurl(threddsEndpoint, fileAbsolutePath);
    } else {
      logger.info("Uploading with DT library currently disabled due to a mismatch in the version of jackson");
//      this.uploadUsingLibrary(threddsEndpoint, fileAbsolutePath);
    }

    logger.info("Finished");
    return true;
  }
  
  private void uploadUsingCurl(String dataTransferEndpoint, String fileAbsolutePath) throws Exception {

//    dataTransferEndpoint = "http://localhost:9000";
    
    String command = "curl";
    command+=" -F uploadedFile=@"+fileAbsolutePath;
    command+=" --header gcube-token:"+SecurityTokenProvider.instance.get();
    command+=" " + dataTransferEndpoint+"/data-transfer-service/gcube/service/REST/FileUpload/"+DT_PERSISTENCEID+"/"+DT_SUBFOLDER;
    command+="?on-existing-file=REWRITE";
    command+="&on-existing-dir=APPEND";
    command+="&create-dirs=true";

    logger.info(command);
    
    try {
      // using the Runtime exec method:
      Process p = Runtime.getRuntime().exec(command);

      BufferedReader stdInput = new BufferedReader(new InputStreamReader(
          p.getInputStream()));

      BufferedReader stdError = new BufferedReader(new InputStreamReader(
          p.getErrorStream()));

      // read the output from the command
      logger.info("Here is the standard output of the command:\n");
      String s;
      while ((s = stdInput.readLine()) != null) {
        logger.info(s);
      }

      // read any errors from the attempted command
      logger.info("Here is the standard error of the command (if any):\n");
      while ((s = stdError.readLine()) != null) {
        logger.info(s);
      }

      System.exit(0);
    } catch (IOException e) {
      logger.info("exception happened - here's what I know: ");
      e.printStackTrace();
      System.exit(-1);
    }    
  }
  
//  private void uploadUsingLibrary(String threddsEndpoint, String fileAbsolutePath) throws Exception {
//
//    // build a client
//    DataTransferClient client = DataTransferClient.getInstanceByEndpoint(threddsEndpoint);
//    
//    // prepare a destination
//    Destination dest=new Destination(fileAbsolutePath);
//    dest.setCreateSubfolders(true);
//    dest.setOnExistingFileName(DestinationClashPolicy.REWRITE);
//    dest.setOnExistingSubFolder(DestinationClashPolicy.APPEND);
//    dest.setPersistenceId(DT_PERSISTENCEID);
//    dest.setSubFolder(DT_SUBFOLDER);
////    dest.setDestinationFileName(fileAbsolutePath.substring(fileAbsolutePath.lastIndexOf("/")+1));
//    
//    // do transfer the file
//    client.localFile(fileAbsolutePath, dest);
//
//  }

  public void setUploadWithCurl(boolean uploadWithCurl) {
    this.uploadWithCurl = uploadWithCurl;
  }
    
}


/*
 *     /*
    // 2. locate the Data Transfer Services in the current scope
    // THIS IS NO LONGER NEEDED
    List<URL> dataTransferAddress = new ISClient().getDataTransferServices();
    if (dataTransferAddress.size() == 0)
      throw new Exception("Data Transfer services are not available in scope " + scope);

    // 3. pick the Data Transfer service located on the Thredds host
    // THIS IS NO LONGER NEEDED
    URL dataTransfer = null;
    for(URL address:dataTransferAddress) {
      if(address.getHost().equals(thredds.getHost())) {
        dataTransfer = address;
        break;
      }
    }
    if (dataTransfer==null) {
      throw new Exception("Thredds data transfer has not been found in the same scope of the catalog: " + scope);
    }

    // 4. do transfer
    // THIS IS NO LONGER NEEDED
    logger.info("TODO: Transferring files using: " + dataTransfer);
//    this.transferFileToService(scope, dataTransfer.getHost(), 9090, fileAbsolutePath, remoteFolder);

    logger.info("TODO: Adding metadata on GeoNetwork");
    */
