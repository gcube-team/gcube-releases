package org.gcube.vomanagement.occi.datamodel.cloud;

import java.net.URI;

/**
 * This class models a storage unit usually attached to a virtual machine.
 */
public class Storage {

  /**
   * The id of the storage.
   */
  private String id;

  /**
   * The name of the storage.
   */
  private String name;

  /**
   * A summary description for the storage.
   */
  private String summary;

  /**
   * The size, in bytes, of the storage.
   */
  private Long size;

  /**
   * The status of the storage.
   */
  private String status;

  /**
   * An endpoint for the storage.
   */
  private URI endpoint;

  /**
   * Getter for the id field.
   * 
   * @return the id of the storage
   */
  public String getId() {
    return id;
  }

  /**
   * Setter for the id field.
   * 
   * @param id the storage id
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Getter for the summary field.
   * 
   * @return a summary description of the storage.
   */
  public String getSummary() {
    return summary;
  }

  /**
   * Set a summary description of the storage.
   * 
   * @param summary the summary of the storage.
   */
  public void setSummary(String summary) {
    this.summary = summary;
  }

  /**
   * Return the size of the storage.
   * 
   * @return the size of the storage.
   */
  public Long getSize() {
    return size;
  }

  /**
   * Set the size of the storage.
   * 
   * @param size the size of the storage.
   */
  public void setSize(Long size) {
    this.size = size;
  }

  /**
   * Return the status of the storage.
   * 
   * @return the storage status.
   */
  public String getStatus() {
    return status;
  }

  /**
   * Set the status of the storage.
   * 
   * @param status the storage status.
   */
  public void setStatus(String status) {
    this.status = status;
  }

  /**
   * Return the name of the storage.
   * 
   * @return the name of the storage.
   */
  public String getName() {
    return this.name;
  }

  /**
   * Set the name of the storage.
   * 
   * @param name the name of the storage.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Return the endpoint of the storage.
   * 
   * @return the endpoint of the storage.
   */
  public URI getEndpoint() {
    return this.endpoint;
  }

  /**
   * Set the endpoint of the storage.
   * 
   * @param endpoint the endpoint of the storage.
   */
  public void setEndpoint(URI endpoint) {
    this.endpoint = endpoint;
  }

}
