package org.gcube.resources.federation.fhnmanager.api.type;

import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ResourceTemplate extends FHNResource {

  private String name;
  private int cores;
  private Long memory;
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

  public Long getMemory() {
    return memory;
  }

  public void setMemory(Long memory) {
    this.memory = memory;
  }

	public ResourceReference<VMProvider> getVmProvider() {
		return vmProvider;
	}
	
	public void setVmProvider(ResourceReference<VMProvider> vmProvider) {
		this.vmProvider = vmProvider;
	}
}
