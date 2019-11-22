package org.gcube.vomanagement.occi.datamodel.cloud;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.UUID;

public class VMNetworkTest {

  @Test
  public void testNetwork() {
    VMNetwork link = new VMNetwork();
    Network network = new Network();
    link.setNetwork(network);
    assertEquals("message", link.getNetwork(), network);
  }

  @Test
  public void testGetId() {
    VMNetwork link = new VMNetwork();
    String id = UUID.randomUUID().toString();
    link.setId(id);
    assertEquals("message", link.getId(), id);
  }

  @Test
  public void testGetName() {
    VMNetwork link = new VMNetwork();
    String name = "Small Instance";
    link.setName(name);
    assertEquals("message", link.getName(), name);
  }

  @Test
  public void testGetAddress() {
    VMNetwork link = new VMNetwork();
    String addresss = "127.0.0.1";
    link.setAddress(addresss);
    assertEquals("message", link.getAddress(), addresss);
  }

  @Test
  public void testGetIface() {
    VMNetwork link = new VMNetwork();
    String iface = "eth0";
    link.setInterface(iface);
    assertEquals("message", link.getIface(), iface);
  }

  @Test
  public void testGetMac() {
    VMNetwork link = new VMNetwork();
    String mac = "AA:AA:AA:AA:AA:AA";
    link.setMac(mac);
    assertEquals("message", link.getMac(), mac);
  }

  @Test
  public void testGetStatus() {
    VMNetwork link = new VMNetwork();
    String status = "some status";
    link.setStatus(status);
    assertEquals("message", link.getStatus(), status);
  }

  @Test
  public void testGetEndpoint() {
    VMNetwork link = new VMNetwork();
    String endpoint = "some endpoint";
    link.setEndpoint(endpoint);
    assertEquals("message", link.getEndpoint(), endpoint);
  }


}
