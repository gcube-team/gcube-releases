package org.gcube.vomanagement.occi.datamodel.cloud;

import java.io.Serializable;

public class OSTemplate implements Serializable {

  // an id for this OSTemplate
  private String id;

  // the name of the operating system
  private String os;

  // the version of the operating system
  private String osVersion;

  // the name of the image
  private String name;

  // a description for this image
  private String description;

  // the version of the image
  private String version;

  // the size of the disk
  private Long diskSize;

  public OSTemplate() {

  }

  public String getOsVersion() {
    return osVersion;
  }

  public void setOsVersion(String osVersion) {
    this.osVersion = osVersion;
  }

  public String getOs() {
    return os;
  }

  public void setOs(String os) {
    this.os = os;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Long getDiskSize() {
    return diskSize;
  }

  public void setDiskSize(Long diskSize) {
    this.diskSize = diskSize;
  }


}
