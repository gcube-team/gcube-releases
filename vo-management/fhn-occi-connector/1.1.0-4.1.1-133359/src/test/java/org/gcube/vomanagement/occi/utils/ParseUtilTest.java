package org.gcube.vomanagement.occi.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ParseUtilTest {

  @Test
  public void testExtractHostAndPort() {
    String uri = "http://host1.domain.com:8443/path/to/element/";
    assertEquals(ParseUtil.extractHostAndPort(uri),
        "http://host1.domain.com:8443");
  }

  @Test
  public void testExtractHostAndPortAreNull() {
    String uri = "unknown://host1.domain.com:8443/path/to/element/";
    assertTrue(ParseUtil.extractHostAndPort(uri) == null);
  }

  @Test
  public void testExtractPath() {
    String uri = "http://host1.domain.com:8443/path/to/element/";
    assertEquals(ParseUtil.extractPath(uri), "path/to/element/");
  }

  @Test
  public void testExtractPathAreNull() {
    String uri = "unknown://host1.domain.com:8443/path/to/element/";
    assertTrue(ParseUtil.extractPath(uri) == null);
  }

}
