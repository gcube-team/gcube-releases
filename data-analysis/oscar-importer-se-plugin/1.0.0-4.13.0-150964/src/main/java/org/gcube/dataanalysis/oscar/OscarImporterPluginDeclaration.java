package org.gcube.dataanalysis.oscar;

import java.util.HashMap;
import java.util.Map;

import org.gcube.vremanagement.executor.plugin.Plugin;
import org.gcube.vremanagement.executor.plugin.PluginDeclaration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The OscarImporterPlugin declaration class.
 * 
 * @author Paolo Fabriani
 */

public class OscarImporterPluginDeclaration implements PluginDeclaration {

  /**
   * Logger
   */
  private static Logger logger = LoggerFactory
      .getLogger(OscarImporterPluginDeclaration.class);

  private static final String NAME = "oscar-importer-se-plugin";
  private static final String DESCRIPTION = "The oscar-importer-se-plugin periodically builds a merged version of the OSCAR dataset and publishes it in the thredds server.";
  private static final String VERSION = "1.0.0";

  /** {@inheritDoc} */
  @Override
  public void init() {
    logger.debug(String.format("%s initialized",
        OscarImporterPluginDeclaration.class.getSimpleName()));
  }

  /** {@inheritDoc} */
  @Override
  public String getName() {
    return NAME;
  }

  /** {@inheritDoc} */
  @Override
  public String getDescription() {
    return DESCRIPTION;
  }

  /** {@inheritDoc} */
  @Override
  public String getVersion() {
    return VERSION;
  }

  /** {@inheritDoc} */
  @Override
  public Map<String, String> getSupportedCapabilities() {
    Map<String, String> discoveredCapabilities = new HashMap<String, String>();
    return discoveredCapabilities;
  }

  /** {@inheritDoc} */
  @Override
  public Class<? extends Plugin<? extends PluginDeclaration>> getPluginImplementation() {
    return OscarImporterPlugin.class;
  }

  @Override
  public String toString() {
    return String.format("%s : %s - %s - %s - %s - %s", this.getClass()
        .getSimpleName(), getName(), getVersion(), getDescription(),
        getSupportedCapabilities(), getPluginImplementation().getClass()
            .getSimpleName());
  }

}
