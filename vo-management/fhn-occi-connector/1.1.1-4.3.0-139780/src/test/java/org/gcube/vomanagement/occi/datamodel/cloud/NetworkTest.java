package org.gcube.vomanagement.occi.datamodel.cloud;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.net.URI;
import java.util.UUID;

public class NetworkTest {

  @Test
  public void testGetEndpoint() {
    Network network = new Network();
    URI endpoint = URI.create("someendpoint");
    network.setEndpoint(endpoint);
    assertEquals("message", network.getEndpoint(), endpoint);
  }

  @Test
  public void testGetAddress() {
    Network network = new Network();
    String address = "some address";
    network.setAddress(address);
    assertEquals("message", network.getAddress(), address);
  }

  @Test
  public void testGetGateway() {
    Network network = new Network();
    String gateway = "some gateway";
    network.setGateway(gateway);
    assertEquals("message", network.getGateway(), gateway);
  }

  @Test
  public void testGetAllocation() {
    Network network = new Network();
    String allocation = "some allocation";
    network.setAllocation(allocation);
    assertEquals("message", network.getAllocation(), allocation);
  }

  @Test
  public void testGetStatus() {
    Network network = new Network();
    String status = "some status";
    network.setStatus(status);
    assertEquals("message", network.getStatus(), status);
  }

  @Test
  public void testGetName() {
    Network network = new Network();
    String name = "some name";
    network.setName(name);
    assertEquals("message", network.getName(), name);
  }

  @Test
  public void testGetDescription() {
    Network network = new Network();
    String description = "some description";
    network.setDescription(description);
    assertEquals("message", network.getDescription(), description);
  }

  @Test
  public void testGetId() {
    Network network = new Network();
    String id = UUID.randomUUID().toString();
    network.setId(id);
    assertEquals("message", network.getId(), id);
  }

}
