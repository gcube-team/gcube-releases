package org.gcube.vomanagement.occi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.gcube.resources.federation.fhnmanager.api.type.ResourceTemplate;
import org.gcube.vomanagement.occi.datamodel.cloud.OSTemplate;
import org.junit.Test;

import java.util.UUID;

public class TemplatesCacheTest {

  @Test
  public void testInstance() {
    TemplatesCache i1 = TemplatesCache.getInstance();
    TemplatesCache i2 = TemplatesCache.getInstance();
    assertEquals(i1, i2);
  }

  @Test
  public void testGetOSTemplateIsNull() {
    OSTemplate ost = TemplatesCache.getInstance().getOSTemplate("someprovider",
        "someid");
    assertTrue(ost == null);
  }

  @Test
  public void testGetResourcetemplateIsNull() {
    ResourceTemplate rt = TemplatesCache.getInstance().getResourceTemplate(
        "someprovider", "someid");
    assertTrue(rt == null);
  }

  @Test
  public void testGetOSTemplate() {
    OSTemplate ost = new OSTemplate();
    String id = UUID.randomUUID().toString();
    String providerId = "someprovider";
    ost.setId(id);
    TemplatesCache.getInstance().cache(providerId, ost);
    assertEquals(TemplatesCache.getInstance().getOSTemplate(providerId, id),
        ost);
  }

  @Test
  public void testGetResourceTemplate() {
    ResourceTemplate rt = new ResourceTemplate();
    String id = UUID.randomUUID().toString();
    String providerId = "someprovider";
    rt.setId(id);
    TemplatesCache.getInstance().cache(providerId, rt);
    assertEquals(
        TemplatesCache.getInstance().getResourceTemplate(providerId, id), rt);
  }

}
