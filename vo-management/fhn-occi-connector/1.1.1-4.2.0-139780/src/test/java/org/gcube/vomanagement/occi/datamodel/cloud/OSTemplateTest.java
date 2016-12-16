package org.gcube.vomanagement.occi.datamodel.cloud;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.UUID;

public class OSTemplateTest {

  @Test
  public void testGetDiskSize() {
    OSTemplate template = new OSTemplate();
    Long diskSize = 23497L;
    template.setDiskSize(diskSize);
    assertEquals("message", template.getDiskSize(), diskSize);
  }

  @Test
  public void testGetDescription() {
    OSTemplate template = new OSTemplate();
    String description = "some descriptoin";
    template.setDescription(description);
    assertEquals("message", template.getDescription(), description);
  }

  @Test
  public void testGetName() {
    OSTemplate template = new OSTemplate();
    String name = "gCubeSmartExecutorTemplate";
    template.setName(name);
    assertEquals("message", template.getName(), name);
  }

  @Test
  public void testGetOSVersion() {
    OSTemplate template = new OSTemplate();
    String version = "7.9";
    template.setOsVersion(version);
    assertEquals("message", template.getOsVersion(), version);
  }

  @Test
  public void testGetVersion() {
    OSTemplate template = new OSTemplate();
    String version = "1.0.0";
    template.setVersion(version);
    assertEquals("message", template.getVersion(), version);
  }

  @Test
  public void testGetOS() {
    OSTemplate template = new OSTemplate();
    String os = "Debian";
    template.setOs(os);
    assertEquals("message", template.getOs(), os);
  }

  @Test
  public void testGetId() {
    OSTemplate template = new OSTemplate();
    String id = UUID.randomUUID().toString();
    template.setId(id);
    assertEquals("message", template.getId(), id);
  }

}
