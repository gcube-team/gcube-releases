package org.gcube.dataanalysis.dataminerpoolmanager;

import java.io.IOException;
import java.io.InputStream;
import java.net.ProxySelector;
import java.net.URL;
import java.util.List;
import java.util.Vector;

import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Algorithm;
import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.AlgorithmSet;
import org.gcube.dataanalysis.dataminer.poolmanager.process.AddAlgorithmCommand;
import org.gcube.dataanalysis.dataminer.poolmanager.process.AlgorithmPackageParser;
import org.gcube.dataanalysis.dataminer.poolmanager.util.PropertiesBasedProxySelector;

public class AlgorithmPackageParserTest {

  private static int BUFFER_SIZE = 2048;

  public void extractAllAlgorithms() throws IOException {
    String url = "http://svn.research-infrastructures.eu/public/d4science/gcube/trunk/data-analysis/DataMinerConfiguration/algorithms/dev/algorithms";
    List<String> commands = this.extractAddAlgorithmCommands(url);
    AlgorithmSet algorithms = new AlgorithmSet();
    for (String cmd : commands) {
      System.out.println("-----------------------------------------");
      System.out.println(cmd);
      AddAlgorithmCommand aac = new AddAlgorithmCommand(cmd);
      System.out.println(aac);

      // start creating the algo from the command
      Algorithm algo = new Algorithm();
      algo.setAlgorithmType(aac.getAlgorithmType());
      algo.setCategory(aac.getCategory());
      algo.setClazz(aac.getClazz());
      algo.setDescription(aac.getDescription());
      algo.setName(aac.getName());
      algo.setPackageURL(aac.getUrl());
      algo.setSkipJava(aac.getSkipjava());

      // then override with info from the package
      if (aac.getUrl().length() > 4) {
        Algorithm packagedAlgo = this.extractAlgorithm(aac.getUrl());
        if (packagedAlgo != null) {
          algo.setDependencies(packagedAlgo.getDependencies());
        }
      }
      algorithms.addAlgorithm(algo);
      break;
    }
   //to uncomment
   // new DataminerPoolManager().addAlgorithmsToVRE(algorithms,
     //   "/gcube/devNext/NextNext");
  }

  /**
   * Extract 'addAlgorithm' commands from a file containing wiki-table-style
   * entries for algorithm.
   * 
   * @return
   * @throws IOException
   */
  private List<String> extractAddAlgorithmCommands(String listUrl)
      throws IOException {
    URL url = new URL(listUrl);
    InputStream is = url.openStream();

    StringBuilder s = new StringBuilder();
    byte[] buffer = new byte[BUFFER_SIZE];
    int read = 0;
    while ((read = is.read(buffer)) != -1) {
      s.append(new String(buffer, 0, read));
    }
    List<String> out = new Vector<>();
    String[] lines = s.toString().split("\n");
    for (String line : lines) {
      System.out.println("--------------------");
      if (!line.isEmpty()) {
        String[] parts = line.split("\\|");
        int c = 1;
        for (String part : parts) {
          if (part == null || part.trim().isEmpty()) {
            continue;
          }
          System.out.println(c + ". " + part);
          c++;
          if (part.contains("addAlgorithm.sh")) {
            String cmd = part.trim();
            cmd = cmd.replaceAll("<notextile>", "");
            cmd = cmd.replaceAll("</notextile>", "");
            System.out.println(cmd);
            // AddAlgorithmCommand aac = new AddAlgorithmCommand(cmd);
            // System.out.println(aac);
            out.add(cmd);
          }
        }
      }
    }
    return out;
  }

  /**
   * Create an Algorithm starting from the algorithm jar.
   * 
   * @param url
   * @return
   * @throws IOException
   */
  private Algorithm extractAlgorithm(String url) throws IOException {
    return new AlgorithmPackageParser().parsePackage(url);
  }

  public static void main(String[] args) throws Exception {
//    ProxySelector.setDefault(new PropertiesBasedProxySelector(
//        "/home/ngalante/.proxy-settings"));

    new AlgorithmPackageParserTest().extractAllAlgorithms();
//    AlgorithmPackageParserTest at = new AlgorithmPackageParserTest();
//    Algorithm a = at.extractAlgorithm("http://data.d4science.org/YjJ3TmJab1dqYzVoTmppdjlsK0l0b1ZXWGtzWlQ1NHNHbWJQNStIS0N6Yz0");
//    System.out.println(a.getFullname());
  }

}
