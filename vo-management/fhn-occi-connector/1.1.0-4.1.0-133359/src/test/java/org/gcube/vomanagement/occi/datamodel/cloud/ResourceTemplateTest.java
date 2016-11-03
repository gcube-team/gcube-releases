package org.gcube.vomanagement.occi.datamodel.cloud;

import static org.junit.Assert.assertEquals;

import org.gcube.resources.federation.fhnmanager.api.type.ResourceTemplate;
import org.junit.Test;

import java.util.UUID;

public class ResourceTemplateTest {

  @Test
  public void testGetMemory() {
    ResourceTemplate template = new ResourceTemplate();
    Long memory = 239482347L;
    template.setMemory(memory);
    assertEquals("message", template.getMemory(), memory);
  }

  @Test
  public void testGetCores() {
    ResourceTemplate template = new ResourceTemplate();
    int cores = 16;
    template.setCores(cores);
    assertEquals("message", template.getCores(), cores);
  }

  @Test
  public void testGetName() {
    ResourceTemplate template = new ResourceTemplate();
    String name = "Small Instance";
    template.setName(name);
    assertEquals("message", template.getName(), name);
  }

  @Test
  public void testGetId() {
    ResourceTemplate template = new ResourceTemplate();
    String id = UUID.randomUUID().toString();
    template.setId(id);
    assertEquals("message", template.getId(), id);
  }

}
