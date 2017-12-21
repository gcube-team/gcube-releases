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
    ost.setDescription("some text");
    TemplatesCache.getInstance().cache(providerId, ost);
    OSTemplate ost2 = TemplatesCache.getInstance().getOSTemplate(providerId, ost.getId());
    assertEquals(ost2.getId(), ost.getId());
    assertEquals(ost2.getDescription(), ost.getDescription());
  }

  @Test
  public void testGetResourceTemplate() {
    ResourceTemplate rt = new ResourceTemplate();
    String id = UUID.randomUUID().toString();
    String providerId = "someprovider";
    rt.setId(id);
    rt.setCores(10);
    TemplatesCache.getInstance().cache(providerId, rt);
    ResourceTemplate rt2 = TemplatesCache.getInstance().getResourceTemplate(providerId, id);

    assertEquals(rt.getId(), rt2.getId());
    assertEquals(rt.getCores(), rt2.getCores());

  }

}
