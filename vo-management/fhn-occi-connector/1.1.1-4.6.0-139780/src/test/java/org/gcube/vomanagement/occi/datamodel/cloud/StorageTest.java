package org.gcube.vomanagement.occi.datamodel.cloud;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.net.URI;

public class StorageTest {

  @Test
  public void multiplicationOfZeroIntegersShouldReturnZero() {

    // MyClass is tested
    Storage storage = new Storage();
    storage.setId("someId");
    // assert statements
    assertEquals("", storage.getId(), "someId");
  }

  @Test
  public void testGetId() {
    Storage storage = new Storage();
    String id = "someid";
    storage.setId(id);
    assertEquals("message", storage.getId(), id);
  }

  @Test
  public void testGetName() {
    Storage storage = new Storage();
    String name = "someName";
    storage.setName(name);
    assertEquals("message", storage.getName(), name);
  }

  @Test
  public void testGetEndpoint() {
    Storage storage = new Storage();
    URI uri = URI.create("someURI");
    storage.setEndpoint(uri);
    assertEquals("message", storage.getEndpoint(), uri);
  }

  @Test
  public void testGetSize() {
    Storage storage = new Storage();
    Long size = 574L;
    storage.setSize(size);
    assertEquals("message", storage.getSize(), size);
  }

  @Test
  public void testGetStatus() {
    Storage storage = new Storage();
    String status = "some status";
    storage.setStatus(status);
    assertEquals("message", storage.getStatus(), status);
  }

  @Test
  public void testGetSummary() {
    Storage storage = new Storage();
    String summary = "some summary";
    storage.setSummary(summary);
    assertEquals("message", storage.getSummary(), summary);
  }
}
