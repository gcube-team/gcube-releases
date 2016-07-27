package org.gcube.vomanagement.occi.datamodel.cloud;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.net.URI;

public class VMTest {

  @Test
  public void testGetId() {
    VM vm = new VM();
    String id = "someid";
    vm.setId(id);
    assertEquals(vm.getId(), id);
  }

  @Test
  public void testGetName() {
    VM vm = new VM();
    String name = "someName";
    vm.setName(name);
    assertEquals(vm.getName(), name);
  }

  @Test
  public void testGetEndpoint() {
    VM vm = new VM();
    URI uri = URI.create("someURI");
    vm.setEndpoint(uri);
    assertEquals(vm.getEndpoint(), uri);
  }

  @Test
  public void testGetMemory() {
    VM vm = new VM();
    Long size = 123413574L;
    vm.setMemory(size);
    assertEquals(vm.getMemory(), size);
  }

  @Test
  public void testGetStatus() {
    VM vm = new VM();
    String status = "some status";
    vm.setStatus(status);
    assertEquals(vm.getStatus(), status);
  }

  @Test
  public void testGetProvider() {
    VM vm = new VM();
    String provider = "some provider";
    vm.setProvider(provider);
    assertEquals(vm.getProvider(), provider);
  }

  @Test
  public void testGetHostname() {
    VM vm = new VM();
    String hostname = "host.domain.tld";
    vm.setHostname(hostname);
    assertEquals(vm.getHostname(), hostname);
  }

  @Test
  public void testGetCores() {
    VM vm = new VM();
    int cores = 8;
    vm.setCores(cores);
    assertEquals(vm.getCores(), cores);
  }

  @Test
  public void testGetDiskSizeIsZero() {
    VM vm = new VM();
    Long size = 0L;
    assertEquals(vm.getDiskSize(), size);
  }

  @Test
  public void testGetDiskSize() {
    VM vm = new VM();
    Long size = 17583L;
    Storage storage = new Storage();
    storage.setSize(size);
    VMStorage link = new VMStorage();
    link.setStorage(storage);
    vm.addStorage(link);
    assertEquals(vm.getDiskSize(), size);
  }

  @Test
  public void testGetDiskSizeWithNoStorage() {
    VM vm = new VM();
    Long size = 0L;
    VMStorage link = new VMStorage();
    vm.addStorage(link);
    assertEquals(vm.getDiskSize(), size);
  }

  @Test
  public void testGetDiskSizeWithNullDiskSize() {
    VM vm = new VM();
    Storage storage = new Storage();
    VMStorage link = new VMStorage();
    link.setStorage(storage);
    vm.addStorage(link);
    assertEquals(vm.getDiskSize(), new Long(0));
  }

  @Test
  public void testAddNetwork() {
    Network network = new Network();
    VMNetwork link = new VMNetwork();
    link.setNetwork(network);
    VM vm = new VM();
    vm.addNetwork(link);
    assertEquals(vm.getNetworks().size(), 1);
    assertEquals(vm.getNetworks().contains(link), Boolean.TRUE);
  }

  @Test
  public void testAddStorage() {
    Storage network = new Storage();
    VMStorage link = new VMStorage();
    link.setStorage(network);
    VM vm = new VM();
    vm.addStorage(link);
    assertEquals(vm.getStorage().size(), 1);
    assertEquals(vm.getStorage().contains(link), Boolean.TRUE);
  }

}
