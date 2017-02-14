package org.gcube.resources.federation.fhnmanager.api.type;

import java.io.Serializable;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ResourceTemplate extends FHNResource implements Serializable {

  private String name;
  private int cores;
  private Double memory;
  private ResourceReference<VMProvider> vmProvider;


public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getCores() {
    return cores;
  }

  public void setCores(int cores) {
    this.cores = cores;
  }

  public Double getMemory() {
    return memory;
  }

  public void setMemory(double d) {
    this.memory = d;
  }

	public ResourceReference<VMProvider> getVmProvider() {
		return vmProvider;
	}
	
	public void setVmProvider(ResourceReference<VMProvider> vmProvider) {
		this.vmProvider = vmProvider;
	}
}
