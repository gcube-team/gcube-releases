package org.gcube.dataanalysis.dataminer.poolmanager.process;

import java.util.StringTokenizer;

public class AddAlgorithmCommand {

  private String command;
  private String name;
  private String category;
  private String clazz;
  private String scope;
  private String algorithmType;
  private String skipJava;
  private String url;
  private String description;

  public AddAlgorithmCommand(String cmd) {
    StringTokenizer st = new StringTokenizer(cmd, " ");
    if (st.hasMoreElements())
      command = st.nextToken();
    if (st.hasMoreElements())
      name = st.nextToken();
    if (st.hasMoreElements())
      category = st.nextToken();
    if (st.hasMoreElements())
      clazz = st.nextToken();
    if (st.hasMoreElements())
      scope = st.nextToken();
    if (st.hasMoreElements())
      algorithmType = st.nextToken();
    if (st.hasMoreElements())
      skipJava = st.nextToken();
    if (st.hasMoreElements())
      url = st.nextToken();
    
    String d = "";
    while (st.hasMoreElements())
      d = d + st.nextToken() + " ";
    this.setDescription(d);
    
  }

  public void setDescription(String d) {
    if(d!=null) {
      d = d.trim();
      if(d.startsWith("\"") && d.endsWith("\"")) {
        d = d.substring(1, d.length()-1).trim();
      }
    }
    this.description = d;
  }
  
  public String getCommand() {
    return command;
  }

  public String getName() {
    return name;
  }

  public String getCategory() {
    return category;
  }

  public String getClazz() {
    return clazz;
  }

  public String getVRE() {
    return scope;
  }

  public String getAlgorithmType() {
    return algorithmType;
  }

  public String getSkipjava() {
    return skipJava;
  }

  public String getUrl() {
    return url;
  }

  public String getDescription() {
    return description;
  }

  public String toString() {
    String out = "";
    out += String.format("%-12s: %s\n", "command", command);
    out += String.format("%-12s: %s\n", "algo name", name);
    out += String.format("%-12s: %s\n", "category", category);
    out += String.format("%-12s: %s\n", "class", clazz);
    out += String.format("%-12s: %s\n", "scope", scope);
    out += String.format("%-12s: %s\n", "algo type", algorithmType);
    out += String.format("%-12s: %s\n", "skip java", skipJava);
    out += String.format("%-12s: %s\n", "url", url);
    out += String.format("%-12s: %s\n", "description", this.description);
    return out;
  }

}
