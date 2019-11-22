package org.gcube.dataanalysis.oscar;

import java.util.Map;

import org.gcube.vremanagement.executor.exception.InputsNullException;
import org.gcube.vremanagement.executor.plugin.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OscarImporterPlugin extends Plugin<OscarImporterPluginDeclaration> {

  private IncrementalOscarMerger merger;

  private static final Logger logger = LoggerFactory.getLogger(OscarImporterPlugin.class);
  
  public OscarImporterPlugin(OscarImporterPluginDeclaration pluginDeclaration) {
    super(pluginDeclaration);
    logger.info("Creating a merger...");
    this.merger = new IncrementalOscarMerger();
  }

  @Override
  public void launch(Map<String, Object> params) throws Exception {

    logger.info("1. parsing input parameters");

    Integer intervalSize = this.getParameter(params, "intervalSize", 3);
    logger.info("Setting interval size to " + intervalSize);
    this.merger.setIntervalSize(intervalSize);

    Boolean removeTmpFiles = this.getParameter(params, "removeTmpFiles", Boolean.TRUE);
    logger.info("Removing temporary files? " + removeTmpFiles);
    this.merger.setRemoveTmpFiles(removeTmpFiles);

    Boolean debug = this.getParameter(params, "debug", Boolean.FALSE);
    logger.info("Debug mode? " + debug);
    this.merger.setDebug(debug);

    Boolean uploadWithCurl = this.getParameter(params, "uploadWithCurl", Boolean.FALSE);
    logger.info("Upload merged file with cURL (instead of DataTransfer library)? " + uploadWithCurl);

    logger.info("2. create the merged file");
    String mergedFile = this.merger.merge();
    
    logger.info("3. upload to thredds");
    ThreddsUploader tu = new ThreddsUploader();
    tu.setUploadWithCurl(uploadWithCurl);
    tu.publishOnThredds(mergedFile);
    
    logger.info("4. cleanup");
    this.merger.cleanup();
  }

  @Override
  protected void onStop() throws Exception {
    logger.info("4. onStop");
//    this.merger.cleanup();
  }

  private Integer getParameter(Map<String, Object> params, String key, Integer defaultValue) {
    if(params!=null) {
      Object o = params.get(key);
      if(o instanceof Integer) {
        return (Integer)o;
      }
    }
    return defaultValue;
  }

  private Boolean getParameter(Map<String, Object> params, String key, Boolean defaultValue) {
    if(params!=null) {
      Object o = params.get(key);
      if(o instanceof Boolean) {
        return (Boolean)o;
      }
    }
    return defaultValue;
  }

  private String getParameter(Map<String, Object> params, String key, String defaultValue) {
    if(params!=null) {
      Object o = params.get(key);
      if(o instanceof String) {
        return (String)o;
      }
    }
    return defaultValue;
  }
  
}
