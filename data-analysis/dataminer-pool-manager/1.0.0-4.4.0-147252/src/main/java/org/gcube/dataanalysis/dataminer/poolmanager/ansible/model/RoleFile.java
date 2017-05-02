package org.gcube.dataanalysis.dataminer.poolmanager.ansible.model;

public class RoleFile {

  /**
   * The path to the file, starting from the role root
   */
  private String path;
  
  /**
   * The name of the task file
   */
  private String name;
  
  /**
   * The content of the task file
   * @return
   */
  private String content;
  
  public RoleFile() {
  }

  public RoleFile(String name, String content) {
    this();
    this.setName(name);
    this.setContent(content);
  }
  
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }
  
}
