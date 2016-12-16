package org.gcube.vomanagement.occi.datamodel.cloud;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.UUID;

public class VMStorageTest {

  private String status;
  private String endpoint;

  @Test
  public void testGetId() {
    VMStorage link = new VMStorage();
    String id = UUID.randomUUID().toString();
    link.setId(id);
    assertEquals("message", link.getId(), id);
  }

  @Test
  public void testGetName() {
    VMStorage link = new VMStorage();
    String name = "some name";
    link.setName(name);
    assertEquals("message", link.getName(), name);
  }

  @Test
  public void testGetDeviceId() {
    VMStorage link = new VMStorage();
    String deviceId = "someDeviceId";
    link.setDeviceId(deviceId);
    assertEquals("message", link.getDeviceId(), deviceId);
  }

  @Test
  public void testGetStatus() {
    VMStorage link = new VMStorage();
    String status = "somestatus";
    link.setStatus(status);
    assertEquals("message", link.getStatus(), status);
  }

  @Test
  public void testGetEndpoint() {
    VMStorage link = new VMStorage();
    String endpoint = "someendpoint";
    link.setEndpoint(endpoint);
    assertEquals("message", link.getEndpoint(), endpoint);
  }

}
